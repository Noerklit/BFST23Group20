package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Multipolygon {
    public static ArrayList<Way> outerRingAssignment(List<Way> outerWays) {
        ArrayList<Way> waysToPutTogether = new ArrayList<>();
        ArrayList<Way> closedWays = new ArrayList<>();
        int counter = 0;
        Way temporaryWay;

        for (Way way : outerWays) {
            if(way.getFirstNodeId() == way.getLastNodeId()) {
                closedWays.add(way);
            }
            else if(way.getFirstNodeId() != way.getLastNodeId()) {
                    for (Way e : outerWays) {
                        waysToPutTogether.add(e);
                    }
                break;
            }
        }
            for (Way way : waysToPutTogether) {
                Way toAdd = new Way(way.getNodeList(), null, null);
                // System.out.println("Size of waysToPutTogether :" + waysToPutTogether.size());
                // System.out.println("counter :" + counter);
                // System.out.println(waysToPutTogether.get(0).getFirstNodeId() + " " + waysToPutTogether.get(0).getLastNodeId());
                counter++;
                for (Way wayToMerge : waysToPutTogether) {
                    // System.out.println("wayFirstNodeId :" + way.getFirstNodeId() + " WayLastNodeId :" + way.getLastNodeId());
                    // System.out.println("wayToMergeFirstNodeId :" + wayToMerge.getFirstNodeId() + " WayToMergeLastNodeId :" + wayToMerge.getLastNodeId());
                    // System.out.println(way != wayToMerge);
                    if (way != wayToMerge) {
                        if (way.getLastNodeId() == wayToMerge.getFirstNodeId()) {
                            toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                        } else if (way.getLastNodeId() == wayToMerge.getFirstNodeId()) {
                            // reverse first
                            Collections.reverse(way.getNodeList());
                            toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                        } else if (way.getLastNodeId() == wayToMerge.getLastNodeId()) {
                            // reverse otherway, do same as one
                            Collections.reverse(wayToMerge.getNodeList());
                            toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                        } else if (way.getLastNodeId() == wayToMerge.getLastNodeId()) {
                            // reverse both
                            Collections.reverse(way.getNodeList());
                            Collections.reverse(wayToMerge.getNodeList());
                            toAdd.addNodes(toAdd.getNodeList(), wayToMerge.getNodeList());
                        }
                        if (toAdd.getFirstNodeId() == toAdd.getLastNodeId()) {
                            // System.out.println("This is a closedWay now: FirstNodeId " + toAdd.getFirstNodeId() + " LastNodeId " + toAdd.getLastNodeId());
                            // System.out.println("Size of toAdd :" + toAdd.getNodeList().size());
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
