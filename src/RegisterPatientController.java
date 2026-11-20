import java.util.Map;

public class RegisterPatientController {
    private final PatientDB patientDB;

    public RegisterPatientController(PatientDB patientDB) {
        this.patientDB = patientDB;
    }

    public Patient registerPatient(Map<String, String> personalInformation, Map<String, String> insuranceInformation) {
        for (String field : new String[]{"firstName", "lastName", "dateOfBirth", "phoneNumber"}) {
            Checks.text(personalInformation.get(field), field);
        }
        InsuranceInformation insurance = insuranceInformation == null ? null : new InsuranceInformation(
                insuranceInformation.get("insuranceNumber"), insuranceInformation.get("insuranceType"));
        return patientDB.createPatient(personalInformation, insurance);
    }
}
