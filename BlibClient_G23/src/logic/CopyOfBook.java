package logic;

public class CopyOfBook {
	private String bookID;
	private String copyID;
	private String location;
	private String status;
	public CopyOfBook(String bookID, String copyID, String location, String status) {
		super();
		this.bookID = bookID;
		this.copyID = copyID;
		this.location = location;
		this.status = status;
	}
	
	public String getBookID() {
		return bookID;
	}

	public String getCopyID() {
		return copyID;
	}

	public String getStatus() {
		return status;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
	

}
