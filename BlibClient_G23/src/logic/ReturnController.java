package logic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import client.ClientMain;

public class ReturnController {
	
	
    private final ClientMain client;
    private Object response;
    private CountDownLatch latch;
    
    
	
	public ReturnController(ClientMain client) {
		super();
	
		this.client = client;
	}



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
