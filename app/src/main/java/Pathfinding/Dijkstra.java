package Pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.MinPQ;

public class Dijkstra {
    public Dijkstra(EdgeWeightedGraph graph, long start, long end, double weight) {
        // BuildEdgeWeightedGraph();
        int start_int = ((Long)(start % 300000)).intValue();
        int end_int = ((Long)(end % 300000)).intValue();
        RunDijkstra(graph, start_int, end_int, weight);
    }

    // public void BuildEdgeWeightedGraph() {

    //     return;
    // }

    public ArrayList<Edge> RunDijkstra(EdgeWeightedGraph graph, int start, int end, double weight) {
        // Create a priority queue to sort the minimal weights
        MinPQ<Edge> priorityQueue = new MinPQ<>();

        Edge startEdge = new Edge(start, start, weight);
        priorityQueue.insert(startEdge);

        // Create array dist that holds distance to certain vertices
        // Create map which holds the previous node, used to reach the current node
        double distanceTo[] = new double[graph.V()];
        Map<Edge, Edge> previous = new HashMap<>();

        // Fill the array with max value, except for the start
        Arrays.fill(distanceTo, Double.MAX_VALUE);
        distanceTo[start] = 0;

        // While there is a minimal weight in the priority queue
        while (!priorityQueue.isEmpty()) {
            Edge minimalEdge = priorityQueue.delMin();
            if (minimalEdge.either() == end) {
                break;
            }

            // Get the destination vertex, we will look from this. I.e. 0-5, the int returned
            // would in this case be 5, as the structure is FROM - TO
            int destinationVertex = minimalEdge.other(minimalEdge.either());

            // Find adjacents to this destination, this will be the new source
            for (Edge neighbourEdge : graph.adj(destinationVertex)) {
                int newDestination = neighbourEdge.other(destinationVertex);

                if (distanceTo[newDestination] > distanceTo[destinationVertex] + neighbourEdge.weight()) {
                    distanceTo[newDestination] = distanceTo[destinationVertex] + neighbourEdge.weight();
                    int newSource = neighbourEdge.either();

                    Edge toInsert = new Edge(newSource, newDestination, neighbourEdge.weight());

                    if (previous.containsKey(minimalEdge)) {
                        // If the minimal edge is already in our 'previous' hashmap, check if it's
                        // a better way from source to new destination, if it is, then we update our mapping
                        if (previous.get(minimalEdge).weight() > toInsert.weight() + distanceTo[newDestination]) {
                            previous.put(minimalEdge, toInsert);
                        }
                    } else {
                        previous.put(minimalEdge, toInsert);
                    }

                    priorityQueue.insert(toInsert);
                }
            }
        }
      
        // Create our path from start to end, based on our mapping of edge to edge
        ArrayList<Edge> returnEdges = new ArrayList<>();
        Edge tmp = startEdge;
        System.out.println("Size of our SPT " + previous.size());
        while (previous.containsKey(tmp)) {
            tmp = previous.get(tmp);
            System.out.println(tmp);
            returnEdges.add(tmp);
        }

        return returnEdges;
    }

    // public EdgeWeightedGraph DijkstraTest() {
    //     EdgeWeightedGraph test = new EdgeWeightedGraph(9);

    //     test.addEdge(new Edge(0, 1, 4));
    //     test.addEdge(new Edge(1, 2, 8));
    //     test.addEdge(new Edge(2, 3, 7));
    //     test.addEdge(new Edge(3, 4, 9));
    //     test.addEdge(new Edge(0, 7, 8));
    //     test.addEdge(new Edge(7, 8, 7));
    //     test.addEdge(new Edge(7, 1, 11));
    //     test.addEdge(new Edge(7, 6, 1));
    //     test.addEdge(new Edge(6, 8, 6));
    //     test.addEdge(new Edge(6, 5, 2));
    //     test.addEdge(new Edge(8, 2, 2));
    //     test.addEdge(new Edge(2, 5, 4));
    //     test.addEdge(new Edge(5, 3, 14));
    //     test.addEdge(new Edge(5, 4, 10));

    //     RunDijkstra(test, new Edge(0, 0, 0), 4);
    //     return test;
    // }
}