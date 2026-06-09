package com.ijse.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class AuctionServer {

    private static final int PORT = 6000;

    private ServerSocket serverSocket;

    private ArrayList<ClientHandler> clients = new ArrayList<>();

    private AuctionState auctionState = new AuctionState();

    public AuctionState getAuctionState() {
        return auctionState;
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void startServer() {

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Auction Server Started on Port " + PORT);


            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String cmd = scanner.nextLine();

                    if (cmd.equalsIgnoreCase("END")) {

                        auctionState.endAuction();

                        broadcast("WINNER:"
                                + auctionState.getHighestBidder()
                                + ":"
                                + auctionState.getHighestBid());

                        System.out.println("Auction Ended");
                        break;
                    }
                }
            }).start();

            while (true) {
                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket, this);

                addClient(handler);

                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AuctionServer().startServer();
    }
}