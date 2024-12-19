package parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import data.Review;
import hotelapp.InvalidRatingException;

import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * Parses review data from json files
 * Uses multi-threading to parse multiple files concurrently
 */
public class ReviewParser {

    /**
     * Parses data from a json file and create Review objects
     *
     * @param path path to the json file
     * @return list of reviews
     */
    public List<Review> loadDataFromJson(String path) {
        try (FileReader fr = new FileReader(path)) {
            Gson gson = new Gson();
            JsonObject allObj = gson.fromJson(fr, JsonObject.class);
            JsonObject reviewDetails = allObj.getAsJsonObject("reviewDetails");
            JsonObject reviewCollection = reviewDetails.getAsJsonObject("reviewCollection");
            JsonArray reviewsArray = reviewCollection.getAsJsonArray("review");
            if (reviewsArray != null) {
                List<Review> arrayList = new ArrayList<>();
                for (int i = 0; i < reviewsArray.size(); i++) {
                    JsonObject reviewObj = reviewsArray.get(i).getAsJsonObject();

                    Review review = parseReviewFromJson(reviewObj);
                    if (review != null) {
                        arrayList.add(review);
                    }
                }
                return arrayList;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Parses a json object and creates Review objects
     *
     * @param reviewObj json object
     * @return Review object
     */
    private Review parseReviewFromJson(JsonObject reviewObj) {
        try {
            String hotelId = reviewObj.get("hotelId").getAsString();
            String reviewId = reviewObj.get("reviewId").getAsString();
            int rating = reviewObj.get("ratingOverall").getAsInt();
            String title = reviewObj.get("title").getAsString();
            String text = reviewObj.get("reviewText").getAsString();
            String nickname = reviewObj.get("userNickname").getAsString().trim();
            String date = reviewObj.get("reviewSubmissionDate").getAsString();
            return new Review(hotelId, reviewId, rating, title, text, nickname, date);
        } catch (DateTimeParseException e) {
            System.out.println(e);
        } catch (InvalidRatingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
