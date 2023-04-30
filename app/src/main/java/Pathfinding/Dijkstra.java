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

    public Edge[] RunDijkstra(EdgeWeightedGraph graph, int start, int end, double weight) {
        DijkstraUndirectedSP test = new DijkstraUndirectedSP(graph, start);

        System.out.println(test.hasPathTo(end));
        System.out.println(test.pathTo(end));
        // Create a priority queue to sort the minimal weights
        // MinPQ<Edge> priorityQueue = new MinPQ<>();

        // Edge startEdge = graph.adj(start).iterator().next();
        // priorityQueue.insert(startEdge);

        // // Create array dist that holds distance to certain vertices
        // // Create map which holds the previous node, used to reach the current node
        // double distanceTo[] = new double[graph.V()];
        // Edge edgeTo[] = new Edge[graph.V()];
        // // Map<Edge, Edge> previous = new HashMap<>();

        // // Fill the array with max value, except for the start
        // Arrays.fill(distanceTo, Double.POSITIVE_INFINITY);
        // distanceTo[start] = 0;

        // while (!priorityQueue.isEmpty()) {
        //     // System.out.println(priorityQueue);
        //     Edge minEdge = priorityQueue.delMin();
        //     // System.out.println("Min edge: " + minEdge);

        //     int left = minEdge.either();
        //     int right = minEdge.other(left);

        //     System.out.println("lr: " + left + " " + right);
        // //    System.exit(0);
        //     for (Edge neighbour : graph.adj(left)) {
        //         if (neighbour.either() != minEdge.either() || neighbour.other(neighbour.either()) != 
        //         minEdge.other(minEdge.either())) {
        //             System.out.println("Neighbour: " + neighbour + " other: " + minEdge);
        //             // Check connection between the left min edge (that is the right neighour)
        //             int neighbour_left = neighbour.either(); // other(right);

        //             System.out.println(distanceTo[neighbour_left] + " > " + distanceTo[left] + " + " + neighbour.weight());
        //             if ( distanceTo[neighbour_left] > distanceTo[left] + neighbour.weight()) {
        //                 distanceTo[neighbour_left] = distanceTo[left] + neighbour.weight();
        //                 System.out.println(neighbour_left + " " + neighbour.either());
        //                 Edge toInsert = new Edge(neighbour_left, neighbour.other(neighbour_left), distanceTo[neighbour_left]);
        //                 System.out.println(toInsert);
        //                 priorityQueue.insert(toInsert);
        //                 edgeTo[neighbour_left] = neighbour;
        //             }
        //         }
        //     }
        // }
        
        // for (int i = 0; i < distanceTo.length; i++) {
        //     System.out.println("Distance to: " + distanceTo[i]);
        // }

        // for (int i = 0; i < edgeTo.length; i++) {
        //     System.out.println("Edges to: " + edgeTo[i]);
        // }

        // return edgeTo;
        Edge[] a = new Edge[1];
        return a;
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