package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

			// SubscriberController s = new SubscriberController(c);
			// ArrayList<String> result = (ArrayList<String>) s.viewBorrowHistory("1");

			ReportController r = new ReportController(c);
			List<Record> result = r.getMonthlyStatusReport(1, 2025);
			System.out.println(result);
			Map<String, Integer> re = r.getStatusStatistics();
			System.out.println(re);
			// if (result.isEmpty()) {
			// System.out.println("No data found for the subscriber.");
			// } else {
			// System.out.println("Subscriber card data: " + result.toString());
			// }
			// SubscriberController s = new SubscriberController(c);

			// System.out.println(s.editSubscriber("7","3333444466","irit14@gmail.com","password"));

			// BorrowController b = new BorrowController(null, c);
			// System.out.println(b.orderBook(7, 1));

			// SubscriberController sc=new SubscriberController(c);
			// System.out.println(sc.getAllSubscribers());

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
