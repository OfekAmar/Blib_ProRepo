package logic;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientMain;
import ocsf.client.*;

public class BookController {
	private ClientMain client;

	public BookController(ClientMain client) {
		this.client = client;
	}
	
	public void addBook(String bookCode,String author,String title,String subject,String description) {
		
		String msg = "ADD_BOOK,"+ bookCode + "," + author + "," + title + "," + subject + "," +  description;
		client.sendMessageToServer(msg);
		
	}
	public void deleteBook(String bookCode) {
        String message = "DELETE_BOOK," + bookCode;
        client.sendMessageToServer(message);
    }

    // עריכת ספר קיים על פי book_code
    public void editBook(String bookCode, String newAuthor, String newTitle, String newSubject, String newDescription) {
        String message = "EDIT_BOOK," + bookCode + "," + newAuthor + "," + newTitle + "," + newSubject + "," + newDescription;
        client.sendMessageToServer(message);
    }

    // בדיקת זמינות ספר במסד הנתונים על פי book_code
    public void checkBookAvailability(String bookCode) {
        String message = "CHECK_BOOK_AVAILABILITY," + bookCode;
        client.sendMessageToServer(message);
    }

    // שליפת פרטים של ספר על פי book_code
    public void getBookDetails(String bookCode) {
        String message = "GET_BOOK_DETAILS," + bookCode;
        client.sendMessageToServer(message);
    }


	
	

}
