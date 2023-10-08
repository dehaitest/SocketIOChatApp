package com.example.socketiochatapp;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class ChatApplication {
    private Socket mSocket;
    String serverIp = Config.SERVER_IP;
    int serverPort = Config.SERVER_PORT;


    public ChatApplication() {
        try {
            mSocket = IO.socket(serverIp + ":" + serverPort);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}

