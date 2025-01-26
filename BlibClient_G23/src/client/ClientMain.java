package client;

import java.util.function.Consumer;

import ocsf.client.*;

/**
 * The main client class that handles communication with the server. Extends
 * {@link AbstractClient} and provides functionality to send messages to the
 * server and handle responses asynchronously using a message handler.
 */
public class ClientMain extends AbstractClient {

	private Consumer<Object> messageHandler;

	/**
	 * Constructs a new client instance.
	 * 
	 * @param host the hostname or IP address of the server
	 * @param port the port number on which the server is listening
	 */
	public ClientMain(String host, int port) {
		super(host, port);
	}

	/**
	 * Handles messages received from the server. This method is called
	 * automatically when the server sends a message. The message is passed to the
	 * message handler if it is set.
	 * 
	 * @param msg the message received from the server
	 */
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

	/**
	 * Sets the message handler for processing messages from the server. The handler
	 * is a {@link Consumer} that takes an {@link Object} as input.
	 * 
	 * @param handler the message handler to set
	 */
	public void setMessageHandler(Consumer<Object> handler) {

		this.messageHandler = handler;
	}

	/**
	 * Sends a message to the server. If the operation fails, an error message is
	 * printed to the console.
	 * 
	 * @param message the message to send to the server
	 */
	public synchronized void sendMessageToServer(String message) {
		try {
			sendToServer(message);
		} catch (Exception e) {
			System.err.println("Failed to send message: " + e.getMessage());
		}
	}

}
