import java.time.LocalDate;

public final class Patient {
    // The identifier stays fixed when personal information changes.
    private final String patientID;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private InsuranceInformation insuranceInformation;

    public Patient(String patientID, String firstName, String lastName, LocalDate dateOfBirth, String phoneNumber,
                   InsuranceInformation insuranceInformation) {
        this.patientID = Checks.text(patientID, "Patient ID");
        setFirstName(firstName);
        setLastName(lastName);
        setDateOfBirth(dateOfBirth);
        setPhoneNumber(phoneNumber);
        setInsuranceInformation(insuranceInformation);
    }

    public String getPatientID() {
        return patientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = Checks.text(firstName, "First name");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = Checks.text(lastName, "Last name");
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        Checks.required(dateOfBirth, "Date of birth");
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = Checks.text(phoneNumber, "Telephone");
    }

    public InsuranceInformation getInsuranceInformation() {
        return insuranceInformation;
    }

    public void setInsuranceInformation(InsuranceInformation insuranceInformation) {
        this.insuranceInformation = insuranceInformation;
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Patient that && patientID.equals(that.patientID);
    }

    @Override
    public int hashCode() {
        return patientID.hashCode();
    }

    @Override
    public String toString() {
        return patientID + " | " + firstName + " " + lastName + " | " + phoneNumber;
    }
}
