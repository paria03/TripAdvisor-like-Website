package hotelapp;

import data.Hotel;
import parser.HotelParser;
import server.DatabaseHandler;
import server.HotelDatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for loading and managing hotel data.
 */
public class HotelLoader {
    private final HotelParser hotelParser = new HotelParser();
    private final HotelDatabaseHandler hotelDatabaseHandler = HotelDatabaseHandler.getInstance();


    /**
     * Updates the hotelMap with data parsed from a JSON file.
     *
     * @param fileName the name of the JSON file that contains hotel data
     *                 This method uses the HotelParser to parse the JSON file and extract data for hotels,
     *                 adding hotels to the hotelMap with hotel IDs as the keys and Hotel objects as the values.
     */
    public void updateHotelMap(String fileName) {

        List<Hotel> list = hotelParser.jsonParser(fileName);
        if (list != null) {
            for (Hotel hotel : list) {
                hotelDatabaseHandler.registerHotel(hotel);
            }
        }

    }

    /**
     * Finds hotels that have the given keyword in their name
     *
     * @param keyword keyword to search hotels name
     * @return list of hotels that has this keyword in their name
     */
    public List<Hotel> findHotelsByKeyword(String keyword) {
        List<String> hotelsName = hotelDatabaseHandler.getAllHotelsName();
        List<Hotel> hotelsWithKeyword = new ArrayList<>();
        for (String hotelName : hotelsName) {
            if (hotelName.toLowerCase().contains(keyword.toLowerCase())) {
                Hotel hotel = hotelDatabaseHandler.getHotelByName(hotelName);
                if (hotel != null) {
                    hotelsWithKeyword.add(hotel);
                }
            }
        }
        return hotelsWithKeyword;
    }
}