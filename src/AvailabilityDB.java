import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AvailabilityDB {
    private final List<Availability> availabilities = new ArrayList<>();
    private final AppointmentDB appointmentDB;

    public AvailabilityDB(AppointmentDB appointmentDB) {
        this.appointmentDB = appointmentDB;
    }

    public void addAll(List<Availability> slots) {
        List<Availability> checked = new ArrayList<>(availabilities);
        for (Availability slot : slots) {
            Checks.required(slot, "Availability");
            for (Availability existing : checked) {
                if (existing.getSchedule().getProfessional().equals(slot.getSchedule().getProfessional())
                        && existing.overlaps(slot.getDate(), slot.getStartTime(), slot.getEndTime())) {
                    throw new IllegalArgumentException("Overlapping professional availability, including across clinics: " + slot);
                }
            }
            checked.add(slot);
        }
        availabilities.addAll(slots);
        for (Availability slot : slots) {
            slot.getSchedule().addAvailability(slot);
        }
    }

    public List<Availability> findAvailable(String professionalID, String clinicLocationID,
                                           LocalDateTime from, LocalDateTime to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("Search start must be before search end.");
        }
        return findAll().stream()
                .filter(slot -> professionalID == null || slot.getSchedule().getProfessional().getProfessionalID().equals(professionalID))
                .filter(slot -> clinicLocationID == null || slot.getSchedule().getClinic().getLocationID().equals(clinicLocationID))
                .filter(slot -> !slot.getStart().isBefore(from) && !slot.getEnd().isAfter(to))
                .filter(slot -> !appointmentDB.hasConflict(slot.getSchedule().getProfessional().getProfessionalID(),
                        slot.getDate(), slot.getStartTime(), slot.getEndTime())).toList();
    }

    public List<Availability> findAll() {
        return availabilities.stream().sorted(Comparator.comparing(Availability::getStart)
                .thenComparing(slot -> slot.getSchedule().getProfessional().getName())).toList();
    }
}
