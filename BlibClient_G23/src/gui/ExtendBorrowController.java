package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
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
	public void setClient(ClientMain cm) {
		this.cm = cm;
	}

	@FXML
    public void initialize() {
        // Fetch active borrowed books from the server for the current subscriber
        fetchActiveBorrowedBooks();
    }

    private void fetchActiveBorrowedBooks() {
        if (sub == null || cm == null) {
            ScreenLoader.showAlert("Error", "Subscriber or client not initialized.");
            return;
        }
        

        try {
            // Send request to the server to fetch active borrowed books
            String msg = "GET_ACTIVE_BORROWED," + sub.getId();
            cm.setMessageHandler((response) -> {
                if (response instanceof List) {
                    borrowedBooks = (List<String>) response; // Assuming the server returns a list of strings
                    booksListView.getItems().clear();
                    booksListView.getItems().addAll(borrowedBooks);
                } else {
                    ScreenLoader.showAlert("Error", "Failed to fetch active borrowed books. Invalid server response.");
                }
            });

            cm.sendMessageToServer(msg);
        } catch (Exception e) {
            ScreenLoader.showAlert("Error", "Failed to fetch active borrowed books: " + e.getMessage());
        }
    }

    @FXML
    private void onRequestExtendClick(ActionEvent event) {
        String selectedBook = booksListView.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedBook == null || selectedBook.isEmpty()) {
            ScreenLoader.showAlert("Error", "Please select a book to extend.");
            return;
        }

        if (selectedDate == null) {
            ScreenLoader.showAlert("Error", "Please select a due date.");
            return;
        }
        // Validate that the selected date is not before the current date
        if (selectedDate.isBefore(currentDate)) {
            ScreenLoader.showAlert("Error", "The selected due date cannot be in the past.");
            return;
        }

        // Validate that the selected date is not more than 7 days from the current date
        if (selectedDate.isAfter(currentDate.plusDays(7))) {
            ScreenLoader.showAlert("Error", "The selected due date cannot be more than 7 days from today.");
            return;
        }


        try {
            // Send the request to extend the borrow period to the server
            String msg = "EXTEND_BORROW," + selectedBook + "," + selectedDate.toString();
            cm.setMessageHandler((response) -> {
                if (response instanceof String) {
                    String serverResponse = (String) response;
                    if (serverResponse.startsWith("SUCCESS")) {
                        ScreenLoader.showAlert("Success",
                                "The borrow period for '" + selectedBook + "' has been extended to " + selectedDate + ".");
                    } else {
                        ScreenLoader.showAlert("Error", serverResponse);
                    }
                } else {
                    ScreenLoader.showAlert("Error", "Failed to process the request. Invalid server response.");
                }
            });

            cm.sendMessageToServer(msg);
        } catch (Exception e) {
            ScreenLoader.showAlert("Error", "Failed to send the request: " + e.getMessage());
        }
    }

   
	@FXML
	private void onCloseClick(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}
}
