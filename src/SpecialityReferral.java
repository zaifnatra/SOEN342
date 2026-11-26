public final class SpecialityReferral extends Referral {
    private final MedicalSpeciality targetSpeciality;

    public SpecialityReferral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                             String priority, String clinicalReason, MedicalSpeciality targetSpeciality) {
        super(referralID, patient, referringProfessional, priority, clinicalReason);
        this.targetSpeciality = Checks.required(targetSpeciality, "Target speciality");
    }

    @Override
    public HealthcareProfessional getTargetProfessional() {
        return null;
    }

    @Override
    public MedicalSpeciality getTargetSpeciality() {
        return targetSpeciality;
    }
}
