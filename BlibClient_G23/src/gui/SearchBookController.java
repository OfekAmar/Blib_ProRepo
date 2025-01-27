package gui;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.awt.TextArea;
import java.awt.print.PrinterException;
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

/**
 * The `SearchBookController` class handles the book search functionality in the
 * application. It provides a user interface for searching books by title,
 * subject, or free text, and displays the results to the user.
 */
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
	@FXML
	private ListView<String> searchResultList;

	private ObservableList<String> obslist;
	private Stage stage;
	private boolean loggedIn = false;
	private Book b = null;
	private ClientMain c;
	private boolean searchbuttonflag = true;
	private List<Book> books;

	/**
	 * Sets the stage for the current screen.
	 * 
	 * @param stage the current {@link Stage}
	 */
	public void setStage(Stage stage) {
		this.stage = stage;

	}

	/**
	 * Sets the login status and updates button visibility based on it.
	 * 
	 * @param l the logged-in status of the user
	 */
	public void setLoggedIn(boolean l) {
		this.loggedIn = l;
		updateButtonsVisibility();
	}

	/**
	 * Sets the client instance used for communication.
	 * 
	 * @param c the client instance of type {@link ClientMain}
	 */
	public void setClient(ClientMain c) {
		this.c = c;
	}

	/**
	 * Updates the visibility of the buttons based on the user's logged-in status.
	 */
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

	/**
	 * Updates the visibility of the search results list.
	 *
	 * @param isVisible true to show the list, false to hide it
	 */
	private void updateListVisibility(boolean b) {
		if (b) {
			searchResultList.setVisible(true);
			searchResultList.setManaged(true);
		} else {
			searchResultList.setVisible(false);
			searchResultList.setManaged(false);
		}
	}

	/**
	 * Initializes the screen, setting up the search options and button visibility.
	 */
	@FXML
	public void initialize() {
		updateButtonsVisibility();
		updateListVisibility(false);
		// Initialize the ComboBox with search options
		searchByComboBox.getItems().addAll("Title", "Subject", "Free Text");
		searchByComboBox.setValue("Title"); // Set default value
	}

	/**
	 * Handles the search functionality based on the selected criteria and search
	 * term.
	 *
	 * @param event the triggered event
	 */
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
			if (!searchbuttonflag) {
				int selectedIndex = searchResultList.getSelectionModel().getSelectedIndex();
				if (selectedIndex != -1) {
					int titlesearch = books.get(selectedIndex).getId();
					ScreenLoader.showAlert("Book Result", searchLogic.getLocation(titlesearch));
					searchbuttonflag = true;
				} else {
					searchbuttonflag = true;
					updateListVisibility(false);
					onSearchBookClick(event);
					return;
				}

			} else {
				switch (searchBy) {
				case "Title":
					books = searchLogic.searchBookByTitle(searchTerm);
					if (books == null || books.isEmpty()) {
						ScreenLoader.showAlert("Info", "No books found for the search criteria");
					} else {
						searchbuttonflag = false;
						obslist = FXCollections.observableArrayList();
						searchResultList.setItems(obslist);
						for (Book b : books) {
							obslist.add(b.getTitle() + " By: " + b.getAuthor() + " Description: " + b.getDescription());
						}
						updateListVisibility(true);
						ScreenLoader.resizeCenterWindow(event, 600, 400);
						searchResultList.setPrefWidth(500);

					}
					break;

				case "Subject":
					books = searchLogic.searchBookBySubject(searchTerm);
					if (books == null || books.isEmpty()) {
						ScreenLoader.showAlert("Info", "No books found for the search criteria");
					} else {
						searchbuttonflag = false;
						obslist = FXCollections.observableArrayList();
						searchResultList.setItems(obslist);
						for (Book b : books) {
							obslist.add(b.getTitle() + " By: " + b.getAuthor() + " Description: " + b.getDescription());
						}
						updateListVisibility(true);
						ScreenLoader.resizeCenterWindow(event, 600, 400);
						searchResultList.setPrefWidth(500);

					}
					break;
				case "Free Text":
					books = searchLogic.searchBookByFreeText(searchTerm);
					if (books == null || books.isEmpty()) {
						ScreenLoader.showAlert("Info", "No books found for the search criteria");
					} else {
						searchbuttonflag = false;
						obslist = FXCollections.observableArrayList();
						searchResultList.setItems(obslist);
						for (Book b : books) {
							obslist.add(b.getTitle() + " By: " + b.getAuthor() + " Description: " + b.getDescription());
						}
						updateListVisibility(true);
						ScreenLoader.resizeCenterWindow(event, 600, 400);
						searchResultList.setPrefWidth(500);
					}
					break;

				default:
					ScreenLoader.showAlert("Error", "Unknown search category: " + searchBy);
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error during book search: " + e.getMessage());
			ScreenLoader.showAlert("Error", "An error occurred during the search. Please try again later.");
		}
	}

	/**
	 * Handles the action of going back to the previous screen when the "Back"
	 * button is clicked.
	 *
	 * @param event the triggered event when the "Back" button is clicked
	 */
	@FXML
	private void handleBack(ActionEvent event) {

		ScreenLoader.openScreen("/gui/LoginScreen.fxml", "Login Screen", event, controller -> {
			if (controller instanceof LoginController) {
				((LoginController) controller).setStage(new Stage());
				((LoginController) controller).setClient(c);
			}
		});
	}

	/**
	 * Handles the action of closing the current window when the "Close" button is
	 * clicked.
	 *
	 * @param event the triggered event when the "Close" button is clicked
	 */
	@FXML
	private void handleClose(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}

}
