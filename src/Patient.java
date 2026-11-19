import java.time.LocalDate;

public record Patient(String patientID, String firstName, String lastName, LocalDate dateOfBirth,
                      String phoneNumber, InsuranceInformation insuranceInformation) {
    public Patient {
        firstName = Checks.text(firstName, "First name");
        lastName = Checks.text(lastName, "Last name");
        phoneNumber = Checks.text(phoneNumber, "Telephone");
        Checks.required(dateOfBirth, "Date of birth");
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
    }

    @Override
    public String toString() {
        return patientID + " | " + firstName + " " + lastName + " | " + phoneNumber;
    }
}
