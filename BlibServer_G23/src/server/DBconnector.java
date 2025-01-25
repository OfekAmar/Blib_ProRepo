package server;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
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

public class DBconnector {
	private static volatile DBconnector instance;
	private Connection dbConnection;

	private DBconnector() {
		connectToDatabase();
	}

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

	private void connectToDatabase() {
		try {
			dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/librarydb?serverTimezone=IST", "root",
					"Aa123456");
			System.out.println("Database connected successfully.");
		} catch (SQLException e) {
			System.err.println("Database connection failed: " + e.getMessage());
			System.exit(1);
		}
	}

	public Connection getDbConnection() {
		if (dbConnection == null) {
			throw new IllegalStateException("Database connection is not initialized.");
		}
		return dbConnection;
	}

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

	public void editCopyOfBook(int bookCode, int copyId, String newLocation, String newStatus) throws SQLException {
		String query = "UPDATE copyofbook SET location = ?, status = ? WHERE book_code =? AND copy_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, newLocation);
		ps.setString(2, newStatus);
		ps.setInt(3, bookCode);
		ps.setInt(4, copyId);
		ps.executeUpdate();
	}

	// Read subscribers
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

	// Show subscriber by ID
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

	// edit subscriber information
	public void editSubscriber(int id, String phoneNumber, String email, String password) throws SQLException {
		String query = "UPDATE subscriber SET phone_num = ?, email_address = ?, password = ? WHERE sub_id = ?";
		PreparedStatement pstmt = dbConnection.prepareStatement(query);
		pstmt.setString(1, phoneNumber);
		pstmt.setString(2, email);
		pstmt.setString(3, password);
		pstmt.setInt(4, id);
		pstmt.executeUpdate();
	}

	// Execute a general query
	public ResultSet executeQuery(String query) throws SQLException {
		Statement statement = dbConnection.createStatement();
		return statement.executeQuery(query);
	}

	// Execute a general update query
	public int updateQuery(String query) throws SQLException {
		Statement statement = dbConnection.createStatement();
		int rowsAffected = statement.executeUpdate(query);
		System.out.println("Update Query Executed: " + query + " | Rows Affected: " + rowsAffected);
		return rowsAffected;
	}

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

	// Search book by title
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

	// Search books by subject
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

	// Search books by free text
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
			record.put("borrowDate", rs.getDate("borrow_date").toString());
			record.put("returnDate", rs.getDate("return_max_date").toString());

			borrowHistory.put(borrowId, record);
		}

		return borrowHistory;
	}

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

	public List<Record> AllRecordByID(int subscriberID) throws SQLException {
		List<Record> records = new ArrayList<Record>();
		String query = "SELECT * FROM records WHERE sub_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subscriberID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Record r = new Record(rs.getInt("record_id"), rs.getString("record_type"), rs.getInt("sub_id"),
					rs.getDate("record_date").toString(), rs.getInt("book_code"), rs.getString("description"));
			records.add(r);
		}
		return records;
	}

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

	public void updateBorrowReturn(int borrowID, LocalDate date) throws SQLException {
		String query = "UPDATE borrow SET return_max_date = ? WHERE borrow_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setDate(1, Date.valueOf(date));
		ps.setInt(2, borrowID);
		ps.executeUpdate();
	}

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
		recordActivity("reserved", subID, bookCode, null);
	}

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

	public List<ExtendedRecord> getMonthlyBorrowRecords(int month, int year) throws SQLException {
		String query = "SELECT r1.record_id AS borrow_id, r1.sub_id, r1.book_code, "
				+ "r1.record_date AS borrow_date, COALESCE(r2.record_date, r3.record_date) AS return_date, "
				+ "CASE WHEN r2.record_id IS NOT NULL THEN 'Returned' "
				+ "WHEN r3.record_id IS NOT NULL THEN 'Late Returned' " + "ELSE 'Not Returned' END AS return_status "
				+ "FROM records r1 "
				+ "LEFT JOIN records r2 ON r1.book_code = r2.book_code AND r1.sub_id = r2.sub_id AND r2.record_type = 'return' "
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

	public void setCopyToReservedTable(int resID, int copyID) throws SQLException {
		LocalDate today = LocalDate.now();
		String query = "UPDATE reserved SET copy_id = ?, res_max_date = ?, order_status = 'waiting' WHERE res_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, copyID);
		ps.setDate(2, Date.valueOf(today.plusDays(2)));
		ps.setInt(3, resID);
		ps.executeUpdate();
	}

	public void changeStatusOfCopyOfBook(int bookID, int copyID, String status) throws SQLException {
		String query = "UPDATE copyofbook SET status = ? WHERE copy_id = ? AND book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, status);
		ps.setInt(2, copyID);
		ps.setInt(3, bookID);
		ps.executeUpdate();
	}

	public void decreaseAmountOfReservation(int bookID) throws SQLException {
		String query = "UPDATE book SET amount_of_reservations = amount_of_reservations - 1 WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ps.executeUpdate();
	}

	public void encreaseAmountOfReservation(int bookID) throws SQLException {
		String query = "UPDATE book SET amount_of_reservations = amount_of_reservations + 1 WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ps.executeUpdate();
	}

	public void allocateReturnedCopyToReserved(int bookID, int copyID) throws SQLException {
		String query = "SELECT sub_id,res_id FROM reserved WHERE book_code = ? AND order_status = 'waiting' ORDER BY res_date ASC LIMIT 1";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			int subid = rs.getInt("sub_id");
			int resid = rs.getInt("res_id");
			setCopyToReservedTable(resid, copyID);
			changeStatusOfCopyOfBook(bookID, copyID, "reserved");
			encreaseAmountOfReservation(bookID);
			String desc = searchBookByID(bookID)
					+ "is wating for you to borrow\n The reservation wiil be exired in 2 days";
			sendNotificationToSubscriber(subid, desc);
		}
	}

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

	public void readNotiSubs(int notiID) throws SQLException {
		String query = "UPDATE subscribernotifications SET read_status = 'read' WHERE noti_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, notiID);
		ps.executeUpdate();
	}

	public void readNotiLib(int notiID) throws SQLException {
		String query = "UPDATE librariannotifications SET read_status = 'read' WHERE noti_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, notiID);
		ps.executeUpdate();
	}

	public boolean isUserExists(String userName) throws SQLException {
		String query = "SELECT user_name FROM subscriber WHERE user_name = ? UNION SELECT user_name FROM librarian WHERE user_name = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, userName);
		ps.setString(2, userName);
		ResultSet rs = ps.executeQuery();
		return rs.next();
	}

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
