package logic;

import java.io.Serializable;
/**
 * Represents a copy of a book in the library system.
 */
public class CopyOfBook implements Serializable {
	private static final long serialVersionUID = 2L;
	private int copyID;
	private String location;
	private String status;
	/**
     * Constructs a CopyOfBook object with the specified details.
     * 
     * @param bookCode the book code this copy belongs to
     * @param copyID the unique identifier of the copy
     * @param location the location of the copy
     * @param status the status of the copy (e.g., available, borrowed)
     */
	public CopyOfBook(int bookCode,int copyID, String location, String status) {
		this.copyID = copyID;
		this.location = location;
		this.status = status;
	}

	public int getCopyID() {
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
	
	/**
     * Returns a string representation of the copy.
     * 
     * @return a string containing the copy details
     */
	@Override
	public String toString() {
		return "copy id=" + copyID + ", location=" + location + ", status=" + status;
	}

}
