package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

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
import logic.Record;

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

	@FXML
	private LineChart<String, Number> statusLineChart;

	@FXML
	private CategoryAxis lineChartXAxis;

	@FXML
	private NumberAxis lineChartYAxis;

	private Stage stage;
	private ClientMain c;
	private ReportController r;
	private List<ExtendedRecord> monthleyBorrowReport;
	private List<Record> monthlyStatusReport;
	private Map<String, Integer> statusStatistics;

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
			monthleyBorrowReport = r.getMonthlyBorrowReport(d.getMonthValue(), d.getYear());
			monthlyStatusReport = r.getMonthlyStatusReport(d.getMonthValue(), d.getYear());
			statusStatistics = r.getStatusStatistics();
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

		statusLineChart.setVisible(false);
		statusLineChart.setManaged(false);

		borrowingPieChart.setVisible(true);
		borrowingPieChart.setManaged(true);

		subscriberBarChart.setVisible(true);
		subscriberBarChart.setManaged(true);

		Map<String, Integer> actionCounts = new HashMap<>();
		for (ExtendedRecord record : monthleyBorrowReport) {
			String status = record.getReturnStatus();
			actionCounts.put(status, actionCounts.getOrDefault(status, 0) + 1);
		}

		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
		for (Map.Entry<String, Integer> entry : actionCounts.entrySet()) {
			pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
		}
		borrowingPieChart.setData(pieData);

		Map<Integer, Integer> borrowCounts = new HashMap<>();
		for (ExtendedRecord record : monthleyBorrowReport) {
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
		Stage st = (Stage) ((Node) event.getSource()).getScene().getWindow();
		st.setWidth(800);
		st.setHeight(1200);

		statusLineChart.setVisible(true);
		statusLineChart.setManaged(true);
		borrowingPieChart.setVisible(true);
		borrowingPieChart.setManaged(true);

		subscriberBarChart.setVisible(false);
		subscriberBarChart.setManaged(false);

		borrowingPieChart.getData().clear();

		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
		for (Map.Entry<String, Integer> entry : statusStatistics.entrySet()) {
			pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
		}
		borrowingPieChart.setData(pieData);
		borrowingPieChart.setTitle("Subscribers Status Distribution");

		statusLineChart.getData().clear();

		Map<String, Integer> cumulativeFreeze = new HashMap<>();
		Map<String, Integer> cumulativeUnfreeze = new HashMap<>();

		int freezeCount = 0;
		int unfreezeCount = 0;

		for (Record record : monthlyStatusReport) {
			String date = record.getRecordDate();
			if (record.getRecordType().equals("freeze")) {
				freezeCount++;
			} else if (record.getRecordType().equals("unfreeze")) {
				unfreezeCount++;
			}
			cumulativeFreeze.put(date, freezeCount);
			cumulativeUnfreeze.put(date, unfreezeCount);
		}
		List<String> allDates = cumulativeFreeze.keySet().stream().distinct().sorted().toList();

		XYChart.Series<String, Number> freezeSeries = new XYChart.Series<>();
		freezeSeries.setName("Freeze");

		XYChart.Series<String, Number> unfreezeSeries = new XYChart.Series<>();
		unfreezeSeries.setName("Unfreeze");

		for (String date : allDates) {
			freezeSeries.getData().add(new XYChart.Data<>(date, cumulativeFreeze.getOrDefault(date, 0)));
			unfreezeSeries.getData().add(new XYChart.Data<>(date, cumulativeUnfreeze.getOrDefault(date, 0)));
		}

		statusLineChart.getData().addAll(freezeSeries, unfreezeSeries);
		statusLineChart.setTitle("Cumulative Freeze and Unfreeze Actions");
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
