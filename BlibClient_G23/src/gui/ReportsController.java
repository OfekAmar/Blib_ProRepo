package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;

import client.ClientMain;
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

	private Stage stage;
	private ClientMain c;
	private ReportController r;
	private List<ExtendedRecord> monthleyReport;
	private List<Record> statsReport;

	public void setClient(ClientMain c) {
		this.c = c;
		this.r = new ReportController(c);

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
		// Implement logic for borrowing duration reports
		ScreenLoader.showAlert("Borrowing Duration Reports", "Displaying Borrowing Duration Reports.");
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
			}
		});
	}
}
