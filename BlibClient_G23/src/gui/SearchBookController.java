package gui;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private TableView<Book> booksTable;

	@FXML
	private TableColumn<Book, String> titleColumn;

	@FXML
	private TableColumn<Book, String> authorColumn;

	@FXML
	private TableColumn<Book, Integer> descColumn;

	@FXML
	private ComboBox<String> searchByComboBox;

	@FXML
	private TextField searchField;

	@FXML
	private Button searchButton;

	@FXML
	private Button getLocationButton;

	@FXML
	private Button backButton;

	@FXML
	private Button closeButton;

	@FXML
	private TextArea resultsArea;

	private ObservableList<Book> obslist = FXCollections.observableArrayList();
	private Stage stage;
	private boolean loggedIn = false;
	private Book b = null;
	private ClientMain c;
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
	 * Initializes the screen, setting up the search options and button visibility.
	 */
	@FXML
	public void initialize() {
		updateButtonsVisibility();
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
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
		descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		booksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		booksTable.setVisible(true);
		booksTable.setManaged(true);
		getLocationButton.setVisible(true);
		getLocationButton.setManaged(true);

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
				books = searchLogic.searchBookByTitle(searchTerm);
				if (books == null || books.isEmpty()) {
					ScreenLoader.showAlert("Info", "No books found for the search criteria");
				} else {
					obslist.setAll(books);
					booksTable.setItems(obslist);
					booksTable.setVisible(true);
					booksTable.setManaged(true);
					booksTable.setPrefWidth(500);
					ScreenLoader.resizeCenterWindow(event, 600, 400);

				}
				break;

			case "Subject":
				books = searchLogic.searchBookBySubject(searchTerm);
				if (books == null || books.isEmpty()) {
					ScreenLoader.showAlert("Info", "No books found for the search criteria");
				} else {
					obslist.setAll(books);
					booksTable.setItems(obslist);
					booksTable.setVisible(true);
					booksTable.setManaged(true);
					booksTable.setPrefWidth(500);
					ScreenLoader.resizeCenterWindow(event, 600, 400);

				}
				break;
			case "Free Text":
				books = searchLogic.searchBookByFreeText(searchTerm);
				if (books == null || books.isEmpty()) {
					ScreenLoader.showAlert("Info", "No books found for the search criteria");
				} else {
					obslist = FXCollections.observableArrayList();
					obslist.setAll(books);
					booksTable.setItems(obslist);
					booksTable.setVisible(true);
					booksTable.setManaged(true);
					booksTable.setPrefWidth(500);
					ScreenLoader.resizeCenterWindow(event, 600, 400);
				}
				break;

			default:
				ScreenLoader.showAlert("Error", "Unknown search category: " + searchBy);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error during book search: " + e.getMessage());
			ScreenLoader.showAlert("Error", "An error occurred during the search. Please try again later.");
		}
	}

	/**
	 * Handles the "Get Location" action triggered by the user. This method
	 * retrieves the location of the selected book in the table and displays it
	 * using an alert. If no book is selected, it triggers a search action.
	 *
	 * @param event the {@link ActionEvent} triggered by the user's interaction.
	 */
	@FXML
	private void onGetLocation(ActionEvent event) {
		SearchBookLogic searchLogic = new SearchBookLogic(c);
		Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
		try {
			if (selectedBook != null) {
				ScreenLoader.showAlert("Book Result", searchLogic.getLocation(selectedBook.getId()));
			} else {
				onSearchBookClick(event);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
