package server;

import com.ijse.AuctionServer.AuctionState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class AuctionServer {

    private static final int PORT = 6000;

    private ServerSocket serverSocket;

    private ArrayList<server.ClientHandler> clients =
            new ArrayList<>();

    private AuctionState auctionState =
            new AuctionState();

    public AuctionState getAuctionState() {
        return auctionState;
    }

    public synchronized void addClient(
            server.ClientHandler client) {

        clients.add(client);
    }

    public synchronized void removeClient(
            server.ClientHandler client) {

        clients.remove(client);
    }

    public synchronized void broadcast(
            String message) {

        for (server.ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void startServer() {

        try {

            serverSocket = new ServerSocket(PORT);

            System.out.println(
                    "Auction Server Started on Port "
                            + PORT);

            Thread consoleThread = new Thread(() -> {

                Scanner scanner =
                        new Scanner(System.in);

                while (true) {

                    String cmd =
                            scanner.nextLine();

                    if (cmd.equalsIgnoreCase("END")) {

                        auctionState.endAuction();

                        String winner =
                                auctionState
                                        .getHighestBidder();

                        double amount =
                                auctionState
                                        .getHighestBid();

                        broadcast(
                                "WINNER:"
                                        + winner
                                        + ":"
                                        + amount);

                        System.out.println(
                                "Auction Ended");

                        break;
                    }
                }
            });

            consoleThread.start();

            while (true) {

                Socket socket =
                        serverSocket.accept();

                server.ClientHandler handler =
                        new server.ClientHandler(
                                socket,
                                this);

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