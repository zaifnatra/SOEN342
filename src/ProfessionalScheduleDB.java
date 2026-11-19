import java.util.ArrayList;
import java.util.List;

public class ProfessionalScheduleDB {
    private final List<ProfessionalSchedule> schedules = new ArrayList<>();

    public ProfessionalSchedule find(String professionalID, String clinicLocationID) {
        return schedules.stream().filter(schedule -> schedule.getProfessional().getProfessionalID().equals(professionalID)
                && schedule.getClinic().getLocationID().equals(clinicLocationID)).findFirst().orElse(null);
    }

    public ProfessionalSchedule findOrCreate(HealthcareProfessional professional, ClinicLocation clinic) {
        ProfessionalSchedule existing = find(professional.getProfessionalID(), clinic.getLocationID());
        if (existing != null) {
            return existing;
        }
        ProfessionalSchedule schedule = new ProfessionalSchedule("SCH" + (schedules.size() + 1), professional, clinic);
        schedules.add(schedule);
        return schedule;
    }
}
