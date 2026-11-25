public final class SpecialityReferral extends Referral {
    private MedicalSpeciality targetSpeciality;

    public SpecialityReferral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                              String priority, String clinicalReason, MedicalSpeciality targetSpeciality) {
        super(referralID, patient, referringProfessional, priority, clinicalReason);
        setTargetSpeciality(targetSpeciality);
    }

    public MedicalSpeciality getTargetSpeciality() {
        return targetSpeciality;
    }

    public void setTargetSpeciality(MedicalSpeciality targetSpeciality) {
        this.targetSpeciality = Checks.required(targetSpeciality, "Target specialty");
    }

    @Override
    protected boolean matchesTarget(HealthcareProfessional professional) {
        return professional.getSpeciality().equals(targetSpeciality);
    }

    @Override
    protected String getTargetName() {
        return targetSpeciality.getSpecialtyName();
    }
}
