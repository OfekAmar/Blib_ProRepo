package server;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.Book;
import logic.ExtendedRecord;
import logic.Librarian;
import logic.Record;
import logic.Subscriber;

public class DBconnector {
	private Connection dbConnection;

	public DBconnector() {
		connectToDatabase();
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

	public ArrayList<String> BorrowHistoryByID(int subscriberID) throws SQLException {
		List<String> borrowdList = new ArrayList<String>();
		String query = "SELECT b.book_code, bk.title, b.borrow_date, b.return_max_date " + "FROM borrow b "
				+ "JOIN book bk ON b.book_code = bk.book_code " + "WHERE b.sub_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subscriberID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String s = rs.getString("title") + " borrowed at: " + rs.getDate("borrow_date") + ", return date: "
					+ rs.getDate("return_max_date");
			borrowdList.add(s);
		}
		return (ArrayList<String>) borrowdList;
	}

	public void recordActivity(String recordType, int subID, int bookID) throws SQLException {
		LocalDate today = LocalDate.now();
		String query = "INSERT INTO record (record_type, subscriber_id, record_date, book_code) VALUES (?, ?, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, recordType);
		ps.setInt(2, subID);
		ps.setDate(3, Date.valueOf(today));
		ps.setInt(4, bookID);
		ps.executeUpdate();
	}

	public List<Record> AllRecordByID(int subscriberID) throws SQLException {
		List<Record> records = new ArrayList<Record>();
		String query = "SELECT * FROM record WHERE subscriber_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subscriberID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Record r = new Record(rs.getInt("record_id"), rs.getString("record_type"), rs.getInt("subscriber_id"),
					rs.getDate("record_date").toString(), rs.getInt("book_code"));
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
		String query;
		PreparedStatement ps;
		query = "INSERT INTO reserved (book_code, sub_id, res_date, status) VALUES (?, ?, ?, ?, 'wait')";
		ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ps.setInt(2, subID);
		ps.setDate(3, Date.valueOf(today));
		ps.executeUpdate();
		recordActivity("order", subID, bookCode);
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
		String query = "SELECT amount_of_copies, amount_of_reservation FROM book WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			if (rs.getInt("amount_of_copies") - rs.getInt("amount_of_reservation") > 0) {
				return true;
			}
		}
		return false;
	}

	public String findExistCopy(int bookCode) throws SQLException {
		String str;
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
		String query = "SELECT r1.record_id AS borrow_id, r1.subscriber_id, r1.book_code, "
				+ "r1.record_date AS borrow_date, COALESCE(r2.record_date, r3.record_date) AS return_date, "
				+ "CASE WHEN r2.record_id IS NOT NULL THEN 'Returned' "
				+ "WHEN r3.record_id IS NOT NULL THEN 'Late Returned' " + "ELSE 'Not Returned' END AS return_status "
				+ "FROM record r1 "
				+ "LEFT JOIN record r2 ON r1.book_code = r2.book_code AND r1.subscriber_id = r2.subscriber_id AND r2.record_type = 'return' "
				+ "LEFT JOIN record r3 ON r1.book_code = r3.book_code AND r1.subscriber_id = r3.subscriber_id AND r3.record_type = 'lateReturned' "
				+ "WHERE r1.record_type = 'borrow' AND MONTH(r1.record_date) = ? AND YEAR(r1.record_date) = ?";

		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, month);
		ps.setInt(2, year);

		ResultSet rs = ps.executeQuery();
		List<ExtendedRecord> records = new ArrayList<>();
		while (rs.next()) {
			records.add(new ExtendedRecord(rs.getInt("borrow_id"), "borrow", // תמיד recordType = borrow לפי השאילתה
					rs.getInt("subscriber_id"), rs.getString("borrow_date"), rs.getInt("book_code"),
					rs.getString("return_date"), rs.getString("return_status")));
		}
		return records;
	}

	public List<Record> getMonthlyFreezeAndUnfreezeRecords(int month, int year) throws SQLException {
		String query = "SELECT * FROM record WHERE (record_type = 'freeze' OR record_type = 'unfreeze') "
				+ "AND MONTH(record_date) = ? AND YEAR(record_date) = ?";
		List<Record> records = new ArrayList<>();
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, month);
		ps.setInt(2, year);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Record record = new Record(rs.getInt("record_id"), rs.getString("record_type"), rs.getInt("subscriber_id"),
					rs.getString("record_date"), rs.getInt("book_code"));
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
		String query = "UPDATE reserved SET copy_id = ?, res_max_date = ?, status = 'wait' WHERE res_id = ?";
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
		String query = "UPDATE book SET amount_of_reservation = amount_of_reservation - 1 WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ps.executeUpdate();
	}

	public void allocateReturnedCopyToReserved(int bookID, int copyID) throws SQLException {
		String query = "SELECT sub_id,res_id FROM reserved WHERE book_code = ? AND status = 'wait' ORDER BY res_date ASC LIMIT 1";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			int subid = rs.getInt("sub_id");
			int resid = rs.getInt("res_id");
			setCopyToReservedTable(resid, copyID);
			changeStatusOfCopyOfBook(bookID, copyID, "reserved");
			decreaseAmountOfReservation(bookID);
			String desc = searchBookByID(bookID)
					+ "is wating for you to borrow\n The reservation wiil be exired in 2 days";
			sendNotificationToSubscriber(subid, desc);
		}
	}

	public void sendNotificationToSubscriber(int subID, String description) throws SQLException {
		String query = "INSERT INTO subscribernotifications (sub_id, description) VALUES (?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subID);
		ps.setString(2, description);
		ps.executeUpdate();
	}

	public void sendNotificationToLibrarian(int subID, String description) throws SQLException {
		String query = "INSERT INTO librariannotifications (description) VALUES (?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, description);
		ps.executeQuery();

	}

	public List<String> getNotiSubs(int subID, int status) throws SQLException {
		List<String> noti = null;
		String query = "SELECT description FROM subscribernotifications WHERE sub_id = ? AND read_status = 0";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			noti.add(rs.getString("description"));
		}
		return noti;
	}

	public List<String> getNotiLib(int status) throws SQLException {
		List<String> noti = null;
		String query = "SELECT description FROM librariannotifications WHERE read_status = 0";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			noti.add(rs.getString("description"));
		}
		return noti;

	}

	public boolean isUserExists(String userName) throws SQLException {
		String query = "SELECT user_name FROM subscriber WHERE user_name = ? UNION SELECT user_name FROM librarian WHERE user_name = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, userName);
		ps.setString(2, userName);
		ResultSet rs = ps.executeQuery();
		return rs.next();
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
