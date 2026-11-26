public final class ProfessionalReferral extends Referral {
    private final HealthcareProfessional targetProfessional;

    public ProfessionalReferral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                                String priority, String clinicalReason, HealthcareProfessional targetProfessional) {
        super(referralID, patient, referringProfessional, priority, clinicalReason);
        this.targetProfessional = Checks.required(targetProfessional, "Target professional");
    }

    @Override
    public HealthcareProfessional getTargetProfessional() {
        return targetProfessional;
    }

    @Override
    public MedicalSpeciality getTargetSpeciality() {
        return null;
    }
}
