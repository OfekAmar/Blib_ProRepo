package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientMain;
import ocsf.client.*;

//the class is the controller for the Client.fxml and make the connection between the logic and the UI
public class ClientUIController {
	@FXML
	private ListView<String> subscriberList; // FXML component from type list
	@FXML
	private Button editButton; // FXML component from type button

	private Stage stage; // stage of the current GUI window
	private ClientMain client; // instance of ClientMain to manage the connection between the client to the
								// server

	// the method declare the messagehandler and open the connection to the server
	public void setClient(ClientMain client) {
		this.client = client;
		try {
			client.openConnection();
			// client.setMessageHandler(this::processServerMessage); // the messagehandler
			// insted of value it points to the
			// method processServerMessage
			loadSubscribers(); // in order to fatch all the subscribers from the database
		} catch (IOException e) {
			System.err.println("Failed to connect to server: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// the method save the stage instance in order to close the window and make
	// updates
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	// the method send message to the server using the keyword "READ" that is
	// familiar by the server
	private void loadSubscribers() {
		client.sendMessageToServer("READ");
	}

	// handle the messages coming from the server
	// updates the list with the returned valur from the server after the "READ"
	// message was sent
	private void processServerMessage(String message) {
		List<String> subscribers = parseSubscribers(message);
		ObservableList<String> observableSubscribers = FXCollections.observableArrayList(subscribers);
		subscriberList.setItems(observableSubscribers);
	}

	// casting from Strint that was sent from server into list of subscribers
	private List<String> parseSubscribers(String response) {
		List<String> result = new ArrayList<>();
		String[] rows = response.split("\\\\n");
		for (String row : rows) {
			if (!row.trim().isEmpty()) {
				result.add(row.trim());
			}
		}
		return result;
	}

	/*
	 * // the method send message to the server using the keyword "SHOW" that is //
	 * familiar by the server // in order to get back subscriber identified by his
	 * id
	 * 
	 * @FXML public void editSubscriber() { String selectedSubscriber =
	 * subscriberList.getSelectionModel().getSelectedItem();
	 * 
	 * if (selectedSubscriber != null) { String[] parts =
	 * selectedSubscriber.split(","); String subscriberId = parts[0];// Assuming ID
	 * is the first field client.sendMessageToServer("SHOW," + subscriberId); //
	 * send the server "SHOW" keyword client.setMessageHandler(response -> { if
	 * (response.startsWith(parts[0])) { String details =
	 * response.replace("SHOW_RESPONSE,", ""); openSubscriberDetailsGUI(details);//
	 * call the method to open new GUI in order to edit subscriber // details } });
	 * } }
	 */
	// load new FXML file in order to open the subscriber GUI
	// using Platfrom.runLater() - JavaFX tool operats like Thread
	// because we are using another thread of JavaFx we need to return to the main
	// one in order to make GUI changes
	private void openSubscriberDetailsGUI(String details) {
		Platform.runLater(() -> {
			try {
				// load the FXML
				FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberDetails.fxml"));
				Parent root = loader.load();

				// create new stage
				Stage stage = new Stage();
				stage.setTitle("Subscriber Details");
				stage.setScene(new Scene(root));

				// get the controller and transfer the details
				SubscriberDetailsController controller = loader.getController();
				controller.setStage(stage, client);
				// controller.setDetails(details);

				// start the GUI window
				stage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	public void closeApplication() {
		try {
			// close the connection
			client.closeConnection();
			System.out.println("Disconnected from server.");
		} catch (Exception e) {
			System.err.println("Failed to disconnect: " + e.getMessage());
		}

		// close the GUI window
		if (stage != null) {
			stage.close();
		} else {
			System.err.println("Stage is not initialized.");
		}
	}

}
