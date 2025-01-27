package server;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.Book;
import logic.CopyOfBook;
import logic.ExtendedRecord;
import logic.Librarian;
import logic.NotificationData;
import logic.Record;
import logic.Subscriber;

/**
 * The 'DBconnector' class is a singleton class that manages the connection to
 * the MySQL database. This class ensures only a single instance of the database
 * connection is created, and provides a global access point to that connection.
 */
public class DBconnector {
	private static volatile DBconnector instance;
	private Connection dbConnection;

	/**
	 * Private constructor to initialize the database connection. This constructor
	 * is called only once when the singleton instance is created.
	 */
	private DBconnector() {
		connectToDatabase();
	}

	/**
	 * Returns the singleton instance of the DBconnector class. Uses double-checked
	 * locking to ensure thread-safe initialization of the instance.
	 *
	 * @return the singleton instance of the DBconnector
	 */
	public static DBconnector getInstance() {
		if (instance == null) {
			synchronized (DBconnector.class) {
				if (instance == null) {
					instance = new DBconnector();
				}
			}
		}
		return instance;
	}

	/**
	 * Establishes a connection to the MySQL database. If the connection fails, the
	 * program will terminate with an error message.
	 */
	private void connectToDatabase() {
		try {
			dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/librarydb?serverTimezone=UTC", "root",
					"Aa123456");
			System.out.println("Database connected successfully.");
		} catch (SQLException e) {
			System.err.println("Database connection failed: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Retrieves the current database connection. If the connection is not
	 * initialized, an exception will be thrown.
	 *
	 * @return the current {@link Connection} to the database.
	 * @throws IllegalStateException if the database connection has not been
	 *                               initialized.
	 */
	public Connection getDbConnection() {
		if (dbConnection == null) {
			throw new IllegalStateException("Database connection is not initialized.");
		}
		return dbConnection;
	}

	/**
	 * Processes the borrowing of a book by a subscriber. Performs the following
	 * steps:
	 * 
	 * Checks the status of the copy (reserved or exists). If reserved, verifies the
	 * reservation belongs to the subscriber. Updates the reservation status, copy
	 * status, and the amount of reservations. Inserts a borrow record and logs the
	 * borrowing in the Records table.
	 * 
	 * 
	 * @param bookId       the ID of the book being borrowed
	 * @param copyId       the ID of the specific copy being borrowed
	 * @param subscriberId the ID of the subscriber borrowing the book
	 * @param returnDate   the maximum return date for the book
	 * @return a success message if the process is completed successfully, or an
	 *         error message if any issues occur
	 */
	public String processBorrow(int bookId, int copyId, int subscriberId, LocalDate returnDate) {
		String copyStatusQuery = "SELECT status FROM CopyOfBook WHERE book_code = ? AND copy_id = ?";
		String reservationCheckQuery = "SELECT * FROM Reserved WHERE book_code = ? AND copy_id = ? AND sub_id = ? AND order_status = 'waiting'";
		String updateReservationQuery = "UPDATE Reserved SET order_status = 'borrowed' WHERE order_id = ?";
		String updateCopyStatusQuery = "UPDATE CopyOfBook SET status = 'borrowed' WHERE book_code = ? AND copy_id = ?";
		String decreaseReservationCountQuery = "UPDATE Book SET amount_of_reservations = amount_of_reservations - 1 WHERE book_code = ?";
		String insertBorrowQuery = "INSERT INTO Borrow (sub_id, book_code, copy_id, borrow_date, return_max_date, status) VALUES (?, ?, ?, ?, ?, ?)";
		String insertRecordQuery = "INSERT INTO Records (record_type, sub_id, record_date, book_code) VALUES (?, ?, ?, ?)";

		try {
			// Check the copy's current status
			try (PreparedStatement copyStatusPs = dbConnection.prepareStatement(copyStatusQuery)) {
				copyStatusPs.setInt(1, bookId);
				copyStatusPs.setInt(2, copyId);
				ResultSet rsCopyStatus = copyStatusPs.executeQuery();

				if (rsCopyStatus.next()) {
					String copyStatus = rsCopyStatus.getString("status");

					if ("reserved".equals(copyStatus)) {
						// Check if there's a matching reservation for this subscriber and copy
						try (PreparedStatement reservationCheckPs = dbConnection
								.prepareStatement(reservationCheckQuery)) {
							reservationCheckPs.setInt(1, bookId);
							reservationCheckPs.setInt(2, copyId);
							reservationCheckPs.setInt(3, subscriberId);
							ResultSet rsReservationCheck = reservationCheckPs.executeQuery();

							if (rsReservationCheck.next()) {
								int orderId = rsReservationCheck.getInt("order_id");

								// Update reservation status to 'borrowed'
								try (PreparedStatement updateReservationPs = dbConnection
										.prepareStatement(updateReservationQuery)) {
									updateReservationPs.setInt(1, orderId);
									updateReservationPs.executeUpdate();
								}

								// Update the copy's status to 'borrowed'
								try (PreparedStatement updateCopyStatusPs = dbConnection
										.prepareStatement(updateCopyStatusQuery)) {
									updateCopyStatusPs.setInt(1, bookId);
									updateCopyStatusPs.setInt(2, copyId);
									updateCopyStatusPs.executeUpdate();
								}

								// Decrease the amount_of_reservations in the Book table
								try (PreparedStatement decreaseReservationCountPs = dbConnection
										.prepareStatement(decreaseReservationCountQuery)) {
									decreaseReservationCountPs.setInt(1, bookId);
									decreaseReservationCountPs.executeUpdate();
								}

								// Insert a borrow record
								try (PreparedStatement insertBorrowPs = dbConnection
										.prepareStatement(insertBorrowQuery)) {
									insertBorrowPs.setInt(1, subscriberId);
									insertBorrowPs.setInt(2, bookId);
									insertBorrowPs.setInt(3, copyId);
									insertBorrowPs.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
									insertBorrowPs.setDate(5, java.sql.Date.valueOf(returnDate));
									insertBorrowPs.setString(6, "borrowed");
									insertBorrowPs.executeUpdate();
								}

								// Insert a record in the Records table
								try (PreparedStatement insertRecordPs = dbConnection
										.prepareStatement(insertRecordQuery)) {
									insertRecordPs.setString(1, "borrowed");
									insertRecordPs.setInt(2, subscriberId);
									insertRecordPs.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
									insertRecordPs.setInt(4, bookId);
									insertRecordPs.executeUpdate();
								}

								return "Borrow process completed successfully.";
							} else {
								return "ERROR: No matching reservation found for this subscriber.";
							}
						}
					} else if ("exists".equals(copyStatus)) {
						// Handle normal borrowing process if the copy status is 'exists'

						// Update the copy's status to 'borrowed'
						try (PreparedStatement updateCopyStatusPs = dbConnection
								.prepareStatement(updateCopyStatusQuery)) {
							updateCopyStatusPs.setInt(1, bookId);
							updateCopyStatusPs.setInt(2, copyId);
							updateCopyStatusPs.executeUpdate();
						}

						// Insert a borrow record
						try (PreparedStatement insertBorrowPs = dbConnection.prepareStatement(insertBorrowQuery)) {
							insertBorrowPs.setInt(1, subscriberId);
							insertBorrowPs.setInt(2, bookId);
							insertBorrowPs.setInt(3, copyId);
							insertBorrowPs.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
							insertBorrowPs.setDate(5, java.sql.Date.valueOf(returnDate));
							insertBorrowPs.setString(6, "borrowed");
							insertBorrowPs.executeUpdate();
						}

						// Insert a record in the Records table
						try (PreparedStatement insertRecordPs = dbConnection.prepareStatement(insertRecordQuery)) {
							insertRecordPs.setString(1, "borrowed");
							insertRecordPs.setInt(2, subscriberId);
							insertRecordPs.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
							insertRecordPs.setInt(4, bookId);
							insertRecordPs.executeUpdate();
						}

						return "Borrow process completed successfully.";
					} else {
						return "ERROR: Copy is not available for borrowing.";
					}
				} else {
					return "ERROR: Copy does not exist.";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}

	/**
	 * Processes the return of a borrowed book. Performs the following steps:
	 *
	 * Checks if there is an active borrow record for the specified book copy. Marks
	 * the borrow record as 'returned'. Checks for existing reservations for the
	 * returned book. If a reservation exists, assigns the returned copy to the
	 * reservation, updates its status to 'reserved', and notifies the subscriber.
	 * If no reservation exists, updates the copy's status to 'exists'. Records the
	 * return action in the Records table.
	 * 
	 *
	 * @param bookId the ID of the book being returned
	 * @param copyId the ID of the specific copy being returned
	 * @return a success message if the process is completed successfully, or an
	 *         error message if any issues occur
	 */
	public String processReturnBook(int bookId, int copyId) {
		String borrowQuery = "SELECT * FROM Borrow WHERE book_code = ? AND copy_id = ? AND status = 'borrowed'";
		String updateBorrowQuery = "UPDATE Borrow SET status = 'returned' WHERE book_code = ? AND copy_id = ?";
		String reservationQuery = "SELECT * FROM Reserved WHERE book_code = ? AND order_status = 'waiting' AND copy_id IS NULL ORDER BY res_date ASC, res_time ASC LIMIT 1";
		String updateReservationQuery = "UPDATE Reserved SET copy_id = ?, res_max_date = ?, order_status = 'waiting' WHERE order_id = ?";
		String updateCopyToReservedQuery = "UPDATE CopyOfBook SET status = 'reserved' WHERE book_code = ? AND copy_id = ?";
		String updateCopyToExistsQuery = "UPDATE CopyOfBook SET status = 'exists' WHERE book_code = ? AND copy_id = ?";
		String bookNameQuery = "SELECT title FROM Book WHERE book_code = ?";
		String insertRecordQuery = "INSERT INTO Records (record_type, sub_id, record_date, book_code) VALUES (?, ?, ?, ?)";

		try {
			// Check if the borrow record exists with status 'borrowed'
			try (PreparedStatement borrowPs = dbConnection.prepareStatement(borrowQuery)) {
				borrowPs.setInt(1, bookId);
				borrowPs.setInt(2, copyId);
				ResultSet rsBorrow = borrowPs.executeQuery();

				if (rsBorrow.next()) {
					int subscriberId = rsBorrow.getInt("sub_id");

					// Mark the borrow record as returned
					try (PreparedStatement updateBorrowPs = dbConnection.prepareStatement(updateBorrowQuery)) {
						updateBorrowPs.setInt(1, bookId);
						updateBorrowPs.setInt(2, copyId);
						updateBorrowPs.executeUpdate();
					}

					boolean reserved = false;

					// Fetch the book name
					String bookName = "";
					try (PreparedStatement bookNamePs = dbConnection.prepareStatement(bookNameQuery)) {
						bookNamePs.setInt(1, bookId);
						ResultSet rsBookName = bookNamePs.executeQuery();
						if (rsBookName.next()) {
							bookName = rsBookName.getString("title");
						}
					}

					// Check for reservations
					try (PreparedStatement reservationPs = dbConnection.prepareStatement(reservationQuery)) {
						reservationPs.setInt(1, bookId);
						ResultSet rsReservation = reservationPs.executeQuery();

						if (rsReservation.next()) {
							reserved = true;
							int orderId = rsReservation.getInt("order_id");
							int reservedSubscriberId = rsReservation.getInt("sub_id");

							// Calculate the res_max_date (2 days from today)
							LocalDate resMaxDate = LocalDate.now().plusDays(2);

							// Update reservation with the returned copy ID and res_max_date
							try (PreparedStatement updateReservationPs = dbConnection
									.prepareStatement(updateReservationQuery)) {
								updateReservationPs.setInt(1, copyId);
								updateReservationPs.setDate(2, java.sql.Date.valueOf(resMaxDate));
								updateReservationPs.setInt(3, orderId);
								updateReservationPs.executeUpdate();
							}

							// Update the copy's status to 'reserved'
							try (PreparedStatement updateCopyPs = dbConnection
									.prepareStatement(updateCopyToReservedQuery)) {
								updateCopyPs.setInt(1, bookId);
								updateCopyPs.setInt(2, copyId);
								updateCopyPs.executeUpdate();
							}

							// Notify the subscriber that the book is waiting for them
							String notificationDescription = "The book you reserved (" + bookName
									+ ") is now waiting for you. Please collect it by " + resMaxDate + ".";
							sendNotificationToSubscriber(reservedSubscriberId, notificationDescription);
							System.out.println("Notification sent to Subscriber ID " + reservedSubscriberId + ": "
									+ notificationDescription);
						}
					}

					// If no reservation exists, update the copy's status to 'exists'
					if (!reserved) {
						try (PreparedStatement updateCopyPs = dbConnection.prepareStatement(updateCopyToExistsQuery)) {
							updateCopyPs.setInt(1, bookId);
							updateCopyPs.setInt(2, copyId);
							updateCopyPs.executeUpdate();
						}
					}

					// Always record the return action in the Records table
					try (PreparedStatement recordPs = dbConnection.prepareStatement(insertRecordQuery)) {
						recordPs.setString(1, "returned");
						recordPs.setInt(2, subscriberId);
						recordPs.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
						recordPs.setInt(4, bookId);
						recordPs.executeUpdate();
					}

					return "Book returned successfully.";
				} else {
					return "ERROR: No active borrow record found for this copy.";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}

	/**
	 * Extends the borrow period for a specific borrow record. Validates that the
	 * borrow can be extended based on the current return date, requested date, and
	 * reservation status of the book.
	 *
	 * 
	 * Steps performed:
	 * 
	 * Fetches the current maximum return date and book information from the Borrow
	 * table. Ensures there are no reservations for the book. Validates that the
	 * requested date is within 7 days from the current maximum return date. Updates
	 * the borrow record with the new return date. Logs the extension action in the
	 * Records table.
	 *
	 * @param borrowId      the ID of the borrow record to extend
	 * @param requestedDate the new requested return date
	 * @return a success message if the borrow period is extended successfully, or
	 *         an error message if validation fails or an error occurs
	 */
	public String extendBorrow(int borrowId, LocalDate requestedDate) {
		String borrowQuery = "SELECT return_max_date, book_code, sub_id FROM Borrow WHERE borrow_id = ?";
		String reservationCheckQuery = "SELECT amount_of_reservations FROM Book WHERE book_code = ?";
		String updateBorrowQuery = "UPDATE Borrow SET return_max_date = ? WHERE borrow_id = ?";
		String insertRecordQuery = "INSERT INTO Records (record_type, sub_id, record_date, book_code) "
				+ "VALUES ('extend', ?, ?, ?)";

		try {
			LocalDate maxReturnDate;
			int bookCode;
			int subId;

			// Fetch max_return_date and book_code from the Borrow table
			try (PreparedStatement borrowPs = dbConnection.prepareStatement(borrowQuery)) {
				borrowPs.setInt(1, borrowId);
				ResultSet rs = borrowPs.executeQuery();

				if (rs.next()) {
					maxReturnDate = rs.getDate("return_max_date").toLocalDate();
					bookCode = rs.getInt("book_code");
					subId = rs.getInt("sub_id");
				} else {
					return "ERROR: Borrow record not found.";
				}
			}

			// Check if there is more than 7 days remaining to the return_max_date from
			// today
			if (maxReturnDate.isAfter(LocalDate.now().plusDays(7))) {
				return "ERROR: Cannot extend borrow period. There are more than 7 days remaining to the current return date ("
						+ maxReturnDate + ").";
			}

			// Validate that the requested date is not more than 7 days from the
			// max_return_date
			if (requestedDate.isAfter(maxReturnDate.plusDays(7))) {
				return "ERROR: The selected due date cannot be more than 7 days from the current max return date ("
						+ maxReturnDate + ").";
			}

			// Check if there are reservations for the book
			try (PreparedStatement reservationCheckPs = dbConnection.prepareStatement(reservationCheckQuery)) {
				reservationCheckPs.setInt(1, bookCode);
				ResultSet rs = reservationCheckPs.executeQuery();

				if (rs.next()) {
					int reservations = rs.getInt("amount_of_reservations");

					if (reservations > 0) {
						return "ERROR: Cannot extend borrow period. There are reservations for this book.";
					}
				}
			}

			// Update the borrow period
			try (PreparedStatement updateBorrowPs = dbConnection.prepareStatement(updateBorrowQuery)) {
				updateBorrowPs.setDate(1, java.sql.Date.valueOf(requestedDate));
				updateBorrowPs.setInt(2, borrowId);
				int rowsUpdated = updateBorrowPs.executeUpdate();

				if (rowsUpdated == 0) {
					return "ERROR: Failed to extend borrow period.";
				}
			}

			// Insert a record into the Records table
			try (PreparedStatement insertRecordPs = dbConnection.prepareStatement(insertRecordQuery)) {
				insertRecordPs.setInt(1, subId);
				insertRecordPs.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
				insertRecordPs.setInt(3, bookCode);
				insertRecordPs.executeUpdate();
			}

			return "SUCCESS: Borrow period extended successfully.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}

	/**
	 * Extends the borrow period for a specified borrow record by 7 days. The method
	 * performs the following steps:
	 * 
	 * Checks if there are active reservations for the book associated with the
	 * borrow record. If no active reservations exist, updates the borrow record to
	 * extend the return date by 7 days. Logs the extension in the Records table for
	 * the subscriber and the book.
	 * 
	 *
	 * @param borrowId the ID of the borrow record to extend
	 * @return a success message if the borrow period is extended successfully, or
	 *         an error message if validation fails or an exception occurs
	 */
	public String extendBorrow(int borrowId) {
		String reservationCheckQuery = "SELECT b.amount_of_reservations FROM Book b JOIN Borrow bo ON b.book_code = bo.book_code WHERE bo.borrow_id = ?";
		String extendBorrowQuery = "UPDATE Borrow SET return_max_date = return_max_date + INTERVAL 7 DAY WHERE borrow_id = ?";
		String insertRecordQuery = "INSERT INTO Records (record_type, sub_id, record_date, book_code) VALUES ('extend', ?, ?, ?)";

		try {
			// Check if the book has active reservations
			try (PreparedStatement reservationCheckPs = dbConnection.prepareStatement(reservationCheckQuery)) {
				reservationCheckPs.setInt(1, borrowId);
				ResultSet rs = reservationCheckPs.executeQuery();

				if (rs.next()) {
					int amountOfReservations = rs.getInt("amount_of_reservations");

					if (amountOfReservations > 0) {
						return "ERROR: Cannot extend borrow. The book has active reservations.";
					}
				} else {
					return "ERROR: Borrow record not found.";
				}
			}

			// Extend the borrow period
			try (PreparedStatement extendBorrowPs = dbConnection.prepareStatement(extendBorrowQuery)) {
				extendBorrowPs.setInt(1, borrowId);
				int rowsAffected = extendBorrowPs.executeUpdate();

				if (rowsAffected == 0) {
					return "ERROR: Failed to extend borrow period.";
				}
			}

			// Insert a record in the Records table
			String subscriberQuery = "SELECT sub_id, book_code FROM Borrow WHERE borrow_id = ?";
			try (PreparedStatement subscriberPs = dbConnection.prepareStatement(subscriberQuery)) {
				subscriberPs.setInt(1, borrowId);
				ResultSet rs = subscriberPs.executeQuery();

				if (rs.next()) {
					int subscriberId = rs.getInt("sub_id");
					int bookCode = rs.getInt("book_code");

					try (PreparedStatement insertRecordPs = dbConnection.prepareStatement(insertRecordQuery)) {
						insertRecordPs.setInt(1, subscriberId);
						insertRecordPs.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
						insertRecordPs.setInt(3, bookCode);
						insertRecordPs.executeUpdate();
					}
				} else {
					return "ERROR: Failed to retrieve subscriber or book information for the borrow record.";
				}
			}

			return "Borrow period extended successfully.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}

	/**
	 * Retrieves a book by its unique code from the database. The method fetches all
	 * details about the book, including its title, author, subject, description,
	 * and the number of available copies.
	 *
	 * @param bookCode the unique code of the book to be retrieved
	 * @return a {@link Book} object containing all the details of the book
	 * @throws SQLException             if a database access error occurs
	 * @throws IllegalArgumentException if no book with the specified code is found
	 */

	public Book getBookByCode(int bookCode) throws SQLException {
		String query = "SELECT * FROM book WHERE book_code = ?";
		// Subscriber sub;
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return new Book(rs.getString("title"), rs.getInt("book_code"), rs.getString("author"),
					rs.getString("subject"), rs.getString("description"), rs.getInt("amount_of_copies"));
		} else {
			throw new IllegalArgumentException("Book with code " + bookCode + "not found.");
		}
	}

	/**
	 * Retrieves a list of all books from the database. The method fetches details
	 * for each book, including its title, author, subject, description, and the
	 * number of available copies, and returns a list of {@link Book} objects.
	 *
	 * @return a list of {@link Book} objects containing details of all books in the
	 *         database
	 * @throws SQLException if a database access error occurs
	 */
	public List<Book> getAllBooks() throws SQLException {
		List<Book> booksList = new ArrayList<>();
		Book book;
		String query = "SELECT * FROM book";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			book = new Book(rs.getString("title"), rs.getInt("book_code"), rs.getString("author"),
					rs.getString("subject"), rs.getString("description"), rs.getInt("amount_of_copies"));
			booksList.add(book);
		}
		return booksList;
	}

	/**
	 * Retrieves a list of all copies of a specific book from the database. The
	 * method fetches details for each copy, including its location and status, and
	 * returns a list of {@link CopyOfBook} objects.
	 *
	 * @param bookCode the unique identifier of the book for which copies are to be
	 *                 fetched.
	 * @return a list of {@link CopyOfBook} objects containing details of all copies
	 *         of the specified book.
	 * @throws SQLException if a database access error occurs while retrieving the
	 *                      data.
	 */
	public List<CopyOfBook> getAllBookCopies(int bookCode) throws SQLException {
		List<CopyOfBook> copiesList = new ArrayList<>();
		CopyOfBook cb;
		String query = "SELECT * FROM copyofbook WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			cb = new CopyOfBook(rs.getInt("book_code"), rs.getInt("copy_id"), rs.getString("location"),
					rs.getString("status"));
			copiesList.add(cb);
		}
		return copiesList;
	}

	/**
	 * Adds a new book to the database. The book is added with its author, title,
	 * subject, and description. The initial amount of copies for the book is set to
	 * 0.
	 *
	 * @param author      the author of the book
	 * @param title       the title of the book
	 * @param subject     the subject or genre of the book
	 * @param description a brief description of the book
	 * @throws SQLException if a database access error occurs
	 */
	public void addBook(String author, String title, String subject, String description) throws SQLException {
		String query = "INSERT INTO book (author, title, subject, description, amount_of_copies) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, author);
		ps.setString(2, title);
		ps.setString(3, subject);
		ps.setString(4, description);
		ps.setInt(5, 0);
		ps.executeUpdate();
	}

	/**
	 * Adds a new copy of a book to the database. The copy is associated with the
	 * specified book code and includes a location. The status of the new copy is
	 * set to "exists". Additionally, the total number of copies for the book in the
	 * `book` table is incremented.
	 *
	 * @param bookCode the book id to which the new copy belongs
	 * @param location the location where the new copy is stored
	 * @throws SQLException if a database access error occurs
	 */
	public void addCopyOfBook(int bookCode, String location) throws SQLException {
		String query = "INSERT INTO copyofbook (book_code, copy_id, location, status) SELECT ?, COALESCE(MAX(copy_id),0)+1, ?, 'exists' FROM copyofbook WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ps.setString(2, location);
		ps.setInt(3, bookCode);
		ps.executeUpdate();
		query = "UPDATE book SET amount_of_copies = amount_of_copies+1 WHERE book_code = ?";
		ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ps.executeUpdate();
	}

	/**
	 * Updates the location and status of a specific copy of a book in the database.
	 *
	 * @param bookCode    the book id to which the book to which the copy belongs
	 * @param copyId      the id of the specific copy being updated
	 * @param newLocation the new location of the copy
	 * @param newStatus   the new status of the copy
	 * @throws SQLException if a database access error occurs
	 */
	public void editCopyOfBook(int bookCode, int copyId, String newLocation, String newStatus) throws SQLException {
		String query = "UPDATE copyofbook SET location = ?, status = ? WHERE book_code =? AND copy_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, newLocation);
		ps.setString(2, newStatus);
		ps.setInt(3, bookCode);
		ps.setInt(4, copyId);
		ps.executeUpdate();
	}

	/**
	 * Retrieves a list of all subscribers from the database and thier details.
	 *
	 * @return a list of {@link Subscriber} objects containing details of all
	 *         subscribers
	 * @throws SQLException if a database access error occurs
	 */
	public List<Subscriber> getAllSubscribers() throws SQLException {
		List<Subscriber> subscribersList = new ArrayList<>();
		Subscriber sub;
		String query = "SELECT * FROM subscriber";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			sub = new Subscriber(rs.getInt("sub_id"), rs.getString("sub_name"), rs.getString("phone_num"),
					rs.getString("status"), rs.getString("email_address"), rs.getString("password"),
					rs.getString("user_name"));
			subscribersList.add(sub);
		}
		return subscribersList;
	}

	/**
	 * Searches for a subscriber in the database by their id. If a subscriber with
	 * the specified id exists, their details are retrieved and returned as a
	 * {@link Subscriber} object.
	 *
	 * @param id the unique ID of the subscriber to search for
	 * @return a {@link Subscriber} object containing the details of the subscriber
	 * @throws SQLException             if a database access error occurs
	 * @throws IllegalArgumentException if no subscriber with the specified ID is
	 *                                  found
	 */
	public Subscriber searchSubscriberById(int id) throws SQLException {
		String query = "SELECT * FROM subscriber WHERE sub_id = ?";
		// Subscriber sub;
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return new Subscriber(rs.getInt("sub_id"), rs.getString("sub_name"), rs.getString("phone_num"),
					rs.getString("status"), rs.getString("email_address"), rs.getString("password"),
					rs.getString("user_name"));
		} else {
			throw new IllegalArgumentException("Subscriber with ID " + id + "not found.");
		}
	}

	/**
	 * Updates the details of an existing subscriber in the database. Specifically,
	 * this method updates the subscriber's phone number, email address, and
	 * password based on their unique ID.
	 *
	 * @param id          the id of the subscriber whose details are to be updated
	 * @param phoneNumber the new phone number of the subscriber
	 * @param email       the new email address of the subscriber
	 * @param password    the new password of the subscriber
	 * @throws SQLException if a database access error occurs while updating the
	 *                      subscriber details.
	 */
	public void editSubscriber(int id, String phoneNumber, String email, String password) throws SQLException {
		String query = "UPDATE subscriber SET phone_num = ?, email_address = ?, password = ? WHERE sub_id = ?";
		PreparedStatement pstmt = dbConnection.prepareStatement(query);
		pstmt.setString(1, phoneNumber);
		pstmt.setString(2, email);
		pstmt.setString(3, password);
		pstmt.setInt(4, id);
		pstmt.executeUpdate();
	}

	/**
	 * Executes a query that retrieves data from the database.
	 * 
	 * @param query the SQL query to execute, typically a SELECT statement.
	 * @return a ResultSet containing the results of the query.
	 * @throws SQLException if a database access error occurs or the SQL query is
	 *                      invalid.
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		Statement statement = dbConnection.createStatement();
		return statement.executeQuery(query);
	}

	/**
	 * Executes a general update query that modifies data in the database.
	 * 
	 * @param query the SQL query to execute, typically an UPDATE, INSERT, or DELETE
	 *              statement.
	 * @return the number of rows affected by the query.
	 * @throws SQLException if a database access error occurs or the SQL query is
	 *                      invalid.
	 */
	public int updateQuery(String query) throws SQLException {
		Statement statement = dbConnection.createStatement();
		int rowsAffected = statement.executeUpdate(query);
		System.out.println("Update Query Executed: " + query + " | Rows Affected: " + rowsAffected);
		return rowsAffected;
	}

	/**
	 * Searches for a book in the database by its book id.
	 *
	 * @param bookID the unique ID of the book to search for
	 * @return the title of the book if found, or {@code null} if no book with the
	 *         specified ID exists
	 * @throws SQLException if a database access error occurs
	 */
	public String searchBookByID(int bookID) throws SQLException {
		String query = "SELECT title FROM book WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("title");
		}
		return null;
	}

	/**
	 * Searches for a book in the database by its title. The method retrieves the
	 * book's code and checks its availability.
	 * 
	 * If the book is available, its location is returned. If the book is not
	 * available, the earliest return date is provided, along with an option to
	 * order the book.
	 * 
	 *
	 * @param title the title of the book to search for
	 * @return a message indicating whether the book is available, its location if
	 *         available, or the earliest return date and order option if it is not
	 *         available
	 * @throws SQLException if a database access error occurs
	 */
	public String searchBookByTitle(String title) throws SQLException {
		String query = "SELECT book_code FROM book WHERE title = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, title);
		ResultSet rs = ps.executeQuery();
		int bookCodeResult = 0;
		if (rs.next()) {
			bookCodeResult = rs.getInt(1);
		} else {
			return "No book found with the given title";
		}
		query = "SELECT location FROM copyofbook WHERE book_code=? AND status=?";
		ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCodeResult);
		ps.setString(2, "exists");
		rs = ps.executeQuery();
		if (rs.next()) {
			return "The book is available for borrowing\nThe book location is: " + rs.getString(1);
		} else {
			return "The book is not available for borrowing\nEarliest return date is: "
					+ earliestReturnDate(bookCodeResult) + "\nYou can order the book by its book ID: " + bookCodeResult;
		}
	}

	/**
	 * Searches for books in the database by their subject. The method retrieves all
	 * books that match the specified subject.
	 *
	 * @param subject the subject of the books to search for
	 * @return a list of {@link Book} objects that match the specified subject
	 * @throws SQLException if a database access error occurs
	 */
	public List<Book> searchBookBySubject(String subject) throws SQLException {
		List<Book> bookList = new ArrayList<>();
		Book b;
		String query = "SELECT * FROM book WHERE subject = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, subject);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			b = new Book(rs.getString("title"), rs.getInt("book_code"), rs.getString("author"), rs.getString("subject"),
					rs.getString("description"), rs.getInt("amount_of_copies"));
			bookList.add(b);
		}
		return bookList;
	}

	/**
	 * Searches for books in the database using free text. The method performs a
	 * case-insensitive search for books where the free text matches the title,
	 * author, or description of the book (partially or fully).
	 *
	 * @param freeText the free text to search for in the title, author, or
	 *                 description
	 * @return a list of {@link Book} objects that match the free text criteria
	 * @throws SQLException if a database access error occurs
	 */
	public List<Book> searchBookByFreeText(String freeText) throws SQLException {
		List<Book> bookList = new ArrayList<>();
		Book b;
		freeText = '%' + freeText + '%';
		String query = "SELECT * FROM book WHERE title LIKE ? OR author LIKE ? OR description LIKE ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, freeText);
		ps.setString(2, freeText);
		ps.setString(3, freeText);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			b = new Book(rs.getString("title"), rs.getInt("book_code"), rs.getString("author"), rs.getString("subject"),
					rs.getString("description"), rs.getInt("amount_of_copies"));
			bookList.add(b);
		}
		return bookList;
	}

	/**
	 * Retrieves the earliest return date for a specific book from the database. The
	 * method calculates the minimum value of `return_max_date` from the `borrow`
	 * table for the specified book code.
	 *
	 * @param bookCode the unique code of the book to search for
	 * @return a {@link String} representing the earliest return date, or
	 *         {@code null} if no borrow records are found for the specified book
	 * @throws SQLException if a database access error occurs
	 */
	public String earliestReturnDate(int bookCode) throws SQLException {
		String query = "SELECT MIN(return_max_date) AS earliest FROM borrow WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("earliest");
		} else {
			return null;
		}
	}

