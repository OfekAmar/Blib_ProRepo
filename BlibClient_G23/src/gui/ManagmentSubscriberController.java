package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import client.ClientMain;
import javafx.event.ActionEvent;
import logic.Librarian;
import logic.ScreenLoader;
import logic.Subscriber;
import logic.SubscriberController;

public class ManagmentSubscriberController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private TextField searchField;

	@FXML
	private TextField subscriberDetailsField;

	@FXML
	private Button searchButton;

	@FXML
	private Button readerCard;

	@FXML
	private Button viewProfileButton;

	@FXML
	private Button addSubscriberButton;

	@FXML
	private Button scanCardButton;

	@FXML
	private Button exitButton;

	private Stage stage;
	private Subscriber searchedsubs = null;
	private ClientMain c;
	private SubscriberController sc;
	private Librarian lib;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
		sc = new SubscriberController(c);
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	@FXML
	private void onSearchSubscriberClick(ActionEvent event) throws InterruptedException {
		String subscriberID = searchField.getText();
		if (subscriberID.isEmpty()) {
			ScreenLoader.showAlert("Error", "Please enter a subscriber name to search.");
		} else {
			searchedsubs = sc.searchSubscriberById(subscriberID);
			subscriberDetailsField
					.setText("Details of subscriber: " + searchedsubs.getName() + "(" + searchedsubs.getId() + ")");
		}
	}

	@FXML
	private void onReaderCardClick(ActionEvent event) {
		if (searchedsubs != null) {
			ScreenLoader.openPopUpScreenWithSize("/gui/ReaderCardScreen.fxml", "Reader Card", event, controller -> {
				if (controller instanceof ReaderCardController) {
					((ReaderCardController) controller).setStage(stage);
					((ReaderCardController) controller).setClientAndSubscriber(searchedsubs, c);
					((ReaderCardController) controller).setLibrarian(lib);
				}
			}, 450, 300);
		} else {
			ScreenLoader.showAlert("Error", "No such subscriber exist or you didnt searched for one ! ");
		}
	}

	@FXML
	private void onViewProfileClick(ActionEvent event) {
		if (searchedsubs != null) {
			ScreenLoader.openPopUpScreen("/gui/SubscriberDetails.fxml", "Subcriber Details", event, controller -> {
				if (controller instanceof SubscriberDetailsController) {
					((SubscriberDetailsController) controller).setStage(new Stage(), null);
					((SubscriberDetailsController) controller).setSubscriber(searchedsubs);
					
				}
			});
		} else {
			ScreenLoader.showAlert("Error", "No such subscriber exist or you didnt searched for one ! ");
		}
	}

	@FXML
	private void onAddSubscriberClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/NewSubscriberScreen.fxml", "Add New Subscriber", event, controller -> {
			if (controller instanceof NewSubscriberController) {
				((NewSubscriberController) controller).setStage(new Stage());
				((NewSubscriberController) controller).setClient(c);
			}
		});
	}

	@FXML
	private void onScanCardClick(ActionEvent event) {
		// Implement scan card logic here
		ScreenLoader.showAlert("Scan Card", "Scanning subscriber card.");
	}

	@FXML
	private void onBackClick(ActionEvent event) {
		ScreenLoader.openScreen("/gui/LibrarianMainScreen.fxml", "Librarian Main Screen", event, controller -> {
			if (controller instanceof LibrarianMainController) {
				((LibrarianMainController) controller).setStage(new Stage());
				((LibrarianMainController) controller).setClient(c);
				((LibrarianMainController) controller).setLibrarian(lib);
			}
		});
	}

}
