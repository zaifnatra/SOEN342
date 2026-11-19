import java.util.Objects;

public final class InsuranceInformation {
    private String insuranceNumber;
    private String insuranceType;

    public InsuranceInformation(String insuranceNumber, String insuranceType) {
        setInsuranceNumber(insuranceNumber);
        setInsuranceType(insuranceType);
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = Checks.text(insuranceNumber, "Insurance number");
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = Checks.text(insuranceType, "Insurance type");
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof InsuranceInformation that
                && insuranceNumber.equals(that.insuranceNumber) && insuranceType.equals(that.insuranceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insuranceNumber, insuranceType);
    }

    @Override
    public String toString() {
        return "InsuranceInformation[insuranceNumber=" + insuranceNumber + ", insuranceType=" + insuranceType + "]";
    }
}
