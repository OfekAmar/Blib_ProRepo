package logic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * The TimeController class provides utility methods for managing dates and
 * times. It includes methods for calculating due dates, overdue days, and order
 * expiry dates.
 */
public class TimeController {

	/**
	 * Gets the current date.
	 * 
	 * @return the current date as a {@link LocalDate} object.
	 */
	public LocalDate getCurrentDate() {
		return LocalDate.now();
	}

	/**
	 * Calculates the number of days between a borrowing date and a return date.
	 *
	 * @param borrowDate the date the item was borrowed.
	 * @param returnDate the date the item is to be returned.
	 * @return the number of days between the borrow date and the return date.
	 */
	public long calculateReturnDueDate(LocalDate borrowDate, LocalDate returnDate) {
		return ChronoUnit.DAYS.between(borrowDate, returnDate);
	}

	/**
	 * Calculates the number of overdue days for a borrowed item. If the item is
	 * returned on or before the due date, the overdue days will be 0.
	 *
	 * @param dueDate    the due date for the borrowed item.
	 * @param returnDate the date the item was actually returned.
	 * @return the number of overdue days, or 0 if there are no overdue days.
	 */
	public long calculateOverDueBorrow(LocalDate dueDate, LocalDate returnDate) {
		if (returnDate.isAfter(dueDate)) {
			return ChronoUnit.DAYS.between(dueDate, returnDate);
		}
		return 0; // No overdue if returned on or before the due date
	}

	/**
	 * Calculates the expiration date for an order based on the order date and an
	 * expiry period.
	 *
	 * @param orderDate          the date the order was placed.
	 * @param expiryPeriodInDays the number of days after which the order expires.
	 * @return the expiration date as a {@link LocalDate} object.
	 */
	public LocalDate calculateOrderExpiry(LocalDate orderDate, int expiryPeriodInDays) {
		return orderDate.plusDays(expiryPeriodInDays);
	}

}
