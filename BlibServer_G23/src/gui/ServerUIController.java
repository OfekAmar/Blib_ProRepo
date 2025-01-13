package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import ocsf.server.ConnectionToClient;
import server.ServerMain;
//this class is used to manage all the user input to the GUI window, its the connection 
// between the GUI window and the code logic
public class ServerUIController {
	@FXML
	private ListView<String> connectionList; //FXML component to present list in the GUI
	//JavaFX List that notifies when changes are made, it bound to the UI and keep it updated
	private ObservableList<String> connections; 
	private ServerMain server; // ServerMain instance 

	//declaration of the lis and connect it to the GUI list
	@FXML
	public void initialize() {
		connections = FXCollections.observableArrayList();
		connectionList.setItems(connections);
	}

	//start the server if not working with "5555" port
	//and override the clientConnected and clientDisconnected method 
	//in order to update the status on the go while the GUI is on
	@FXML
	public void startServer() {
		if (server == null) {
			server = new ServerMain(5555) {
				@Override
				protected void clientConnected(ConnectionToClient client) {
					String clientInfo = "Connected: IP = " + client.getInetAddress().getHostAddress() + ", Host = "
							+ client.getInetAddress().getHostName();
					connections.add(clientInfo);
				}

				@Override
				protected void clientDisconnected(ConnectionToClient client) {
					String clientInfo = "Disconnected: IP = " + client.getInetAddress().getHostAddress();
					connections.add(clientInfo);
				}
			};

			try {
				server.listen(); // OCSF method in order that the server will listen to connections
			} catch (Exception e) {
				System.err.println("Error starting server: " + e.getMessage());
			}
		}
	}

	//the class close the server if it runs and clean the connection list
	@FXML
	public void stopServer() {
		if (server != null) {
			try {
				server.close();
				server = null;
				connections.clear();
			} catch (Exception e) {
				System.err.println("Error stopping server: " + e.getMessage());
			}
		}
	}
}
