package logic;

import java.io.Serializable;

public class ExtendedRecord extends Record implements Serializable {
	private static final long serialVersionUID = 4L;

	private String returnDate;
	private String returnStatus;

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

	@Override
	public String toString() {
		return super.toString() + ", returnDate=" + returnDate + ", returnStatus=" + returnStatus;
	}
}
