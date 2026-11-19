import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientDB {
    private final List<Patient> patients = new ArrayList<>();

    public Patient find(String patientID) {
        return patients.stream().filter(patient -> patient.patientID().equals(patientID)).findFirst().orElse(null);
    }

    public Patient createPatient(Map<String, String> personalInformation, InsuranceInformation insurance) {
        Patient patient = new Patient("P" + (patients.size() + 1), personalInformation.get("firstName"),
                personalInformation.get("lastName"), LocalDate.parse(personalInformation.get("dateOfBirth")),
                personalInformation.get("phoneNumber"), insurance);
        patients.add(patient);
        return patient;
    }

    public List<Patient> findAll() {
        return List.copyOf(patients);
    }
}
