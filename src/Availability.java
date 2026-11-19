import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record Availability(String availabilityID, ProfessionalSchedule schedule, LocalDate date,
                           LocalTime startTime, LocalTime endTime) {
    public Availability {
        Checks.required(schedule, "Schedule");
        Checks.required(date, "Date");
        Checks.interval(startTime, endTime);
    }

    public Availability(ProfessionalSchedule schedule, LocalDate date, LocalTime start, LocalTime end) {
        this(UUID.randomUUID().toString(), schedule, date, start, end);
    }

    public LocalDateTime start() {
        return date.atTime(startTime);
    }

    public LocalDateTime end() {
        return date.atTime(endTime);
    }

    public boolean covers(LocalDate requestedDate, LocalTime start, LocalTime end) {
        return date.equals(requestedDate) && !start.isBefore(startTime) && !end.isAfter(endTime);
    }

    public boolean overlaps(LocalDate requestedDate, LocalTime start, LocalTime end) {
        return date.equals(requestedDate) && startTime.isBefore(end) && start.isBefore(endTime);
    }

    @Override
    public String toString() {
        return date + " " + startTime + "–" + endTime + " | " + schedule.professional().name()
                + " | " + schedule.professional().speciality() + " | " + schedule.clinic().address();
    }
}
