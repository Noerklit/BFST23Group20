package View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import javafx.scene.text.Text;

import Model.Model;
import Model.DrawType;
import Model.Way;
import Pathfinding.Dijkstra;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import Benchmark.FrameRateTimer;

public class MapDraw {
    private Canvas canvas;
    public static GraphicsContext gc;
    private Dijkstra dijkstra;

    private double scalingConst;

    public Affine trans;
    private ArrayList<ArrayList<Float>> dijstraPathList;

    private Model model;
    public static HashMap<DrawType, Color> defaultColorMap;
    public static int fidelity;

    private boolean darkMode, debugMode, viewMode;

    private final double defaultCutoffFactor;
    private  double debugCutoffFactor;
    private double cutoffFactor; // value can be: 0 - 0.5

    private Text debugText;

    private Dijkstra dijkstraSPT;

    public MapDraw(Model model, Canvas canvas) {
        this.model = model;
        this.canvas = canvas;
        this.darkMode = false;
        this.debugMode = false;
        this.viewMode = false;
        this.dijstraPathList = new ArrayList<>();
        this.trans = new Affine();
        this.gc = canvas.getGraphicsContext2D();

        cutoffFactor = defaultCutoffFactor = 0;
        debugCutoffFactor = .1;

        createColorMap();

        calculateFidelity();
        pan(-0.56*model.minlon, model.maxlat);
        zoom(0, 0, canvas.getHeight() / (model.maxlat - model.minlat));
        scalingConst = (model.maxlat - model.minlat) / canvas.getHeight();
//        pan(-100,-100);
//        zoom(380, 340, 40);

        dijkstra = new Dijkstra(model.wayKDTree, "SPEED");
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
//        System.out.println(up);
        double left = getScreenLon(0 + cutoffFactor);
        double right = getScreenLon(1 - cutoffFactor);

        gc.setLineWidth(1.5 * 1/Math.sqrt(trans.determinant()));

        gc.setLineWidth(1/Math.sqrt(trans.determinant()));
        gc.beginPath();

        for (Way coast: model.coastlines)
            coast.draw();
        if(viewMode){
            model.wayKDTree.drawTree(left, right, up, down, fidelity);
        } else {
            model.backgroundKdtree.drawTree(left, right, up, down, fidelity);
            model.wayKDTree.drawTree(left, right, up, down, fidelity);
            model.buildingKdtree.drawTree(left, right, up, down, fidelity);
        }
        
        if (dijstraPathList != null && dijstraPathList.size()>0)
        {
            for (ArrayList<Float> sub : dijstraPathList){
                gc.setLineWidth(1.5 * 1/Math.sqrt(trans.determinant()));
                gc.beginPath();
                gc.moveTo(sub.get(0), sub.get(1));
                for (int i = 2; i<sub.size()-2; i+=2){
                    gc.lineTo(sub.get(i), sub.get(i+1));
                }
                gc.setStroke(Color.DARKMAGENTA);
                gc.stroke();
            }
        }
        //traverseDebug(rootWay, left, right, up, down, left, right, up, down, 100);
        
        FrameRateTimer.endFrame();
    }

    public void runDijsktra(long from, long to){
        var temp = dijkstra.getCoords(dijkstra.RunDijkstra(from, to));
        var tempList = new ArrayList<Float>();
        for (int i = 0; i<temp.size(); i++)
            tempList.add(temp.get(i));
        dijstraPathList.add(tempList);
        
        redraw();
    }


