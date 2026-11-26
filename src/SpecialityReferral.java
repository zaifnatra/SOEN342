public final class SpecialityReferral extends Referral {
    public SpecialityReferral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                             String priority, String clinicalReason, MedicalSpeciality targetSpeciality) {
        super(referralID, patient, referringProfessional, priority, clinicalReason, null, targetSpeciality);
    }
}
