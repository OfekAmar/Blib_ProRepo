package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import client.ClientMain;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.SubscriberController;

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
	private Button confirmButton;

	@FXML
	private Button cancelButton;

	private Stage stage;
	private Subscriber newSubs;

	private ClientMain c;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	@FXML
	private void onConfirmClick(ActionEvent event) {
		String name = nameField.getText();
		String phone = phoneField.getText();
		String email = emailField.getText();
		String password = passwordField.getText();

		if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
			ScreenLoader.showAlert("Error", "All fields are required.");
			return;
		} else {
			// Implement logic to create a new subscriber here
			ScreenLoader.closeWindow(cancelButton);
		}
		SubscriberController s = new SubscriberController(c);
		try {
			ScreenLoader.showAlert("user added", s.addSubscriber(name, phone, email, password));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}

}
