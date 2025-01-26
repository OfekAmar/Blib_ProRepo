package logic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import client.ClientMain;
/**
 * The ReturnController class handles the logic for returning borrowed books.
 * It communicates with the server to process book return requests.
 */
public class ReturnController {
	  /**
     * The client used for communicating with the server.
     */
	
    private final ClientMain client;
    /**
     * Stores the server's response to the return request.
     */
    private Object response;
    /**
     * Synchronization mechanism to wait for the server's response.
     */
    private CountDownLatch latch;
    
    /**
     * Constructs a ReturnController instance with the specified client.
     *
     * @param client the ClientMain instance used for server communication.
     */
	
	public ReturnController(ClientMain client) {
		super();
	
		this.client = client;
	}


	 /**
     * Sends a request to the server to return a book identified by its book ID and copy ID.
     *
     * @param bookId the ID of the book being returned.
     * @param copyId the ID of the specific copy being returned.
     * @return a String message indicating the result of the return process.
     */
	public synchronized String returnBook(String bookId, String copyId) {
		
		String msg = "RETURN_BOOK," + bookId + "," + copyId;
		latch = new CountDownLatch(1);
        client.setMessageHandler((Object serverResponse) -> {
			this.response = serverResponse; // Save the server's response
			latch.countDown(); // Release the latch
        });
    	client.sendMessageToServer(msg);
    	try {
			if (!latch.await(5, TimeUnit.SECONDS)) { // Wait for response with timeout
			    System.err.println("Timeout waiting for server response.");
			    return "ERROR: Timeout waiting for server response.";
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println( response);
		return (String) (response);

		
	}

}
