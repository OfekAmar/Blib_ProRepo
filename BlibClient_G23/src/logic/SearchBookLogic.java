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
	public synchronized void searchBookByTitle(String bookTitle) throws InterruptedException {
        String msg = "SEARCH_BOOK_BY_TITLE," + bookTitle;
        latch = new CountDownLatch(1); // Create a latch to wait for the response

        client.setMessageHandler((Object serverResponse) -> {
            this.response = serverResponse; // Save the server's response
            latch.countDown(); // Release the latch
        });

        client.sendMessageToServer(msg); // Send the message to the server

        latch.await();
        System.out.println(response);// Wait for the server response
    }
	
	public synchronized List<Book> searchBookBySubject(String subject) throws InterruptedException {
	    String msg = "SEARCH_BOOK_BY_SUBJECT," + subject;
	    latch = new CountDownLatch(1); // Create a latch to wait for the response

	    client.setMessageHandler((Object serverResponse) -> {
	        if (serverResponse instanceof List<?>) {
	            this.response = serverResponse; // Save the response
	        } else {
	            System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
	        }
	        latch.countDown(); // Release the latch
	    });

	    client.sendMessageToServer(msg); // Send the message to the server
	    latch.await(); // Wait for the response

	    if (response instanceof List<?>) {
	        List<?> rawList = (List<?>) response;
	        try {
	            // Safely cast to List<Book>
	            List<Book> bookList = (List<Book>) rawList;
	            return bookList;
	        } catch (ClassCastException e) {
	            System.err.println("Failed to cast response to List<Book>: " + e.getMessage());
	            return null;
	        }
	    } else {
	        System.err.println("Response is not of type List<Book>.");
	        return null;
	    }
	}
    
	
	public synchronized void searchBookByFreeText(String freeText) throws InterruptedException {
        String msg = "SEARCH_BOOK_BY_FREE_TEXT," + freeText ;
        //client.sendMessageToServer(msg);
        Thread.sleep(1000);
    }
}
