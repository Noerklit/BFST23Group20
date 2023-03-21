package Model;

import java.util.Map;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonParser {
    public static Address[] parseAddress(String json) {
        Gson gson = new Gson();
        Address[] parsed = gson.fromJson(json, Address[].class);
        return parsed;
    }
}
