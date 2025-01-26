
package gui;

import java.lang.ModuleLayer.Controller;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
import logic.Librarian;
import logic.LoginLogic;
import logic.ScreenLoader;
import logic.Subscriber;

/**
 * The `LoginController` class is responsible for handling user login
 * interactions. It processes login attempts for both subscribers and librarians
 * and navigates to the respective main screens upon successful login. The
 * controller also provides a guest login option for searching books.
 */
public class LoginController {

	@FXML
	private TextField userNameField;

	@FXML
	private PasswordField passwordField;

	private Stage stage;
	private ClientMain c;
	private Object response;
	private LoginLogic lg;

	/**
	 * Sets the current stage (window) of the application.
	 *
	 * @param stage the current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the client instance for the controller and initializes the login logic
	 * handler.
	 *
	 * @param c the {@link ClientMain} instance used for server communication
	 */
	public void setClient(ClientMain c) {
		this.c = c;
		lg = new LoginLogic(c);
	}

	/**
	 * Handles the login button click event. Validates the input fields and attempts
	 * to log in the user. Navigates to the appropriate screen based on the user
	 * type (subscriber or librarian).
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the login button
	 */
	@FXML
	private void onLoginClick(ActionEvent event) {
		String userName = userNameField.getText();
		String password = passwordField.getText();

		if (userName.isEmpty() || password.isEmpty()) {
			ScreenLoader.showAlert("ERROR", "all fields are required");
			return;
		}

		try {
			Object response = lg.login(userName, password);
			if (response instanceof Subscriber) {
				Subscriber sub = (Subscriber) response;
				ScreenLoader.openScreenWithSize("/gui/SubscriberMainScreen.fxml", "Subscriber Screen", event,
						controller -> {
							if (controller instanceof SubscriberMainController) {
								((SubscriberMainController) controller).setSubscriber(sub);
								((SubscriberMainController) controller).setClient(c);
							}
						}, 400, 250);

			} else if (response instanceof Librarian) {
				Librarian lib = (Librarian) response;
				ScreenLoader.openScreenWithSize("/gui/LibrarianMainScreen.fxml", "Librarian Screen", event,
						controller -> {
							if (controller instanceof LibrarianMainController) {
								((LibrarianMainController) controller).setStage(new Stage());
								((LibrarianMainController) controller).setLibrarian(lib);
								((LibrarianMainController) controller).setClient(c);

							}
						}, 250, 500);

			} else if (response instanceof String) {
				ScreenLoader.showAlert("Error: ", (String) response);
			}
		} catch (Exception e) {
			System.out.println("Error during login: " + e.getMessage());
			ScreenLoader.showAlert("Error", "An error occurred during the login. Please try again later.");
		}

	}

	/**
	 * Handles the guest login button click event. Navigates to the search book
	 * screen for guest users.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the guest login
	 *              button
	 */
	@FXML
	private void onGuestLoginClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/SearchBookScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof SearchBookController) {
				((SearchBookController) controller).setStage(new Stage());
				((SearchBookController) controller).setClient(c);

			}
		});
	}

	/**
	 * Handles the exit button click event. Closes the application.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the exit button
	 */
	@FXML
	private void handleExit(ActionEvent event) {
		System.exit(0);

	}

}
