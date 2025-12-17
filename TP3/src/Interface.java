import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Interface extends Application {

    private World w;
    private Earth earth;
    private PerspectiveCamera camera;

    private double lastX, lastY;
    private boolean draggingRight = false;

    private static final String ACCESS_KEY = "0eb6c1c0b72e9412134d7db4f344ed19";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TERRRRRRRRRE");

        w = new World("data/airport-codes_no_comma.csv");
        earth = new Earth();

        Group root = new Group(earth);
        Scene scene = new Scene(root, 900, 700, true, SceneAntialiasing.BALANCED);

        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(4000);
        scene.setCamera(camera);

        scene.addEventHandler(MouseEvent.ANY, this::handleMouse);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleMouse(MouseEvent event) {


        if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.SECONDARY) {
            draggingRight = true;
            lastX = event.getSceneX();
            lastY = event.getSceneY();
        }

        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && draggingRight && event.getButton() == MouseButton.SECONDARY) {
            double x = event.getSceneX();
            double y = event.getSceneY();

            double dx = x - lastX;
            double dy = y - lastY;

            lastX = x;
            lastY = y;

            earth.addRotation(dy * 0.3, dx * 0.3);
        }

        if (event.getEventType() == MouseEvent.MOUSE_RELEASED && event.getButton() == MouseButton.SECONDARY) {
            draggingRight = false;
        }


        if (event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getButton() == MouseButton.PRIMARY) {
            PickResult pr = event.getPickResult();
            if (pr == null || pr.getIntersectedNode() == null) return;

            Point2D uv = pr.getIntersectedTexCoord();
            if (uv == null) return;

            double X = uv.getX();
            double Y = uv.getY();

            double lat = 180.0 * (0.5 - Y);
            double lon = 360.0 * (X - 0.5);

            System.out.println("Clicked lon/lat = " + lon + " / " + lat);

            Aeroport nearest = w.findNearestAirport(lon, lat);
            System.out.println("Nearest = " + nearest);

            if (nearest != null) {
                earth.displayRedSphere(nearest);
                earth.clearYellowLeds();
                fetchFlightsAndDisplayYellow(nearest);
            }
        }
    }

    private void fetchFlightsAndDisplayYellow(Aeroport arrival) {
        new Thread(() -> {
            try {
                String json = null;


                if (ACCESS_KEY != null && !ACCESS_KEY.isBlank()) {
                    String arr = arrival.getIata();
                    String arrEncoded = URLEncoder.encode(arr, StandardCharsets.UTF_8);

                    String url = "http://api.aviationstack.com/v1/flights?access_key=" + ACCESS_KEY + "&arr_iata=" + arrEncoded;

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

                    System.out.println("HTTP status = " + response.statusCode());

                    if (response.statusCode() == 200) {
                        json = response.body();
                    } else {
                        System.out.println("⚠ API KO (status " + response.statusCode() + "), fallback -> data/test.txt");
                    }
                } else {
                    System.out.println("⚠ ACCESS_KEY vide, fallback -> data/test.txt");
                }

                // 2) Fallback test.txt si API KO (429 etc.)
                if (json == null || json.isBlank()) {
                    json = readFile("data/test.txt");
                    if (json == null || json.isBlank()) {
                        System.out.println("❌ API KO ET data/test.txt introuvable/vide.");
                        return;
                    }
                }

                JsonFlight parser = new JsonFlight(json);

                Platform.runLater(() -> {
                    int shown = 0;
                    int notFound = 0;

                    for (String depCode : parser.getDepartureCodes()) {
                        Aeroport dep = w.findByCode(depCode);
                        if (dep != null) {
                            earth.displayYellowLed(dep);
                            shown++;
                        } else {
                            notFound++;
                        }
                    }

                    System.out.println("Departure codes = " + parser.getDepartureCodes().size());
                    System.out.println("LEDs shown = " + shown);
                    System.out.println("Not found in World = " + notFound + " (souvent car on garde seulement large_airport)");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String readFile(String path) {
        try {
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Erreur lecture fichier: " + path);
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
