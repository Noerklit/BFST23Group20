package Model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import Benchmark.TaskTimer;
import Parser.AddressSearch;
import KDTree.KDTree;

public class OSMParser {
    Comparator<Way> cmp = new Comparator<Way>() {
        public int compare(Way o1, Way o2) {
            return Float.compare(o1.xVal, o2.xVal) * -1;
        }
    };
    private List<Way> ways = new ArrayList<Way>();

    private float minlat, maxlat, minlon, maxlon;

    private ArrayList<Node> allNodes;
    private ArrayList<Way> allWays;
    public ArrayList<Way> coastlines;

    private ArrayList<Way> background = new ArrayList<>() , buildings = new ArrayList<>(), waysToAdd = new ArrayList<>();
    public KDTree wayKDTree, backgroundKdtree, buildingKdtree;

    public OSMParser(InputStream inputStream, AddressSearch addressSearch) {
        wayKDTree = new KDTree();
        backgroundKdtree = new KDTree();
        buildingKdtree = new KDTree();
        mainOSMParsing(inputStream, addressSearch);
    }

    public List<Way> getWays() {
        return ways;
    }

    public float getMinlat() {
        return minlat;
    }

    public float getMaxlat() {
        return maxlat;
    }

    public float getMinlon() {
        return minlon;
    }

    public float getMaxlon() {
        return maxlon;
    }

    private void mainOSMParsing(InputStream inputStream, AddressSearch addressSearch) {
        //System.out.println("løl");
        //System.exit(0);

        TemporaryAddressFile temporaryAddressFile = new TemporaryAddressFile();

        TaskTimer.recordTask("Begin OSM parsing");

        try {
            var input = getXMLReader(inputStream);

            initialParsing(input);
            parseBounds(input);

            TaskTimer.recordTask("Nodes");
            parseNodes(input, temporaryAddressFile);

            //*/
            TaskTimer.recordTask("Ways");
            parseWays(input);
            
            TaskTimer.recordTask("Relations");
            parseRelations(input);

            TaskTimer.recordTask("Finalize parsing");
            //*/
            
        } catch (XMLStreamException e) {
            throw new RuntimeException(e); // We should probably throw our own exception type here
        }

        allNodes = null;
        allWays = null;

        TaskTimer.recordTask("Add addresses to tries");

        temporaryAddressFile.openReadStream();

        while (temporaryAddressFile.hasAddress()) {
            String address = temporaryAddressFile.getAddress();
            String[] addressInfo = address.split(", ");
            addressSearch.addAddress(addressInfo[0], addressInfo[1]);
        }

        temporaryAddressFile.closeReadStream();
    }

    private XMLStreamReader2 getXMLReader(InputStream inputStream) throws XMLStreamException {
        var stax = (XMLInputFactory2) XMLInputFactory2.newInstance();
        stax.configureForSpeed();
        var input = (XMLStreamReader2) stax.createXMLStreamReader(inputStream);
        return input;
    }

    private void initialParsing(XMLStreamReader2 input) throws XMLStreamException {
        while (input.hasNext()) {
            if (input.hasName()) {
                var name = input.getLocalName();
                if (name == "bounds")
                    return;
            }
            input.next();
        }
    }

    private void parseBounds(XMLStreamReader2 input) throws XMLStreamException {
        while (input.hasNext()) {
            if (input.isStartElement()) {
                var name = input.getLocalName();
                if (name == "bounds") {
                    minlat = queryFloat(input, "minlat");
                    maxlat = queryFloat(input, "maxlat");
                    minlon = queryFloat(input, "minlon");
                    maxlon = queryFloat(input, "maxlon");
                } else {
                    break;
                }
            }
            input.next();
        }
    }

