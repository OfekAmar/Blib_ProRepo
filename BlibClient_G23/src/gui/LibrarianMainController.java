package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
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
		ScreenLoader.openScreen("/gui/ManagmentSubscriberScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof ManagmentSubscriberController) {
				((ManagmentSubscriberController) controller).setStage(new Stage());
			}
		});
	}

	@FXML
	private void onBorrowBookClick(ActionEvent event) {
		showAlert("Borrow Book", "You clicked on 'Borrow Book'.");
	}

	@FXML
	private void onReturnBookClick(ActionEvent event) {
		showAlert("Return Book", "You clicked on 'Return Book'.");
	}

	@FXML
	private void onReportsClick(ActionEvent event) {
		showAlert("Reports", "You clicked on 'Reports'.");
	}

	@FXML
	private void onLogoutClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(new Stage());
			}
		});
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
