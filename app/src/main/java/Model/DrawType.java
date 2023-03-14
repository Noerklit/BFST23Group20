package Model;

public enum DrawType {
    motorway(0, false),
    trunk(0, false),
    coastline(0, false),

    primary(1, false),
    secondary(1, false),
    tertiary(1, false),

    unclassified(2, false),
    residential(2, false),

    livingStreet(3, false),
    service(3, false),

    building(4, true),
    ;

    public int fidelity;
    public boolean shouldFill;
    private DrawType(int fidelity, boolean shouldFill) {
        this.fidelity = fidelity;
        this.shouldFill = shouldFill;
    }
}
