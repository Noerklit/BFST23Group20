package View;

import java.util.HashMap;
import java.util.Iterator;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

import Model.Model;
import Model.DrawType;
import Model.Edge;

public class View {
    public Canvas canvas = new Canvas(1500, 1200);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    double scalingConst;
    double x1 = 100;
    double y1 = 100;
    double x2 = 200;
    double y2 = 800;

    Affine trans = new Affine();

    Model model;
    HashMap<DrawType, Color> defaultColorMap;
    int fidelity; 

    public View(Model model, Stage primaryStage) {
        this.model = model;
        createColorMap();
        primaryStage.setTitle("Draw Lines");
        BorderPane pane = new BorderPane(canvas);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
        calculateFidelity();
        redraw();
        scalingConst = (model.maxlat - model.minlat) / canvas.getHeight();
        System.out.println(model.maxlat);
        pan(-0.56*model.minlon, model.maxlat);
        zoom(0, 0, canvas.getHeight() / (model.maxlat - model.minlat));
    }

    public void redraw() {
        gc.setTransform(new Affine());
        gc.setFill(Color.WHITE); // Hvidt canvas
        //gc.setFill(Color.web("#6e8dff")); Blåt canvas
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setTransform(trans);
        gc.setLineWidth(1.5 * 1/Math.sqrt(trans.determinant()));
        for (var edge : model.edges) {
            edge.draw(gc);
        }
//        System.out.println(trans.getTy() / scalingConst / canvas.getHeight() / 3.14159265);
        for (var way : model.ways) {
            // I have no clue why this works but it does
            if (way.coords[1] > ((trans.getTy() * scalingConst * canvas.getHeight() * 3.14159265) - 0.6)*-1 && way.coords[0] > (((trans.getTx() * scalingConst * canvas.getHeight() * 3.14159265)+0.1)*-1)) {
            }
                way.draw(gc, defaultColorMap, fidelity);
                gc.setStroke(Color.web("#000000"));
            }
        }
    }

    private void calculateFidelity() {
        double zoomLevel = trans.getMxx();
        fidelity = 0;
        if (zoomLevel > 2000) {
            fidelity = 1;
        }
        if (zoomLevel > 3200) {
            fidelity = 2;
        }
        if (zoomLevel > 15000) {
            fidelity = 3;
        }
        if (zoomLevel > 100000) {
            fidelity = 4;
        }
    }

    public void pan(double dx, double dy) {

        trans.prependTranslation(dx, dy);
//        System.out.println((((trans.getTx() * scalingConst * canvas.getHeight() * 3.14159265) - 0.6)*-1) + " : " + ((trans.getTy() * scalingConst * canvas.getHeight() * 3.14159265) - 0.6)*-1);
//        System.out.println(trans.getTx() + " 2:2 " + trans.getTy());
//        System.out.println(model.minlon + " 3:3 " + model.maxlat);
        redraw();
    }

    public void zoom(double dx, double dy, double factor) {
        pan(-dx, -dy);
        trans.prependScale(factor, factor);
        scalingConst /= factor;
        pan(dx, dy);
        calculateFidelity();
        redraw();
    }

    public Point2D mousetoModel(double lastX, double lastY) {
        try {
            return trans.inverseTransform(lastX, lastY);
        } catch (NonInvertibleTransformException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    public void createColorMap() {
        defaultColorMap = new HashMap<>();
        defaultColorMap.put(DrawType.motorway, Color.web("#EA90A1")); // pink-ish red
        defaultColorMap.put(DrawType.trunk, Color.web("#FCC0AC")); // salmon
        defaultColorMap.put(DrawType.coastline, Color.web("#000000")); //Black
        defaultColorMap.put(DrawType.landuseFarmland, Color.web("#abb895")); // Brownish Green?
        defaultColorMap.put(DrawType.landuseResidential, Color.web("#cecfcc")); // Light grey

        defaultColorMap.put(DrawType.primary, Color.web("#FDD7A1")); // light orange
        defaultColorMap.put(DrawType.secondary, Color.web("#F6FABB")); // light green
        defaultColorMap.put(DrawType.tertiary, Color.GREEN); // Green

        defaultColorMap.put(DrawType.unclassified, Color.RED); // Red
        defaultColorMap.put(DrawType.residential, Color.LAVENDER); // Lavender?

        defaultColorMap.put(DrawType.livingStreet, Color.web("#EDEDED")); // Blue-ish-white
        defaultColorMap.put(DrawType.service,Color.DARKMAGENTA); // Dark magenta

        defaultColorMap.put(DrawType.building,Color.web("#858585")); // grey
        /*/
        defaultColorMap = new HashMap<>();
        defaultColorMap.put(DrawType.motorway, Color.web("#EA90A1")); // pink-ish red
        defaultColorMap.put(DrawType.trunk, Color.web("#FCC0AC")); // salmon
        defaultColorMap.put(DrawType.coastline, Color.web("#000000")); //Black
        defaultColorMap.put(DrawType.primary, Color.web("#FDD7A1")); // light orange
        defaultColorMap.put(DrawType.secondary, Color.web("#F6FABB")); // light green
        defaultColorMap.put(DrawType.tertiary, Color.web("#FEFEFE")); // White
        defaultColorMap.put(DrawType.unclassified, Color.web("#FEFEFE")); // White
        defaultColorMap.put(DrawType.residential, Color.web("#FEFEFE")); // White
        defaultColorMap.put(DrawType.livingStreet, Color.web("#EDEDED")); // Blue-ish-white
        defaultColorMap.put(DrawType.service,Color.web("#ffffff")); // white
        defaultColorMap.put(DrawType.building,Color.web("#858585")); // grey
        //*/

    }
}