package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import client.ClientMain;

/**
 * The `SubscriberController` class handles various operations related to
 * subscribers in the library system. It allows for adding, deleting, editing,
 * and viewing subscriber details, as well as managing subscriptions (such as
 * freezing or unfreezing a subscriber).
 */
public class SubscriberController {

	private final ClientMain client;
	private Object response;
	private CountDownLatch latch;

	/**
	 * Constructs a SubscriberController with the specified client for
	 * communication.
	 * 
	 * @param client the client instance used for sending messages to the server.
	 */
	public SubscriberController(ClientMain client) {
		this.client = client;
	}

	/**
	 * Sends a synchronized message to the server and waits for the response.
	 * 
	 * @param message the message to send to the server.
	 */
	private synchronized void sendSynchronizedMessage(String message) {
		client.sendMessageToServer(message);
	}

	/**
	 * Adds a new subscriber with the specified details.
	 * 
	 * @param name     the name of the subscriber.
	 * @param phone    the phone number of the subscriber.
	 * @param email    the email address of the subscriber.
	 * @param password the password of the subscriber.
	 * @param userName the username of the subscriber.
	 * @return a message from the server indicating the result of the operation.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public String addSubscriber(String name, String phone, String email, String password, String userName)
			throws InterruptedException {
		String msg = "ADD_SUBSCRIBER," + name + "," + phone + "," + email + "," + password + "," + userName;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();
		return (String) (response);// Wait for the server response
	}

	/**
	 * Edits the details of an existing subscriber.
	 * 
	 * @param subscriberID the ID of the subscriber to edit.
	 * @param phone        the new phone number of the subscriber.
	 * @param email        the new email address of the subscriber.
	 * @param password     the new password of the subscriber.
	 * @return a message from the server indicating the result of the operation.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public String editSubscriber(String subscriberID, String phone, String email, String password)
			throws InterruptedException {
		String msg = "EDIT_SUBSCRIBER," + subscriberID + "," + phone + "," + email + "," + password;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();
		return (String) (response);// Wait for the server response

	}

	/**
	 * Checks the status of a subscriber.
	 * 
	 * @param subscriberId the ID of the subscriber whose status is to be checked.
	 * @return the status of the subscriber.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public synchronized String checkSubscriberStatus(String subscriberId) throws InterruptedException {
		String msg = "CHECK_SUBSCRIBER_STATUS," + subscriberId;
		latch = new CountDownLatch(1);
		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg);
		latch.await();
		return (String) (response);

	}

	/**
	 * Freezes a subscriber, preventing them from borrowing books.
	 * 
	 * @param subscriberId the ID of the subscriber to freeze.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public void freezeSubscriber(String subscriberId) throws InterruptedException {
		String msg = "FREEZE_SUBSCRIBER," + subscriberId;
		sendSynchronizedMessage(msg);
		TimeUnit.MILLISECONDS.sleep(500);
	}

	/**
	 * Sends a notification to a subscriber.
	 * 
	 * @param subscriberId the ID of the subscriber to notify.
	 * @param message      the message to send to the subscriber.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public void notifySubscriber(String subscriberId, String message) throws InterruptedException {
		String msg = "NOTIFY_SUBSCRIBER," + subscriberId + "," + message;
		sendSynchronizedMessage(msg);
		TimeUnit.MILLISECONDS.sleep(500);
	}

	/**
	 * Retrieves the list of books borrowed by a subscriber based on their ID.
	 * 
	 * @param subscriberId the ID of the subscriber whose borrowed books are to be
	 *                     retrieved.
	 * @return a map of borrow IDs and their corresponding book details.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public Map<Integer, Map<String, String>> viewBorrowsOfSub(int subscriberId) throws InterruptedException {
		String msg = "BORROW_BY_ID," + subscriberId;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof Map<?, ?>) {
				this.response = (Map<Integer, Map<String, String>>) serverResponse;
			} else {
				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
				this.response = null;
			}
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server
		latch.await(); // Wait for the response

		if (response == null) {
			System.err.println("Failed to retrieve subscriber card. Response is null or invalid.");
			return new HashMap<Integer, Map<String, String>>();
		}
		System.out.println(response.toString());
		return (Map<Integer, Map<String, String>>) response;
	}

	/**
	 * Retrieves all subscribers in the system.
	 * 
	 * @return a list of all subscribers.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public synchronized List<Subscriber> getAllSubscribers() throws InterruptedException {
		String msg = "GET_ALL_SUBSCRIBERS";
		latch = new CountDownLatch(1);

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof List<?>) {
				try {
					List<Subscriber> subscribersList = (List<Subscriber>) serverResponse;
					this.response = subscribersList;
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to List<Subscriber>: " + e.getMessage());
				}
			} else {
				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
			}
			latch.countDown(); // Release the latch
		});
		client.sendMessageToServer(msg); // Send the message to the server
		latch.await(); // Wait for the response
		if (response instanceof List<?>) {

			try {
				return (List<Subscriber>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to List<Subscriber>: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type List<Subscriber>.");
			// Thread.sleep(1000);
			return null;
		}
	}

	/**
	 * Searches for a subscriber by their ID.
	 * 
	 * @param subscriberId the ID of the subscriber to search for.
	 * @return the subscriber object if found, null otherwise.
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response.
	 */
	public synchronized Subscriber searchSubscriberById(String subscriberId) throws InterruptedException {
		String msg = "SEARCH_SUBSCRIBER_BY_ID," + subscriberId;
		latch = new CountDownLatch(1);

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof Subscriber) {
				try {
					Subscriber sub = (Subscriber) serverResponse;
					this.response = sub;
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to Subscriber: " + e.getMessage());
				}
			} else {
				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
			}
			latch.countDown(); // Release the latch
		});
		client.sendMessageToServer(msg); // Send the message to the server
		latch.await(); // Wait for the response
		if (response instanceof Subscriber) {

			try {
				return (Subscriber) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to Subscriber: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type Subscriber.");
			// Thread.sleep(1000);
			return null;
		}

	}

}
