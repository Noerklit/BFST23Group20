package KDTree;

import Model.Model;
import Model.Way;
import View.MapDraw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.lang.Math;

// import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

public class KDTree implements Serializable {

    /// Needs to be public because of unit tests
    public Way root;
    public int cnt = 0;

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
    public void drawTree(double left, double right, double up, double down,int fidelity){
        drawTree(root, left, right, up, down, fidelity);
    }

    private void drawTree(
            Way current,
            double left,
            double right,
            double up,
            double down,
            int fidelity)
    {
        boolean inRange;
        boolean tooSmall;
        if (current == null) {
            return;
        }

        if (current.depth%2==0) {
            inRange = left < current.xVal && current.xVal < right;
            tooSmall = current.xVal < left;
        } else {
            inRange = up < current.yVal && current.yVal < down;
            tooSmall = current.yVal < up;
        }
        if (inRange) {
            current.draw();
            drawTree(current.left, left, right, up, down, fidelity);
            drawTree(current.right, left, right, up, down, fidelity);
            return;
        }
        if (tooSmall) {
            drawTree(current.right, left, right, up, down, fidelity);
        } else {
            drawTree(current.left, left, right, up, down, fidelity);
        }
    }

    public EdgeWeightedGraph traverse(){
        EdgeWeightedGraph graph = new EdgeWeightedGraph(7000);
        traverse(root, graph);
        return graph;
    }

    private void traverse(Way curr, EdgeWeightedGraph graph){
        if (curr == null)
            return;
      
        for (int i = 0; i < curr.coords.length/4; i+=2) {
            try {
                float source_x = curr.coords[i];
                float source_y = curr.coords[i+1];
                float dest_x = curr.coords[i+2];
                float dest_y = curr.coords[i+3];

                Long id_from = curr.ids[i];
                Long id_to = curr.ids[i+2];

                double delta_x = dest_x-source_x;
                double delta_y = dest_y-source_y;

                double weight = Math.sqrt(delta_x * delta_x + delta_y * delta_y);

                int from = Math.abs(id_from.intValue()) % 6999;
                int dest = Math.abs(id_to.intValue()) % 6999;
            
                System.out.println(from + " " + dest);

                graph.addEdge(new Edge(from, dest, weight));
                cnt++;
            } catch (Exception e) {
                System.out.println("Got exception");
            }
        }

        traverse(curr.left, graph);
        traverse(curr.right, graph);
    }
}