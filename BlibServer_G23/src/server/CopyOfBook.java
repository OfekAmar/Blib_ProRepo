package server;

public class CopyOfBook extends Book {
	private static final long serialVersionUID=2L;
	private String copyID;
	private String location;
	private String status;

	public CopyOfBook(String title, int id, String author, String subject,String description, int totalCopies,
			String copyID, String location, String status) {
		super(title, id, author, subject,description, totalCopies);
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