	/**
	 * Retrieves the borrowing history of a specific subscriber by their unique ID.
	 * The method returns details of all books borrowed by the subscriber, including
	 * the borrow ID, book title, borrow date, and maximum return date.
	 *
	 * @param subscriberID the unique ID of the subscriber
	 * @return a map where the key is the borrow ID, and the value is another map
	 *         containing:
	 * 
	 *         "title" - the title of the borrowed book
	 * 
	 *         "borrowDate" - the borrow date in the format `yyyy-MM-dd`
	 * 
	 *         "returnDate" - the maximum return date in the format `yyyy-MM-dd`
	 * @throws SQLException if a database access error occurs
	 */
	public Map<Integer, Map<String, String>> BorrowByID(int subscriberID) throws SQLException {
		Map<Integer, Map<String, String>> borrowHistory = new HashMap<>();

		String query = "SELECT b.borrow_id, b.book_code, bk.title, b.borrow_date, b.return_max_date " + "FROM borrow b "
				+ "JOIN book bk ON b.book_code = bk.book_code " + "WHERE b.sub_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subscriberID);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			int borrowId = rs.getInt("borrow_id");
			Map<String, String> record = new HashMap<>();
			record.put("title", rs.getString("title"));
			LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();
			LocalDate returnDate = rs.getDate("return_max_date").toLocalDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			record.put("borrowDate", borrowDate.format(formatter));
			record.put("returnDate", returnDate.format(formatter));

			borrowHistory.put(borrowId, record);
		}

