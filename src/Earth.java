import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import java.io.FileInputStream;
import java.util.HashSet;

public class Earth extends Group {
    private final double R = 300.0;


    private final Group globeGroup = new Group();

    private final Sphere sph;

    private final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    private final Rotate ry = new Rotate(0, Rotate.Y_AXIS);

    private Sphere redBall = null;
    private final HashSet<String> yellowShown = new HashSet<>();

    public Earth() {
        sph = new Sphere(R);

        try {
            PhongMaterial mat = new PhongMaterial();
            Image tex = new Image(new FileInputStream("data/earth.png"));
            mat.setDiffuseMap(tex);
            sph.setMaterial(mat);
        } catch (Exception e) {
            System.out.println("Texture introuvable: data/earth.png");
            e.printStackTrace();
        }


        globeGroup.getTransforms().addAll(rx, ry);

        globeGroup.getChildren().add(sph);
        this.getChildren().add(globeGroup);
    }

    public void addRotation(double dPitchDeg, double dYawDeg) {
        rx.setAngle(clamp(rx.getAngle() + dPitchDeg, -90, 90));
        ry.setAngle(ry.getAngle() + dYawDeg);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private Point3D lonLatToXYZ(double longitudeDeg, double latitudeDeg) {
        double lat = Math.toRadians(latitudeDeg / 1.3);
        double lon = Math.toRadians(longitudeDeg);

        double x = R * Math.cos(lat) * Math.sin(lon);
        double y = -R * Math.sin(lat);
        double z = -R * Math.cos(lat) * Math.cos(lon);

        return new Point3D(x, y, z);
    }

    private Sphere createPoint(Aeroport a, Color color, double radius) {
        Sphere s = new Sphere(radius);
        s.setMaterial(new PhongMaterial(color));

        Point3D p = lonLatToXYZ(a.getLongitude(), a.getLatitude());
        s.setTranslateX(p.getX());
        s.setTranslateY(p.getY());
        s.setTranslateZ(p.getZ());

        return s;
    }

    public void displayRedSphere(Aeroport a) {
        if (a == null) return;

        if (redBall != null) globeGroup.getChildren().remove(redBall);

        redBall = createPoint(a, Color.RED, 4);
        globeGroup.getChildren().add(redBall);
    }

    public void clearYellowLeds() {
        globeGroup.getChildren().removeIf(node ->
                (node instanceof Sphere) && node != sph && node != redBall
        );
        yellowShown.clear();
    }

    public void displayYellowLed(Aeroport a) {
        if (a == null) return;
        if (yellowShown.contains(a.getIata())) return;

        Sphere y = createPoint(a, Color.YELLOW, 2.5);
        globeGroup.getChildren().add(y);
        yellowShown.add(a.getIata());
    }
}
