public final class HealthcareProfessional {
    // Identity and specialty stay fixed so schedules and accepted referrals remain valid.
    private final String professionalID;
    private String firstName;
    private String lastName;
    private String title;
    private final MedicalSpeciality speciality;
    private final ProfessionalType type;

    public HealthcareProfessional(String professionalID, String firstName, String lastName, String title,
                                  MedicalSpeciality speciality, ProfessionalType type) {
        this.professionalID = Checks.text(professionalID, "Professional ID");
        setFirstName(firstName);
        setLastName(lastName);
        setTitle(title);
        this.speciality = Checks.required(speciality, "Specialty");
        this.type = Checks.required(type, "Professional type");
    }

    public String getProfessionalID() {
        return professionalID;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Checks.text(title, "Title");
    }

    public MedicalSpeciality getSpeciality() {
        return speciality;
    }

    public ProfessionalType getType() {
        return type;
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof HealthcareProfessional that && professionalID.equals(that.professionalID);
    }

    @Override
    public int hashCode() {
        return professionalID.hashCode();
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return title + " " + getName() + " | " + type + " | " + speciality;
    }
}
