import java.util.ArrayList;
import java.util.List;

public class ProfessionalScheduleDB {
    private final List<ProfessionalSchedule> schedules = new ArrayList<>();

    public ProfessionalSchedule find(String professionalID, String clinicLocationID) {
        return schedules.stream().filter(schedule -> schedule.professional().professionalID().equals(professionalID)
                && schedule.clinic().locationID().equals(clinicLocationID)).findFirst().orElse(null);
    }

    public ProfessionalSchedule findOrCreate(HealthcareProfessional professional, ClinicLocation clinic) {
        ProfessionalSchedule existing = find(professional.professionalID(), clinic.locationID());
        if (existing != null) {
            return existing;
        }
        ProfessionalSchedule schedule = new ProfessionalSchedule("SCH" + (schedules.size() + 1), professional, clinic);
        schedules.add(schedule);
        return schedule;
    }
}
