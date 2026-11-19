import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Console {
    private final PatientDB patientDB = new PatientDB();
    private final HealthcareProfessionalDB healthcareProfessionalDB = new HealthcareProfessionalDB();
    private final MedicalSpecialityDB medicalSpecialityDB = new MedicalSpecialityDB();
    private final ClinicLocationDB clinicLocationDB = new ClinicLocationDB();
    private final ProfessionalScheduleDB professionalScheduleDB = new ProfessionalScheduleDB();
    private final MedicalServiceDB medicalServiceDB = new MedicalServiceDB();
    private final ReferralDB referralDB = new ReferralDB();
    private final AppointmentDB appointmentDB = new AppointmentDB();
    private final AvailabilityDB availabilityDB = new AvailabilityDB(appointmentDB);
    private final RegisterPatientController registerPatientController = new RegisterPatientController(patientDB);
    private final AvailabilityCSV availabilityCSV = new AvailabilityCSV(availabilityDB, professionalScheduleDB,
            healthcareProfessionalDB, medicalSpecialityDB, clinicLocationDB);
    private final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        new Console().run();
    }

    public void run() {
        printHeading("SOEN 342 - Clinic Proof of Concept");
        while (true) {
            printHeading("Main menu");
            System.out.println("""
                    1. Load professionals' appointment slots from a CSV file
                    2. Register a healthcare professional and their clinic workplace
                    3. Register a patient who needs an appointment

                    4. Add dates and times when a healthcare professional can see patients
                    5. Find unbooked appointment slots by professional, clinic and dates
                    6. Book a patient's appointment with a healthcare professional
                    7. View all patients' booked appointments

                    8. Save professionals' appointment slots to CSV (not patient bookings)
                    9. Refer a patient to another professional or specialty

                    0. Exit (discard session data)
                    """);
            try {
                switch (ask("Option")) {
                    case "1" -> importAvailability();
                    case "2" -> registerProfessional();
                    case "3" -> registerPatient();
                    case "4" -> makeAvailability();
                    case "5" -> searchAvailability();
                    case "6" -> bookAppointment();
                    case "7" -> {
                        printHeading("Patients' booked appointments");
                        display(appointmentDB.findAll());
                    }
                    case "8" -> exportAvailability();
                    case "9" -> createReferral();
                    case "0" -> {
                        System.out.println("Goodbye. Session data is discarded; exported CSV files remain.");
                        return;
                    }
                    default -> System.out.println("Choose a menu number from 0 to 9.");
                }
            } catch (IllegalArgumentException | IOException exception) {
                System.out.println("Error: " + exception.getMessage());
            } catch (NoSuchElementException exception) {
                System.out.println("\nInput closed. Session ended.");
                return;
            }
        }
    }

    public Appointment bookAppointment(String patientID, String professionalID, String clinicLocationID,
                                       String serviceType, LocalDateTime requestedDateTime, String referralID) {
        Patient patient = Checks.required(patientDB.find(patientID), "Patient");
        HealthcareProfessional professional = Checks.required(healthcareProfessionalDB.find(professionalID), "Professional");
        ClinicLocation clinic = Checks.required(clinicLocationDB.find(clinicLocationID), "Clinic");
        MedicalService service = Checks.required(medicalServiceDB.find(serviceType), "Service");
        Checks.required(requestedDateTime, "Appointment date/time");
        Availability slot = searchAvailability(professionalID, clinicLocationID, requestedDateTime,
                requestedDateTime.toLocalDate().plusDays(1).atStartOfDay()).stream()
                .filter(available -> available.getStart().equals(requestedDateTime)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No free slot starts at the requested time."));
        Referral referral = referralID == null || referralID.isBlank() ? null
                : Checks.required(referralDB.find(referralID), "Referral");
        return appointmentDB.newAppointment(patient, professional, clinic, service, slot, referral);
    }

    public List<Availability> searchAvailability(String professionalID, String clinicLocationID,
                                                 LocalDateTime from, LocalDateTime to) {
        return availabilityDB.findAvailable(professionalID, clinicLocationID, from, to);
    }

    public List<Availability> makeAvailability(String professionalID, String clinicLocationID, LocalDate dateFrom,
                                               LocalDate dateTo, String repetitionPattern, LocalTime startTime, LocalTime endTime) {
        ProfessionalSchedule schedule = Checks.required(professionalScheduleDB.find(professionalID, clinicLocationID),
                "Professional's schedule at this clinic (register the professional at the clinic first)");
        List<Availability> slots = schedule.makeAvailability(dateFrom, dateTo, repetitionPattern, startTime, endTime);
        availabilityDB.addAll(slots);
        return slots;
    }

    public int importAvailability(String filePath) throws IOException {
        return availabilityCSV.importFromCSV(filePath);
    }

    public int exportAvailability(String filePath) throws IOException {
        return availabilityCSV.exportToCSV(filePath);
    }

    private void importAvailability() throws IOException {
        printHeading("Load professionals' appointment slots from CSV");
        String filename = ask("CSV filename in data/ (Enter = availability.csv)");
        if (filename.isBlank()) {
            filename = "availability.csv";
        }
        if (filename.contains("/") || filename.contains("\\") || filename.equals(".") || filename.equals("..")) {
            throw new IllegalArgumentException("Enter only the filename, without a folder path.");
        }
        Path path = Path.of("data").resolve(filename);
        System.out.println("Imported " + importAvailability(path.toString()) + " new slots (duplicates skipped).");
    }

    private void registerProfessional() {
        printHeading("Register a healthcare professional and workplace");
        String firstName = Checks.text(ask("First name"), "First name");
        String lastName = Checks.text(ask("Last name"), "Last name");
        String title = Checks.text(ask("Title, e.g. Dr. or Nurse Practitioner"), "Title");
        String specialty = Checks.text(ask("Specialty"), "Specialty");
        String address = Checks.text(ask("Clinic name/address"), "Clinic name/address");
        HealthcareProfessional existing = healthcareProfessionalDB.findByName(firstName, lastName);
        if (existing != null && (!existing.getTitle().equalsIgnoreCase(title)
                || !existing.getSpeciality().getSpecialtyName().equalsIgnoreCase(specialty))) {
            throw new IllegalArgumentException("This professional already has different title/specialty details.");
        }
        HealthcareProfessional professional = healthcareProfessionalDB.findOrCreate(firstName, lastName, title,
                medicalSpecialityDB.findOrCreate(specialty));
        ClinicLocation clinic = clinicLocationDB.findOrCreate(address);
        professionalScheduleDB.findOrCreate(professional, clinic);
        System.out.println("Registered " + professional + " at " + clinic);
    }

    private void registerPatient() {
        printHeading("Register a patient");
        Map<String, String> personal = Map.of("firstName", ask("First name"), "lastName", ask("Last name"),
                "dateOfBirth", askDate("Date of birth").toString(), "phoneNumber", ask("Telephone"));
        String insuranceNumber = ask("RAMQ/private insurance number (Enter if none)");
        Map<String, String> insurance = insuranceNumber.isBlank() ? null
                : Map.of("insuranceNumber", insuranceNumber, "insuranceType", ask("Insurance type (RAMQ or insurer name)"));
        System.out.println("Registered " + registerPatientController.registerPatient(personal, insurance));
    }

    private void makeAvailability() {
        printHeading("Add a professional's appointment slots");
        System.out.println("Create appointment slots for a healthcare professional (not a patient booking).");
        System.out.println("Whose availability do you want to add?");
        HealthcareProfessional professional = choose(healthcareProfessionalDB.findAll(), "Professional");
        List<ClinicLocation> clinics = clinicLocationDB.findAll().stream()
                .filter(clinic -> professionalScheduleDB.find(professional.getProfessionalID(), clinic.getLocationID()) != null).toList();
        System.out.println("Where will " + professional.getTitle() + " " + professional.getName() + " see patients?");
        System.out.println("Only clinics registered for this professional are listed. Use option 2 to add another workplace.");
        ClinicLocation clinic = choose(clinics, "Clinic location");
        System.out.println("Enter the first and last dates separately, one date per answer. Both dates are included.");
        System.out.println("For one day only, enter the same date twice. The repeat setting chooses days within this range.");
        LocalDate from = askDate("First date to create slots");
        LocalDate to = askLastDate(from);
        String pattern = ask("Repeat: DAILY, MONTHLY, or weekdays e.g. MONDAY,WEDNESDAY");
        LocalTime start = askTime("Slot start time on each matching day");
        LocalTime end = askTime("Slot end time on each matching day");
        List<Availability> slots = makeAvailability(professional.getProfessionalID(), clinic.getLocationID(), from, to, pattern, start, end);
        System.out.println("Created " + slots.size() + " slots. Each interval is booked as one appointment.");
        display(slots);
    }

    private void searchAvailability() {
        printHeading("Find unbooked appointment slots");
        HealthcareProfessional professional = chooseOptional(healthcareProfessionalDB.findAll(), "Professional (0 = all)");
        ClinicLocation clinic = chooseOptional(clinicLocationDB.findAll(), "Clinic (0 = all)");
        System.out.println("Enter the first and last search dates separately. Both dates are included; repeat the date for one day only.");
        LocalDate from = askDate("First date to search");
        LocalDate to = askLastDate(from);
        display(searchAvailability(professional == null ? null : professional.getProfessionalID(),
                clinic == null ? null : clinic.getLocationID(), from.atStartOfDay(), to.plusDays(1).atStartOfDay()));
    }

    private void bookAppointment() {
        printHeading("Book an appointment for a patient");
        System.out.println("Choose the patient, then an unbooked slot showing the professional, clinic, date and time.");
        Patient patient = choose(patientDB.findAll(), "Patient to book for");
        Availability slot = choose(searchAvailability(null, null, LocalDateTime.MIN, LocalDateTime.MAX), "Appointment slot");
        MedicalService service = choose(medicalServiceDB.findAll(), "Type of consultation");
        String referralID = null;
        if (service.isReferralRequired()) {
            List<Referral> eligible = referralDB.findAll().stream()
                    .filter(referral -> referral.isValidFor(patient, slot.getSchedule().getProfessional(), service)).toList();
            referralID = choose(eligible, "Referral (use option 9 first if none)").getReferralID();
        }
        Appointment appointment = bookAppointment(patient.getPatientID(), slot.getSchedule().getProfessional().getProfessionalID(),
                slot.getSchedule().getClinic().getLocationID(), service.getServiceID(), slot.getStart(), referralID);
        System.out.println("Appointment confirmed: " + appointment);
    }

    private void createReferral() {
        printHeading("Refer a patient");
        Patient patient = choose(patientDB.findAll(), "Patient");
        HealthcareProfessional referring = choose(healthcareProfessionalDB.findAll(), "Referring professional");
        String targetType = ask("Target: 1 = professional, 2 = specialty");
        HealthcareProfessional targetProfessional = null;
        MedicalSpeciality targetSpeciality = null;
        if (targetType.equals("1")) {
            targetProfessional = choose(healthcareProfessionalDB.findAll(), "Referred professional");
        } else if (targetType.equals("2")) {
            targetSpeciality = choose(medicalSpecialityDB.findAll(), "Referred specialty");
        } else {
            throw new IllegalArgumentException("Target must be 1 or 2.");
        }
        String priority = ask("Priority, e.g. routine or urgent");
        String reason = ask("Clinical reason");
        System.out.println("Referral created: " + referralDB.newReferral(patient, referring, priority, reason,
                targetProfessional, targetSpeciality));
    }

    private void exportAvailability() throws IOException {
        printHeading("Save professionals' appointment slots to CSV");
        String path = Checks.text(ask("Output CSV path, e.g. exports/availability.csv"), "Output path");
        if (Files.exists(Path.of(path)) && !ask("File exists. Overwrite? (yes/no)").equalsIgnoreCase("yes")) {
            System.out.println("Export cancelled.");
            return;
        }
        System.out.println("Exported " + exportAvailability(path) + " slots to " + Path.of(path).toAbsolutePath());
        System.out.println("Availability only; patient records, referrals and bookings are not exported.");
    }

    private void printHeading(String title) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println(title);
        System.out.println("------------------------------------------------------------\n");
    }

    private String ask(String prompt) {
        System.out.println();
        System.out.print(prompt + ": ");
        return input.nextLine().strip();
    }

    private LocalDate askDate(String prompt) {
        while (true) {
            String answer = ask(prompt + " (one date, YYYY-MM-DD, e.g. 2026-11-06)");
            try {
                return LocalDate.parse(answer);
            } catch (DateTimeParseException exception) {
                System.out.println("Enter one valid calendar date, e.g. 2026-11-06. Do not enter a list or a date range.");
            }
        }
    }

    private LocalDate askLastDate(LocalDate firstDate) {
        while (true) {
            LocalDate lastDate = askDate("Last date (on or after " + firstDate + ")");
            if (!lastDate.isBefore(firstDate)) {
                return lastDate;
            }
            System.out.println("The last date cannot be before " + firstDate + ". Enter it again.");
        }
    }

    private LocalTime askTime(String prompt) {
        while (true) {
            String answer = ask(prompt + " (24-hour HH:MM, e.g. 09:00)");
            try {
                return LocalTime.parse(answer);
            } catch (DateTimeParseException exception) {
                System.out.println("Enter one valid time in 24-hour format, e.g. 09:00 or 14:30.");
            }
        }
    }

    private <T> T choose(List<T> values, String prompt) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("No entries for " + prompt + ". Import or register the required data first.");
        }
        System.out.println("\n" + prompt + " — choose a list number:\n");
        display(values);
        int selected = chooseNumber(prompt, 1, values.size());
        return values.get(selected - 1);
    }

    private <T> T chooseOptional(List<T> values, String prompt) {
        System.out.println("\n" + prompt + " — choose a list number:\n");
        display(values);
        int selected = chooseNumber(prompt, 0, values.size());
        return selected == 0 ? null : values.get(selected - 1);
    }

    private int chooseNumber(String prompt, int minimum, int maximum) {
        while (true) {
            String answer = ask(prompt + " (list number " + minimum + "–" + maximum + ")");
            try {
                int selected = Integer.parseInt(answer);
                if (selected >= minimum && selected <= maximum) {
                    return selected;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Enter a list number, not a name or an internal ID.");
                continue;
            }
            System.out.println(minimum == maximum ? "Only option " + minimum + " is listed. Enter " + minimum + "."
                    : "Enter a list number from " + minimum + " to " + maximum + ".");
        }
    }

    private void display(List<?> values) {
        if (values.isEmpty()) {
            System.out.println("No entries found.");
        }
        for (int index = 0; index < values.size(); index++) {
            System.out.println((index + 1) + ". " + values.get(index));
        }
    }
}
