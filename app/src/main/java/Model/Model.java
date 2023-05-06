package Model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.zip.ZipInputStream;

import Benchmark.TaskTimer;
import KDTree.KDTree;
import Parser.AddressSearch;
import Pathfinding.Dijkstra;

public class Model implements Serializable {

    private AddressSearch addressSearch;

    public List<Way> ways = new ArrayList<Way>();
    public List<Way> coastlines;

    public double minlat, maxlat, minlon, maxlon;
    public KDTree wayKDTree, backgroundKdtree, buildingKdtree;

    private transient Dijkstra dijkstraGraph;

    public static Model load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
        if (filename.endsWith(".obj")) {
            TaskTimer.recordTask("Load obj-file");
            try (var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                Model loadedModel = (Model) in.readObject();
                loadedModel.initDijkstra();
                return loadedModel;
            }
        }
        return new Model(filename);
    }


    public Model(String filename) throws IOException {
        addressSearch = new AddressSearch();

        if (filename.endsWith(".osm.zip")) {
            parseZIP(filename);
        } else if (filename.endsWith(".osm")) {
            parseOSM(filename);
        } else {
            throw new IOException("Could not find .osm.zip or .osm file");
        }

        TaskTimer.recordTask("Save to obj-file");
        save(filename+".obj");
    }

    void save(String filename) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    private void parseZIP(String filename) throws IOException {
        var input = new ZipInputStream(new FileInputStream(filename));
        input.getNextEntry();
        parseOSM(input);
    }

    private void parseOSM(String filename) throws FileNotFoundException {
        parseOSM(new FileInputStream(filename));
    }

    private void parseOSM(InputStream inputStream) {
        OSMParser parser = new OSMParser(inputStream, addressSearch);
        ways = parser.getWays();
        wayKDTree = parser.wayKDTree;
        backgroundKdtree = parser.backgroundKdtree;
        buildingKdtree = parser.buildingKdtree;
        coastlines = parser.coastlines;
        minlat = parser.getMinlat();
        minlon = parser.getMinlon();
        maxlat = parser.getMaxlat();
        maxlon = parser.getMaxlon();
        initDijkstra();
        /*/
        for (String address : parser.getAddresses()) {
            addressSearch.addAddress(address);
        }
        //*/
    }

    public void initDijkstra() {
        TaskTimer.recordTask("Init Dijkstra");
        dijkstraGraph = new Dijkstra(wayKDTree, "SPEED");
    }

    public float[] getCoordsFromNodeId(Long nodeId) {
        return dijkstraGraph.getCoordsFromNodeId(nodeId);
    }
    public ArrayList<Float> getShortestPathCoords(long start, long end) {
        return dijkstraGraph.getShortestPathCoords(start, end);
    }

    public List<String> searchAddresses(String text) {
        try {
            return addressSearch.getAddresses(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}