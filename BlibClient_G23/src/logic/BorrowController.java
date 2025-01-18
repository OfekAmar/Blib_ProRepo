package logic;

import java.time.LocalDate;

import client.ClientMain;

public class BorrowController {
	private final BookController bookController;
    private final TimeController timeController;
    private final SubscriberController subscriberController;
    private final ClientMain client;

    public BorrowController(BookController bookController, TimeController timeController, SubscriberController subscriberController, ClientMain client) {
        this.bookController = bookController;
        this.timeController = timeController;
        this.subscriberController = subscriberController;
        this.client = client;
    }

    public synchronized void processBorrow(String bookId, String copyId, String subscriberId, LocalDate returnDate) throws InterruptedException {
        // Send the request to ServerMain
        String msg = "PROCESS_BORROW," + bookId + "," + copyId + "," + subscriberId + "," + returnDate;
        client.sendMessageToServer(msg);
        Thread.sleep(1000);
    }
    public synchronized void extendBorrow(String borrowId) throws InterruptedException {
        // Send the request to ServerMain
        String msg = "EXTEND_BORROW," + borrowId;
        client.sendMessageToServer(msg);
        Thread.sleep(1000);
    }
    

}
