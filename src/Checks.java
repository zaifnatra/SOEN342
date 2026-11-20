import java.time.LocalDate;
import java.time.LocalTime;

final class Checks {
    private Checks() {}

    static String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.strip();
    }

    static String phoneNumber(String value) {
        String phoneNumber = text(value, "Telephone");
        if (!phoneNumber.matches("[0-9]+")) {
            throw new IllegalArgumentException("Telephone must contain digits only (0–9), without spaces or symbols.");
        }
        return phoneNumber;
    }

    static <T> T required(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " was not found.");
        }
        return value;
    }

    static void interval(LocalTime start, LocalTime end) {
        required(start, "Start time");
        required(end, "End time");
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time (same day).");
        }
    }

    static void dateRange(LocalDate from, LocalDate to) {
        required(from, "Start date");
        required(to, "End date");
        if (to.isBefore(from) || to.isAfter(from.plusYears(5))) {
            throw new IllegalArgumentException("Choose an ordered date range of at most five years.");
        }
    }
}
