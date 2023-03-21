package View;

import java.util.HashMap;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import Model.Model;
import Model.DrawType;
import Benchmark.FrameRateTimer;

public class MapDraw {
    private Canvas canvas;
    private GraphicsContext gc;

    private double scalingConst;

    private Affine trans = new Affine();

    private Model model;
    private HashMap<DrawType, Color> defaultColorMap;
    private int fidelity;

    private boolean darkMode, debugMode;

    private double cutoffFactor = .1; // value can be: 0 - 0.5

    public MapDraw(Model model, Canvas canvas) {
        this.model = model;
        this.canvas = canvas;
        this.darkMode = false;
        this.debugMode = false;

        this.gc = canvas.getGraphicsContext2D();

        createColorMap();
        
        calculateFidelity();
        System.out.println(model.maxlat);
        pan(-0.56*model.minlon, model.maxlat);
        zoom(0, 0, canvas.getHeight() / (model.maxlat - model.minlat));
        scalingConst = (model.maxlat - model.minlat) / canvas.getHeight();
        redraw();
    }

    public void redraw() {
        FrameRateTimer.startFrame(trans.getMxx(), fidelity);
        gc.setTransform(new Affine());
        if (darkMode){
            gc.setFill(Color.web("#4a4a4a"));
        } else {
            gc.setFill(Color.WHITE); // Hvidt canvas
        }
        //gc.setFill(Color.web("#6e8dff")); Blåt canvas
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        gc.setTransform(trans);

        if (debugMode){
            cutoffFactor = .1;
            drawDebugLines();
        } else {
            cutoffFactor = 0;
        }

        double up = getScreenLat(0 + cutoffFactor);
        double down = getScreenLat(1 - cutoffFactor);

        double left = getScreenLon(0 + cutoffFactor);
        double right = getScreenLon(1 - cutoffFactor);
        
        gc.setLineWidth(1.5 * 1/Math.sqrt(trans.determinant()));

        for (var edge : model.edges) {
            edge.draw(gc);
        }

        for (var way : model.ways) {
            boolean inHorizontal = left < way.coords[0] && way.coords[0] < right;
            boolean inVertical = up < way.coords[1] && way.coords[1] < down;

            if (inHorizontal && inVertical) {
                way.draw(gc, defaultColorMap, fidelity);
            }
        }
        FrameRateTimer.endFrame();
    }
    
    
    private void drawDebugLines() {
        gc.setLineWidth(10 * 1/Math.sqrt(trans.determinant()));
        gc.setStroke(Color.web("#6e8dff"));

        double up = getScreenLat(0 + cutoffFactor);
        double down = getScreenLat(1 - cutoffFactor);
        double left = getScreenLon(0 + cutoffFactor);
        double right = getScreenLon(1 - cutoffFactor);
        
        gc.beginPath();
        gc.moveTo(left, up);
        gc.lineTo(right, up);
        gc.lineTo(right, down);
        gc.lineTo(left, down);
        gc.lineTo(left, up);
        
        gc.stroke();
    }

    private double getScreenLon(double factor) {
        return (trans.getTx() * -1 + canvas.getWidth() * factor) * scalingConst;
    }
    private double getScreenLat(double factor) {
        return (trans.getTy() * -1 + canvas.getHeight() * factor) * scalingConst;
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
    }

    public void zoom(double dx, double dy, double factor) {
        pan(-dx, -dy);
        trans.prependScale(factor, factor);
        scalingConst /= factor;
        pan(dx, dy);
        calculateFidelity();
    }

    private void createColorMap() {
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

    public void toggleDarkMode(boolean toggle) {
        this.darkMode = toggle;
    }

    public void toggleDebug(){this.debugMode = !this.debugMode;}
}