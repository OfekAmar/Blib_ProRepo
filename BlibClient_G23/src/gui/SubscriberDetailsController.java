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
import logic.SubscriberController;

////the class is the controller for the SubscriberDetails.fxml and make the connection between the logic and the UI
public class SubscriberDetailsController {
	@FXML
	private Label idLabel;

	@FXML
	private Label statusLabel;

	@FXML
	private Label nameLabel;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField passwordField;

	@FXML
	private Button closeButton;

	@FXML
	private Button editButton;

	private Stage stage;
	private ClientMain client;
	private Subscriber sub;
	private String subscriberId;
	private SubscriberController sc;

	// initialize stage to further operations
	public void setStage(Stage stage, ClientMain client) {
		this.stage = stage;
		this.client = client;
		sc = new SubscriberController(client);
	}

	public void setSubscriber(Subscriber s) {
		sub = s;
		subscriberId = String.valueOf(sub.getId());
		setDetails();
	}

	// the method uses the data recived by the server in order to present it in the
	// GUI so the user can use the data
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

	// the method recive the updated TextField after the client press the edit
	// button
	// and send message to the client using key word "UPDATE" and the details that
	// need update
	@FXML
	public void onEditClick() {
		String newPhone = phoneField.getText();
		String newEmail = emailField.getText();
		String newPassword = passwordField.getText();

		// send to the client
		if (subscriberId != null) {
			try {
				String alert = sc.editSubscriber(subscriberId, newPhone, newEmail, newPassword);
				ScreenLoader.showAlert("Successs", alert);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
