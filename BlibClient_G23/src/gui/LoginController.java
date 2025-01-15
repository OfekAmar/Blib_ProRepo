package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ScreenLoader;

public class LoginController {

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	private Stage stage;
	private boolean isLibrarian = false;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onLoginClick(ActionEvent event) {
		if (isLibrarian) {
			ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Screen", event, null);
		} else {
			ScreenLoader.openScreen("/gui/SubscriberMainScreen.fxml", "Subscriber Screen", event, controller -> {
				if (controller instanceof SubscriberMainController) {
					((SubscriberMainController) controller).setSubscriberName(usernameField.getText());
				}
			});
		}
	}

	@FXML
	private void onGuestLoginClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/SearchBookScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof SearchController) {
				((SearchController) controller).setStage(new Stage());
			}
		});
	}

	@FXML
	private void handleExit(ActionEvent event) {
		System.exit(0);

	}

}
