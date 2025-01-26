package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import client.ClientMain;

public class SubscriberController {

	private final ClientMain client;
	private Object response;
	private CountDownLatch latch;

	public SubscriberController(ClientMain client) {
		this.client = client;
	}

	private synchronized void sendSynchronizedMessage(String message) {
		client.sendMessageToServer(message);
	}

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

	public void deleteSubscriber(String subscriberId) throws InterruptedException {

	}

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

	public void freezeSubscriber(String subscriberId) throws InterruptedException {
		String msg = "FREEZE_SUBSCRIBER," + subscriberId;
		sendSynchronizedMessage(msg);
		TimeUnit.MILLISECONDS.sleep(500);
	}

	public void unfreezeSubscriber(String subscriberId) throws InterruptedException {

	}

	public void getSubscriberHistory(String subscriberId) throws InterruptedException {

	}

	public void notifySubscriber(String subscriberId, String message) throws InterruptedException {
		String msg = "NOTIFY_SUBSCRIBER," + subscriberId + "," + message;
		sendSynchronizedMessage(msg);
		TimeUnit.MILLISECONDS.sleep(500);
	}

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
