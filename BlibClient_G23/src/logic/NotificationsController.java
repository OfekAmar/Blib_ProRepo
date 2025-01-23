package logic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;

public class NotificationsController {

	private ClientMain client;
	private Object response;
	private CountDownLatch latch;

	public NotificationsController(ClientMain c) {
		this.client = c;
	}

	public synchronized Map<String, Integer> getNotificationsSub(int subID, int status) throws InterruptedException {
		String msg = "GET_NOTIFICATIONS_SUB," + subID + "," + status;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof Map<?, ?>) {
				try {
					this.response = (Map<String, Integer>) serverResponse;
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to Map<String, Integer>: " + e.getMessage());
				}
			} else {
				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
			}
			latch.countDown(); // Release the latch
		});
		client.sendMessageToServer(msg); // Send the message to the server
		latch.await(); // Wait for the response
		if (response instanceof Map<?, ?>) {

			try {
				return (Map<String, Integer>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to Map<String, Integer>: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type Map<String, Integer>.");
			return null;
		}
	}

	public synchronized Map<String, Integer> getNotificationsLib(int status) throws InterruptedException {
		String msg = "GET_NOTIFICATIONS_LIB," + status;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof Map<?, ?>) {
				try {
					this.response = (Map<String, Integer>) serverResponse;
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to Map<String, Integer>: " + e.getMessage());
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
				return (Map<String, Integer>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to List<Book>: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type List<Book>.");
			return null;
		}
	}

	public void markAsReadSubs(Integer notificationId) throws InterruptedException {
		String msg = "MARK_AS_READ_SUB," + notificationId;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();// Wait for the server response
	}

	public void markAsReadLib(Integer notificationId) throws InterruptedException {
		String msg = "GET_NOTIFICATIONS_LIB," + notificationId;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();// Wait for the server response
	}
}
