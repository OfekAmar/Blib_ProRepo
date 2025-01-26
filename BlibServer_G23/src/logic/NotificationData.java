package logic;
/**
 * The NotificationData class represents a notification related to a book.
 * It contains information about the subscriber ID and the book name associated with the notification.
 */
public class NotificationData {
	 	private int subId;
	    private String bookName;
	    /**
	     * Constructs a new NotificationData object with the specified subscriber ID and book name.
	     *
	     * @param subId    the ID of the subscriber.
	     * @param bookName the name of the book associated with the notification.
	     */
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
