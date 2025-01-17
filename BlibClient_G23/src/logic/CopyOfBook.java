package logic;

public class CopyOfBook extends Book {
	private String copyID;
	private String location;
	private String status;

	public CopyOfBook(String title, String id, String author, String subject, int totalCopies, int reservedCopies,
			String copyID, String location, String status) {
		super(title, id, author, subject, totalCopies, reservedCopies);
		this.copyID = copyID;
		this.location = location;
		this.status = status;
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
