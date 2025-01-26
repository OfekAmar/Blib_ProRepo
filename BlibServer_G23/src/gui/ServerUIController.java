package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import ocsf.server.ConnectionToClient;
import server.ServerMain;

/**
 * The `ServerUIController` class is used to manage user input in the GUI window
 * and serves as the connection between the GUI window and the underlying code
 * logic. This class handles starting and stopping the server and updating the
 * list of client connections in the GUI.
 */
public class ServerUIController {
	@FXML
	private ListView<String> connectionList; // FXML component to present list in the GUI
	private ObservableList<String> connections;
	private ServerMain server; // ServerMain instance

	/**
	 * Initializes the ListView and sets up the ObservableList to hold the
	 * connection details.
	 */
	@FXML
	public void initialize() {
		connections = FXCollections.observableArrayList();
		connectionList.setItems(connections);
	}

	/**
	 * Starts the server and begins listening for client connections. If the server
	 * is not already running, it will create a new ServerMain instance and override
	 * the `clientConnected` and `clientDisconnected` methods to update the
	 * connection list.
	 */
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

	/**
	 * Stops the server and clears the connection list. If the server is running, it
	 * will be closed and the connection list will be reset.
	 */
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
