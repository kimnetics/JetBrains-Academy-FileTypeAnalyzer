package analyzer;

import analyzer.Algorithms.RabinKarpAlgorithm;
import analyzer.Algorithms.StreamScanningAlgorithm;
import analyzer.Data.Pattern;
import analyzer.Utils.Logger;
import analyzer.Utils.StreamBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SearcherThread implements Runnable {
    private final Logger logger;
    private final String threadId;
    private final Pattern[] patterns;

    private final StreamScanningAlgorithm algorithm = new RabinKarpAlgorithm();

    public SearcherThread(Logger logger, String threadId, Pattern[] patterns) {
        this.logger = logger;
        this.threadId = threadId;
        this.patterns = patterns;
    }

    @Override
    public void run() {
        logger.info(String.format("Searcher %s started.", threadId));

        // Loop until shutdown.
        while (!FileSearchManager.executor.isShutdown()) {
            // Get next file from queue.
            Optional<File> optionalFile = FileSearchManager.getNextFile();
            if (optionalFile.isPresent()) {
                // Loop through patterns.
                File file = optionalFile.get();
                String fileName = file.getName();
                logger.info(String.format("Working on file %s", fileName));
                if (Main.DEBUG_MODE) {
                    try {
                        List<String> fileLines = Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
                        for (String fileLine : fileLines) {
                            logger.debug(String.format("%s: %s", fileName, fileLine));
                        }
                    } catch (IOException ignored) {
                    }
                }
                Pattern foundPattern = null;
                for (Pattern pattern : patterns) {
                    // Search file for pattern.
                    if (searchFile(file, pattern)) {
                        foundPattern = pattern;
                        break;
                    }
                }
                // Show results.
                if (foundPattern != null) {
                    logger.console(String.format("%s: %s", fileName, foundPattern.fileType()));
                } else {
                    logger.console(String.format("%s: Unknown file type", fileName));
                }
            } else {
                // Sleep for a while.
                try {
                    TimeUnit.MILLISECONDS.sleep(200L);
                } catch (InterruptedException ignored) {
                }
            }
        }

        logger.info(String.format("Searcher %s stopped.", threadId));
    }

    // Search file for pattern.
    // Returns true if pattern found.
    private boolean searchFile(File file, Pattern pattern) {
        boolean found = false;

        String fileName = file.getName();
        try (InputStream inputStream = new FileInputStream(file)) {
            var streamBuffer = new StreamBuffer(logger, inputStream);
            long position = algorithm.find(pattern.pattern(), streamBuffer);
            found = (position != -1);
        } catch (IOException e) {
            logger.console(String.format("Unexpected error reading file '%s'. %s", fileName, e.getMessage()), Logger.Severity.ERROR);
        }

        return found;
    }
}
