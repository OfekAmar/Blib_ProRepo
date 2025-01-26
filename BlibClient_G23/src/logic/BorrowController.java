package logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import client.ClientMain;

/**
 * Logic controller for handling borrowing operations in the library system.
 */
public class BorrowController {

	private final SubscriberController subscriberController;
	private final ClientMain client;
	private Object response;
	private CountDownLatch latch;

	/**
	 * Constructs a BorrowController with the specified subscriber controller and
	 * client.
	 * 
	 * @param subscriberController the subscriber controller
	 * @param client               the client object
	 */
	public BorrowController(SubscriberController subscriberController, ClientMain client) {

		this.subscriberController = subscriberController;
		this.client = client;
	}

	/**
	 * Retrieves a list of active borrows for a subscriber.
	 * 
	 * @param subID the subscriber ID
	 * @return a list of active borrows
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
	public synchronized List<String> showBorrowes(int subID) throws InterruptedException {
		String msg = "GET_ACTIVE_BORROWED," + subID;
		latch = new CountDownLatch(1);

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg);
		latch.await();

		if (response instanceof List) {
			return (List<String>) response;
		} else if (response instanceof String) {
			List<String> errorResponse = new ArrayList<>();
			errorResponse.add((String) response);
			return errorResponse;
		} else {
			throw new IllegalStateException("Invalid server response for GET_ACTIVE_BORROWED");
		}
	}

	/**
	 * Processes a borrowing request for a book.
	 * 
	 * @param bookId       the book ID
	 * @param copyId       the specific copy ID of the book
	 * @param subscriberId the subscriber ID
	 * @param returnDate   the due date for returning the book
	 * @return the response from the server
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
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

	/**
	 * Extends a borrowing period for a specific borrow ID.
	 * 
	 * @param borrowId     the borrow ID
	 * @param selectedDate the new return date
	 * @return the response from the server
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
	public synchronized String extendBorrow(String borrowId, String selecteDate) throws InterruptedException {
		// Send the request to ServerMain
		String msg = "EXTEND_BORROW," + borrowId + "," + selecteDate;
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

	/**
	 * Manually extends a borrowing period.
	 * 
	 * @param subscriberID the subscriber ID
	 * @param borrowID     the borrow ID
	 * @param date         the new return date
	 * @param description  the reason for the extension
	 * @return the response from the server
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
	public synchronized String extendBorrowManualy(int subscriberID, int borrowID, LocalDate date, String description)
			throws InterruptedException {
		String msg = "UPDATE_BORROW_RETURN," + subscriberID + "," + borrowID + "," + date + "," + description;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server
		latch.await();
		return (String) (response);
	}

	/**
	 * Places a book order for a subscriber.
	 * 
	 * @param subscriberID the subscriber ID
	 * @param bookID       the book ID
	 * @return the response from the server
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
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
