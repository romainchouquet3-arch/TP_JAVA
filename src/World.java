import java.io.*;
import java.util.*;

public class World {
    private final ArrayList<Aeroport> list = new ArrayList<>();

    public World(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new RuntimeException("CSV vide");

            List<String> header = parseCsvLine(headerLine);
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < header.size(); i++) idx.put(header.get(i).trim(), i);

            int iType = idx.getOrDefault("type", -1);
            int iName = idx.getOrDefault("name", -1);
            int iIata = idx.getOrDefault("iata_code", -1);
            int iCoord = idx.getOrDefault("coordinates", -1);

            if (iType < 0 || iName < 0 || iIata < 0 || iCoord < 0) {
                throw new RuntimeException("Colonnes manquantes. Header=" + header);
            }

            String line;
            while ((line = br.readLine()) != null) {
                List<String> f = parseCsvLine(line);
                int need = Math.max(Math.max(iType, iName), Math.max(iIata, iCoord));
                if (f.size() <= need) continue;

                String type = f.get(iType).trim();
                String name = f.get(iName).trim();
                String iata = f.get(iIata).trim();
                String coord = f.get(iCoord).trim();


                if (!"large_airport".equals(type)) continue;
                if (iata.isEmpty()) continue;

                String[] parts = coord.split("\\s*,\\s*");
                if (parts.length < 2) continue;

                double lon = Double.parseDouble(parts[0]);
                double lat = Double.parseDouble(parts[1]);

                list.add(new Aeroport(name, iata, lon, lat));
            }

            System.out.println("Airports loaded = " + list.size());
            System.out.println("Example CDG = " + findByCode("CDG"));

        } catch (Exception e) {
            System.out.println("Erreur lecture CSV: " + fileName);
            e.printStackTrace();
        }
    }


    private List<String> parseCsvLine(String line) {
        ArrayList<String> out = new ArrayList<>();
        if (line == null) return out;

        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }

    public ArrayList<Aeroport> getList() { return list; }

    public Aeroport findByCode(String code) {
        if (code == null) return null;
        String c = code.trim().toUpperCase();
        for (Aeroport a : list) {
            if (a.getIata().equalsIgnoreCase(c)) return a;
        }
        return null;
    }

    public Aeroport findNearestAirport(double longitude, double latitude) {
        if (list.isEmpty()) return null;

        Aeroport ref = new Aeroport("CLICK", "XXX", longitude, latitude);
        Aeroport best = null;
        double bestD = Double.POSITIVE_INFINITY;

        for (Aeroport a : list) {
            double d = a.calculDistance(ref);
            if (d < bestD) {
                bestD = d;
                best = a;
            }
        }
        return best;
    }
}
