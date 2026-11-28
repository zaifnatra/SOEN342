# SOEN342
SOEN342: Software Requirements and Deployment

- Huzaifa Fareed (40272738)
- Sia Shah (40275251)
- Maëlys Arnaud (40278798)

## Run the PoC

Requires JDK 17 or newer. From the repository folder (or VS Code terminal):

**macOS / Linux:**

```bash
bash run.sh
```

**Windows (PowerShell or Command Prompt; no Git Bash needed):**

```powershell
.\run.bat
```

No VS Code extension is needed to run these terminal commands.

Choose option **1**, then enter a CSV filename from `data/`, without the folder path.
Press Enter for `availability.csv` (Main Clinic), or enter `availability-second-clinic.csv`
for Second Clinic. You can also place your own availability CSV in `data/`. Register a fictional patient
with **3**, then book with **6**. Set `JAVA_HOME` if your JDK is not on PATH.

Data stays in memory. CSV exports availability only, not bookings or patients.
Each availability interval is one bookable slot. This demo assumes authorized staff.

## Referral checks

The referral factory creates a `ProfessionalReferral` or `SpecialityReferral` with
exactly one required, final target stored in its subclass. Abstract `Referral` exposes
read-only target getters; the getter for the other target kind returns `null`.

With JDK 17 or newer on your PATH:

```bash
javac --release 17 -encoding UTF-8 -d out src/*.java tests/ReferralTest.java
java -cp out ReferralTest
```

## iCalendar Export

The system supports exporting booked appointments to an iCalendar (.ics) file using the Gateway design pattern.

### How to export appointments

1. Run the application.
2. Select option 8 (Export booked appointments to iCalendar).
3. Select one of the following export types:
   - By healthcare professional
   - By patient
   - By referral type (professional or speciality)
4. Select the corresponding professional, patient or referral type.
5. Enter a start date in YYYY-MM-DD format or press Enter to use today's date.
6. Enter an output filename or press Enter to use `appointments.ics`.

Only confirmed appointments occurring on or after the selected start date are exported.

### Confidentiality

The generated calendar file contains the appointment identifier, start and end times, and a generic event title. Patient names, contact information, insurance information and clinical information are not included.

Appointment times are converted from the Montreal time zone to UTC. The generated file can be imported into external calendar applications such as Apple Calendar.

### Implementation

- `CalendarExportController.java`: Handles appointment retrieval and filtering.
- `CalendarExportGateway.java`: Generates the iCalendar file.
- `Console.java`: Provides the calendar export menu and handles user input.
- `CalendarExportTest.java`: Tests calendar export functionality.

### Testing

Compile and run the calendar export tests:

```bash
javac -d /tmp/soen342-build src/*.java tests/*.java
java -cp /tmp/soen342-build CalendarExportTest
```

The export functionality was also manually tested through the console and by importing a generated calendar file into Apple Calendar.

## Use Cases

The [initial requirements](artifacts/initial-requirements.md) list the critical and
non-critical use cases, their descriptions, and actors.

[SSD assumptions are here](artifacts/assumptions-and-notes.md).

## Contributing

See the [team workflow](.github/CONTRIBUTING.md) for issue templates, PR guidance,
and the one-approval review requirement for `main`.
