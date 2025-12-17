public class Aeroport {
    private final String name;
    private final String iata;
    private final double longitude;
    private final double latitude;

    public Aeroport(String name, String iata, double longitude, double latitude) {
        this.name = name;
        this.iata = iata;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() { return name; }
    public String getIata() { return iata; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }


    public double calculDistance(Aeroport a) {
        double theta1 = Math.toRadians(a.latitude);
        double theta2 = Math.toRadians(this.latitude);

        double phi1 = Math.toRadians(a.longitude);
        double phi2 = Math.toRadians(this.longitude);

        double dTheta = theta2 - theta1;
        double dPhi = phi2 - phi1;

        double cosMeanTheta = Math.cos((theta2 + theta1) / 2.0);

        return (dTheta * dTheta) + (dPhi * cosMeanTheta) * (dPhi * cosMeanTheta);
    }

    @Override
    public String toString() {
        return "Aeroport{" +
                "name='" + name + '\'' +
                ", iata='" + iata + '\'' +
                ", lon=" + longitude +
                ", lat=" + latitude +
                '}';
    }
}
