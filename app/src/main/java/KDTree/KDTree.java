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

//    private final Comparator<Way> compareX = new Comparator<Way>(){
//        public int compare(Way way1, Way way2) {
//            return Double.compare(way1.xVal, way2.xVal);
//        }
//    };
//
//
//    private final Comparator<Way> compareY = new Comparator<Way>(){
//        public int compare(Way way1, Way way2) {
//            return Double.compare(way1.yVal, way2.yVal);
//        }
//    };

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
        while(current != null){
            parent = current;
            if(isX){
                if(way.xVal < current.xVal) current = current.left;
                else current = current.right;
            } else {
                if(way.yVal < current.yVal) current = current.left;
                else current = current.right;
            }
        }
        if(isX){
            if(way.xVal < parent.xVal) parent.left = way;
            else parent.right = way;
        } else {
            if(way.yVal < parent.yVal) parent.left = way;
            else parent.right = way;
        }
        return root;
    }




}