
package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.Map;
import client.ClientMain;
import javafx.event.ActionEvent;
import logic.Book;
import logic.BookController;
import logic.NotificationsController;
import logic.ScreenLoader;


public class LibrarianMainController {
	@FXML
	private Label welcomeLabel;

	@FXML
	private Button manageUsersButton;
	
	@FXML
	private Button manageBooksButton;

	@FXML
	private Button borrowBookButton;

	@FXML
	private Button returnBookButton;
	@FXML
	private Button readerCardButton;

	@FXML
	private Button reportsButton;

	@FXML
	private Button logoutButton;
	@FXML
	private Button exitButton;
	@FXML
	private Button notiButton;

	@FXML
	private Label notificationBubble;

	private Stage stage;
	private ClientMain c;
	private Librarian lib;
	private BookController bc;
	private NotificationsController nc;
	private int notifilabel;
	private Map<String, Integer> notifications;

	public void setClient(ClientMain c) {
		this.c = c;
		nc = new NotificationsController(c);
		try {
			this.notifications = nc.getNotificationsLib(0);
			notifilabel = notifications.size();
			updateNotificationBubble(notifilabel);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setClient(ClientMain c) {
		this.c = c;
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
		welcomeLabel.setText("Hello, " + lib.getName() + "!");
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
	private void onManageBooksClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/ManagmentBooksScreen.fxml", "Books Managemnt", event, controller -> {
	        if (controller instanceof ManagmentBookController) {
	            ManagmentBookController managmentController = (ManagmentBookController) controller;
	            managmentController.setStage(stage);
	            managmentController.setClient(c);
	            managmentController.setLibrarian(lib);
	            System.out.println("onManageBooksClick: setClient and setLibrarian called");
			}
		});
	}

	@FXML
	private void onManageSubscriberClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/ManagmentSubscriberScreen.fxml", "Subscriber Managemnt", event, controller -> {
			if (controller instanceof ManagmentSubscriberController) {
				((ManagmentSubscriberController) controller).setStage(stage);
				((ManagmentSubscriberController) controller).setClient(c);
				((ManagmentSubscriberController) controller).setLibrarian(lib);
			}
		});
	}

	@FXML
	private void onBorrowBookClick(ActionEvent event) {
		ScreenLoader.openScreenWithSize("/gui/BorrowBookScreen.fxml", "Borrow Book", event, controller -> {
			if (controller instanceof BorrowBookController) {
				((BorrowBookController) controller).setStage(stage);
				((BorrowBookController) controller).setClient(c);
				((BorrowBookController) controller).setLibrarian(lib);
			}
		}, 500, 350);

	}

	@FXML
	private void onReturnBookClick(ActionEvent event) {
		ScreenLoader.openScreenWithSize("/gui/ReturnBookScreen.fxml", "Return Book", event, controller -> {
			if (controller instanceof ReturnBookController) {
				((ReturnBookController) controller).setStage(stage);
				((ReturnBookController) controller).setClient(c);
				((ReturnBookController) controller).setLibrarian(lib);
			}
		}, 400, 300);
	}

	@FXML
	private void onReportsClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/ReportsScreen.fxml", "Reports Screen", event, controller -> {
			if (controller instanceof ReportsController) {
				((ReportsController) controller).setStage(stage);
				((ReportsController) controller).setClient(c);
				((ReportsController) controller).setLibrarian(lib);
			}
		});
	}

	@FXML
	private void onLogoutClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(stage);
				((LoginController) controller).setClient(c);
			}
		});
	}

	@FXML
	private void onNotificationClick(ActionEvent event) {
		ScreenLoader.openPopUpScreenWithSize("/gui/NotificationShowScreen.fxml", "Notification", event, controller -> {
			if (controller instanceof NotificationShowController) {
				((NotificationShowController) controller).setStage(stage);
				((NotificationShowController) controller).setLibrarian(lib, nc);
				((NotificationShowController) controller).setLibrarianMainController(this);
			}
		}, 400, 400);
	}

	@FXML
	private void onExitClick(ActionEvent event) {
		ScreenLoader.closeWindow(exitButton);
	}

}
