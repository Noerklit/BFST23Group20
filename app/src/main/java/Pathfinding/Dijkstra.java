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
    int nodeCount;
    HashMap<Long, Integer> nodeIdToIntegerId = new HashMap<>();
    HashMap<Integer, float[]> integerIdToCoords = new HashMap<>();

    public Dijkstra(KDTree tree) {
        this.tree = tree;
        this.root = tree.root;

        prepareForBuildGraph(root);
        buildGraph(nodeCount);
    }

    public Iterable<Edge> RunDijkstra(long start, long end) {
        int startAsInteger = nodeIdToIntegerId.get(start);
        int endAsInteger = nodeIdToIntegerId.get(end);

        DijkstraUndirectedSP dijkstraShortestPath = new DijkstraUndirectedSP(graph, startAsInteger);

        if (dijkstraShortestPath.hasPathTo(endAsInteger))
            return dijkstraShortestPath.pathTo(endAsInteger);

        return null; 
    }

    private void buildGraph(int treeSize) {
        graph = new EdgeWeightedGraph(treeSize);
        buildEdgesFromWays(root);
    }

    public ArrayList<Float> getCoords(Iterable<Edge> pathToDraw) {
        ArrayList<Float> coordList = new ArrayList<>();
        for (Edge e : pathToDraw) {
            int src = e.either();
            int dest = e.other(src);

            float x1 = integerIdToCoords.get(src)[0];
            float y1 = integerIdToCoords.get(src)[1];
            coordList.add(x1);
            coordList.add(y1);

            float x2 = integerIdToCoords.get(dest)[0];
            float y2 = integerIdToCoords.get(dest)[1];
            coordList.add(x2);
            coordList.add(y2);
        }
        return coordList;
    }

    private void buildEdgesFromWays(Way curr){
        if (curr == null)
            return;
     
        for (int i = 0; i < (curr.coords.length)-2; i+=2) {
            float x1 = curr.coords[i + 0];
            float y1 = curr.coords[i + 1];
            long id1 = curr.nodeIds[i/2];

            float x2 = curr.coords[i + 2];
            float y2 = curr.coords[i + 3];
            long id2 = curr.nodeIds[i/2 + 1];

            int id_src = nodeIdToIntegerId.get(id1);
            int id_dest = nodeIdToIntegerId.get(id2);

            if (!integerIdToCoords.containsKey(id_src))
                integerIdToCoords.put(id_src, new float[]{x1, y1});
            else if (!integerIdToCoords.containsKey(id_dest))
                integerIdToCoords.put(id_dest, new float[]{x2, y2});
          
            double weight = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)); // @todo: haversine
            Edge edgeToAdd = new Edge(id_src, id_dest, weight);

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
}