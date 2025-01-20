package logic;

import java.time.LocalDate;

import client.ClientMain;

public class DBtest {
	public static void main(String[] args) {
        try {
            ClientMain clientMain = new ClientMain("localhost", 5555);
            clientMain.openConnection();

            BookController bookController = new BookController(clientMain);
            TimeController timeController = new TimeController();
            SubscriberController subscriberController = new SubscriberController(clientMain);
            BorrowController borrowController = new BorrowController(bookController, timeController, subscriberController, clientMain);

           // Test BorrowController functionality
            System.out.println("\nTesting processBorrow...");
            borrowController.processBorrow("1", "2", "6", LocalDate.of(2025, 1, 25));
            
            // Test BorrowController functionality
            System.out.println("\nTesting processBorrow...");
            borrowController.processBorrow("1", "2", "7", LocalDate.of(2025, 1, 25));
            
            // Test BorrowController functionality
            System.out.println("\nTesting processBorrow...");
            borrowController.processBorrow("1", "1", "4", LocalDate.of(2025, 1, 25));
            
            // Test BorrowController functionality
            System.out.println("\nTesting processBorrow...");
            borrowController.processBorrow("2", "1", "6", LocalDate.of(2025, 1, 25));
            
         // Test BorrowController functionality
            System.out.println("\nTesting processBorrow...");
            borrowController.processBorrow("5", "1", "4", LocalDate.of(2025, 1, 25));
            
            // Test BorrowController functionality
            System.out.println("\nTesting processBorrow...");
            borrowController.processBorrow("4", "1", "4", LocalDate.of(2025, 1, 25));
           
            // Test extending a borrow
            System.out.println("\nTesting extendBorrow...");
            borrowController.extendBorrow("2");
            
            // Test extending a borrow
            System.out.println("\nTesting extendBorrow...");
            borrowController.extendBorrow("1");

            clientMain.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

            
