package Model;

import java.io.Serializable;

public class Node implements Comparable<Node>,Serializable {
    public float lat;
    public float lon;
    Long id;

    public Node(float lat, float lon, Long id) {
        this.lat = lat;
        this.lon = lon;
        this.id = id;
    }
    @Override
    public int compareTo(Node otherNode){
        return Long.compare(id, otherNode.id);
    }
}