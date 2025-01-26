package logic;

import java.io.Serializable;
/**
 * Represents a book in the library system.
 */
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private int id;
	private String author;
	private String subject;
	private String description;
	private int totalCopies;
	 /**
     * Constructs a Book object with the specified details.
     * 
     * @param title the title of the book
     * @param id the unique identifier of the book
     * @param author the author of the book
     * @param subject the subject of the book
     * @param description the description of the book
     * @param totalCopies the total number of copies available
     */
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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTotalCopies() {
		return totalCopies;
	}

	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}
	 /**
     * Returns a string representation of the book.
     * 
     * @return a string containing the book details
     */
	@Override
	public String toString() {
		return "Book title=" + title + ", id=" + id + ", author=" + author + ", subject=" + subject
				+ "Book description=" + description + "\n";
	}

}
