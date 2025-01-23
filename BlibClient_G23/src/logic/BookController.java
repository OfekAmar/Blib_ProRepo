package logic;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;

public class BookController {
	private final ClientMain client;
	private Object response;
	private CountDownLatch latch;

    public BookController(ClientMain client) {
        this.client = client;
    }

    private synchronized void sendSynchronizedMessage(String message) {
        client.sendMessageToServer(message);
    }

    public String addBook(String author, String title, String subject, String description)
			throws InterruptedException {
		String msg = "ADD_BOOK," + author + "," + title + "," + subject + "," + description;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();
		return (String) (response);// Wait for the server response
	}
    
    public String addCopyOfBook(int bookCode, String location)
			throws InterruptedException {
		String msg = "ADD_COPY_OF_BOOK," + bookCode + "," + location;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();
		return (String) (response);// Wait for the server response
	}

    public String editCopyOfBook(int bookCode, int copyId, String newLocation, String newStatus)
			throws InterruptedException {
		String msg = "EDIT_COPY_OF_BOOK," + bookCode + "," + copyId + "," + newLocation + "," + newStatus;
		latch = new CountDownLatch(1); // Create a latch to wait for the response

		client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg); // Send the message to the server

		latch.await();
		return (String) (response);// Wait for the server response
	}
    
    public synchronized List<Book> getAllBooks() throws InterruptedException {
    	System.out.println("getallbooks called");
		String msg = "GET_ALL_BOOKS";
		latch = new CountDownLatch(1);

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof List<?>) {
				try {
					List<Book> booksList = (List<Book>) serverResponse;
					this.response = booksList;
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
 
    public synchronized List<CopyOfBook> getAllBookCopies(int bookCode) throws InterruptedException {
		String msg = "GET_ALL_BOOK_COPIES,"+bookCode;
		latch = new CountDownLatch(1);

		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof List<?>) {
				try {
					List<CopyOfBook> copiesList = (List<CopyOfBook>) serverResponse;
					this.response = copiesList;
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to List<CopyOfBook>: " + e.getMessage());
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
				return (List<CopyOfBook>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to List<CopyOfBook>: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type List<CopyOfBook>.");
			// Thread.sleep(1000);
			return null;
		}
	}
  
       
    public synchronized void updateCopyStatus(String bookCode, String copyId, String status) throws InterruptedException {
        String msg = "UPDATE_COPY_STATUS," + bookCode + "," + copyId + "," + status;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }

    public void checkBookAvailability(String bookCode) throws InterruptedException {
        String msg = "CHECK_BOOK_AVAILABILITY," + bookCode;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }


}
