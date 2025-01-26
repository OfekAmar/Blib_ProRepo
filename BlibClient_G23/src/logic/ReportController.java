package logic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import client.ClientMain;

/**
 * Controller for managing report-related operations in the library system.
 */
public class ReportController {
	private ClientMain client;
	private Object response;
	private CountDownLatch latch;

	/**
	 * Constructs a ReportController object with the specified client.
	 * 
	 * @param client the client object for server communication
	 */
	public ReportController(ClientMain c) {
		this.client = c;
	}

	/**
	 * Sends a message to the server and awaits a response of the specified type.
	 * 
	 * @param <T>          the expected response type
	 * @param message      the message to send
	 * @param expectedType the expected class type of the response
	 * @return the server response cast to the specified type
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
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

	/**
	 * Retrieves all records associated with a specific subscriber ID.
	 * 
	 * @param subscriberID the ID of the subscriber
	 * @return a list of records for the subscriber
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
	public List<Record> getAllRecordsByID(int subscriberID) throws InterruptedException {
		String msg = "GET_RECORDS_BY_ID," + subscriberID;
		return sendMessageAndAwaitResponse(msg, List.class);
	}

	/**
	 * Retrieves a monthly borrow report for the specified month and year.
	 * 
	 * @param month the month for the report
	 * @param year  the year for the report
	 * @return a list of extended records for the month
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
	public List<ExtendedRecord> getMonthlyBorrowReport(int month, int year) throws InterruptedException {
		String msg = "MONTHLY_BORROW_REPORT," + month + "," + year;
		return sendMessageAndAwaitResponse(msg, List.class);
	}

	/**
	 * Retrieves a monthly status report for the specified month and year.
	 * 
	 * @param month the month for the report
	 * @param year  the year for the report
	 * @return a list of records for the month
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
	public List<Record> getMonthlyStatusReport(int month, int year) throws InterruptedException {
		String msg = "MONTHLY_STATUS_REPORT," + month + "," + year;
		return sendMessageAndAwaitResponse(msg, List.class);
	}

	/**
	 * Retrieves statistics on the status of library items.
	 * 
	 * @return a map of status categories to their respective counts
	 * @throws InterruptedException if interrupted while waiting for the server
	 *                              response
	 */
	public Map<String, Integer> getStatusStatistics() throws InterruptedException {
		String msg = "STATUS_STATISTICS";
		return sendMessageAndAwaitResponse(msg, Map.class);
	}
}
