import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public record ProfessionalSchedule(String scheduleID, HealthcareProfessional professional, ClinicLocation clinic) {
    public ProfessionalSchedule {
        Checks.required(professional, "Professional");
        Checks.required(clinic, "Clinic");
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
