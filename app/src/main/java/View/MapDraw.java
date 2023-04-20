package View;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Benchmark.TaskTimer;
import KDTree.KDTree;
import KDTree.Quickselect;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import javafx.scene.text.Text;

import Model.Model;
import Model.DrawType;
import Model.Way;
import Benchmark.FrameRateTimer;

public class MapDraw {
    private Canvas canvas;
    private GraphicsContext gc;

    private double scalingConst;

    private Affine trans;

    private Model model;
    private Way[] waysArr;
    private HashMap<DrawType, Color> defaultColorMap;
    private int fidelity;

    private boolean darkMode, debugMode;

    private final double defaultCutoffFactor;
    private  double debugCutoffFactor;
    private double cutoffFactor; // value can be: 0 - 0.5

    private Text debugText;

    Comparator<Way> cmp = new Comparator<Way>() {
        public int compare(Way o1, Way o2) {
            if (o1.coords.length < o2.coords.length){
                return 1;
            }
            if (o1.coords.length > o2.coords.length){
                return -1;
            }
            return 0;
        }
    };

    public MapDraw(Model model, Canvas canvas) {
        this.model = model;
        this.waysArr = model.ways.toArray(new Way[0]);
//        this.waysArr = model.ways;
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
        Collections.sort(model.ways, cmp);
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

        for (var way : model.ways) {
            int mid = way.coords.length>>1;
            int trueMid = mid%2==0?mid:mid-1;
            double lon = way.coords[trueMid];
            double lat = way.coords[trueMid+1];
            boolean inHorizontal = left < lon && lon < right;
            boolean inVertical = up < lat && lat < down;

            if (inHorizontal && inVertical) {
                way.draw(gc, defaultColorMap, fidelity);
            }

        }

        Way current = model.kdTree.root;

        traverse(current, left, right, up, down);
        FrameRateTimer.endFrame();
    }

    private Way traverse(Way current, double left, double right, double up, double down) {
        boolean inRange;
        boolean tooLeft;
        gc.setLineWidth(1/Math.sqrt(trans.determinant()));
        gc.setStroke(Color.web("#6e8dff"));
        gc.beginPath();
        if (current == null) {
            return null;
        }

        if (current.depth%2==0) {
            inRange = left < current.xVal && current.xVal < right;
            tooLeft = current.xVal < left;
            if (current.depth < 10){
                gc.moveTo(current.xVal, up);
                gc.lineTo(current.xVal, down);
                gc.stroke();
            }
        } else {
            inRange = up < current.yVal && current.yVal < down;
            tooLeft = current.yVal < up;
            if (current.depth < 10){
                gc.moveTo(left,current.yVal);
                gc.lineTo(right,current.yVal);
                gc.stroke();
            }
        }
        if (inRange) {
            current.draw(gc, defaultColorMap, fidelity);
            traverse(current.left, left, right, up, down);
            traverse(current.right, left, right, up, down);
            return current;
        }
        if (tooLeft) {
            return traverse(current.right, left, right, up, down);
        }
        return traverse(current.left, left, right, up, down);
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
        double newCanvasHeight = canvas.getHeight() + delta;
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
