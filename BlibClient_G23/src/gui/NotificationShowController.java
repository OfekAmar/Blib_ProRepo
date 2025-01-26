package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import logic.Librarian;
import logic.LoginLogic;
import logic.NotificationsController;
import logic.ScreenLoader;
import logic.Subscriber;

import java.util.HashMap;
import java.util.Map;

import client.ClientMain;

/**
 * The `NotificationShowController` class handles the display and interaction
 * with notifications for both subscribers and librarians. The controller
 * provides functionality to mark notifications as read, show all notifications,
 * and manage the notification list view.
 */
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
	private Librarian lib;
	private ClientMain c;
	private NotificationsController nc;
	private SubscriberMainController smc;
	private LibrarianMainController lmc;

	/**
	 * Sets the current stage (window) for the controller.
	 *
	 * @param stage the current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the subscriber for which the notifications are fetched and displayed.
	 * Initializes the notifications using the {@link NotificationsController}.
	 *
	 * @param s  the {@link Subscriber} instance
	 * @param nc the {@link NotificationsController} instance for fetching
	 *           notifications
	 */
	public void setSubscriber(Subscriber s, NotificationsController nc) {
		this.sub = s;
		this.nc = nc;
		setNotifications();
	}

	/**
	 * Sets the librarian for which the notifications are fetched and displayed.
	 * Initializes the notifications using the {@link NotificationsController}.
	 *
	 * @param l  the {@link Librarian} instance
	 * @param nc the {@link NotificationsController} instance for fetching
	 *           notifications
	 */
	public void setLibrarian(Librarian l, NotificationsController nc) {
		this.lib = l;
		this.nc = nc;
		setNotifications();
	}

	/**
	 * Sets the client instance for the controller.
	 *
	 * @param c the {@link ClientMain} instance used for server communication
	 */
	public void setClient(ClientMain c) {
		this.c = c;
	}

	/**
	 * Sets the main controller for the subscriber's screen. This allows the
	 * `NotificationShowController` to interact with the subscriber's main screen
	 * and update notifications.
	 *
	 * @param controller the {@link SubscriberMainController} instance for the
	 *                   subscriber's main screen
	 */
	public void setSubscriberMainController(SubscriberMainController controller) {
		this.smc = controller;
	}

	/**
	 * Sets the main controller for the librarian's screen. This allows the
	 * `NotificationShowController` to interact with the librarian's main screen and
	 * update notifications.
	 *
	 * @param controller the {@link LibrarianMainController} instance for the
	 *                   librarian's main screen
	 */
	public void setLibrarianMainController(LibrarianMainController controller) {
		this.lmc = controller;
	}

	/**
	 * Updates the notification list view with the current notifications based on
	 * the user type. Notifications are fetched from the
	 * {@link NotificationsController}.
	 */
	public void setNotifications() {
		try {
			if (this.sub != null) {
				this.notifications = nc.getNotificationsSub(sub.getId(), 0);
			} else {
				this.notifications = nc.getNotificationsLib(0);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		notificationListView.getItems().clear();
		notificationListView.getItems().addAll(notifications.keySet());
	}

	/**
	 * Handles marking a selected notification as read. Updates the notification
	 * list and unread count.
	 */
	@FXML
	private void onMarkAsReadClick() {
		String selectedNotification = notificationListView.getSelectionModel().getSelectedItem();

		if (selectedNotification != null) {
			Integer notificationId = notifications.get(selectedNotification);

			try {
				if (this.sub != null) {
					nc.markAsReadSubs(notificationId);
					setNotifications();
					if (smc != null) {
						int newUnreadCount = nc.getNotificationsSub(sub.getId(), 0).size();
						smc.updateNotificationBubble(newUnreadCount);
					}
				} else {
					nc.markAsReadLib(notificationId);
					setNotifications();
					if (lmc != null) {
						int newUnreadCount = nc.getNotificationsLib(0).size();
						lmc.updateNotificationBubble(newUnreadCount);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			ScreenLoader.showAlert("Error", "No notification selected.");
		}
	}

	/**
	 * Displays all notifications, including read and unread, in the notification
	 * list view.
	 */
	@FXML
	private void onShowAllClick() {
		try {
			if (this.sub != null) {
				this.notifications = nc.getNotificationsSub(sub.getId(), 1);
			} else {
				this.notifications = nc.getNotificationsLib(1);
			}
			notificationListView.getItems().clear();
			notificationListView.getItems().addAll(notifications.keySet());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Closes the notification window.
	 */
	@FXML
	private void onCloseClick() {
		ScreenLoader.closeWindow(closeButton);
	}
}
