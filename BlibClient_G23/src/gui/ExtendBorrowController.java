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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	@FXML
	public void initialize() {
		// Example: Replace this with actual database fetching logic
		borrowedBooks = List.of("Book 1", "Book 2", "Book 3");
		booksListView.getItems().addAll(borrowedBooks);
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

		// Example: Replace this with actual logic to request an extension in the
		// database
		ScreenLoader.showAlert("Success",
				"The borrow period for '" + selectedBook + "' has been extended to " + selectedDate + ".");
	}

	@FXML
	private void onCloseClick(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}
}
