import java.time.LocalDateTime;

public class Flight {
    private final String airlineName;
    private final String flightIata;
    private final Aeroport departure;
    private final Aeroport arrival;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;

    public Flight(String airlineName, String flightIata,
                  Aeroport departure, Aeroport arrival,
                  LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.airlineName = airlineName;
        this.flightIata = flightIata;
        this.departure = departure;
        this.arrival = arrival;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public String getAirlineName() { return airlineName; }
    public String getFlightIata() { return flightIata; }
    public Aeroport getDeparture() { return departure; }
    public Aeroport getArrival() { return arrival; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
}
