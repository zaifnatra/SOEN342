
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public final class CalendarExportTest {

    public static void main(String[] args) throws Exception {

        // Create test patients.
        Patient patient = new Patient(
                "P1", "John", "Smith",
                LocalDate.of(2000, 1, 1),
                "5145550100", null);

        Patient otherPatient = new Patient(
                "P2", "Jane", "Brown",
                LocalDate.of(2000, 1, 1),
                "5145550101", null);

        // Create test professionals.
        MedicalSpeciality cardiology =
                new MedicalSpeciality("S1", "Cardiology");

        HealthcareProfessional professional =
                new HealthcareProfessional(
                        "H1", "Alex", "One", "Dr", cardiology);

        HealthcareProfessional otherProfessional =
                new HealthcareProfessional(
                        "H2", "Sam", "Two", "Dr", cardiology);

        // Create clinic and services.
        ClinicLocation clinic =
                new ClinicLocation("C1", "Test Clinic");

        MedicalService generalService =
                new MedicalService("M1", "General consultation", false);

        MedicalService specialistService =
                new MedicalService("M2", "Specialist consultation", true);

        // Create professional schedules.
        ProfessionalSchedule schedule =
                new ProfessionalSchedule("SC1", professional, clinic);

        ProfessionalSchedule otherSchedule =
                new ProfessionalSchedule("SC2", otherProfessional, clinic);

        // Create referrals.
        ReferralDB referrals = new ReferralDB();

        Referral professionalReferral = referrals.newReferral(
                patient, otherProfessional, "normal",
                "Confidential clinical reason", professional, null);

        Referral specialityReferral = referrals.newReferral(
                otherPatient, professional, "normal",
                "Confidential diagnosis", null, cardiology);

        // Create appointment dates.
        LocalDate today = LocalDate.now(
                ZoneId.of("America/Montreal"));

        LocalDate futureDate = today.plusDays(10);
        LocalDate pastDate = today.minusDays(10);

        AppointmentDB appointments = new AppointmentDB();

        // AP1: Future appointment with professional referral.
        appointments.newAppointment(
                patient, professional, clinic, specialistService,
                new Availability(schedule, futureDate,
                        LocalTime.of(9, 0), LocalTime.of(9, 30)),
                professionalReferral);

        // AP2: Future appointment with speciality referral.
        appointments.newAppointment(
                otherPatient, otherProfessional, clinic, specialistService,
                new Availability(otherSchedule, futureDate,
                        LocalTime.of(10, 0), LocalTime.of(10, 30)),
                specialityReferral);

        // AP3: Past appointment without referral.
        appointments.newAppointment(
                patient, professional, clinic, generalService,
                new Availability(schedule, pastDate,
                        LocalTime.of(11, 0), LocalTime.of(11, 30)),
                null);

        // AP4: Future appointment without referral.
        appointments.newAppointment(
                patient, professional, clinic, generalService,
                new Availability(schedule, futureDate,
                        LocalTime.of(14, 0), LocalTime.of(14, 30)),
                null);

        CalendarExportController controller =
                new CalendarExportController(appointments);

        Path file = Files.createTempFile("calendar-export-test", ".ics");

        try {

            // Test 1: Patient filtering.
            controller.exportByPatient("P1", futureDate, file);
            String content = Files.readString(file);

            check(content.contains("UID:AP1@soen342"),
                    "Patient appointment should be exported");

            check(content.contains("UID:AP4@soen342"),
                    "All matching future patient appointments should be exported");

            check(!content.contains("UID:AP2@soen342"),
                    "Other patient's appointment must be excluded");

            System.out.println("Patient filtering test passed.");

            // Test 2: Professional filtering.
            controller.exportByProfessional("H2", futureDate, file);
            content = Files.readString(file);

            check(content.contains("UID:AP2@soen342"),
                    "Selected professional's appointment should be exported");

            check(!content.contains("UID:AP1@soen342")
                            && !content.contains("UID:AP4@soen342"),
                    "Other professional's appointments must be excluded");

            System.out.println("Professional filtering test passed.");

            // Test 3: Professional referral filtering.
            controller.exportByReferralType(
                    "professional", futureDate, file);

            content = Files.readString(file);

            check(content.contains("UID:AP1@soen342"),
                    "Professional referral should be exported");

            check(!content.contains("UID:AP2@soen342")
                            && !content.contains("UID:AP4@soen342"),
                    "Other referral types must be excluded");

            System.out.println("Professional referral test passed.");

            // Test 4: Speciality referral filtering.
            controller.exportByReferralType(
                    "speciality", futureDate, file);

            content = Files.readString(file);

            check(content.contains("UID:AP2@soen342"),
                    "Speciality referral should be exported");

            check(!content.contains("UID:AP1@soen342")
                            && !content.contains("UID:AP4@soen342"),
                    "Other referral types must be excluded");

            System.out.println("Speciality referral test passed.");

            // Test 5: Default start date.
            controller.exportByPatient("P1", null, file);
            content = Files.readString(file);

            check(!content.contains("UID:AP3@soen342"),
                    "Past appointments must be excluded by default");

            check(content.contains("UID:AP1@soen342")
                            && content.contains("UID:AP4@soen342"),
                    "Future appointments should be included");

            System.out.println("Default start date test passed.");

            // Test 6: Custom start date.
            controller.exportByPatient("P1", pastDate, file);
            content = Files.readString(file);

            check(content.contains("UID:AP3@soen342"),
                    "Appointment on the custom start date should be included");

            check(content.contains("UID:AP1@soen342")
                            && content.contains("UID:AP4@soen342"),
                    "Later appointments should also be included");

            System.out.println("Custom start date test passed.");

            // Test 7: Confidentiality.
            controller.exportByProfessional("H1", pastDate, file);
            content = Files.readString(file);

            check(!content.contains("John")
                            && !content.contains("Smith"),
                    "Patient name must not appear in professional export");

            check(!content.contains("5145550100"),
                    "Patient telephone must not be exported");

            check(!content.contains("Confidential clinical reason"),
                    "Clinical information must not be exported");

            System.out.println("Confidentiality test passed.");

            // Test 8: Calendar structure.
            check(content.startsWith("BEGIN:VCALENDAR\r\n"),
                    "Calendar must have a valid beginning");

            check(content.contains("VERSION:2.0\r\n"),
                    "Calendar version must be included");

            check(content.contains("PRODID:"),
                    "Calendar product identifier must be included");

            check(content.contains("BEGIN:VEVENT\r\n")
                            && content.contains("END:VEVENT\r\n"),
                    "Appointment must have valid event boundaries");

            check(content.contains("DTSTAMP:")
                            && content.contains("DTSTART:")
                            && content.contains("DTEND:"),
                    "Required date and time fields must be included");

            check(content.endsWith("END:VCALENDAR\r\n"),
                    "Calendar must have a valid ending");

            System.out.println("Calendar structure test passed.");

            // Test 9: Empty results.
            controller.exportByPatient(
                    "P1", futureDate.plusDays(1), file);

            content = Files.readString(file);

            check(!content.contains("BEGIN:VEVENT"),
                    "No appointments should match the selected date");

            System.out.println("Empty results test passed.");

            // Test 10: UTC time conversion.
            AppointmentDB timeAppointments = new AppointmentDB();

            timeAppointments.newAppointment(
                    patient, professional, clinic, generalService,
                    new Availability(schedule, LocalDate.of(2030, 1, 1),
                            LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    null);

            CalendarExportController timeController =
                    new CalendarExportController(timeAppointments);

            timeController.exportByPatient(
                    "P1", LocalDate.of(2030, 1, 1), file);

            content = Files.readString(file);

            check(content.contains("DTSTART:20300101T140000Z"),
                    "Start time must be converted to UTC");

            check(content.contains("DTEND:20300101T143000Z"),
                    "End time must be converted to UTC");

            System.out.println("UTC conversion test passed.");

            // Test 11: Invalid referral type.
            boolean rejected = false;

            try {
                controller.exportByReferralType(
                        "invalid", futureDate, file);
            } catch (IllegalArgumentException expected) {
                rejected = true;
            }

            check(rejected, "Invalid referral type must be rejected");

            System.out.println("Invalid referral type test passed.");

            System.out.println("\nAll calendar export tests passed.");

        } finally {
            Files.deleteIfExists(file);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
