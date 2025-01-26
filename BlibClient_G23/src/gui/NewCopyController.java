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

/**
 * The `NewCopyController` class manages the functionality for adding a new copy
 * of a book to the system. This controller allows users to input the location
 * of the copy and adds it to the relevant book in the system.
 */
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

	/**
	 * Sets the current stage (window) for the controller.
	 *
	 * @param stage the current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the client instance for the controller, enabling communication with the
	 * server.
	 *
	 * @param c the {@link ClientMain} instance used for server communication
	 */
	public void setClient(ClientMain c) {
		this.c = c;
	}

	/**
	 * Sets a callback function to be invoked after a new copy is successfully
	 * added.
	 *
	 * @param callback a {@link Consumer} to handle the added copy
	 */
	public void setOnCopyAddedCallback(Consumer<CopyOfBook> callback) {
		this.onCopyAddedCallback = callback;
	}

	/**
	 * Sets the book to which the new copy will belong. Also updates the UI to
	 * display details of the selected book.
	 *
	 * @param b the {@link Book} instance
	 */
	public void setBook(Book b) {
		book = b;
		setDetails();
	}

	/**
	 * Updates the UI with details of the selected book and the next copy ID.
	 */
	public void setDetails() {
		bookCodeLabel.setText(Integer.toString(book.getId()));
		copyIdLabel.setText(Integer.toString((book.getTotalCopies()) + 1));
		statusLabel.setText("exists");
	}

	/**
	 * Handles the "Add" button click event. Validates the input location, interacts
	 * with the backend to add the copy, and triggers the callback for successful
	 * addition.
	 *
	 * @param event the event triggered by clicking the "Add" button
	 */
	@FXML
	private void onAddClick(ActionEvent event) {
		String location = locationField.getText();

		if (location.isEmpty()) {
			ScreenLoader.showAlert("Error", "Enter location.");
			return;
		} else {
			ScreenLoader.closeWindow(cancelButton);
		}

		BookController b = new BookController(c);
		try {
			String result = b.addCopyOfBook(book.getId(), location);
			ScreenLoader.showAlert("copy added", result);

			int nextCopyId = book.getTotalCopies() + 1;
			CopyOfBook newCopy = new CopyOfBook(book.getId(), nextCopyId, location, "exists");
			if (onCopyAddedCallback != null) {
				onCopyAddedCallback.accept(newCopy);
			}
			Stage currentStage = (Stage) addButton.getScene().getWindow();
			currentStage.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to add copy.");
		}
	}

	/**
	 * Handles the "Cancel" button click event. Closes the window without adding a
	 * copy.
	 *
	 * @param event the event triggered by clicking the "Cancel" button
	 */
	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}
}
