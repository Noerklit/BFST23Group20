package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import View.MapDraw;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Way implements Serializable, Comparable<Way> {
    public float[] coords;
    public long[] nodeIds;
    DrawType drawType;
    ArrayList<Node> way;

    int trueMid;
    public float xVal;
    public float yVal;
    public byte maxspeed;

    public Long lastNodeId;
    public Long id;
    public Way left = null;
    public Way right = null;
    public int depth;

    public Way(ArrayList<Node> way, DrawType drawType, Long id) {
            coords = new float[way.size() * 2];
            nodeIds = new long[way.size()];

            for (int i = 0; i < way.size(); ++i) {
                var node = way.get(i);
                coords[2 * i] = (float)0.56 * node.lon;
                coords[2 * i + 1] = -node.lat;
                nodeIds[i] = node.id;
            }
            this.drawType = drawType;
            this.id = id;
            // right bitshift divides by 2
            var mid = coords.length>>1;
            trueMid = mid%2==0 ? mid : mid-1;
            if (way.size()>0){
                xVal = coords[trueMid];
                yVal = coords[trueMid+1];
            }
            this.way = new ArrayList<>(way);
            if(way.size()>0) {
                this.lastNodeId = way.get(way.size()-1).id;
            }
    }

    public void draw() {
        GraphicsContext gc = MapDraw.gc;
        Color color = MapDraw.defaultColorMap.get(drawType);
        int fidelity = MapDraw.fidelity;
        if(drawType.fidelity <= fidelity) {
            makePath(gc, fidelity);
            if(drawType.shouldFill) {
                makeFill(gc, color);
            } else {
                makeStroke(gc, color);
            }
        } 
    }

    private void makePath(GraphicsContext gc, int fidelity) {
        gc.beginPath();
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2 ; i < coords.length ; i += 2) {
            if (fidelity>0){
                gc.lineTo(coords[i], coords[i+1]);
            } else if((i/2)%16==0){
                gc.lineTo(coords[i], coords[i+1]);
            }
        }
    }
    
    private void makeStroke(GraphicsContext gc, Color color) {
        gc.setStroke(color);
        gc.stroke();
    }

    private void makeFill(GraphicsContext gc, Color color) {
        gc.setFill(color);
        gc.fill();
    }

    @Override
    public int compareTo(Way otherWay) {
        return Long.compare(id, otherWay.id);
    }

    public DrawType getDrawType() {
        return drawType;
    }

    public ArrayList<Node> getNodeList() {
        return way;
    }

    public void addNodes(ArrayList<Node> test, ArrayList<Node> toAdd) {
        way = new ArrayList<>(test);
        for (Node e : toAdd) {
            way.add(e);
        }
        System.out.println("How many nodes in way after adding : " + way.size());
        //Problem now is that the id of the last node is not updated anymore.... No lastNodeId felt I can change either...

        System.out.println("LastNodeId :" + lastNodeId);
        System.out.println("LastNodeId :" + lastNodeId);
        System.out.println("Done adding the ways together");
    }
}