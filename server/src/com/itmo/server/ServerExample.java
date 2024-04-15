package com.itmo.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerExample {
    public void exampleVoid() {
        ServerSocket serverSocket = null; // позволит установить соединение
        // с клиентскими программами
        OutputStream output = null; // позволит отправлять данные
        InputStream input = null; // позволит получать данные


        try {
            serverSocket = new ServerSocket(2222); // привязка серверной программы к указанному порту.
            // ServerSocket слушает указанный порт...
            System.out.println("Сервер запущен");

            while (true) {
                Socket socket = serverSocket.accept(); // ...и устанавливает соединение при появлении клиента
                System.out.println("Новое подключение");

                System.out.println(socket.getLocalSocketAddress());
                System.out.println(socket.getRemoteSocketAddress());

                output = socket.getOutputStream(); // для отправки данных по socket соединению
                input = socket.getInputStream(); // для получения данных по socket соединению

                // ObjectInputStream objectInput = new ObjectInputStream(input); // десериализация
                // ObjectOutputStream objectOutput = new ObjectOutputStream(output); // сериализация

                // ожидание, когда в inputStream появятся данные
                System.out.println(input.read()); // чтение данных из inputStream
                output.write(2); // отправка данных в outputStream
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
