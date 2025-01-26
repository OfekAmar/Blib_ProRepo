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
 * The `EditCopyController` class manages the user interface for editing the
 * details of a specific copy of a book. It provides fields for modifying the
 * copy's location and displays its current status.
 */
public class EditCopyController {
	@FXML
	private Label bookCodeLabel;

	@FXML
	private Label copyIdLabel;

	@FXML
	private TextField locationField;

	@FXML
	private Label statusLabel;

	@FXML
	private Button saveButton;

	@FXML
	private Button cancelButton;

	private Stage stage;

	private ClientMain c;

	private Consumer<CopyOfBook> onCopyEditedCallback;

	private CopyOfBook copy;
	private Book book;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
	}

	/**
	 * Sets the callback function to handle actions after editing the copy.
	 *
	 * @param callback a {@link Consumer} to handle the edited copy
	 */
	public void setOnCopyEditedCallback(Consumer<CopyOfBook> callback) {
		this.onCopyEditedCallback = callback;
	}

	public void setBook(CopyOfBook bookCopy, Book b) {
		copy = bookCopy;
		book = b;
		setDetails();
	}

	/**
	 * Displays the details of the book copy in the UI fields.
	 */
	public void setDetails() {
		copyIdLabel.setText(Integer.toString(copy.getCopyID()));
		locationField.setText(copy.getLocation());
		statusLabel.setText(copy.getStatus());
	}

	/**
	 * Handles the save button click event to update the copy details.
	 *
	 * @param event the {@link ActionEvent} triggered by the button click
	 */
	@FXML
	private void onSaveClick(ActionEvent event) {
		String location = locationField.getText();

		if (location.isEmpty()) {
			ScreenLoader.showAlert("Error", "Enter location.");
			return;
		} else {
			ScreenLoader.closeWindow(cancelButton);
		}

		BookController bc = new BookController(c);
		try {
			String result = bc.editCopyOfBook(book.getId(), copy.getCopyID(), location, copy.getStatus());
			ScreenLoader.showAlert("copy edited", result);

			CopyOfBook editedCopy = new CopyOfBook(book.getId(), copy.getCopyID(), copy.getLocation(),
					copy.getStatus());
			if (onCopyEditedCallback != null) {
				onCopyEditedCallback.accept(editedCopy);
			}
			Stage currentStage = (Stage) saveButton.getScene().getWindow();
			currentStage.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to edit copy.");
		}
	}

	@FXML
	private void onCancelClick(ActionEvent event) {
		ScreenLoader.closeWindow(cancelButton);
	}
}
