public final class MedicalSpeciality {
    // The identifier stays fixed when the specialty description changes.
    private final String specialtyID;
    private String specialtyName;

    public MedicalSpeciality(String specialtyID, String specialtyName) {
        this.specialtyID = Checks.text(specialtyID, "Specialty ID");
        setSpecialtyName(specialtyName);
    }

    public String getSpecialtyID() {
        return specialtyID;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = Checks.text(specialtyName, "Specialty");
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof MedicalSpeciality that && specialtyID.equals(that.specialtyID);
    }

    @Override
    public int hashCode() {
        return specialtyID.hashCode();
    }

    @Override
    public String toString() {
        return specialtyName;
    }
}
