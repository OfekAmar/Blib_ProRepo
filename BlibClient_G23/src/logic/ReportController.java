package logic;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import client.ClientMain;

public class ReportController {
	private ClientMain client;
	private Object response;
	private CountDownLatch latch;

	public ReportController(ClientMain c) {
		this.client = c;
	}

	public List<Record> getAllRecordsByID(int subscriberID) throws InterruptedException {
		String msg = "GET_RECORDS_BY_ID," + String.valueOf(subscriberID);
		latch = new CountDownLatch(1);
		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof List<?>) {
				try {
					List<Record> records = (List<Record>) serverResponse; // Try to cast to List<Record>
					this.response = records; // Save the response
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to List<Record>: " + e.getMessage());
				}
			} else {
				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
			}
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg);
		latch.await();
		if (response instanceof List<?>) {

			try {
				return (List<Record>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to List<Record>:: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type List<Record>:.");
			return null;
		}
	}

	public List<ExtendedRecord> getMonthlyBorrowReport(int month, int year) throws InterruptedException {
		String msg = "MONTHLY_BORROW_REPORT," + String.valueOf(month) + "," + String.valueOf(year);
		latch = new CountDownLatch(1);
		client.setMessageHandler((Object serverResponse) -> {
			if (serverResponse instanceof List<?>) {
				try {
					List<ExtendedRecord> records = (List<ExtendedRecord>) serverResponse; // Try to cast to List<Record>
					this.response = records; // Save the response
				} catch (ClassCastException e) {
					System.err.println("Failed to cast response to List<Record>: " + e.getMessage());
				}
			} else {
				System.err.println("Unexpected response type from server: " + serverResponse.getClass().getName());
			}
			latch.countDown(); // Release the latch
		});

		client.sendMessageToServer(msg);
		latch.await();
		if (response instanceof List<?>) {

			try {
				return (List<ExtendedRecord>) response;
			} catch (ClassCastException e) {
				System.err.println("Failed to cast response to List<ExtendedRecord>:: " + e.getMessage());
				return null;
			}
		} else {
			System.err.println("Response is not of type List<ExtendedRecord>:.");
			return null;
		}

	}
}
