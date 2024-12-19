package hotelapp;

import data.Review;
import parser.ReviewParser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/**
 * This Class traverse directories concurrently
 */
public class ReviewDirectoryTraverser {
    private final ExecutorService poolManager;
    private final Phaser phaser;
    private final ReviewParser reviewParser;

    public ReviewDirectoryTraverser(int threadNum, ReviewParser reviewParser) {
        this.poolManager = Executors.newFixedThreadPool(threadNum);
        this.phaser = new Phaser();
        this.reviewParser = reviewParser;
    }

    /**
     * Helper method for traversing directories concurrently and shutting down the pool
     *
     * @param directoryPath        path to directories
     * @param threadSafeReviewData thread safe class for adding reviews to data structures
     */
    public void traverseDirectory(String directoryPath, ThreadSafeReviewData threadSafeReviewData) {
        try {
            processDirectory(directoryPath, threadSafeReviewData);
        } finally {
            shutdownPool();
        }
    }

    /**
     * Traverse over directories and parse json files in each directory and get information for them
     * each json file is parsed concurrently
     *
     * @param directoryPath        path to the directory with reviews
     * @param threadSafeReviewData thread safe version of the ReviewData class
     */
    private void processDirectory(String directoryPath, ThreadSafeReviewData threadSafeReviewData) {
        Path path = Paths.get(directoryPath);
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> filesName = Files.newDirectoryStream(path)) {
                for (Path filePath : filesName) {
                    if (Files.isDirectory(filePath)) {
                        processDirectory(filePath.toString(), threadSafeReviewData);
                    } else if (!Files.isDirectory(filePath) && filePath.toString().endsWith(".json")) {
                        poolManager.submit(new FileWorker(filePath, threadSafeReviewData));
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        } else if (path.toString().endsWith(".json")) {
            poolManager.submit(new FileWorker(path, threadSafeReviewData));
        } else {
            System.out.println("Invalid path");
        }
    }

    /**
     * Inner class responsible for processing json files concurrently
     */
    private class FileWorker implements Runnable {
        private final Path filePath;
        private final ThreadSafeReviewData threadSafeReviewData;

        public FileWorker(Path filePath, ThreadSafeReviewData threadSafeReviewData) {
            this.filePath = filePath;
            this.threadSafeReviewData = threadSafeReviewData;
            phaser.register();
        }

        @Override
        public void run() {
            try {
                List<Review> localReviews = reviewParser.loadDataFromJson(filePath.toString());
                if (localReviews != null) {
                    threadSafeReviewData.addReviews(localReviews);
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                phaser.arriveAndDeregister();
            }
        }
    }

    /**
     * Waits for other threads to reach a certain point and shutdowns the pool
     */
    private void shutdownPool() {
        phaser.awaitAdvance(phaser.getPhase());
        poolManager.shutdown();
    }
}
