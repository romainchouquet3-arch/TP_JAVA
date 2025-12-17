import java.util.ArrayList;
import java.util.HashSet;

public class JsonFlight {
    private final ArrayList<String> departureCodes = new ArrayList<>();

    public JsonFlight(String json) {
        if (json == null || json.isBlank()) return;

        if (json.contains("\"error\"")) {
            System.out.println("⚠ API returned error JSON:");
            System.out.println(json);
            return;
        }

        HashSet<String> uniq = new HashSet<>();

        int idx = 0;
        while (true) {
            int depPos = json.indexOf("\"departure\"", idx);
            if (depPos < 0) break;

            int windowEnd = Math.min(json.length(), depPos + 1200);
            String window = json.substring(depPos, windowEnd);

            int iataPos = window.indexOf("\"iata\"");
            if (iataPos >= 0) {
                int colon = window.indexOf(':', iataPos);
                if (colon >= 0) {
                    int q1 = window.indexOf('"', colon + 1);
                    if (q1 >= 0) {
                        int q2 = window.indexOf('"', q1 + 1);
                        if (q2 >= 0) {
                            String code = window.substring(q1 + 1, q2).trim();
                            if (!code.isEmpty() && !"null".equalsIgnoreCase(code)) {
                                uniq.add(code.toUpperCase());
                            }
                        }
                    }
                }
            }

            idx = depPos + 10;
        }

        departureCodes.addAll(uniq);
    }

    public ArrayList<String> getDepartureCodes() {
        return departureCodes;
    }
}
