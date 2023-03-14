package App;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileToLoad {

    private static final String defaultFile = "../app/data/bornholm.osm.zip";
    private static final String fileToLoadPath = "../app/file-to-load.txt";

    public static String getFileToLoad() {
        String fileToLoad = readFileToLoad();

        if (fileToLoad == null || fileToLoad.equals("")) return defaultFile;
        
        return fileToLoad;
    }

    private static String readFileToLoad() {
        String firstLineInFile = null;
        try {
            File fileStream = new File(fileToLoadPath);
            Scanner sc = new Scanner(fileStream);

            if (sc.hasNextLine()) {
                firstLineInFile = sc.nextLine();
            }
            sc.close();
        } catch (FileNotFoundException e) {
        } 
        /*catch (IOException e) {
        } */
        return firstLineInFile;
    }
}
