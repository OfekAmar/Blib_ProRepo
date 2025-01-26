package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

import client.ClientMain;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import logic.Book;
import logic.CopyOfBook;
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

	@FXML
	private TableView<Subscriber> subscribersTable;

	@FXML
	private TableColumn<Subscriber, Integer> idColumn;

	@FXML
	private TableColumn<Subscriber, String> nameColumn;

	@FXML
	private TableColumn<Subscriber, String> phoneColumn;

	@FXML
	private TableColumn<Subscriber, String> statusColumn;

	@FXML
	private TableColumn<Subscriber, String> emailColumn;

	private ObservableList<Subscriber> subscribersList = FXCollections.observableArrayList();

	private Stage stage;
	private Subscriber searchSub = null;
	private ClientMain c;
	private SubscriberController sc;
	private Librarian lib;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setClient(ClientMain c) {
		this.c = c;
		sc = new SubscriberController(c);
		loadSubscribersToTable();
	}

	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	@FXML
	public void loadSubscribersToTable() {
		System.out.println("initizlize called");
		if (sc == null) {
			System.out.println("sc not initialized. check setClient");
			return;
		}
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

		subscribersTable.setItems(subscribersList);

		Platform.runLater(() -> {
			try {
				List<Subscriber> subscribers = sc.getAllSubscribers();
				System.out.println("Subscribers fetched for table: " + subscribers);
				// booksTable.setItems(FXCollections.observableArrayList(books));
				subscribersList.setAll(subscribers);
				System.out.println("Subscribers loaded into table");
			} catch (InterruptedException e) {
				e.printStackTrace();
				ScreenLoader.showAlert("Error", "Failed to load Subscribers data.");
			}
		});
	}

	@FXML
	private void onReaderCardClick(ActionEvent event) {
		Subscriber selectedSub = subscribersTable.getSelectionModel().getSelectedItem();
		if (selectedSub == null) {
			ScreenLoader.showAlert("Error", "Select a subscriber.");
			return;
		}

		ScreenLoader.openScreen("/gui/ReaderCardScreen.fxml", "Reader Card", event, controller -> {
			if (controller instanceof ReaderCardController) {
				ReaderCardController readerCardController = (ReaderCardController) controller;
				readerCardController.setStage(stage);
				readerCardController.setClientAndSubscriber(selectedSub, c);
				readerCardController.setLibrarian(lib);
			} else {
				ScreenLoader.showAlert("Error", "No such subscriber exist or you didnt searched for one ! ");
			}
		});
	}

	@FXML
	private void onEditProfileClick(ActionEvent event) {
		Subscriber selectedSub = subscribersTable.getSelectionModel().getSelectedItem();
		if (selectedSub == null) {
			ScreenLoader.showAlert("Error", "Select a subscriber.");
			return;
		}

		ScreenLoader.openPopUpScreen("/gui/SubscriberDetails.fxml", "Edit Profile", event, controller -> {
			if (controller instanceof SubscriberDetailsController) {
				SubscriberDetailsController editSubController = (SubscriberDetailsController) controller;
				editSubController.setStage(new Stage());
				editSubController.setClient(c);
				editSubController.setSubscriber(selectedSub);

				editSubController.setDetails();

				editSubController.setOnEditSubscriberCallback(editedSub -> {
					if (subscribersList != null) {
						subscribersList.setAll(editedSub);
						Platform.runLater(() -> subscribersTable.refresh());
						System.out.println("Edit subscriber updated in table: " + editedSub);

						try {
							List<Subscriber> updatedSubscribers = sc.getAllSubscribers();
							subscribersList.setAll(updatedSubscribers);
							System.out.println("updtaedsubscribers after set all" + subscribersList);
							Platform.runLater(() -> subscribersTable.refresh());
						} catch (InterruptedException e) {
							e.printStackTrace();
							ScreenLoader.showAlert("Error", "Failed to update subscribers table");
						}
					} else {
						System.out.println("subscribersList is null");
					}

				});

			}
		});
	}

	@FXML
	private void onAddSubscriberClick(ActionEvent event) {
		ScreenLoader.openPopUpScreen("/gui/NewSubscriberScreen.fxml", "Add New Subscriber", event, controller -> {
			if (controller instanceof NewSubscriberController) {
				NewSubscriberController newSubscriberController = (NewSubscriberController) controller;
				newSubscriberController.setStage(new Stage());
				newSubscriberController.setClient(c);

				newSubscriberController.setOnSubscriberAddedCallback(newSub -> {
					if (subscribersList != null) {
						subscribersList.add(newSub);
						Platform.runLater(() -> subscribersTable.refresh());
						System.out.println("New subscriber added to table: " + newSub);

						try {
							List<Subscriber> updatedSubs = sc.getAllSubscribers();
							subscribersList.setAll(updatedSubs);
							Platform.runLater(() -> subscribersTable.refresh());
						} catch (InterruptedException e) {
							e.printStackTrace();
							ScreenLoader.showAlert("Error", "Failed to update subscribers table");
						}
					} else {
						System.out.println("subscribersList is null");
					}

				});
			}
		});
	}

	@FXML
	private void onScanSubscriberCardClick(ActionEvent event) {
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

	@FXML
	private void onSearchSubscriberClick(ActionEvent event) throws InterruptedException {
		String subscriberId = searchField.getText();

		if (subscriberId.isEmpty()) {
			ScreenLoader.showAlert("Error", "Enter subscriber id.");
			return;
		}

		try {
			searchSub = sc.searchSubscriberById(subscriberId);

			if (searchSub != null) {
				subscribersList.clear();
				subscribersList.add(searchSub);
				subscribersTable.refresh();
				System.out.println("Subscriber found: " + searchSub);
			} else {
				ScreenLoader.showAlert("Error", "Subscriber not found");
			}
		} catch (NumberFormatException e) {
			ScreenLoader.showAlert("Error", "Subscriber Id must be a number.");
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to fetch Subscriber.");
		}
	}

	@FXML
	private void onResetSearchClick(ActionEvent event) {
		try {
			List<Subscriber> subscribers = sc.getAllSubscribers();
			subscribersList.setAll(subscribers);
			searchField.setText("");
			subscribersTable.refresh();
		} catch (InterruptedException e) {
			e.printStackTrace();
			ScreenLoader.showAlert("Error", "Failed to fetch");
		}
	}

}
