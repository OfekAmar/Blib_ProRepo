
package gui;

import java.lang.ModuleLayer.Controller;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Librarian;
import logic.LoginLogic;
import logic.ScreenLoader;
import logic.Subscriber;

public class LoginController {

	@FXML
	private TextField userNameField;

	@FXML
	private PasswordField passwordField;

	private Stage stage;
	private ClientMain c;
	private Object response;
	private LoginLogic loginLogic;
	private boolean isLibrarian = true;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}
	

	@FXML
	private void onLoginClick(ActionEvent event) {
		String userName = userNameField.getText();
		String password = passwordField.getText();
		
		if(userName.isEmpty() || password.isEmpty()) {
			ScreenLoader.showAlert("ERROR", "all fields are required");
			return;
		}
		
		try {
			Object response=new LoginLogic(c).login(userName, password);
			if(response instanceof Subscriber) {
				Subscriber sub=(Subscriber)response;
				ScreenLoader.openScreenWithSize("/gui/SubscriberMainScreen.fxml", "Subscriber Screen", event, controller -> {
					if(controller instanceof SubscriberMainController) {
						((SubscriberMainController) controller).setClient(c);
						((SubscriberMainController) controller).setSubscriber(sub);
					}
				}, 400, 250);
				
			}
			else if(response instanceof Librarian) {
				Librarian lib=(Librarian)response;
				ScreenLoader.openScreenWithSize("/gui/LibrarianMainScreen.fxml", "Librarian Screen", event, controller -> {
					if(controller instanceof LibrarianMainController) {
						((LibrarianMainController) controller).setStage(new Stage());
						((LibrarianMainController) controller).setClient(c);
					}
				}, 250, 400);
				
			}
			else if(response instanceof String) {
				ScreenLoader.showAlert("Error: ",(String)response);
			}
		}catch(Exception e) {
			System.out.println("Error during login: "+e.getMessage());
			ScreenLoader.showAlert("Error", "An error occurred during the login. Please try again later.");
		}

	}


	@FXML
	private void onGuestLoginClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/SearchBookScreen.fxml", "Search Book", event, controller -> {
			if (controller instanceof SearchBookController) {
				((SearchBookController) controller).setStage(new Stage());
				((SearchBookController) controller).setClient(c);

			}
		});
	}

	@FXML
	private void handleExit(ActionEvent event) {
		System.exit(0);

	}

}
