package gui;

import javafx.fxml.FXML;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import client.ClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.Book;
import logic.BookController;
import logic.CopyOfBook;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.SubscriberController;

/**
 * The `SubscriberDetailsController` class manages the subscriber details screen
 * in the GUI. It allows the user to view and edit a subscriber's details,
 * including phone number, email, and status. This controller interacts with the
 * backend to fetch and update subscriber data.
 */
public class SubscriberDetailsController {
	@FXML
	private Label idLabel;

	@FXML
	private Label statusLabel;

	@FXML
	private Label nameLabel;

	@FXML
	private Label passwordLabel;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField passwordField;

	@FXML
	private Button cancelButton;

	@FXML
	private Button saveButton;

	private Stage stage;
	private ClientMain c;
	private Subscriber sub;
	private String subscriberId;
	private SubscriberController sc;
	SubscriberMainController smc;
	boolean passFlag = false;

	private Consumer<Subscriber> onEditSubscriberCallback;

	/**
	 * Initializes the stage for further operations.
	 *
	 * @param stage the current stage of the application.
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the client instance to communicate with the server.
	 *
	 * @param c the client instance of type {@link ClientMain}.
	 */
	public void setClient(ClientMain c) {
		this.c = c;
	}

	/**
	 * Sets the subscriber whose details are to be displayed and edited.
	 *
	 * @param s the {@link Subscriber} object whose details are to be displayed.
	 */
	public void setSubscriber(Subscriber s) {
		sub = s;
		subscriberId = String.valueOf(sub.getId());
		setDetails();
	}

	/**
	 * Sets the subscriber by ID and client, and fetches subscriber data from the
	 * server.
	 *
	 * @param id the ID of the subscriber to be fetched.
	 * @param c  the client instance of type {@link ClientMain}.
	 */
	public void setIDandClient(int id, ClientMain c) {
		this.c = c;
		sc = new SubscriberController(c);
		try {
			this.sub = sc.searchSubscriberById(String.valueOf(id));
			passFlag = true;
			passwordField.setVisible(true);
			passwordField.setManaged(true);
			passwordLabel.setVisible(true);
			passwordLabel.setManaged(true);
			setDetails();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the SubscriberMainController to update the main screen if needed.
	 *
	 * @param controller the {@link SubscriberMainController} to set.
	 */
	public void setSubscriberMainController(SubscriberMainController controller) {
		smc = controller;

	}

	/**
	 * Sets a callback function to handle changes to subscriber details.
	 *
	 * @param callback a {@link Consumer} that handles the edited subscriber.
	 */
	public void setOnEditSubscriberCallback(Consumer<Subscriber> callback) {
		this.onEditSubscriberCallback = callback;
	}

	/**
	 * Populates the fields with the details of the subscriber.
	 */
	public void setDetails() {
		// changed to support the Subscriber Class
		// String[] parts = details.split(",");
		idLabel.setText(Integer.toString(sub.getId()));
		statusLabel.setText(sub.getStatus());
		nameLabel.setText(sub.getName());
		phoneField.setText(sub.getPhone());
		emailField.setText(sub.getEmail());
		passwordField.setText(sub.getPassword());

	}

	/**
	 * Handles the save action. Updates the subscriber details and sends them to the
	 * server.
	 */
	@FXML
	public void onSaveClick() {
		String phone = phoneField.getText();
		String email = emailField.getText();
		String password;
		if (passFlag) {
			password = passwordField.getText();
		} else {
			password = sub.getPassword();
		}

		if (phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
			ScreenLoader.showAlert("Error", "All fields are required");
			return;
		} else {
			ScreenLoader.closeWindow(cancelButton);
		}

		SubscriberController sc = new SubscriberController(c);
		try {
			String result = sc.editSubscriber(String.valueOf(sub.getId()), phone, email, password);
			if (passFlag) {
				ScreenLoader.showAlert("subscriber edited", result);
			} else {
				ScreenLoader.showAlert("Subscriber edited", "Phone: " + phone + "\nEmail: " + email);
			}
			Subscriber editedSub = new Subscriber(sub.getId(), sub.getName(), sub.getPhone(), sub.getStatus(),
					sub.getEmail(), password, sub.getUserName());
			if (onEditSubscriberCallback != null) {
				onEditSubscriberCallback.accept(editedSub);
			}
			Stage currentStage = (Stage) saveButton.getScene().getWindow();
			currentStage.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to edit subscriber.");
		}
	}

	/**
	 * Handles the cancel action when the user clicks the cancel button. Closes the
	 * current window without saving changes.
	 */
	@FXML
	private void onCancelClick() {
		ScreenLoader.closeWindow(cancelButton);
	}

}
