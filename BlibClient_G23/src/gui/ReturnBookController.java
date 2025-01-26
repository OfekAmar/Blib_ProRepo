package gui;

import java.time.LocalDate;

import client.ClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.Book;
import logic.BorrowController;
import logic.Librarian;
import logic.ReturnController;
import logic.ScreenLoader;
import logic.SubscriberController;
/**
 * The ReturnBookController class handles the return process of borrowed books.
 * It provides the user interface and functionality for returning books, including
 * input validation and communication with the backend.
 */
public class ReturnBookController {
	/**
     * TextField for entering the book information.
     */
	@FXML
	private TextField bookField;
	 /**
     * Button for scanning the book's information (not implemented).
     */
	@FXML
	private Button scanBookButton;
	/**
     * Button for confirming the return process.
     */
	@FXML
	private Button returnButton;
	 /**
     * Button for navigating back to the main screen.
     */
	@FXML
	private Button backButton;
	 /**
     * The stage used for displaying the user interface.
     */
	private Stage stage;
	
	private LocalDate todayDate = LocalDate.now();  // The current date, used for return validation.
	private Librarian lib; //The librarian currently managing the process.

	/**
     * Sets the stage for this controller.
     *
     * @param stage the Stage object to set.
     */
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private ClientMain cm;  //The client instance for communicating with the backend
	/**
     * Sets the client instance for backend communication.
     *
     * @param cm the ClientMain object to set.
     */
	public void setClient(ClientMain cm) {
		this.cm = cm;
	}
	/**
     * Sets the librarian currently managing the process.
     *
     * @param lib the Librarian object to set.
     */
	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}
	 /**
     * Handles the event when the user presses Enter in the book field.
     * Validates that the field is not empty.
     *
     * @param event the ActionEvent triggered by the user.
     */
	@FXML
	private void onEnterBook(ActionEvent event) {
		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a book name or code.");
		
		}
	}
	 /**
     * Handles the event when the return button is clicked.
     * Validates the input and initiates the return process by communicating with the backend.
     *
     * @param event the ActionEvent triggered by the user.
     */
	@FXML
	private void onReturnClick(ActionEvent event) {
		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a book name or code.");
		}

		// implement the logic of return book
		// add copy to database add return record and goes on....
		// the details of book name and subscriber are in the variables subscriberInfo
		// variables subscriberInfo and bookInfo
		// the due date to the return is in todayDate variable

		String[] parts = bookInfo.split("/");

		// Extract the book code and copy ID
		String bookCode = parts[0]; // Before the '/'
		String copyId = parts[1]; // After the '/'

		SubscriberController sc = new SubscriberController(cm);

		ReturnController returnController = new ReturnController(cm);
		try {
			ScreenLoader.showAlert("Success", returnController.returnBook(bookCode, copyId));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
     * Simulates the scanning of a book (not implemented).
     * Displays a placeholder alert to the user.
     *
     * @param event the ActionEvent triggered by the user.
     */
	@FXML
	private void onScanBook(ActionEvent event) {
		// there is no implementation for scan we dont have access to scanner :(
		ScreenLoader.showAlert("Scan Book", "Scanning book...");
	}
	 /**
     * Navigates back to the main librarian screen.
     *
     * @param event the ActionEvent triggered by the user.
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
