package logic;

import java.io.Serializable;

public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private int id;
	private String author;
	private String subject;
	private String description;
	private int totalCopies;

	public Book(String title, int id, String author, String subject, String description, int totalCopies) {
		this.title = title;
		this.id = id;
		this.author = author;
		this.subject = subject;
		this.description = description;
		this.totalCopies = totalCopies;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getId() {
		return id;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getTotalCopies() {
		return totalCopies;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}

	@Override
	public String toString() {
		return "Book title=" + title + ", id=" + id + ", author=" + author + ", subject=" + subject
				+ "Book description=" + description + "\n";
	}

}
