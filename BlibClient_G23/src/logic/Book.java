package logic;

public class Book {
	private String title;
	private String id;
	private String author;
	private String subject;
	private int totalCopies;
	private int ReservedCopies;
	
	
	
	public Book(String title, String id, String author, String subject, int totalCopies, int reservedCopies) {
		this.title = title;
		this.id = id;
		this.author = author;
		this.subject = subject;
		this.totalCopies = totalCopies;
		ReservedCopies = reservedCopies;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public int getTotalCopies() {
		return totalCopies;
	}
	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}
	public int getReservedCopies() {
		return ReservedCopies;
	}
	public void setReservedCopies(int reservedCopies) {
		ReservedCopies = reservedCopies;
	}
	
	
	
	

}
