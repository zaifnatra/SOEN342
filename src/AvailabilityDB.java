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
            for (Availability existing : checked) {
                if (existing.schedule().professional().equals(slot.schedule().professional())
                        && existing.overlaps(slot.date(), slot.startTime(), slot.endTime())) {
                    throw new IllegalArgumentException("Overlapping professional availability, including across clinics: " + slot);
                }
            }
            checked.add(slot);
        }
        availabilities.addAll(slots);
    }

    public List<Availability> findAvailable(String professionalID, String clinicLocationID,
                                           LocalDateTime from, LocalDateTime to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("Search start must be before search end.");
        }
        return findAll().stream()
                .filter(slot -> professionalID == null || slot.schedule().professional().professionalID().equals(professionalID))
                .filter(slot -> clinicLocationID == null || slot.schedule().clinic().locationID().equals(clinicLocationID))
                .filter(slot -> !slot.start().isBefore(from) && !slot.end().isAfter(to))
                .filter(slot -> !appointmentDB.hasConflict(slot.schedule().professional().professionalID(),
                        slot.date(), slot.startTime(), slot.endTime())).toList();
    }

    public List<Availability> findAll() {
        return availabilities.stream().sorted(Comparator.comparing(Availability::start)
                .thenComparing(slot -> slot.schedule().professional().name())).toList();
    }
}
