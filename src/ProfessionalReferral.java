public final class ProfessionalReferral extends Referral {
    private HealthcareProfessional targetProfessional;

    public ProfessionalReferral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                                String priority, String clinicalReason, HealthcareProfessional targetProfessional) {
        super(referralID, patient, referringProfessional, priority, clinicalReason);
        setTargetProfessional(targetProfessional);
    }

    public HealthcareProfessional getTargetProfessional() {
        return targetProfessional;
    }

    public void setTargetProfessional(HealthcareProfessional targetProfessional) {
        this.targetProfessional = Checks.required(targetProfessional, "Target professional");
    }

    @Override
    protected boolean matchesTarget(HealthcareProfessional professional) {
        return targetProfessional.equals(professional);
    }

    @Override
    protected String getTargetName() {
        return targetProfessional.getName();
    }
}
