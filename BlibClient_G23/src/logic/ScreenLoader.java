package logic;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;
import gui.*;

import javafx.geometry.Rectangle2D;
import java.util.function.Consumer;
import javafx.scene.control.Alert;

/**
 * The ScreenLoader class provides utility methods for managing and loading
 * JavaFX screens. It includes methods for opening new stages, resizing windows,
 * showing alerts, and managing pop-ups.
 */
public class ScreenLoader {
	/**
	 * Opens a new screen with the specified FXML file and stage title, and closes
	 * the current stage.
	 *
	 * @param <T>                the type of the controller for the FXML.
	 * @param fxmlFileName       the name of the FXML file to load.
	 * @param stageTitle         the title of the new stage.
	 * @param event              the ActionEvent that triggered the screen change.
	 * @param controllerConsumer a consumer to set up the controller for the new
	 *                           screen.
	 */
	public static <T> void openScreen(String fxmlFileName, String stageTitle, ActionEvent event,
			Consumer<T> controllerConsumer) {
		Platform.runLater(() -> {
			try {
				// Get current stage
				Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

				// Load FXML
				FXMLLoader loader = new FXMLLoader(ScreenLoader.class.getResource(fxmlFileName));
				Parent root = loader.load();

				// Create new stage
				Stage newStage = new Stage();
				newStage.setTitle(stageTitle);
				newStage.setScene(new Scene(root));

				// Pass controller to consumer for additional setup
				T controller = loader.getController();
				if (controllerConsumer != null) {
					controllerConsumer.accept(controller);
				}

				// Show new stage and close current stage
				newStage.show();
				currentStage.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Opens a new screen with the specified size, FXML file, and stage title, then
	 * closes the current stage.
	 *
	 * @param <T>                the type of the controller for the FXML.
	 * @param fxmlFileName       the name of the FXML file to load.
	 * @param stageTitle         the title of the new stage.
	 * @param event              the ActionEvent that triggered the screen change.
	 * @param controllerConsumer a consumer to set up the controller for the new
	 *                           screen.
	 * @param Width              the width of the new stage.
	 * @param Hight              the height of the new stage.
	 */

	public static <T> void openScreenWithSize(String fxmlFileName, String stageTitle, ActionEvent event,
			Consumer<T> controllerConsumer, int Width, int Hight) {
		Platform.runLater(() -> {
			try {
				// Get current stage
				Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

				// Load FXML
				FXMLLoader loader = new FXMLLoader(ScreenLoader.class.getResource(fxmlFileName));
				Parent root = loader.load();

				// Create new stage
				Stage newStage = new Stage();
				newStage.setTitle(stageTitle);
				newStage.setScene(new Scene(root, Width, Hight));

				// Pass controller to consumer for additional setup
				T controller = loader.getController();
				if (controllerConsumer != null) {
					controllerConsumer.accept(controller);
				}

				// Show new stage and close current stage
				newStage.show();
				currentStage.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Opens a pop-up screen with the specified FXML file and stage title.
	 *
	 * @param <T>                the type of the controller for the FXML.
	 * @param fxmlFileName       the name of the FXML file to load.
	 * @param stageTitle         the title of the pop-up stage.
	 * @param event              the ActionEvent that triggered the pop-up.
	 * @param controllerConsumer a consumer to set up the controller for the new
	 *                           screen.
	 */
	public static <T> void openPopUpScreen(String fxmlFileName, String stageTitle, ActionEvent event,
			Consumer<T> controllerConsumer) {
		Platform.runLater(() -> {
			try {
				// Load FXML
				FXMLLoader loader = new FXMLLoader(ScreenLoader.class.getResource(fxmlFileName));
				Parent root = loader.load();

				// Create new stage
				Stage newStage = new Stage();
				newStage.setTitle(stageTitle);
				newStage.setScene(new Scene(root));

				// Pass controller to consumer for additional setup
				T controller = loader.getController();
				if (controllerConsumer != null) {
					controllerConsumer.accept(controller);
				}

				// Show new stage and close current stage
				newStage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Opens a pop-up screen with the specified FXML file, stage title, and
	 * dimensions.
	 *
	 * @param <T>                the type of the controller for the FXML.
	 * @param fxmlFileName       the name of the FXML file to load.
	 * @param stageTitle         the title of the pop-up stage.
	 * @param event              the ActionEvent that triggered the pop-up.
	 * @param controllerConsumer a consumer to set up the controller for the new
	 *                           screen.
	 * @param Width              the width of the pop-up stage.
	 * @param Hight              the height of the pop-up stage.
	 */
	public static <T> void openPopUpScreenWithSize(String fxmlFileName, String stageTitle, ActionEvent event,
			Consumer<T> controllerConsumer, int Width, int Hight) {
		Platform.runLater(() -> {
			try {
				// Load FXML
				FXMLLoader loader = new FXMLLoader(ScreenLoader.class.getResource(fxmlFileName));
				Parent root = loader.load();

				// Create new stage
				Stage newStage = new Stage();
				newStage.setTitle(stageTitle);
				newStage.setScene(new Scene(root, Width, Hight));

				// Pass controller to consumer for additional setup
				T controller = loader.getController();
				if (controllerConsumer != null) {
					controllerConsumer.accept(controller);
				}

				// Show new stage and close current stage
				newStage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Resizes the current window and centers it on the screen.
	 *
	 * @param event  the ActionEvent that triggered the resize.
	 * @param width  the new width of the window.
	 * @param height the new height of the window.
	 */
	public static void resizeCenterWindow(ActionEvent event, int width, int height) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setWidth(width);
		stage.setHeight(height);

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
		double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

		stage.setX(centerX);
		stage.setY(centerY);
	}

	/**
	 * Displays an alert dialog with the specified title and content.
	 *
	 * @param title   the title of the alert dialog.
	 * @param content the content of the alert dialog.
	 */

	public static void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Closes the current window associated with the given button.
	 *
	 * @param b the button whose window is to be closed.
	 */

	public static void closeWindow(Button b) {
		Stage stage = (Stage) b.getScene().getWindow();
		stage.close();
	}
}