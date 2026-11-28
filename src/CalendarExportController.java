
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Predicate;

public final class CalendarExportController {

    private final AppointmentDB appointmentDB;
    private final CalendarExportGateway gateway;

    public CalendarExportController(AppointmentDB appointmentDB) {
        this.appointmentDB = appointmentDB;
        this.gateway = new CalendarExportGateway();
    }

    // Export appointments for a selected professional.
    public void exportByProfessional(String professionalID,
                                     LocalDate startDate,
                                     Path filePath) throws IOException {

        List<Appointment> appointments = filterAppointments(
                startDate,
                appointment -> appointment.getProfessional()
                        .getProfessionalID().equals(professionalID)
        );

        gateway.export(appointments, filePath);
    }

    // Export appointments for a selected patient.
    public void exportByPatient(String patientID,
                                LocalDate startDate,
                                Path filePath) throws IOException {

        List<Appointment> appointments = filterAppointments(
                startDate,
                appointment -> appointment.getPatient()
                        .getPatientID().equals(patientID)
        );

        gateway.export(appointments, filePath);
    }

    // Export appointments associated with a selected referral type.
    public void exportByReferralType(String referralType,
                                     LocalDate startDate,
                                     Path filePath) throws IOException {

        if (!"professional".equalsIgnoreCase(referralType)
                && !"speciality".equalsIgnoreCase(referralType)) {
            throw new IllegalArgumentException("Invalid referral type.");
        }

        List<Appointment> appointments = filterAppointments(
                startDate,
                appointment -> {
                    if ("professional".equalsIgnoreCase(referralType)) {
                        return appointment.getReferral()
                                instanceof ProfessionalReferral;
                    }

                    return appointment.getReferral()
                            instanceof SpecialityReferral;
                }
        );

        gateway.export(appointments, filePath);
    }

    // Apply common filtering rules to all export types.
    private List<Appointment> filterAppointments(
            LocalDate startDate,
            Predicate<Appointment> filter) {

        LocalDate effectiveStartDate = startDate == null
                ? LocalDate.now(ZoneId.of("America/Montreal"))
                : startDate;

        return appointmentDB.findAll().stream()
                .filter(appointment ->
                        "confirmed".equalsIgnoreCase(
                                appointment.getStatus()))
                .filter(appointment ->
                        !appointment.getSlot().getDate()
                                .isBefore(effectiveStartDate))
                .filter(filter)
                .toList();
    }
}
