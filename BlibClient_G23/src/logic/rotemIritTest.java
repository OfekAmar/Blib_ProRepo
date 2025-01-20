package logic;

import java.io.IOException;
import java.util.List;

import client.*;

public class rotemIritTest {
	public static void main(String[] args) throws InterruptedException {
		ClientMain c = new ClientMain("localhost", 5555);
		try {
			c.openConnection(); // Open the connection
			SearchBookLogic searchLogic = new SearchBookLogic(c);

			// Search by title
			System.out.println(searchLogic.searchBookByTitle("The Hobbit"));

			// Search by subject
			List<Book> books = searchLogic.searchBookBySubject("Fantasy");
			System.out.println("Books found: " + books);

			// Search by freeText
			books = searchLogic.searchBookByFreeText("adventure");
			System.out.println("Books found IN FREE TEXT: " + books);

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
