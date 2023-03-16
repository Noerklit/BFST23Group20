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
            onlineParsing();
        } else {
            offlineParsing();
        }
    }

    private static void onlineParsing() {
        return;
    }

    private static void offlineParsing() {
        return;
    }
}
