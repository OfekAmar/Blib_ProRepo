package gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import javafx.scene.control.Label;
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

/**
 * The `ReaderCardController` class serves as the controller for the reader card
 * screen. It provides functionality for viewing a subscriber's borrowing
 * history, activity records, and allows librarians to manually extend borrowing
 * periods.
 */
public class ReaderCardController {

	@FXML
	private TextField borrowIdField;

	@FXML
	private DatePicker dueDatePicker;

	@FXML
	private Button backButton, editButton, borrowHistoryButton, activityRecordsButton;

	@FXML
	private HBox editHBox;

	@FXML
	private TableView<Map.Entry<Integer, Map<String, String>>> borrowHistoryTableView;

	@FXML
	private TableColumn<Map.Entry<Integer, Map<String, String>>, Integer> borrowIdColumn;

	@FXML
	private TableColumn<Map.Entry<Integer, Map<String, String>>, String> titleColumn, borrowDateColumn,
			returnDateColumn;

	@FXML
	private TableView<Record> activityRecordsTableView;

	@FXML
	private TableColumn<Record, Integer> recordIdColumn, subscriberIDColumn, bookCodeColumn;

	@FXML
	private TableColumn<Record, String> recordTypeColumn, recordDateColumn, descriptionColumn;

	@FXML
	private Label idLabel;

	private ObservableList<Record> recordData;

	private ObservableList<String> obslist;
	private Librarian lib;
	private Subscriber sub;
	private Stage stage;
	private ClientMain c;
	SubscriberController sc;
	ReportController rc;
	BorrowController bc;

	/**
	 * Sets the current stage (window) for the controller.
	 *
	 * @param stage the current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the librarian who is currently logged in.
	 *
	 * @param lib the {@link Librarian} instance
	 */
	public void setLibrarian(Librarian lib) {
		this.lib = lib;
		backButton.setText("Back");
	}

	/**
	 * Sets the subscriber whose details are being viewed.
	 *
	 * @param s the {@link Subscriber} instance
	 */
	public void setSubscriber(Subscriber s) {
		this.sub = s;
		setDetails();
	}

	/**
	 * Sets the subscriber whose details are being viewed.
	 *
	 * @param c the {@link ClientMain} instance
	 */
	public void setClientMain(ClientMain c) {
		this.c = c;
	}

	/**
	 * Initializes the client and subscriber for this controller and loads the
	 * borrow history.
	 *
	 * @param s the {@link Subscriber} instance
	 * @param c the {@link ClientMain} instance
	 */
	public void setClientAndSubscriber(Subscriber s, ClientMain c) {
		this.sub = s;
		this.c = c;
		sc = new SubscriberController(c);
		rc = new ReportController(c);
		bc = new BorrowController(sc, c);
		setDetails();
		updateBorrowHistoryTable();
	}

	/**
	 * Displays the subscriber's details on the screen.
	 */
	public void setDetails() {
		idLabel.setText("Subscriber ID:" + Integer.toString(sub.getId()));
	}

