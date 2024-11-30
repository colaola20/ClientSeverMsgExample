package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientView {
    @FXML
    private TextArea chatArea;

    @FXML
    private AnchorPane ap_main;

    @FXML
    private Button button_send;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private PrintWriter out;
    private BufferedReader in;

    /**
     * This method is used to initialize the client view
     */
    public void initialize() {
        try {
            Socket socket = new Socket("localhost", 6666);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));



            new Thread(() -> {
                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        updateChatArea(response);
                    }
                } catch (Exception e) {
                    updateChatArea("Error: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            updateChatArea("Error: " + e.getMessage());
        }
    }

    /**
     * This method is used to send a message to the server
     * @param event
     */
    @FXML
    private void sendMessage(ActionEvent event) {
        String message = tf_message.getText();
        if (message != null && !message.isEmpty()) {
            out.println(message);
            updateChatArea("You: " + message);
            tf_message.clear();
        }
    }

    /**
     * This method is used to update the chat area
     * @param message
     */
    private void updateChatArea(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }
}
