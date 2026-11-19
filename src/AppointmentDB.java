import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDB {
    private final List<Appointment> appointments = new ArrayList<>();

    public boolean hasConflict(String professionalID, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return appointments.stream().anyMatch(appointment -> appointment.getProfessional().getProfessionalID().equals(professionalID)
                && appointment.overlaps(date, startTime, endTime));
    }

    public Appointment newAppointment(Patient patient, HealthcareProfessional professional, ClinicLocation clinic,
                                      MedicalService service, Availability slot, Referral referral) {
        if (!slot.getSchedule().getProfessional().equals(professional) || !slot.getSchedule().getClinic().equals(clinic)) {
            throw new IllegalArgumentException("The slot does not belong to that professional and clinic.");
        }
        if (hasConflict(professional.getProfessionalID(), slot.getDate(), slot.getStartTime(), slot.getEndTime())) {
            throw new IllegalArgumentException("The professional already has an appointment during this time.");
        }
        if (appointments.stream().anyMatch(appointment -> appointment.getPatient().equals(patient)
                && appointment.overlaps(slot.getDate(), slot.getStartTime(), slot.getEndTime()))) {
            throw new IllegalArgumentException("The patient already has an appointment during this time.");
        }
        if ((service.isReferralRequired() && referral == null)
                || (referral != null && !referral.isValidFor(patient, professional, service))) {
            throw new IllegalArgumentException("A valid referral for this patient and professional/specialty is required.");
        }
        Appointment appointment = new Appointment("AP" + (appointments.size() + 1), patient, professional, clinic,
                service, slot, referral);
        appointments.add(appointment);
        return appointment;
    }

    public List<Appointment> findAll() {
        return List.copyOf(appointments);
    }
}
