package gui;

import client.ClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

////the class is the controller for the SubscriberDetails.fxml and make the connection between the logic and the UI
public class SubscriberDetailsController {
	@FXML
	private Label idLabel;

	@FXML
	private TextField nameField;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	private Stage stage;
	private String subscriberId;
	private ClientMain client;

	// initialize stage to further operations
	public void setStage(Stage stage, ClientMain client) {
		this.stage = stage;
		this.client = client;
	}

	// the method uses the data recived by the server in order to present it in the
	// GUI so the user can use the data
	public void setDetails(String details) {
		// string split by , into TextFields
		String[] parts = details.split(",");
		if (parts.length >= 4) {
			subscriberId = parts[0];
			idLabel.setText(parts[0]);
			nameField.setText(parts[1]);
			phoneField.setText(parts[2]);
			emailField.setText(parts[3]);
		}

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
}
