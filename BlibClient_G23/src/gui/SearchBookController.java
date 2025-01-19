package gui;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.TextArea;
import java.util.List;

import client.ClientMain;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import logic.Book;
import logic.CopyOfBook;
import logic.ScreenLoader;
import logic.SearchBookLogic;
import logic.CopyOfBook;

public class SearchBookController {

	@FXML
	private ComboBox<String> searchByComboBox;

	@FXML
	private TextField searchField;

	@FXML
	private Button searchButton;

	@FXML
	private Button backButton;

	@FXML
	private Button closeButton;

	@FXML
	private TextArea resultsArea;

	private Stage stage;
	private boolean loggedIn = false;
	private Book b = null;
	private ClientMain c;

	public void setStage(Stage stage) {
		this.stage = stage;

	}

	public void setLoggedIn(boolean l) {
		this.loggedIn = l;
		updateButtonsVisibility();
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	private void updateButtonsVisibility() {
		if (loggedIn) {
			closeButton.setVisible(true);
			backButton.setVisible(false);
			backButton.setManaged(false);
		} else {
			closeButton.setVisible(false);
			backButton.setVisible(true);
		}
	}

	@FXML
	public void initialize() {
		updateButtonsVisibility();
		// Initialize the ComboBox with search options
		searchByComboBox.getItems().addAll("Title", "Subject", "Free Text");
		searchByComboBox.setValue("Title"); // Set default value
	}

	@FXML
	private void onSearchBookClick(ActionEvent event) {
		String searchBy = searchByComboBox.getValue();
		String searchTerm = searchField.getText();

		if (searchTerm == null || searchTerm.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please fill all the fields of search");
			return;
		}

		try {
			// Validate search criteria
			if (searchBy == null || searchBy.isEmpty()) {
				ScreenLoader.showAlert("Error", "Please select a search category");
				return;
			}

			System.out.println("Searching for books with criteria: " + searchBy + ", Term: " + searchTerm);

			// Assuming integration with SearchBookLogic
			SearchBookLogic searchLogic = new SearchBookLogic(c);

			switch (searchBy) {
			case "Title":
				ScreenLoader.showAlert("Book Result", searchLogic.searchBookByTitle(searchTerm));
				// searchLogic.searchBookByTitle(searchTerm);
				break;

			case "Subject":
				List<Book> books = searchLogic.searchBookBySubject(searchTerm);
				if (books == null || books.isEmpty()) {
					ScreenLoader.showAlert("Info", "No books found for the search criteria");
				} else {
					System.out.println("Books found: " + books);
				}
				break;
			case "Free Text":
				books = searchLogic.searchBookByFreeText(searchTerm);
				if (books == null || books.isEmpty()) {
					ScreenLoader.showAlert("Info", "No books found for the search criteria");
				} else {
					System.out.println("Books found: " + books);
				}
				break;

			default:
				ScreenLoader.showAlert("Error", "Unknown search category: " + searchBy);
				break;
			}

		} catch (Exception e) {
			System.err.println("Error during book search: " + e.getMessage());
			ScreenLoader.showAlert("Error", "An error occurred during the search. Please try again later.");
		}
	}

	@FXML
	private void handleBack(ActionEvent event) {

		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(new Stage());
			}
		});
		// this is pop - up alert in case needed !
		// showAlert("Info", "Going back to the previous screen.");
	}

	@FXML
	private void handleClose(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}

}
