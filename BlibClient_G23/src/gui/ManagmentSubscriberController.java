package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import client.ClientMain;
import javafx.event.ActionEvent;
import logic.ScreenLoader;
import logic.Subscriber;

public class ManagmentSubscriberController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private TextField searchField;

	@FXML
	private TextField subscriberDetailsField;

	@FXML
	private Button searchButton;

	@FXML
	private Button deleteSubscriberButton;

	@FXML
	private Button viewProfileButton;

	@FXML
	private Button addSubscriberButton;

	@FXML
	private Button scanCardButton;

	@FXML
	private Button exitButton;

	private Stage stage;
	private Subscriber searchedsubs = null;
	private ClientMain c;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	@FXML
	private void onSearchSubscriberClick(ActionEvent event) {
		String subscriberName = searchField.getText();
		if (subscriberName.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name to search.");
		} else {
			// Implement search logic here
			subscriberDetailsField.setText("Details of subscriber: " + subscriberName);
			searchedsubs = new Subscriber(0, "name example", "phone example", "active", "email example", "password");
		}
	}

	@FXML
	private void onDeleteSubscriberClick(ActionEvent event) {
		// Implement delete logic here
		ScreenLoader.showAlert("Delete Subscriber", "Subscriber deleted successfully.");
		subscriberDetailsField.clear();
	}

	@FXML
	private void onViewProfileClick(ActionEvent event) {
		if (searchedsubs != null) {
			ScreenLoader.openPopUpScreen("/gui/SubscriberDetails.fxml", "Subcriber Details", event, controller -> {
				if (controller instanceof SubscriberDetailsController) {
					((SubscriberDetailsController) controller).setStage(new Stage(), null);
					// !!!! suppose to be setStage(new Stage(), ClientMain Client) !!!!!!! need to
					// adapt later on
					((SubscriberDetailsController) controller).setSubscriber(searchedsubs);
				}
			});
		} else {
			ScreenLoader.showAlert("Error", "No such subscriber exist or you didnt searched for one ! ");
		}
	}

	@FXML
	private void onAddSubscriberClick(ActionEvent event) {
		// Implement add subscriber logic here
		ScreenLoader.openPopUpScreen("/gui/NewSubscriberScreen.fxml", "Add New Subscriber", event, controller -> {
			if (controller instanceof NewSubscriberController) {
				((NewSubscriberController) controller).setStage(new Stage());
				((NewSubscriberController) controller).setClient(c);
			}
		});
	}

	@FXML
	private void onScanCardClick(ActionEvent event) {
		// Implement scan card logic here
		ScreenLoader.showAlert("Scan Card", "Scanning subscriber card.");
	}

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
			}
		});
	}

}
