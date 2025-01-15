package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// test test rotem
public class LoginController {

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	private Stage stage;
	private FXMLLoader loader;
	private boolean isLibrarian = false;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onLoginClick(ActionEvent event) {
		Platform.runLater(() -> {
			try {
				Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				if (isLibrarian) {
					loader = new FXMLLoader(getClass().getResource("LibrarianMainScreen.fxml"));

					Parent root = loader.load();
					Stage stage = new Stage();
					stage.setTitle("Librarian Screen");
					stage.setScene(new Scene(root));
					// SearchController controller = loader.getController();
					// controller.setStage(stage);
					stage.show();
				} else {
					loader = new FXMLLoader(getClass().getResource("SubscriberMainScreen.fxml"));
					Parent root = loader.load();
					Stage stage = new Stage();
					stage.setTitle("Subscriber Screen");
					stage.setScene(new Scene(root));
					SubscriberMainController controller = loader.getController();
					controller.setStage(stage);
					controller.setSubscriberName("Ofek"); // need to be changed
					stage.show();
				}

				currentStage.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	private void onGuestLoginClick(ActionEvent event) {
		Platform.runLater(() -> {
			try {
				Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

				// load the FXML
				loader = new FXMLLoader(getClass().getResource("SearchBookScreen.fxml"));
				Parent root = loader.load();

				// create new stage
				Stage stage = new Stage();
				stage.setTitle("Search Book");
				stage.setScene(new Scene(root));

				SearchController controller = loader.getController();
				controller.setStage(stage);

				// start the GUI window
				stage.show();

				currentStage.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	private void handleExit(ActionEvent event) {
		System.exit(0);

	}

}
