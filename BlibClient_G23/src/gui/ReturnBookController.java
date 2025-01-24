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
	private LocalDate todayDate = LocalDate.now();
	private Librarian lib;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private ClientMain cm;

	public void setClient(ClientMain cm) {
		this.cm = cm;
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	@FXML
	private void onEnterBook(ActionEvent event) {
		String bookInfo = bookField.getText();
		if (bookInfo.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a book name or code.");
		
		}
	}

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
			returnController.returnBook(bookCode, copyId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				((LibrarianMainController) controller).setClient(cm);
				((LibrarianMainController) controller).setLibrarian(lib);
			}
		});
	}

}
