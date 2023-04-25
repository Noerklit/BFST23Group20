package KDTree;

import Model.Model;
import Model.Way;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KDTree implements Serializable {

    public Way root;

    public KDTree() {
    }

//    private void buildTree(Way way, List<Way> ways, int from, int to, boolean isX){
//        if(from == to) return;
//        int mid = (from + to) >> 1;
//        if(isX) {
//            Collections.sort(ways, compareX);
//        } else {
//            Collections.sort(ways, compareY);
//        }
//        way = ways.get(mid);
//        way.left = ways.get(mid >> 1);
//        way.right = ways.get(mid + (mid >> 1));
//        buildTree(way.left, ways, from, mid, !isX);
//        buildTree(way.right, ways, mid + 1, to, !isX);
//    }


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
                if(way2Insert.yVal > current.yVal) current = current.left;
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
            if(way2Insert.yVal > parent.yVal) parent.left = way2Insert;
            else parent.right = way2Insert;
        }
        return root;
    }




}