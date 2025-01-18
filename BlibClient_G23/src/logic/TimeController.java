package logic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TimeController {

    // Method to get the current date
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    // Method to calculate the return due date based on a borrowing period
    public long calculateReturnDueDate(LocalDate borrowDate, LocalDate returnDate) {
        return ChronoUnit.DAYS.between(borrowDate, returnDate);
    }

    // Method to calculate overdue days for a borrow
    public long calculateOverDueBorrow(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, returnDate);
        }
        return 0; // No overdue if returned on or before the due date
    }

    // Method to calculate the expiration date for an order
    public LocalDate calculateOrderExpiry(LocalDate orderDate, int expiryPeriodInDays) {
        return orderDate.plusDays(expiryPeriodInDays);
    }


   
}
