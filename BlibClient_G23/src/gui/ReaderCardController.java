package gui;

import java.time.LocalDate;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.BorrowController;
import logic.Librarian;
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
	private Button backButton;

	@FXML
	private Button editButton;

	@FXML
	private Button borrowHistoryButton;

	@FXML
	private Button activityRecordsButton;

	@FXML
	private HBox editHBox;

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
	private Librarian lib;
	private Subscriber sub;
	private Stage stage;
	private ClientMain c;
	SubscriberController sc;
	ReportController rc;
	BorrowController bc;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	public void setClientMain(ClientMain c) {
		this.c = c;
	}

	public void setClientAndSubscriber(Subscriber s, ClientMain c) {
		this.sub = s;
		this.c = c;
		sc = new SubscriberController(c);
		rc = new ReportController(c);
		bc = new BorrowController(sc, c);
		updateBorrowHistoryList();
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

	@FXML
	public void initialize() {
		resultsTableView.setVisible(false);
		resultsTableView.setManaged(false);
		resultsListView.setVisible(false);
		resultsListView.setManaged(false);
		editHBox.setVisible(false);
		editHBox.setManaged(false);
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
	private void onEditClick(ActionEvent event) throws InterruptedException {
		Integer borrowId = Integer.valueOf(borrowIdField.getText());
		LocalDate dueDate = dueDatePicker.getValue();

		if (borrowId == null || dueDate == null) {
			ScreenLoader.showAlert("Error", "Please fill all the fields before editing.");
			return;
		}
		bc.extendBorrowManualy(sub.getId(), borrowId, dueDate);
		updateBorrowHistoryList();
	}

	@FXML
	private void onBorrowHistoryClick(ActionEvent event) {
		ScreenLoader.resizeCenterWindow(event, 700, 500);
		resultsTableView.setVisible(false);
		resultsTableView.setManaged(false);
		resultsListView.setVisible(true);
		resultsListView.setManaged(true);
		editHBox.setVisible(true);
		editHBox.setManaged(true);
		updateBorrowHistoryList();
	}

	@FXML
	private void onActivityRecordsClick(ActionEvent event) {
		ScreenLoader.resizeCenterWindow(event, 700, 500);
		resultsListView.setVisible(false);
		resultsListView.setManaged(false);
		resultsTableView.setVisible(true);
		resultsTableView.setManaged(true);
		editHBox.setVisible(false);
		editHBox.setManaged(false);
		updateActivityRecordsTable();
	}

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/ManagmentSubscriberScreen.fxml", "Managment Subscriber", event, controller -> {
			if (controller instanceof ManagmentSubscriberController) {
				((ManagmentSubscriberController) controller).setStage(new Stage());
				((ManagmentSubscriberController) controller).setClient(c);
				((ManagmentSubscriberController) controller).setLibrarian(lib);
			}
		});

	}
}
