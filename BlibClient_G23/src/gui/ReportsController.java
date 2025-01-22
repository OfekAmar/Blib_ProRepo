package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.ClientMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ExtendedRecord;
import logic.ReportController;
import logic.ScreenLoader;

public class ReportsController {

	@FXML
	private Button borrowingDurationButton;

	@FXML
	private Button subscribersStatusButton;

	@FXML
	private Button backButton;
	@FXML
	private PieChart borrowingPieChart;

	@FXML
	private BarChart<String, Number> subscriberBarChart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	private Stage stage;
	private ClientMain c;
	private ReportController r;
	private List<ExtendedRecord> monthleyReport;
	private List<Record> statsReport;

	public void setClient(ClientMain c) {
		this.c = c;
		this.r = new ReportController(c);
		initializeReportData();

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private void initializeReportData() {
		LocalDate d = LocalDate.now();
		try {
			monthleyReport = r.getMonthlyBorrowReport(d.getMonthValue(), d.getYear());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void onBorrowingDurationClick(ActionEvent event) {

		Stage st = (Stage) ((Node) event.getSource()).getScene().getWindow();
		st.setWidth(800);
		st.setHeight(1200);

		borrowingPieChart.setVisible(true);
		borrowingPieChart.setManaged(true);

		subscriberBarChart.setVisible(true);
		subscriberBarChart.setManaged(true);

		Map<String, Integer> actionCounts = new HashMap<>();
		for (ExtendedRecord record : monthleyReport) {
			String status = record.getReturnStatus();
			actionCounts.put(status, actionCounts.getOrDefault(status, 0) + 1);
		}

		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
		for (Map.Entry<String, Integer> entry : actionCounts.entrySet()) {
			pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
		}
		borrowingPieChart.setData(pieData);

		// מילוי נתונים לתרשים עמודות
		Map<Integer, Integer> borrowCounts = new HashMap<>();
		for (ExtendedRecord record : monthleyReport) {
			int subscriberId = record.getSubscriberID();
			borrowCounts.put(subscriberId, borrowCounts.getOrDefault(subscriberId, 0) + 1);
		}

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Books Borrowed");

		for (Map.Entry<Integer, Integer> entry : borrowCounts.entrySet()) {
			series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
		}
		subscriberBarChart.getData().clear();
		subscriberBarChart.getData().add(series);
	}

	@FXML
	private void onSubscribersStatusClick(ActionEvent event) {
		// Implement logic for subscribers status reports
		ScreenLoader.showAlert("Subscribers Status Reports", "Displaying Subscribers Status Reports.");
	}

	@FXML
	private void onBackToMainClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
				((LibrarianMainController) controller).setClient(c);
			}
		});
	}
}
