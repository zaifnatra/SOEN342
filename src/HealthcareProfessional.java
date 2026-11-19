public record HealthcareProfessional(String professionalID, String firstName, String lastName,
                                     String title, MedicalSpeciality speciality) {
    public HealthcareProfessional {
        firstName = Checks.text(firstName, "First name");
        lastName = Checks.text(lastName, "Last name");
        title = Checks.text(title, "Title");
        Checks.required(speciality, "Specialty");
    }

    public String name() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return title + " " + name() + " | " + speciality;
    }
}
