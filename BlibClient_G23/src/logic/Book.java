package logic;

import java.io.Serializable;

public class Book implements Serializable{
	private static final long serialVersionUID=1L;
	
	private String title;
	private int id;
	private String author;
	private String subject;
	private String description;
	private int totalCopies;

	
	
	
	public Book(String title, int id, String author, String subject,String description, int totalCopies) {
		this.title = title;
		this.id = id;
		this.author = author;
		this.subject = subject;
		this.description=description;
		this.totalCopies = totalCopies;

	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
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

	
	@Override
	public String toString() {
		return "Book title=" + title + ", id=" + id + ", author=" + author + ", subject=" + subject + "Book description=" + description+"\n";
	}
	
	

}
