package server;

import com.ijse.AuctionServer.AuctionServer;

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
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            writer = new PrintWriter(
                    socket.getOutputStream(), true);

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

            System.out.println(username + " connected");

            server.broadcast("INFO:" + username + " joined auction");

            sendMessage("ITEM:"
                    + server.getAuctionState().getItemName());

            sendMessage("START:"
                    + server.getAuctionState().getStartingPrice());

            sendMessage("HIGHEST:"
                    + server.getAuctionState().getHighestBid());

            String message;

            while ((message = reader.readLine()) != null) {

                if (message.startsWith("BID:")) {

                    double bid =
                            Double.parseDouble(message.substring(4));

                    boolean accepted =
                            server.getAuctionState()
                                    .placeBid(username, bid);

                    if (accepted) {

                        System.out.println(
                                "Valid Bid -> "
                                        + username
                                        + " : "
                                        + bid);

                        server.broadcast(
                                "NEWBID:"
                                        + username
                                        + ":"
                                        + bid);

                    } else {

                        writer.println(
                                "REJECTED:Bid must be higher than "
                                        + server.getAuctionState()
                                        .getHighestBid());

                        System.out.println(
                                "Invalid Bid -> "
                                        + username
                                        + " : "
                                        + bid);
                    }
                }
            }

        } catch (Exception e) {

            System.out.println(username + " disconnected");

        } finally {

            server.removeClient(this);

            try {
                socket.close();
            } catch (Exception ex) {
            }
        }
    }
}