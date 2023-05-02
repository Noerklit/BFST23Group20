package Parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;


public class AddressSearch implements Serializable {
    private boolean online;
    private Trie addressesTrie;

    public AddressSearch() {
        this.online = isOnline();
        this.online = false;
        addressesTrie = new Trie();
    }

    public void addAddress(String address, String city) {
        addressesTrie.insert(address, city);
    }

    public List<String> getAddresses(String text) {
        List<String> result = search(text);
        return result;
    }

    private String getUrlEncodedStr(String nonEncodedStr) {
        return URLEncoder.encode(nonEncodedStr);
    }

    private boolean isOnline() {
        try {
            URL url = new URL("http://example.com/");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private List<String> search(String toParse) {
        List<String> result = new ArrayList<>(); 
        if (online) {
            result = onlineSearch(toParse);
        } else {
            result = offlineSearch(toParse);
        }
        return result;
    }

    private List<String> onlineSearch(String searchFor) {
        String result = "";

        try {
            URL url = new URL("https://api.dataforsyningen.dk/autocomplete?q=" + getUrlEncodedStr(searchFor) + "&caretpos=18&fuzzy=");
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200) {
                result = httpToString(connection);
            } else {
                System.out.println("Could not connect to DAWA API\nStatus code: " + connection.getResponseCode());
                List<String> return_strings = offlineSearch(searchFor);
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

    private List<String> offlineSearch(String toParse) {
        List<String> trieRes = addressesTrie.autoComplete(toParse);
        return trieRes;
    }

    private String httpToString(HttpURLConnection conn) {
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
}
