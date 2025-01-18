package logic;

import client.ClientMain;

public class DBtest {
	public static void main(String[] args) {
	    ClientMain clientMain = new ClientMain("localhost", 5555);

	    try {
	        clientMain.openConnection();

	        BookController bookController = new BookController(clientMain);

	        System.out.println("Testing addBook...");
	        bookController.addBook("33333", "BarElrom212", "chat2", "Programming3", "abcd.4");

	        System.out.println("\nTesting checkBookAvailability...");
	        bookController.checkBookAvailability("22222");

	        System.out.println("\nTesting deleteBook...");
	        bookController.deleteBook("22222");

	        System.out.println("\nClosing connection...");
	        clientMain.closeConnection();
	        
	    } catch (Exception e) {
	        System.err.println("Error: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}

            
