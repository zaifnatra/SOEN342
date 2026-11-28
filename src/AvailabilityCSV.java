import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AvailabilityCSV {
    private static final List<String> HEADERS = List.of("First Name", "Last Name", "Title", "Type", "Specialty", "Date", "Start", "End");
    private final AvailabilityDB availabilityDB;
    private final ProfessionalScheduleDB professionalScheduleDB;
    private final HealthcareProfessionalDB healthcareProfessionalDB;
    private final MedicalSpecialityDB medicalSpecialityDB;
    private final ClinicLocationDB clinicLocationDB;

    public AvailabilityCSV(AvailabilityDB availabilityDB, ProfessionalScheduleDB professionalScheduleDB,
                           HealthcareProfessionalDB healthcareProfessionalDB, MedicalSpecialityDB medicalSpecialityDB,
                           ClinicLocationDB clinicLocationDB) {
        this.availabilityDB = availabilityDB;
        this.professionalScheduleDB = professionalScheduleDB;
        this.healthcareProfessionalDB = healthcareProfessionalDB;
        this.medicalSpecialityDB = medicalSpecialityDB;
        this.clinicLocationDB = clinicLocationDB;
    }

    public int importFromCSV(String filePath) throws IOException {
        List<List<String>> rows = Csv.read(Files.readString(Path.of(filePath), StandardCharsets.UTF_8));
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("CSV is empty. A header row is required.");
        }
        List<String> headers = rows.get(0).stream().map(String::strip).toList();
        if (!headers.containsAll(HEADERS) || new HashSet<>(headers).size() != headers.size()
                || headers.stream().anyMatch(header -> !HEADERS.contains(header) && !header.equals("Clinic"))) {
            throw new IllegalArgumentException("CSV headers must be: " + String.join(",", HEADERS) + " (plus optional Clinic).");
        }
        List<CsvSlot> known = new ArrayList<>();
        for (Availability slot : availabilityDB.findAll()) {
            known.add(CsvSlot.from(slot));
        }
        List<CsvSlot> pending = new ArrayList<>();
        for (int index = 1; index < rows.size(); index++) {
            try {
                CsvSlot candidate = parse(headers, rows.get(index));
                validateProfessional(candidate);
                boolean duplicate = false;
                for (CsvSlot existing : known) {
                    if (candidate.sameProfessional(existing)) {
                        if (!candidate.title().equalsIgnoreCase(existing.title())
                            || candidate.type() != existing.type() || !candidate.specialty().equalsIgnoreCase(existing.specialty())) {
                            throw new IllegalArgumentException("Conflicting title/type/specialty for " + candidate.firstName() + " " + candidate.lastName());
                        }
                        if (candidate.date().equals(existing.date()) && candidate.start().isBefore(existing.end())
                                && existing.start().isBefore(candidate.end())) {
                            if (candidate.start().equals(existing.start()) && candidate.end().equals(existing.end())
                                    && candidate.clinic().equalsIgnoreCase(existing.clinic())) {
                                duplicate = true;
                            } else {
                                throw new IllegalArgumentException("Overlapping availability for " + candidate.firstName() + " " + candidate.lastName());
                            }
                        }
                    }
                }
                if (!duplicate) {
                    known.add(candidate);
                    pending.add(candidate);
                }
            } catch (IllegalArgumentException | DateTimeParseException exception) {
                throw new IllegalArgumentException("CSV record " + (index + 1) + ": " + exception.getMessage()
                        + " Nothing was imported.");
            }
        }
        List<Availability> slots = new ArrayList<>();
        for (CsvSlot row : pending) {
            MedicalSpeciality speciality = medicalSpecialityDB.findOrCreate(row.specialty());
            HealthcareProfessional professional = healthcareProfessionalDB.findOrCreate(row.firstName(), row.lastName(), row.title(), speciality, row.type());
            ClinicLocation clinic = clinicLocationDB.findOrCreate(row.clinic());
            ProfessionalSchedule schedule = professionalScheduleDB.findOrCreate(professional, clinic);
            slots.add(new Availability(schedule, row.date(), row.start(), row.end()));
        }
        availabilityDB.addAll(slots);
        return slots.size();
    }

    public int exportToCSV(String filePath) throws IOException {
        List<String> headers = new ArrayList<>(HEADERS);
        headers.add("Clinic");
        StringBuilder text = new StringBuilder(Csv.writeRow(headers));
        List<Availability> slots = availabilityDB.findAll();
        for (Availability slot : slots) {
            CsvSlot row = CsvSlot.from(slot);
            text.append(Csv.writeRow(List.of(row.firstName(), row.lastName(), row.title(), row.type().name(), row.specialty(),
                    row.date().toString(), row.start().toString(), row.end().toString(), row.clinic())));
        }
        Path target = Path.of(filePath).toAbsolutePath();
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), ".availability-", ".csv");
        try {
            Files.writeString(temporary, text, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
        return slots.size();
    }

    private CsvSlot parse(List<String> headers, List<String> values) {
        if (values.size() != headers.size()) {
            throw new IllegalArgumentException("Incorrect number of columns.");
        }
        List<String> fields = values.stream().map(String::strip).toList();
        for (String header : HEADERS) {
            Checks.text(fields.get(headers.indexOf(header)), header);
        }
        ProfessionalType type;
        try {
            type = ProfessionalType.valueOf(fields.get(headers.indexOf("Type")));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Type must be one of: " + String.join(", ",
                    java.util.Arrays.stream(ProfessionalType.values()).map(Enum::name).toList()));
        }
        LocalDate date = LocalDate.parse(fields.get(headers.indexOf("Date")));
        LocalTime start = LocalTime.parse(fields.get(headers.indexOf("Start")));
        LocalTime end = LocalTime.parse(fields.get(headers.indexOf("End")));
        Checks.interval(start, end);
        String clinic = headers.contains("Clinic") ? Checks.text(fields.get(headers.indexOf("Clinic")), "Clinic") : "Main Clinic";
        return new CsvSlot(fields.get(headers.indexOf("First Name")), fields.get(headers.indexOf("Last Name")),
                fields.get(headers.indexOf("Title")), type, fields.get(headers.indexOf("Specialty")), clinic, date, start, end);
    }

    private void validateProfessional(CsvSlot row) {
        HealthcareProfessional existing = healthcareProfessionalDB.findByName(row.firstName(), row.lastName());
        if (existing != null && (!existing.getTitle().equalsIgnoreCase(row.title()) || existing.getType() != row.type()
                || !existing.getSpeciality().getSpecialtyName().equalsIgnoreCase(row.specialty()))) {
            throw new IllegalArgumentException("This professional already has different title/type/specialty details.");
        }
    }

    private record CsvSlot(String firstName, String lastName, String title, ProfessionalType type, String specialty, String clinic,
                           LocalDate date, LocalTime start, LocalTime end) {
        static CsvSlot from(Availability slot) {
            HealthcareProfessional professional = slot.getSchedule().getProfessional();
            return new CsvSlot(professional.getFirstName(), professional.getLastName(), professional.getTitle(), professional.getType(),
                    professional.getSpeciality().getSpecialtyName(), slot.getSchedule().getClinic().getAddress(),
                    slot.getDate(), slot.getStartTime(), slot.getEndTime());
        }

        boolean sameProfessional(CsvSlot other) {
            return firstName.equalsIgnoreCase(other.firstName()) && lastName.equalsIgnoreCase(other.lastName());
        }
    }
}
