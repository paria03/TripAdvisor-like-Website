package data;

import hotelapp.InvalidRatingException;

/**
 * this class is for creating objects of this type
 */
public class Review {
    private final String hotelId;
    private final String reviewId;
    private int rating;
    private String title;
    private String text;
    private String nickName = "Anonymous";
    private final String date;
    private final String formattedDate;
    private  int likeCount=0;
    public Review(String hotelId, String reviewId, int rating, String title, String text, String nickName, String date) throws InvalidRatingException {
        this.hotelId = hotelId;
        this.reviewId=reviewId;
        setRating(rating);
        this.rating = rating;
        this.title = title;
        this.text = text;
        if (!nickName.isEmpty()) {
            this.nickName = nickName;
        }
        this.date = date;
        this.formattedDate=date.split(" ")[0];
    }

    //getters
    public String getDate(){
        return date;
    }



    public String getFormattedDate() {
        return formattedDate;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getNickName() {
        return nickName;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getText() {
        return text;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setRating(int rating) throws InvalidRatingException {
        if (rating < 1 || rating > 5) {
            throw new InvalidRatingException("Rating is not valid");
        }
        this.rating = rating;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Prints data for each review
     *
     * @return review information
     */
    public String toString() {
        return "--------------------" + System.lineSeparator() +
                "Review by " + nickName + " on " + date + System.lineSeparator() +
                "Rating: " + rating + System.lineSeparator() +
                "ReviewId: " + reviewId + System.lineSeparator() +
                title + System.lineSeparator() +
                text + System.lineSeparator();

    }
}
