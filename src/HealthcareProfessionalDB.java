import java.util.ArrayList;
import java.util.List;

public class HealthcareProfessionalDB {
    private final List<HealthcareProfessional> professionals = new ArrayList<>();

    public HealthcareProfessional find(String professionalID) {
        return professionals.stream().filter(professional -> professional.professionalID().equals(professionalID))
                .findFirst().orElse(null);
    }

    public HealthcareProfessional findByName(String firstName, String lastName) {
        return professionals.stream().filter(professional -> professional.firstName().equalsIgnoreCase(firstName.strip())
                && professional.lastName().equalsIgnoreCase(lastName.strip())).findFirst().orElse(null);
    }

    public HealthcareProfessional findOrCreate(String firstName, String lastName, String title, MedicalSpeciality speciality) {
        HealthcareProfessional candidate = new HealthcareProfessional("HP" + (professionals.size() + 1),
                firstName, lastName, title, speciality);
        HealthcareProfessional existing = findByName(candidate.firstName(), candidate.lastName());
        if (existing != null) {
            if (!existing.title().equalsIgnoreCase(candidate.title()) || !existing.speciality().equals(speciality)) {
                throw new IllegalArgumentException("This professional's name already has different title/specialty details.");
            }
            return existing;
        }
        professionals.add(candidate);
        return candidate;
    }

    public List<HealthcareProfessional> findAll() {
        return List.copyOf(professionals);
    }
}
