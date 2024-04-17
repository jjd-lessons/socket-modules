package com.itmo.server;

import com.itmo.common.ClassName;
import com.itmo.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int port;
    private final List<Message> messages = new ArrayList<>();
    private final List<ClassName> connectionHandlers = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClassName connectionHandler = new ClassName(socket);
                    // обеспечение потокобезопасности необходимо,
                    // так как разные потоки будут менять список
                    synchronized (connectionHandlers){
                        connectionHandlers.add(connectionHandler);
                        // удалить соединение из списка,
                        // если работа с соединением
                        // не может быть продолжена (ошибка на чтение / запись)
                    }
                    new ThreadForClient(connectionHandler).start();
                } catch (Exception e) {
                    System.out.println("Проблема с установкой нового соединения");
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера");
            throw new RuntimeException(e);
        }
    }

    private class ThreadForClient extends Thread {
        private final ClassName connectionHandler;

        public ThreadForClient(ClassName connectionHandler) {
            this.connectionHandler = connectionHandler;
        }

        @Override
        public void run() {
            while (true) {
                Message fromClient = connectionHandler.read();
                System.out.println(fromClient.getText());
                Message message = new Message("server: " + fromClient.getSender());
                message.setText(fromClient.getText());
                // вариант 1. рассылка по всем соединениям полученного сообщения
                            /*for (ClassName handler : connectionHandlers) {
                                handler.send(message);
                            }*/
                // вариант 2. рассылка сообщений из списка в отдельном потоке
                messages.add(message);
            }
        }
    }
}
