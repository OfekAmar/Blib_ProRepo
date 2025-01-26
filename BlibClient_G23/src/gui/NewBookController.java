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

/**
 * The `NewBookController` class manages the functionality for adding a new book
 * to the system. This controller is responsible for handling user input and
 * interacting with the backend to create a new book entry in the database.
 */
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

	/**
	 * Sets the current stage (window) for the controller.
	 *
	 * @param stage the current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	/**
	 * Sets a callback function to be invoked after a new book is successfully
	 * added.
	 *
	 * @param callback a {@link Consumer} to handle the added book
	 */
	public void setOnBookAddedCallback(Consumer<Book> callback) {
		this.onBookAddesCallback = callback;
	}

	/**
	 * Handles the "Add" button click event. Validates the input fields and attempts
	 * to add the new book to the system.
	 *
	 * @param event the event triggered by clicking the "Add" button
	 */
	@FXML
	private void onAddClick(ActionEvent event) {
		String title = titleField.getText();
		String author = authorField.getText();
		String subject = subjectField.getText();
		String description = descriptionField.getText();

		// Validate that all fields are filled
		if (title.isEmpty() || author.isEmpty() || subject.isEmpty() || description.isEmpty()) {
			ScreenLoader.showAlert("Error", "All fields are required.");
			return;
		} else {
			ScreenLoader.closeWindow(cancelButton);
		}

		// Attempt to add the book using the BookController
		BookController b = new BookController(c);
		try {
			String result = b.addBook(author, title, subject, description);
			ScreenLoader.showAlert("book added", result);

			// Create a new Book object to represent the added book
			Book newBook = new Book(title, 0, author, subject, description, 0);
			if (onBookAddesCallback != null) {
				onBookAddesCallback.accept(newBook);
			}
			Stage currentStage = (Stage) addButton.getScene().getWindow();
			currentStage.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to add book.");
		}
	}

	/**
	 * Handles the "Cancel" button click event. Closes the window without adding a
	 * book.
	 *
	 * @param event the event triggered by clicking the "Cancel" button
	 */
	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}
}
