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

/**
 * The `ExtendBorrowController` class manages the user interface for extending
 * the borrow period of books borrowed by a subscriber. It interacts with the
 * server to fetch borrowed books and requests an extension for the selected
 * book.
 */
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
	private ClientMain cm;

	/**
	 * Sets the stage for the controller.
	 *
	 * @param stage the current stage of the application
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the subscriber for the controller.
	 *
	 * @param s the current subscriber of the application
	 */
	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	/**
	 * Sets the client and subscriber for the controller. This also triggers
	 * fetching the borrowed books for the subscriber.
	 *
	 * @param cm the client instance
	 * @param s  the subscriber whose borrowed books are being managed
	 */
	public void setClient(ClientMain cm, Subscriber s) {
		this.cm = cm;
		this.sub = s;
		fetchBorrowedBooksWithDetails();
	}

	/**
	 * Initializes the controller. This method is called automatically after the
	 * FXML file is loaded.
	 */
	@FXML
	public void initialize() {
		// No initialization logic currently
	}

	/**
	 * Fetches the list of borrowed books with details for the current subscriber.
	 * The list is retrieved from the server and displayed in the list view.
	 */
	private void fetchBorrowedBooksWithDetails() {
		try {
			BorrowController borrowController = new BorrowController(null, cm);
			borrowedBooks = borrowController.showBorrowes(sub.getId());

			booksListView.getItems().clear();
			booksListView.getItems().addAll(borrowedBooks);

		} catch (Exception e) {
			Platform.runLater(() -> {
				ScreenLoader.showAlert("Error", "Failed to fetch borrowed book details: " + e.getMessage());
			});
		}
	}

	/**
	 * Handles the request to extend the borrow period for the selected book.
	 *
	 * @param event the {@link ActionEvent} triggered by the button click
	 */
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
			BorrowController borrowController = new BorrowController(null, cm);
			String response = borrowController.extendBorrow(String.valueOf(borrowId), String.valueOf(selectedDate));

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

	/**
	 * Extracts the borrow ID from the selected book string.
	 *
	 * @param selectedBook the selected book string in the format "Borrow ID: {id} |
	 *                     Title: ..."
	 * @return the borrow ID, or -1 if parsing fails
	 */
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

	/**
	 * Handles the close button click event to close the current window.
	 *
	 * @param event the {@link ActionEvent} triggered by the button click
	 */
	@FXML
	private void onCloseClick(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}
}
