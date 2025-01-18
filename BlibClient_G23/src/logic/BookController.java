package logic;

import client.ClientMain;

public class BookController {
    private final ClientMain client;

    public BookController(ClientMain client) {
        this.client = client;
    }

    private synchronized void sendSynchronizedMessage(String message) {
        client.sendMessageToServer(message);
    }

    public void addBook(String bookCode, String author, String title, String subject, String description) throws InterruptedException {
        String msg = "ADD_BOOK," + bookCode + "," + author + "," + title + "," + subject + "," + description;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }

    public void deleteBook(String bookCode) throws InterruptedException {
        String msg = "DELETE_BOOK," + bookCode;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }

    public void checkBookAvailability(String bookCode) throws InterruptedException {
        String msg = "CHECK_BOOK_AVAILABILITY," + bookCode;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }

    public void getBookDetails(String bookCode) throws InterruptedException {
        String msg = "GET_BOOK_DETAILS," + bookCode;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }

    public void editBook(String bookCode, String newAuthor, String newTitle, String newSubject, String newDescription) throws InterruptedException {
        String msg = "EDIT_BOOK," + bookCode + "," + newAuthor + "," + newTitle + "," + newSubject + "," + newDescription;
        sendSynchronizedMessage(msg);
        Thread.sleep(1000);
    }
}
