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

import Benchmark.TaskTimer;

public class Model implements Serializable {

    public List<Way> ways = new ArrayList<Way>();
    
    public double minlat, maxlat, minlon, maxlon;

    public static Model load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
        if (filename.endsWith(".obj")) {
            TaskTimer.recordTask("Load obj-file");
            try (var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                return (Model) in.readObject();
            }
        }
        return new Model(filename);
    }
    

    public Model(String filename) throws IOException {
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

    private void parseZIP(String filename) throws IOException {
        var input = new ZipInputStream(new FileInputStream(filename));
        input.getNextEntry();
        parseOSM(input);
    }

    private void parseOSM(String filename) throws FileNotFoundException {
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

    private void parseOSM(InputStream inputStream) {
        OSMParser parser = new OSMParser(inputStream);
        ways = parser.getWays();
        minlat = parser.getMinlat();
        minlon = parser.getMinlon();
        maxlat = parser.getMaxlat();
        maxlon = parser.getMaxlon();
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
