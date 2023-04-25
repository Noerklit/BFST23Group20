package Pathfinding;

import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.MinPQ;

public class Dijkstra {
    public Dijkstra() {
        DijkstraTest();
    }

    public void DijkstraTest() {
        // Generate testcase from this
        // https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/

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
        test.addEdge(new Edge(6, 5, 2));
        test.addEdge(new Edge(8, 2, 2));
        test.addEdge(new Edge(2, 5, 4));
        test.addEdge(new Edge(5, 3, 14));
        test.addEdge(new Edge(5, 4, 10));

        MinPQ<Edge> priorityQueue = new MinPQ<>();


        // Only src parameter and weight matters
        Edge start = new Edge(0, 0 , 0);
        priorityQueue.insert(start); // new Edge(0, 0, 4));

        double dist[] = new double[test.V()];
        Arrays.fill(dist, Double.MAX_VALUE);
        dist[0] = 0;

        while (!priorityQueue.isEmpty()) {
            Edge min = priorityQueue.delMin();    
            int src = min.either();

            System.out.println(src);
            for (Edge v : test.adj(src)) {
                int current = v.other(src);
                if ( dist[current] > dist[src] + v.weight() ) {
                    dist[current] = dist[src] + v.weight();

                    Edge toInsert = new Edge(current, 0, v.weight());
                    priorityQueue.insert(toInsert);
                }
            }
        }

        System.out.println("Vertex Distance from Source");
        for (int i = 0; i < test.V(); i++) {
            System.out.println(i + "\t\t" + dist[i]);
        }
    }
}