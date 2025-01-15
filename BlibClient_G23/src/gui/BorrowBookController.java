package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class BorrowBookController {

	@FXML
	private TextField subscriberField;

	@FXML
	private TextField bookField;

	@FXML
	private Button enterSubscriberButton;

	@FXML
	private Button enterBookButton;

	@FXML
	private Button scanSubscriberButton;

	@FXML
	private Button scanBookButton;

	@FXML
	private Button backButton;
	@FXML
	private DatePicker datePicker;

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onEnterSubscriber(ActionEvent event) {
		String subscriberInfo = subscriberField.getText();
		if (subscriberInfo.isEmpty()) {
			showAlert("Error", "Please enter a subscriber name or ID.");
		} else {
			// Implement logic to handle subscriber information
			showAlert("Success", "Subscriber information submitted: " + subscriberInfo);
		}
	}

	@FXML
	private void onEnterBook(ActionEvent event) {
		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			showAlert("Error", "Please enter a book name or code.");
		} else {
			// Implement logic to handle book information
			showAlert("Success", "Book information submitted: " + bookInfo);
		}
	}

	@FXML
	private void onScanSubscriber(ActionEvent event) {
		// Implement logic to scan subscriber card
		showAlert("Scan Subscriber", "Scanning subscriber card...");
	}

	@FXML
	private void onScanBook(ActionEvent event) {
		// Implement logic to scan book
		showAlert("Scan Book", "Scanning book...");
	}

	@FXML
	private void onBackToMain(ActionEvent event) {
		// Close the current stage
		Stage stage = (Stage) backButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onDateSelected(ActionEvent event) {
		LocalDate selectedDate = datePicker.getValue();
		if (selectedDate != null) {
			showAlert("Date Selected", "You selected: " + selectedDate.toString());
		} else {
			showAlert("Error", "Please select a valid date.");
		}
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}