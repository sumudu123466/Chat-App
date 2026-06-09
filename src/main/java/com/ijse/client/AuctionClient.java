package com.ijse.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AuctionClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public AuctionClient(String host, int port) throws Exception {
        socket = new Socket(host, port);

        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        writer = new PrintWriter(
                socket.getOutputStream(), true);
    }

    public void send(String msg) {
        writer.println(msg);
    }

    public String receive() throws Exception {
        return reader.readLine();
    }

    public void close() throws Exception {
        socket.close();
    }
}