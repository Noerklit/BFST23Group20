package Parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class AddressParser {
    private static boolean online;

    public AddressParser() {
        this.online = isOnline();
    }

    //public static Address[] getAddresses(String address) {
    public static void getAddresses(String address) {
        List<String> result = parse(address);         

        for (String e : result) {
            System.out.println(e);
        }
    }

    private static String getUrlEncodedStr(String nonEncodedStr) {
        return URLEncoder.encode(nonEncodedStr);
    }

    private static boolean isOnline() {
        try {
            URL url = new URL("http://example.com/");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return (connection.getResponseCode() == 200) ? true : false;

        } catch (Exception e) {
                e.printStackTrace();
        }
        return false;
    }

    private static List<String> parse(String toParse) {
        List<String> result = new ArrayList<>(); 
        if (online) {
            result = onlineParsing(toParse);
        } else {
            result = offlineParsing(toParse);
        }
        return result;
    }

    private static List<String> onlineParsing(String searchFor) {
        String result = "";

        try {
            URL url = new URL("https://api.dataforsyningen.dk/autocomplete?q=" + getUrlEncodedStr(searchFor) + "&caretpos=18&fuzzy=");
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200) {
                result = httpToString(connection);
            } else {
                System.out.println("Could not connect to DAWA API\nStatus code: " + connection.getResponseCode());
                List<String> return_strings = offlineParsing(searchFor);
                return return_strings;
            }
        } catch (Exception e) {
                e.printStackTrace();
        }

        Address[] addresses = JsonParser.parseAddress(result);
        List<String> return_strings = new ArrayList<>();

        for (Address address : addresses) {
            return_strings.add(address.type + " " + address.forslagstekst);
        }

        return return_strings;
    }

    private static List<String> offlineParsing(String toParse) {
        Trie ourTrie = new Trie();

        String fileToLoadPath = "/home/cave/UNI/Map/BFST23Group20/app/data/addresses_static";
        File file = new File(fileToLoadPath);
        
        List<String> FileLines = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                ourTrie.insert(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> trieRes = ourTrie.autoComplete(toParse);
        return trieRes;
    }

    private static String httpToString(HttpURLConnection conn) {
        String result = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;

            // Iterate through output of request
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            result = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String offlineParsing(String toParse) {
        // String fileToLoadPath = "/home/cave/UNI/Map/BFST23Group20/app/data/addresses_static";
        // File file = new File(fileToLoadPath);
        // 
        // List<String> FileLines = new ArrayList<>();
        // try {
        //     List<String> lines = Files.readAllLines(file.toPath());
        //     for (String line : lines) {
        //         FileLines.add(line);
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // for (String line : FileLines) {
        //     System.out.println(line);
        // }
        // TrieNode ourTrie = new TrieNode();
        // ourTrie.trieInsert("HelloWorld");
        return "";
    }
}
