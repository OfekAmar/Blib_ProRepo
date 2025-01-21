package server;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import logic.Book;
import logic.Record;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ServerMain extends AbstractServer {

	private final DBconnector dbConnector;

    private final ScheduledExecutorService scheduler;

	public ServerMain(int port) {
		super(port);
		this.dbConnector = new DBconnector();
		 this.scheduler = Executors.newScheduledThreadPool(1);
	     startDailyOverdueCheck();
	}
	
	private void startDailyOverdueCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkAndFreezeOverdueSubscribers();
            } catch (Exception e) {
                System.err.println("Error during overdue subscriber check: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.DAYS); // Initial delay = 0, repeats every 1 day
    }

	private void checkAndFreezeOverdueSubscribers() {
	    System.out.println("Running daily overdue check...");

	    // Update overdue borrows to 'late' and freeze subscribers
	    String overdueQuery = "SELECT sub_id, borrow_id FROM Borrow WHERE status = 'borrowed' AND return_max_date < ?";
	    String updateBorrowQuery = "UPDATE Borrow SET status = 'late' WHERE borrow_id = ?";
	    String freezeSubscriberQuery = "UPDATE Subscriber SET status = 'frozen' WHERE sub_id = ?";

	    try (PreparedStatement overduePs = dbConnector.getDbConnection().prepareStatement(overdueQuery)) {
	        overduePs.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(7)));
	        ResultSet rs = overduePs.executeQuery();

	        while (rs.next()) {
	            int subscriberId = rs.getInt("sub_id");
	            int borrowId = rs.getInt("borrow_id");

	            // Update the Borrow status to 'late'
	            try (PreparedStatement updateBorrowPs = dbConnector.getDbConnection().prepareStatement(updateBorrowQuery)) {
	                updateBorrowPs.setInt(1, borrowId);
	                int rowsAffected = updateBorrowPs.executeUpdate();
	                if (rowsAffected > 0) {
	                    System.out.println("Borrow ID " + borrowId + " status updated to 'late'.");
	                }
	            }

	            // Freeze the subscriber
	            try (PreparedStatement freezeSubscriberPs = dbConnector.getDbConnection().prepareStatement(freezeSubscriberQuery)) {
	                freezeSubscriberPs.setInt(1, subscriberId);
	                int rowsAffected = freezeSubscriberPs.executeUpdate();
	                if (rowsAffected > 0) {
	                    System.out.println("Subscriber ID " + subscriberId + " has been frozen.");
	                }
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("Error during overdue check: " + e.getMessage());
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
					if (parts.length == 6) {
						String bookCode = parts[1];
						String author = parts[2];
						String title = parts[3];
						String subject = parts[4];
						String description = parts[5];
						dbConnector.updateQuery(
								"INSERT INTO book (book_code, author, title, subject, description) VALUES ('" + bookCode
										+ "', '" + author + "', '" + title + "', '" + subject + "', '" + description
										+ "')");
						client.sendToClient("Book added successfully: " + title);
					} else {
						client.sendToClient("ERROR: Invalid ADD_BOOK format.");
					}
					break;
				case "ADD_SUBSCRIBER":
					if(parts.length==5) {
						String name=parts[1];
						String phone=parts[2];
						String email=parts[3];
						String password=parts[4];
						dbConnector.addSubscriber(name, phone, email, password);
						client.sendToClient("New Subscriber added successfuly:\nname: "+name+"\nphone number: "+phone+"\nemail: "+email);
					}else {
						client.sendToClient("ERROR: Invalid ADD_SUBSCRIBER format.");
					}
					break;
				case "EDIT_SUBSCRIBER":	
					if(parts.length==5) {
						int id=Integer.parseInt(parts[1]);
						String phone=parts[2];
						String email=parts[3];
						String password=parts[4];
						dbConnector.editSubscriber(id, phone, email, password);
						client.sendToClient("edit Subscriber successfuly:\nid: "+id+"\nphone number: "+phone+"\nemail: "+email+"\npassword: "+password);
					}else {
						client.sendToClient("ERROR: Invalid EDIT_SUBSCRIBER format.");
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
				case "BORROW_HISTORY_BY_ID":
					if (parts.length == 2) {
						int id = Integer.valueOf(parts[1]);
						List<String> result = dbConnector.BorrowHistoryByID(id);
						client.sendToClient(new ArrayList<>(result));
					} else {
						client.sendToClient(new ArrayList<>());
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
						String bookId = parts[1];
						String copyId = parts[2];
						String subscriberId = parts[3];
						LocalDate returnDate = LocalDate.parse(parts[4]);

						try {
							// Check the copy's status
							String copyQuery = "SELECT status FROM CopyOfBook WHERE book_code = ? AND copy_id = ?";
							try (PreparedStatement copyPs = dbConnector.getDbConnection().prepareStatement(copyQuery)) {
								copyPs.setString(1, bookId);
								copyPs.setString(2, copyId);
								ResultSet rsCopy = copyPs.executeQuery();

								if (rsCopy.next()) {
									String copyStatus = rsCopy.getString("status");

									if ("exists".equals(copyStatus)) {
										// Update the copy status to 'borrowed'
										String updateCopyQuery = "UPDATE CopyOfBook SET status = 'borrowed' WHERE book_code = ? AND copy_id = ?";
										try (PreparedStatement updateCopyPs = dbConnector.getDbConnection()
												.prepareStatement(updateCopyQuery)) {
											updateCopyPs.setString(1, bookId);
											updateCopyPs.setString(2, copyId);
											updateCopyPs.executeUpdate();
										}

										// Add a new record to the Borrow table
										String borrowQuery = "INSERT INTO Borrow (sub_id, book_code, copy_id, borrow_date, return_max_date, status) VALUES (?, ?, ?, ?, ?, ?)";
										try (PreparedStatement borrowPs = dbConnector.getDbConnection()
												.prepareStatement(borrowQuery, Statement.RETURN_GENERATED_KEYS)) {
											borrowPs.setString(1, subscriberId);
											borrowPs.setString(2, bookId);
											borrowPs.setString(3, copyId);
											borrowPs.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
											borrowPs.setDate(5, java.sql.Date.valueOf(returnDate));
											borrowPs.setString(6, "borrowed");
											borrowPs.executeUpdate();

											ResultSet generatedKeys = borrowPs.getGeneratedKeys();
											if (generatedKeys.next()) {
												client.sendToClient("Borrow record created successfully with ID: "
														+ generatedKeys.getInt(1));
											} else {
												client.sendToClient("ERROR: Borrow record creation failed.");
											}
										}
									} else if ("reserved".equals(copyStatus)) {
										// Handle reserved copies
										String reservationQuery = "SELECT sub_id, status FROM Reserved WHERE book_code = ? AND copy_id = ?";
										try (PreparedStatement resPs = dbConnector.getDbConnection()
												.prepareStatement(reservationQuery)) {
											resPs.setString(1, bookId);
											resPs.setString(2, copyId);
											ResultSet rsReservation = resPs.executeQuery();

											if (rsReservation.next()) {
												String reservedSubId = rsReservation.getString("sub_id");
												String reservationStatus = rsReservation.getString("status");

												if (subscriberId.equals(reservedSubId)
														&& "wait".equals(reservationStatus)) {
													// Update the reservation status to 'borrowed'
													String updateReservationQuery = "UPDATE Reserved SET status = 'taken' WHERE book_code = ? AND copy_id = ?";
													try (PreparedStatement updateResPs = dbConnector.getDbConnection()
															.prepareStatement(updateReservationQuery)) {
														updateResPs.setString(1, bookId);
														updateResPs.setString(2, copyId);
														updateResPs.executeUpdate();
													}

													// Update the copy status to 'borrowed'
													String updateCopyQuery = "UPDATE CopyOfBook SET status = 'borrowed' WHERE book_code = ? AND copy_id = ?";
													try (PreparedStatement updateCopyPs = dbConnector.getDbConnection()
															.prepareStatement(updateCopyQuery)) {
														updateCopyPs.setString(1, bookId);
														updateCopyPs.setString(2, copyId);
														updateCopyPs.executeUpdate();
													}

													// Add a new record to the Borrow table
													String borrowQuery = "INSERT INTO Borrow (sub_id, book_code, copy_id, borrow_date, return_max_date, status) VALUES (?, ?, ?, ?, ?, ?)";
													try (PreparedStatement borrowPs = dbConnector.getDbConnection()
															.prepareStatement(borrowQuery,
																	Statement.RETURN_GENERATED_KEYS)) {
														borrowPs.setString(1, subscriberId);
														borrowPs.setString(2, bookId);
														borrowPs.setString(3, copyId);
														borrowPs.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
														borrowPs.setDate(5, java.sql.Date.valueOf(returnDate));
														borrowPs.setString(6, "borrowed");
														borrowPs.executeUpdate();

														ResultSet generatedKeys = borrowPs.getGeneratedKeys();
														if (generatedKeys.next()) {
															client.sendToClient(
																	"Borrow record created successfully with ID: "
																			+ generatedKeys.getInt(1));
														} else {
															client.sendToClient(
																	"ERROR: Borrow record creation failed.");
														}
													}
												} else {
													client.sendToClient(
															"ERROR: This copy is reserved by another subscriber.");
												}
											} else {
												client.sendToClient("ERROR: No valid reservation found for this copy.");
											}
										}
									} else {
										client.sendToClient("ERROR: Copy is not available for borrowing.");
									}
								} else {
									client.sendToClient("ERROR: Copy does not exist.");
								}
							}
						} catch (Exception e) {
							client.sendToClient("ERROR: " + e.getMessage());
						}
					} else {
						client.sendToClient("ERROR: Invalid PROCESS_BORROW format.");
					}
					break;
					
				case "RETURN_BOOK":
				    if (parts.length == 3) {
				        String bookId = parts[1];
				        String copyId = parts[2];

				        try {
				            // Check if the borrow record exists with status 'borrowed'
				            String borrowQuery = "SELECT * FROM Borrow WHERE book_code = ? AND copy_id = ? AND status = 'borrowed'";
				            try (PreparedStatement borrowPs = dbConnector.getDbConnection().prepareStatement(borrowQuery)) {
				                borrowPs.setString(1, bookId);
				                borrowPs.setString(2, copyId);
				                ResultSet rsBorrow = borrowPs.executeQuery();

				                if (rsBorrow.next()) {
				                    // Change the status in the Borrow table to 'returned'
				                    String updateBorrowQuery = "UPDATE Borrow SET status = 'returned' WHERE book_code = ? AND copy_id = ? AND status = 'borrowed'";
				                    try (PreparedStatement updateBorrowPs = dbConnector.getDbConnection().prepareStatement(updateBorrowQuery)) {
				                        updateBorrowPs.setString(1, bookId);
				                        updateBorrowPs.setString(2, copyId);
				                        updateBorrowPs.executeUpdate();
				                    }

				                    // Check if there is a reservation with status 'borrowed' for this copy
				                    String reserveQuery = "SELECT * FROM Reserved WHERE book_code = ? AND copy_id = ? AND status = 'borrowed'";
				                    try (PreparedStatement reservePs = dbConnector.getDbConnection().prepareStatement(reserveQuery)) {
				                        reservePs.setString(1, bookId);
				                        reservePs.setString(2, copyId);
				                        ResultSet rsReserve = reservePs.executeQuery();

				                        if (rsReserve.next()) {
				                            // Update the reservation status to 'wait'
				                            String updateReserveQuery = "UPDATE Reserved SET status = 'wait' WHERE book_code = ? AND copy_id = ?";
				                            try (PreparedStatement updateReservePs = dbConnector.getDbConnection().prepareStatement(updateReserveQuery)) {
				                                updateReservePs.setString(1, bookId);
				                                updateReservePs.setString(2, copyId);
				                                updateReservePs.executeUpdate();
				                            }

				                            // Update the copy status in CopyOfBook to 'reserved'
				                            String updateCopyQuery = "UPDATE CopyOfBook SET status = 'reserved' WHERE book_code = ? AND copy_id = ?";
				                            try (PreparedStatement updateCopyPs = dbConnector.getDbConnection().prepareStatement(updateCopyQuery)) {
				                                updateCopyPs.setString(1, bookId);
				                                updateCopyPs.setString(2, copyId);
				                                updateCopyPs.executeUpdate();
				                            }

				                            client.sendToClient("Book returned successfully. The copy is now reserved.");
				                        } else {
				                            // No reservation exists, update the copy status to 'exists'
				                            String updateCopyQuery = "UPDATE CopyOfBook SET status = 'exists' WHERE book_code = ? AND copy_id = ?";
				                            try (PreparedStatement updateCopyPs = dbConnector.getDbConnection().prepareStatement(updateCopyQuery)) {
				                                updateCopyPs.setString(1, bookId);
				                                updateCopyPs.setString(2, copyId);
				                                updateCopyPs.executeUpdate();
				                            }

				                            client.sendToClient("Book returned successfully. The copy is now available.");
				                        }
				                    }
				                } else {
				                    client.sendToClient("ERROR: This borrow does not exist or the copy is not borrowed.");
				                }
				            }
				        } catch (Exception e) {
				            client.sendToClient("ERROR: " + e.getMessage());
				        }
				    } else {
				        client.sendToClient("ERROR: Invalid RETURN_BOOK format.");
				    }
				    break;
				    
				    
				case "GET_ACTIVE_BORROWED":
				    if (parts.length == 2) {
				        String subscriberId = parts[1];
				        try {
				            String query = "SELECT book_code, copy_id FROM Borrow WHERE sub_id = ? AND status = 'borrowed'";
				            PreparedStatement ps = dbConnector.getDbConnection().prepareStatement(query);
				            ps.setString(1, subscriberId);
				            ResultSet rs = ps.executeQuery();

				            List<String> borrowedBooks = new ArrayList<>();
				            while (rs.next()) {
				                borrowedBooks.add("Book ID: " + rs.getString("book_code") + ", Copy ID: " + rs.getString("copy_id"));
				            }

				            client.sendToClient(borrowedBooks);
				        } catch (Exception e) {
				            client.sendToClient("ERROR: Failed to fetch borrowed books: " + e.getMessage());
				        }
				    } else {
				        client.sendToClient("ERROR: Invalid GET_ACTIVE_BORROWED format.");
				    }
				    break;
					
					

				case "EXTEND_BORROW":
					if (parts.length == 2) {
						String borrowId = parts[1];

						try {
							// Validate that the max return date is within 7 days
							String dateQuery = "SELECT return_max_date FROM Borrow WHERE borrow_id = ?";
							LocalDate returnMaxDate;
							try (PreparedStatement datePs = dbConnector.getDbConnection().prepareStatement(dateQuery)) {
								datePs.setString(1, borrowId);
								ResultSet rsDate = datePs.executeQuery();
								if (rsDate.next()) {
									returnMaxDate = rsDate.getDate("return_max_date").toLocalDate();
									LocalDate currentDate = LocalDate.now();

									if (returnMaxDate.isBefore(currentDate)
											|| returnMaxDate.isAfter(currentDate.plusDays(7))) {
										client.sendToClient(
												"ERROR: Borrow cannot be extended. Return date is not within 7 days of the current date.");
										break;
									}
								} else {
									client.sendToClient("ERROR: Borrow record not found.");
									break;
								}
							}

							// Check if the copy is reserved
							String reserveQuery = "SELECT status FROM Reserved WHERE book_code = (SELECT book_code FROM Borrow WHERE borrow_id = ?) AND copy_id = (SELECT copy_id FROM Borrow WHERE borrow_id = ?) AND status = 'borrowed'";
							try (PreparedStatement reservePs = dbConnector.getDbConnection()
									.prepareStatement(reserveQuery)) {
								reservePs.setString(1, borrowId);
								reservePs.setString(2, borrowId);
								ResultSet rsReserve = reservePs.executeQuery();
								if (rsReserve.next()) {
									client.sendToClient("ERROR: Borrow cannot be extended. This copy is reserved.");
									break;
								}
							}

							// Extend the max return date by 7 days
							String updateQuery = "UPDATE Borrow SET return_max_date = return_max_date + INTERVAL 7 DAY WHERE borrow_id = ?";
							try (PreparedStatement updatePs = dbConnector.getDbConnection()
									.prepareStatement(updateQuery)) {
								updatePs.setString(1, borrowId);
								int rowsAffected = updatePs.executeUpdate();
								if (rowsAffected > 0) {
									client.sendToClient(
											"Borrow extended successfully. 7 days added to the return max date.");
								} else {
									client.sendToClient("ERROR: Failed to extend borrow. No rows updated.");
								}
							}
						} catch (Exception e) {
							client.sendToClient("ERROR: " + e.getMessage());
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
