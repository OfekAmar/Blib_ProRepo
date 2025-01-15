package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onSearchSubscriberClick(ActionEvent event) {
		String subscriberName = searchField.getText();
		if (subscriberName.isEmpty()) {
			showAlert("Error", "Please enter a subscriber name to search.");
		} else {
			// Implement search logic here
			subscriberDetailsField.setText("Details of subscriber: " + subscriberName);
			searchedsubs = new Subscriber(0, "name example", "phone example", "active", "email example", "password");
		}
	}

	@FXML
	private void onDeleteSubscriberClick(ActionEvent event) {
		// Implement delete logic here
		showAlert("Delete Subscriber", "Subscriber deleted successfully.");
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
			showAlert("Error", "No such subscriber exist or you didnt searched for one ! ");
		}
	}

	@FXML
	private void onAddSubscriberClick(ActionEvent event) {
		// Implement add subscriber logic here
		showAlert("Add Subscriber", "Adding a new subscriber.");
	}

	@FXML
	private void onScanCardClick(ActionEvent event) {
		// Implement scan card logic here
		showAlert("Scan Card", "Scanning subscriber card.");
	}

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
			}
		});
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
