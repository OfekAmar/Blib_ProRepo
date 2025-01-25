package logic;

import java.io.Serializable;

public class Record implements Serializable {
	private static final long serialVersionUID = 3L;

	private int recordID;
	private String recordType;
	private int subscriberID;
	private String recordDate;
	private int bookCode;
	private String description;

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

	@Override
	public String toString() {
		return "Record [recordID=" + recordID + ", recordType=" + recordType + ", subscriberID=" + subscriberID
				+ ", recordDate=" + recordDate + ", bookCode=" + bookCode + ", description=" + description + "]";
	}

}
