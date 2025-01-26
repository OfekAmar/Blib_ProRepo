package logic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;
/**
 * The NotificationsController class manages notifications for both subscribers and librarians.
 * It provides functionality to fetch notifications and mark them as read.
 */
public class NotificationsController {

	private ClientMain client;
	private Object response;
	private CountDownLatch latch;

	public NotificationsController(ClientMain c) {
		this.client = c;
	}
	/**
     * Fetches notifications for a specific subscriber based on their ID and the notification status.
     *
     * @param subID  the ID of the subscriber.
     * @param status the notification status to filter (e.g., unread).
     * @return a map of notification IDs and their corresponding statuses.
     * @throws InterruptedException if the thread is interrupted while waiting for the server response.
     */

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
	  /**
     * Fetches notifications for librarians based on the notification status.
     *
     * @param status the notification status to filter (e.g., unread).
     * @return a map of notification IDs and their corresponding statuses.
     * @throws InterruptedException if the thread is interrupted while waiting for the server response.
     */
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
	/**
     * Marks a subscriber's notification as read.
     *
     * @param notificationId the ID of the notification to mark as read.
     * @throws InterruptedException if the thread is interrupted while waiting for the server response.
     */

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
	/**
     * Marks a librarian's notification as read.
     *
     * @param notificationId the ID of the notification to mark as read.
     * @throws InterruptedException if the thread is interrupted while waiting for the server response.
     */

	public void markAsReadLib(Integer notificationId) throws InterruptedException {
		String msg = "MARK_AS_READ_LIB," + notificationId;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();// Wait for the server response
	}
}
