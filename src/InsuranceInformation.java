public record InsuranceInformation(String insuranceNumber, String insuranceType) {
    public InsuranceInformation {
        insuranceNumber = Checks.text(insuranceNumber, "Insurance number");
        insuranceType = Checks.text(insuranceType, "Insurance type");
    }
}
