public record MedicalSpeciality(String specialtyID, String specialtyName) {
    public MedicalSpeciality {
        specialtyName = Checks.text(specialtyName, "Specialty");
    }

    @Override
    public String toString() {
        return specialtyName;
    }
}
