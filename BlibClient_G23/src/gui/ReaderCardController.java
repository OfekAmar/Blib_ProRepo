package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;

import java.util.List;
import java.util.ArrayList;

public class ReaderCardController {

	@FXML
	private TextField searchField;

	@FXML
	private Button searchButton;

	@FXML
	private ListView<String> resultsListView;

	@FXML
	private Button backButton;
	@FXML
	private Button editButton;

	private boolean isLibrarian = false;
	private Subscriber sub;
	private List<String> subscribers; // Example list of subscribers (replace with database integration)
	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setLibrarian(boolean b) {
		isLibrarian = b;
		updateVisibility();
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
		backButton.setText("close");
	}

	private void updateVisibility() {
		if (isLibrarian) {
			searchField.setVisible(true);
			searchButton.setVisible(true);
			searchField.setManaged(true);
			searchButton.setManaged(true);
			editButton.setVisible(true);
			editButton.setManaged(true);
		} else {
			searchField.setVisible(false);
			searchButton.setVisible(false);
			searchField.setManaged(false);
			searchButton.setManaged(false);
			editButton.setVisible(false);
			editButton.setManaged(false);
		}
	}

	@FXML
	public void initialize() {
		updateVisibility();
		// Initialize with sample data (replace with actual database fetch logic)
		subscribers = new ArrayList<>();
		subscribers.add("John Doe");
		subscribers.add("Jane Smith");
		subscribers.add("Emily Johnson");
	}

	@FXML
	private void onSearchClick(ActionEvent event) {
		String searchTerm = searchField.getText();

		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name or ID.");
			return;
		}

		// Filter results based on search term
		List<String> filteredResults = new ArrayList<>();
		for (String subscriber : subscribers) {
			if (subscriber.toLowerCase().contains(searchTerm.toLowerCase())) {
				filteredResults.add(subscriber);
			}
		}

		// Update ListView with results
		resultsListView.getItems().clear();
		if (filteredResults.isEmpty()) {
			resultsListView.getItems().add("No results found.");
		} else {
			resultsListView.getItems().addAll(filteredResults);
		}
	}

	@FXML
	private void onEditClick(ActionEvent event) {

	}

	@FXML
	private void onBackClick(ActionEvent event) {
		if (sub != null && !isLibrarian) {
			ScreenLoader.closeWindow(backButton);
		} else {
			ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
				if (controller instanceof LibrarianMainController) {
					((LibrarianMainController) controller).setStage(new Stage());
				}
			});
		}
	}
}