package analyzer.Algorithms;

import analyzer.Utils.StreamBuffer;

public class NaiveAlgorithm implements StreamScanningAlgorithm {
    @Override
    public long find(String pattern, StreamBuffer streamBuffer) {
        long matchStartIndex = 0;

        int patternIndex = 0;
        int patternFinished = pattern.length() - 1;

        while (true) {
            // Get substring element.
            int substringElement = streamBuffer.getSubstringElement(patternIndex);

            // Exit if we hit end of data.
            if (substringElement == -1) {
                break;
            }

            // Does pattern element match substring element?
            char patternElement = pattern.charAt(patternIndex);
            if (patternElement == substringElement) {
                // Did we match entire pattern?
                if (patternIndex == patternFinished) {
                    return matchStartIndex;
                } else {
                    patternIndex++;
                }
            } else {
                // Increment match start index.
                matchStartIndex++;

                // Move substring start index over by one.
                streamBuffer.moveSubstringStartIndex(1);

                // Start at beginning of pattern again.
                patternIndex = 0;
            }
        }

        // If we got here, we didn't find a match.
        return -1;
    }
}
