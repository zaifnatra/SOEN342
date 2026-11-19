public record Referral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                       String priority, String clinicalReason, HealthcareProfessional targetProfessional,
                       MedicalSpeciality targetSpeciality) {
    public Referral {
        Checks.required(patient, "Patient");
        Checks.required(referringProfessional, "Referring professional");
        priority = Checks.text(priority, "Priority");
        clinicalReason = Checks.text(clinicalReason, "Clinical reason");
        if ((targetProfessional == null) == (targetSpeciality == null)) {
            throw new IllegalArgumentException("Choose exactly one referral target.");
        }
    }

    public String status() {
        return "approved";
    }

    public boolean isValidFor(Patient requestedPatient, HealthcareProfessional professional, MedicalService service) {
        return patient.equals(requestedPatient) && (professional.equals(targetProfessional)
                || professional.speciality().equals(targetSpeciality));
    }

    @Override
    public String toString() {
        String target = targetProfessional == null ? targetSpeciality.specialtyName() : targetProfessional.name();
        return referralID + " | " + patient.firstName() + " " + patient.lastName() + " → " + target + " | " + status();
    }
}
