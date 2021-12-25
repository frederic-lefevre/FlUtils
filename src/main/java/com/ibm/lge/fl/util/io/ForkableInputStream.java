package com.ibm.lge.fl.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ForkableInputStream extends InputStream {

    private final Logger logger;
    private final int readerId;
    private final MultiReaderCircularBuffer<Integer> buffer;
    private final InputStream sourceInputStream;
    private List<ForkedOutputStream> forkedOutputStreams;

    public ForkableInputStream(InputStream sourceInputStream, Logger l) {

        this.sourceInputStream = sourceInputStream;
        this.logger = l;
        this.buffer = new MultiReaderCircularBuffer<>();
        this.readerId = this.buffer.newReadClient();
        this.forkedOutputStreams = new ArrayList<>();
    }

    public ForkableInputStream(InputStream sourceInputStream, int capacity, Logger l) {

        this.sourceInputStream = sourceInputStream;
        this.logger = l;
        this.buffer = new MultiReaderCircularBuffer<>(capacity);
        this.readerId = this.buffer.newReadClient();
        this.forkedOutputStreams = new ArrayList<>();
    }

    // Essentially for test
    protected ForkableInputStream(InputStream sourceInputStream, int capacity, boolean force, Logger l) {

        this.sourceInputStream = sourceInputStream;
        this.logger = l;
        this.buffer = new MultiReaderCircularBuffer<>(capacity, force);
        this.readerId = this.buffer.newReadClient();
        this.forkedOutputStreams = new ArrayList<>();
    }

    public boolean addForkedOutputStream(OutputStream outputStream) {

        int osReaderClient = buffer.newReadClient();
        if (osReaderClient > -1) {
            forkedOutputStreams.add(new ForkedOutputStream(osReaderClient, outputStream));
            return true;
        } else {
            logger.severe("Impossible to fork an output stream: maximal number of fork reached");
            return false;
        }
    }

    @Override
    public int read() throws IOException {

        Integer b = buffer.read(readerId);
        if (b == null) {
            // no bytes available in buffer for this reader
            // fill buffer with source
            int i = sourceInputStream.read();

            if (buffer.write(i)) {
                b = buffer.read(readerId);
            } else {
                // no more space in buffer
                // Make space in sending to output streams
                writeToForkedOutputStreams();
                if (buffer.write(i)) {
                    b = buffer.read(readerId);
                } else {
                    // Still no more space, give up
                    throw new IOException("ForkableInputStream buffer full");
                }
            }
        }
        if (b == -1) {
            // end of source input stream
            writeToForkedOutputStreams();
        }
        return b;
    }

    @Override
    public int available() {
        return (int)Math.min(buffer.available(readerId), Integer.MAX_VALUE);
    }

    private void writeToForkedOutputStreams() throws IOException {

        for (ForkedOutputStream forkedOutputStream : forkedOutputStreams) {
            try (OutputStream outputStream = forkedOutputStream.outputStream ) {
                Integer b = 0;
                while (b != null) {
                    b = buffer.read(forkedOutputStream.readerId);
                    if ((b != null) && (b != -1)) {
                        outputStream.write(b);
                    }
                }
            } catch (IOException e) {
                logger.severe("Exception writing to a forked output stream");
                throw e;
            }
        }
    }

    private static class ForkedOutputStream {

        private final int readerId;
        private OutputStream outputStream;

        public ForkedOutputStream(int readerId, OutputStream outputStream) {
            this.readerId = readerId;
            this.outputStream = outputStream;
        }
    }
}
