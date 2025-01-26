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
 * It provides the user interface and functionality for returning books,
 * including input validation and communication with the backend.
 */
public class ReturnBookController {

	@FXML
	private TextField bookField;

	@FXML
	private Button scanBookButton;

	@FXML
	private Button returnButton;

	@FXML
	private Button backButton;

	private Stage stage;

	private LocalDate todayDate = LocalDate.now(); // The current date, used for return validation.
	private Librarian lib; // The librarian currently managing the process.
	private ClientMain cm;

	/**
	 * Sets the stage for the current screen.
	 * 
	 * @param stage the current {@link Stage}
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the client instance for backend communication.
	 * 
	 * @param cm the client instance of type {@link ClientMain}
	 */
	public void setClient(ClientMain cm) {
		this.cm = cm;
	}

	/**
	 * Sets the librarian managing the return process.
	 * 
	 * @param lib the {@link Librarian} managing the process
	 */
	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	/**
	 * Handles the event when the user presses Enter in the book field. Validates
	 * that the field is not empty.
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
	 * Handles the event when the return button is clicked. Validates the input and
	 * initiates the return process by communicating with the backend.
	 *
	 * @param event the ActionEvent triggered by the user.
	 */
	@FXML
	private void onReturnClick(ActionEvent event) {
		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a book name or code.");
		}
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
	 * Simulates the scanning of a book (not implemented). Displays a placeholder
	 * alert to the user.
	 *
	 * @param event the ActionEvent triggered by the user.
	 */
	@FXML
	private void onScanBook(ActionEvent event) {
		// there is no implementation for scan we dont have access to scanner :(
		ScreenLoader.showAlert("Scan Book", "Scanning book...");
	}

	/**
	 * Handles the action of navigating back to the Librarian Main Screen.
	 *
	 * @param event the ActionEvent triggered by the user
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
