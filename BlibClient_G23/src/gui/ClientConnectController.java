package gui;

import java.awt.Button;
import javafx.event.ActionEvent;

import client.ClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ScreenLoader;

public class ClientConnectController {

	private ClientMain client;

	@FXML
	private TextField portField;

	@FXML
	private TextField hostField;

	@FXML
	private javafx.scene.control.Button loginButton;

	@FXML
	public void connectToServer(ActionEvent event) {
		// auto connect for testing purposes
		// String port = portField.getText();
		// String host = hostField.getText();

		String port = "5555";
		String host = "localhost";

		if (port == null || port.isEmpty() || host == null || host.isEmpty()) {
			System.out.println("IP or Port is empty. Please enter valid details.");
			return;
		}

		try {
			client = new ClientMain(host, Integer.parseInt(port));
			client.openConnection();
			System.out.println("Connected to server at " + host + ":" + port);
			ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Search Book", event, controller -> {
				if (controller instanceof LoginController) {
					((LoginController) controller).setStage(new Stage());
					((LoginController) controller).setClient(client);
				}
			});

		} catch (NumberFormatException e) {
			System.err.println("Invalid port number: " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
