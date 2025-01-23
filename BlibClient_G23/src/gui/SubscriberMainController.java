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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
		welcomeLabel.setText("Hello, " + s.getName() + "!");
	}

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

	public void updateNotificationBubble(int count) {
		if (count > 0) {
			notificationBubble.setText(String.valueOf(count));
			notificationBubble.setVisible(true);
		} else {
			notificationBubble.setVisible(false);
		}
	}

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

	@FXML
	private void borrowHistoryClick(ActionEvent event) {
		ScreenLoader.openPopUpScreenWithSize("/gui/BorrowHistoryScreen.fxml", "Borrow History", event, controller -> {
			if (controller instanceof BorrowHistoryController) {
				((BorrowHistoryController) controller).setStage(stage);
				((BorrowHistoryController) controller).setClientAndSubscrber(sub, cm);

			}
		}, 450, 300);
	}

	@FXML
	private void onExtendBorrowClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/ExtendBorrowScreen.fxml", "Extend Borrow", event, controller -> {
			if (controller instanceof ExtendBorrowController) {
				((ExtendBorrowController) controller).setStage(stage);
				// ((ExtendBorrowController) controller).setSubscriber(sub); // Setting
				// Subscriber
				((ExtendBorrowController) controller).setClient(cm, sub); // Setting ClientMain
			}
		});
	}

	@FXML
	private void onViewProfileClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/SubscriberDetails.fxml", "Subcriber Details", event, controller -> {
			if (controller instanceof SubscriberDetailsController) {
				((SubscriberDetailsController) controller).setStage(new Stage(), cm);
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
	private void onNotificationClick(ActionEvent event) {
		ScreenLoader.openPopUpScreenWithSize("/gui/NotificationShowScreen.fxml", "Notification", event, controller -> {
			if (controller instanceof NotificationShowController) {
				((NotificationShowController) controller).setStage(stage);
				((NotificationShowController) controller).setSubscriber(sub, nc);
				((NotificationShowController) controller).setSubscriberMainController(this);
			}
		}, 400, 400);
	}

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

	@FXML
	private void handleExit() {
		ScreenLoader.closeWindow(exitButton);
	}
}
