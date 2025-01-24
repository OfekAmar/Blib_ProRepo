package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.SubscriberController;

import java.util.List;
import java.util.Map;

import client.ClientMain;

import java.util.ArrayList;

public class BorrowHistoryController {

	@FXML
	private TextField searchField;

	@FXML
	private Button searchButton;

	@FXML
	private TableView<Map.Entry<Integer, Map<String, String>>> borrowHistoryTableView;

	@FXML
	private TableColumn<Map.Entry<Integer, Map<String, String>>, Integer> borrowIdColumn;

	@FXML
	private TableColumn<Map.Entry<Integer, Map<String, String>>, String> titleColumn, borrowDateColumn,
			returnDateColumn;
	@FXML
	private Button backButton;
	@FXML
	private Button editButton;

	private ObservableList<String> obslist;
	private Subscriber sub;
	private List<String> subscribers; // Example list of subscribers (replace with database integration)
	private Stage stage;
	private ArrayList<String> result;
	private ClientMain c;
	private SubscriberController sc;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
		backButton.setText("close");
	}

	public void setClientMain(ClientMain c) {
		this.c = c;
	}

	public void setClientAndSubscrber(Subscriber s, ClientMain c) {
		this.sub = s;
		backButton.setText("close");
		this.c = c;
		sc = new SubscriberController(c);
		updateList();
	}

	private void updateList() {
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

	@FXML
	public void initialize() {

	}

	@FXML
	private void onEditClick(ActionEvent event) {

	}

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.closeWindow(backButton);
	}
}