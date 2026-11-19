import java.util.ArrayList;
import java.util.List;

public class ReferralDB {
    private final List<Referral> referrals = new ArrayList<>();

    public Referral find(String referralID) {
        return referrals.stream().filter(referral -> referral.referralID().equals(referralID)).findFirst().orElse(null);
    }

    public Referral newReferral(Patient patient, HealthcareProfessional referring, String priority, String reason,
                                HealthcareProfessional targetProfessional, MedicalSpeciality targetSpeciality) {
        Referral referral = new Referral("R" + (referrals.size() + 1), patient, referring, priority, reason,
                targetProfessional, targetSpeciality);
        referrals.add(referral);
        return referral;
    }

    public List<Referral> findAll() {
        return List.copyOf(referrals);
    }
}
