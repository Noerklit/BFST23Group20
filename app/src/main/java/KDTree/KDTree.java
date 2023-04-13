package KDTree;

import Model.Model;
import Model.Way;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KDTree {

    List<Way> xList = new ArrayList<>();
    List<Way> yList = new ArrayList<>();

    Way root;

    private final Comparator<Way> compareX = new Comparator<Way>(){
        public int compare(Way way1, Way way2) {
            return Double.compare(way1.xVal, way2.xVal);
        }
    };


    private final Comparator<Way> compareY = new Comparator<Way>(){
        public int compare(Way way1, Way way2) {
            return Double.compare(way1.yVal, way2.yVal);
        }
    };

    public KDTree(List<Way> ways) {
        buildTree();
    }

    private void buildTree(){

    }




}