package KDTree;

import Model.Model;
import Model.Way;
import View.MapDraw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KDTree implements Serializable {

    /// Needs to be public because of unit tests
    public Way root;

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

    // public void traverse(){
    //     traverse(root);
    // }

    // private void traverse(Way curr){
    //     if (curr == null)
    //         return;
    //     
    //     // do something
    //     traverse(curr.left);
    //     traverse(curr.right);
    // }
}