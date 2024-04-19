package com.itmo.server;

import com.itmo.common.ClassName;
import com.itmo.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

// блокирующая очередь на 20 элементов
// put -> [] <- take

public class Server {
    private final int port;
    // private final List<Message> messages = new ArrayList<>();
    private final ArrayBlockingQueue<Message> messages =
            new ArrayBlockingQueue<>(1000, true);
    // private final List<ClassName> connectionHandlers = new ArrayList<>();
    // private final List<ClassName> connectionHandlers =
    // Collections.synchronizedList(new ArrayList<>());
    private final List<ClassName> connectionHandlers = new CopyOnWriteArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            new Sender().start();

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClassName connectionHandler = new ClassName(socket);
                    // обеспечение потокобезопасности необходимо,
                    // так как разные потоки будут менять список
                    // synchronized (connectionHandlers) {
                    connectionHandlers.add(connectionHandler);
                    // удалить соединение из списка,
                    // если работа с соединением
                    // не может быть продолжена (ошибка на чтение / запись)
                    // }
                    new ThreadForClient(connectionHandler).start();
                } catch (Exception e) {
                    System.out.println("Проблема с установкой нового соединения");
                    // throw new RuntimeException(e);
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
                // удалить соединение из списка,
                // если работа с соединением
                // не может быть продолжена (ошибка на чтение / запись)
                // синхронизация на connectionHandlers при удалении
                Message fromClient = null;
                try {
                    fromClient = connectionHandler.read();
                } catch (IOException e) {
                    // synchronized (connectionHandlers){
                    connectionHandlers.remove(connectionHandler);
                    return;
                    // }
                }
                System.out.println(fromClient.getText());
                Message message = new Message("server: " + fromClient.getSender());
                message.setText(fromClient.getText());
                // вариант 1. рассылка по всем соединениям полученного сообщения

                /*for (ClassName handler : connectionHandlers) {
                    try {
                        handler.send(message);
                    } catch (IOException e) {
                        // synchronized (connectionHandlers){
                            connectionHandlers.remove(handler);
                        // }
                    }
                }*/
                // вариант 2. рассылка сообщений из списка в отдельном потоке
                // messages.add(message);
                try {
                    // вызывающий поток будет заблокирован до тех пор,
                    // пока в очереди не появится свободное место
                    messages.put(message);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    // Thread.currentThread().interrupt();
                }
                // notify();
            }
        }
    }

    private class Sender extends Thread {
        @Override
        public void run() {
            // рассылка сообщений по всем соединениям
            // while (messages.isEmpty()) wait();
            // удаляет сообщение из очереди и удаляет его
            // поток будет заблокирован, пока очередь пуста
            // [] <-
            while (!Thread.currentThread().isInterrupted()) { // true
                try {
                    Message message = messages.take(); // блокировка потока
                    for (ClassName handler : connectionHandlers) {
                        try {
                            handler.send(message);
                        } catch (IOException e) {
                            connectionHandlers.remove(handler);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
