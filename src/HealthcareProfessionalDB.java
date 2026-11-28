import java.util.ArrayList;
import java.util.List;

public class HealthcareProfessionalDB {
    private final List<HealthcareProfessional> professionals = new ArrayList<>();

    public HealthcareProfessional find(String professionalID) {
        return professionals.stream().filter(professional -> professional.getProfessionalID().equals(professionalID))
                .findFirst().orElse(null);
    }

    public HealthcareProfessional findByName(String firstName, String lastName) {
        return professionals.stream().filter(professional -> professional.getFirstName().equalsIgnoreCase(firstName.strip())
                && professional.getLastName().equalsIgnoreCase(lastName.strip())).findFirst().orElse(null);
    }

    public HealthcareProfessional findOrCreate(String firstName, String lastName, String title,
                                               MedicalSpeciality speciality, ProfessionalType type) {
        HealthcareProfessional candidate = new HealthcareProfessional("HP" + (professionals.size() + 1),
                firstName, lastName, title, speciality, type);
        HealthcareProfessional existing = findByName(candidate.getFirstName(), candidate.getLastName());
        if (existing != null) {
            if (!existing.getTitle().equalsIgnoreCase(candidate.getTitle()) || !existing.getSpeciality().equals(speciality)
                    || existing.getType() != type) {
                throw new IllegalArgumentException("This professional's name already has different title/type/specialty details.");
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
