package analyzer.Algorithms;

import analyzer.Utils.StreamBuffer;

public interface StreamScanningAlgorithm {
    long find(String pattern, StreamBuffer streamBuffer);
}
