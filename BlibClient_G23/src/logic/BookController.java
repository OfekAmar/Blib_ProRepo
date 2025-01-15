package logic;

import client.ClientMain;

public class BookController {
    private ClientMain client;

    public BookController(ClientMain client) {
        this.client = client;
    }

    public void addBook(String bookCode, String author, String title, String subject, String description) {
        String msg = "ADD_BOOK," + bookCode + "," + author + "," + title + "," + subject + "," + description;
        client.sendMessageToServer(msg);
    }

    public void deleteBook(String bookCode) {
        if (client != null && client.isConnected()) { // Check if client is connected
            String message = "DELETE_BOOK," + bookCode;
            System.out.println("Sending delete request for book code: " + bookCode);
            client.sendMessageToServer(message);
        } else {
            System.err.println("Client is not connected. Cannot delete book.");
        }
    }

    public void editBook(String bookCode, String newAuthor, String newTitle, String newSubject, String newDescription) {
        String message = "EDIT_BOOK," + bookCode + "," + newAuthor + "," + newTitle + "," + newSubject + "," + newDescription;
        client.sendMessageToServer(message);
    }

    public void checkBookAvailability(String bookCode) {
        String message = "CHECK_BOOK_AVAILABILITY," + bookCode;
        client.sendMessageToServer(message);
    }

    public void getBookDetails(String bookCode) {
        String message = "GET_BOOK_DETAILS," + bookCode;
        client.sendMessageToServer(message);
    }
}
