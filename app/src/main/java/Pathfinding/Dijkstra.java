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

    HashMap<Long, Integer> UniqueIdentifierMap = new HashMap<>();
    HashMap<Integer, float[]> uniqueIdToCoordinates = new HashMap<>();

    public Dijkstra(KDTree tree) {
        this.tree = tree;
        this.root = tree.root;

        prepareForSPT(root);
        buildTree(nodeCount);
    }

    public Iterable<Edge> runDijkstra(long start, long end) {
        int startAsInteger = UniqueIdentifierMap.get(start);
        int endAsInteger = UniqueIdentifierMap.get(end);

        DijkstraUndirectedSP dijkstraShortestPath = new DijkstraUndirectedSP(graph, startAsInteger);

        // If we have found a path return that, otherwise
        // we will return null
        if (dijkstraShortestPath.hasPathTo(endAsInteger))
            return dijkstraShortestPath.pathTo(endAsInteger);

        return null; 
    }

    private void buildTree(int treeSize) {
        graph = new EdgeWeightedGraph(treeSize);
        traverseAndBuildSPT(root);
    }

    private ArrayList<Float> getCoords(Iterable<Edge> pathToDraw) {
        ArrayList<Float> coordList = new ArrayList<>();
        for (Edge e : pathToDraw) {
            int src = e.either();
            int dest = e.other(src);

            float x1 = uniqueIdToCoordinates.get(src)[0];
            float y1 = uniqueIdToCoordinates.get(src)[1];
            coordList.add(x1);
            coordList.add(y1);

            float x2 = uniqueIdToCoordinates.get(dest)[0];
            float y2 = uniqueIdToCoordinates.get(dest)[1];
            coordList.add(x2);
            coordList.add(y2);
            System.out.println(x1 + ", " + y1);
        }
        return coordList;
    }

    private void traverseAndBuildSPT(Way currentWay){
        if (currentWay == null)
            return;
     
        float x1, y1, x2, y2;
        long id1, id2;
        int id_src;
        int id_dest;

        for (int i = 0; i < (currentWay.coords.length)-2; i+=2) {
            x1 = currentWay.coords[i + 0];
            y1 = currentWay.coords[i + 1];
            id1 = currentWay.ids[i/2];

            x2 = currentWay.coords[i + 2];
            y2 = currentWay.coords[i + 3];
            id2 = currentWay.ids[i/2 + 1];

            id_src = UniqueIdentifierMap.get(id1);
            id_dest = UniqueIdentifierMap.get(id2);

            if (!uniqueIdToCoordinates.containsKey(id_src))
                uniqueIdToCoordinates.put(id_src, new float[]{x1, y1});

            if (!uniqueIdToCoordinates.containsKey(id_dest))
                uniqueIdToCoordinates.put(id_dest, new float[]{x2, y2});
         
            double weight = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            Edge edgeToAdd = new Edge(id_src, id_dest, weight);

            graph.addEdge(edgeToAdd);
        }

        traverseAndBuildSPT(currentWay.left);
        traverseAndBuildSPT(currentWay.right);
    }

    private void prepareForSPT(Way currentWay) {
        if (currentWay == null)
            return;
        
        for (int i = 0; i < currentWay.ids.length; i++) {
            long nodeID = currentWay.ids[i];

            // If we already have a unique identifier for this node id continue
            if (UniqueIdentifierMap.containsKey(nodeID)) {
                continue;
            } else {
                UniqueIdentifierMap.put(nodeID, nodeCount);
                nodeCount++;
            }
        }

        prepareForSPT(currentWay.left);
        prepareForSPT(currentWay.right);
    }
}