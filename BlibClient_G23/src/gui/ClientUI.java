package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main entry point for the client-side GUI application. This class creates
 * the GUI window and initializes the "Client Connect" screen. It extends
 * {@link Application}, which is part of the JavaFX framework.
 */
public class ClientUI extends Application {
	/**
	 * The entry point for the JavaFX application. This method is automatically
	 * called when {@link #launch(String...)} is invoked from the main method. It
	 * sets up the primary stage (main GUI window) and loads the FXML file for the
	 * "Client Connect" screen.
	 *
	 * @param primaryStage the primary stage (main window) of the application
	 * @throws Exception if an error occurs during FXML loading or stage
	 *                   initialization
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// FMXL loading stage, load the FXML file that have the UI(design)
		Parent root = FXMLLoader.load(getClass().getResource("ClientConnect.fxml"));
		primaryStage.setTitle("Client Connect Application");
		primaryStage.setScene(new Scene(root, 350, 350));
		primaryStage.show();
	}

	/**
	 * The main method of the application. This method launches the JavaFX
	 * application by invoking {@link #launch(String...)}.
	 *
	 * @param args the command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
