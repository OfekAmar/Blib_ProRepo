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
	private Button cancelButton;

	@FXML
	private Button saveButton;

	private Stage stage;
	private ClientMain c;
	private Subscriber sub;
	private String subscriberId;
	private SubscriberController sc;
	
	private Consumer<Subscriber> onEditSubscriberCallback;


	// initialize stage to further operations
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	public void setSubscriber(Subscriber s) {
		sub = s;
		subscriberId = String.valueOf(sub.getId());
		setDetails();
	}
	
	public void setOnEditSubscriberCallback(Consumer<Subscriber> callback) {
		this.onEditSubscriberCallback=callback;
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
	public void onSaveClick() {
		String phone=phoneField.getText();
		String email=emailField.getText();
		
		if(phone.isEmpty() || email.isEmpty()) {
			ScreenLoader.showAlert("Error", "All fields are required");
			return;
		}else {
			ScreenLoader.closeWindow(cancelButton);
		}
		
		
		SubscriberController sc=new SubscriberController(c);
		try {
			String result=sc.editSubscriber(String.valueOf(sub.getId()), phone, email, sub.getPassword());
			ScreenLoader.showAlert("subscriber edited",result);
			
			Subscriber editedSub=new Subscriber(sub.getId(),sub.getName(),sub.getPhone(),sub.getStatus(),sub.getEmail(),sub.getPassword(),sub.getUserName());
			if(onEditSubscriberCallback!=null) {
				onEditSubscriberCallback.accept(editedSub);
			}
			Stage currentStage = (Stage) saveButton.getScene().getWindow();
	        currentStage.close();
		}catch(InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to edit subscriber.");
		}
	}

	@FXML
	private void onCancelClick() {
		ScreenLoader.closeWindow(cancelButton);
	}
}
