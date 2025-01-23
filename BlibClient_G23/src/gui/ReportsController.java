package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.IntStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.ClientMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ExtendedRecord;
import logic.Librarian;
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
	private Button makeReportsButton;

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
	@FXML
	private ComboBox<String> monthComboBox;

	@FXML
	private ComboBox<Integer> yearComboBox;

	private Stage stage;
	private ClientMain c;
	private Librarian lib;
	private ReportController r;
	private List<ExtendedRecord> monthleyBorrowReport;
	private List<Record> monthlyStatusReport;
	private Map<String, Integer> statusStatistics;
	private boolean selected = false;

	public void setClient(ClientMain c) {
		this.c = c;
		this.r = new ReportController(c);
		initializeComboBox();

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	private void initializeComboBox() {
		monthComboBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December");
		IntStream.rangeClosed(2020, 2025).forEach(year -> yearComboBox.getItems().add(year));

	}

	private void initializeReportData(int month, int year) {
		try {
			monthleyBorrowReport = r.getMonthlyBorrowReport(month, year);
			monthlyStatusReport = r.getMonthlyStatusReport(month, year);
			statusStatistics = r.getStatusStatistics();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onMakeReportsClick() {
		int selectedMonth = monthComboBox.getSelectionModel().getSelectedIndex();
		Integer selectedYear = yearComboBox.getValue();

		if (selectedMonth == -1 || selectedYear == null) {
			ScreenLoader.showAlert("Error", "Please select both month and year.");
			return;
		}

		selected = true;
		initializeReportData(selectedMonth + 1, selectedYear);
	}

	@FXML
	private void onBorrowingDurationClick(ActionEvent event) {

		if (!selected)
			return;

		ScreenLoader.resizeCenterWindow(event, 800, 1200);

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
		borrowingPieChart.setTitle("Monthly Returened Book Status Distribution");

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
		subscriberBarChart.setTitle("Monthly Amount Of Borrowing Per Subscriber");
	}

	@FXML
	private void onSubscribersStatusClick(ActionEvent event) {

		if (!selected)
			return;

		ScreenLoader.resizeCenterWindow(event, 800, 1200);

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
		statusLineChart.setTitle("Monthly Freeze and Unfreeze Actions");
	}

	@FXML
	private void onBackToMainClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
				((LibrarianMainController) controller).setClient(c);
				((LibrarianMainController) controller).setLibrarian(lib);
			}
		});
	}
}
