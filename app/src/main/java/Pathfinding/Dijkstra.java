package Pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import edu.princeton.cs.algs4.DijkstraUndirectedSP;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.MinPQ;

public class Dijkstra {
    EdgeWeightedGraph graph;
    int start;
    int end;
    double weight;

    public Dijkstra(EdgeWeightedGraph graph, int start, int end, double weight) {
        // BuildEdgeWeightedGraph();
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.graph = new EdgeWeightedGraph(graph);
    }

    public Iterable<Edge> RunDijkstra(EdgeWeightedGraph graph, int start, int end, double weight) {
        DijkstraUndirectedSP test = new DijkstraUndirectedSP(graph, start);

        return test.pathTo(end);
    }

    public void DijkstraTest() {
        EdgeWeightedGraph test = new EdgeWeightedGraph(9);

        test.addEdge(new Edge(0, 1, 4));
        test.addEdge(new Edge(1, 2, 8));
        test.addEdge(new Edge(2, 3, 7));
        test.addEdge(new Edge(3, 4, 9));
        test.addEdge(new Edge(0, 7, 8));
        test.addEdge(new Edge(7, 8, 7));
        test.addEdge(new Edge(7, 1, 11));
        test.addEdge(new Edge(7, 6, 1));
        test.addEdge(new Edge(6, 8, 6));
        // test.addEdge(new Edge(5, 6, 2));
        test.addEdge(new Edge(6, 5, 2));
        test.addEdge(new Edge(8, 2, 2));
        test.addEdge(new Edge(2, 5, 4));
        test.addEdge(new Edge(5, 3, 14));
        test.addEdge(new Edge(5, 4, 10));

        RunDijkstra(test, 0, 4, 4);
    }
}