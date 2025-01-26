package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.text.TabableView;

import client.ClientMain;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import logic.Librarian;
import logic.ScreenLoader;
import logic.Book;
import logic.BookController;
import logic.CopyOfBook;

/**
 * The `ManagmentBookController` class manages the functionality of the Book
 * Management screen. This screen allows librarians to view, add, edit, and
 * search for books and their copies.
 */
public class ManagmentBookController {
	@FXML
	private TableView<Book> booksTable;

	@FXML
	private TableView<CopyOfBook> copiesTable;

	@FXML
	private TableColumn<Book, String> titleColumn;

	@FXML
	private TableColumn<Book, Integer> idColumn;

	@FXML
	private TableColumn<Book, String> authorColumn;

	@FXML
	private TableColumn<Book, String> subjectColumn;

	@FXML
	private TableColumn<Book, String> descriptionColumn;

	@FXML
	private TableColumn<Book, Integer> totalCopiesColumn;

	@FXML
	private TableColumn<CopyOfBook, Integer> copyIDColumn;

	@FXML
	private TableColumn<CopyOfBook, String> locationColumn;

	@FXML
	private TableColumn<CopyOfBook, String> statusColumn;

	@FXML
	private Button addNewBookButton;

	@FXML
	private Button viewCopiesButton;

	@FXML
	private Button scanBookBarcodeButton;

	@FXML
	private Button searchButton;

	@FXML
	private Button backButton;

	@FXML
	private Button addCopyButton;

	@FXML
	private Button editCopyButton;

	@FXML
	private Button resetSearchButton;

	@FXML
	private TextField searchField;

	private Stage stage;
	private ClientMain c;
	private Librarian lib;
	private BookController bc;
	private Book searchBook = null;
	private ObservableList<Book> booksList = FXCollections.observableArrayList();
	private ObservableList<CopyOfBook> copiesList = FXCollections.observableArrayList();

	/**
	 * Sets the current stage (window) for the controller.
	 * 
	 * @param stage the current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the client instance for the controller and loads the books into the
	 * table.
	 * 
	 * @param c the {@link ClientMain} instance used for server communication
	 */
	public void setClient(ClientMain c) {
		this.c = c;
		bc = new BookController(c);
		loadBooksToTable();
	}

	/**
	 * Loads the list of books into the table view by fetching data from the server.
	 * Uses {@link BookController} to get the list of all books and populate the
	 * table.
	 */
	public void setLibrarian(Librarian lib) {
		this.lib = lib;
		// welcomeLabel.setText("Hello, " + lib.getName() + "!");
	}

