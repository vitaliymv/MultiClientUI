package com.example.multiclientui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User extends Thread {
    Socket socket;
    BufferedReader reader;
    String username;
    PrintWriter out;
    public User(Socket socket, String username) throws IOException {
        this.socket = socket;
        this.username = username;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    @Override
    public void run() {
        String str = null;
        try {
            while (!(str = reader.readLine()).equalsIgnoreCase("exit!")) {
                System.out.println(username + ": " + str);
                Server.messages.add(new Message(username, str));
                Server.update = true;
                if (str.contains(" -> joined the chat")) {
                    String getUsername = Server.messages.get(Server.messages.size() - 1).message.split(" -> ")[0];
                    setUsername(getUsername);
                }
            }
            System.out.println(username + " was disconnected");
            out.println("kick");
            reader.close();
        } catch (IOException e) {
            System.out.println("Connection is lost");
        }
    }
}
