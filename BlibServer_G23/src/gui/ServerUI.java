package gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * the class creat the GUI window for the server while she extands Application
 * (JavaFX)
 */
public class ServerUI extends Application {
	/**
	 * this method called automaticly when using launch(in the main method)
	 * primaryStage - presents the main "stage" (GUI window) of the application
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// FMXL loading stage, load the FXML file that have the UI(design)
			Parent root = FXMLLoader.load(getClass().getResource("ServerUI.fxml"));
			primaryStage.setTitle("Server Control Panel"); // the GUI Headline text
			primaryStage.setScene(new Scene(root, 300, 200)); // the GUI window size
			primaryStage.show();
		} catch (IOException e) {
			System.err.println("Failed to load FXML: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args); // call start method
	}
}
