package Catchme.Catchme.Catchme;

/**
 * Classe représentant un aéroport avec ses informations de base
 * Stocke le code IATA, le nom, le pays et les coordonnées GPS
 */
public class Aeroport {
    // Code IATA unique de l'aéroport (ex: CDG, ORY)
    private final String iata;
    // Nom complet de l'aéroport
    private final String name;
    // Pays où se trouve l'aéroport
    private final String country;
    // Coordonnées GPS
    private final double latitude;
    private final double longitude;

    /**
     * Constructeur - Crée un nouvel aéroport
     * @param iata Code IATA de l'aéroport
     * @param name Nom complet de l'aéroport
     * @param country Pays de l'aéroport
     * @param latitude Latitude GPS
     * @param longitude Longitude GPS
     */
    public Aeroport(String iata, String name, String country, double latitude, double longitude) {
        // Gestion des valeurs null pour les chaînes
        this.iata = iata == null ? "" : iata;
        this.name = name == null ? "" : name;
        this.country = country == null ? "" : country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Accesseurs (getters)
    /**
     * @return Le code IATA de l'aéroport
     */
    public String getIATA() { return iata; }

    /**
     * @return Le nom complet de l'aéroport
     */
    public String getName() { return name; }

    /**
     * @return Le pays de l'aéroport
     */
    public String getCountry() { return country; }

    /**
     * @return La latitude GPS de l'aéroport
     */
    public double getLatitude() { return latitude; }

    /**
     * @return La longitude GPS de l'aéroport
     */
    public double getLongitude() { return longitude; }

    /**
     * Conversion en chaîne de caractères pour l'affichage
     * @return Une représentation textuelle de l'aéroport
     */
    @Override
    public String toString() {
        return String.format("Aeroport[iata=%s, name=%s, country=%s, lat=%.6f, lon=%.6f]",
                             iata, name, country, latitude, longitude);
    }
}