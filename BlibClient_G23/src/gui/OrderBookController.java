package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import client.ClientMain;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.BorrowController;
import logic.ScreenLoader;
import logic.Subscriber;

/**
 * The `OrderBookController` class handles the process of ordering a book for a
 * subscriber. It provides a user interface for entering a book ID and placing
 * an order for the book.
 */
public class OrderBookController {

	@FXML
	private TextField bookIdField;

	@FXML
	private Button orderButton;

	@FXML
	private Button closeButton;

	private Stage stage;
	private Subscriber sub;
	private ClientMain c;

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
	 * @param cm the {@link ClientMain} instance used for server communication
	 */
	public void setClient(ClientMain cm) {
		this.c = cm;
	}

	/**
	 * Sets the subscriber who is placing the book order.
	 *
	 * @param s the {@link Subscriber} instance for the subscriber placing the order
	 */
	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

	/**
	 * Handles the action of ordering a book. Reads the book ID from the text field
	 * and sends an order request to the server. Displays an alert with the result
	 * of the operation.
	 *
	 * @param event the triggered event when the "Order" button is clicked
	 */
	@FXML
	private void onOrderBookClick(ActionEvent event) {
		int bookId = Integer.valueOf(bookIdField.getText());
		int subID = this.sub.getId();
		BorrowController b = new BorrowController(null, c);
		try {
			ScreenLoader.showAlert("Order", b.orderBook(subID, bookId));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handles the action of closing the order window when the "Back" button is
	 * clicked.
	 *
	 * @param event the triggered event when the "Back" button is clicked
	 */
	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}

}
