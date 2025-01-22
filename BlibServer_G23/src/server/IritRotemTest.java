package server;

import java.sql.SQLException;

public class IritRotemTest {
	public static void main(String[] args) throws SQLException {
		DBconnector db = new DBconnector();
		try {
			// db.editSubscriber(7,"3333444466","irit@gmail.com","password");
			// System.out.println(db.earliestReturnDate(3));
			// System.out.println(db.searchBookBySubject("Fantasy"));
			// System.out.println(db.searchBookByFreeText("A tale"));
			// System.out.println(db.ReaderCardByID(1));
			// System.out.println(db.AllRecordByID(2));
			// System.out.println(db.findCopyToOrder(4));
			// System.out.println(db.checkFrozenSubscriber(1));
			// db.orderBook(7, 4, 2);
			// System.out.println(db.findExistCopy(4));
			System.out.println(db.getMonthlyBorrowRecords(1, 2025));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
