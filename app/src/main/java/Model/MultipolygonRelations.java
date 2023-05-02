package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipolygonRelations {
    
    public static ArrayList<Way> mergingRelationWays(List<Way> outerWays) {
        ArrayList<Way> waysToPutTogether = new ArrayList<>();
        ArrayList<Way> closedWays = new ArrayList<>();
        Way temporaryWay;

        for (Way outerWay : outerWays) {
            if(outerWay.nodeIds[0] == outerWay.nodeIds[outerWay.nodeIds.length-1]) {
                closedWays.add(outerWay);
            }
            else {
                waysToPutTogether.add(outerWay);
            }
        }

        for (Way way : waysToPutTogether) {
            Way toAdd = new Way(way.getNodeList(), null, null);

            for (Way wayToMerge : waysToPutTogether) {
                if (way != wayToMerge) {
                    if (way.nodeIds[way.nodeIds.length-1] == wayToMerge.nodeIds[0]) {
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                    }
                    else if (way.nodeIds[way.nodeIds.length-1] == wayToMerge.nodeIds[0]) {
                        // Reverse first
                        Collections.reverse(way.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                    }
                    else if (way.nodeIds[way.nodeIds.length-1] == wayToMerge.nodeIds[wayToMerge.nodeIds.length-1]) {
                        //Reverse wayToMerge, do the same as one
                        Collections.reverse(wayToMerge.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                    }
                    else if (way.nodeIds[way.nodeIds.length-1] == wayToMerge.nodeIds[wayToMerge.nodeIds.length-1]) {
                        //Reverse both
                        Collections.reverse(way.getNodeList());
                        Collections.reverse(wayToMerge.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                    }

                    if (toAdd.nodeIds[0] == toAdd.nodeIds[toAdd.nodeIds.length-1]) {
                        closedWays.add(toAdd);
                        toAdd = new Way(way.getNodeList(), null, null);
                    }
                    else {
                        way = toAdd;
                    }
                }
            }
        }


        return closedWays;
    }
}
