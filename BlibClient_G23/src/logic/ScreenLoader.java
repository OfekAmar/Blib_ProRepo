package logic;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gui.*;
import java.util.function.Consumer;

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
}