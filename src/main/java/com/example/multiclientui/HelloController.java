package com.example.multiclientui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class HelloController {
    public TextField textFieldHost;
    public TextField textFieldPort;
    public TextField textFieldName;
    public TextField textFieldMessage;
    public ListView<String> listViewMessages;
    public Label errorHost;
    public Label errorPort;
    public Boolean sendMessageFlag = false;
    public void buttonSendClick() {
        sendMessageFlag = true;
    }

    public void buttonConnectClick() throws IOException {
        new Client();
    }

    private static String host;
    private static Integer port;
    private static String name;
    class Client {
        PrintWriter out;
        BufferedReader in;
        Socket socket;
        String fromServer = "";

        Client() throws IOException {
            try {
                port = Integer.parseInt(textFieldPort.getText());
            } catch (NumberFormatException e) {
                errorPort.setText("Incorrect port");
            }

            try {
                socket = new Socket(textFieldHost.getText(), port);
            } catch (UnknownHostException e) {
                errorHost.setText("Incorrect host");
            } catch (IllegalArgumentException e) {
                errorPort.setText("Incorrect port");
            }

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(textFieldName.getText() + " -> joined the chat");
            runSendMessage();
            runReadMessage();
        }

        void sendMessage() throws IOException {
            String outStr;
            while (!(outStr = textFieldMessage.getText()).equalsIgnoreCase("exit!")) {
                if (sendMessageFlag && !textFieldMessage.getText().isEmpty()) {
                    final String messageToSend = outStr;
                    Platform.runLater(() -> {
                        out.println(messageToSend);
                        textFieldMessage.setText("");
                    });
                }
                sendMessageFlag = false;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            out.println(outStr);
            out.close();
            socket.close();
        }

        void runSendMessage() {
            new Thread(() -> {
                try {
                    sendMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        void readMessage() throws IOException {
            while (!(fromServer = in.readLine()).equals("kick")) {
                final String message = fromServer;
                Platform.runLater(() -> listViewMessages.getItems().add(message));
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
            Platform.runLater(() -> listViewMessages.getItems().add("You have benn kicked"));
            in.close();
        }

        void runReadMessage() {
            new Thread(() -> {
                try {
                    readMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}