package server;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logic.Book;
import logic.ExtendedRecord;
import logic.Record;

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
	public String readSubscribers() throws SQLException {
		StringBuilder result = new StringBuilder();
		String query = "SELECT * FROM subscriber";
		Statement stmt = dbConnection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			result.append(rs.getInt("sub_id")).append(",").append(rs.getString("sub_name")).append(",")
					.append(rs.getString("phone_num")).append(",").append(rs.getString("status")).append(",")
					.append(rs.getString("email_address")).append(",").append(rs.getString("password")).append("\\n");
		}
		return result.toString();
	}

	// Show subscriber by ID
	public String showSubscriber(int id) throws SQLException {
		String query = "SELECT * FROM subscriber WHERE sub_id = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		StringBuilder result = new StringBuilder();
		while (rs.next()) {
			result.append(rs.getInt("sub_id")).append(",").append(rs.getString("sub_name")).append(",")
					.append(rs.getString("phone_num")).append(",").append(rs.getString("status")).append(",")
					.append(rs.getString("email_address"));
		}
		return result.toString();
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

	public void addSubscriber(String name, String phone, String email, String password) throws SQLException {
		String query = "INSERT INTO subscriber (sub_name, phone_num, status, email_address, password) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, name);
		ps.setString(2, phone);
		ps.setString(3, "active");
		ps.setString(4, email);
		ps.setString(5, password);
		ps.executeUpdate();
	}

	public void orderBook(int subID, int bookCode, int copyID) throws SQLException {
		LocalDate today = LocalDate.now();
		String query;
		PreparedStatement ps;
		// query = "UPDATE copyofbook SET status = 'reserved' WHERE copy_id = ?";
		// ps = dbConnection.prepareStatement(query);
		// ps.setInt(1, copyID);
		// ps.executeUpdate();
		query = "INSERT INTO reserved (book_code, copy_id, sub_id, res_max_date, status) VALUES (?, ?, ?, ?, 'wait')";
		ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ps.setInt(2, (Integer) null);
		ps.setInt(3, subID);
		ps.setDate(4, Date.valueOf(today.plusDays(2)));
		ps.executeUpdate();
		query = "INSERT INTO record (record_type, subscriber_id, record_date, book_code) VALUES ('order', ?, ?, ?)";
		ps = dbConnection.prepareStatement(query);
		ps.setInt(1, subID);
		ps.setDate(2, Date.valueOf(today));
		ps.setInt(3, bookCode);
		ps.executeUpdate();
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

	public int findCopyToOrder(int bookCode) throws SQLException {
		int copytoorder;
		String query = "SELECT copy_id, status FROM copyofbook WHERE book_code = ?";
		PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setInt(1, bookCode);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			copytoorder = rs.getInt("copy_id");
			if (!("reserved".equalsIgnoreCase(rs.getString("status")))) {
				return copytoorder;
			}
		}
		return 0;
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
	
	

}
