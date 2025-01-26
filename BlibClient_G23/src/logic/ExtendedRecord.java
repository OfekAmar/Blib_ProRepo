package logic;

import java.io.Serializable;

/**
 * Represents an extended record in the library system, which includes
 * additional details such as the return date and the return status of a
 * borrowed book. Extends the {@link Record} class to provide these additional
 * fields.
 */
public class ExtendedRecord extends Record implements Serializable {
	private static final long serialVersionUID = 4L;

	private String returnDate;
	private String returnStatus;

	/**
	 * Constructs an ExtendedRecord with the specified details.
	 *
	 * @param recordID     the unique identifier of the record
	 * @param recordType   the type of the record (e.g., borrow, return)
	 * @param subscriberID the ID of the subscriber associated with the record
	 * @param recordDate   the date the record was created
	 * @param bookCode     the code of the book associated with the record
	 * @param returnDate   the date when the book is expected to be returned
	 * @param returnStatus the status of the book's return (e.g., returned, overdue)
	 */
	public ExtendedRecord(int recordID, String recordType, int subscriberID, String recordDate, int bookCode,
			String returnDate, String returnStatus) {
		super(recordID, recordType, subscriberID, recordDate, bookCode, "");
		this.returnDate = returnDate;
		this.returnStatus = returnStatus;
	}

	/**
	 * Retrieves the return date of the book.
	 *
	 * @return the return date
	 */
	public String getReturnDate() {
		return returnDate;
	}

	/**
	 * Updates the return date of the book.
	 *
	 * @param returnDate the new return date
	 */
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	/**
	 * Retrieves the return status of the book.
	 *
	 * @return the return status (e.g., returned, overdue)
	 */
	public String getReturnStatus() {
		return returnStatus;
	}

	/**
	 * Updates the return status of the book.
	 *
	 * @param returnStatus the new return status (e.g., returned, overdue)
	 */
	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	/**
	 * Returns a string representation of the extended record. Includes details from
	 * the parent {@link Record} class along with the return date and return status.
	 *
	 * @return a string containing the record details
	 */
	@Override
	public String toString() {
		return super.toString() + ", returnDate=" + returnDate + ", returnStatus=" + returnStatus;
	}
}
