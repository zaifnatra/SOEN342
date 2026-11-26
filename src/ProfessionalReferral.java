public final class ProfessionalReferral extends Referral {
    public ProfessionalReferral(String referralID, Patient patient, HealthcareProfessional referringProfessional,
                                String priority, String clinicalReason, HealthcareProfessional targetProfessional) {
        super(referralID, patient, referringProfessional, priority, clinicalReason, targetProfessional, null);
    }
}
