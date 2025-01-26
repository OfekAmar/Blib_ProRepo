package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

import client.ClientMain;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.Book;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.SubscriberController;

/**
 * The `NewSubscriberController` class handles the user interface and logic for
 * adding a new subscriber to the system. This controller provides fields for
 * entering subscriber details and functionality to save the subscriber to the
 * system.
 */
public class NewSubscriberController {

	@FXML
	private TextField nameField;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField userNameField;

	@FXML
	private Button confirmButton;

	@FXML
	private Button cancelButton;

	private Stage stage;
	private Subscriber newSubs;

	private ClientMain c;

	private Consumer<Subscriber> onSubscriberAddedCallback;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	public void setOnSubscriberAddedCallback(Consumer<Subscriber> callback) {
		this.onSubscriberAddedCallback = callback;
	}

	/**
	 * Handles the "Confirm" button click event. Validates the input fields, creates
	 * a new subscriber, and adds it to the system.
	 *
	 * @param event the event triggered by clicking the "Confirm" button
	 */
	@FXML
	private void onConfirmClick(ActionEvent event) {
		String name = nameField.getText();
		String phone = phoneField.getText();
		String email = emailField.getText();
		String password = passwordField.getText();
		String userName = userNameField.getText();

		// Validate that all fields are filled
		if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
			ScreenLoader.showAlert("Error", "All fields are required.");
			return;
		} else {
			ScreenLoader.closeWindow(cancelButton);
		}

		// Attempt to add the subscriber using the SubscriberController
		SubscriberController s = new SubscriberController(c);
		try {
			String result = s.addSubscriber(name, phone, email, password, userName);
			ScreenLoader.showAlert("subscriber added", result);

			// Create a new Subscriber object for the added subscriber
			Subscriber newSub = new Subscriber(0, name, phone, "active", email, password, userName);
			if (onSubscriberAddedCallback != null) {
				onSubscriberAddedCallback.accept(newSub);
			}
			Stage currentStage = (Stage) confirmButton.getScene().getWindow();
			currentStage.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to add subscriber.");
		}

	}

	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}

}