		return borrowHistory;
	}

	/**
	 * Records an activity in the `records` table for a specific subscriber and
	 * book.
	 *
	 * @param recordType  the type of the activity (e.g., "borrow", "return",
	 *                    "reserve")
	 * @param subID       the unique ID of the subscriber associated with the
	 *                    activity
	 * @param bookID      the unique code of the book associated with the activity
	 * @param description a description of the activity (can provide additional
	 *                    context)
	 * @throws SQLException if a database access error occurs
	 */
	public void recordActivity(String recordType, int subID, int bookID, String description) throws SQLException {
		LocalDate today = LocalDate.now();
		String query = "INSERT INTO records (record_type, sub_id, record_date, book_code, description) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, recordType);
		ps.setInt(2, subID);
		ps.setDate(3, Date.valueOf(today));
		ps.setInt(4, bookID);
		ps.setString(5, description);
		ps.executeUpdate();
	}

	/**
	 * Retrieves all activity records for a specific subscriber by their unique ID.
	 * The method fetches details such as the record ID, type, date, associated
	 * book, and description for each record.
	 *
	 * @param subscriberID the unique ID of the subscriber whose records are to be
	 *                     retrieved
	 * @return a list of {@link Record} objects containing the activity records of
	 *         the subscriber
	 * @throws SQLException if a database access error occurs
	 */
	public List<Record> AllRecordByID(int subscriberID) throws SQLException {
		List<Record> records = new ArrayList<Record>();
		String query = "SELECT * FROM records WHERE sub_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subscriberID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			LocalDate recordDate = rs.getDate("record_date").toLocalDate();
			Record r = new Record(rs.getInt("record_id"), rs.getString("record_type"), rs.getInt("sub_id"),
					recordDate.toString(), rs.getInt("book_code"), rs.getString("description"));
			records.add(r);
		}
		return records;
	}

	/**
	 * Adds a new subscriber to the database. The new subscriber is initialized with
	 * an "active" status and the provided details.
	 *
	 * @param name     the name of the subscriber
	 * @param phone    the phone number of the subscriber
	 * @param email    the email address of the subscriber
	 * @param password the password for the subscriber's account
	 * @param userName the username for the subscriber's account
	 * @throws SQLException if a database access error occurs
	 */
	public void addSubscriber(String name, String phone, String email, String password, String userName)
			throws SQLException {
		String query = "INSERT INTO subscriber (sub_name, phone_num, status, email_address, password, user_name) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, name);
		ps.setString(2, phone);
		ps.setString(3, "active");
		ps.setString(4, email);
		ps.setString(5, password);
		ps.setString(6, userName);
		ps.executeUpdate();
	}

	/**
	 * Updates the return date of a borrow record in the database. The method
	 * ensures that there are available copies of the book before extending the
	 * return date.
	 *
	 * @param borrowID the unique ID of the borrow record to be updated
	 * @param bookID   the unique code of the book associated with the borrow record
	 * @param date     the new return date to set
	 * @return a message indicating whether the extension was successful or the
	 *         reason for failure
	 * @throws SQLException if a database access error occurs
	 */
	public String updateBorrowReturn(int borrowID, int bookID, LocalDate date) throws SQLException {
		String query = "SELECT amount_of_copies, amount_of_reservations FROM book WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			if (rs.getInt("amount_of_copies") - rs.getInt("amount_of_reservations") < 1) {
				return "All copies are reserved, unable to make extend";
			} else {
				query = "UPDATE borrow SET return_max_date = ? WHERE borrow_id = ?";
				ps = dbConnection.prepareStatement(query); // צור PreparedStatement חדש
				ps.setDate(1, Date.valueOf(date));
				ps.setInt(2, borrowID);
				ps.executeUpdate();
				return "The book extended successfully";
			}
		} else {
			return "Book not found in the database";
		}
	}

	/**
	 * Places a reservation for a book in the database. The reservation is marked
	 * with a status of "waiting" and includes the date and time the order was
	 * placed. Additionally, an activity record is logged for the reservation.
	 *
	 * @param subID    the unique ID of the subscriber placing the order
	 * @param bookCode the unique code of the book being reserved
	 * @throws SQLException if a database access error occurs
	 */
	public void orderBook(int subID, int bookCode) throws SQLException {
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		String query;
		PreparedStatement ps;
		query = "INSERT INTO reserved (book_code, sub_id, res_date, res_time, order_status) VALUES (?, ?, ?, ?, 'waiting')";
		ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ps.setInt(2, subID);
		ps.setDate(3, Date.valueOf(today));
		ps.setTime(4, Time.valueOf(now));
		ps.executeUpdate();
		encreaseAmountOfReservation(bookCode);
		recordActivity("reserved", subID, bookCode, null);
	}

	/**
	 * Checks whether a subscriber is in a "frozen" status. The method retrieves the
	 * subscriber's status from the database and compares it to "frozen".
	 *
	 * @param subID the unique ID of the subscriber to check
	 * @return {@code true} if the subscriber's status is "frozen"
	 *         (case-insensitive), otherwise {@code false}
	 * @throws SQLException if a database access error occurs or the subscriber with
	 *                      the specified ID is not found
	 */
	public boolean checkFrozenSubscriber(int subID) throws SQLException {
		String status = null;
		String query = "SELECT status FROM subscriber WHERE sub_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			status = rs.getString("status");
		} else {
			throw new SQLException("Subscriber with sub_id " + subID + " not found.");
		}
		return "frozen".equalsIgnoreCase(status);
	}

	/**
	 * Checks if there are available copies of a book to order. The method retrieves
	 * the number of copies and reservations for a specific book and determines if
	 * there are any unreserved copies.
	 *
	 * @param bookCode the unique code of the book to check
	 * @return {@code true} if there are available copies to order, otherwise
	 *         {@code false}
	 * @throws SQLException if a database access error occurs
	 */
	public boolean findCopyToOrder(int bookCode) throws SQLException {
		String query = "SELECT amount_of_copies, amount_of_reservations FROM book WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			if (rs.getInt("amount_of_copies") - rs.getInt("amount_of_reservations") > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds an available copy of a specific book in the database. The method
	 * searches for copies of the book with the status "exists" and returns details
	 * about the first available copy, including its copy ID and location.
	 *
	 * @param bookCode the unique code of the book to search for
	 * @return a {@link String} containing details of the available copy if found,
	 *         or {@code null} if no available copies exist
	 * @throws SQLException if a database access error occurs
	 */
	public String findExistCopy(int bookCode) throws SQLException {
		String query = "SELECT copy_id, status, location FROM copyofbook WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			if ("exists".equalsIgnoreCase(rs.getString("status"))) {
				return "The book is available for borrowing\nCopyID: " + Integer.valueOf(rs.getInt("copy_id"))
						+ ", Location: " + rs.getString("location");
			}
		}
		return null;

	}

	/**
	 * Retrieves a list of borrowing records for a specific month and year. The
	 * method returns detailed information about each borrowing record, including:
	 * The borrow ID, The subscriber ID, The book code, The borrow date, The return
	 * date (if returned), The return status.
	 * 
	 * @param month the month for which borrowing records are to be retrieved (1-12)
	 * @param year  the year for which borrowing records are to be retrieved
	 * @return a list of {@link ExtendedRecord} objects containing the borrowing
	 *         details
	 * @throws SQLException if a database access error occurs
	 */
	public List<ExtendedRecord> getMonthlyBorrowRecords(int month, int year) throws SQLException {
		String query = "SELECT r1.record_id AS borrow_id, r1.sub_id, r1.book_code, "
				+ "r1.record_date AS borrow_date, COALESCE(r2.record_date, r3.record_date) AS return_date, "
				+ "CASE WHEN r2.record_id IS NOT NULL THEN 'Returned' "
				+ "WHEN r3.record_id IS NOT NULL THEN 'Late Returned' " + "ELSE 'Not Returned' END AS return_status "
				+ "FROM records r1 "
				+ "LEFT JOIN records r2 ON r1.book_code = r2.book_code AND r1.sub_id = r2.sub_id AND r2.record_type = 'returned' "
				+ "LEFT JOIN records r3 ON r1.book_code = r3.book_code AND r1.sub_id = r3.sub_id AND r3.record_type = 'late' "
				+ "WHERE r1.record_type = 'borrowed' AND MONTH(r1.record_date) = ? AND YEAR(r1.record_date) = ?";

		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, month);
		ps.setInt(2, year);

		ResultSet rs = ps.executeQuery();
		List<ExtendedRecord> records = new ArrayList<>();
		while (rs.next()) {
			records.add(new ExtendedRecord(rs.getInt("borrow_id"), "borrow", rs.getInt("sub_id"),
					rs.getString("borrow_date"), rs.getInt("book_code"), rs.getString("return_date"),
					rs.getString("return_status")));
		}
		return records;
	}

	/**
	 * Retrieves a list of freeze and unfreeze activity records for a specific month
	 * and year. The method fetches all records with the type "freeze" or "unfreeze"
	 * from the database and includes details such as the record ID, type,
	 * subscriber ID, date, book code, and description.
	 *
	 * @param month the month for which freeze and unfreeze records are to be
	 *              retrieved (1-12)
	 * @param year  the year for which freeze and unfreeze records are to be
	 *              retrieved
	 * @return a list of {@link Record} objects containing freeze and unfreeze
	 *         activity details
	 * @throws SQLException if a database access error occurs
	 */
	public List<Record> getMonthlyFreezeAndUnfreezeRecords(int month, int year) throws SQLException {
		String query = "SELECT * FROM records WHERE (record_type = 'freeze' OR record_type = 'unfreeze') "
				+ "AND MONTH(record_date) = ? AND YEAR(record_date) = ?";
		List<Record> records = new ArrayList<>();
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, month);
		ps.setInt(2, year);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Record record = new Record(rs.getInt("record_id"), rs.getString("record_type"), rs.getInt("sub_id"),
					rs.getString("record_date"), rs.getInt("book_code"), rs.getString("description"));
			records.add(record);
		}

		return records;
	}

	/**
	 * Retrieves the count of subscribers grouped by their status. The method
	 * queries the database to count the number of subscribers for each status
	 * (e.g., "active", "frozen") and returns the results in a map.
	 *
	 * @return a {@link Map} where the key is the subscriber status (e.g., "active",
	 *         "frozen"), and the value is the count of subscribers with that status
	 * @throws SQLException if a database access error occurs
	 */
	public Map<String, Integer> getSubscriberStatusCounts() throws SQLException {
		String query = "SELECT status, COUNT(*) AS count FROM subscriber GROUP BY status";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();

		Map<String, Integer> statusCounts = new HashMap<>();
		while (rs.next()) {
			String status = rs.getString("status");
			int count = rs.getInt("count");
			statusCounts.put(status, count);
		}

		return statusCounts;
	}

	/**
	 * Updates the reservation table to associate a specific copy of a book with a
	 * reservation. The method sets the copy ID, updates the maximum reservation
	 * date to 2 days from the current date, and changes the order status to
	 * "waiting".
	 *
	 * @param resID  the unique ID of the reservation to update
	 * @param copyID the unique ID of the book copy to associate with the
	 *               reservation
	 * @throws SQLException if a database access error occurs
	 */
	public void setCopyToReservedTable(int resID, int copyID) throws SQLException {
		LocalDate today = LocalDate.now();
		String query = "UPDATE reserved SET copy_id = ?, res_max_date = ?, order_status = 'waiting' WHERE order_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, copyID);
		ps.setDate(2, Date.valueOf(today.plusDays(2)));
		ps.setInt(3, resID);
		ps.executeUpdate();
	}

	/**
	 * Updates the status of a specific copy of a book in the database.
	 *
	 * @param bookID the unique code of the book to which the copy belongs
	 * @param copyID the unique ID of the copy to update
	 * @param status the new status to set for the copy (e.g., "exists", "borrowed",
	 *               "reserved")
	 * @throws SQLException if a database access error occurs
	 */
	public void changeStatusOfCopyOfBook(int bookID, int copyID, String status) throws SQLException {
		String query = "UPDATE copyofbook SET status = ? WHERE copy_id = ? AND book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, status);
		ps.setInt(2, copyID);
		ps.setInt(3, bookID);
		ps.executeUpdate();
	}

	/**
	 * Decreases the number of active reservations for a specific book in the
	 * database by 1.
	 *
	 * @param bookID the unique code of the book for which the reservation count is
	 *               to be decreased
	 * @throws SQLException if a database access error occurs
	 */
	public void decreaseAmountOfReservation(int bookID) throws SQLException {
		String query = "UPDATE book SET amount_of_reservations = amount_of_reservations - 1 WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ps.executeUpdate();
	}

	/**
	 * Encreases the number of active reservations for a specific book in the
	 * database by 1.
	 *
	 * @param bookID the unique code of the book for which the reservation count is
	 *               to be increased
	 * @throws SQLException if a database access error occurs
	 */
	public void encreaseAmountOfReservation(int bookID) throws SQLException {
		String query = "UPDATE book SET amount_of_reservations = amount_of_reservations + 1 WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ps.executeUpdate();
	}

	/**
	 * Allocates a returned copy of a book to the next waiting reservation, if
	 * available. If no reservation is found, the copy's status is updated to
	 * "exists". This method performs the following steps:
	 * 
	 * Finds the next reservation in the queue for the book based on the reservation
	 * date.
	 * 
	 * Updates the reservation with the returned copy and adjusts its status to
	 * "reserved".
	 * 
	 * Increases the reservation count for the book.
	 * 
	 * Sends a notification to the subscriber associated with the reservation.
	 * 
	 * If no reservation exists, updates the copy's status to "exists".
	 * 
	 *
	 * @param bookID the unique code of the book being returned
	 * @param copyID the unique ID of the copy being allocated or updated
	 * @throws SQLException         if a database access error occurs
	 * @throws InterruptedException if the thread is interrupted while waiting
	 *                              during processing
	 */
	public void allocateReturnedCopyToReserved(int bookID, int copyID) throws SQLException, InterruptedException {
		String query = "SELECT sub_id, order_id FROM reserved WHERE book_code = ? AND order_status = 'waiting' ORDER BY res_date ASC, res_time ASC LIMIT 1";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			int subid = rs.getInt("sub_id");
			int resid = rs.getInt("order_id");
			setCopyToReservedTable(resid, copyID);
			Thread.sleep(1000);
			changeStatusOfCopyOfBook(bookID, copyID, "reserved");
			Thread.sleep(1000);
			decreaseAmountOfReservation(bookID);
			Thread.sleep(1000);
			String bookName = searchBookByID(bookID);
			LocalDate resMaxDate = LocalDate.now().plusDays(2);
			String description = "The book you reserved (" + bookName
					+ ") is now waiting for you. Please collect it by " + resMaxDate + ".";
			sendNotificationToSubscriber(subid, description);
			Thread.sleep(1000);
		} else {
			changeStatusOfCopyOfBook(bookID, copyID, "exists");
			Thread.sleep(1000);
			decreaseAmountOfReservation(bookID);
		}
	}

	/**
	 * Checks for expired reservations in the database and collects relevant details
	 * for further processing. This method identifies reservations with a maximum
	 * reservation date (`res_max_date`) that expired yesterday and have a status of
	 * "waiting". And than set expired to all the reservations found.
	 *
	 * The method performs the following steps:
	 * 
	 * Prepares a query to select reservations that expired yesterday.
	 * 
	 * For each expired reservation, extracts the book code, copy ID, and order ID.
	 * 
	 * Stores the extracted details in a list of maps for further use.
	 * 
	 * Set expired status to all reservation found
	 *
	 * @throws SQLException         if a database access error occurs
	 * @throws InterruptedException if the thread is interrupted during processing
	 */
	public void checkExpiredReservations() throws SQLException, InterruptedException {
		List<Map<String, Object>> results = new ArrayList<>();
		String query = "SELECT order_id, book_code, copy_id " + "FROM reserved "
				+ "WHERE res_max_date = ? AND order_status = 'waiting'";
		try (PreparedStatement ps = dbConnection.prepareStatement(query)) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			ps.setDate(1, Date.valueOf(yesterday));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("book_code", rs.getInt("book_code"));
					row.put("copy_id", rs.getInt("copy_id"));
					row.put("order_id", rs.getInt("order_id"));
					results.add(row);
				}
			}
		}

		for (Map<String, Object> reservation : results) {
			int orderId = (int) reservation.get("order_id");
			int bookCode = (int) reservation.get("book_code");
			int copyId = (int) reservation.get("copy_id");
			setExpired(orderId);
			Thread.sleep(1000);
			allocateReturnedCopyToReserved(bookCode, copyId);
			Thread.sleep(1000);
		}

	}

	/**
	 * Updates the status of a reservation to "expired" in the database. This method
	 * is used to mark a specific reservation as expired
	 *
	 * @param orderID the unique ID of the reservation to be updated
	 * @throws SQLException if a database access error occurs
	 */
	public void setExpired(int orderID) throws SQLException {
		String query = "UPDATE reserved SET order_status = 'expired' WHERE order_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, orderID);
		ps.executeUpdate();
	}

	/**
	 * Performs a daily check for overdue borrows and freezes the accounts of
	 * subscribers who have overdue items. This method updates the status of borrows
	 * and subscribers as follows:
	 * 
	 * - Identifies borrows that are overdue by more than 7 days. - Updates the
	 * status of overdue borrows to "late". - Freezes the accounts of subscribers
	 * with overdue borrows.
	 * 
	 * Logs messages to indicate the progress and results of the check.
	 *
	 * @throws SQLException if a database access error occurs while performing the
	 *                      overdue check.
	 */
	public void checkAndFreezeOverdueSubscribers() {
		System.out.println("Running daily overdue check...");

		String overdueQuery = "SELECT sub_id, borrow_id FROM Borrow WHERE status = 'borrowed' AND return_max_date < ?";
		String updateBorrowQuery = "UPDATE Borrow SET status = 'late' WHERE borrow_id = ?";
		String freezeSubscriberQuery = "UPDATE Subscriber SET status = 'frozen' WHERE sub_id = ?";

		try (PreparedStatement overduePs = dbConnection.prepareStatement(overdueQuery)) {
			overduePs.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(7)));
			ResultSet rs = overduePs.executeQuery();

			while (rs.next()) {
				int subscriberId = rs.getInt("sub_id");
				int borrowId = rs.getInt("borrow_id");

				// Update the Borrow status to 'late'
				try (PreparedStatement updateBorrowPs = dbConnection.prepareStatement(updateBorrowQuery)) {
					updateBorrowPs.setInt(1, borrowId);
					int rowsAffected = updateBorrowPs.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Borrow ID " + borrowId + " status updated to 'late'.");
					}
				}

				// Freeze the subscriber
				try (PreparedStatement freezeSubscriberPs = dbConnection.prepareStatement(freezeSubscriberQuery)) {
					freezeSubscriberPs.setInt(1, subscriberId);
					int rowsAffected = freezeSubscriberPs.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Subscriber ID " + subscriberId + " has been frozen.");
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error during overdue check: " + e.getMessage());
		}
	}

	/**
	 * Sends a notification to a specific subscriber if it does not already exist
	 * for the same day and description. The notification is stored in the
	 * `subscribernotifications` table with the current date and an "unread" status.
	 *
	 * The method performs the following steps:
	 * 
	 * Checks if a notification with the same description already exists for the
	 * subscriber and date.
	 * 
	 * If no duplicate exists, inserts the notification into the database.
	 * 
	 * Includes the current date in the notification description.
	 *
	 * @param subID       the unique ID of the subscriber to whom the notification
	 *                    is sent
	 * @param description the description of the notification
	 * @throws SQLException if a database access error occurs
	 */
	public void sendNotificationToSubscriber(int subID, String description) throws SQLException {
		// Get the current date
		LocalDate currentDate = LocalDate.now();

		// Include the current date in the description
		String updatedDescription = description + " (Date: " + currentDate + ")";

		// Check if the notification already exists for today and the same description
		String checkQuery = "SELECT COUNT(*) FROM subscribernotifications WHERE sub_id = ? AND description = ? AND noti_date = ?";
		try (PreparedStatement checkPs = dbConnection.prepareStatement(checkQuery)) {
			checkPs.setInt(1, subID);
			checkPs.setString(2, updatedDescription);
			checkPs.setDate(3, java.sql.Date.valueOf(currentDate));
			ResultSet rs = checkPs.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				System.out
						.println("Notification already exists for Subscriber ID " + subID + ": " + updatedDescription);
				return; // Do not send duplicate notification
			}
		}

		// Insert the notification if it doesn't exist
		String insertQuery = "INSERT INTO subscribernotifications (noti_date, sub_id, description, read_status) VALUES (?, ?, ?, 'unread')";
		try (PreparedStatement ps = dbConnection.prepareStatement(insertQuery)) {
			ps.setDate(1, java.sql.Date.valueOf(currentDate));
			ps.setInt(2, subID);
			ps.setString(3, updatedDescription);
			ps.executeUpdate();
			System.out.println("Notification sent to Subscriber ID " + subID + ": " + updatedDescription);
		}
	}

	/**
	 * Sends a notification to all librarians if it does not already exist for the
	 * same day and description. The notification is stored in the
	 * `librariannotifications` table with the current date and an "unread" status.
	 * 
	 * The method performs the following steps:
	 * 
	 * Checks if a notification with the same description already exists for the
	 * current date.
	 * 
	 * If no duplicate exists, inserts the notification into the database.
	 * 
	 * Includes the current date in the notification description.
	 *
	 * @param description the description of the notification
	 * @throws SQLException if a database access error occurs
	 */
	public void sendNotificationToLibrarian(String description) throws SQLException {
		// Get the current date
		LocalDate currentDate = LocalDate.now();

		// Include the current date in the description
		String updatedDescription = description + " (Date: " + currentDate + ")";

		// Check if the notification already exists for today and the same description
		String checkQuery = "SELECT COUNT(*) FROM librariannotifications WHERE description = ? AND noti_date = ?";
		try (PreparedStatement checkPs = dbConnection.prepareStatement(checkQuery)) {
			checkPs.setString(1, updatedDescription);
			checkPs.setDate(2, java.sql.Date.valueOf(currentDate));
			ResultSet rs = checkPs.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Notification already exists for Librarian: " + updatedDescription);
				return; // Do not send duplicate notification
			}
		}

		// Insert the notification into the table
		String insertQuery = "INSERT INTO librariannotifications (noti_date, description, read_status) VALUES (?, ?, 'unread')";
		try (PreparedStatement ps = dbConnection.prepareStatement(insertQuery)) {
			ps.setDate(1, java.sql.Date.valueOf(currentDate));
			ps.setString(2, updatedDescription);
			ps.executeUpdate();
			System.out.println("Notification sent to Librarian: " + updatedDescription);
		}
	}

	/**
	 * Retrieves notifications for a specific subscriber based on their read status.
	 * The method allows filtering for unread notifications or fetching all
	 * notifications for the subscriber.
	 *
	 * @param subID  the unique ID of the subscriber whose notifications are to be
	 *               retrieved
	 * @param status the status filter for notifications: 0 - fetch only unread
	 *               notifications 1 - fetch all notifications
	 * @return a {@link Map} where the key is the notification description and the
	 *         value is the notification ID
	 * @throws SQLException if a database access error occurs
	 */

	public Map<String, Integer> getNotiSubs(int subID, int status) throws SQLException {
		Map<String, Integer> noti = new HashMap<String, Integer>();
		String query;
		if (status == 0) {
			query = "SELECT noti_id,description FROM subscribernotifications WHERE sub_id = ? AND read_status = 'unread'";
		} else {
			query = "SELECT noti_id,description FROM subscribernotifications WHERE sub_id = ?";
		}
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			noti.put(rs.getString("description"), rs.getInt("noti_id"));
		}
		return noti;
	}

	/**
	 * Retrieves notifications for librarians based on their read status. The method
	 * allows filtering for unread notifications or fetching all notifications.
	 *
	 * @param status the status filter for notifications: 0 - fetch only unread
	 *               notifications 1 - fetch all notifications
	 * @return a {@link Map} where the key is the notification description and the
	 *         value is the notification ID
	 * @throws SQLException if a database access error occurs
	 */
	public Map<String, Integer> getNotiLib(int status) throws SQLException {
		Map<String, Integer> noti = new HashMap<String, Integer>();
		String query;
		if (status == 0) {
			query = "SELECT noti_id,description FROM librariannotifications WHERE read_status = 'unread'";
		} else {
			query = "SELECT noti_id,description FROM librariannotifications";
		}
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			noti.put(rs.getString("description"), rs.getInt("noti_id"));
		}
		return noti;

	}

	/**
	 * Retrieves a list of borrows that are due for return tomorrow. The method
	 * fetches details of borrowed books whose `return_max_date` is equal to
	 * tomorrow's date. Each entry in the returned list contains the subscriber ID
	 * and the name of the book.
	 *
	 * @return a {@link List} of {@link Map} objects where:
	 * 
	 *         The key "sub_id" maps to the subscriber ID as a string.
	 * 
	 *         The key "book_name" maps to the name of the borrowed book.
	 *
	 *         If no borrows are due tomorrow, an empty list is returned.
	 */
	public List<Map<String, String>> getBorrowsDueTomorrow() {
		String dueTomorrowQuery = "SELECT b.sub_id, bk.title AS book_name " + "FROM Borrow b "
				+ "JOIN Book bk ON b.book_code = bk.book_code "
				+ "WHERE b.status = 'borrowed' AND b.return_max_date = ?";
		List<Map<String, String>> notifications = new ArrayList<>();

		try (PreparedStatement ps = dbConnection.prepareStatement(dueTomorrowQuery)) {
			ps.setDate(1, java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Map<String, String> notification = new HashMap<>();
				notification.put("sub_id", String.valueOf(rs.getInt("sub_id")));
				notification.put("book_name", rs.getString("book_name"));
				notifications.add(notification);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching due-tomorrow notifications: " + e.getMessage());
		}

		return notifications;
	}

	/**
	 * Marks a subscriber notification as read in the database.
	 *
	 * @param notiID the unique ID of the notification to be marked as read
	 * @throws SQLException if a database access error occurs
	 */
	public void readNotiSubs(int notiID) throws SQLException {
		String query = "UPDATE subscribernotifications SET read_status = 'read' WHERE noti_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, notiID);
		ps.executeUpdate();
	}

	/**
	 * Marks a librarian notification as read in the database.
	 *
	 * @param notiID the unique ID of the notification to be marked as read
	 * @throws SQLException if a database access error occurs
	 */
	public void readNotiLib(int notiID) throws SQLException {
		String query = "UPDATE librariannotifications SET read_status = 'read' WHERE noti_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, notiID);
		ps.executeUpdate();
	}

	/**
	 * Checks if a username exists in the system. The method searches for the
	 * username in both the subscriber and librarian tables.
	 *
	 * @param userName the username to check for existence
	 * @return {@code true} if the username exists in either the subscriber or
	 *         librarian tables, otherwise {@code false}
	 * @throws SQLException if a database access error occurs
	 */
	public boolean isUserExists(String userName) throws SQLException {
		String query = "SELECT user_name FROM subscriber WHERE user_name = ? UNION SELECT user_name FROM librarian WHERE user_name = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, userName);
		ps.setString(2, userName);
		ResultSet rs = ps.executeQuery();
		return rs.next();
	}

	/**
	 * Retrieves a list of active borrows for a specific subscriber. The method
	 * fetches details of all books currently borrowed by the subscriber, including
	 * the borrow ID, book title, copy ID, and the maximum return date.
	 *
	 * @param subId the unique ID of the subscriber whose active borrows are to be
	 *              retrieved
	 * @return a {@link List} of {@link String} objects, where each string contains
	 *         details of an active borrow formatted as: Borrow ID: [borrowId] |
	 *         Title: [title] | Copy ID: [copyId] | Return Date: [returnDate]
	 * 
	 *         If the subscriber has no active borrows, an empty list is returned.
	 */
	public List<String> getActiveBorrows(int subId) {
		String query = "SELECT bo.borrow_id, bk.title, bo.copy_id, bo.return_max_date " + "FROM borrow bo "
				+ "JOIN book bk ON bo.book_code = bk.book_code " + "WHERE bo.sub_id = ? AND bo.status = 'borrowed'";
		List<String> activeBorrows = new ArrayList<>();

		try (PreparedStatement ps = dbConnection.prepareStatement(query)) {
			ps.setInt(1, subId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int borrowId = rs.getInt("borrow_id");
				String title = rs.getString("title");
				int copyId = rs.getInt("copy_id");
				Date returnDate = rs.getDate("return_max_date");

				// Format the borrow details as a string
				activeBorrows.add(String.format("Borrow ID: %d | Title: %s | Copy ID: %d | Return Date: %s", borrowId,
						title, copyId, returnDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return activeBorrows;
	}

	/**
	 * Authenticates a user based on their username and password. The method checks
	 * if the user exists in the system and validates their credentials. It supports
	 * both subscribers and librarians.
	 *
	 * @param userName the username of the user attempting to log in
	 * @param password the password of the user attempting to log in
	 * @return an instance of {@link Subscriber} if the user is a subscriber, an
	 *         instance of {@link Librarian} if the user is a librarian, or a
	 *         {@link String} with an error message ("user does not exists" or
	 *         "wrong password").
	 * @throws SQLException if a database access error occurs
	 */
	public Object login(String userName, String password) throws SQLException {
		if (!isUserExists(userName)) {
			return "user does not exists";
		}
		String query = "SELECT * FROM subscriber WHERE user_name = ? AND password = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, userName);
		ps.setString(2, password);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return new Subscriber(rs.getInt("sub_id"), rs.getString("sub_name"), rs.getString("phone_num"),
					rs.getString("status"), rs.getString("email_address"), rs.getString("password"),
					rs.getString("user_name"));
		} else {
			query = "SELECT * FROM librarian WHERE user_name = ? AND password = ?";
			ps = dbConnection.prepareStatement(query);
			ps.setString(1, userName);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				return new Librarian(rs.getInt("lib_id"), rs.getString("lib_name"), rs.getString("phone_num"),
						rs.getString("password"), rs.getString("user_name"));
			}
		}
		return "wrong password";
	}

	/**
	 * Retrieves the book code associated with a specific borrow ID.
	 *
	 * @param borrowID the unique ID of the borrow record to retrieve the book code
	 *                 from
	 * @return the book code associated with the borrow ID, or {@code 0} if no
	 *         matching record is found
	 * @throws SQLException if a database access error occurs
	 */
	public int getBookFromBorrow(int borrowID) throws SQLException {
		String query = "SELECT book_code FROM borrow WHERE borrow_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, borrowID);
		ResultSet rs = ps.executeQuery();
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getInt("book_code");
		}
		return 0;
	}
}
