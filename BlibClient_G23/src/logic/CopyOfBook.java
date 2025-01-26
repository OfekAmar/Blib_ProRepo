package logic;

import java.io.Serializable;

/**
 * Represents a copy of a book in the library system. Each copy is associated
 * with a specific book and has unique attributes such as its location, status,
 * and copy ID.
 */
public class CopyOfBook implements Serializable {
	private static final long serialVersionUID = 2L;
	private int copyID;
	private String location;
	private String status;
	private int bookCode;

	/**
	 * Constructs a CopyOfBook object with the specified details.
	 * 
	 * @param bookCode the book code this copy belongs to
	 * @param copyID   the unique identifier of the copy
	 * @param location the location of the copy
	 * @param status   the status of the copy (e.g., available, borrowed)
	 */
	public CopyOfBook(int bookCode, int copyID, String location, String status) {
		this.copyID = copyID;
		this.location = location;
		this.status = status;
	}

	/**
	 * Constructs a CopyOfBook object with the specified book code and location.
	 * 
	 * @param bookCode the book code this copy belongs to
	 * @param location the location of the copy
	 */
	public CopyOfBook(int bookCode, String location) {
		this.bookCode = bookCode;
		this.location = location;
	}

	/**
	 * Retrieves the unique identifier of this copy.
	 * 
	 * @return the copy ID
	 */
	public int getCopyID() {
		return copyID;
	}

	/**
	 * Retrieves the current status of this copy.
	 * 
	 * @return the status of the copy (e.g., available, borrowed)
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Updates the location of this copy.
	 * 
	 * @param location the new location of the copy
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Retrieves the location of this copy.
	 * 
	 * @return the location of the copy
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Updates the status of this copy.
	 * 
	 * @param status the new status of the copy (e.g., available, borrowed)
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Returns a string representation of the copy. The string includes details such
	 * as the copy ID, location, and status.
	 * 
	 * @return a string containing the copy details
	 */

	@Override
	public String toString() {
		return "copy id=" + copyID + ", location=" + location + ", status=" + status;
	}

}
