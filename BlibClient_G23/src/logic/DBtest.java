package logic;

import client.ClientMain;

public class DBtest {
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain("localhost", 5555);

        try {
            clientMain.openConnection();

            BookController bookController = new BookController(clientMain);

            System.out.println("Testing addBook...");
            String bookCode = "11111";
            String bookCode2 = "4565";
            String author = "BarElrom214532";
            String title = "chat25434";
            String subject = "Programming3";
            String description = "abcd.4";
            bookController.addBook(bookCode, author, title, subject, description);
            System.out.println("Book added successfully: " + title);

            System.out.println("\nTesting checkBookAvailability...");
            String checkBookCode = "456";
            bookController.checkBookAvailability(checkBookCode);
            System.out.println("Check availability request sent for book code: " + checkBookCode);

            System.out.println("\nTesting getBookDetails...");
            String detailsBookCode = "456";
            bookController.getBookDetails(detailsBookCode);
            System.out.println("Get details request sent for book code: " + detailsBookCode);

            System.out.println("\nTesting deleteBook...");
            bookController.deleteBook(bookCode2);
            System.out.println("Delete request sent for book code: " + bookCode2);

            System.out.println("\nClosing connection...");
            clientMain.closeConnection();
            System.out.println("Connection closed.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
