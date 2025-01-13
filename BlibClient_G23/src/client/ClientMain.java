package client;

import java.util.function.Consumer;

import ocsf.client.*;

//the class extends AbstractClient part of OCSF lib
//managed the connection between the client and the server
public class ClientMain extends AbstractClient {
	// variable to hold the messagehandler
	private Consumer<String> messageHandler;

	// initialize the connection to the server using port and host address
	public ClientMain(String host, int port) {
		super(host, port);
	}

	// automotacly operates every time the server send message to the client
	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof String && messageHandler != null) {
			//System.out.println("Message from server: " + msg);
			messageHandler.accept((String) msg);// move the message to continue handling
		}
	}

	// responable to handle the message with handler (method, lambada, etc)
	public void setMessageHandler(Consumer<String> handler) {
		this.messageHandler = handler;
	}

	// send message to the server
	public void sendMessageToServer(String message) {
		try {
			sendToServer(message);
		} catch (Exception e) {
			System.err.println("Failed to send message: " + e.getMessage());
		}
	}
}
