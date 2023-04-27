package Model;

public enum DrawType {
    motorway(0, false),
    trunk(0, false),
    coastline(0, false),
    landuseFarmland(0,true),
    landuseResidential(0, true),
    landuseForest(0,true),
    wetland(0,true),
    landuseMeadow(0,true),
    landuseBeach(0,true),
    landuseFarmyard(0,true),
    landuseIndustrial(0,true),
    landuseRecreationground(0,true),
    naturalHeath(0,true),
    naturalWater(0,true),
    naturalSand(0,true),
    leisurePark(0,true),


    primary(1, false),
    secondary(1, false),
    tertiary(1, false),

    unclassified(2, false),
    residential(2, false),

    living_street(3, false),
    service(3, false),

    building(4, true),
    ;

    public final int fidelity;
    public final boolean shouldFill;
    private DrawType(int fidelity, boolean shouldFill) {
        this.fidelity = fidelity;
        this.shouldFill = shouldFill;
    }
}
