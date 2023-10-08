# SocketIOChatApp

## How to use
Add `<uses-permission android:name="android.permission.INTERNET" />` , `<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>` and `android:usesCleartextTraffic="true"` to `AndroidManifest.xml`.
Add `implementation("io.socket:socket.io-client:2.1.0")` to gradle dependencies.

Run the app, Click `Send Image` to test the UI automation and `Generate Task` to test task generation.

## How to modify the code
Please check `MainActivity.java` for the main process.

This code use socketio to communicate with server. Use the code to create connection.
```
ChatApplication app = new ChatApplication();
mSocket = app.getSocket();
mSocket.connect();
```
`mSocket.on("my_response", args -> runOnUiThread(() -> {` is the listener to recieve message from server.

You can refer to the buttons to know how to implement each function.

## Connect to server
Modify `Config.java` and change the IP address, you know that.
Remember to modify all the code snippets that use this IP address.

## Sign up
Refere to `SignupActivity.java` to implement the sign up function.
Nothing special.

## Login
Refere to `LoginActivity.java` to implement the sign up function. 
The user information will be encoded and saved in a safe place (see line 20, 21) for further usage.
Therefore, when you call automation or generation, you have to add the access code to the request body (see `MainActivity.java` line 158, 110)

![Login](https://github.com/dehaitest/SocketIOChatApp/blob/main/images/login.png)


## Note
After you login, you have to call join to join a room before you can start a session. Refer to line 116 in `MainActivity.java`.

![Login](https://github.com/dehaitest/SocketIOChatApp/blob/main/images/join.png)

This function sends user information to server and the server will create a new room for this user to make sure the server is only sending message to this user but not broadcast.

![Login](https://github.com/dehaitest/SocketIOChatApp/blob/main/images/task.png)


