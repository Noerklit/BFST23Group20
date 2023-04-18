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


    public Way insert(Way way, boolean isX){
        if(root == null) {
            root = way;
            return root;
        }
        Way current = root;
        Way parent = null;
        int depth = 0;
        while(current != null){
            parent = current;
            if(isX){
                if(way.xVal < current.xVal) current = current.left;
                else current = current.right;
            } else {
                if(way.yVal < current.yVal) current = current.left;
                else current = current.right;
            }
            depth++;
        }
        if(isX){
            way.depth = depth;
            if(way.xVal < parent.xVal) parent.left = way;
            else parent.right = way;
        } else {
            way.depth = depth;
            if(way.yVal < parent.yVal) parent.left = way;
            else parent.right = way;
        }
        return root;
    }




}