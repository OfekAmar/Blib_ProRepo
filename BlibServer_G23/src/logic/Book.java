package logic;

import java.io.Serializable;

/**
 * Represents a book in the library system. This class stores details about a
 * book, including its title, ID, author, subject, description, and the total
 * number of copies available. Implements {@link Serializable} for object
 * serialization.
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
	 * @param title       the title of the book
	 * @param id          the unique identifier of the book
	 * @param author      the author of the book
	 * @param subject     the subject of the book
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

	/**
	 * Retrieves the title of the book.
	 *
	 * @return the title of the book
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Retrieves the unique identifier of the book.
	 *
	 * @return the book's ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieves the author of the book.
	 *
	 * @return the author's name
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Retrieves the subject of the book.
	 *
	 * @return the subject of the book
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Retrieves the description of the book.
	 *
	 * @return the book's description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Retrieves the total number of copies of the book.
	 *
	 * @return the total number of copies available
	 */
	public int getTotalCopies() {
		return totalCopies;
	}

	/**
	 * Updates the title of the book.
	 *
	 * @param title the new title of the book
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Updates the unique identifier of the book.
	 *
	 * @param id the new ID of the book
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Updates the author of the book.
	 *
	 * @param author the new author's name
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Updates the subject of the book.
	 *
	 * @param subject the new subject of the book
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Updates the description of the book.
	 *
	 * @param description the new description of the book
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Updates the total number of copies of the book.
	 *
	 * @param totalCopies the new total number of copies
	 */
	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}

	/**
	 * Returns a string representation of the book. The string includes the title,
	 * ID, author, subject, and description of the book.
	 *
	 * @return a string containing the book's details
	 */
	@Override
	public String toString() {
		return "Book title=" + title + ", id=" + id + ", author=" + author + ", subject=" + subject
				+ "Book description=" + description + "\n";
	}

}
