package analyzer.Algorithms;

import analyzer.Utils.StreamBuffer;

public class SelectionContext {
    private StreamScanningAlgorithm algorithm;

    public void setAlgorithm(StreamScanningAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public long find(String pattern, StreamBuffer streamBuffer) {
        return algorithm.find(pattern, streamBuffer);
    }
}
