package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

public class SubscriberMainController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private Button orderBookButton;

	@FXML
	private Button searchBookButton;

	@FXML
	private Button extendBorrowButton;

	@FXML
	private Button myProfileButton;

	@FXML
	private Button viewHistoryButton;

	@FXML
	private Button exitButton;

	private String subscriberName;
	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
		welcomeLabel.setText("Hello, " + subscriberName + "!");
	}

	@FXML
	private void onOrderBookClick(ActionEvent event) {
		showAlert("Order Book", "You clicked on 'Order Book'.");
	}

	@FXML
	private void onSearchBookClick(ActionEvent event) {
		showAlert("Search Book", "You clicked on 'Search Book'.");
	}

	@FXML
	private void onExtendBorrowClick(ActionEvent event) {
		showAlert("Extend Loan", "You clicked on 'Extend Loan'.");
	}

	@FXML
	private void onViewProfileClick(ActionEvent event) {
		showAlert("My Profile", "You clicked on 'My Profile'.");
	}

	@FXML
	private void onHistoryClick(ActionEvent event) {
		showAlert("View History", "You clicked on 'View History'.");
	}

	@FXML
	private void handleExit(ActionEvent event) {
		if (stage != null) {
			stage.close();
			System.exit(0);
		} else {
			System.err.println("Stage is not initialized.");
			System.exit(0);
		}
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
