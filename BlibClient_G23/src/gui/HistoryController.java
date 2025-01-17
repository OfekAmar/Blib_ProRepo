package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Subscriber;

import javafx.event.ActionEvent;
import java.sql.ResultSet;

public class HistoryController {

	@FXML
	private TableView<ObservableList<String>> historyTable;

	@FXML
	private TableColumn<ObservableList<String>, String> bookTitleColumn;

	@FXML
	private TableColumn<ObservableList<String>, String> borrowDateColumn;

	@FXML
	private TableColumn<ObservableList<String>, String> returnDateColumn;

	@FXML
	private Button backButton;

	private final ObservableList<ObservableList<String>> historyEntries = FXCollections.observableArrayList();

	private Stage stage;
	private Subscriber sub;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	@FXML
	public void initialize() {
		// Set up the table columns
		bookTitleColumn.setCellValueFactory(
				cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().get(0)));
		borrowDateColumn.setCellValueFactory(
				cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().get(1)));
		returnDateColumn.setCellValueFactory(
				cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().get(2)));

		// Load data into the table
		loadHistoryData();
		historyTable.setItems(historyEntries);
	}

	private void loadHistoryData() {
		// need to implement fetching the history information from
		// the database after that to add the data into the table coloms
		// like so:

		ResultSet resultSet = null;
		try {
			while (resultSet.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();
				row.add(resultSet.getString("book_title"));
				row.add(resultSet.getString("borrow_date"));
				row.add(resultSet.getString("return_date"));
				historyEntries.add(row);
			}
		} catch (Exception e) {
		}
	}

	@FXML
	private void onBackToMainClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/SubscriberMainScreen.fxml", "Subscriber Screen", event, controller -> {
			if (controller instanceof SubscriberMainController) {
				((SubscriberMainController) controller).setSubscriberName(sub.getName());
			}
		});
	}
}
