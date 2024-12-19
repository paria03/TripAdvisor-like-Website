package hotelapp;

/**
 * Custom exception class for entering invalid hotel rating
 */
public class InvalidRatingException extends Exception {

    /**
     * Checks if the rating for hotels are within a specific range
     */
    public InvalidRatingException(String message) {
        super(message);
        System.out.println("The value of the hotel rating is out of range");
    }

}
