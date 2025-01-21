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
import javafx.stage.Stage;
import gui.*;
import java.util.function.Consumer;
import javafx.scene.control.Alert;

public class ScreenLoader {

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

	public static void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public static void closeWindow(Button b) {
		Stage stage = (Stage) b.getScene().getWindow();
		stage.close();
	}
}