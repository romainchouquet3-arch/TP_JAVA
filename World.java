package Catchme.Catchme.Catchme;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe représentant le monde des aéroports
 * Gère le chargement et la recherche des aéroports
 */
public class World {
    // Liste des aéroports chargés depuis le fichier CSV
    private ArrayList<Aeroport> list = new ArrayList<>();

    /**
     * Constructeur - Charge les aéroports depuis un fichier CSV
     * @param fileName Chemin vers le fichier CSV des aéroports
     */
    public World(String fileName) {
        // Pattern pour trouver les champs entre guillemets
        Pattern quoted = Pattern.compile("\"([^\"]*)\"");
        try (BufferedReader buf = new BufferedReader(new FileReader(fileName))) {
            // lire et ignorer l'en-tête
            String line = buf.readLine();
            while ((line = buf.readLine()) != null) {
                // Remplacer temporairement les virgules à l'intérieur des guillemets
                Matcher m = quoted.matcher(line);
                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    // remplacer la virgule interne par un point-virgule pour ne pas casser le split
                    String fixed = m.group(1).replace(",", ";");
                    // échapper backslashes éventuels pour appendReplacement
                    fixed = Matcher.quoteReplacement(fixed);
                    m.appendReplacement(sb, fixed);
                }
                m.appendTail(sb);
                String cleaned = sb.toString();

                // découpage en champs (on garde les champs vides avec -1)
                String[] fields = cleaned.split(",", -1);

                // Vérifie que la ligne contient au moins les colonnes attendues (indices basés sur le header fourni)
                // header example: ident,type,name,elevation_ft,continent,iso_country,iso_region,municipality,gps_code,iata_code,local_code,coordinates,,
                if (fields.length > 11) {
                    String type = fields[1].trim();
                    // Ne garde que les grands aéroports
                    if ("large_airport".equalsIgnoreCase(type)) {
                        try {
                            String name = fields[2].trim();
                            String country = fields[5].trim();
                            String iata = fields[9].trim();
                            if (iata.isEmpty()) continue; // on veut un code IATA

                            // coordinates a été nettoyé : lon; lat (virgule interne devenue ;)
                            String coordField = fields[11].trim();
                            String[] coords = coordField.split(";", 2);
                            if (coords.length < 2) continue;
                            double lon = Double.parseDouble(coords[0].trim());
                            double lat = Double.parseDouble(coords[1].trim());

                            Aeroport a = new Aeroport(iata, name, country, lat, lon);
                            list.add(a);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            // ignorer la ligne mal formée
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture du fichier CSV !");
            e.printStackTrace();
        }
    }

    /**
     * @return La liste complète des aéroports
     */
    public ArrayList<Aeroport> getList() {
        return list;
    }

    /**
     * Calcule la distance approximative entre deux points GPS
     * Utilise une approximation sphérique simplifiée
     * @return Distance au carré (pour optimisation - évite le calcul de la racine)
     */
    public double distance(double lon1, double lat1, double lon2, double lat2) {
        // Conversion en radians pour les calculs trigonométriques
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);

        // Formule de distance approximative sur une sphère
        return Math.pow(lat2 - lat1, 2) +
               Math.pow((lon2 - lon1) * Math.cos((lat2 + lat1) / 2), 2);
    }

    /**
     * Recherche un aéroport par son code IATA
     * @param code Code IATA à rechercher (ex: "CDG")
     * @return L'aéroport trouvé ou null si non trouvé
     */
    public Aeroport findByCode(String code) {
        for (Aeroport a : list) {
            if (a.getIATA() != null && a.getIATA().equalsIgnoreCase(code)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Trouve l'aéroport le plus proche d'une position GPS donnée
     * @param lon Longitude du point de référence
     * @param lat Latitude du point de référence
     * @return L'aéroport le plus proche ou null si la liste est vide
     */
    public Aeroport findNearestAirport(double lon, double lat) {
        if (list.isEmpty()) return null;

        Aeroport nearest = list.get(0);
        double minDist = distance(lon, lat, nearest.getLongitude(), nearest.getLatitude());

        for (Aeroport a : list) {
            double d = distance(lon, lat, a.getLongitude(), a.getLatitude());
            if (d < minDist) {
                minDist = d;
                nearest = a;
            }
        }
        return nearest;
    }

    /**
     * Programme de test
     */
    public static void main(String[] args) {
        String csv = "./data/airport-codes_no_comma.csv";
        if (!new File(csv).exists()) csv = "src/data/airport-codes_no_comma.csv";
        if (!new File(csv).exists()) {
            System.err.println("Fichier CSV introuvable. Placez-le dans ./data ou src/TP3D/data.");
            return;
        }

        World w = new World(csv);
        System.out.println("Nombre d'aéroports trouvés : " + w.getList().size());

        double lat = 48.866, lon = 2.316;
        Aeroport paris = w.findNearestAirport(lon, lat);
        Aeroport cdg = w.findByCode("CDG");

        if (paris != null) {
            double dist = w.distance(lon, lat, paris.getLongitude(), paris.getLatitude());
            System.out.println("Aéroport le plus proche de Paris : " + paris);
            System.out.println("Distance (approx) : " + dist);
        } else {
            System.out.println("Aucun aéroport trouvé à proximité.");
        }

        if (cdg != null) {
            double distCdg = w.distance(lon, lat, cdg.getLongitude(), cdg.getLatitude());
            System.out.println("CDG : " + cdg);
            System.out.println("Distance Paris-CDG : " + distCdg*6371); // en km (rayon Terre ~6371 km)
        } else {
            System.out.println("Aéroport CDG non trouvé.");
        }
    }
}
