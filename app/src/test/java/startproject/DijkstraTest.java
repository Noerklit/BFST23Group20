package startproject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;

import Pathfinding.Dijkstra;

import edu.princeton.cs.algs4.DijkstraUndirectedSP;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;


public class DijkstraTest {
    static HashMap<Long, Integer> nodeIdToIntegerId = new HashMap<>();
    DijkstraUndirectedSP dijkstraShortestPath;
    EdgeWeightedGraph graph;
    Long start;
    Long end;
    double distance;
    Edge edge0_1;
    Edge edge1_2;
    Edge edge2_3;
    Edge edge3_4;
    Edge edge4_5;
    Edge edge5_6;
    Edge edge6_7;
    Edge edge0_7;
    Edge edge1_7;
    Edge edge7_8;
    Edge edge2_8;
    Edge edge6_8;
    Edge edge2_5;
    Edge edge3_5;


    @BeforeEach
    public void makeTestEdgeWeightedGraph() {
        graph = new EdgeWeightedGraph(9);
        edge0_1 = new Edge(0, 1, 4);
        graph.addEdge(edge0_1);
        edge1_2 = new Edge(1, 2, 8);
        graph.addEdge(edge1_2);
        edge2_3 = new Edge(2, 3, 7);
        graph.addEdge(edge2_3);
        edge3_4 = new Edge(3, 4, 9);
        graph.addEdge(edge3_4);
        edge4_5 = new Edge(4, 5, 10);
        graph.addEdge(edge4_5);
        edge5_6 = new Edge(5, 6, 2);
        graph.addEdge(edge5_6);
        edge6_7 = new Edge(6,7,1);
        graph.addEdge(edge6_7);
        edge0_7 = new Edge(0,7,8);
        graph.addEdge(edge0_7);
        edge1_7 = new Edge(1,7,11);
        graph.addEdge(edge1_7);
        edge7_8 = new Edge(7,8,7);
        graph.addEdge(edge7_8);
        edge2_8 = new Edge(2,8,2);
        graph.addEdge(edge2_8);
        edge6_8 = new Edge(6,8,6);
        graph.addEdge(edge6_8);
        edge2_5 = new Edge(2,5,4);
        graph.addEdge(edge2_5);
        edge3_5 = new Edge(3,5,14);
        graph.addEdge(edge3_5);
         
        for (int i = 0; i <= 8; i++) {
            nodeIdToIntegerId.put((long) i, i);
        }
    }
   

    @Test
    public void testDijkstraShortestPath() {
        double totalWeightOfSTP = 0.0;
        Iterable<Edge> edgesInShortestPath = RunDijkstra(0, 3);
        for (Edge edge : edgesInShortestPath) {
            totalWeightOfSTP += edge.weight();
        }
        assertEquals(19.0,totalWeightOfSTP);
    }
    @Test
    public void testAnotherDijkstraShortestPath() {
        double totalWeightOfSTP = 0.0;
        Iterable<Edge> edgesInShortestPath = RunDijkstra(0, 4);
        for (Edge edge : edgesInShortestPath) {
            totalWeightOfSTP += edge.weight();
        }
        assertEquals(21.0,totalWeightOfSTP);
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

}
