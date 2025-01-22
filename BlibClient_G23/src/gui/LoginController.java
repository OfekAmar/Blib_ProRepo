
package gui;

import client.ClientMain;
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
	private ClientMain c;
	private boolean isLibrarian = true;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	@FXML
	private void onLoginClick(ActionEvent event) {
		if (isLibrarian) {
			ScreenLoader.openScreenWithSize("/gui/LibrarianMainScreen.fxml", "Librarian Screen", event, controller -> {
				if (controller instanceof LibrarianMainController) {
					((LibrarianMainController) controller).setStage(new Stage());
					((LibrarianMainController) controller).setClient(c);
				}
			}, 250, 400);
		} else {
			ScreenLoader.openScreenWithSize("/gui/SubscriberMainScreen.fxml", "Subscriber Screen", event,
					controller -> {
						if (controller instanceof SubscriberMainController) {
							((SubscriberMainController) controller).setSubscriberName(usernameField.getText());
							((SubscriberMainController) controller).setClient(c);
						}
					}, 400, 250);
		}
	}

	@FXML
	private void onGuestLoginClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/SearchBookScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof SearchBookController) {
				((SearchBookController) controller).setStage(new Stage());
				((SearchBookController) controller).setClient(c);

			}
		});
	}

	@FXML
	private void handleExit(ActionEvent event) {
		System.exit(0);

	}

}
