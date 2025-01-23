package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import logic.LoginLogic;
import logic.NotificationsController;
import logic.ScreenLoader;
import logic.Subscriber;

import java.util.HashMap;
import java.util.Map;

import client.ClientMain;

public class NotificationShowController {

	@FXML
	private ListView<String> notificationListView;

	@FXML
	private Button markAsReadButton;

	@FXML
	private Button showAllButton;

	@FXML
	private Button closeButton;

	private Map<String, Integer> notifications;

	private Stage stage;
	private Subscriber sub;
	private ClientMain c;
	private NotificationsController nc;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriber(Subscriber s, NotificationsController nc) {
		this.sub = s;
		this.nc = nc;
		setNotifications();
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	public void setNotifications() {
		try {
			this.notifications = nc.getNotificationsSub(sub.getId(), 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		notificationListView.getItems().clear();
		notificationListView.getItems().addAll(notifications.keySet());
	}

	private SubscriberMainController subscriberMainController;

	public void setSubscriberMainController(SubscriberMainController controller) {
		this.subscriberMainController = controller;
	}

	@FXML
	private void onMarkAsReadClick() {
		String selectedNotification = notificationListView.getSelectionModel().getSelectedItem();

		if (selectedNotification != null) {
			Integer notificationId = notifications.get(selectedNotification);

			try {
				nc.markAsReadSubs(notificationId);
				setNotifications();
				if (subscriberMainController != null) {
					int newUnreadCount = nc.getNotificationsSub(sub.getId(), 0).size();
					subscriberMainController.updateNotificationBubble(newUnreadCount);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// implement mark as read logic !!!
			System.out.println("Marking notification as read: ID = " + notificationId);
		} else {
			System.out.println("No notification selected.");
		}
	}

	@FXML
	private void onShowAllClick() {
		try {
			this.notifications = nc.getNotificationsSub(sub.getId(), 1);
			notificationListView.getItems().clear();
			notificationListView.getItems().addAll(notifications.keySet());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@FXML
	private void onCloseClick() {
		ScreenLoader.closeWindow(closeButton);
	}
}
