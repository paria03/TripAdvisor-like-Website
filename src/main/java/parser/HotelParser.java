package parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import data.Hotel;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses information for hotels from json files
 */
public class HotelParser {

    /**
     * Parse a Json file and creates Hotel objects
     * and added them to a list
     *
     * @param fileName path to the json file
     * @return a list of all hotels
     */
    public List<Hotel> jsonParser(String fileName) {
        try (FileReader fr = new FileReader(fileName)) {
            Gson gson = new Gson();
            Hotel hotel;
            JsonObject allObj = gson.fromJson(fr, JsonObject.class);
            JsonArray arr = allObj.getAsJsonArray("sr");
            if (arr != null) {
                List<Hotel> list = new ArrayList<>();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject hotelObj = arr.get(i).getAsJsonObject();
                    String name = hotelObj.get("f").getAsString();
                    String id = hotelObj.get("id").getAsString();
                    String address = hotelObj.get("ad").getAsString();
                    String city = hotelObj.get("ci").getAsString();
                    String province = hotelObj.get("pr").getAsString();
                    JsonObject ll = hotelObj.getAsJsonObject("ll");
                    String lat = ll.get("lat").getAsString();
                    String lng = ll.get("lng").getAsString();

                    hotel = new Hotel(name, id, address, city, province, lat, lng);
                    list.add(hotel);
                }
                return list;
            }
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException("Failed to parse json file " + fileName + " for hotels", e);
        }
        return null;
    }
}
