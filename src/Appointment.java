import java.time.LocalDate;
import java.time.LocalTime;

public record Appointment(String appointmentID, Patient patient, HealthcareProfessional professional,
                          ClinicLocation clinic, MedicalService service, Availability slot, Referral referral) {
    public String status() {
        return "confirmed";
    }

    public boolean overlaps(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return slot.overlaps(date, startTime, endTime);
    }

    @Override
    public String toString() {
        return appointmentID + " | " + patient.firstName() + " " + patient.lastName() + " | " + slot
                + " | " + service.serviceName() + " | " + status();
    }
}
