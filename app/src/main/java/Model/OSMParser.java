package Model;

import java.io.InputStream;

import java.util.*;

import javax.xml.stream.XMLStreamException;

import KDTree.KDTree;
import org.codehaus.stax2.*;

import Benchmark.TaskTimer;

public class OSMParser {
    Comparator<Way> cmp = new Comparator<Way>() {
        public int compare(Way o1, Way o2) {
            return Double.compare(o1.xVal, o2.xVal) *-1;
        }
    };
    private List<Way> ways = new ArrayList<Way>();

    private double minlat, maxlat, minlon, maxlon;

    private ArrayList<Node> allNodes;
    private ArrayList<Way> allWays;
    public KDTree wayKDTree;
    public KDTree backgroundKdtree;

    public OSMParser(InputStream inputStream) {
        wayKDTree = new KDTree();
        backgroundKdtree = new KDTree();
        mainOSMParsing(inputStream);
    }

    public List<Way> getWays() {
//        ways.sort(cmp);
        return ways;
    }

    public double getMinlat() {
        return minlat;
    }
    public double getMaxlat() {
        return maxlat;
    }
    public double getMinlon() {
        return minlon;
    }
    public double getMaxlon() {
        return maxlon;
    }

    private void mainOSMParsing(InputStream inputStream) {
        TaskTimer.recordTask("Begin OSM parsing");
        
        try {
            var input = getXMLReader(inputStream);

            initialParsing(input);
            parseBounds(input);

            TaskTimer.recordTask("Nodes");
            parseNodes(input);

            TaskTimer.recordTask("Ways");
            parseWays(input);

            TaskTimer.recordTask("Relations");
            parseRelations(input);
            
            TaskTimer.recordTask("Finalize parsing");

        } catch (XMLStreamException e) {
            throw new RuntimeException(e); // We should probably throw our own exception type here
        }
    }

    private XMLStreamReader2 getXMLReader(InputStream inputStream) throws XMLStreamException {
        var stax = (XMLInputFactory2)XMLInputFactory2.newInstance();
        stax.configureForSpeed();
        var input = (XMLStreamReader2)stax.createXMLStreamReader(inputStream);
        return input;
    }

    private void initialParsing(XMLStreamReader2 input) throws XMLStreamException {
        while (input.hasNext()) {
            if (input.hasName()) {
                var name = input.getLocalName();
                if (name == "bounds") return;
            }
            input.next();
        }
    }
    
    private void parseBounds(XMLStreamReader2 input) throws XMLStreamException {
        while (input.hasNext()) {
            if (input.isStartElement()) {
                var name = input.getLocalName();
                if (name == "bounds") {
                    minlat = queryDouble(input, "minlat");
                    maxlat = queryDouble(input, "maxlat");
                    minlon = queryDouble(input, "minlon");
                    maxlon = queryDouble(input, "maxlon");
                } else {
                    break;
                }
            }
            input.next();
        }
    }

    private void parseNodes(XMLStreamReader2 input) throws XMLStreamException {

        allNodes = new ArrayList<Node>();

        while (input.hasNext()) {
            if (input.isStartElement()) {
                var name = input.getLocalName();
                if (name == "node") {
                    TaskTimer.incrementSubtask();
                    var id = queryLong(input, "id");
                    var lat = queryDouble(input, "lat");
                    var lon = queryDouble(input, "lon");
                    allNodes.add(new Node(lat, lon, id));
                } else if (name == "tag") {
                    
                } else {
                    break;
                }
            }
            input.next();
        }

        TaskTimer.recordTask("Sort node array");
        Collections.sort(allNodes);
    }

    private void parseWays(XMLStreamReader2 input) throws XMLStreamException {

        allWays = new ArrayList<Way>();
        var nodesToWay = new ArrayList<Node>();

        var addWay = false;
        DrawType currentDrawType = null;
        Long wayId = null;

        while (input.hasNext()) {
            if (input.isStartElement()) {
                var name = input.getLocalName();
                if (name == "way") {
                    TaskTimer.incrementSubtask();
                    nodesToWay.clear();
                    addWay = false;
                    currentDrawType = null;

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
                } else {
                    break;
                }
            } else if (input.isEndElement()) {
                var name = input.getLocalName();
                if (name == "way") {

                    Way wayToAdd = new Way(nodesToWay, currentDrawType, wayId);

                    allWays.add(wayToAdd);

                    if (addWay) {
                        if (
                            (currentDrawType.toString().contains("landuse") ||
                            currentDrawType.toString().contains("natural") ||
                            currentDrawType.toString().contains("wet") ||
                            currentDrawType == DrawType.coastline) &&
                            !currentDrawType.toString().contains("building"))
                            backgroundKdtree.insert(wayToAdd);
                        else
                            wayKDTree.insert(wayToAdd);
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
                        var index = Collections.binarySearch(allWays, new Way(empty,null, ref));

                        if (index >= 0) {
                            var singleOuterWay = allWays.get(index);

                            //Some relations does not have drawtype (fx Coastline), this makes sure we dont draw realtion way where the ways have drawtypes
                            if (singleOuterWay.drawType == null){
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
                if  (name == "relation" && addRelation) {
                    for(Way way : outerWays){
                        way.drawType = currentDrawType;
                        backgroundKdtree.insert(way);
//                        ways.add(way);
                    }
                }
            }
            input.next();
        }
//        Collections.reverse(ways);
    }

    private String queryString(XMLStreamReader2 input, String localName) {
        return input.getAttributeValue(null, localName);
    }
    private double queryDouble(XMLStreamReader2 input, String localName) {
        return Double.parseDouble(queryString(input, localName));
    }
    private long queryLong(XMLStreamReader2 input, String localName) {
        return Long.parseLong(queryString(input, localName));
    }

    private DrawType keyValToDrawType(String key, String val) {

        switch (key) {
            case "building":
                return DrawType.building;
            case "highway":
                switch (val) {
                    case "motorway":
                        return DrawType.motorway;
                    case "trunk":
                        return DrawType.trunk;
                    case "primary":
                        return DrawType.primary;
                    case "secondary":
                        return DrawType.secondary;
                    case "tertiary":
                        return DrawType.tertiary;
                    case "unclassified":
                        return DrawType.unclassified;
                    case "residential":
                        return DrawType.residential;
                    case "living_street":
                        return DrawType.livingStreet;
                    case "service":
                        return DrawType.service;
                }
                break;
            case "landuse":
                switch(val) {
                    case "farmland":
                        return DrawType.landuseFarmland;
                    case "residential":
                        return DrawType.landuseResidential; //Currently residential roads, not residential landuse.
                    case "forest":
                        return DrawType.landuseForest;
                    case "meadow":
                        return DrawType.landuseMeadow;
                    case "sand":
                        return DrawType.landuseSand;
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
            case "natural":
                switch (val) {
                    case "wetland":
                        return DrawType.wetland;
                    case "heath":
                        return DrawType.naturalHeath;
                    case "water":
                        return DrawType.naturalWater;
                }
        }

        switch (val) {
            case "coastline":
                return DrawType.coastline;
        }

        return null;
    }
}
