package server;

import java.sql.*;

public class DBconnector {
    private Connection dbConnection;

    public DBconnector() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/librarydb?serverTimezone=IST", "root", "Aa12345");
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

    public String readSubscribers() throws SQLException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT * FROM subscriber";
        Statement stmt = dbConnection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            result.append(rs.getInt("sub_id")).append(",").append(rs.getString("sub_name")).append(",")
                    .append(rs.getString("phone_num")).append(",")
                    .append(rs.getString("status")).append(",").append(rs.getString("email_address"))
                    .append(",").append(rs.getString("password")).append("\\n");
        }
        return result.toString();
    }

    public String showSubscriber(int id) throws SQLException {
        String query = "SELECT * FROM subscriber WHERE sub_id = ?";
        PreparedStatement ps = dbConnection.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        StringBuilder result = new StringBuilder();
        while (rs.next()) {
            result.append(rs.getInt("subscriber_id")).append(",").append(rs.getString("subscriber_name")).append(",")
                    .append(rs.getString("subscriber_phone_number")).append(",")
                    .append(rs.getString("subscriber_email"));
        }
        return result.toString();
    }

    public void updateSubscriber(int id, String name, String email, String phoneNumber) throws SQLException {
        String query = "UPDATE subscriber SET subscriber_name = ?, subscriber_email = ?, subscriber_phone_number = ? WHERE subscriber_id = ?";
        PreparedStatement pstmt = dbConnection.prepareStatement(query);
        pstmt.setString(1, name);
        pstmt.setString(2, email);
        pstmt.setString(3, phoneNumber);
        pstmt.setInt(4, id);
        pstmt.executeUpdate();
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = dbConnection.createStatement();
        return statement.executeQuery(query);
    }

    public int updateQuery(String query) throws SQLException {
        Statement statement = dbConnection.createStatement();
        int rowsAffected = statement.executeUpdate(query);
        System.out.println("Update Query Executed: " + query + " | Rows Affected: " + rowsAffected);
        return rowsAffected;
    }

}
