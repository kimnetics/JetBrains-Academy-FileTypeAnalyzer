package analyzer;

import analyzer.Data.Pattern;
import analyzer.Utils.Logger;

import java.io.File;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileSearchManager {
    public static Logger logger;
    public static ExecutorService executor;
    private static final ArrayDeque<File> fileQueue = new ArrayDeque<>();
    private static final int NUMBER_OF_THREADS = 10;

    public FileSearchManager() {
    }

    // Set logger.
    public static void setLogger(Logger logger) {
        FileSearchManager.logger = logger;
    }

    // Add files in directory to queue.
    public synchronized static void addFiles(String directoryPath) {
        // Verify directory is valid.
        var directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(String.format("'%s' is not a directory.", directoryPath));
        }

        // Loop through files in directory.
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            logger.info(String.format("List of files in directory %s:", directoryPath));
            for (File file : directoryListing) {
                if (!file.isDirectory()) {
                    logger.info(file.getName());
                    // Add files to queue.
                    fileQueue.add(file);
                } else {
                    logger.info(String.format("Ignoring directory %s", file.getName()));
                }
            }
        }
    }

    // Start searching files.
    public static void startSearching(Pattern[] patterns) {
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for (int i = 1; i <= NUMBER_OF_THREADS; i++) {
            String threadId = "Thread" + i;
            executor.execute(new SearcherThread(logger, threadId, patterns));
        }
    }

    // Stop searching files.
    public static void stopSearching() {
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Sleep for a while.
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        }
    }

    // Is file queue empty?
    public synchronized static boolean isQueueEmpty() {
        return fileQueue.isEmpty();
    }

    // Get next file from queue.
    public synchronized static Optional<File> getNextFile() {
        Optional<File> file = Optional.empty();

        if (!fileQueue.isEmpty()) {
            try {
                file = Optional.of(fileQueue.remove());
            } catch (NoSuchElementException ignored) {
            }
        }

        return file;
    }
}
