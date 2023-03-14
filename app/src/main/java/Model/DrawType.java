package Model;

public enum DrawType {
    motorway(0),
    trunk(0),
    coastline(0),
    landuseFarmland(0),
    landuseResidential(0),

    primary(1),
    secondary(1),
    tertiary(1),

    unclassified(2),
    residential(2),

    livingStreet(3),
    service(3),

    building(4),
    ;

    public int fidelity;
    private DrawType(int fidelity) {
        this.fidelity = fidelity;
    }
}
