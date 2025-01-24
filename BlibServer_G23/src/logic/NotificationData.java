package logic;

public class NotificationData {
	 	private int subId;
	    private String bookName;

	    public NotificationData(int subId, String bookName) {
	        this.subId = subId;
	        this.bookName = bookName;
	    }

	    public int getSubId() {
	        return subId;
	    }

	    public String getBookName() {
	        return bookName;
	    }

}
