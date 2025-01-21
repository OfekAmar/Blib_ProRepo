package gui;

import java.util.ArrayList;
import java.util.List;

import client.ClientMain;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.Record;
import logic.SubscriberController;
import logic.ReportController;

public class ReaderCardController {

	@FXML
	private TextField searchField;

	@FXML
	private TextField borrowIdField;

	@FXML
	private DatePicker dueDatePicker;

	@FXML
	private ComboBox<String> subscriberComboBox;

	@FXML
	private Button searchButton;

	@FXML
	private Button backButton;

	@FXML
	private Button editButton;

	@FXML
	private Button borrowHistoryButton;

	@FXML
	private Button activityRecordsButton;

	@FXML
	private ListView<String> resultsListView;
	@FXML
	private TableView<Record> resultsTableView;

	@FXML
	private TableColumn<Record, Integer> recordIdColumn;

	@FXML
	private TableColumn<Record, String> recordTypeColumn;

	@FXML
	private TableColumn<Record, Integer> subscriberIDColumn;

	@FXML
	private TableColumn<Record, String> recordDateColumn;

	@FXML
	private TableColumn<Record, Integer> bookCodeColumn;

	private ObservableList<Record> recordData;

	private ObservableList<String> obslist;
	private boolean isLibrarian = false;
	private Subscriber sub;
	private Stage stage;
	private ClientMain c;
	SubscriberController sc;
	ReportController rc;

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

	public void setClientAndSubscriber(Subscriber s, ClientMain c) {
		this.sub = new Subscriber(1, "xx", "xx", "xx", "xx", "xx");
		this.c = c;
		sc = new SubscriberController(c);
		rc = new ReportController(c);
	}

	private void updateBorrowHistoryList() {
		resultsListView.setPrefWidth(420);
		try {
			ArrayList<String> result = (ArrayList<String>) sc.viewBorrowHistory(String.valueOf(sub.getId()));
			obslist = FXCollections.observableArrayList();
			resultsListView.setItems(obslist);
			if (result != null) {
				for (String rs : result) {
					obslist.add(rs);
				}
			} else {
				obslist.add("No Books Borrowed");
			}
		} catch (InterruptedException e) {
			System.err.println("Error fetching subscriber data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateActivityRecordsTable() {
		resultsListView.setPrefWidth(420);
		try {
			List<Record> result = rc.getAllRecordsByID(sub.getId());
			recordData = FXCollections.observableArrayList();
			resultsTableView.setItems(recordData);
			if (result != null) {
				for (Record r : result) {
					recordData.add(r);
				}
			} else {
				ScreenLoader.showAlert("Error", "No Records to show");
			}
		} catch (InterruptedException e) {
			System.err.println("Error fetching subscriber data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateSubscribersComboBox() {
		// after reciving from database all the subscribers names + id
		// need to initialize the combobox !!!
	}

	@FXML
	public void initialize() {

		updateSubscribersComboBox();
		resultsTableView.setVisible(false);
		resultsTableView.setManaged(false);
		resultsListView.setVisible(false);
		resultsListView.setManaged(false);
		recordIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordID()));

		recordTypeColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordType()));

		subscriberIDColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscriberID()));

		recordDateColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordDate()));

		bookCodeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookCode()));
	}

	@FXML
	private void onSearchClick(ActionEvent event) {
		String searchTerm = searchField.getText();
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name or ID.");
			return;
		}

		// Simulating search result
		// recive from the comboBox the chosen subscriber
		// set the subscriber returend to the setSubscriber of this controller !

		updateBorrowHistoryList();
	}

	@FXML
	private void onEditClick(ActionEvent event) {
		String subscriberId = borrowIdField.getText();
		String dueDate = (dueDatePicker.getValue() != null) ? dueDatePicker.getValue().toString() : null;

		if (subscriberId == null || subscriberId.trim().isEmpty() || dueDate == null) {
			ScreenLoader.showAlert("Error", "Please fill all the fields before editing.");
			return;
		}

		// Perform edit logic here
		System.out.println("Editing subscriber ID: " + subscriberId + " with due date: " + dueDate);
	}

	@FXML
	private void onBorrowHistoryClick(ActionEvent event) {
		Stage st = (Stage) ((Node) event.getSource()).getScene().getWindow();
		st.setWidth(500);
		st.setHeight(500);
		resultsTableView.setVisible(false);
		resultsTableView.setManaged(false);
		resultsListView.setVisible(true);
		resultsListView.setManaged(true);
		// Logic for Borrow History button
		updateBorrowHistoryList(); // Example: Refresh list with borrow history
	}

	@FXML
	private void onActivityRecordsClick(ActionEvent event) {
		Stage st = (Stage) ((Node) event.getSource()).getScene().getWindow();
		st.setWidth(500);
		st.setHeight(500);
		resultsListView.setVisible(false);
		resultsListView.setManaged(false);
		resultsTableView.setVisible(true);
		resultsTableView.setManaged(true);
		// Logic for Activity Records button
		updateActivityRecordsTable();
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
