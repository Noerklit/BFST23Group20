package KDTree;

import Model.Model;
import Model.Way;
import View.MapDraw;
import javafx.geometry.Point2D;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import Model.Way;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import javafx.scene.shape.Circle;

public class KDTree implements Serializable {

    /// Needs to be public because of unit tests
    public Way root;
    public int nodeCount = 0;
    public HashMap<Long, Integer> map = new HashMap<>();
    public HashMap<Integer, float[]> countToCoords = new HashMap<>();

    private int currFidelity=0;

    private ArrayList<Way> buffer = new ArrayList<>();
    public KDTree() {
    }

    //Sets the first incoming way to the root of the tree. Each incoming way is compared to variable current that moves
    // down the tree until it finds an empty node where the way is placed.
    /**
     *
     * @param way2Insert the way we want to insert in the KD-tree
     * @return the root of the tree
     */
    public Way insert(Way way2Insert){
        if(root == null) {
            way2Insert.depth = 0;
            root = way2Insert;
            // root.depth = 0;
            return root;
        }
        Way parent = null;
        Way current = root;
        int depth = 0;
        while(current != null){
            parent = current;
            if(depth%2==0){
                if(way2Insert.xVal < current.xVal) current = current.left;
                else current = current.right;
            } else {
                if(way2Insert.yVal < current.yVal) current = current.left;
                else current = current.right;
            }
            depth++;
        }
        if(depth%2!=0){
            way2Insert.depth = depth;
            if(way2Insert.xVal < parent.xVal) parent.left = way2Insert;
            else parent.right = way2Insert;
        } else {
            way2Insert.depth = depth;
            if(way2Insert.yVal < parent.yVal) parent.left = way2Insert;
            else parent.right = way2Insert;
        }
        return root;
    }

    /**
     * Starts the drawing of the entire kd-tree by calling drawTree
     * we dont want to give the tree it's own root as an argument
     * @param left left bound of screen
     * @param right right bound of screen
     * @param up lower bound of screen
     * @param down upper bound of screen
     * @param fidelity current fidelity level
     */
//    public void drawTree(double left, double right, double up, double down,int fidelity){
//        drawTree(root, left, right, up, down, fidelity);
//    }

    public void drawTree(
            double left,
            double right,
            double up,
            double down,
            int fidelity) {
        if (root == null) {
            return;
        }

        if (fidelity == currFidelity && buffer.size() > 0){
            for(Way way : buffer)
                way.draw();
            return;
        } else if (fidelity != currFidelity) {
            buffer.clear();
        }

        Stack<Way> stack = new Stack<>();
        stack.push(root);

        while (!stack.empty()) {
            Way current = stack.pop();

            boolean inRange;
            boolean inRangeX;
            boolean inRangeY;
            boolean tooSmall;

            if (current.depth % 2 == 0) {
                inRange = left < current.xVal && current.xVal < right;
                tooSmall = current.xVal < left;
            } else {
                inRange = up < current.yVal && current.yVal < down;
                tooSmall = current.yVal < down;
            }
            inRangeY = up < current.yVal && current.yVal < down;
            inRangeX = left < current.xVal && current.xVal < right;
            if (inRange) {
                if (inRangeY && inRangeX){
                    current.draw();
                    buffer.add(current);
                }
                if (current.right != null) {
                    stack.push(current.right);
                }
                if (current.left != null) {
                    stack.push(current.left);
                }
            } else if (tooSmall) {
                if (current.right != null) {
                    stack.push(current.right);
                }
            } else {
                if (current.left != null) {
                    stack.push(current.left);
                }
            }
        }
    }

    public long inArea(Way way, Point2D target){
        var dist = distTo(way, new Point2D(way.coords[0], way.coords[1]));
        Circle circle = new Circle(way.xVal, way.yVal,50);
        Point2D closest = new Point2D(way.xVal, way.yVal);
        return findNearby(circle, closest, target);
    }
    private long findNearby(Circle circle, Point2D closest, Point2D target){
        long id = 0;
        for (Way nearby: findNearby(root, circle, new ArrayList<Way>())){
            for (int i = 0; i<nearby.coords.length-2; i+=2) {
                var dist = new Point2D(nearby.coords[i], nearby.coords[i+1]).distance(target);
                if (dist < closest.distance(target)) {
                    closest = new Point2D(nearby.coords[i], nearby.coords[i+1]);
                    id = nearby.nodeIds[i>>1];
                }
            }
        }
        return id;
    }

    private List<Way> findNearby(
            Way current,
            Circle circle,
            List<Way> ways)
    {
        boolean inRange;
        boolean tooSmall;
        if (current == null) {
            return ways;
        }

        inRange = circle.contains(current.xVal,current.yVal);
        if (current.depth%2==0) {
            tooSmall = current.xVal < circle.getCenterX() - circle.getRadius();
        } else {
            tooSmall = current.yVal < circle.getCenterY()+circle.getRadius();
        }
        if (inRange) {
            ways.add(current);
            findNearby(current.left, circle, ways);
            findNearby(current.right, circle, ways);
            return ways;
        }
        if (tooSmall) {
            findNearby(current.right, circle, ways);
        } else {
            findNearby(current.left, circle, ways);
        }
        return ways;
    }
    public Way findWay(Point2D point2d){
        return findWay(root, root, point2d ,Double.MAX_VALUE);
    }

    /**
     * inspiration for this algorithm was found at
     * https://github.com/mgruben/Kd-Trees/blob/master/KdTree.java
     * @param curr
     * @param nearest
     * @param pointToFind
     * @param minDist
     * @return
     */
    private Way findWay(Way curr, Way nearest, Point2D pointToFind ,double minDist){
        if (curr == null)
            return nearest;

        if (curr.xVal == pointToFind.getX() && curr.yVal == pointToFind.getY())
            return curr;

        if (distTo(curr, pointToFind) < distTo(nearest, pointToFind))
            nearest = curr;

        double outOfBounds = curr.depth%2==0? pointToFind.getX() - curr.xVal: pointToFind.getY()-curr.yVal;

        if (outOfBounds<0){
            nearest = findWay(curr.left, nearest, pointToFind, minDist);

            if (distTo(nearest, pointToFind) >= Math.sqrt(Math.pow(outOfBounds, 2))){
                nearest = findWay(curr.right, nearest, pointToFind, minDist);
            }
        }
        else {
            nearest = findWay(curr.right, nearest, pointToFind, minDist);
            if (distTo(nearest, pointToFind) >= Math.sqrt(Math.pow(outOfBounds, 2))){
                nearest = findWay(curr.left, nearest, pointToFind, minDist);
            }
        }

        return nearest;
    }

    private Point2D findNearestCoordinates(Way way, Point2D nearestCoordinates, Point2D target){
        Point2D nearest = new Point2D(nearestCoordinates.getX(), nearestCoordinates.getY());
        for (int i = 0; i<way.coords.length-2; i+=2)
        {
            if(distToPoint(way.coords[i], way.coords[i+1], target)<distToPoint(nearest.getX(), nearest.getY(), target))
                nearest = new Point2D(way.coords[i], way.coords[i+1]);
        }
        return nearest;
    }

    private double distTo(Way way, Point2D point){
        return Math.sqrt(Math.pow(way.xVal - point.getX(),2) + Math.pow(way.yVal-point.getY(), 2));
    }

    private double distToPoint(double x, double y, Point2D point){
        return Math.sqrt(Math.pow(x - point.getX(),2) + Math.pow(y-point.getY(), 2));
    }


}