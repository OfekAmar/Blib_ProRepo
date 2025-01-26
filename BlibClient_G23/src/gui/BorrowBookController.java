package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.time.LocalDate;

import client.ClientMain;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.BorrowController;
import logic.Librarian;
import logic.ScreenLoader;
import logic.SubscriberController;

/**
 * Controller for managing the borrowing of books in the library system.
 */
public class BorrowBookController {

	@FXML
	private TextField subscriberField;

	@FXML
	private TextField bookField;

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
	private Librarian lib;
	private ClientMain cm;

	/**
	 * Sets the stage for the controller.
	 *
	 * @param stage the current stage of the application
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the librarian for the controller.
	 *
	 * @param lib the librarian object
	 */
	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	/**
	 * Sets the client for the controller.
	 *
	 * @param cm the {@link ClientMain} instance
	 */
	public void setClient(ClientMain cm) {
		this.cm = cm;
	}

	/**
	 * Handles the acceptance of a borrowing request.
	 * 
	 * @param event the action event
	 */
	@FXML
	private void onAcceptClick(ActionEvent event) {
		String subscriberInfo = subscriberField.getText();
		if (subscriberInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name or ID.");
			return;
		}

		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a book name or code.");
			return;
		}

		// Check for date selection
		LocalDate selectedDate = datePicker.getValue();
		LocalDate currentDate = LocalDate.now();
		if (selectedDate == null) {
			ScreenLoader.showAlert("Error", "Please select a valid due date.");
			return;
		}

		// Validate that the selected date is not before the current date
		if (selectedDate.isBefore(currentDate)) {
			ScreenLoader.showAlert("Error", "The selected due date cannot be in the past.");
			return;
		}
		// Validate the bookInfo format (book_code/copy_id)
		if (!bookInfo.matches("^\\d+/\\d+$")) {
			ScreenLoader.showAlert("Error",
					"Invalid book information format. Please use the format: book_code/copy_id (e.g., 123/1).");
			return;
		}

		// Validate that the selected date is not more than 14 days from the current
		// date
		if (selectedDate.isAfter(currentDate.plusDays(14))) {
			ScreenLoader.showAlert("Error", "The selected due date cannot be more than 14 days from today.");
			return;
		}
		String[] parts = bookInfo.split("/");

		// Extract the book code and copy ID
		String bookCode = parts[0]; // Before the '/'
		String copyId = parts[1]; // After the '/'

		SubscriberController sc = new SubscriberController(cm);

		BorrowController borrowController = new BorrowController(sc, cm);
		try {
			String response = borrowController.processBorrow(bookCode, copyId, subscriberInfo, selectedDate);
			ScreenLoader.showAlert("Allert", response);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Action event handler for scanning subscriber information.
	 * 
	 * @param event the action event
	 */

	@FXML
	private void onScanSubscriber(ActionEvent event) {
		// there is no implementation for scan we dont have access to scanner :(
		ScreenLoader.showAlert("Scan Subscriber", "Scanning subscriber card...");
	}

	/**
	 * Action event handler for scanning book information.
	 * 
	 * @param event the action event
	 */
	@FXML
	private void onScanBook(ActionEvent event) {
		// there is no implementation for scan we dont have access to scanner :(
		ScreenLoader.showAlert("Scan Book", "Scanning book...");
	}

	/**
	 * Navigates back to the librarian main screen.
	 * 
	 * @param event the action event
	 */
	@FXML
	private void onBackToMain(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
				((LibrarianMainController) controller).setClient(cm);
				((LibrarianMainController) controller).setLibrarian(lib);
			}
		});
	}

}