	/**
	 * Loads the list of books into the table view. Fetches data from the server
	 * using the BookController.
	 */
	@FXML
	public void loadBooksToTable() {
		System.out.println("initizlize called");
		if (bc == null) {
			System.out.println("bc not initialized. check setClient");
			return;
		}
		//
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
		subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		totalCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));

		booksTable.setItems(booksList);

		Platform.runLater(() -> {
			try {
				List<Book> books = bc.getAllBooks();
				booksList.setAll(books);
			} catch (InterruptedException e) {
				e.printStackTrace();
				ScreenLoader.showAlert("Error", "Failed to load books data.");
			}
		});
	}

	/**
	 * Handles adding a new book. Opens the "Add New Book" pop-up screen.
	 *
	 * @param event the event triggered by clicking the "Add New Book" button
	 */
	@FXML
	private void onAddBookClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/NewBookScreen.fxml", "Add new book", event, controller -> {
			if (controller instanceof NewBookController) {
				NewBookController newBookController = (NewBookController) controller;
				((NewBookController) controller).setStage(new Stage());
				((NewBookController) controller).setClient(c);

				newBookController.setOnBookAddedCallback(newBook -> {
					if (booksList != null) {
						booksList.add(newBook);
						Platform.runLater(() -> booksTable.refresh());
					} else {
						System.out.println("bookList is null");
					}

				});
			}
		});
	}

	/**
	 * Handles viewing copies of the selected book. Loads the list of copies into
	 * the copies table.
	 *
	 * @param event the event triggered by clicking the "View Copies" button
	 */
	@FXML
	private void onViewCopiesClick(ActionEvent event) {
		copyIDColumn.setCellValueFactory(new PropertyValueFactory<>("copyID"));
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

		copiesTable.setItems(copiesList);

		Book selectedBook = booksTable.getSelectionModel().getSelectedItem();

		if (selectedBook == null) {
			ScreenLoader.showAlert("Error", "Select a book to view its copies.");
			return;
		}
		copiesTable.setVisible(true);
		copiesTable.setManaged(true);

		addCopyButton.setVisible(true);
		addCopyButton.setManaged(true);

		editCopyButton.setVisible(true);
		editCopyButton.setManaged(true);

		Platform.runLater(() -> {
			try {
				List<CopyOfBook> copies = bc.getAllBookCopies(selectedBook.getId());
				copiesList.setAll(copies);
			} catch (Exception e) {
				e.printStackTrace();
				ScreenLoader.showAlert("Error", "Failed to fetch");
			}
		});
	}

	/**
	 * Handles the scan book functionality. Searches for a book by its ID.
	 *
	 * @param event the event triggered by clicking the "Search" button
	 */
	@FXML
	private void onScanBookBarcodeClick(ActionEvent event) {
		ScreenLoader.showAlert("Scan Book", "Scan book please");
		// not able to implement scan
	}

	/**
	 * Handles the search functionality. Searches for a book by its ID.
	 *
	 * @param event the event triggered by clicking the "Search" button
	 */
	@FXML
	private void onSearchBookClick(ActionEvent event) throws InterruptedException {
		String bookId = searchField.getText();

		if (bookId.isEmpty()) {
			ScreenLoader.showAlert("Error", "Enter book code.");
			return;
		}

		try {
			searchBook = bc.getBookByCode(Integer.valueOf(bookId));

			if (searchBook != null) {
				booksList.clear();
				booksList.add(searchBook);
				booksTable.refresh();
				System.out.println("Book found: " + searchBook);
			} else {
				ScreenLoader.showAlert("Error", "Book not found");
			}
		} catch (NumberFormatException e) {
			ScreenLoader.showAlert("Error", "Book Id must be a number.");
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to fetch book.");
		}
	}

	/**
	 * Resets the search results and reloads all books.
	 *
	 * @param event the event triggered by clicking the "Reset Search" button
	 */
	@FXML
	private void onResetSearchClick(ActionEvent event) {
		try {
			List<Book> books = bc.getAllBooks();
			booksList.setAll(books);
			searchField.setText("");
			copiesTable.setVisible(false);
			copiesTable.setManaged(false);
			booksTable.refresh();
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to fetch");
		}
	}

	/**
	 * Handles adding a new copy of the selected book. Opens the "Add New Copy"
	 * pop-up screen.
	 *
	 * @param event the event triggered by clicking the "Add Copy" button
	 */
	@FXML
	private void onAddCopyClick(ActionEvent event) {
		Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
		if (selectedBook == null) {
			ScreenLoader.showAlert("Error", "Select a book.");
			return;
		}

		ScreenLoader.openPopUpScreen("/gui/NewCopyScreen.fxml", "Add new copy", event, controller -> {
			if (controller instanceof NewCopyController) {
				NewCopyController newCopyController = (NewCopyController) controller;
				newCopyController.setStage(new Stage());
				newCopyController.setClient(c);

				newCopyController.setBook(selectedBook);

				newCopyController.setOnCopyAddedCallback(newCopy -> {
					if (copiesList != null) {
						copiesList.add(newCopy);
						Platform.runLater(() -> copiesTable.refresh());
						try {
							List<Book> updatedbooks = bc.getAllBooks();
							booksList.setAll(updatedbooks);
							Platform.runLater(() -> booksTable.refresh());
						} catch (InterruptedException e) {
							e.printStackTrace();
							ScreenLoader.showAlert("Error", "Failed to update book table");
						}
					} else {
						System.out.println("copiesList is null");
					}

				});

			}
		});
	}

	/**
	 * Handles editing a selected copy of a book. Opens the "Edit Copy" pop-up
	 * screen.
	 *
	 * @param event the event triggered by clicking the "Edit Copy" button
	 */
	@FXML
	private void onEditCopyClick(ActionEvent event) {
		Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
		CopyOfBook selectedCopy = copiesTable.getSelectionModel().getSelectedItem();
		if (selectedCopy == null) {
			ScreenLoader.showAlert("Error", "Select a copy.");
			return;
		}

		ScreenLoader.openPopUpScreen("/gui/editCopyScreen.fxml", "Edit copy screen", event, controller -> {
			if (controller instanceof EditCopyController) {
				EditCopyController editCopyController = (EditCopyController) controller;
				((EditCopyController) controller).setStage(new Stage());
				((EditCopyController) controller).setClient(c);

				editCopyController.setBook(selectedCopy, selectedBook);

				editCopyController.setOnCopyEditedCallback(editedCopy -> {
					if (copiesList != null) {
						copiesList.setAll(editedCopy);
						Platform.runLater(() -> copiesTable.refresh());

						try {
							List<CopyOfBook> updatedCopies = bc.getAllBookCopies(selectedBook.getId());
							copiesList.setAll(updatedCopies);
							Platform.runLater(() -> copiesTable.refresh());
						} catch (InterruptedException e) {
							e.printStackTrace();
							ScreenLoader.showAlert("Error", "Failed to update copies table");
						}
					} else {
						System.out.println("copiesList is null");
					}

				});

			}
		});
		booksTable.requestFocus();
	}

	/**
	 * Handles the "Back" button click event. Navigates back to the Librarian's main
	 * screen.
	 * 
	 * @param event the {@link ActionEvent} triggered by clicking the "Back" button
	 */
	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
				((LibrarianMainController) controller).setClient(c);
				((LibrarianMainController) controller).setLibrarian(lib);
			}
		});
	}

}
