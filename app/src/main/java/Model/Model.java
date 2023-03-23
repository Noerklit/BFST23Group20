package Model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.*;

import org.checkerframework.checker.units.qual.K;

import javafx.geometry.Point2D;
import org.codehaus.stax2.evt.XMLEvent2;

import Benchmark.TaskTimer;

public class Model implements Serializable {
    public List<Edge> edges = new ArrayList<Edge>();
    public List<Way> ways = new ArrayList<Way>();

    private int fidelity = 0;

    public double minlat, maxlat, minlon, maxlon;

    public static Model load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException, XMLStreamException, FactoryConfigurationError {
        if (filename.endsWith(".obj")) {
            TaskTimer.recordTask("Load obj-file");
            try (var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                return (Model) in.readObject();
            }
        }
        return new Model(filename);
    }
    

    public Model(String filename) throws XMLStreamException, FactoryConfigurationError, IOException {
        if (filename.endsWith(".osm.zip")) {
            parseZIP(filename);
        } else if (filename.endsWith(".osm")) {
            parseOSM(filename);
        } else {
            throw new IOException("Could not find .osm.zip or .osm file");
        }
        save(filename+".obj");
    }

    void save(String filename) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    private void parseZIP(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        var input = new ZipInputStream(new FileInputStream(filename));
        input.getNextEntry();
        parseOSM(input);
    }

    private void parseOSM(String filename) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        parseOSM(new FileInputStream(filename));
    }

    private String parseAddress(String k, String v) {
        // Currently not used, only used for creating data address file

        if (k.contains("addr:country")) {
            System.out.print(v+", ");
        }
        if (k.contains("addr:municipality")) {
            System.out.print(v+", ");
        }
        if (k.contains("addr:place")) {
            System.out.print(v+", ");
        }
        if (k.contains("addr:postcode")) {
            System.out.print(v+", ");
        }
        if (k.contains("addr:city")) {
            System.out.print(v+", ");
        }
        if (k.contains("addr:housenumber")) {
            System.out.print(v+", ");
        }
        if (k.contains("addr:street")) {
            System.out.print(v);
        }


        if (k.contains("osak:identifier")) {
            System.out.println("");
        }

        
        return "";
    }

    private void parseOSM(InputStream inputStream) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        TaskTimer.recordTask("Begin OSM parsing");
        var stax = (XMLInputFactory2)XMLInputFactory2.newInstance();
        stax.configureForSpeed();
        var input = (XMLStreamReader2)stax.createXMLStreamReader(inputStream);
//        var id2node = new HashMap<Long, Node>();
        var id2node = new ArrayList<Node>();
        var way = new ArrayList<Node>();
        var addWay = false;
        DrawType currentDrawType = null;
        var hasSorted = false;

        boolean firstNode = true, firstWay = true, firstRelation = true;

        while (input.hasNext()) {
            var tagKind = input.next();
            if (tagKind == XMLEvent2.START_ELEMENT) {
                var name = input.getLocalName();
                if (name == "bounds") {
                    minlat = Double.parseDouble(input.getAttributeValue(null, "minlat"));
                    maxlat = Double.parseDouble(input.getAttributeValue(null, "maxlat"));
                    minlon = Double.parseDouble(input.getAttributeValue(null, "minlon"));
                    maxlon = Double.parseDouble(input.getAttributeValue(null, "maxlon"));
                } else if (name == "node") {
                    if (firstNode) {
                        TaskTimer.recordTask("Nodes");
                        firstNode = false;
                    } 
                    TaskTimer.incrementSubtask();
                    var id = Long.parseLong(input.getAttributeValue(null, "id"));
                    var lat = Double.parseDouble(input.getAttributeValue(null, "lat"));
                    var lon = Double.parseDouble(input.getAttributeValue(null, "lon"));
//                    id2node.put(id, new Node(lat, lon));
                    id2node.add(new Node(lat, lon, id));
                } else if (name == "way") {
                    if (firstWay) {
                        TaskTimer.recordTask("Sort array");
                        Collections.sort(id2node);
                        TaskTimer.recordTask("Ways");
                        firstWay = false;
                    }
                    TaskTimer.incrementSubtask();
                    way.clear();
                    addWay = false;
                    currentDrawType = null;
                } else if (name == "tag") {
                    var k = input.getAttributeValue(null, "k");
                    var v = input.getAttributeValue(null, "v");

                    // parseAddress(k,v);

                    if (currentDrawType == null)
                        currentDrawType = keyValToDrawType(k, v);
                    if (currentDrawType != null) {
                        addWay = true;
                    }
                } else if (name == "nd") {
                    var ref = Long.parseLong(input.getAttributeValue(null, "ref"));
                    var index = Collections.binarySearch(id2node, new Node(0, 0, ref));
                    var node = id2node.get(index);
                    way.add(node);
                } else if (name == "relation") {
                    if (firstRelation) {
                        TaskTimer.recordTask("Relations");
                        firstRelation = false;
                    } 
                    TaskTimer.incrementSubtask();
                }
            } else if (tagKind == XMLStreamConstants.END_ELEMENT) {
                var name = input.getLocalName();
                if (name == "way" && addWay) {
                    ways.add(new Way(way, currentDrawType));
                }
            }
        }
        TaskTimer.recordTask("Finalize parsing");
        input.close();
    }

    public void add(Point2D p1, Point2D p2) {
        edges.add(new Edge(p1, p2));
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
                    case "forest":
                        return DrawType.landuseFarmland;
                    case "residential":
                        return DrawType.landuseResidential; //Currently residential roads, not residential landuse.
                }
                break;
        }

        switch (val) {
            case "coastline":
                return DrawType.coastline;
        }

        return null;
    }
}
