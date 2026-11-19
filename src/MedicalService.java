public final class MedicalService {
    // Identity and referral policy are fixed for this consultation type.
    private final String serviceID;
    private String serviceName;
    private final boolean referralRequired;

    public MedicalService(String serviceID, String serviceName, boolean referralRequired) {
        this.serviceID = Checks.text(serviceID, "Service ID");
        setServiceName(serviceName);
        this.referralRequired = referralRequired;
    }

    public String getServiceID() {
        return serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = Checks.text(serviceName, "Service name");
    }

    public boolean isReferralRequired() {
        return referralRequired;
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof MedicalService that && serviceID.equals(that.serviceID);
    }

    @Override
    public int hashCode() {
        return serviceID.hashCode();
    }

    @Override
    public String toString() {
        return serviceName + (referralRequired ? " (referral required)" : " (no referral required)");
    }
}
