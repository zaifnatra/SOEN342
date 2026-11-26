public abstract class Referral {
    private final String referralID;
    private final Patient patient;
    private final HealthcareProfessional referringProfessional;
    private String priority;
    private String status = "approved";
    private String clinicalReason;

    protected Referral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                       String priority, String clinicalReason) {
        this.referralID = Checks.text(referralID, "Referral ID");
        this.patient = Checks.required(patient, "Patient");
        this.referringProfessional = Checks.required(referringProfessional, "Referring professional");
        setPriority(priority);
        setClinicalReason(clinicalReason);
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

    // The target of the other referral kind is absent.
    public abstract HealthcareProfessional getTargetProfessional();

    public abstract MedicalSpeciality getTargetSpeciality();

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
        return patient.equals(requestedPatient) && (professional.equals(getTargetProfessional())
                || professional.getSpeciality().equals(getTargetSpeciality()));
    }

    @Override
    public String toString() {
        String target = getTargetProfessional() == null
                ? getTargetSpeciality().getSpecialtyName() : getTargetProfessional().getName();
        return referralID + " | " + patient.getFirstName() + " " + patient.getLastName() + " → " + target + " | " + getStatus();
    }
}