    private void traverseDebug(
            Way current,
            double left,
            double right,
            double up,
            double down,
            double leftDraw,
            double rightDraw,
            double upDraw,
            double downDraw,
            int depthDraw)
    {
        boolean inRange;
        boolean tooSmall;
        if (current == null) {
            return;
        }
        depthDraw = (current.getDrawType().fidelity+1)*2;
//        int currFidelity = 20;

        if (current.depth%2==0) {
            inRange = left < current.xVal && current.xVal < right;
            tooSmall = current.xVal < left;
            if (current.depth < depthDraw){
//                System.out.println("left: " + left + " right: " + right + " up: " + up + " Down: " + down);
//                System.out.println(current.getDrawType());
//                gc.stroke();
                gc.setStroke(Color.web("#ff0000"));
                gc.moveTo(current.xVal, upDraw);
                gc.lineTo(current.xVal, downDraw);
                gc.stroke();
            }
        } else {
            inRange = up < current.yVal && current.yVal < down;
            tooSmall = current.yVal > down;
            if (current.depth < depthDraw){
//                System.out.println("left: " + left + " right: " + right + " up: " + up + " Down: " + down);
//                System.out.println(current.getDrawType());
                gc.setStroke(Color.web("#ff0000"));
                gc.moveTo(leftDraw,current.yVal);
                gc.lineTo(rightDraw,current.yVal);
                gc.stroke();
            }
        }
        if (inRange) {
            traverseDebug(
                    current.left,
                    left,
                    right,
                    up,
                    down,
                    leftDraw,
                    current.xVal,
                    upDraw,
                    current.yVal,
                    depthDraw);
            traverseDebug(
                    current.right,
                    left,
                    right,
                    up,
                    down,
                    current.xVal,
                    rightDraw,
                    current.yVal,
                    downDraw, depthDraw);
            return;
        }
        if (tooSmall) {
            traverseDebug(
                    current.right,
                    left,
                    right,
                    up,
                    down,
                    current.xVal,
                    right,
                    current.yVal,
                    down, depthDraw);
        } else {
            traverseDebug(
                    current.left,
                    left,
                    right,
                    up,
                    down,
                    left,
                    current.xVal,
                    up,
                    current.yVal,
                    depthDraw);
        }
    }
    private int getTreeDepth(Way current, int largest) {
        if (current == null) {
            return largest;
        }
        if (current.depth > largest) {
            largest = current.depth;
        }
        largest = getTreeDepth(current.left, largest);
        largest = getTreeDepth(current.right, largest);
        return largest;
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
    public double convertMouseLon(double mouseCoord) {
        return (trans.getTx() * -1 - canvas.getWidth()+(canvas.getWidth() + mouseCoord))*scalingConst;
    }
    public double convertMouseLat(double mouseCoord) {
        return (trans.getTy() * -1 + canvas.getHeight() - (canvas.getHeight()- mouseCoord))*scalingConst;
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
        redraw();
    }

    public void setViewMode(boolean viewMode){
        this.viewMode = viewMode;
        redraw();
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
      
        defaultColorMap.put(DrawType.landuseFarmland, Color.web("#eef0d5")); // correct Beige/green
        defaultColorMap.put(DrawType.landuseResidential, Color.web("#dadada")); // correct Light grey
        defaultColorMap.put(DrawType.landuseForest,Color.web("9dca8a")); //correct forest green
        defaultColorMap.put(DrawType.wetland, Color.web("caf0d6")); //corret blå/grøn
        defaultColorMap.put(DrawType.landuseMeadow,Color.web("cdebb0")); //correct green
        defaultColorMap.put(DrawType.landuseFarmyard, Color.web("eacca4")); // correct between Salmon and Clay
        defaultColorMap.put(DrawType.landuseIndustrial, Color.web("e6d1e3")); // correct Light purple
        defaultColorMap.put(DrawType.landuseRecreationground, Color.web("dffce2")); // correct Turquoise green?
        defaultColorMap.put(DrawType.naturalHeath,Color.web("d6d99f")); // correct Light brown/green
        defaultColorMap.put(DrawType.naturalWater,Color.web("aad3df")); // correct Light blue
        defaultColorMap.put(DrawType.naturalSand,Color.web("f5e9c6")); //correct beige
        defaultColorMap.put(DrawType.leisurePark,Color.web("cdf7c9")); // correct light grass green


        defaultColorMap.put(DrawType.primary, Color.web("#FDD7A1")); // light orange
        defaultColorMap.put(DrawType.secondary, Color.web("#F6FABB")); // light green
        defaultColorMap.put(DrawType.tertiary, Color.GREEN); // Green

        defaultColorMap.put(DrawType.unclassified, Color.RED); // Red
        defaultColorMap.put(DrawType.residential, Color.LAVENDER); // Lavender?

        defaultColorMap.put(DrawType.living_street, Color.web("#EDEDED")); // Blue-ish-white
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
