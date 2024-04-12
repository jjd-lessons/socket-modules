package com.itmo.client;

import com.itmo.common.ClassName;
import com.itmo.common.Message;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private InetSocketAddress address;
    private String username;
    private Scanner scanner;
    public Client(InetSocketAddress address) {
        this.address = address;
        scanner = new Scanner(System.in);
    }

    public void startClient(){
        System.out.println("Введите имя");
        username = scanner.nextLine();
        while (true){
            System.out.println("Введите текст сообщения");
            String text = scanner.nextLine();
            try (ClassName connectionHandler =
                    new ClassName(new Socket(
                            address.getHostName(),
                            address.getPort()
                    ))){
                Message message = new Message(username);
                message.setText(text);
                connectionHandler.send(message);
                Message fromServer = connectionHandler.read();
                System.out.println(fromServer.getText());
            }
        }
    }

}
