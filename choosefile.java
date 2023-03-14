/*
 * This java script is completely seperate from the rest of the project.
 * It can't reference any other class in the project. It only affects
 * the file: "app/file-to-load.txt" when it is executed in the terminal.
 */

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.InputMismatchException;

public class choosefile {
    
    private static final String dataPath = "app/data/";
    private static final String dataPathGradle = "../app/data/";
    private static final String backDir = "../";

    private static final String fileToLoadPath = "app/file-to-load.txt";
    
    public static void main(String[] args) {
        String choosenFile = chooseFile();
        System.out.println(choosenFile);

        writeToFileToLoad(choosenFile);
    }

    private static String chooseFile() {
        Scanner sc = new Scanner(System.in);
        File[] dataFiles = getDataFiles();

        if (dataFiles == null || dataFiles.length == 0) {
            System.out.println("There are no files to choose from");
            return null;
        }

        System.out.println("Choose a file by typing the corresponding number:");
        for (int i = 0; i < dataFiles.length; i++) {
            String temp = dataFiles[i].toPath().toString().substring(dataPath.length());
            System.out.printf("[%s] %s\n", i, temp);
        }
        int fileNum = -1;
        while (true) {
            try {
                fileNum = sc.nextInt();
                
                //fileNum = 0;
                
                if (0 <= fileNum && fileNum < dataFiles.length) {
                    break;
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Write a number within range");
            } catch (InputMismatchException e) {
                System.out.println("Write a number");
                sc.nextLine(); // Clears the input
            }
            /*catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        return backDir + dataFiles[fileNum].toPath();
    }

    private static File[] getDataFiles() {
        File dataDir = new File(dataPath);
        File[] dataFiles = dataDir.listFiles();
        return dataFiles;
    }

    //*/
    private static void writeToFileToLoad(String data) {
        try {
            FileWriter writeHandler = new FileWriter(fileToLoadPath, false);
            writeHandler.write(data);
            writeHandler.close();
        } catch (FileNotFoundException noFileToLoad) { 
            // Will create a new file with the name
            // of the not found file, and call itself again.                                                     
            try {
                File newFileObject = new File(fileToLoadPath);
                newFileObject.createNewFile();
                writeToFileToLoad(data);
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }
    //*/
}
