package logic;

import java.util.Scanner;

import client.ClientMain;

public class DBtest {
	public static void main(String[] args) {
        // יצירת מופע של ClientMain
		ClientMain clientMain = new ClientMain("localhost", 5555);
		

        try {
            // פתיחת חיבור לשרת
            clientMain.openConnection();

            // יצירת מופע של BookController
            BookController bookController = new BookController(clientMain);

            System.out.println("Testing addBook...");
            String bookCode = "45651";
            String bookCode2 = "456";
            String author = "BarElrom212";
            String title = "chat2";
            String subject = "Programming3";
            String description = "abcd.4";
            bookController.addBook(bookCode, author, title, subject, description);
            System.out.println("Book added successfully: " + title);

            // 2. בדיקה: בדיקת זמינות ספר
            System.out.println("\nTesting checkBookAvailability...");
            String checkBookCode = "456";
            bookController.checkBookAvailability(checkBookCode);
            System.out.println("Check availability request sent for book code: " + checkBookCode);

            // 3. בדיקה: שליפת פרטי ספר
            System.out.println("\nTesting getBookDetails...");
            String detailsBookCode = "456";
            bookController.getBookDetails(detailsBookCode);
            
            System.out.println("Get details request sent for book code: " + detailsBookCode);

            // 4. בדיקה: מחיקת ספר (אופציונלי)
            System.out.println("\nTesting deleteBook...");
            bookController.deleteBook(bookCode2);
            System.out.println("Delete request sent for book code: " + bookCode);
           
     

            // סיום החיבור
            System.out.println("\nClosing connection...");
            clientMain.closeConnection();
            System.out.println("Connection closed.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
	}

}
