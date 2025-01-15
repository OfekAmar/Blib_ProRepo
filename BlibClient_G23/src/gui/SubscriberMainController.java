package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;

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
	private Subscriber sub = new Subscriber(0, "name example", "phone example", "active", "email example", "password");

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
		welcomeLabel.setText("Hello, " + sub.getName() + "!");
	}

	@FXML
	private void onOrderBookClick(ActionEvent event) {
		ScreenLoader.showAlert("Order Book", "You clicked on 'Order Book'.");
	}

	@FXML
	private void onSearchBookClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/SearchBookScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof SearchController) {
				((SearchController) controller).setStage(new Stage());
			}
		});
	}

	@FXML
	private void onExtendBorrowClick(ActionEvent event) {
		ScreenLoader.showAlert("Extend Loan", "You clicked on 'Extend Loan'.");
	}

	@FXML
	private void onViewProfileClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/SubscriberDetails.fxml", "Subcriber Details", event, controller -> {
			if (controller instanceof SubscriberDetailsController) {
				((SubscriberDetailsController) controller).setStage(new Stage(), null);
				// !!!! suppose to be setStage(new Stage(), ClientMain Client) !!!!!!! need to
				// adapt later on
				((SubscriberDetailsController) controller).setSubscriber(sub);
			}
		});
	}

	@FXML
	private void onHistoryClick(ActionEvent event) {
		ScreenLoader.showAlert("View History", "You clicked on 'View History'.");
	}

	@FXML
	private void handleExit() {
		ScreenLoader.closeWindow(exitButton);
	}
}
