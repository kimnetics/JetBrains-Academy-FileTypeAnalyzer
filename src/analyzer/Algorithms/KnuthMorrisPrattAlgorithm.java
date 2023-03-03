package analyzer.Algorithms;

import analyzer.Main;
import analyzer.Utils.StreamBuffer;

public class KnuthMorrisPrattAlgorithm implements StreamScanningAlgorithm {
    @Override
    public long find(String pattern, StreamBuffer streamBuffer) {
        if (Main.DEBUG_MODE) {
            Main.logger.debug("Knuth-Morris-Pratt Algorithm started");
        }

        long matchStartIndex = 0;

        // Get Knuth–Morris–Pratt partial match table.
        int[] partialMatchTable = getPartialMatchTable(pattern);

        int patternLength = pattern.length();

        int alreadyMatchedOffset = 0;
        while (true) {
            // Log debug information.
            if (Main.DEBUG_MODE) {
                Main.logger.debug("");
                Main.logger.debug(String.format("Text index: %d", matchStartIndex));
                Main.logger.debug(String.format("Pattern: %s", pattern));
                Main.logger.debug(String.format("         %s^", " ".repeat(alreadyMatchedOffset)));
                var substring = new StringBuilder();
                for (int i = 0; i < patternLength; i++) {
                    int substringElement = streamBuffer.getSubstringElement(i);
                    if (substringElement == -1) {
                        break;
                    }
                    substring.append((char) streamBuffer.getSubstringElement(i));
                }
                Main.logger.debug(String.format("Stream:  %s", substring));
            }

            // Compare pattern against substring.
            int patternIndex = alreadyMatchedOffset;
            for (int i = alreadyMatchedOffset; i < patternLength; i++) {
                // Get substring element.
                int substringElement = streamBuffer.getSubstringElement(i);

                // Exit if we hit end of data.
                if (substringElement == -1) {
                    if (Main.DEBUG_MODE) {
                        Main.logger.debug("Knuth-Morris-Pratt Algorithm ended - hit end of data");
                    }
                    return -1;
                }

                if (Main.DEBUG_MODE) {
                    Main.logger.debug(String.format("Comparing pattern '%s' against stream '%s'", pattern.charAt(i), (char) substringElement));
                }
                if (pattern.charAt(i) == substringElement) {
                    patternIndex++;
                } else {
                    break;
                }
            }

            // Did we find a match?
            if (patternIndex == patternLength) {
                if (Main.DEBUG_MODE) {
                    Main.logger.debug("Knuth-Morris-Pratt Algorithm ended - found match");
                }
                return matchStartIndex;
            }

            // Did we fail at very beginning?
            // I believe this check is necessary, because the Knuth–Morris–Pratt partial match table logic we got from
            // the lesson is a bit different from that shown in the Knuth–Morris–Pratt Wikipedia entry. That one has a
            // 0 element value of -1 and the array is one element longer than the length of the pattern.
            int numberOfElements;
            if (patternIndex == 0) {
                // Set pattern offset.
                alreadyMatchedOffset = 0;

                // Set number of elements to jump.
                numberOfElements = 1;
            } else {
                // Set pattern offset.
                alreadyMatchedOffset = partialMatchTable[patternIndex - 1];

                // Calculate number of elements to jump.
                numberOfElements = patternIndex - alreadyMatchedOffset;
            }

            // Move match start index.
            matchStartIndex += numberOfElements;

            // Move substring start index.
            streamBuffer.moveSubstringStartIndex(numberOfElements);

            if (Main.DEBUG_MODE) {
                Main.logger.debug(String.format("Failed at position %d. Jumping by (%d) - (%d) = %d", patternIndex, patternIndex, alreadyMatchedOffset, numberOfElements));
            }
        }
    }

    // Get Knuth–Morris–Pratt partial match table.
    private int[] getPartialMatchTable(String pattern) {
        int patternLength = pattern.length();
        int[] partialMatchTable = new int[patternLength];

        partialMatchTable[0] = 0;
        for (int patternIndex = 1; patternIndex < patternLength; patternIndex++) {
            int i = partialMatchTable[patternIndex - 1];
            while (true) {
                if (pattern.charAt(i) == pattern.charAt(patternIndex)) {
                    partialMatchTable[patternIndex] = i + 1;
                    break;
                }
                if (i < 1) {
                    partialMatchTable[patternIndex] = 0;
                    break;
                }
                i = partialMatchTable[i - 1];
                if (i == 0) {
                    partialMatchTable[patternIndex] = 0;
                    break;
                }
            }
        }

        return partialMatchTable;
    }
}
