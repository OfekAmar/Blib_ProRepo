package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.BorrowController;
import logic.ScreenLoader;
import logic.Subscriber;

import java.time.LocalDate;
import java.util.List;

import client.ClientMain;

public class ExtendBorrowController {

	@FXML
	private ListView<String> booksListView;

	@FXML
	private DatePicker datePicker;

	@FXML
	private Button extendButton;

	@FXML
	private Button closeButton;

	private List<String> borrowedBooks; // This should be fetched from the database
	private Stage stage;
	private Subscriber sub;
	LocalDate currentDate = LocalDate.now();

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	private ClientMain cm;
	public void setClient(ClientMain cm,Subscriber s) {
		this.cm = cm;
		this.sub = s;
		fetchBorrowedBooksWithDetails();
	}
	@FXML
	public void initialize() {
	    // Fetch active borrowed books with details for the current subscriber
		
	
	}

	private void fetchBorrowedBooksWithDetails() {
	    try {
	        BorrowController borrowController = new BorrowController(null,cm);
	        borrowedBooks = borrowController.showBorrowes(sub.getId());

	        
	        booksListView.getItems().clear();
	        booksListView.getItems().addAll(borrowedBooks);
	       
	    } catch (Exception e) {
	        Platform.runLater(() -> {
	            ScreenLoader.showAlert("Error", "Failed to fetch borrowed book details: " + e.getMessage());
	        });
	    }
	}

	@FXML
	private void onRequestExtendClick(ActionEvent event) {
	    String selectedBook = booksListView.getSelectionModel().getSelectedItem();
	    LocalDate selectedDate = datePicker.getValue();

	    // Ensure a book is selected
	    if (selectedBook == null || selectedBook.isEmpty()) {
	        ScreenLoader.showAlert("Error", "Please select a book to extend.");
	        return;
	    }

	    // Ensure a date is selected
	    if (selectedDate == null) {
	        ScreenLoader.showAlert("Error", "Please select a due date.");
	        return;
	    }

	    // Validate that the selected date is not before the current date
	    if (selectedDate.isBefore(currentDate)) {
	        ScreenLoader.showAlert("Error", "The selected due date cannot be in the past.");
	        return;
	    }

	    try {
	        // Extract borrow ID from the selected book string
	        // Assuming `selectedBook` has a format like "Borrow ID: 123 | Title: ..."
	        int borrowId = extractBorrowIdFromSelectedBook(selectedBook);
	        if (borrowId == -1) {
	            ScreenLoader.showAlert("Error", "Invalid selected book format.");
	            return;
	        }

	        // Use BorrowController to send the request to extend the borrow period
	        BorrowController borrowController = new BorrowController(null,cm);
	        String response = borrowController.extendBorrow(String.valueOf(borrowId),String.valueOf(selectedDate));

	        // Handle the server response
	        
	         if (response.startsWith("SUCCESS")) {
	                ScreenLoader.showAlert("Success", "The borrow period for the selected book has been extended.");
	            } else {
	                ScreenLoader.showAlert("Error", response);
	            }
	        
	    } catch (Exception e) {
	        Platform.runLater(() -> {
	            ScreenLoader.showAlert("Error", "Failed to extend the borrow period: " + e.getMessage());
	        });
	    }
	}
	
	
	private int extractBorrowIdFromSelectedBook(String selectedBook) {
	    try {
	        String[] parts = selectedBook.split("\\|");
	        if (parts.length > 0 && parts[0].startsWith("Borrow ID:")) {
	            return Integer.parseInt(parts[0].replace("Borrow ID:", "").trim());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return -1; // Return -1 if parsing fails
	}
	@FXML
	private void onCloseClick(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}
}