    private void parseNodes(XMLStreamReader2 input, TemporaryAddressFile temporaryAddressFile) throws XMLStreamException {
        
        allNodes = new ArrayList<Node>();
        Node currentNode = null;

        TemporaryAddress temporaryAddress = null;

        temporaryAddressFile.openStream();

        while (input.hasNext()) {
            if (input.isStartElement()) {
                var name = input.getLocalName();
                if (name == "node") {
                    TaskTimer.incrementSubtask();
                    var id = queryLong(input, "id");
                    var lat = queryFloat(input, "lat");
                    var lon = queryFloat(input, "lon");
                    currentNode = new Node(lat, lon, id);
                    // we need to test this makes it faster. Alternatively we can make a clear() method
                    temporaryAddress = null;
                    temporaryAddress = new TemporaryAddress();
                    allNodes.add(currentNode);
                } else if (name == "tag") {
                    String k = queryString(input, "k");
                    String v = queryString(input, "v");
                    if (k.startsWith("addr:")) {
                        setAddressInfo(temporaryAddress, k, v);
                    } else if (k.equals("osak:identifier")) {
                        temporaryAddressFile.writeToStream(temporaryAddress.get());
                    }
                } else {
                    break;
                }
            }
            input.next();
        }

        temporaryAddressFile.closeStream();

        TaskTimer.recordTask("Sort node array");
        Collections.sort(allNodes);
    }

    
    
    private void setAddressInfo(TemporaryAddress temporaryAddress, String k, String v) {
        
        if (k.equals("addr:postcode")) {
            temporaryAddress.setPostcode(v);
        }
        if (k.equals("addr:city")) {
            temporaryAddress.setCity(v);
        }
        if (k.equals("addr:housenumber")) {
            temporaryAddress.setHousenumber(v);
        }
        if (k.equals("addr:street")) {
            temporaryAddress.setStreet(v);
        }
    }

    private void parseWays(XMLStreamReader2 input) throws XMLStreamException {
        allWays = new ArrayList<Way>();
        coastlines = new ArrayList<>();
        var nodesToWay = new ArrayList<Node>();

//        var waysToAdd = new ArrayList<Way>();

        var addWay = false;
        DrawType currentDrawType = null;
        Long wayId = null;
        byte maxspeed = 0;

        while (input.hasNext()) {
            if (input.isStartElement()) {
                var name = input.getLocalName();
                if (name == "way") {
                    TaskTimer.incrementSubtask();
                    nodesToWay.clear();
                    addWay = false;
                    currentDrawType = null;
                    maxspeed = 80; // default danish speed limit

                    wayId = queryLong(input, "id");
                } else if (name == "nd") {
                    var ref = queryLong(input, "ref");
                    var index = Collections.binarySearch(allNodes, new Node(0, 0, ref));
                    var node = allNodes.get(index);
                    nodesToWay.add(node);
                } else if (name == "tag") {
                    var k = queryString(input, "k");
                    var v = queryString(input, "v");
                    if (currentDrawType == null)
                        currentDrawType = keyValToDrawType(k, v);
                    if (currentDrawType != null) {
                        addWay = true;
                    }
                    if (k.equals("maxspeed")) {
                        if (k.matches("[0-9]+")) {
                            maxspeed = (byte)Integer.parseInt(v);
                        }
                    }
                } else {
                    break;
                }
            } else if (input.isEndElement()) {
                var name = input.getLocalName();
                if (name == "way") {

                    Way wayToAdd = new Way(nodesToWay, currentDrawType, wayId);
                    wayToAdd.maxspeed = maxspeed;
                    allWays.add(wayToAdd);

                    if (addWay) {
                        try {
                            String drawtypeString = currentDrawType.toString().substring(0, 7);
                            if (drawtypeString.equals("coastli")) {
                                coastlines.add(wayToAdd);
                            } else if (drawtypeString.equals("buildin")
                                    || currentDrawType.toString().equals("naturalWater")) {
//                                buildingKdtree.insert(wayToAdd);
                                buildings.add(wayToAdd);
                            } else if ((drawtypeString.equals("landuse") ||
                                    drawtypeString.equals("natural") ||
                                    drawtypeString.equals("wetland") ||
                                    drawtypeString.equals("leisure") ||
                                    currentDrawType == DrawType.coastline) &&
                                    !drawtypeString.equals("buildin")) {
                                background.add(wayToAdd);
                            } else {
                                waysToAdd.add(wayToAdd);
                            }
                        } catch (Exception e) {
                            waysToAdd.add(wayToAdd);
                        }
                    }
                }
            }
            input.next();
        }

        TaskTimer.recordTask("Sort way array");
        Collections.sort(allWays);
    }

