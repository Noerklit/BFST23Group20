package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Multipolygon {
    public static ArrayList<Way> outerRingAssignment(List<Way> outerWays) {
        ArrayList<Way> waysToPutTogether = new ArrayList<>();
        ArrayList<Way> closedWays = new ArrayList<>();
        // System.out.println(outerWays.size());
        int counter = 0;
        Way temporaryWay;
        System.out.println("Size of outerWays :" + outerWays.size());


        // DEBUG
        // if (outerWays.size() == 3) {
        //     for (Way e : outerWays) {
        //         System.out.println("This is one of the ways of 3: " + e.id);
        //     }
        // }


        for (Way way : outerWays) {
            //boolean xCoordEqual = way.coords[0] == way.coords[way.coords.length-2];
            //boolean yCoordEqual = way.coords[1] == way.coords[way.coords.length-1];
            if(way.getFirstNodeId() == way.getLastNodeId()) {
                closedWays.add(way);
            }
            else if(way.getFirstNodeId() != way.getLastNodeId()) {
                // DEBUG
                if (outerWays.size() == 3) {
                    for (Way e : outerWays) {
                        System.out.println("This is one of the ways of 3: " + e.id);
                        waysToPutTogether.add(e);
                    }
                }

                break;
            }
        }

        for (Way way : waysToPutTogether) {
            Way toAdd = new Way(way.getNodeList(), null, null);
            System.out.println("counter :" + counter);
            counter++;
            for (Way otherWay : waysToPutTogether) {
                if (way != otherWay) {
                    if (way.getLastNodeId() == otherWay.getFirstNodeId()) {
                        toAdd.addNodes(toAdd.getNodeList(), otherWay.getNodeList());
                    } else if (way.getLastNodeId() == otherWay.getFirstNodeId()) {
                        // reverse first
                        Collections.reverse(way.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), otherWay.getNodeList());
                    } else if (way.getLastNodeId() == otherWay.getLastNodeId()) {
                        // reverse otherway, do same as one
                        Collections.reverse(otherWay.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), otherWay.getNodeList());
                    } else if (way.getLastNodeId() == otherWay.getLastNodeId()) {
                        // reverse both
                        Collections.reverse(way.getNodeList());
                        Collections.reverse(otherWay.getNodeList());
                        toAdd.addNodes(toAdd.getNodeList(), otherWay.getNodeList());
                    }

                    /*
                     *
                     * 155 nodes - start        end
                     *          1827444000      1830746841
                     *
                     *
                     *
                     *
                    */
                    // System.out.println("------Before FLIP-------------");
                    // System.out.print(toAdd.getFirstNodeId() + " --- ");
                    // System.out.println(toAdd.getLastNodeId());
                    // System.out.println("------------------------");

                    // System.out.println("Now flipping...");

                    // System.out.println("-------After FLIP-----------------");
                    // System.out.print(toAdd.getFirstNodeId() + " --- ");
                    // System.out.println(toAdd.getLastNodeId());
                    // System.out.println("------------------------");

                    // System.out.println("\n\n\n");

                    if (toAdd.getFirstNodeId() == toAdd.getLastNodeId()) {
                        System.out.println("This is a closedWay now: " + toAdd.getFirstNodeId());
                        closedWays.add(toAdd);
                        toAdd = new Way(way.getNodeList(), null, null);

                        // waysToPutTogether.remove(way);
                    }
                }
            }
        }
        System.out.println("Size of closedWays: " + closedWays.size());

        return closedWays;
    }
}
