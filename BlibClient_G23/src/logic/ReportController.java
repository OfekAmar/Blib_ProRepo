package logic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import client.ClientMain;

public class ReportController {
	private ClientMain client;
	private Object response;
	private CountDownLatch latch;

	public ReportController(ClientMain c) {
		this.client = c;
	}

	private <T> T sendMessageAndAwaitResponse(String message, Class<T> expectedType) throws InterruptedException {
		latch = new CountDownLatch(1);
		client.setMessageHandler((Object serverResponse) -> {
			if (expectedType.isInstance(serverResponse)) {
				response = expectedType.cast(serverResponse);
			} else {
				System.err.println("Unexpected response type: " + serverResponse.getClass().getName());
			}
			latch.countDown();
		});

		client.sendMessageToServer(message);
		latch.await();

		if (expectedType.isInstance(response)) {
			return expectedType.cast(response);
		} else {
			System.err.println("Response is not of type: " + expectedType.getName());
			return null;
		}
	}

	public List<Record> getAllRecordsByID(int subscriberID) throws InterruptedException {
		String msg = "GET_RECORDS_BY_ID," + subscriberID;
		return sendMessageAndAwaitResponse(msg, List.class);
	}

	public List<ExtendedRecord> getMonthlyBorrowReport(int month, int year) throws InterruptedException {
		String msg = "MONTHLY_BORROW_REPORT," + month + "," + year;
		return sendMessageAndAwaitResponse(msg, List.class);
	}

	public List<Record> getMonthlyStatusReport(int month, int year) throws InterruptedException {
		String msg = "MONTHLY_STATUS_REPORT," + month + "," + year;
		return sendMessageAndAwaitResponse(msg, List.class);
	}

	public Map<String, Integer> getStatusStatistics() throws InterruptedException {
		String msg = "STATUS_STATISTICS";
		return sendMessageAndAwaitResponse(msg, Map.class);
	}
}
