package org.example.clientsevermsgexample;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class MainController implements Initializable {
    private PrintWriter out;
    private BufferedReader in;
    private Set<ClientHandler> clients = new HashSet<>();


    @FXML
    private ComboBox dropdownPort;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropdownPort.getItems().addAll("7",     // ping
                "13",     // daytime
                "21",     // ftp
                "23",     // telnet
                "71",     // finger
                "80",     // http
                "119",     // nntp (news)
                "161"      // snmp);
        );

        // connects to the server as the application starts
        new Thread(this::runServer).start();
    }

    @FXML
    private Button clearBtn;



    @FXML
    private TextArea resultArea;

    @FXML
    private Label server_lbl;

    @FXML
    private Button testBtn;

    @FXML
    private Label test_lbl;

    @FXML
    private TextField urlName;

    Socket socket1;

    @FXML
    private Label lb122, lb12;
    TextField msgText;

    @FXML
    void checkConnection(ActionEvent event) {

        String host = urlName.getText();
        int port = Integer.parseInt(dropdownPort.getValue().toString());

        try {
            Socket sock = new Socket(host, port);
            resultArea.appendText(host + " listening on port " + port + "\n");
            sock.close();
        } catch (UnknownHostException e) {
            resultArea.setText(String.valueOf(e) + "\n");
            return;
        } catch (Exception e) {
            resultArea.appendText(host + " not listening on port "
                    + port + "\n");
        }


    }


    @FXML
    void clearBtn(ActionEvent event) {
        resultArea.setText("");
        urlName.setText("");

    }



    @FXML
    void startServer(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Label lb11 = new Label("Server");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);

        lb12 = new Label("info");
        lb12.setLayoutX(100);
        lb12.setLayoutY(200);
        root.getChildren().addAll(lb11, lb12);
        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        lb12.setText("Server is running and waiting for a client...");

        stage.setTitle("Server");
        stage.show();


//        new Thread(this::runServer).start();

    }

//    String message;

    /**
     * This method runs the server and listens for incoming connections.
     */
    private void runServer() {
        try {

            ServerSocket serverSocket = new ServerSocket(6666);
            updateServer("Server is running and waiting for a client...");
            while (true) { // Infinite loop
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                    updateServer("Client connected!");

//                    new Thread(() -> {
//                        try {
//                            sleep(3000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });


//                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
//                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
//
//                    message = dis.readUTF();
//                    updateServer("Message from client: " + message);
//
//                    // Sending a response back to the client
//                    dos.writeUTF("Received: " + message);
//
//                    dis.close();
//                    dos.close();




                } catch (IOException e) {
                    updateServer("Error: " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        } catch (IOException e) {
            updateServer("Error: " + e.getMessage());
        }
    }

    private void updateServer(String message) {
        // Run on the UI thread
        javafx.application.Platform.runLater(() -> lb12.setText(message + "\n"));
    }


    @FXML
    void startClient(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Button connectButton = new Button("Connect to server");
        connectButton.setLayoutX(100);
        connectButton.setLayoutY(300);
        connectButton.setOnAction(this::connectToServer);
        // new Thread(this::connectToServer).start();

        Button sendButton = new Button("Send");
        sendButton.setLayoutX(200);
        sendButton.setLayoutY(300);
        sendButton.setOnAction(this::sendMessage);

        Label lb11 = new Label("Client");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);
        msgText = new TextField("msg");
        msgText.setLayoutX(100);
        msgText.setLayoutY(150);

        lb122 = new Label("info");
        lb122.setLayoutX(100);
        lb122.setLayoutY(200);
        root.getChildren().addAll(lb11, lb122, connectButton, msgText);


        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show();


    }

    /**
     * This method is used to connect to the server
     * @param event
     */
    private void connectToServer(ActionEvent event) {


        try {
            socket1 = new Socket("localhost", 6666);

            out = new PrintWriter(socket1.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket1.getInputStream()));

            new Thread(() -> {
                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        updateTextClient(response);
                    }
                } catch(IOException e) {
                    updateTextClient("Error: " + e.getMessage());
                }
            }).start();
            out.println(msgText.getText());

//            DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
//            DataInputStream dis = new DataInputStream(socket1.getInputStream());
//
//            dos.writeUTF(msgText.getText());
//            String response = dis.readUTF();
//            updateTextClient("Server response: " + response + "\n");
//
//            dis.close();
//            dos.close();
//            socket1.close();
        } catch (Exception e) {
            updateTextClient("Error: " + e.getMessage() + "\n");
        }


    }

    /**
     * This method is used to send a message to the server
     * @param event
     */
    @FXML
    void sendMessage(ActionEvent event) {
        if (out != null) {
            out.println(msgText.getText());
        }
    }

    private void updateTextClient(String message) {
        // Run on the UI thread
        javafx.application.Platform.runLater(() -> lb122.setText(message + "\n"));
    }

    /**
     * This method is used to open a new window for user 1
     * @param event
     */
    @FXML
    void user1Msg(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/clientsevermsgexample/client-view.fxml"));
            Parent root = loader.load();
            ClientView controller = loader.getController();
            controller.initialize();
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.setTitle("User 1");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to open a new window for user 2
     * @param event
     */
    @FXML
    void user2Msg(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/clientsevermsgexample/client-view.fxml"));
            Parent root = loader.load();
            ClientView controller = loader.getController();
            controller.initialize();
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.setTitle("User 2");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
