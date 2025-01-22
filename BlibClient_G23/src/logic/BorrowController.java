package logic;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import client.ClientMain;

public class BorrowController {

	private final SubscriberController subscriberController;
	private final ClientMain client;
	private Object response;
	private CountDownLatch latch;

	public BorrowController(SubscriberController subscriberController, ClientMain client) {

		this.subscriberController = subscriberController;
		this.client = client;
	}

	public synchronized String processBorrow(String bookId, String copyId, String subscriberId, LocalDate returnDate)
			throws InterruptedException {
		// Send the request to ServerMain

		String SubStatus = subscriberController.checkSubscriberStatus(subscriberId);
		if (SubStatus.equals("ERROR: Subscriber not found.")) {
			System.out.println(SubStatus);
			return SubStatus;
		} else if (SubStatus.equals("Subscriber status: " + "frozen")) {
			System.out.println(SubStatus);
			return SubStatus;

		} else {
			String msg = "PROCESS_BORROW," + bookId + "," + copyId + "," + subscriberId + "," + returnDate;
			latch = new CountDownLatch(1);
			client.setMessageHandler((Object serverResponse) -> {
				this.response = serverResponse; // Save the server's response
				latch.countDown(); // Release the latch
			});
			client.sendMessageToServer(msg);
			if (!latch.await(5, TimeUnit.SECONDS)) { // Wait for response with timeout
				System.err.println("Timeout waiting for server response.");
				return "ERROR: Timeout waiting for server response.";
			}
			System.out.println(response);
			return (String) (response);
		}

	}

	public synchronized String extendBorrow(String borrowId) throws InterruptedException {
		// Send the request to ServerMain
		String msg = "EXTEND_BORROW," + borrowId;
		latch = new CountDownLatch(1);
		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});
		client.sendMessageToServer(msg);
		latch.await();
		System.out.println(response);
		return (String) (response);
	}

	public synchronized void extendBorrowManualy(int subscriberID, int borrowID, LocalDate date)
			throws InterruptedException {
		String msg = "UPDATE_BORROW_RETURN," + subscriberID + "," + borrowID + "," + date;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server
		latch.await();
		System.out.println(response);
	}

	public synchronized String orderBook(int subscriberID, int bookID) throws InterruptedException {
		String msg = "ORDER_BOOK," + subscriberID + "," + bookID;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();
		return (String) (response);// Wait for the server response
	}

}
