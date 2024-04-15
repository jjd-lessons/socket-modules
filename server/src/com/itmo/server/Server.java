package com.itmo.server;

import com.itmo.common.ClassName;
import com.itmo.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void startServer(){
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClassName connectionHandler = new ClassName(socket);
                    // throw new IOException()
                    Message fromClient = connectionHandler.read();
                    System.out.println(fromClient.getText());
                    Message message = new Message("server");
                    message.setText("text");
                    connectionHandler.send(message);
                } catch (Exception e) {
                    System.out.println("Проблема с соединением");
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера");
            throw new RuntimeException(e);
        }
    }
}
