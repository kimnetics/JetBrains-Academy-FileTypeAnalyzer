package analyzer.Algorithms;

import analyzer.Main;
import analyzer.Utils.Logger;
import analyzer.Utils.StreamBuffer;

public class RabinKarpAlgorithm implements StreamScanningAlgorithm {
    @Override
    public long find(String pattern, StreamBuffer streamBuffer) {
        // Check if stream fit entirely into buffer?
        // (If so, we can treat buffer like a string.)
        int streamLength = streamBuffer.getLength();
        if (streamLength == -1) {
            Main.logger.console("Stream exceeded buffer size.", Logger.Severity.ERROR);
            return -1;
        }

        // The following routine was based on:
        // https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/solutions/1963567/explained-java-code-with-comments-rabin-karp-algo-rolling-hash/

        int patternLength = pattern.length();
        if (patternLength > streamLength) return -1;

        int i;
        int j;
        int patternHash = 0;
        int streamHash = 0;
        int h = 1;
        int d = 256; // number of characters in input alphabet
        int q = 31; // a prime number to best distribute data among hash buckets

        // Value of h is "pow(d, pattern.length - 1) % q".
        for (i = 0; i < patternLength - 1; i++)
            h = (h * d) % q;

        // Calculate hash value of pattern and first window of text.
        for (i = 0; i < patternLength; i++) {
            patternHash = (d * patternHash + pattern.charAt(i)) % q;
            streamHash = (d * streamHash + streamBuffer.getSubstringElement(i)) % q;
        }

        // Slide pattern over text one by one.
        for (i = 0; i <= streamLength - patternLength; i++) {
            // Do hash values of current window of text and pattern match?
            if (streamHash == patternHash) {
                // Check characters one by one.
                for (j = 0; j < patternLength; j++) {
                    if (streamBuffer.getSubstringElement(i + j) != pattern.charAt(j))
                        break;
                }

                // We have a confirmed match.
                if (j == patternLength)
                    return i;
            }

            // Calculate hash value for next window of text: remove leading digit, add trailing digit.
            if (i < streamLength - patternLength) {
                streamHash = (d * (streamHash - streamBuffer.getSubstringElement(i) * h) + streamBuffer.getSubstringElement(i + patternLength)) % q;

                // We might get a negative value, convert it to positive.
                if (streamHash < 0)
                    streamHash = (streamHash + q);
            }
        }

        return -1;
    }
}
