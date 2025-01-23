
package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import client.ClientMain;
import javafx.event.ActionEvent;
import logic.Book;
import logic.Librarian;
import logic.ScreenLoader;

public class LibrarianMainController {
	@FXML
	private Label welcomeLabel;

	@FXML
	private Button manageUsersButton;

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

	private Stage stage;
	private ClientMain c;
	private Librarian lib;

	public void setClient(ClientMain c) {
		this.c = c;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
		welcomeLabel.setText("Hello, " + lib.getName() + "!");
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
	private void onExitClick(ActionEvent event) {
		ScreenLoader.closeWindow(exitButton);
	}

}
