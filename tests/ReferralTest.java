import java.time.LocalDate;
import java.time.LocalTime;

public final class ReferralTest {
    public static void main(String[] args) {
        Patient patient = new Patient("P1", "Test", "Patient", LocalDate.of(2000, 1, 1), "5145550100", null);
        Patient otherPatient = new Patient("P2", "Other", "Patient", LocalDate.of(2000, 1, 1), "5145550101", null);
        MedicalSpeciality cardiology = new MedicalSpeciality("S1", "Cardiology");
        MedicalSpeciality dermatology = new MedicalSpeciality("S2", "Dermatology");
        HealthcareProfessional target = new HealthcareProfessional("H1", "Alex", "One", "Dr", cardiology);
        HealthcareProfessional colleague = new HealthcareProfessional("H2", "Sam", "Two", "Dr", cardiology);
        HealthcareProfessional other = new HealthcareProfessional("H3", "Lee", "Three", "Dr", dermatology);
        MedicalService service = new MedicalService("M1", "Consultation", true);
        ReferralDB referrals = new ReferralDB();

        Referral professional = referrals.newReferral(patient, other, "normal", "Consultation", target, null);
        Referral speciality = referrals.newReferral(patient, other, "normal", "Consultation", null, cardiology);
        check(professional instanceof ProfessionalReferral, "Factory should create a professional referral");
        check(speciality instanceof SpecialityReferral, "Factory should create a speciality referral");
        check(professional.getTargetProfessional() == target && professional.getTargetSpeciality() == null,
                "Professional referral should have only a professional target");
        check(speciality.getTargetProfessional() == null && speciality.getTargetSpeciality() == cardiology,
                "Specialty referral should have only a specialty target");
        check(referrals.find(professional.getReferralID()) == professional, "Lookup should preserve the referral");
        check(professional.isValidFor(patient, target, service), "Named professional should be accepted");
        check(!professional.isValidFor(patient, colleague, service), "Same specialty is insufficient for a named referral");
        check(speciality.isValidFor(patient, target, service), "Matching specialty should be accepted");
        check(speciality.isValidFor(patient, colleague, service), "Another professional in the specialty should be accepted");
        check(!speciality.isValidFor(patient, other, service), "Different specialty should be rejected");
        check(!professional.isValidFor(otherPatient, target, service), "Professional referral must match the patient");
        check(!speciality.isValidFor(otherPatient, target, service), "Specialty referral must match the patient");
        check(professional.toString().contains(target.getName()), "Display should show the target professional");
        check(speciality.toString().contains(cardiology.getSpecialtyName()), "Display should show the target specialty");

        rejects(() -> referrals.newReferral(patient, other, "normal", "Consultation", null, null));
        rejects(() -> referrals.newReferral(patient, other, "normal", "Consultation", target, cardiology));
        rejects(() -> referrals.newReferral(patient, null, "normal", "Consultation", target, null));
        rejects(() -> referrals.newReferral(patient, null, "normal", "Consultation", null, cardiology));
        check(referrals.findAll().size() == 2, "Invalid requests must not be stored");
        rejects(() -> new Referral("R3", patient, other, "normal", "Consultation", null, null));
        rejects(() -> new Referral("R3", patient, other, "normal", "Consultation", target, cardiology));
        rejects(() -> new ProfessionalReferral("R3", patient, other, "normal", "Consultation", null));
        rejects(() -> new SpecialityReferral("R3", patient, other, "normal", "Consultation", null));
        for (Referral referral : new Referral[]{professional, speciality}) {
            HealthcareProfessional previousProfessional = referral.getTargetProfessional();
            MedicalSpeciality previousSpeciality = referral.getTargetSpeciality();
            rejects(() -> referral.setTargetProfessional(null));
            rejects(() -> referral.setTargetSpeciality(null));
            rejects(() -> referral.setTarget(null, null));
            rejects(() -> referral.setTarget(target, cardiology));
            if (referral instanceof ProfessionalReferral) {
                rejects(() -> referral.setTargetSpeciality(cardiology));
                rejects(() -> referral.setTarget(null, cardiology));
            } else {
                rejects(() -> referral.setTargetProfessional(target));
                rejects(() -> referral.setTarget(target, null));
            }
            check(referral.getTargetProfessional() == previousProfessional
                            && referral.getTargetSpeciality() == previousSpeciality,
                    "Rejected edits must preserve the previous target");
        }

        professional.setTargetProfessional(colleague);
        check(professional.getTargetProfessional() == colleague && professional.getTargetSpeciality() == null,
                "Updating a professional target must preserve its kind");
        check(professional.isValidFor(patient, colleague, service), "Updated professional should be accepted");
        check(!professional.isValidFor(patient, target, service), "Previous professional should be rejected");
        professional.setTargetProfessional(target);
        speciality.setTargetSpeciality(dermatology);
        check(speciality.getTargetProfessional() == null && speciality.getTargetSpeciality() == dermatology,
                "Updating a specialty target must preserve its kind");
        check(speciality.isValidFor(patient, other, service), "Updated specialty should be accepted");
        check(!speciality.isValidFor(patient, target, service), "Previous specialty should be rejected");
        speciality.setTargetSpeciality(cardiology);

        ClinicLocation clinic = new ClinicLocation("C1", "Test Clinic");
        Availability slot = new Availability(new ProfessionalSchedule("SC1", target, clinic),
                LocalDate.of(2030, 1, 1), LocalTime.of(9, 0), LocalTime.of(9, 30));
        for (Referral referral : new Referral[]{professional, speciality}) {
            AppointmentDB appointments = new AppointmentDB();
            rejects(() -> appointments.newAppointment(otherPatient, target, clinic, service, slot, referral));
            Appointment booked = appointments.newAppointment(patient, target, clinic, service, slot, referral);
            check(booked.getReferral() == referral, "Booking should accept either valid referral kind");
        }
        Availability colleagueSlot = new Availability(new ProfessionalSchedule("SC2", colleague, clinic),
                LocalDate.of(2030, 1, 1), LocalTime.of(9, 0), LocalTime.of(9, 30));
        rejects(() -> new AppointmentDB().newAppointment(patient, colleague, clinic, service, colleagueSlot, professional));
        Availability otherSlot = new Availability(new ProfessionalSchedule("SC3", other, clinic),
                LocalDate.of(2030, 1, 1), LocalTime.of(9, 0), LocalTime.of(9, 30));
        rejects(() -> new AppointmentDB().newAppointment(patient, other, clinic, service, otherSlot, speciality));
        System.out.println("Referral tests passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected an invalid referral or booking to be rejected");
    }
}
