package com.example.socketiochatapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.client.Socket;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    private EditText messageInput;
    private TextView messageDisplay;
    private final Executor executor = Executors.newSingleThreadExecutor(); // Using a single thread executor for simplicity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatApplication app = new ChatApplication();
        mSocket = app.getSocket();
        messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);
        Button generateTaskButton = findViewById(R.id.generateTaskButton);
        Button connectButton = findViewById(R.id.connectButton);
        Button leaveButton = findViewById(R.id.leaveButton);
        Button joinButton = findViewById(R.id.joinButton);
        messageDisplay = findViewById(R.id.messageDisplay);
        messageDisplay.setMovementMethod(new ScrollingMovementMethod());
        String accessToken = getAccessToken();

        Button sendImageButton = findViewById(R.id.sendImageButton);
        ActivityResultLauncher<String> mImagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        executor.execute(() -> sendImageToServer(uri, accessToken));
                    }
                });
        sendImageButton.setOnClickListener(v -> mImagePicker.launch("image/*"));
        mSocket.connect();

        // To listen for messages from the server
        mSocket.on("connect", args -> {
            try {
                JSONObject dataObject = new JSONObject();
                dataObject.put("data", "I'm connected!");
                mSocket.emit("my_event", dataObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mSocket.on("my_response", args -> runOnUiThread(() -> {
            if (args.length > 0 && args[0] instanceof JSONObject) {
                String message = ((JSONObject) args[0]).optString("data");
                messageDisplay.append("\n" + message);
            }
        }));

        mSocket.on("error", args -> Log.e("SocketIO", "error: " + args[0]));
        mSocket.on("connect_error", args -> Log.e("SocketIO", "connection error: " + args[0]));


        // Connect to the server
        connectButton.setOnClickListener(v -> {
            try {
                JSONObject dataObject = new JSONObject();
                dataObject.put("data", "I'm connected!");
                mSocket.emit("my_event", dataObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Send message
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString();
            if (!message.isEmpty()) {
                try {
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("data", message);
                    mSocket.emit("my_event", dataObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Generate task
        generateTaskButton.setOnClickListener(v -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_name", "Cook Chinese dishes")
                        .put("access_token", accessToken);
                mSocket.emit("generateTask", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        // Join room
        joinButton.setOnClickListener(v -> {
            try {
                JSONObject dataObject = new JSONObject();
                dataObject.put("access_token", accessToken);
                mSocket.emit("join", dataObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Leave the room
        leaveButton.setOnClickListener(v -> {
            try {
                JSONObject dataObject = new JSONObject();
                dataObject.put("access_token", accessToken);
                mSocket.emit("leave", dataObject);
//                mSocket.disconnect();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }


    private void sendImageToServer(Uri imageUri, String accessToken) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            assert imageStream != null;
            String vh = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><hierarchy><node index=\"1240\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,2160]\"><node index=\"1241\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,2160]\"><node index=\"1242\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,2160]\"><node index=\"1243\" text=\"\" class=\"android.view.ViewGroup\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,2160]\"><node index=\"1244\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,66][1080,242]\"><node index=\"1245\" text=\"\" class=\"com.google.android.material.card.MaterialCardView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,88][1058,220]\"><node index=\"1246\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,88][1058,220]\"><node index=\"1247\" text=\"\" class=\"android.view.ViewGroup\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,88][1058,220]\"><node index=\"1248\" text=\"\" class=\"android.widget.ImageButton\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,88][170,220]\" /><node index=\"1249\" text=\"Search settings\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[384,124][690,183]\" /></node><node index=\"1250\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"Profile picture, double tap to open Google Account\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[926,88][1058,220]\" /></node></node></node><node index=\"1251\" text=\"\" class=\"android.widget.ScrollView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"true\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,66][1080,2160]\"><node index=\"1252\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"true\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,66][1080,2160]\"><node index=\"1253\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[11,242][1069,429]\"><node index=\"1254\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[11,242][1069,429]\"><node index=\"1255\" text=\"\" class=\"androidx.recyclerview.widget.RecyclerView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[11,242][1069,429]\"><node index=\"1256\" text=\"\" class=\"com.google.android.material.card.MaterialCardView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1257\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1258\" text=\"\" class=\"android.view.ViewGroup\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1259\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1260\" text=\"\" class=\"android.view.View\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\" /><node index=\"1261\" text=\"\" class=\"androidx.recyclerview.widget.RecyclerView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1262\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1263\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1264\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,242][1058,407]\"><node index=\"1265\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[22,258][159,390]\"><node index=\"1266\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[27,258][159,390]\" /></node><node index=\"1267\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[159,242][835,407]\"><node index=\"1268\" text=\"Wi‑Fi\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[197,252][329,329]\" /><node index=\"1269\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[197,329][791,396]\"><node index=\"1270\" text=\"Connected to Origin Broadband 4853 5G\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[197,329][791,396]\" /></node></node><node index=\"1271\" text=\"\" class=\"android.view.View\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[835,280][838,368]\" /><node index=\"1272\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[838,258][1058,390]\"><node index=\"1273\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[882,258][1014,390]\"><node NAF=\"true\" index=\"1274\" text=\"\" class=\"android.widget.Switch\" package=\"com.android.settings\" content-desc=\"\" checkable=\"true\" checked=\"true\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[882,258][1014,390]\" /></node></node></node></node></node></node></node></node></node></node></node></node></node><node index=\"1275\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,429][1080,2160]\"><node index=\"1276\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,429][1080,2160]\"><node index=\"1277\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,429][1080,2160]\"><node index=\"1278\" text=\"\" class=\"androidx.recyclerview.widget.RecyclerView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,429][1080,2160]\"><node index=\"1279\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,429][1080,719]\"><node index=\"1280\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,513][198,634]\"><node index=\"1281\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,524][143,623]\" /></node><node index=\"1282\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,429][1036,719]\"><node index=\"1283\" text=\"Network &amp; internet\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,473][681,550]\" /><node index=\"1284\" text=\"Wi‑Fi, mobile, data usage, and hotspot\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,550][1036,675]\" /></node></node><node index=\"1285\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,719][1080,1009]\"><node index=\"1286\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,803][198,924]\"><node index=\"1287\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,814][143,913]\" /></node><node index=\"1288\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,719][1036,1009]\"><node index=\"1289\" text=\"Connected devices\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,763][696,840]\" /><node index=\"1290\" text=\"Bluetooth, Android Auto, driving mode, NFC\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,840][1036,965]\" /></node></node><node index=\"1291\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1009][1080,1241]\"><node index=\"1292\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1064][198,1185]\"><node index=\"1293\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1075][143,1174]\" /></node><node index=\"1294\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1009][1036,1241]\"><node index=\"1295\" text=\"Apps &amp; notifications\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1053][712,1130]\" /><node index=\"1296\" text=\"Assistant, recent apps, default apps\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1130][990,1197]\" /></node></node><node index=\"1297\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1241][1080,1473]\"><node index=\"1298\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1296][198,1417]\"><node index=\"1299\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1307][143,1406]\" /></node><node index=\"1300\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1241][1036,1473]\"><node index=\"1301\" text=\"Battery\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1285][384,1362]\" /><node index=\"1302\" text=\"100%\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1362][319,1429]\" /></node></node><node index=\"1303\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1473][1080,1763]\"><node index=\"1304\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1557][198,1678]\"><node index=\"1305\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1568][143,1667]\" /></node><node index=\"1306\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1473][1036,1763]\"><node index=\"1307\" text=\"Display\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1517][382,1594]\" /><node index=\"1308\" text=\"Styles, wallpapers, screen timeout, font size\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1594][1036,1719]\" /></node></node><node index=\"1309\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1763][1080,1995]\"><node index=\"1310\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1818][198,1939]\"><node index=\"1311\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,1829][143,1928]\" /></node><node index=\"1312\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1763][1036,1995]\"><node index=\"1313\" text=\"Sound &amp; vibration\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1807][648,1884]\" /><node index=\"1314\" text=\"Volume, haptics, Do Not Disturb\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1884][903,1951]\" /></node></node><node index=\"1315\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1995][1080,2160]\"><node index=\"1316\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,2050][198,2160]\"><node index=\"1317\" text=\"\" class=\"android.widget.ImageView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[44,2061][143,2160]\" /></node><node index=\"1318\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,1995][1036,2160]\"><node index=\"1319\" text=\"Storage\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,2039][398,2116]\" /><node index=\"1320\" text=\"19% used - 104 GB free\" class=\"android.widget.TextView\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[198,2116][711,2160]\" /></node></node></node></node></node></node></node></node></node></node></node><node index=\"1321\" text=\"\" class=\"android.view.View\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,66]\" /><node index=\"1322\" text=\"\" class=\"android.view.View\" package=\"com.android.settings\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,2028][1080,2160]\" /></node></hierarchy>";
            byte[] bytes = getBytes(imageStream);
            String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", encodedImage)
                    .put("access_token", accessToken)
                    .put("VH", vh)
                    .put("task", "I want to change the device brightness")
                    .put("resolution", "(1080, 2160)")
                    .put("package_name", "com.data61.uta")
                    .put("activity_name", "android.widget.FrameLayout")
                    .put("keyboard_active", "false")
                    .put("new_task", "true");
            mSocket.emit("automation", jsonObject);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFERENCES_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(LoginActivity.ACCESS_TOKEN_KEY, null);
    }
}
