package hotelapp;

import data.Review;
import server.DatabaseHandler;
import server.HotelDatabaseHandler;
import server.ReviewsDatabaseHandler;
import server.UserDatabaseHandler;

import java.util.*;

/**
 * This class is responsible for loading, storing, and retrieving reviews for hotels.
 */
public class ReviewData {

    private final DatabaseHandler dbHandler = DatabaseHandler.getInstance();
    private final UserDatabaseHandler userDatabaseHandler=UserDatabaseHandler.getInstance();
    private final ReviewsDatabaseHandler reviewsDatabaseHandler=ReviewsDatabaseHandler.getInstance();
    private final HotelDatabaseHandler hotelDatabaseHandler= HotelDatabaseHandler.getInstance();
    /**
     * Adds a review to the reviews map. If the hotel ID associated with the review already has
     * reviews recorded, the review is added to the existing set. If there are no reviews yet
     * for the hotel ID, a new set is created and the review is added to it.
     *
     * @param review the review object to be added to the reviews map
     */
    public void addReview(Review review, boolean newReview) {
        String hotelId = review.getHotelId();
        if (hotelId != null) {
            if (newReview) {
                reviewsDatabaseHandler.registerNewReview(review);
            } else {
                reviewsDatabaseHandler.registerDatesetReview(review);
                createUser(review);
            }

        }
    }

    /**
     * Adds all review in the list from a file to the main map
     * and updates the invertedIndex map as well
     *
     * @param allReviews list of all reviews
     */
    public void addReviews(List<Review> allReviews) {
        for (Review review : allReviews) {
            addReview(review, false);
        }
    }

    /**
     * Creates username and password for users in the given json file
     *
     * @param review the review written by a user
     */
    private void createUser(Review review) {
        String username = review.getNickName();
        String password = "StrongPass" + username + review.getReviewId() + "$";
        userDatabaseHandler.registerUser(username, password);
    }

    /**
     * finds reviews for the given hotel id
     *
     * @param hotelId hotel id
     * @return reviews of the given hotel id
     */
    public List<Review> getReviews(String hotelId, int limit, int offset) {
        List<Review> reviewList = hotelDatabaseHandler.getAllHotelReviews(hotelId, limit, offset);
        for (Review review : reviewList) {
            int likeCount = DatabaseHandler.getInstance().getLikeCount(Integer.parseInt(review.getReviewId()));
            review.setLikeCount(likeCount);
        }
        return reviewList;

    }
}
