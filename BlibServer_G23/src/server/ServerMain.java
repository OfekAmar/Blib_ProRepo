package server;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import logic.Book;
import logic.CopyOfBook;
import logic.Subscriber;
import logic.ExtendedRecord;
import logic.Librarian;
import logic.Record;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ServerMain extends AbstractServer {

	private final DBconnector dbConnector;

	private final ScheduledExecutorService scheduler;

	public ServerMain(int port) {
		super(port);
		this.dbConnector = DBconnector.getInstance();
		this.scheduler = Executors.newScheduledThreadPool(1);
		startDailyOverdueCheck();
	}

	private void startDailyOverdueCheck() {
		scheduler.scheduleAtFixedRate(() -> {
			try {
				// Check and freeze overdue subscribers
				dbConnector.checkAndFreezeOverdueSubscribers();

				// Check for borrows due tomorrow and send notifications
				checkAndNotifyDueTomorrow();
			} catch (Exception e) {
				System.err.println("Error during daily checks: " + e.getMessage());
			}
		}, 0, 1, TimeUnit.DAYS); // Initial delay = 0, repeats every 1 day
	}

	private void checkAndNotifyDueTomorrow() {
		System.out.println("Running due-tomorrow notification check...");

		try {
			// Fetch data from DBconnector
			List<Map<String, String>> notifications = dbConnector.getBorrowsDueTomorrow();

			for (Map<String, String> notification : notifications) {
				// Prepare notification description
				String description = "Remain 1 day to return the book: " + notification.get("book_name");

				// Send notification using DBconnector
				try {
					dbConnector.sendNotificationToSubscriber(Integer.parseInt(notification.get("sub_id")), description);
				} catch (SQLException e) {
					System.err.println("Error while sending notification to Subscriber ID " + notification.get("sub_id")
							+ ": " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println("Error during due-tomorrow notifications: " + e.getMessage());
		}
	}

	private void sendNotificationToSubscriber(int subId, String description) {
		System.out.println("Sending notification to Subscriber ID " + subId + ": " + description);

		// Add the logic to store the notification in the database
		String notificationQuery = "INSERT INTO subscribernotifications (sub_id, description, read_status) VALUES (?, ?, 'unread')";

		try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(notificationQuery)) {
			ps.setInt(1, subId);
			ps.setString(2, description);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error while sending notification: " + e.getMessage());
		}
	}

	@Override
	protected void finalize() throws Throwable {
		scheduler.shutdownNow(); // Shut down the scheduler when the server stops
		super.finalize();
	}

	@Override
	protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Processing message from client: " + msg);

		try {
			if (msg instanceof String) {
				String[] parts = ((String) msg).split(",");
				String command = parts[0].toUpperCase();

				switch (command) {
				case "ADD_BOOK":
					if (parts.length == 5) {
						String author = parts[1];
						String title = parts[2];
						String subject = parts[3];
						String description = parts[4];
						dbConnector.addBook(author, title, subject, description);
						client.sendToClient("New book added successfuly:\nauthor: " + author + "\ntitle: " + title
								+ "\nsubject: " + subject + "\ndescription: " + description);
					} else {
						client.sendToClient("ERROR: Invalid ADD_BOOK format.");
					}
					break;

				case "ADD_COPY_OF_BOOK":
					if (parts.length == 3) {
						int bookId = Integer.valueOf(parts[1]);
						String location = parts[2];
						dbConnector.addCopyOfBook(bookId, location);
						client.sendToClient(
								"New copy of book added successfuly:\nbook id: " + bookId + "\nlocation: " + location);
					} else {
						client.sendToClient("ERROR: Invalid ADD_COPY_OF_BOOK format.");
					}
					break;

				case "ADD_SUBSCRIBER":
					if (parts.length == 6) {
						String name = parts[1];
						String phone = parts[2];
						String email = parts[3];
						String password = parts[4];
						String userName = parts[5];
						dbConnector.addSubscriber(name, phone, email, password, userName);
						client.sendToClient("New Subscriber added successfuly:\nname: " + name + "\nphone number: "
								+ phone + "\nemail: " + email + "\nuserName: " + userName);
					} else {
						client.sendToClient("ERROR: Invalid ADD_SUBSCRIBER format.");
					}
					break;

				case "GET_ALL_BOOKS":
					List<Book> booksList = dbConnector.getAllBooks();
					client.sendToClient(new ArrayList<>(booksList));
					break;

				case "GET_ALL_BOOK_COPIES":
					if (parts.length == 2) {
						int bookCode = Integer.valueOf(parts[1]);
						List<CopyOfBook> copiesList = dbConnector.getAllBookCopies(bookCode);
						client.sendToClient(new ArrayList<>(copiesList));
					} else {
						client.sendToClient("ERROR: Invalid GET_ALL_BOOK_COPIES format.");
					}
					break;

				case "EDIT_COPY_OF_BOOK":
					if (parts.length == 5) {
						int bookCode = Integer.valueOf(parts[1]);
						int copyId = Integer.valueOf(parts[2]);
						String location = parts[3];
						String status = parts[4];
						dbConnector.editCopyOfBook(bookCode, copyId, location, status);
						client.sendToClient("edit copy of book successfuly: \nbook code: " + bookCode + "\ncopy id: "
								+ copyId + "\nlocation: " + location + "\nstatus: " + status);
					} else {
						client.sendToClient("ERROR: Invalid EDIT_COPY_OF_BOOK format.");
					}
					break;

				case "EDIT_SUBSCRIBER":
					if (parts.length == 5) {
						int id = Integer.parseInt(parts[1]);
						String phone = parts[2];
						String email = parts[3];
						String password = parts[4];
						dbConnector.editSubscriber(id, phone, email, password);
						client.sendToClient("edit Subscriber successfuly:\nid: " + id + "\nphone number: " + phone
								+ "\nemail: " + email + "\npassword: " + password);
					} else {
						client.sendToClient("ERROR: Invalid EDIT_SUBSCRIBER format.");
					}
					break;

				case "GET_ALL_SUBSCRIBERS":
					List<Subscriber> subscribersList = dbConnector.getAllSubscribers();
					client.sendToClient(new ArrayList<>(subscribersList));
					break;

				case "SEARCH_SUBSCRIBER_BY_ID":
					if (parts.length == 2) {
						int id = Integer.valueOf(parts[1]);
						Subscriber sub = dbConnector.searchSubscriberById(id);
						client.sendToClient(sub);
					} else {
						client.sendToClient("ERROR: Invalid GET_ALL_SUBSCRIBERS format.");
					}
					break;
				case "IS_USER_EXISTS":
					if (parts.length == 2) {
						String userName = parts[1];
						boolean result = dbConnector.isUserExists(userName);
						client.sendToClient(result);
					} else {
						client.sendToClient("ERROR: Invalid IS_USER_EXISTS format.");
					}
					break;

				case "LOGIN":
					if (parts.length == 3) {
						String userName = parts[1];
						String password = parts[2];
						Object user = dbConnector.login(userName, password);
						if (user instanceof Subscriber) {
							// Subscriber sub=(Subscriber)user;
							// client.sendToClient("Logged in as Subscriber: " + sub);
							client.sendToClient(user);
						} else if (user instanceof Librarian) {
							// Librarian lib=(Librarian)user;
							// client.sendToClient("Logged in as Librarian: " + lib);
							client.sendToClient(user);
						} else if (user instanceof String) {
							client.sendToClient(user);
						}
					} else {
						client.sendToClient("ERROR: Invalid LOGIN format.");
					}
					break;

				case "DELETE_BOOK":
					if (parts.length == 2) {
						String bookCode = parts[1];
						int rowsAffected = dbConnector
								.updateQuery("DELETE FROM book WHERE book_code = '" + bookCode + "'");
						if (rowsAffected > 0) {
							client.sendToClient("Book deleted successfully: " + bookCode);
						} else {
							client.sendToClient("Book not found: " + bookCode);
						}
					} else {
						client.sendToClient("ERROR: Invalid DELETE_BOOK format.");
					}
					break;
				case "SEARCH_BOOK_BY_TITLE":
					if (parts.length == 2) {
						String title = parts[1];
						String result = dbConnector.searchBookByTitle(title);

						client.sendToClient(result);

					} else {
						client.sendToClient("ERROR: Invalid SEARCH_BOOK_BY_TITLE format.");
					}
					break;
				case "SEARCH_BOOK_BY_SUBJECT":
					if (parts.length == 2) {
						String subject = parts[1];
						List<Book> result = dbConnector.searchBookBySubject(subject);
						client.sendToClient(new ArrayList<>(result));
					} else {
						client.sendToClient("ERROR: Invalid SEARCH_BOOK_BY_SUBJECT format.");
					}
					break;
				case "SEARCH_BOOK_BY_FREE_TEXT":
					if (parts.length == 2) {
						String freeText = parts[1];
						List<Book> result = dbConnector.searchBookByFreeText(freeText);
						client.sendToClient(new ArrayList<>(result));
					} else {
						client.sendToClient("ERROR: Invalid SEARCH_BOOK_BY_FREE_TEXT format.");
					}
					break;
				case "BORROW_BY_ID":
					if (parts.length == 2) {
						int id = Integer.valueOf(parts[1]);
						Map<Integer, Map<String, String>> result = dbConnector.BorrowByID(id);
						client.sendToClient(result);
					} else {
						client.sendToClient("ERROR: Invalid BORROW_BY_ID format.");
					}
					break;
				case "GET_RECORDS_BY_ID":
					if (parts.length == 2) {
						int id = Integer.valueOf(parts[1]);
						List<Record> result = dbConnector.AllRecordByID(id);
						client.sendToClient(new ArrayList<>(result));
					} else {
						client.sendToClient("ERROR: Invalid GET_RECORDS_BY_ID format.");
					}
					break;
				case "ORDER_BOOK":
					if (parts.length == 3) {
						int subID = Integer.valueOf(parts[1]);
						int bookCode = Integer.valueOf(parts[2]);
						boolean available = dbConnector.findCopyToOrder(bookCode);
						String checkforexistcopy = dbConnector.findExistCopy(bookCode);
						if (checkforexistcopy != null) {
							client.sendToClient(checkforexistcopy);
						} else if (dbConnector.checkFrozenSubscriber(subID)) {
							client.sendToClient("Subscriber is frozen unable to order");
						} else if (!available) {
							client.sendToClient("All book copies are reserved try again in a few days");
						} else {
							dbConnector.orderBook(subID, bookCode);
							client.sendToClient("The book is ordered for you !");
						}

					} else {
						client.sendToClient("ERROR: Invalid ORDER_BOOK format.");
					}
					break;
				case "MONTHLY_BORROW_REPORT":
					if (parts.length == 3) {
						int month = Integer.valueOf(parts[1]);
						int year = Integer.valueOf(parts[2]);
						List<ExtendedRecord> result = dbConnector.getMonthlyBorrowRecords(month, year);
						client.sendToClient(result);
					} else {
						client.sendToClient("ERROR: Invalid MONTHLY_BORROW_REPORT format.");
					}
					break;
				case "MONTHLY_STATUS_REPORT":
					if (parts.length == 3) {
						int month = Integer.valueOf(parts[1]);
						int year = Integer.valueOf(parts[2]);
						List<Record> result = dbConnector.getMonthlyFreezeAndUnfreezeRecords(month, year);
						client.sendToClient(result);
					} else {
						client.sendToClient("ERROR: Invalid MONTHLY_STATUS_REPORT format.");
					}
					break;
				case "STATUS_STATISTICS":
					if (parts.length == 1) {
						Map<String, Integer> result = dbConnector.getSubscriberStatusCounts();
						client.sendToClient(result);
					} else {
						client.sendToClient("ERROR: STATUS_STATISTICS format.");
					}
					break;

				case "UPDATE_BORROW_RETURN":
					if (parts.length == 5) {
						int subID = Integer.valueOf(parts[1]);
						int borrowID = Integer.valueOf(parts[2]);
						LocalDate date = LocalDate.parse(parts[3]);
						String desc = parts[4];
						int bookID = dbConnector.getBookFromBorrow(borrowID);
						dbConnector.updateBorrowReturn(borrowID, date);
						dbConnector.recordActivity("extend", subID, bookID, desc);
						client.sendToClient("Update Completed Successfuly");
					} else {
						client.sendToClient("ERROR: STATUS_STATISTICS format.");
					}
					break;

				case "GET_NOTIFICATIONS_SUB":
					if (parts.length == 3) {
						int subID = Integer.valueOf(parts[1]);
						int status = Integer.valueOf(parts[2]);
						Map<String, Integer> result = dbConnector.getNotiSubs(subID, status);
						client.sendToClient(result);
					} else {
						client.sendToClient("ERROR: GET_NOTIFICATIONS_SUB format.");
					}
					break;
				case "GET_NOTIFICATIONS_LIB":
					if (parts.length == 2) {
						int status = Integer.valueOf(parts[1]);
						Map<String, Integer> result = dbConnector.getNotiLib(status);
						client.sendToClient(result);
					} else {
						client.sendToClient("ERROR: GET_NOTIFICATIONS_LIB format.");
					}
					break;
				case "MARK_AS_READ_SUB":
					if (parts.length == 2) {
						int notiID = Integer.valueOf(parts[1]);
						dbConnector.readNotiSubs(notiID);
						client.sendToClient("Notification marked as read");
					} else {
						client.sendToClient("ERROR: MARK_AS_READ_SUB format.");
					}
					break;
				case "MARK_AS_READ_LIB":
					if (parts.length == 2) {
						int notiID = Integer.valueOf(parts[1]);
						dbConnector.readNotiLib(notiID);
						client.sendToClient("Notification marked as read");
					} else {
						client.sendToClient("ERROR: MARK_AS_READ_LIB format.");
					}
					break;
				case "CHECK_BOOK_AVAILABILITY":
					if (parts.length == 2) {
						String bookCode = parts[1];
						try {
							// Check if the book exists in the Book table
							String bookExistQuery = "SELECT COUNT(*) AS book_count FROM Book WHERE book_code = ?";
							try (PreparedStatement psBook = dbConnector.getDbConnection()
									.prepareStatement(bookExistQuery)) {
								psBook.setString(1, bookCode);
								ResultSet rsBook = psBook.executeQuery();
								if (rsBook.next() && rsBook.getInt("book_count") > 0) {
									// Check if the number of available copies is greater than 0
									String copyCountQuery = "SELECT COUNT(*) AS copy_count FROM CopyOfBook WHERE book_code = ? AND status = 'exists'";
									try (PreparedStatement psCopy = dbConnector.getDbConnection()
											.prepareStatement(copyCountQuery)) {
										psCopy.setString(1, bookCode);
										ResultSet rsCopy = psCopy.executeQuery();
										if (rsCopy.next() && rsCopy.getInt("copy_count") > 0) {
											client.sendToClient("Book is available: " + bookCode
													+ " | Copies available: " + rsCopy.getInt("copy_count"));
										} else {
											client.sendToClient("Book exists but no copies are available: " + bookCode);
										}
									}
								} else {
									client.sendToClient("Book not found: " + bookCode);
								}
							}
						} catch (Exception e) {
							client.sendToClient("ERROR: " + e.getMessage());
						}
					} else {
						client.sendToClient("ERROR: Invalid CHECK_BOOK_AVAILABILITY format.");
					}
					break;

				case "UPDATE_COPY_STATUS":
					if (parts.length == 4) {
						String bookCode = parts[1];
						String copyId = parts[2];
						String status = parts[3];
						String query = "UPDATE CopyOfBook SET status = ? WHERE book_code = ? AND copy_id = ?";
						try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query)) {
							ps.setString(1, status);
							ps.setString(2, bookCode);
							ps.setString(3, copyId);
							int rowsAffected = ps.executeUpdate();
							if (rowsAffected > 0) {
								client.sendToClient("Copy status updated successfully.");
							} else {
								client.sendToClient("ERROR: Copy not found or status unchanged.");
							}
						} catch (Exception e) {
							client.sendToClient("ERROR: " + e.getMessage());
						}
					} else {
						client.sendToClient("ERROR: Invalid UPDATE_COPY_STATUS format.");
					}
					break;

				case "CHECK_SUBSCRIBER_STATUS":
					if (parts.length == 2) {
						int subscriberId = Integer.parseInt(parts[1]);
						String query = "SELECT status FROM Subscriber WHERE sub_id = ?";
						try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query)) {
							ps.setInt(1, subscriberId);
							ResultSet rs = ps.executeQuery();
							if (rs.next()) {
								String status = rs.getString("status");
								client.sendToClient("Subscriber status: " + status);
							} else {
								client.sendToClient("ERROR: Subscriber not found.");
							}
						}

					} else {
						client.sendToClient("ERROR: Invalid CHECK_SUBSCRIBER_STATUS format.");
					}
					break;

				case "FREEZE_SUBSCRIBER":
					if (parts.length == 2) {
						int subscriberId = Integer.parseInt(parts[1]);
						String query = "UPDATE Subscriber SET status = 'frozen' WHERE sub_id = ?";
						try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query)) {
							ps.setInt(1, subscriberId);
							int rowsAffected = ps.executeUpdate();
							if (rowsAffected > 0) {
								client.sendToClient("Subscriber frozen successfully.");
							} else {
								client.sendToClient("ERROR: Subscriber not found.");
							}
						}
					} else {
						client.sendToClient("ERROR: Invalid FREEZE_SUBSCRIBER format.");
					}
					break;

				case "NOTIFY_SUBSCRIBER":
					if (parts.length == 3) {
						int subscriberId = Integer.parseInt(parts[1]);
						String message = parts[2];
						System.out.println("Notification to Subscriber " + subscriberId + ": " + message);
						client.sendToClient("Notification sent to Subscriber " + subscriberId + ".");
					} else {
						client.sendToClient("ERROR: Invalid NOTIFY_SUBSCRIBER format.");
					}
					break;

				case "PROCESS_BORROW":
					if (parts.length == 5) {
						try {
							int bookId = Integer.parseInt(parts[1]);
							int copyId = Integer.parseInt(parts[2]);
							int subscriberId = Integer.parseInt(parts[3]);
							LocalDate returnDate = LocalDate.parse(parts[4]);

							String result = dbConnector.processBorrow(bookId, copyId, subscriberId, returnDate);
							client.sendToClient(result);
						} catch (Exception e) {
							client.sendToClient("ERROR: Invalid data format. " + e.getMessage());
						}
					} else {
						client.sendToClient("ERROR: Invalid PROCESS_BORROW format.");
					}
					break;

				case "RETURN_BOOK":
					if (parts.length == 3) {
						try {
							int bookId = Integer.parseInt(parts[1]);
							int copyId = Integer.parseInt(parts[2]);

							String result = dbConnector.processReturnBook(bookId, copyId);
							client.sendToClient(result);
						} catch (Exception e) {
							client.sendToClient("ERROR: " + e.getMessage());
						}
					} else {
						client.sendToClient("ERROR: Invalid RETURN_BOOK format.");
					}
					break;
				case "GET_ACTIVE_BORROWED":
					if (parts.length == 2) {
						try {
							int subId = Integer.parseInt(parts[1]);
							List<String> activeBorrows = dbConnector.getActiveBorrows(subId);
							client.sendToClient(activeBorrows);
						} catch (Exception e) {
							List<String> errorResponse = new ArrayList<>();
							errorResponse.add("ERROR: " + e.getMessage());
							client.sendToClient(errorResponse);
						}
					} else {
						List<String> errorResponse = new ArrayList<>();
						errorResponse.add("ERROR: Invalid GET_ACTIVE_BORROWED format.");
						client.sendToClient(errorResponse);
					}
					break;

				case "EXTEND_BORROW":
					if (parts.length == 3) {
						try {
							int borrowId = Integer.parseInt(parts[1]);
							LocalDate selectedDate = LocalDate.parse(parts[2]);

							// Call DBconnector to process the extension
							String result = dbConnector.extendBorrow(borrowId, selectedDate);

							// Send the result back to the client
							client.sendToClient(result);

							// If the extension was successful, send a notification to the librarian
							if (result.startsWith("SUCCESS:")) {
								String description = "The borrow period for Borrow ID: " + borrowId
										+ " has been extended to " + selectedDate + ".";
								dbConnector.sendNotificationToLibrarian(description);
								System.out.println("Librarian notified: " + description);
							}
						} catch (Exception e) {
							client.sendToClient("ERROR: Invalid EXTEND_BORROW request. " + e.getMessage());
						}
					} else {
						client.sendToClient("ERROR: Invalid EXTEND_BORROW format.");
					}
					break;

				case "VIEW_SUBSCRIBER_CARD":
					if (parts.length == 2) {
						int subscriberId = Integer.parseInt(parts[1]);
						String query = "SELECT * FROM Subscriber WHERE sub_id = ?";
						try (PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query)) {
							ps.setInt(1, subscriberId);
							ResultSet rs = ps.executeQuery();
							if (rs.next()) {
								String subscriberCard = "ID: " + rs.getInt("sub_id") + ", Name: "
										+ rs.getString("sub_name") + ", Phone: " + rs.getString("phone_num")
										+ ", Email: " + rs.getString("email_address") + ", Status: "
										+ rs.getString("status");
								client.sendToClient("Subscriber Card: " + subscriberCard);
							} else {
								client.sendToClient("ERROR: Subscriber not found.");
							}
						}
					} else {
						client.sendToClient("ERROR: Invalid VIEW_SUBSCRIBER_CARD format.");
					}
					break;

				default:
					client.sendToClient("ERROR: Unknown command.");
					break;
				}
			} else {
				client.sendToClient("ERROR: Invalid message format.");
			}
		} catch (

		Exception e) {
			System.err.println("Error processing message: " + e.getMessage());
			try {
				client.sendToClient("ERROR: " + e.getMessage());
			} catch (Exception ignored) {
			}
		}

		System.out.println("Finished processing message from client: " + msg);
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		System.out.println("Client connected: IP = " + client.getInetAddress().getHostAddress() + ", Host = "
				+ client.getInetAddress().getHostName());
	}

	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		System.out.println("Client disconnected: IP = " + client.getInetAddress().getHostAddress());
	}
}
