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
            System.out.println("Size of waysToPutTogether :" + waysToPutTogether.size());
            for (Way wayToMerge : waysToPutTogether) {
                System.out.println(way != wayToMerge);
                if (way != wayToMerge) {
                    System.out.println("firstWayFirstNodeId : " + way.nodeIds[0] + " firstWayLastNodeId : " + way.lastNodeId);
                    System.out.println("wayToMergeFirstNodeId : " + wayToMerge.nodeIds[0] + " wayToMergeLastNodeId : " + wayToMerge.lastNodeId);
                    System.out.println("Size of wayNodeList : " + way.getNodeList().size() + " Size of wayToMergeNodeList : "+ wayToMerge.getNodeList().size());
                    if (way.lastNodeId == wayToMerge.nodeIds[0]) {
                        System.out.println("Hit");
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                        System.out.println(toAdd.getNodeList().size());
                    }
                    else if (way.nodeIds[0] == wayToMerge.nodeIds[0]) {
                        // Reverse first
                        System.out.println("Hit1");
                        Collections.reverse(way.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                    }
                    else if (way.lastNodeId == wayToMerge.lastNodeId) {
                        //Reverse wayToMerge, do the same as one
                        System.out.println("Hit2");
                        Collections.reverse(wayToMerge.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                        System.out.println(toAdd.getNodeList().size());
                    }
                    else if (way.nodeIds[0] == wayToMerge.lastNodeId) {
                        //Reverse both
                        System.out.println("Hit3");
                        Collections.reverse(way.getNodeList());
                        Collections.reverse(wayToMerge.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                    }

                    if (toAdd.nodeIds[0] == toAdd.lastNodeId) {
                        System.out.println("This is a closedWay now: FirstNodeId " + toAdd.nodeIds[0] + " LastNodeId " + toAdd.nodeIds[toAdd.nodeIds.length-1]);
                        System.out.println("Size of toAdd :" + toAdd.getNodeList().size());
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
