package com.ijse.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClientController {

    @FXML private Label itemLabel;
    @FXML private Label highestBidLabel;
    @FXML private TextArea historyArea;
    @FXML private TextField bidField;
    @FXML private Button bidButton;
    @FXML private Button disconnectButton;

    private AuctionClient client;
    private String username;

    @FXML
    public void initialize() {

        try {
            TextInputDialog userDialog = new TextInputDialog();
            userDialog.setHeaderText("Enter Username");
            username = userDialog.showAndWait().orElse("");

            TextInputDialog ipDialog = new TextInputDialog("localhost");
            ipDialog.setHeaderText("Enter Server IP");
            String ip = ipDialog.showAndWait().orElse("localhost");

            client = new AuctionClient(ip, 6000);
            client.send(username);

            startListener();

        } catch (Exception e) {
            historyArea.setText("Connection Failed");
        }
    }

    private void startListener() {

        Thread t = new Thread(() -> {
            try {
                String msg;

                while ((msg = client.receive()) != null) {

                    String finalMsg = msg;

                    Platform.runLater(() -> processMessage(finalMsg));
                }

            } catch (Exception e) {
                Platform.runLater(() ->
                        historyArea.appendText("\nDisconnected"));
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void processMessage(String msg) {

        if (msg.startsWith("ITEM:")) {
            itemLabel.setText("Item: " + msg.substring(5));
        }

        else if (msg.startsWith("START:")) {
            historyArea.appendText("Start Price: " + msg.substring(6) + "\n");
        }

        else if (msg.startsWith("HIGHEST:")) {
            highestBidLabel.setText("Highest: " + msg.substring(8));
        }

        else if (msg.startsWith("NEWBID:")) {
            String[] p = msg.split(":");
            highestBidLabel.setText("Highest: " + p[2]);
            historyArea.appendText(p[1] + " -> " + p[2] + "\n");
        }

        else if (msg.startsWith("REJECTED:")) {
            historyArea.appendText("Rejected: " + msg.substring(9) + "\n");
        }

        else if (msg.startsWith("INFO:")) {
            historyArea.appendText(msg.substring(5) + "\n");
        }

        else if (msg.startsWith("WINNER:")) {

            String[] p = msg.split(":");

            historyArea.appendText("\nAUCTION ENDED\n");
            historyArea.appendText("Winner: " + p[1] + "\n");
            historyArea.appendText("Bid: " + p[2] + "\n");

            bidButton.setDisable(true);
            bidField.setDisable(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Auction Ended");
            alert.setHeaderText("Winner: " + p[1]);
            alert.setContentText("Winning Bid: " + p[2]);
            alert.showAndWait();
        }
    }

    @FXML
    private void placeBid() {

        try {
            double bid = Double.parseDouble(bidField.getText());
            client.send("BID:" + bid);
            bidField.clear();

        } catch (Exception e) {
            historyArea.appendText("Invalid bid\n");
        }
    }

    @FXML
    private void disconnect() {

        try {
            client.close();
            bidButton.setDisable(true);
            disconnectButton.setDisable(true);
            historyArea.appendText("Disconnected\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}