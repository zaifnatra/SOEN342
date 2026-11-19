import java.time.LocalDate;
import java.time.LocalTime;

public final class Appointment {
    // Booking relationships are read-only; changes must go through conflict and referral checks.
    private final String appointmentID;
    private final Patient patient;
    private final HealthcareProfessional professional;
    private final ClinicLocation clinic;
    private final MedicalService service;
    private final Availability slot;
    private final Referral referral;

    public Appointment(String appointmentID, Patient patient, HealthcareProfessional professional,
                       ClinicLocation clinic, MedicalService service, Availability slot, Referral referral) {
        this.appointmentID = Checks.text(appointmentID, "Appointment ID");
        this.patient = Checks.required(patient, "Patient");
        this.professional = Checks.required(professional, "Professional");
        this.clinic = Checks.required(clinic, "Clinic");
        this.service = Checks.required(service, "Service");
        this.slot = Checks.required(slot, "Slot");
        this.referral = referral;
        if (!slot.getSchedule().getProfessional().equals(professional)
                || !slot.getSchedule().getClinic().equals(clinic)) {
            throw new IllegalArgumentException("The slot does not belong to that professional and clinic.");
        }
        if ((service.isReferralRequired() && referral == null)
                || (referral != null && !referral.isValidFor(patient, professional, service))) {
            throw new IllegalArgumentException("A valid referral for this patient and professional/specialty is required.");
        }
        slot.lock();
        if (referral != null) {
            referral.lockTarget();
        }
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public Patient getPatient() {
        return patient;
    }

    public HealthcareProfessional getProfessional() {
        return professional;
    }

    public ClinicLocation getClinic() {
        return clinic;
    }

    public MedicalService getService() {
        return service;
    }

    public Availability getSlot() {
        return slot;
    }

    public Referral getReferral() {
        return referral;
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Appointment that && appointmentID.equals(that.appointmentID);
    }

    @Override
    public int hashCode() {
        return appointmentID.hashCode();
    }

    public String getStatus() {
        return "confirmed";
    }

    public boolean overlaps(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return slot.overlaps(date, startTime, endTime);
    }

    @Override
    public String toString() {
        return appointmentID + " | " + patient.getFirstName() + " " + patient.getLastName() + " | " + slot
                + " | " + service.getServiceName() + " | " + getStatus();
    }
}
