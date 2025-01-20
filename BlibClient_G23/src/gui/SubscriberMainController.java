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
	private Button readerCardButton;

	@FXML
	private Button viewHistoryButton;

	@FXML
	private Button exitButton;
	@FXML
	private Button logoutButton;

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

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	@FXML
	private void onOrderBookClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/OrderBookScreen.fxml", "Book Order", event, controller -> {
			if (controller instanceof OrderBookController) {
				((OrderBookController) controller).setStage(stage);
				((OrderBookController) controller).setSubscriber(sub);
			}
		});
	}

	@FXML
	private void onSearchBookClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/SearchBookScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof SearchBookController) {
				((SearchBookController) controller).setStage(stage);
				((SearchBookController) controller).setLoggedIn(true);
			}
		});
	}

	@FXML
	private void onReaderCardClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/ReaderCardScreen.fxml", "Reader Card", event, controller -> {
			if (controller instanceof ReaderCardController) {
				((ReaderCardController) controller).setStage(stage);
				((ReaderCardController) controller).setSubscriber(sub);
			}
		});
	}

	@FXML
	private void onExtendBorrowClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/ExtendBorrowScreen.fxml", "Extend Borrow", event, controller -> {
			if (controller instanceof ExtendBorrowController) {
				((ExtendBorrowController) controller).setStage(stage);
				((ExtendBorrowController) controller).setSubscriber(sub);
			}
		});
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
		ScreenLoader.openPopUpScreen("/gui/HistoryScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof HistoryController) {
				((HistoryController) controller).setStage(stage);
				((HistoryController) controller).setSubscriber(sub);
			}
		});
	}

	@FXML
	private void handleLogout(ActionEvent event) {
		this.sub = null;
		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(stage);
			}
		});
	}

	@FXML
	private void handleExit() {
		ScreenLoader.closeWindow(exitButton);
	}
}
