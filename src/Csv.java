import java.util.ArrayList;
import java.util.List;

final class Csv {
    private Csv() {}

    static List<List<String>> read(String text) {
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1);
        }
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean closedQuote = false;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (quoted) {
                if (character == '"') {
                    if (index + 1 < text.length() && text.charAt(index + 1) == '"') {
                        field.append('"');
                        index++;
                    } else {
                        quoted = false;
                        closedQuote = true;
                    }
                } else {
                    field.append(character);
                }
            } else if (character == ',' || character == '\n' || character == '\r') {
                row.add(field.toString());
                field.setLength(0);
                closedQuote = false;
                if (character != ',') {
                    if (row.size() != 1 || !row.get(0).isBlank()) {
                        rows.add(row);
                    }
                    row = new ArrayList<>();
                    if (character == '\r' && index + 1 < text.length() && text.charAt(index + 1) == '\n') {
                        index++;
                    }
                }
            } else if (closedQuote) {
                throw new IllegalArgumentException("CSV: unexpected character after a closing quote.");
            } else if (character == '"') {
                if (!field.isEmpty()) {
                    throw new IllegalArgumentException("CSV: quote inside an unquoted field.");
                }
                quoted = true;
            } else {
                field.append(character);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("CSV: missing closing quote.");
        }
        if (!row.isEmpty() || !field.isEmpty() || closedQuote) {
            row.add(field.toString());
            rows.add(row);
        }
        return rows;
    }

    static String writeRow(List<String> fields) {
        List<String> escaped = new ArrayList<>();
        for (String field : fields) {
            escaped.add("\"" + field.replace("\"", "\"\"") + "\"");
        }
        return String.join(",", escaped) + "\n";
    }
}
