package Model;

public enum DrawType {
    building(4),
    motorway(2),
    defaultway(1),
    coastline(3);

    public int fidelity;
    private DrawType(int fidelity) {
        this.fidelity = fidelity;
    }
}
