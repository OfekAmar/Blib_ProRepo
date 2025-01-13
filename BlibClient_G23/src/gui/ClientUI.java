package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//the class creat the GUI window for the client the clientConnect screen
//while she extands Application (JavaFX)
public class ClientUI extends Application {

	// this method called automaticly when using launch(in the main method)
	// primaryStage - presents the main "stage" (GUI window) of the application
	@Override
	public void start(Stage primaryStage) throws Exception {
		// FMXL loading stage, load the FXML file that have the UI(design) 
		Parent root = FXMLLoader.load(getClass().getResource("ClientConnect.fxml"));
		primaryStage.setTitle("Client Connect Application");
		primaryStage.setScene(new Scene(root, 300, 300));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
