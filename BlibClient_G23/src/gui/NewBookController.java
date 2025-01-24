package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

import client.ClientMain;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.ScreenLoader;
import logic.Book;
import logic.BookController;


public class NewBookController {
	@FXML
	private TextField titleField;
	
	@FXML
	private TextField authorField;

	@FXML
	private TextField subjectField;
	
	@FXML
	private TextField descriptionField;
	
	@FXML
	private Button addButton;
	
	@FXML
	private Button cancelButton;
	
	private Stage stage;
	
	private ClientMain c;
	
	private Consumer<Book> onBookAddesCallback;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}
	
	public void setOnBookAddedCallback(Consumer<Book> callback) {
		this.onBookAddesCallback=callback;
	}
	
	@FXML
	private void onAddClick(ActionEvent event) {
		String title=titleField.getText();
		String author=authorField.getText();
		String subject=subjectField.getText();
		String description=descriptionField.getText();
		
		if(title.isEmpty() || author.isEmpty() || subject.isEmpty() || description.isEmpty()) {
			ScreenLoader.showAlert("Error", "All fields are required.");
			return;
		}else {
			ScreenLoader.closeWindow(cancelButton);
		}
		
		BookController b=new BookController(c);
		try {
			String result=b.addBook(author, title, subject, description);
			ScreenLoader.showAlert("book added",result);
			
			Book newBook=new Book(title,0,author,subject,description,0);
			if(onBookAddesCallback!=null) {
				onBookAddesCallback.accept(newBook);
			}
			Stage currentStage = (Stage) addButton.getScene().getWindow();
	        currentStage.close();
		}catch(InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to add book.");
		}
	}
	
	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}
}
