package com.itmo.client;

import com.itmo.common.ClassName;
import com.itmo.common.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private final InetSocketAddress address;
    private String username;
    private final Scanner scanner;
    private ClassName connectionHandler;

    public Client(InetSocketAddress address) {
        this.address = address;
        scanner = new Scanner(System.in);
    }

    private void createConnection() throws IOException {
        connectionHandler = new ClassName(
                new Socket(address.getHostName(), address.getPort()));

    }

    private class Writer extends Thread {
        public void run() {
            while (true) {
                System.out.println("Введите текст сообщения");
                String text = scanner.nextLine();
                Message message = new Message(username);
                message.setText(text);
                try {
                    connectionHandler.send(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class Reader extends Thread {
        public void run() {
            while (true) {
                Message message = null;
                try {
                    message = connectionHandler.read();
                    System.out.println(message.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void startClient() /*throws Exception*/ {
        System.out.println("Введите имя");
        username = scanner.nextLine();
        createConnection();
        new Writer().start();
        new Reader().start();
    }
}
