package client;

import java.util.function.Consumer;

import ocsf.client.*;

public class ClientMain extends AbstractClient {
    private Consumer<String> messageHandler;

    public ClientMain(String host, int port) {
        super(host, port);
    }

    @Override
    protected synchronized void handleMessageFromServer(Object msg) {
        if (msg instanceof String) {
            System.out.println("Message received from server: " + msg);
            if (messageHandler != null) {
                try {
                    messageHandler.accept((String) msg);
                } catch (Exception e) {
                    System.err.println("Error processing server response: " + e.getMessage());
                }
            } else {
                System.err.println("No message handler set. Message: " + msg);
            }
        } else {
            System.err.println("Invalid message type received from server.");
        }
    }


    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    public synchronized void sendMessageToServer(String message) {
        try {
            sendToServer(message);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
    
}
