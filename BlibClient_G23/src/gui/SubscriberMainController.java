package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.LoginLogic;
import logic.NotificationsController;
import logic.ScreenLoader;
import logic.Subscriber;

import java.util.Map;

import client.ClientMain;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

/**
 * The `SubscriberMainController` class manages the main screen for subscribers
 * in the GUI. It provides options for subscribers to perform various actions
 * such as ordering books, searching for books, extending borrows, viewing
 * notifications, and managing their profiles.
 */
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
	private Button borrowHistoryButton;

	@FXML
	private Button viewHistoryButton;

	@FXML
	private Button readerCard;

	@FXML
	private Button exitButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Button notiButton;

	@FXML
	private Label notificationBubble;

	private Stage stage;
	private Subscriber sub;
	private Map<String, Integer> notifications;
	private NotificationsController nc;
	private ClientMain cm;
	private int notifilabel;

	/**
	 * Sets the stage for the current screen.
	 * 
	 * @param stage the current {@link Stage}.
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the subscriber and updates the welcome message.
	 * 
	 * @param s the {@link Subscriber} object for the current user.
	 */
	public void setSubscriber(Subscriber s) {
		this.sub = s;
		welcomeLabel.setText("Hello, " + s.getName() + "!");
	}

	/**
	 * Sets the client instance used for communication with the backend.
	 * 
	 * @param cm the client instance of type {@link ClientMain}.
	 */
	public void setClient(ClientMain cm) {
		this.cm = cm;
		nc = new NotificationsController(cm);
		try {
			this.notifications = nc.getNotificationsSub(sub.getId(), 0);
			notifilabel = notifications.size();
			updateNotificationBubble(notifilabel);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updates the notification bubble to display the number of unread
	 * notifications.
	 *
	 * @param count the number of unread notifications
	 */
	public void updateNotificationBubble(int count) {
		if (count > 0) {
			notificationBubble.setText(String.valueOf(count));
			notificationBubble.setVisible(true);
		} else {
			notificationBubble.setVisible(false);
		}
	}

	/**
	 * Handles the "Order Book" button click event. Opens the book order screen.
	 * 
	 * @param event the triggered event when the button is clicked.
	 */
	@FXML
	private void onOrderBookClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/OrderBookScreen.fxml", "Book Order", event, controller -> {
			if (controller instanceof OrderBookController) {
				((OrderBookController) controller).setStage(stage);
				((OrderBookController) controller).setSubscriber(sub);
				((OrderBookController) controller).setClient(cm);
			}
		});
	}

	/**
	 * Handles the "Search Book" button click event. Opens the search book screen.
	 * 
	 * @param event the triggered event when the button is clicked.
	 */
	@FXML
	private void onSearchBookClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/SearchBookScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof SearchBookController) {
				((SearchBookController) controller).setStage(stage);
				((SearchBookController) controller).setClient(cm);
				((SearchBookController) controller).setLoggedIn(true);
			}
		});
	}

	/**
	 * Handles the "Reader Card" button click event. Opens the reader card screen.
	 * 
	 * @param event the triggered event when the button is clicked.
	 */
	@FXML
	private void onReaderCardClick(ActionEvent event) {

		ScreenLoader.openPopUpScreenWithSize("/gui/ReaderCardScreen.fxml", "Reader Card", event, controller -> {
			if (controller instanceof ReaderCardController) {
				((ReaderCardController) controller).setStage(stage);
				((ReaderCardController) controller).setClientAndSubscriber(sub, cm);
			}
		}, 450, 300);
	}

	/**
	 * Handles the "Extend Borrow" button click event. Opens the extend borrow
	 * screen.
	 * 
	 * @param event the triggered event when the button is clicked.
	 */
	@FXML
	private void onExtendBorrowClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/ExtendBorrowScreen.fxml", "Extend Borrow", event, controller -> {
			if (controller instanceof ExtendBorrowController) {
				((ExtendBorrowController) controller).setStage(stage);
				((ExtendBorrowController) controller).setClient(cm, sub);
			}
		});
	}

	/**
	 * Handles the "View Profile" button click event. Opens the subscriber details
	 * screen.
	 * 
	 * @param event the triggered event when the button is clicked.
	 */
	@FXML
	private void onViewProfileClick(ActionEvent event) {
		ScreenLoader.openPopUpScreenWithSize("/gui/SubscriberDetails.fxml", "Subcriber Details", event, controller -> {
			if (controller instanceof SubscriberDetailsController) {
				((SubscriberDetailsController) controller).setStage(stage);
				((SubscriberDetailsController) controller).setIDandClient(sub.getId(), cm);
			}

		}, 400, 450);

	}

	/**
	 * Handles the "Notifications" button click event. Opens the notifications
	 * screen.
	 * 
	 * @param event the triggered event when the button is clicked.
	 */
	@FXML
	private void onNotificationClick(ActionEvent event) {
		ScreenLoader.openPopUpScreenWithSize("/gui/NotificationShowScreen.fxml", "Notification", event, controller -> {
			if (controller instanceof NotificationShowController) {
				((NotificationShowController) controller).setStage(stage);
				((NotificationShowController) controller).setSubscriber(sub, nc);
				((NotificationShowController) controller).setSubscriberMainController(this);
			}
		}, 400, 400);
	}

	/**
	 * Handles the "Logout" button click event. Logs the user out and redirects to
	 * the login screen.
	 * 
	 * @param event the triggered event when the button is clicked.
	 */
	@FXML
	private void handleLogout(ActionEvent event) {
		this.sub = null;
		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(stage);
				((LoginController) controller).setClient(cm);
			}
		});
	}

	/**
	 * Handles the "Exit" button click event. Closes the application window.
	 */
	@FXML
	private void handleExit() {
		ScreenLoader.closeWindow(exitButton);
	}
}
