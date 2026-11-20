import java.util.ArrayList;
import java.util.List;

public class MedicalSpecialityDB {
    private final List<MedicalSpeciality> specialities = new ArrayList<>();

    public MedicalSpeciality findOrCreate(String name) {
        String validName = Checks.text(name, "Specialty");
        for (MedicalSpeciality speciality : specialities) {
            if (speciality.getSpecialtyName().equalsIgnoreCase(validName)) {
                return speciality;
            }
        }
        MedicalSpeciality speciality = new MedicalSpeciality("SP" + (specialities.size() + 1), validName);
        specialities.add(speciality);
        return speciality;
    }

    public List<MedicalSpeciality> findAll() {
        return List.copyOf(specialities);
    }
}
