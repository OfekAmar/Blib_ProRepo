package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import logic.ScreenLoader;

public class LibrarianMainController {
	@FXML
	private Label welcomeLabel;

	@FXML
	private Button manageUsersButton;

	@FXML
	private Button borrowBookButton;

	@FXML
	private Button returnBookButton;

	@FXML
	private Button reportsButton;

	@FXML
	private Button logoutButton;

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onManageSubscriberClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/ManagmentSubscriberScreen.fxml", "Subscriber Managemnt", event, controller -> {
			if (controller instanceof ManagmentSubscriberController) {
				((ManagmentSubscriberController) controller).setStage(stage);
			}
		});
	}

	@FXML
	private void onBorrowBookClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/BorrowBookScreen.fxml", "Subscriber Managemnt", event, controller -> {
			if (controller instanceof BorrowBookController) {
				((BorrowBookController) controller).setStage(stage);
			}
		});

	}

	@FXML
	private void onReturnBookClick(ActionEvent event) {
		ScreenLoader.showAlert("Return Book", "You clicked on 'Return Book'.");
	}

	@FXML
	private void onReportsClick(ActionEvent event) {
		ScreenLoader.showAlert("Reports", "You clicked on 'Reports'.");
	}

	@FXML
	private void onLogoutClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(stage);
			}
		});
	}

}
