package logic;

import java.io.Serializable;
/**
 * Represents an extended record in the library system, including return details.
 */
public class ExtendedRecord extends Record implements Serializable {
	private static final long serialVersionUID = 4L;

	private String returnDate;
	private String returnStatus;
	/**
     * Constructs an ExtendedRecord object with the specified details.
     * 
     * @param recordID the unique identifier of the record
     * @param recordType the type of the record (e.g., borrow, return)
     * @param subscriberID the ID of the subscriber associated with the record
     * @param recordDate the date of the record
     * @param bookCode the book code associated with the record
     * @param returnDate the date the book was returned
     * @param returnStatus the status of the return (e.g., returned, late returned)
     */
	public ExtendedRecord(int recordID, String recordType, int subscriberID, String recordDate, int bookCode,
			String returnDate, String returnStatus) {
		super(recordID, recordType, subscriberID, recordDate, bookCode, "");
		this.returnDate = returnDate;
		this.returnStatus = returnStatus;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}
	 /**
     * Returns a string representation of the extended record.
     * 
     * @return a string containing the extended record details
     */
	@Override
	public String toString() {
		return super.toString() + ", returnDate=" + returnDate + ", returnStatus=" + returnStatus;
	}
}
