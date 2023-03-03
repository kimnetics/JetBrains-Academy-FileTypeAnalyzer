package analyzer;

import analyzer.Data.Pattern;
import analyzer.Utils.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class Main {
    public static Logger logger;

    public static boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        // Initialize logger.
        logger = new Logger(Main.class.getName(), "%h/analyzer.log");

        logger.info("File Type Analyzer program started.");

        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid number of arguments.");
        }

        if (DEBUG_MODE) {
            logger.debug("Command line arguments:");
            for (String arg : args) {
                logger.debug(arg);
            }
        }

        String directoryPath = args[0];
        String patternFileSpecification = args[1];

        // Read file patterns.
        Pattern[] patterns = readPatterns(patternFileSpecification);

        if (DEBUG_MODE) {
            logger.debug("List of patterns in priority order:");
            for (Pattern pattern : patterns) {
                logger.debug(String.format("%d | %s | %s", pattern.priority(), pattern.pattern(), pattern.fileType()));
            }
        }

        // Set FileSearchManager logger.
        FileSearchManager.setLogger(logger);

        // Add files in directory to queue.
        FileSearchManager.addFiles(directoryPath);

        // Start searching files.
        FileSearchManager.startSearching(patterns);

        // Wait until file queue is empty.
        do {
            // Sleep for a while.
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        } while (!FileSearchManager.isQueueEmpty());

        // Stop searching files.
        FileSearchManager.stopSearching();

        logger.info("File Type Analyzer program ended.");
    }

    // Read file patterns.
    private static Pattern[] readPatterns(String patternFileSpecification) {
        try {
            // Read patterns from file.
            List<String> patternStrings = Files.readAllLines(Paths.get(patternFileSpecification), StandardCharsets.UTF_8);

            // Sort patterns by priority, keeping insertion order for same priority.
            TreeMap<String, Pattern> patternTreeMap = new TreeMap<>(Collections.reverseOrder());
            int patternIndex = Integer.MAX_VALUE;
            for (String patternString : patternStrings) {
                String[] elements = patternString.split(";");
                var pattern = new Pattern(Integer.parseInt(elements[0]), elements[1].replaceAll("^\"|\"$", ""), elements[2].replaceAll("^\"|\"$", ""));
                patternTreeMap.put(String.format("%010d", pattern.priority()) + "-" + String.format("%010d", patternIndex), pattern);
                patternIndex--;
            }

            // Convert patterns to array.
            var patterns = new Pattern[patternStrings.size()];
            patternIndex = 0;
            for (Map.Entry<String, Pattern> pattern : patternTreeMap.entrySet()) {
                patterns[patternIndex] = pattern.getValue();
                patternIndex++;
            }

            return patterns;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unexpected error reading file '%s'. %s", patternFileSpecification, e.getMessage()));
        }
    }
}
