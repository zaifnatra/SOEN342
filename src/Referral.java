public final class Referral {
    private final String referralID;
    private final Patient patient;
    private final HealthcareProfessional referringProfessional;
    private String priority;
    private String status = "approved";
    private String clinicalReason;
    private HealthcareProfessional targetProfessional;
    private MedicalSpeciality targetSpeciality;

    public Referral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                    String priority, String clinicalReason, HealthcareProfessional targetProfessional,
                    MedicalSpeciality targetSpeciality) {
        this.referralID = Checks.text(referralID, "Referral ID");
        this.patient = Checks.required(patient, "Patient");
        this.referringProfessional = Checks.required(referringProfessional, "Referring professional");
        setPriority(priority);
        setClinicalReason(clinicalReason);
        setTarget(targetProfessional, targetSpeciality);
    }

    public String getReferralID() {
        return referralID;
    }

    public Patient getPatient() {
        return patient;
    }

    public HealthcareProfessional getReferringProfessional() {
        return referringProfessional;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = Checks.text(priority, "Priority");
    }

    public String getClinicalReason() {
        return clinicalReason;
    }

    public void setClinicalReason(String clinicalReason) {
        this.clinicalReason = Checks.text(clinicalReason, "Clinical reason");
    }

    public HealthcareProfessional getTargetProfessional() {
        return targetProfessional;
    }

    public void setTargetProfessional(HealthcareProfessional targetProfessional) {
        setTarget(targetProfessional, null);
    }

    public MedicalSpeciality getTargetSpeciality() {
        return targetSpeciality;
    }

    public void setTargetSpeciality(MedicalSpeciality targetSpeciality) {
        setTarget(null, targetSpeciality);
    }

    // Switching target kind clears the previous target only after validation succeeds.
    public void setTarget(HealthcareProfessional targetProfessional, MedicalSpeciality targetSpeciality) {
        if ((targetProfessional == null) == (targetSpeciality == null)) {
            throw new IllegalArgumentException("Choose exactly one referral target.");
        }
        this.targetProfessional = targetProfessional;
        this.targetSpeciality = targetSpeciality;
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Referral that && referralID.equals(that.referralID);
    }

    @Override
    public int hashCode() {
        return referralID.hashCode();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Checks.text(status, "Referral status");
    }

    public boolean isValidFor(Patient requestedPatient, HealthcareProfessional professional, MedicalService service) {
        return patient.equals(requestedPatient) && (professional.equals(targetProfessional)
                || professional.getSpeciality().equals(targetSpeciality));
    }

    @Override
    public String toString() {
        String target = targetProfessional == null ? targetSpeciality.getSpecialtyName() : targetProfessional.getName();
        return referralID + " | " + patient.getFirstName() + " " + patient.getLastName() + " → " + target + " | " + getStatus();
    }
}
