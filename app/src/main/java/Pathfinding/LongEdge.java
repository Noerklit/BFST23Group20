package Pathfinding;

public class LongEdge implements Comparable<LongEdge> {

    private final Long v;
    private final Long w;
    private final double weight;

    public LongEdge(Long v, Long w, double weight) {
        if (v < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
        if (w < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    public double weight() {
        return weight;
    }

    public Long either() {
        return v;
    }

    public Long other(Long vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    @Override
    public int compareTo(LongEdge that) {
        return Double.compare(this.weight, that.weight);
    }

    public String toString() {
        return String.format("%f-%f %.5f", v, w, weight);
    }

    public static void main(String[] args) {
        LongEdge e = new LongEdge((long) 12, (long) 34, 5.67);
        System.out.println(e);
    }
}