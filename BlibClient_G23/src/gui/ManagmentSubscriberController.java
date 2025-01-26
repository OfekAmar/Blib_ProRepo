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

/**
 * The `ManagmentSubscriberController` class is responsible for managing the
 * functionality of the Subscriber Management screen. This screen allows
 * librarians to view, search, add, edit, and manage subscribers.
 * 
 */
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

	/**
	 * Sets the current stage (window) for the controller.
	 *
	 * @param stage the current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Sets the client instance for the controller and loads the subscribers into
	 * the table.
	 *
	 * @param c the {@link ClientMain} instance used for server communication
	 */
	public void setClient(ClientMain c) {
		this.c = c;
		sc = new SubscriberController(c);
		loadSubscribersToTable();
	}

	/**
	 * Sets the librarian currently logged into the system.
	 *
	 * @param lib the {@link Librarian} instance
	 */
	public void setLibrarian(Librarian lib) {
		this.lib = lib;
	}

	/**
	 * Loads the list of subscribers into the table view by fetching data from the
	 * server. Uses {@link SubscriberController} to get the list of all subscribers
	 * and populate the table.
	 */
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
				subscribersList.setAll(subscribers);
			} catch (InterruptedException e) {
				e.printStackTrace();
				ScreenLoader.showAlert("Error", "Failed to load Subscribers data.");
			}
		});
	}

	/**
	 * Handles the "Reader Card" button click event. Opens the "Reader Card" screen
	 * to display the selected subscriber's reader card.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the "Reader Card"
	 *              button
	 */
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

	/**
	 * Handles the "Edit Profile" button click event. Opens the "Edit Subscriber
	 * Profile" pop-up screen to edit the selected subscriber's details.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the "Edit Profile"
	 *              button
	 */
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

						try {
							List<Subscriber> updatedSubscribers = sc.getAllSubscribers();
							subscribersList.setAll(updatedSubscribers);
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

	/**
	 * Handles the "Add New Subscriber" button click event. Opens the "Add New
	 * Subscriber" pop-up screen to allow adding a new subscriber.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the "Add
	 *              Subscriber" button
	 */
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

	/**
	 * Handles the "Scan Subscriber Card" button click event. Displays an alert
	 * prompting the librarian to scan a subscriber's card.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the "Scan Card"
	 *              button
	 */
	@FXML
	private void onScanSubscriberCardClick(ActionEvent event) {
		// Implement scan card logic here
		ScreenLoader.showAlert("Scan Card", "Scanning subscriber card.");
	}

	/**
	 * Handles the "Back" button click event. Navigates back to the Librarian's main
	 * screen.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the "Back" button
	 */
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

	/**
	 * Handles the subscriber search functionality. Searches for a subscriber by ID
	 * and displays the results in the table.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the "Search"
	 *              button
	 * @throws InterruptedException if the thread is interrupted while fetching data
	 */
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

	/**
	 * Resets the search results and reloads all subscribers into the table.
	 *
	 * @param event the {@link ActionEvent} triggered by clicking the "Reset Search"
	 *              button
	 */
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
