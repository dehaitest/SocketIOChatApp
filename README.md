# SocketIOChatApp

## How to use
Modify `SocketIOChatApp/app/src/main/java/com/example/socketiochatapp/ChatApplication.java`, change the IP address at `mSocket = IO.socket("The server url");` to connect to the server.

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

## Sign up
