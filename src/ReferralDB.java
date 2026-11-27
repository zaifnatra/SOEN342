import java.util.ArrayList;
import java.util.List;

public class ReferralDB {
    private final List<Referral> referrals = new ArrayList<>();

    public Referral find(String referralID) {
        return referrals.stream().filter(referral -> referral.getReferralID().equals(referralID)).findFirst().orElse(null);
    }

    public Referral newReferral(Patient patient, HealthcareProfessional referring, String priority, String reason,
                                HealthcareProfessional targetProfessional, MedicalSpeciality targetSpeciality) {
        if ((targetProfessional == null) == (targetSpeciality == null)) {
            throw new IllegalArgumentException("Choose exactly one referral target.");
        }
        String referralID = "R" + (referrals.size() + 1);
        Referral referral = targetProfessional != null
                ? new ProfessionalReferral(referralID, patient, referring, priority, reason, targetProfessional)
                : new SpecialityReferral(referralID, patient, referring, priority, reason, targetSpeciality);
        referrals.add(referral);
        return referral;
    }

    public List<Referral> findAll() {
        return List.copyOf(referrals);
    }
}
