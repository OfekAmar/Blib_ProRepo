package gui;

import java.util.ArrayList;
import java.util.List;

import client.ClientMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.SubscriberController;

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

	private ObservableList<String> obslist;
	private boolean isLibrarian = false;
	private Subscriber sub;
	private List<String> subscribers; // Example list of subscribers (replace with database integration)
	private Stage stage;
	private ArrayList<String> result;
	private ClientMain c;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setLibrarian(boolean b) {
		isLibrarian = b;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	public void setClientMain(ClientMain c) {
		this.c = c;
	}

	public void setClientAndSubscrber(Subscriber s, ClientMain c) {
		this.sub = s;
		backButton.setText("close");
		this.c = c;
		updateList();
	}

	private void updateList() {
		resultsListView.setPrefWidth(420);
		SubscriberController sc = new SubscriberController(c);
		try {

			ArrayList<String> result = (ArrayList<String>) sc.viewBorrowHistory(String.valueOf(sub.getId()));
			obslist = FXCollections.observableArrayList();
			resultsListView.setItems(obslist);
			if (result != null) {
				for (String rs : result) {
					obslist.add(rs);
				}
			} else {
				obslist.add("No Books borrowed");
			}
		} catch (InterruptedException e) {

			System.err.println("Error fetching subscriber data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {

	}

	@FXML
	private void onSearchClick(ActionEvent event) {
		String searchTerm = searchField.getText();

		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name or ID.");
			return;
		}

		// need to implement search for subscriber
		// and after the result to use setSubscriber with the result
		// meanwhile alternative !
		int ids = Integer.valueOf(searchTerm);
		Subscriber s = new Subscriber(ids, "xx", "xx", "xx", "xx", "xx");
		setSubscriber(s);
		updateList();

	}

	@FXML
	private void onEditClick(ActionEvent event) {

	}

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
			}
		});

	}
}
