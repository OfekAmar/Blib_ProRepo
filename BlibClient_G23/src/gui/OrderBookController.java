package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;

public class OrderBookController {

	@FXML
	private TextField bookIdField;

	@FXML
	private Button orderButton;

	@FXML
	private Button closeButton;

	private Stage stage;
	private Subscriber sub;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	@FXML
	private void onOrderBookClick(ActionEvent event) {
		String bookId = bookIdField.getText();

		if (bookId == null || bookId.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a valid Book ID.");
		} else {
			// Implement logic to order the book
			ScreenLoader.showAlert("Success", "Book with ID " + bookId + " has been ordered successfully.");
		}
	}

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}

}
