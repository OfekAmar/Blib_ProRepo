package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import logic.Book;

public class DBconnector {
	private Connection dbConnection;

	public DBconnector() {
		connectToDatabase();
	}


    private void connectToDatabase() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/librarydb?serverTimezone=IST", "root", "Aa123456");
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



    // Update subscriber information
    public void updateSubscriber(int id, String name, String email, String phoneNumber) throws SQLException {
        String query = "UPDATE subscriber SET sub_name = ?, email_address = ?, phone_num = ? WHERE sub_id = ?";
        PreparedStatement pstmt = dbConnection.prepareStatement(query);
        pstmt.setString(1, name);
        pstmt.setString(2, email);
        pstmt.setString(3, phoneNumber);
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
            b = new Book(rs.getString("title"), rs.getInt("book_code"), rs.getString("author"),
                    rs.getString("subject"), rs.getString("description"), rs.getInt("amount_of_copies"));
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
            b = new Book(rs.getString("title"), rs.getInt("book_code"), rs.getString("author"),
                    rs.getString("subject"), rs.getString("description"), rs.getInt("amount_of_copies"));
            bookList.add(b);
        }
        return bookList;
    }

    // Get the earliest return date for a book
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
}
