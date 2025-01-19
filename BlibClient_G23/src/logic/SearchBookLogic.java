package logic;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;

public class SearchBookLogic {

	private ClientMain client;
	private Object response;
	private CountDownLatch latch;

	public SearchBookLogic(ClientMain client) {
		this.client = client;
	}

	public synchronized String searchBookByTitle(String bookTitle) throws InterruptedException {
		String msg = "SEARCH_BOOK_BY_TITLE," + bookTitle;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();
		return (String) (response);// Wait for the server response
		// Thread.sleep(1000);
	}

	public synchronized List<Book> searchBookBySubject(String subject) throws InterruptedException {
		String msg = "SEARCH_BOOK_BY_SUBJECT," + subject;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof List<?>) {
				try {
					List<Book> books = (List<Book>) serverResponse; // Try to cast to List<Book>
					this.response = books; // Save the response
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to List<Book>: " + e.getMessage());
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
				return (List<Book>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to List<Book>: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type List<Book>.");
			// Thread.sleep(1000);
			return null;
		}
	}

	public synchronized List<Book> searchBookByFreeText(String freeText) throws InterruptedException {
		String msg = "SEARCH_BOOK_BY_FREE_TEXT," + freeText;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof List<?>) {
				try {
					List<Book> books = (List<Book>) serverResponse; // Try to cast to List<Book>
					this.response = books; // Save the response
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to List<Book>: " + e.getMessage());
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
				return (List<Book>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to List<Book>: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type List<Book>.");
			// Thread.sleep(1000);
			return null;
		}
	}
}
