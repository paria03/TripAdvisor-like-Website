package hotelapp;

import data.Review;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Extends class ReviewData and make reading and writing from this class thread safe
 */
public class ThreadSafeReviewData extends ReviewData {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Adds a list of reviews in a thread-safe manner.
     * Acquires a write lock to ensure that no other reads or writes can occur
     * while adding the new review, preventing data inconsistency.
     *
     * @param review list of all reviews
     */
    @Override
    public void addReviews(List<Review> review) {
        try {
            lock.writeLock().lock();
            super.addReviews(review);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns a list of reviews for the given hotel id in  a thread-safe manner.
     *
     * @param hotelId hotel id
     * @return a list of all reviews
     */
    @Override
    public List<Review> getReviews(String hotelId, int limit, int offset) {
        try {
            lock.readLock().lock();
            return super.getReviews(hotelId, limit, offset);
        } finally {
            lock.readLock().unlock();
        }
    }
}
