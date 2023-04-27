package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TemporaryAddressFile {
    private String fileName;
    private BufferedWriter bufferedWriter;
    private Scanner reader;

    public TemporaryAddressFile() {
        fileName = "data/temp-addresses.txt";
        try {
            FileWriter fw = new FileWriter(fileName, false);
            fw.write("");
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void openStream() {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            bufferedWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeToStream(String address) {
        try {
            bufferedWriter.write(address);
            bufferedWriter.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void closeStream() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void getAddresses() {
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void openReadStream() {
        try {
            File myObj = new File(fileName);
            reader = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public boolean hasAddress() {
        return reader.hasNext();
    }

    public String getAddress() {
        return reader.nextLine();
    }

    public void closeReadStream() {
        reader.close();
    }
}
