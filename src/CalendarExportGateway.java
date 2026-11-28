
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CalendarExportGateway {

    private static final ZoneId CLINIC_ZONE =
            ZoneId.of("America/Montreal");

    private static final DateTimeFormatter ICAL_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    public void export(List<Appointment> appointments,
                       Path filePath) throws IOException {

        StringBuilder calendar = new StringBuilder();

        calendar.append("BEGIN:VCALENDAR\r\n");
        calendar.append("VERSION:2.0\r\n");
        calendar.append("PRODID:-//SOEN342//Appointment System//EN\r\n");

        String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
                .format(ICAL_FORMAT);

        for (Appointment appointment : appointments) {

            Availability slot = appointment.getSlot();

            LocalDateTime start = LocalDateTime.of(
                    slot.getDate(), slot.getStartTime());

            LocalDateTime end = LocalDateTime.of(
                    slot.getDate(), slot.getEndTime());

            calendar.append("BEGIN:VEVENT\r\n");

            calendar.append("UID:")
                    .append(appointment.getAppointmentID())
                    .append("@soen342\r\n");

            calendar.append("DTSTAMP:")
                    .append(timestamp)
                    .append("\r\n");

            calendar.append("DTSTART:")
                    .append(formatDateTime(start))
                    .append("\r\n");

            calendar.append("DTEND:")
                    .append(formatDateTime(end))
                    .append("\r\n");

            calendar.append("SUMMARY:Booked Appointment\r\n");

            calendar.append("END:VEVENT\r\n");
        }

        calendar.append("END:VCALENDAR\r\n");

        Files.writeString(filePath, calendar.toString(),
                StandardCharsets.UTF_8);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(CLINIC_ZONE)
                .withZoneSameInstant(ZoneOffset.UTC)
                .format(ICAL_FORMAT);
    }
}
