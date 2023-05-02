package View;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import javafx.scene.text.Text;

import Model.Model;
import Model.Node;
import Model.DrawType;
import Benchmark.FrameRateTimer;

public class MapDraw {
    private Canvas canvas;
    private GraphicsContext gc;

    private double scalingConst;

    private Affine trans;

    private Model model;
    private HashMap<DrawType, Color> defaultColorMap;
    private int fidelity;

    private boolean darkMode, debugMode;

    private final double defaultCutoffFactor;
    private  double debugCutoffFactor;
    private double cutoffFactor; // value can be: 0 - 0.5

    private Text debugText;

    public MapDraw(Model model, Canvas canvas) {
        this.model = model;
        this.canvas = canvas;
        this.darkMode = false;
        this.debugMode = false;

        this.trans = new Affine();
        this.gc = canvas.getGraphicsContext2D();

        cutoffFactor = defaultCutoffFactor = 0;
        debugCutoffFactor = .1;

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

        if (debugMode) {
            cutoffFactor = debugCutoffFactor;
        } else {
            cutoffFactor = defaultCutoffFactor;
        }
        showCutoffFactor();

        if (debugMode) {
            drawDebugLines();
        }

        double up = getScreenLat(0 + cutoffFactor);
        double down = getScreenLat(1 - cutoffFactor);

        double left = getScreenLon(0 + cutoffFactor);
        double right = getScreenLon(1 - cutoffFactor);
        
        gc.setLineWidth(1.5 * 1/Math.sqrt(trans.determinant()));
        // System.out.println("Size of model.ClosedWaysToDraw " + model.closedWaysToDraw.size());
        // System.out.println(model.closedWaysToDraw.get(6).getNodeList().size());
        // for (var outerWay : model.closedWaysToDraw) {
        //     int mid = outerWay.coords.length/2;
        //     int trueMid = mid%2==0?mid:mid-1;
        //     double lon = outerWay.coords[trueMid];
        //     double lat = outerWay.coords[trueMid+1];
        //     boolean inHorizontal = left < lon && lon < right;
        //     boolean inVertical = up < lat && lat < down;

        //     if (inHorizontal && inVertical) {
        //         outerWay.draw(gc, defaultColorMap, fidelity);
        //     }
        // }

        for (var outerWay : model.closedWaysToDraw) {
            drawFromCoords(outerWay.getNodeList(),outerWay.getDrawType());
        }

        for (var way : model.ways) {
            int mid = way.coords.length/2;
            int trueMid = mid%2==0?mid:mid-1;
            double lon = way.coords[trueMid];
            double lat = way.coords[trueMid+1];
            boolean inHorizontal = left < lon && lon < right;
            boolean inVertical = up < lat && lat < down;

            if (inHorizontal && inVertical) {
                way.draw(gc, defaultColorMap, fidelity);
            }
        }

        FrameRateTimer.endFrame();
    }

    private void drawFromCoords (ArrayList<Node> way, DrawType drawType) {
        double [] coords = new double[way.size() * 2];
        for (int i = 0; i < way.size(); ++i) {
            var node = way.get(i);
            coords[2 * i] = 0.56 * node.lon;
            coords[2 * i + 1] = -node.lat;
        }
        gc.beginPath();
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2 ; i < coords.length ; i += 2) {       
            gc.lineTo(coords[i], coords[i+1]);
        }
        gc.setStroke(defaultColorMap.get(drawType));
        gc.stroke();
        gc.setFill(defaultColorMap.get(drawType));
        gc.fill();
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
        if (zoomLevel > 30000) {
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

    public void resizeCanvasWidth(double delta) {
        double newCanvasWidth = canvas.getWidth() + delta;
        canvas.setWidth(newCanvasWidth);
        pan(delta/2, 0);
        redraw();
    }
    public void resizeCanvasHeight(double delta) {
        double newCanvasHeight = canvas.getWidth() + delta;
        canvas.setHeight(newCanvasHeight);
        pan(0, delta/2);
        redraw();
    }
    
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void toggleDebug() {
        this.debugMode = !this.debugMode;
        redraw();
    }

    public void increaseDebugZone() {
        changeDebugZone(1);
    }
    public void decreaseDebugZone() {
        changeDebugZone(-1);
    }
    private void changeDebugZone(int factor) {
        if (!debugMode) return;

        debugCutoffFactor += .05 * factor;

        double minCutoffFactor = -.5;
        double maxCutoffFactor = .5;
        if (debugCutoffFactor < minCutoffFactor) debugCutoffFactor = minCutoffFactor;
        if (maxCutoffFactor < debugCutoffFactor) debugCutoffFactor = maxCutoffFactor;

        cutoffFactor = debugCutoffFactor;

        showCutoffFactor();

        redraw();
    }
    private void showCutoffFactor() {
        String output = String.format("Cutoff factor: %.2f", cutoffFactor);
        if (debugText != null) debugText.setText(output);
    }
    public void setDebugText(Text debugText) {
        this.debugText = debugText;
    }
    
    private void createColorMap() {
        defaultColorMap = new HashMap<>();
        defaultColorMap.put(DrawType.motorway, Color.web("#EA90A1")); // pink-ish red
        defaultColorMap.put(DrawType.trunk, Color.web("#FCC0AC")); // salmon
        defaultColorMap.put(DrawType.coastline, Color.web("#000000")); //Black
      
        defaultColorMap.put(DrawType.landuseFarmland, Color.web("#f0f4d4")); // Beige
        defaultColorMap.put(DrawType.landuseResidential, Color.web("#cecfcc")); // Light grey
        defaultColorMap.put(DrawType.landuseForest,Color.web("8cbc7c")); //forest green
        defaultColorMap.put(DrawType.wetland, Color.BLUE); //Grim blå, skal skiftes
        defaultColorMap.put(DrawType.landuseMeadow,Color.web("d0ecb4")); //Very light green
        defaultColorMap.put(DrawType.landuseSand,Color.web("f8ecc4")); // Yellow-orange
        defaultColorMap.put(DrawType.landuseSand, Color.web("fff4bc")); // Yellow
        defaultColorMap.put(DrawType.landuseFarmyard, Color.web("f0c49c")); // Mix between Salmon and Clay?
        defaultColorMap.put(DrawType.landuseIndustrial, Color.web("e8d4e4")); // Light purple
        defaultColorMap.put(DrawType.landuseRecreationground, Color.web("e0fce4")); // Turquoise green?
        defaultColorMap.put(DrawType.naturalHeath,Color.web("d8dc9c")); // Light brown
        defaultColorMap.put(DrawType.naturalWater,Color.web("3EBDDC")); // Light blue


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
