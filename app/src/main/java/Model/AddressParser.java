package Model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddressParser {
    private static boolean online;

    public AddressParser() {
        this.online = isOnline();
    }

    public static Address[] getAddresses(String address) {
        String result = parse(address);         

        Address[] addresses = JsonParser.parseAddress(result);
        return addresses;
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

    private static String parse(String toParse) {
        String result = "";
        if (online) {
            result = onlineParsing(toParse);
        } else {
            result = offlineParsing(toParse);
        }
        return result;
    }

    private static String onlineParsing(String searchFor) {
        String result = "";

        try {
            URL url = new URL("https://api.dataforsyningen.dk/autocomplete?q=" + getUrlEncodedStr(searchFor) + "&caretpos=18&fuzzy=");
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200) {
                result = httpToString(connection);
                return result;
            } else {
                System.out.println("Could not connect to DAWA API\nStatus code: " + connection.getResponseCode());
                result = offlineParsing(searchFor);
                return result;
            }
        } catch (Exception e) {
                e.printStackTrace();
        }

        return result;
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
        // Regex her
        System.out.println("Parsing offline now");
        return "";
    }
}
