public record MedicalService(String serviceID, String serviceName, boolean referralRequired) {
    @Override
    public String toString() {
        return serviceName + (referralRequired ? " (referral required)" : " (no referral required)");
    }
}
