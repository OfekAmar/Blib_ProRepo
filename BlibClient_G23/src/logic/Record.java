package logic;

import java.io.Serializable;

/**
 * The Record class represents a record in the system that tracks various
 * activities or events. It contains details such as the record ID, type,
 * subscriber ID, date, book code, and a description. This class implements
 * Serializable to allow for object serialization.
 */
public class Record implements Serializable {
	private static final long serialVersionUID = 3L;

	private int recordID;
	private String recordType;
	private int subscriberID;
	private String recordDate;
	private int bookCode;
	private String description;

	/**
	 * Constructs a new Record object with the specified details.
	 *
	 * @param recordID     the unique identifier of the record.
	 * @param recordType   the type of the record.
	 * @param subscriberID the ID of the subscriber associated with the record.
	 * @param recordDate   the date of the record.
	 * @param bookCode     the code of the book associated with the record.
	 * @param description  additional details or description of the record.
	 */
	public Record(int recordID, String recordType, int subscriberID, String recordDate, int bookCode,
			String description) {
		super();
		this.recordID = recordID;
		this.recordType = recordType;
		this.subscriberID = subscriberID;
		this.recordDate = recordDate;
		this.bookCode = bookCode;
		this.setDescription(description);
	}

	/**
	 * Returns the unique identifier of the record.
	 *
	 * @return the serial version UID.
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Returns the unique identifier of the record.
	 *
	 * @return the record ID.
	 */
	public int getRecordID() {
		return recordID;
	}

	/**
	 * Returns the type of the record.
	 *
	 * @return the record type.
	 */
	public String getRecordType() {
		return recordType;
	}

	/**
	 * Returns the ID of the subscriber associated with the record.
	 *
	 * @return the subscriber ID.
	 */
	public int getSubscriberID() {
		return subscriberID;
	}

	/**
	 * Returns the date of the record.
	 *
	 * @return the record date.
	 */
	public String getRecordDate() {
		return recordDate;
	}

	/**
	 * Returns the code of the book associated with the record.
	 *
	 * @return the book code.
	 */
	public int getBookCode() {
		return bookCode;
	}

	/**
	 * Returns the description of the record.
	 *
	 * @return the description of the record.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the unique identifier of the record.
	 *
	 * @param recordID the record ID to set.
	 */
	public void setRecordID(int recordID) {
		this.recordID = recordID;
	}

	/**
	 * Sets the type of the record.
	 *
	 * @param recordType the record type to set.
	 */
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	/**
	 * Sets the ID of the subscriber associated with the record.
	 *
	 * @param subscriberID the subscriber ID to set.
	 */
	public void setSubscriberID(int subscriberID) {
		this.subscriberID = subscriberID;
	}

	/**
	 * Sets the date of the record.
	 *
	 * @param recordDate the record date to set.
	 */
	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}

	/**
	 * Sets the code of the book associated with the record.
	 *
	 * @param bookCode the book code to set.
	 */
	public void setBookCode(int bookCode) {
		this.bookCode = bookCode;
	}

	/**
	 * Sets the description of the record.
	 *
	 * @param description the description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns a string representation of the Record object.
	 *
	 * @return a string containing the record's details.
	 */
	@Override
	public String toString() {
		return "Record [recordID=" + recordID + ", recordType=" + recordType + ", subscriberID=" + subscriberID
				+ ", recordDate=" + recordDate + ", bookCode=" + bookCode + ", description=" + description + "]";
	}

}
