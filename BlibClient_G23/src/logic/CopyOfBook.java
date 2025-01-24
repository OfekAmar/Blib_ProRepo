package logic;

import java.io.Serializable;

public class CopyOfBook implements Serializable {
	private static final long serialVersionUID = 2L;
	private int copyID;
	private String location;
	private String status;
	private int bookCode;

	public CopyOfBook(int bookCode,int copyID, String location, String status) {
		this.copyID = copyID;
		this.location = location;
		this.status = status;
	}
	
	public CopyOfBook(int bookCode, String location) {
		this.bookCode=bookCode;
		this.location=location;	
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
	

	@Override
	public String toString() {
		return "copy id=" + copyID + ", location=" + location + ", status=" + status;
	}

}
