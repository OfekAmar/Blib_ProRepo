package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ScreenLoader;

public class ReportsController {

	@FXML
	private Button borrowingDurationButton;

	@FXML
	private Button subscribersStatusButton;

	@FXML
	private Button backButton;

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onBorrowingDurationClick(ActionEvent event) {
		// Implement logic for borrowing duration reports
		ScreenLoader.showAlert("Borrowing Duration Reports", "Displaying Borrowing Duration Reports.");
	}

	@FXML
	private void onSubscribersStatusClick(ActionEvent event) {
		// Implement logic for subscribers status reports
		ScreenLoader.showAlert("Subscribers Status Reports", "Displaying Subscribers Status Reports.");
	}

	@FXML
	private void onBackToMainClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
			}
		});
	}
}
