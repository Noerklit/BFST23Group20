package Model;
import java.io.Serializable;

public class Node implements Comparable<Node>, Serializable {
    public double lat;
    public double lon;
    Long id;

    public Node(double lat, double lon, Long id) {
        this.lat = lat;
        this.lon = lon;
        this.id = id;
    }
    @Override
    public int compareTo(Node otherNode){
        return Long.compare(id, otherNode.id);
    }

    public long getId() {
        return id;
    }
}
