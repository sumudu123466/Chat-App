package com.ijse.server;

public class AuctionState {

    private String itemName = "Laptop";
    private double highestBid = 10000;
    private String highestBidder = "None";
    private boolean ended = false;

    public synchronized boolean placeBid(String user, double bid) {

        if (ended) return false;

        if (bid > highestBid) {
            highestBid = bid;
            highestBidder = user;
            return true;
        }
        return false;
    }

    public void endAuction() {
        ended = true;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public String getItemName() {
        return itemName;
    }

    public double getStartingPrice() {
        return 10000;
    }
}