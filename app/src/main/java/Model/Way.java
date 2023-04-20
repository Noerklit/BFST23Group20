package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Way implements Serializable, Comparable<Way> {
    public double[] coords;
    DrawType drawType;

    Long id;
    Long firstNodeId;
    Long lastNodeId;
    ArrayList<Node> way;

    public Way(ArrayList<Node> way, DrawType drawType, Long id) {
        coords = new double[way.size() * 2];
        for (int i = 0; i < way.size(); ++i) {
            var node = way.get(i);
            coords[2 * i] = 0.56 * node.lon;
            coords[2 * i + 1] = -node.lat;
        }
        this.drawType = drawType;
        this.id = id;
        if (way.size()>0){
            firstNodeId = way.get(0).getId();
            lastNodeId = way.get(way.size()-1).getId();
        }

        this.way = new ArrayList<>(way);
    }

    public void addNodes(ArrayList<Node> test, ArrayList<Node> toAdd) {
        way = new ArrayList<>(toAdd);

        for (Node e : test) {
            way.add(e);
        }
        firstNodeId = way.get(0).getId();
        lastNodeId = way.get(way.size()-1).getId();
    }
    
    public ArrayList<Node> getNodeList() {
        return way;
    }

    public long getFirstNodeId() {
        return firstNodeId;
    }

    public long getLastNodeId() {
        return lastNodeId;
    }


    public void draw(GraphicsContext gc, HashMap<DrawType, Color> colorMap, int fidelity) {
        if (drawType == null) {
            //System.out.println("HIT ME");
            //drawType = DrawType.coastline;
        }

        Color color = colorMap.get(drawType);

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
}
