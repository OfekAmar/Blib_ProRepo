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
	private Button deleteBookButton;

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
	private Book searchBook=null;
	private ObservableList<Book> booksList=FXCollections.observableArrayList();
	private ObservableList<CopyOfBook> copiesList=FXCollections.observableArrayList();


	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
		bc = new BookController(c);
		System.out.println("Setclient bookcontoller worked");
		loadBooksToTable();
		loadCopiesToTable();
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
		// welcomeLabel.setText("Hello, " + lib.getName() + "!");
	}

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
				System.out.println("Books fetched for table: " + books);
				//booksTable.setItems(FXCollections.observableArrayList(books));
				booksList.setAll(books);
				System.out.println("books loaded into table");
			} catch (InterruptedException e) {
				e.printStackTrace();
				ScreenLoader.showAlert("Error", "Failed to load books data.");
			}
		});
	}
	
	@FXML
	private void loadCopiesToTable() {
	}
	
	@FXML
	private void onAddBookClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/NewBookScreen.fxml", "Add new book", event, controller -> {
			if (controller instanceof NewBookController) {
				NewBookController newBookController=(NewBookController)controller;
				((NewBookController) controller).setStage(new Stage());
				((NewBookController) controller).setClient(c);
				
				newBookController.setOnBookAddedCallback(newBook -> {
					if(booksList!=null) {
						 booksList.add(newBook);
			             Platform.runLater(() -> booksTable.refresh()); 
			             System.out.println("New book added to table: " + newBook);
					}else {
						System.out.println("bookList is null");
					}
	               
	            });
			}
		});
	}

	@FXML
	private void onDeleteBookClick(ActionEvent event) {

	}

	@FXML
	private void onViewCopiesClick(ActionEvent event) {
		copyIDColumn.setCellValueFactory(new PropertyValueFactory<>("copyID"));
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		
		copiesTable.setItems(copiesList);
		
		Book selectedBook=booksTable.getSelectionModel().getSelectedItem();
		
		
		if(selectedBook==null) {
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
				List<CopyOfBook> copies=bc.getAllBookCopies(selectedBook.getId());
				copiesList.setAll(copies);
				System.out.println("Copies loaded into table: "+copies);
			}catch(Exception e) {
				e.printStackTrace();
				ScreenLoader.showAlert("Error", "Failed to fetch");
			}
		});
	}

	@FXML
	private void onScanBookBarcodeClick(ActionEvent event) {

	}

	@FXML
	private void onSearchBookClick(ActionEvent event) throws InterruptedException {
		String bookCode= searchField.getText();
		
		if(bookCode.isEmpty()) {
			ScreenLoader.showAlert("Error", "Enter book code.");
			return;
		}
			
		try {
			searchBook=bc.getBookByCode(Integer.valueOf(bookCode));
			
			
			if(searchBook!=null) {
				booksList.clear();
				booksList.add(searchBook);
				booksTable.refresh();
				System.out.println("Book found: " +searchBook);
			}else {
				ScreenLoader.showAlert("Error", "Book not found");
			}
		}catch(NumberFormatException e) {
			ScreenLoader.showAlert("Error", "Book code must be a number.");
		}catch(InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to fetch book.");
		}
	}
	
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

	
	@FXML
	private void onAddCopyClick(ActionEvent event) {
		Book selectedBook=booksTable.getSelectionModel().getSelectedItem();
		if(selectedBook==null) {
			ScreenLoader.showAlert("Error", "Select a book.");
			return;
		}
		
		ScreenLoader.openPopUpScreen("/gui/NewCopyScreen.fxml", "Add new copy", event, controller -> {
			if (controller instanceof NewCopyController) {
				NewCopyController newCopyController=(NewCopyController)controller;
				((NewCopyController) controller).setStage(new Stage());
				((NewCopyController) controller).setClient(c);
				
				newCopyController.setBook(selectedBook);
				
				newCopyController.setOnCopyAddedCallback(newCopy -> {
					if(copiesList!=null) {
						 copiesList.add(newCopy);
			             Platform.runLater(() -> copiesTable.refresh()); 
			             System.out.println("New copy added to table: " + newCopy);
			             
			             try {
			            	 List<Book> updatedbooks=bc.getAllBooks();
			            	 booksList.setAll(updatedbooks);
			            	 Platform.runLater(()-> booksTable.refresh());
			             }catch(InterruptedException e) {
			            	 e.printStackTrace();
			            	 ScreenLoader.showAlert("Error", "Failed to update book table");
			             }
					}else {
						System.out.println("copiesList is null");
					}
	               
	            });
				
			}
		});
	}
	
	@FXML
	private void onEditCopyClick(ActionEvent event) {
		
	}

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
