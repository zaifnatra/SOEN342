import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ProfessionalSchedule {
    // Identity and workplace links are read-only because availability refers to this schedule.
    private final String scheduleID;
    private final HealthcareProfessional professional;
    private final ClinicLocation clinic;
    private final List<Availability> availabilities = new ArrayList<>();

    public ProfessionalSchedule(String scheduleID, HealthcareProfessional professional, ClinicLocation clinic) {
        this.scheduleID = Checks.text(scheduleID, "Schedule ID");
        this.professional = Checks.required(professional, "Professional");
        this.clinic = Checks.required(clinic, "Clinic");
    }

    public String getScheduleID() {
        return scheduleID;
    }

    public HealthcareProfessional getProfessional() {
        return professional;
    }

    public ClinicLocation getClinic() {
        return clinic;
    }

    public List<Availability> getAvailabilities() {
        return List.copyOf(availabilities);
    }

    // Called by AvailabilityDB only after the batch has passed overlap validation.
    void addAvailability(Availability availability) {
        availabilities.add(Checks.required(availability, "Availability"));
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ProfessionalSchedule that && scheduleID.equals(that.scheduleID);
    }

    @Override
    public int hashCode() {
        return scheduleID.hashCode();
    }

    @Override
    public String toString() {
        return "ProfessionalSchedule[scheduleID=" + scheduleID + ", professional=" + professional + ", clinic=" + clinic + "]";
    }

    public List<Availability> makeAvailability(LocalDate dateFrom, LocalDate dateTo, String repetitionPattern,
                                              LocalTime startTime, LocalTime endTime) {
        Checks.dateRange(dateFrom, dateTo);
        Checks.interval(startTime, endTime);
        String pattern = Checks.text(repetitionPattern, "Repetition pattern").toUpperCase(Locale.ROOT);
        Set<DayOfWeek> weekdays = EnumSet.noneOf(DayOfWeek.class);
        if (!pattern.equals("DAILY") && !pattern.equals("MONTHLY")) {
            try {
                for (String day : pattern.split(",", -1)) {
                    weekdays.add(DayOfWeek.valueOf(day.strip()));
                }
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Use DAILY, MONTHLY, or weekdays such as MONDAY,WEDNESDAY.");
            }
        }
        List<Availability> slots = new ArrayList<>();
        for (LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1)) {
            if (pattern.equals("DAILY") || weekdays.contains(date.getDayOfWeek())
                    || (pattern.equals("MONTHLY") && date.getDayOfMonth() == dateFrom.getDayOfMonth())) {
                slots.add(new Availability(this, date, startTime, endTime));
            }
        }
        if (slots.isEmpty()) {
            throw new IllegalArgumentException("No dates match this repetition pattern.");
        }
        return slots;
    }
}
