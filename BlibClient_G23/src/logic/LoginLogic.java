package logic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;
import gui.SubscriberMainController;

/**
 * The `LoginLogic` class handles the login process by sending a username and
 * password to the server. It waits for the server's response and returns either
 * a {@link Subscriber}, a {@link Librarian}, or an error message. This class
 * uses {@link CountDownLatch} for synchronization during the login process.
 * 
 * It communicates with the {@link ClientMain} class to manage server
 * interactions.
 */
public class LoginLogic {
	private ClientMain client;
	private Object response;
	private CountDownLatch latch;

	/**
	 * Constructs a new LoginLogic instance with the specified client. This instance
	 * is responsible for handling the login process.
	 *
	 * @param client the {@link ClientMain} instance used to communicate with the
	 *               server
	 */
	public LoginLogic(ClientMain client) {
		this.client = client;
	}

	/**
	 * Handles the login process by sending the provided username and password to
	 * the server. The method waits for the server's response and returns either a
	 * {@link Subscriber}, a {@link Librarian}, or an error message (as a
	 * {@link String}) depending on the server's response.
	 * 
	 * @param userName the username entered by the user
	 * @param password the password entered by the user
	 * @return an {@link Object} that is either a {@link Subscriber}, a
	 *         {@link Librarian}, or a {@link String} containing an error message
	 * @throws InterruptedException if the thread is interrupted while waiting for
	 *                              the server's response
	 */
	public synchronized Object login(String userName, String password) throws InterruptedException {
		String msg = "LOGIN," + userName + "," + password;
		latch = new CountDownLatch(1);

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof Subscriber) {
				try {
					Subscriber sub = (Subscriber) serverResponse;
					this.response = sub;
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to Subscriber: " + e.getMessage());
				}
			} else if (serverResponse instanceof Librarian) {
				try {
					Librarian lib = (Librarian) serverResponse;
					this.response = lib;
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to Librarian: " + e.getMessage());
				}
			} else if (serverResponse instanceof String) {
				String str = (String) serverResponse;
				this.response = str;
			} else {
				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
			}
			latch.countDown();
		});

		client.sendMessageToServer(msg);
		latch.await();
		return response;
	}

}
