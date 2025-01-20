package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.*;

public class rotemIritTest {
	public static void main(String[] args) throws InterruptedException {
		ClientMain c = new ClientMain("localhost", 5555);
		try {
			c.openConnection(); // Open the connection
			// SearchBookLogic searchLogic = new SearchBookLogic(c);

			// Search by title
			// System.out.println(searchLogic.searchBookByTitle("The Hobbit"));

			// Search by subject
			// List<Book> books = searchLogic.searchBookBySubject("Fantasy");
			// System.out.println("Books found: " + books);

			// Search by freeText
			// books = searchLogic.searchBookByFreeText("adventure");
			// System.out.println("Books found IN FREE TEXT: " + books);

			SubscriberController s = new SubscriberController(c);
			ArrayList<String> result = (ArrayList<String>) s.viewBorrowHistory("1");

			if (result.isEmpty()) {
				System.out.println("No data found for the subscriber.");
			} else {
				System.out.println("Subscriber card data: " + result.toString());
			}

		} catch (IOException e) {
			System.err.println("Failed to connect to server: " + e.getMessage());
		} finally {
			try {
				c.closeConnection(); // Close the connection
			} catch (IOException e) {
				System.err.println("Error closing the connection: " + e.getMessage());
			}

		}
	}
}
