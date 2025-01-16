package gui;

import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;

import client.ClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;

////the class is the controller for the SubscriberDetails.fxml and make the connection between the logic and the UI
public class SubscriberDetailsController {
	@FXML
	private Label idLabel;

	@FXML
	private Label statusLabel;

	@FXML
	private TextField nameField;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField passwordField;

	@FXML
	private Button closeButton;

	private Stage stage;
	private ClientMain client;
	private Subscriber sub;
	private String subscriberId;

	// initialize stage to further operations
	public void setStage(Stage stage, ClientMain client) {
		this.stage = stage;
		this.client = client;
	}

	public void setSubscriber(Subscriber s) {
		sub = s;
		setDetails();
	}

	// the method uses the data recived by the server in order to present it in the
	// GUI so the user can use the data
	public void setDetails() {
		// changed to support the Subscriber Class
		// String[] parts = details.split(",");
		idLabel.setText(Integer.toString(sub.getId()));
		statusLabel.setText(sub.getStatus());
		nameField.setText(sub.getName());
		phoneField.setText(sub.getPhone());
		emailField.setText(sub.getEmail());
		passwordField.setText(sub.getPassword());

	}

	// the method recive the updated TextField after the client press the edit
	// button
	// and send message to the client using key word "UPDATE" and the details that
	// need update
	@FXML
	public void editSubscriberDetails() {
		String newName = nameField.getText();
		String newPhone = phoneField.getText();
		String newEmail = emailField.getText();

		// send to the client
		if (subscriberId != null) {
			String updateMessage = "UPDATE," + subscriberId + "," + newName + "," + newEmail + "," + newPhone;
			client.sendMessageToServer(updateMessage);
		}

		// closing stage
		if (stage != null) {
			stage.close();
		}
	}

	@FXML
	public void onCloseClick() {
		ScreenLoader.closeWindow(closeButton);
	}
}
