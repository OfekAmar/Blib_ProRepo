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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain cm) {
		this.c = cm;
	}

	public void setSubscriber(Subscriber s) {
		this.sub = s;
	}

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

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.closeWindow(closeButton);
	}

}
