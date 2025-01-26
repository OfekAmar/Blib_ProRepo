package gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The `ServerUI` class creates the GUI window for the server. It extends
 * `Application` (JavaFX) and serves as the entry point for initializing and
 * launching the server's user interface.
 */
public class ServerUI extends Application {
	/**
	 * This method is called automatically when using `launch()` (in the `main`
	 * method). It sets up the primary stage, which represents the main "window" of
	 * the application.
	 *
	 * @param primaryStage the main stage of the JavaFX application.
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

	/**
	 * The main method that launches the JavaFX application. It calls the `launch()`
	 * method to start the application, which in turn triggers the `start()` method.
	 *
	 * @param args command-line arguments (not used in this case).
	 */
	public static void main(String[] args) {
		launch(args); // call start method
	}
}
