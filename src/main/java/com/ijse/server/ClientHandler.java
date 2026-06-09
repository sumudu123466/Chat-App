package com.ijse.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private String username;
    private AuctionServer server;

    public ClientHandler(Socket socket, AuctionServer server) {

        this.socket = socket;
        this.server = server;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        writer.println(msg);
    }

    @Override
    public void run() {

        try {
            username = reader.readLine();

            server.broadcast("INFO:" + username + " joined auction");

            sendMessage("ITEM:" + server.getAuctionState().getItemName());
            sendMessage("START:" + server.getAuctionState().getStartingPrice());
            sendMessage("HIGHEST:" + server.getAuctionState().getHighestBid());

            String msg;

            while ((msg = reader.readLine()) != null) {

                if (msg.startsWith("BID:")) {

                    double bid = Double.parseDouble(msg.substring(4));

                    boolean ok = server.getAuctionState().placeBid(username, bid);

                    if (ok) {
                        server.broadcast("NEWBID:" + username + ":" + bid);
                    } else {
                        sendMessage("REJECTED:Bid too low");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(username + " disconnected");
        } finally {
            server.removeClient(this);
        }
    }
}