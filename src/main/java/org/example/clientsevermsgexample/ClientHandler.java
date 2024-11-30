package org.example.clientsevermsgexample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

class ClientHandler implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Set<ClientHandler> clients;

    /**
     * Constructor for the ClientHandler class
     * @param clientSocket
     * @param clients
     */
    public ClientHandler(Socket clientSocket, Set<ClientHandler> clients) {
        this.socket = clientSocket;
        this.clients = clients;
        try{
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader((new InputStreamReader(socket.getInputStream())));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to run the client handler thread
     */
    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                System.out.println("Received from client: " + message);
                if (message != null && message.equalsIgnoreCase("exit")) {
                    break;
                }
                broadcastMessage("Response: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to broadcast a message to all clients
     * @param s
     */
    private void broadcastMessage(String s) {
        System.out.println("Broadcasting message: " + s);
        for (ClientHandler client: clients) {
            if (client != this) {
                client.out.println(s);
            }
            if (clients.size() == 1) {
                client.out.println(s);
            }
        }
    }
}