	/**
	 * Updates the borrow history table with the subscriber's data.
	 */
	private void updateBorrowHistoryTable() {
		borrowIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getKey()));
		titleColumn.setCellValueFactory(
				cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue().get("title")));
		borrowDateColumn.setCellValueFactory(
				cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue().get("borrowDate")));
		returnDateColumn.setCellValueFactory(
				cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue().get("returnDate")));

		Map<Integer, Map<String, String>> result;
		try {
			result = sc.viewBorrowsOfSub(sub.getId());

			if (result != null && !result.isEmpty()) {
				ObservableList<Map.Entry<Integer, Map<String, String>>> borrowList = FXCollections
						.observableArrayList(result.entrySet());
				borrowHistoryTableView.setItems(borrowList);
			} else {
				borrowHistoryTableView.setItems(FXCollections.observableArrayList());
				ScreenLoader.showAlert("Info", "No Books Borrowed");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updates the activity records table with the subscriber's activity data.
	 */
	private void updateActivityRecordsTable() {
		recordIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordID()));
		recordTypeColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordType()));
		recordDateColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordDate()));
		bookCodeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookCode()));
		descriptionColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescription()));

		try {
			List<Record> result = rc.getAllRecordsByID(sub.getId());
			if (result != null && !result.isEmpty()) {
				ObservableList<Record> recordList = FXCollections.observableArrayList(result);
				activityRecordsTableView.setItems(recordList);
			} else {
				activityRecordsTableView.setItems(FXCollections.observableArrayList());
				ScreenLoader.showAlert("Error", "No Records to show");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the tables visibility
	 */
	@FXML
	public void initialize() {
		borrowHistoryTableView.setVisible(false);
		borrowHistoryTableView.setManaged(false);
		activityRecordsTableView.setVisible(false);
		activityRecordsTableView.setManaged(false);
		recordIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordID()));

		recordTypeColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordType()));

		recordDateColumn
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRecordDate()));

		bookCodeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookCode()));
	}

	/**
	 * Handles the click event for manually extending a borrow period.
	 *
	 * @param event the {@link ActionEvent} triggered by the button click
	 */
	@FXML
	private void onEditClick(ActionEvent event) throws InterruptedException {
		LocalDate dueDate = dueDatePicker.getValue();
		String res = "";
		Map.Entry<Integer, Map<String, String>> selectedItem = borrowHistoryTableView.getSelectionModel()
				.getSelectedItem();
		if (selectedItem != null && dueDate != null) {
			Integer borrowId = selectedItem.getKey();
			System.out.println("Selected Borrow ID: " + borrowId);
			res = bc.extendBorrowManualy(sub.getId(), borrowId, dueDate, "Manualy extend by: " + lib.getName());
			ScreenLoader.showAlert("Extend Borrow", res);
		} else {
			ScreenLoader.showAlert("Error", "Please chose a row and fill the date field before editing.");
		}
		updateBorrowHistoryTable();
	}

	/**
	 * Handles the click event for showing the borrow history.
	 *
	 * @param event the {@link ActionEvent} triggered by the button click
	 */
	@FXML
	private void onBorrowHistoryClick(ActionEvent event) {
		ScreenLoader.resizeCenterWindow(event, 800, 600);
		borrowHistoryTableView.setVisible(true);
		borrowHistoryTableView.setManaged(true);
		activityRecordsTableView.setVisible(false);
		activityRecordsTableView.setManaged(false);
		if (lib != null) {
			editHBox.setVisible(true);
			editHBox.setManaged(true);
		}
		updateBorrowHistoryTable();
	}

	/**
	 * Handles the click event for showing the activity records.
	 *
	 * @param event the {@link ActionEvent} triggered by the button click
	 */
	@FXML
	private void onActivityRecordsClick(ActionEvent event) {
		ScreenLoader.resizeCenterWindow(event, 800, 600);
		activityRecordsTableView.setVisible(true);
		activityRecordsTableView.setManaged(true);
		borrowHistoryTableView.setVisible(false);
		borrowHistoryTableView.setManaged(false);
		editHBox.setVisible(false);
		editHBox.setManaged(false);
		updateActivityRecordsTable();
	}

	/**
	 * Handles the click event for navigating back to the previous screen.
	 *
	 * @param event the {@link ActionEvent} triggered by the button click
	 */
	@FXML
	private void onBackClick(ActionEvent event) {
		if (lib != null) {
			ScreenLoader.openScreen("/gui/ManagmentSubscriberScreen.fxml", "Managment Subscriber", event,
					controller -> {
						if (controller instanceof ManagmentSubscriberController) {
							((ManagmentSubscriberController) controller).setStage(new Stage());
							((ManagmentSubscriberController) controller).setClient(c);
							((ManagmentSubscriberController) controller).setLibrarian(lib);
						}
					});

		} else {
			ScreenLoader.closeWindow(backButton);
		}
	}
}
