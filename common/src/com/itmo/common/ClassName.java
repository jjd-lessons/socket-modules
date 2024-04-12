package com.itmo.common;

// класс будет использоваться и в модуле client, и в модуле server

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Objects;

// придумать подходящее имя класса
public class ClassName implements AutoCloseable{
    // перечислить свойства, необходимые
    // для отправки сообщения по сокет соединению
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;

    // объявить конструктор с необходимыми параметрами
    public ClassName(Socket socket) throws IOException {
        this.socket = Objects.requireNonNull(socket);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    // метод отправки Message по сокет соединению
    public void send(Message message) throws IOException {
        message.setSentAt(LocalDateTime.now());
        outputStream.writeObject(message);
        outputStream.flush();
    }

    // метод получения Message по сокет соединению
    public Message read() throws IOException {
        try {
            return (Message) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
        inputStream.close();
        socket.close();
    }
}















