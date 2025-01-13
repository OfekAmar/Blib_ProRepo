package gui;

import client.ClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

//the class is the controller for the ClientConnect.fxml and make the connection between the logic and the UI
public class ClientConnectController {

	private ClientMain client;

	@FXML
	private TextField portField;

	@FXML
	private TextField hostField;

	// the method operates when the client press the connect button in the GUI
	// the method reciving the values from the TextField in the GUI and usese them
	// to connect to the server via ClienMain
	@FXML
	public void connectToServer() {
		String port = portField.getText();
		String host = hostField.getText();

		if (port == null || port.isEmpty() || host == null || host.isEmpty()) {
			System.out.println("IP or Port is empty. Please enter valid details.");
			return;
		}

		try {

			// initialize and connect to the server
			client = new ClientMain(host, Integer.parseInt(port));// using the TextField values
			client.openConnection();
			System.out.println("Connected to server at " + host + ":" + port);

			// load the ClientUI GUI
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientUI.fxml"));
			Parent root = loader.load();

			// get the ClientUIController
			ClientUIController controller = loader.getController();
			controller.setClient(client);// transfaring the client to the next controller in order to use it
			controller.setStage((Stage) portField.getScene().getWindow()); // transfering the stage

			Stage stage = (Stage) portField.getScene().getWindow(); // using the stage
			stage.setScene(new Scene(root, 400, 400));// open window
			stage.setTitle("Client UI");

		} catch (NumberFormatException e) {
			System.err.println("Invalid port number: " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
