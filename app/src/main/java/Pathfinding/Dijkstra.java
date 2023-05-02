package Pathfinding;

import java.util.HashMap;
import java.util.ArrayList;

import edu.princeton.cs.algs4.DijkstraUndirectedSP;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

import KDTree.KDTree;
import Model.Way;

public class Dijkstra {
    EdgeWeightedGraph graph;
    KDTree tree;
    Way root;
    String filter;
    int nodeCount;
    double distance;

    long start;
    long end;

    static HashMap<Long, Integer> nodeIdToIntegerId = new HashMap<>();
    static HashMap<Integer, float[]> integerIdToCoords = new HashMap<>();

    public Dijkstra(KDTree tree, String filter) {
        this.tree = tree;
        this.root = tree.root;
        this.filter = filter;

        prepareForBuildGraph(root);
        buildGraph(nodeCount);
    }

    public Iterable<Edge> RunDijkstra(long start, long end) {
        this.start = start;
        this.end = end;

        int startAsInteger = nodeIdToIntegerId.get(start);
        int endAsInteger = nodeIdToIntegerId.get(end);
        distance = 0;

        DijkstraUndirectedSP dijkstraShortestPath = new DijkstraUndirectedSP(graph, startAsInteger);

        if (dijkstraShortestPath.hasPathTo(endAsInteger)) {
            Iterable<Edge> shortestPath = dijkstraShortestPath.pathTo(endAsInteger);
            for (Edge e : shortestPath) {
                distance += e.weight();
            }
            return shortestPath;
        }

        return null; 
    }

    private void buildGraph(int treeSize) {
        graph = new EdgeWeightedGraph(treeSize);
        buildEdgesFromWays(root);
    }

    public static float[] getCoordsFromNodeId(Long nodeId) {
        int integerId = nodeIdToIntegerId.get(nodeId);
        return integerIdToCoords.get(integerId);
    }

    public ArrayList<Float> getCoords(Iterable<Edge> pathToDraw) {
        ArrayList<Float> coordList = new ArrayList<>();
        float x1, y1 , x2, y2;
        int src, dest;

        src = nodeIdToIntegerId.get(start);

        for (Edge e : pathToDraw) {
            dest = e.other(src);

            x1 = integerIdToCoords.get(src)[0];
            y1 = integerIdToCoords.get(src)[1];
            coordList.add(x1);
            coordList.add(y1);

            x2 = integerIdToCoords.get(dest)[0];
            y2 = integerIdToCoords.get(dest)[1];
            coordList.add(x2);
            coordList.add(y2);

            src = dest;
        }
        return coordList;
    }

    private void buildEdgesFromWays(Way curr){
        if (curr == null)
            return;
     
        float x1, y1, x2, y2;
        long id1, id2;
        int id_src, id_dest;
        double distanceKm, weight = 0;

        for (int i = 0; i < (curr.coords.length)-2; i+=2) {
            x1 = curr.coords[i + 0];
            y1 = curr.coords[i + 1];
            id1 = curr.nodeIds[i/2];

            x2 = curr.coords[i + 2];
            y2 = curr.coords[i + 3];
            id2 = curr.nodeIds[i/2 + 1];

            id_src = nodeIdToIntegerId.get(id1);
            id_dest = nodeIdToIntegerId.get(id2);

            if (!integerIdToCoords.containsKey(id_src))
                integerIdToCoords.put(id_src, new float[]{x1, y1});
            else if (!integerIdToCoords.containsKey(id_dest))
                integerIdToCoords.put(id_dest, new float[]{x2, y2});
            
            distanceKm = Haversine.haversine(x1, y1, x2, y2);

            if (filter.equals("SPEED")) {
                // Will get time it takes to drive distance
                weight = distanceKm / curr.maxspeed;
            } else if (filter.equals("DISTANCE")) {
                weight = distanceKm;
            }

            Edge edgeToAdd = new Edge(id_src, id_dest, (float)weight);
            
            graph.addEdge(edgeToAdd);
        }

        buildEdgesFromWays(curr.left);
        buildEdgesFromWays(curr.right);
    }

    private void prepareForBuildGraph(Way curr) {
        if (curr == null)
            return;
        
        for (int i = 0; i < curr.nodeIds.length; i++) {
            long nodeid = curr.nodeIds[i];

            if (!nodeIdToIntegerId.containsKey(nodeid)) {
                nodeIdToIntegerId.put(nodeid, nodeCount);
                nodeCount++;
            }
        }

        prepareForBuildGraph(curr.left);
        prepareForBuildGraph(curr.right);
    }

    public double getDistance() {
        return distance;
    }
}