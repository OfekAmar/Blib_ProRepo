package gui;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import logic.ScreenLoader;

public class SearchController {

	@FXML
	private ComboBox<String> searchByComboBox;

	@FXML
	private TextField searchField;

	@FXML
	private Button searchButton;

	@FXML
	private Button backButton;

	private Stage stage;
	private boolean loggedIn;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	public void initialize() {
		// Initialize the ComboBox with search options
		searchByComboBox.getItems().addAll("Title", "Subject", "Free Text");
		searchByComboBox.setValue("Title"); // Set default value
	}

	@FXML
	private void onSearchBookClick(ActionEvent event) {
		String searchBy = searchByComboBox.getValue();
		String searchTerm = searchField.getText();

		if (searchTerm == null || searchTerm.isEmpty()) {
			showAlert("Error", "Please enter a search term.");
		} else {
			// Implement your search logic here
			showAlert("Search", "Searching for: " + searchTerm + " by " + searchBy);
		}
	}

	@FXML
	private void handleBack(ActionEvent event) {

		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(new Stage());
			}
		});
		// this is pop - up alert in case needed !
		// showAlert("Info", "Going back to the previous screen.");
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
