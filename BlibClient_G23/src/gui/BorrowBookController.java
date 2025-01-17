package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import logic.ScreenLoader;

public class BorrowBookController {

	@FXML
	private TextField subscriberField;

	@FXML
	private TextField bookField;

	@FXML
	private Button enterSubscriberButton;

	@FXML
	private Button enterBookButton;

	@FXML
	private Button scanSubscriberButton;

	@FXML
	private Button scanBookButton;

	@FXML
	private Button acceptButton;

	@FXML
	private Button backButton;

	@FXML
	private DatePicker datePicker;

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void onEnterSubscriber(ActionEvent event) {
		String subscriberInfo = subscriberField.getText();
		if (subscriberInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name or ID.");
		} else {
			// need to verify in database that therre is subscriber like entered
			// delete the show alert afte the implemention !!!
			ScreenLoader.showAlert("Success", "Subscriber information submitted: " + subscriberInfo);
		}
	}

	@FXML
	private void onEnterBook(ActionEvent event) {
		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a book name or code.");
		} else {
			// need to verify that there is copy of the book enterd to borrow
			// delete the show alert afte the implemention !!!
			ScreenLoader.showAlert("Success", "Book information submitted: " + bookInfo);
		}
	}

	@FXML
	private void onAcceptClick(ActionEvent event) {
		// check for date selected
		LocalDate selectedDate = datePicker.getValue();
		if (selectedDate != null) {
			ScreenLoader.showAlert("Date Selected", "You selected: " + selectedDate.toString());
		} else {
			ScreenLoader.showAlert("Error", "Please select a valid date.");
		}
		// implement the logic of borrow book
		// remove copy from database add borrow record and goes on....
		// the details of book name and subscriber are in the variables subscriberInfo
		// variables subscriberInfo and bookInfo
		// the due date to borrow is in the variable selectedDate

	}

	@FXML
	private void onScanSubscriber(ActionEvent event) {
		// there is no implementation for scan we dont have access to scanner :(
		ScreenLoader.showAlert("Scan Subscriber", "Scanning subscriber card...");
	}

	@FXML
	private void onScanBook(ActionEvent event) {
		// there is no implementation for scan we dont have access to scanner :(
		ScreenLoader.showAlert("Scan Book", "Scanning book...");
	}

	@FXML
	private void onBackToMain(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
			}
		});
	}

}