    private void parseRelations(XMLStreamReader2 input) throws XMLStreamException {

        List<Way> outerWays = new ArrayList<Way>();
        var temp = new ArrayList<Way>();

        var addRelation = false;
        DrawType currentDrawType = null;

        while (input.hasNext()) {
            if (input.isStartElement()) {
                var name = input.getLocalName();
                if (name == "relation") {
                    TaskTimer.incrementSubtask();
                    currentDrawType = null;
                    addRelation = false;
                    outerWays.clear();
                } else if (name == "member") {
                    var type = queryString(input, "type");
                    var ref = queryLong(input, "ref");
                    var role = queryString(input, "role");

                    ArrayList<Node> empty = new ArrayList<>();

                    if ((type.equals("way")) && (role.equals("outer"))) {
                        var index = Collections.binarySearch(allWays, new Way(empty, null, ref));

                        if (index >= 0) {
                            var singleOuterWay = allWays.get(index);

                            // Some relations does not have drawtype (fx Coastline), this makes sure we dont
                            // draw realtion way where the ways have drawtypes
                            if (singleOuterWay.drawType == null) {
                                outerWays.add(singleOuterWay);
                            }
                        }
                    }
                } else if (name == "tag") {
                    var k = queryString(input, "k");
                    var v = queryString(input, "v");
                    if (currentDrawType == null) {
                        currentDrawType = keyValToDrawType(k, v);
                    }
                    if (currentDrawType != null) {
                        addRelation = true;
                    }
                } else {
                    break;
                }
            } else if (input.isEndElement()) {
                var name = input.getLocalName();
                if (name == "relation" && addRelation) {
                    for (Way way : outerWays) {
                        way.drawType = currentDrawType;
//                            buildingKdtree.insert(way);
                        if (way.drawType == DrawType.building)
                            buildings.add(way);
                        else
                            background.add(way);
                    }
                }
            }
            input.next();
        }
        Runnable r1 = () -> {
            backgroundKdtree.createFromList(background);
        };
        Runnable r2 = () -> {
            buildingKdtree.createFromList(buildings);
        };
        Runnable r3 = () -> {
            wayKDTree.createFromList(waysToAdd);
        };
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        Thread t3 = new Thread(r3);
        t1.start();
        t2.start();
        t3.start();
    }

    private String queryString(XMLStreamReader2 input, String localName) {
        return input.getAttributeValue(null, localName);
    }

    private float queryFloat(XMLStreamReader2 input, String localName) {
        return Float.parseFloat(queryString(input, localName));
    }

    private int queryInt(XMLStreamReader2 input, String localName) {
        return Integer.parseInt(queryString(input, localName));
    }

    private long queryLong(XMLStreamReader2 input, String localName) {
        return Long.parseLong(queryString(input, localName));
    }

    private DrawType keyValToDrawType(String key, String val) {

        switch (key) {
            case "building":
                return DrawType.building;
            case "highway":
                try {
                    return DrawType.valueOf(val);
                } catch (Exception e) {
                    break;
                }
            case "landuse":
                switch (val) {
                    case "farmland":
                        return DrawType.landuseFarmland;
                    case "residential":
                        return DrawType.landuseResidential; // Currently residential roads, not residential landuse.
                    case "forest":
                        return DrawType.landuseForest;
                    case "meadow":
                        return DrawType.landuseMeadow;
                    case "beach":
                        return DrawType.landuseBeach;
                    case "farmyard":
                        return DrawType.landuseFarmyard;
                    case "industrial":
                        return DrawType.landuseIndustrial;
                    case "recreation_ground":
                        return DrawType.landuseRecreationground;
                }
                break;
            case "leisure":
                switch (val) {
                    case "park":
                        return DrawType.leisurePark;
                }
                break;
            case "natural":
                switch (val) {
                    case "wetland":
                        return DrawType.wetland;
                    case "heath":
                        return DrawType.naturalHeath;
                    case "water":
                        return DrawType.naturalWater;
                    case "sand":
                        return DrawType.naturalSand;
                }
        }

        switch (val) {
            case "coastline":
                return DrawType.coastline;
        }

        return null;
    }
}
