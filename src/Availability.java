import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public final class Availability {
    private final String availabilityID;
    private final ProfessionalSchedule schedule;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Availability(String availabilityID, ProfessionalSchedule schedule, LocalDate date, LocalTime startTime,
                        LocalTime endTime) {
        this.availabilityID = Checks.text(availabilityID, "Availability ID");
        this.schedule = Checks.required(schedule, "Schedule");
        setPeriod(date, startTime, endTime);
    }

    public String getAvailabilityID() {
        return availabilityID;
    }

    public ProfessionalSchedule getSchedule() {
        return schedule;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        setPeriod(date, this.startTime, this.endTime);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        setPeriod(this.date, startTime, this.endTime);
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        setPeriod(this.date, this.startTime, endTime);
    }

    // Validate the whole period before assigning any part of it.
    public void setPeriod(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Checks.required(date, "Date");
        Checks.interval(startTime, endTime);
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Availability that && availabilityID.equals(that.availabilityID);
    }

    @Override
    public int hashCode() {
        return availabilityID.hashCode();
    }

    public Availability(ProfessionalSchedule schedule, LocalDate date, LocalTime start, LocalTime end) {
        this(UUID.randomUUID().toString(), schedule, date, start, end);
    }

    public LocalDateTime getStart() {
        return date.atTime(startTime);
    }

    public LocalDateTime getEnd() {
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
        return date + " " + startTime + "–" + endTime + " | " + schedule.getProfessional().getName()
                + " | " + schedule.getProfessional().getSpeciality() + " | " + schedule.getClinic().getAddress();
    }
}
