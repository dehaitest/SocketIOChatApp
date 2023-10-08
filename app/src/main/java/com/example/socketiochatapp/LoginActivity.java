package com.example.socketiochatapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    public static final String PREFERENCES_FILE = "uta_preferences";
    public static final String ACCESS_TOKEN_KEY = "access_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupRedirectButton = findViewById(R.id.signupRedirectButton);
        String serverIp = Config.SERVER_IP;
        int serverPort = Config.SERVER_PORT;

        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String jsonPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            RequestBody body = RequestBody.create(JSON, jsonPayload);

            Request request = new Request.Builder()
                    .url(serverIp + ":" + serverPort + "/login")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()) {
                        // Extract and save the access token
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String accessToken = jsonResponse.getString("access_token");
                            saveAccessToken(accessToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Navigate to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();  // to prevent coming back to login screen on back press
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        signupRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

    }
    private void saveAccessToken(String accessToken) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.apply();
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
    }
}
