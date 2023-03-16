import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class AdressParsing {
    String toParse;
    boolean online;

    public AdressParsing(String toParse) {
        this.toParse = toParse;
        this.online = isOnline();
    }

    private static boolean isOnline() {
        try {
            URL url = new URL("http://example.com/");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return (connection == 200) ? true: false;

        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    public static void parse() {
        if (online) {
            onlineParsing(toParse);
        } else {
            offlineParsing(toParse);
        }
    }

    private static void onlineParsing(String searchFor) {
        try {
            URL url = new URL("https://api.dataforsyningen.dk/autocomplete?q=" + searchFor + "&caretpos=18&fuzzy=");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection == 200) {
                // Parse output
            } else {
                System.out.println("Could not connect to DAWA API\nFalling back to offline parsing");
                offlineParsing(searchFor);
            }

        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    private static void offlineParsing(String toParse) {
        // Regex her
        return;
    }
}
