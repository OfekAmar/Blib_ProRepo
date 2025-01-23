package gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.swing.text.TabableView;

import client.ClientMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import logic.Librarian;
import logic.ScreenLoader;
import logic.Book;
import logic.BookController;



public class ManagmentBookController {
	@FXML
	private TableView<Book> booksTable;
	 
	@FXML
	private TableColumn<Book, Integer> bookCodeColumn;
	 
	@FXML
	private TableColumn<Book, String> titleColumn;
	 
	@FXML
	private TableColumn<Book, String> authorColumn;
	 
	@FXML
	private TableColumn<Book, String> subjectColumn;
	 
	@FXML
	private TableColumn<Book, String> descriptionColumn;
	 
	@FXML
	private TableColumn<Book, Integer> copiesColumn;
	
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
	
	private Stage stage;
	private ClientMain c;
	private Librarian lib;
	private BookController bc;
	private ObservableList<Book> booksList;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setClient(ClientMain c) {
		this.c = c;
		bc = new BookController(c);
		System.out.println("Setclient bookcontoller worked");
		initializeTable();
	}
	
	public void setLibrarian(Librarian lib) {
		this.lib = lib;
		//welcomeLabel.setText("Hello, " + lib.getName() + "!");
	}
	
	@FXML
	public void initialize() {
		System.out.println("initizlize called");
		if(bc==null) {
			System.out.println("bc not initialized. check setClient");
			return;
		}
		initializeTable();
	}
	@FXML
	public void initializeTable() {
	    // הגדרת העמודות
	    bookCodeColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
	    titleColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
	    authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
	    subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
	    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
	    copiesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));

	    // מילוי הנתונים לטבלה
	    try {
	        booksList = FXCollections.observableArrayList(bc.getAllBooks());
	        booksTable.setItems(booksList);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	        ScreenLoader.showAlert("Error", "Failed to load books data.");
	    }
	}
	
	@FXML
	private void onAddBookClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/NewBookScreen.fxml", "Add new book", event, controller -> {
			if(controller instanceof NewBookController) {
				((NewBookController)controller).setStage(new Stage());
				((NewBookController)controller).setClient(c);
			}
		});
	}
	
	@FXML
	private void onDeleteBookClick(ActionEvent event) {
		
	}
	@FXML
	private void onViewCopiesClick(ActionEvent event) {
		
	}
	@FXML
	private void onScanBookBarcodeClick(ActionEvent event){
		
	}
	@FXML
	private void onSearchBookClick(ActionEvent event) {
		
	}
	@FXML
	private void onBackClick(ActionEvent event) {
		
	}
}
