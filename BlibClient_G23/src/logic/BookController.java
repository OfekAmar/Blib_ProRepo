package logic;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;
/**
 * Controller for managing book-related operations in the library system.
 */
public class BookController {
	private final ClientMain client;
	private Object response;
	private CountDownLatch latch;
	/**
     * Constructs a BookController object with the specified client.
     * 
     * @param client the client object for server communication
     */
    public BookController(ClientMain client) {
        this.client = client;
    }

    private synchronized void sendSynchronizedMessage(String message) {
        client.sendMessageToServer(message);
    }
    /**
     * Adds a new book to the library system.
     * 
     * @param author the author of the book
     * @param title the title of the book
     * @param subject the subject of the book
     * @param description a description of the book
     * @return the server's response to the operation
     * @throws InterruptedException if interrupted while waiting for the server response
     */
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
    /**
     * Adds a new copy of a book to the library system.
     * 
     * @param bookCode the code of the book
     * @param location the location of the copy
     * @return the server's response to the operation
     * @throws InterruptedException if interrupted while waiting for the server response
     */
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
    /**
     * Edits the details of a specific copy of a book.
     * 
     * @param bookCode the code of the book
     * @param copyId the ID of the copy
     * @param newLocation the new location of the copy
     * @param newStatus the new status of the copy
     * @return the server's response to the operation
     * @throws InterruptedException if interrupted while waiting for the server response
     */

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
    /**
     * Retrieves all books from the library system.
     * 
     * @return a list of all books
     * @throws InterruptedException if interrupted while waiting for the server response
     */
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
    /**
     * Retrieves all copies of a specific book from the library system.
     * 
     * @param bookCode the code of the book
     * @return a list of all copies of the specified book
     * @throws InterruptedException if interrupted while waiting for the server response
     */
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
    /**
     * Retrieves a specific book by its code from the library system.
     * 
     * @param bookCode the code of the book
     * @return the book object corresponding to the code
     * @throws InterruptedException if interrupted while waiting for the server response
     */
    public synchronized Book getBookByCode(int bookCode) throws InterruptedException {
    	String msg = "GET_BOOK_BY_CODE,"+bookCode;
    	latch = new CountDownLatch(1);

    	client.setMessageHandler((Object serverResponse) -> {
    		if (serverResponse instanceof Book) {
    			try {
    				Book book = (Book) serverResponse;
    				this.response = book;
    			} catch (ClassCastException e) {
   					System.err.println("Failed to cast response to Book: " + e.getMessage());
   				}
   			} else {
    				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
    		}
    		latch.countDown(); // Release the latch
   		});
   		client.sendMessageToServer(msg); // Send the message to the server
   		latch.await(); // Wait for the response
   		if (response instanceof Book) {
    		try {
    			return (Book) response;
    		} catch (ClassCastException e) {
   				System.err.println("Failed to cast response to Book: " + e.getMessage());
   				return null;
   			}
   		} else {
   			System.err.println("Response is not of type Book.");
    		return null;
   		}

   	}
    
    /**
     * Updates the status of a specific book copy.
     * 
     * @param bookCode the code of the book
     * @param copyId the ID of the copy
     * @param status the new status of the copy
     * @throws InterruptedException if interrupted while waiting for the server response
     */
       
    public synchronized void updateCopyStatus(String bookCode, String copyId, String status) throws InterruptedException {
        String msg = "UPDATE_COPY_STATUS," + bookCode + "," + copyId + "," + status;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }
    /**
     * Checks the availability of a book.
     * 
     * @param bookCode the code of the book to check
     * @throws InterruptedException if interrupted while waiting for the server response
     */
    public void checkBookAvailability(String bookCode) throws InterruptedException {
        String msg = "CHECK_BOOK_AVAILABILITY," + bookCode;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }


}
