package client;

import java.util.function.Consumer;

import ocsf.client.*;

public class ClientMain extends AbstractClient {
	private Consumer<Object> messageHandler;

	public ClientMain(String host, int port) {
		super(host, port);
	}

	@Override
	protected synchronized void handleMessageFromServer(Object msg) {
		if (messageHandler != null) {
			try {
				messageHandler.accept(msg); // Pass the response to the message handler
			} catch (Exception e) {
				System.err.println("Error processing server response: " + e.getMessage());
			}
		} else {
			System.err.println("No message handler set. Message: " + msg);
		}
	}

	public void setMessageHandler(Consumer<Object> handler) {

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
