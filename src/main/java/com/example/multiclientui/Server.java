package com.example.multiclientui;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<User> clients = new ArrayList<>();
    public static List<Message> messages = new ArrayList<>();
    PrintWriter out;
    static Boolean update = false;
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    private void waitForConnection() {
        while (true) {
            try {
                clients.add(new User(serverSocket.accept(), "User" + clients.size()));
                clients.get(clients.size() - 1).start();
            } catch (IOException e) {
                System.out.println("User was disconnected");
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void runWaitForConnectionAsynchronously() {
        new Thread(this::waitForConnection).start();
        new Thread(this::sendMessage).start();
    }

    private void sendMessage() {
        while(true) {
            try {
                if (update) {
                    for (int i = 0; i < clients.size(); i++) {
                        out = new PrintWriter(clients.get(i).socket.getOutputStream(), true);
                        out.println(clients.get(i).username + ": " + messages.get(messages.size() - 1).message);
                    }
                    update = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}