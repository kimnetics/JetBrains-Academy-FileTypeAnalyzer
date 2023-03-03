package analyzer.Utils;

import java.io.IOException;
import java.io.InputStream;

public class StreamBuffer {
    private final int BUFFER_LENGTH = 256;
    private final int[] circularBuffer = new int[BUFFER_LENGTH];
    private int writeIndex;
    private int substringStartIndex;

    private final Logger logger;
    private final InputStream inputStream;
    private int length;

    public StreamBuffer(Logger logger, InputStream inputStream) {
        this.logger = logger;
        this.inputStream = inputStream;
        initialLoadBuffer();
    }

    private void initialLoadBuffer() {
        writeIndex = 0;
        substringStartIndex = 0;

        // Read data to fill buffer.
        length = -1;
        for (int i = 0; i < BUFFER_LENGTH; i++) {
            int value = readValue();
            circularBuffer[i] = value;
            // Did we hit end of stream?
            if (value == -1) {
                for (int j = i + 1; j < BUFFER_LENGTH; j++) {
                    circularBuffer[j] = -1;
                }
                length = i;
            }
        }
    }

    // Get stream length.
    // If we have a length != -1, it implies stream was entirely read during initial load of buffer.
    public int getLength() {
        return length;
    }

    // Get substring element.
    public int getSubstringElement(int offset) {
        int elementIndex = substringStartIndex + offset;
        if (elementIndex >= BUFFER_LENGTH) {
            elementIndex -= BUFFER_LENGTH;
        }
        return circularBuffer[elementIndex];
    }

    // Move substring start index and read data to fill opened buffer spots.
    public void moveSubstringStartIndex(int numberOfElements) {
        // Move substring start index.
        substringStartIndex += numberOfElements;

        // Is substring start index past end of buffer?
        if (substringStartIndex >= BUFFER_LENGTH) {
            // Wrap substring start index around back to start of buffer.
            substringStartIndex -= BUFFER_LENGTH;

            for (int i = writeIndex; i < BUFFER_LENGTH; i++) {
                circularBuffer[i] = readValue();
            }

            for (int i = 0; i < substringStartIndex; i++) {
                circularBuffer[i] = readValue();
            }
        } else {
            for (int i = writeIndex; i < substringStartIndex; i++) {
                circularBuffer[i] = readValue();
            }
        }

        // Adjust write index to indicate we have filled spots.
        writeIndex = substringStartIndex;
    }

    private int readValue() {
        try {
            return inputStream.read();
        } catch (IOException e) {
            logger.console("Unexpected error reading data. " + e.getMessage(), Logger.Severity.ERROR);
            return -1;
        }
    }
}
