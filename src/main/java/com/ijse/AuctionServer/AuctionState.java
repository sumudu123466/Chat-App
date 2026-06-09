package com.ijse.AuctionServer;

public class AuctionState {

    private String itemName = "Laptop";
    private double startingPrice = 10000;

    private double highestBid = startingPrice;
    private String highestBidder = "No Bids Yet";

    private boolean auctionEnded = false;

    public synchronized boolean placeBid(String bidder, double amount) {

        if (auctionEnded) {
            return false;
        }

        if (amount > highestBid) {
            highestBid = amount;
            highestBidder = bidder;
            return true;
        }

        return false;
    }

    public synchronized double getHighestBid() {
        return highestBid;
    }

    public synchronized String getHighestBidder() {
        return highestBidder;
    }

    public synchronized String getItemName() {
        return itemName;
    }

    public synchronized double getStartingPrice() {
        return startingPrice;
    }

    public synchronized boolean isAuctionEnded() {
        return auctionEnded;
    }

    public synchronized void endAuction() {
        auctionEnded = true;
    }
}