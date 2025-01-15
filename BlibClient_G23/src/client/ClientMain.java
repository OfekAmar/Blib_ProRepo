package client;

import java.util.function.Consumer;

import ocsf.client.*;

public class ClientMain extends AbstractClient {
    private Consumer<String> messageHandler;

    public ClientMain(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof String && messageHandler != null) {
            System.out.println("Message from server: " + msg);
            messageHandler.accept((String) msg);
        }
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    public void sendMessageToServer(String message) {
        try {
            sendToServer(message);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
}
