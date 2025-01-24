package gui;

import javafx.scene.control.Label;

import java.util.function.Consumer;

import client.ClientMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Book;
import logic.BookController;
import logic.CopyOfBook;
import logic.ScreenLoader;
import logic.Subscriber;

public class NewCopyController {
	@FXML
	private Label bookCodeLabel;
	
	@FXML
	private Label copyIdLabel;
	
	@FXML
	private TextField locationField;
	
	@FXML
	private Label statusLabel;

	@FXML
	private Button addButton;
	
	@FXML
	private Button cancelButton;
	
	private Stage stage;
	
	private ClientMain c;
	
	private Consumer<CopyOfBook> onCopyAddedCallback;
	
	private Book book;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}
	
	public void setOnCopyAddedCallback(Consumer<CopyOfBook> callback) {
		this.onCopyAddedCallback=callback;
	}
	
	public void setBook(Book b) {
		book=b;
		setDetails();
	}

	public void setDetails() {
		bookCodeLabel.setText(Integer.toString(book.getId()));
		copyIdLabel.setText(Integer.toString((book.getTotalCopies())+1));
		statusLabel.setText("exists");
	}
	
	@FXML
	private void onAddClick(ActionEvent event) {
		String location=locationField.getText();
		
		if(location.isEmpty()) {
			ScreenLoader.showAlert("Error", "Enter location.");
			return;
		}else {
			ScreenLoader.closeWindow(cancelButton);
		}
		
		BookController b=new BookController(c);
		try {
			String result=b.addCopyOfBook(book.getId(),location);
			ScreenLoader.showAlert("copy added",result);
			
			int nextCopyId=book.getTotalCopies()+1;
			CopyOfBook newCopy=new CopyOfBook(book.getId(),nextCopyId,location,"exists");
			if(onCopyAddedCallback!=null) {
				onCopyAddedCallback.accept(newCopy);
			}
			Stage currentStage = (Stage) addButton.getScene().getWindow();
	        currentStage.close();
		}catch(InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to add copy.");
		}
	}
	
	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}
}
