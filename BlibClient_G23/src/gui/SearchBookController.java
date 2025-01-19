package gui;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.TextArea;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import logic.Book;
import logic.CopyOfBook;
import logic.ScreenLoader;
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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setLoggedIn(boolean l) {
		this.loggedIn = l;
		updateButtonsVisibility();
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
		
		SearchBookLogic searchBookLogic=new SearchBookLogic();
		Book book=searchBookLogic.searchBook(searchTerm,searchBy);
		if(book.getAvailbaleCopies()>0) {
		}
		List<CopyOfBook> booksCopies=searchBookLogic.searchBook(searchTerm,searchBy);
		
		if(booksCopies.isEmpty()) {
			ScreenLoader.showAlert("Error", "There is no such book in the library database !\n please try again");
		}
		else {
			StringBuilder results=new StringBuilder();
			for(CopyOfBook bookCopy:booksCopies) {
				results.append("Title: ").append(bookCopy.getTitle()).append("\nAuthor: ").append(bookCopy.getAuthor()).append("\nSubject: ").append(bookCopy.getSubject());
				
				if(book.getAvalibaleCopies()>0) {
					results.append("\nShelf Location: ").append(bookCopy.get)
				}
			}
		} 
		else {
			// Implement your search logic here
			if (b != null) {
				if (b.getTotalCopies() - b.getTotalCopies() < 1) {
					ScreenLoader.showAlert("No copies found",
							"There is no available copy of the book to borrow /n Please order the book by his ID: "
									+ b.getId());
				} else if (b instanceof CopyOfBook) {
					ScreenLoader.showAlert("Copy Found", "The book is located in " + ((CopyOfBook) b).getLocation());
				}
			} else {
				ScreenLoader.showAlert("Error", "There is no such book in the library database !\n please try again");
			}
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
