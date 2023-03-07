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

import org.checkerframework.checker.units.qual.K;

import javafx.geometry.Point2D;

public class Model implements Serializable {
    public List<Edge> lines = new ArrayList<Edge>();
    public List<Way> ways = new ArrayList<Way>();

    private int fidelity = 0;

    public double minlat, maxlat, minlon, maxlon;

    public static Model load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException, XMLStreamException, FactoryConfigurationError {
        if (filename.endsWith(".obj")) {
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

    private void parseOSM(InputStream inputStream) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        var input = XMLInputFactory.newInstance().createXMLStreamReader(new InputStreamReader(inputStream));
        var id2node = new HashMap<Long, Node>();
        var way = new ArrayList<Node>();
        var addWay = false;
        DrawType currentDrawType = null;

        while (input.hasNext()) {
            var tagKind = input.next();
            if (tagKind == XMLStreamConstants.START_ELEMENT) {
                var name = input.getLocalName();
                if (name == "bounds") {
                    minlat = Double.parseDouble(input.getAttributeValue(null, "minlat"));
                    maxlat = Double.parseDouble(input.getAttributeValue(null, "maxlat"));
                    minlon = Double.parseDouble(input.getAttributeValue(null, "minlon"));
                    maxlon = Double.parseDouble(input.getAttributeValue(null, "maxlon"));
                } else if (name == "node") {
                    var id = Long.parseLong(input.getAttributeValue(null, "id"));
                    var lat = Double.parseDouble(input.getAttributeValue(null, "lat"));
                    var lon = Double.parseDouble(input.getAttributeValue(null, "lon"));
                    id2node.put(id, new Node(lat, lon));
                } else if (name == "way") {
                    way.clear();
                    addWay = false;
                    currentDrawType = null;
                } else if (name == "tag") {
                    var k = input.getAttributeValue(null, "k");
                    var v = input.getAttributeValue(null, "v");
                    currentDrawType = keyValToDrawType(k, v);
                    if (currentDrawType != null) {
                        addWay = true;
                    }

                } else if (name == "nd") {
                    var ref = Long.parseLong(input.getAttributeValue(null, "ref"));
                    var node = id2node.get(ref);
                    way.add(node);
                }
            } else if (tagKind == XMLStreamConstants.END_ELEMENT) {
                var name = input.getLocalName();
                if (name == "way" && addWay) {
                    ways.add(new Way(way));
                }
            }
        }
    }

    public void add(Point2D p1, Point2D p2) {
        lines.add(new Edge(p1, p2));
    }

    private DrawType keyValToDrawType(String key, String val) {

        switch (key) {
            case "building":
                return DrawType.building;
        }

        switch (val) {
            case "motorway":
                return DrawType.motorway;
        }

        return null;
    }
}