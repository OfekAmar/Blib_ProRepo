package gui;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.Book;
import logic.ScreenLoader;

public class ReturnBookController {
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

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onEnterSubscriber(ActionEvent event) {
		String subscriberInfo = subscriberField.getText();
		if (subscriberInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name or ID.");
		} else {
			// Implement logic to handle subscriber information
			ScreenLoader.showAlert("Success", "Subscriber information submitted: " + subscriberInfo);
		}
	}

	@FXML
	private void onEnterBook(ActionEvent event) {
		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a book name or code.");
		} else {
			// Implement logic to handle book information
			ScreenLoader.showAlert("Success", "Book information submitted: " + bookInfo);
		}
	}

	@FXML
	private void onScanSubscriber(ActionEvent event) {
		// Implement logic to scan subscriber card
		ScreenLoader.showAlert("Scan Subscriber", "Scanning subscriber card...");
	}

	@FXML
	private void onScanBook(ActionEvent event) {
		// Implement logic to scan book
		ScreenLoader.showAlert("Scan Book", "Scanning book...");
	}

	@FXML
	private void onBackToMain(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
			}
		});
	}

}
