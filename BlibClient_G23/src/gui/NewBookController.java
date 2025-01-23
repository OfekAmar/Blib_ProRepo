package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
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
			ScreenLoader.showAlert("book added", b.addBook(author, title, subject, description));
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}
}
