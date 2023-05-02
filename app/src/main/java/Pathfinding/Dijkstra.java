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
    HashMap<Long, Integer> map = new HashMap<>();
    HashMap<Integer, float[]> countToCoords = new HashMap<>();

    public Dijkstra(KDTree tree) {
        this.tree = tree;
        this.root = tree.root;

        prepareForSPT(root);
        buildTree(nodeCount);
    }

    public Iterable<Edge> RunDijkstra(long start, long end) {
        int startAsInteger = map.get(start);
        int endAsInteger = map.get(end);

        DijkstraUndirectedSP dijkstraShortestPath = new DijkstraUndirectedSP(graph, startAsInteger);

        if (dijkstraShortestPath.hasPathTo(endAsInteger))
            return dijkstraShortestPath.pathTo(endAsInteger);

        return null; 
    }

    private void buildTree(int treeSize) {
        graph = new EdgeWeightedGraph(treeSize);
        traverse(root);
    }

    public ArrayList<Float> getCoords(Iterable<Edge> pathToDraw) {
        ArrayList<Float> coordList = new ArrayList<>();
        for (Edge e : pathToDraw) {
            int src = e.either();
            int dest = e.other(src);

            float x1 = countToCoords.get(src)[0];
            float y1 = countToCoords.get(src)[1];
            coordList.add(x1);
            coordList.add(y1);

            float x2 = countToCoords.get(dest)[0];
            float y2 = countToCoords.get(dest)[1];
            coordList.add(x2);
            coordList.add(y2);
//            System.out.println(x1 + ", " + y1);
        }
        return coordList;
    }

    // public EdgeWeightedGraph traverse(){
    //     this.graph = new EdgeWeightedGraph(nodeCount);
    //     traverse(tree.root, this.graph);
    //     return this.graph;
    // }

    private void traverse(Way curr){
        if (curr == null)
            return;
     
        for (int i = 0; i < (curr.coords.length)-2; i+=2) {
            float x1 = curr.coords[i + 0];
            float y1 = curr.coords[i + 1];
            long id1 = curr.ids[i/2];

            float x2 = curr.coords[i + 2];
            float y2 = curr.coords[i + 3];
            long id2 = curr.ids[i/2 + 1];

            int id_src = map.get(id1);
            int id_dest = map.get(id2);

            if (!countToCoords.containsKey(id_src))
                countToCoords.put(id_src, new float[]{x1, y1});
            else if (!countToCoords.containsKey(id_dest))
                countToCoords.put(id_dest, new float[]{x2, y2});
          
            double weight = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            Edge edgeToAdd = new Edge(id_src, id_dest, weight);

            graph.addEdge(edgeToAdd);
        }

        traverse(curr.left);
        traverse(curr.right);
    }

    private void prepareForSPT(Way curr) {
        if (curr == null)
            return;
        
        for (int i = 0; i < curr.ids.length; i++) {
            long nodeid = curr.ids[i];

            // If we already have a unique identifier continue
            if (map.containsKey(nodeid)) {
                continue;
            } else {
                map.put(nodeid, nodeCount);
                nodeCount++;
            }
        }

        prepareForSPT(curr.left);
        prepareForSPT(curr.right);
    }
}