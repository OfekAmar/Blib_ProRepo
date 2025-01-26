package logic;

import java.io.Serializable;
/**
 * The Record class represents a record in the system that tracks various activities or events.
 * It contains details such as the record ID, type, subscriber ID, date, book code, and a description.
 * This class implements Serializable to allow for object serialization.
 */
public class Record implements Serializable {
	 /**
     * Serial version UID for ensuring compatibility during serialization.
     */
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
     * @param recordID      the unique identifier of the record.
     * @param recordType    the type of the record.
     * @param subscriberID  the ID of the subscriber associated with the record.
     * @param recordDate    the date of the record.
     * @param bookCode      the code of the book associated with the record.
     * @param description   additional details or description of the record.
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getRecordID() {
		return recordID;
	}

	public String getRecordType() {
		return recordType;
	}

	public int getSubscriberID() {
		return subscriberID;
	}

	public String getRecordDate() {
		return recordDate;
	}

	public int getBookCode() {
		return bookCode;
	}

	public String getDescription() {
		return description;
	}

	public void setRecordID(int recordID) {
		this.recordID = recordID;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public void setSubscriberID(int subscriberID) {
		this.subscriberID = subscriberID;
	}

	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}

	public void setBookCode(int bookCode) {
		this.bookCode = bookCode;
	}

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
