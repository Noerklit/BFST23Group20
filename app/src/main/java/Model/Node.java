package Model;

public class Node implements Comparable<Node> {
    float lat, lon;
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