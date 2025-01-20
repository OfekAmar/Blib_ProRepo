package server;

import java.sql.SQLException;

public class IritRotemTest {
	public static void main(String[] args) throws SQLException {
		DBconnector db=new DBconnector();
		try {
			System.out.println(db.searchBookByTitle("1984"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(db.earliestReturnDate(3));
		System.out.println(db.searchBookBySubject("Fantasy"));
		System.out.println(db.searchBookByFreeText("A tale"));
	}
}
