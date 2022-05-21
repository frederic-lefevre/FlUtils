package org.fl.util.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForkableInputStream extends InputStream {

    private final Logger logger;
    private final int readerId;
    private final MultiReaderCircularBuffer<Integer> buffer;
    private final InputStream sourceInputStream;
    private final InputStream originInputStream;
    private List<ForkedOutputStream> forkedOutputStreams;
    private long nbBytesRead;

    public ForkableInputStream(InputStream originInputStream, Logger l) {

    	this.originInputStream = originInputStream;

        // As the implementation of read(yte[] b, int off, int len) calls read()
        // there is a high interest in enclosing the source inputStream in a BufferedInputStream
        // There is a small penalty if the source inputStream is already buffered but a great advantage if it is not
        // That is the case for FileInputStream, for instance
    	this.logger = l;
        this.sourceInputStream = new BufferedInputStream(originInputStream);
        this.buffer = new MultiReaderCircularBuffer<>();
        this.readerId = this.buffer.newReadClient();
        this.forkedOutputStreams = new ArrayList<>();
        this.nbBytesRead = 0;
    }

    public ForkableInputStream(InputStream originInputStream, int capacity, Logger l) {

    	this.logger = l;
    	this.originInputStream = originInputStream;
        this.sourceInputStream = new BufferedInputStream(originInputStream);
        this.buffer = new MultiReaderCircularBuffer<>(capacity);
        this.readerId = this.buffer.newReadClient();
        this.forkedOutputStreams = new ArrayList<>();
        this.nbBytesRead = 0;
    }

    // Essentially for test
    protected ForkableInputStream(InputStream originInputStream, int capacity, boolean force, Logger l) {

    	this.logger = l;
    	this.originInputStream = originInputStream;
        this.sourceInputStream = new BufferedInputStream(originInputStream);
        this.buffer = new MultiReaderCircularBuffer<>(capacity, force);
        this.readerId = this.buffer.newReadClient();
        this.forkedOutputStreams = new ArrayList<>();
        this.nbBytesRead = 0;
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
        } else {
            this.nbBytesRead++;
        }
        return b;
    }

    // The read(byte[] b, int off, int len) has to be overridden in case the source InputStream has itself overridden it
    // without calling the read() in its implementation
    // This ensures that all the bytes are going into the MultiReaderCircularBuffer
    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        // Check parameters
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            // end of source inputStream
            // The writeToForkedOutputStreams has already been done in read()
            return -1;
        }

        b[off] = (byte)c;
        int numberOfBytesRead = 1;
        try {
            for (; numberOfBytesRead < len ; numberOfBytesRead++) {
                c = read();

                if (c == -1) {
                    // end of source inputStream
                    // The writeToForkedOutputStreams has already been done in read()
                    break;
                }
                b[off + numberOfBytesRead] = (byte)c;
            }
        } catch (IOException ignored) {
            // Treated as  if it were end of file, according to InputStream contract
        }

        return numberOfBytesRead;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public void close() throws IOException {
        super.close();
        originInputStream.close();
        closeForkedOutputStreams();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(() -> "Number of bytes read on " + this + " : " + this.nbBytesRead);
            for (ForkedOutputStream forkedOutputStream : forkedOutputStreams) {
            	logger.finest(() -> "Number of bytes written on " + forkedOutputStream + " : " + forkedOutputStream.getNbBytesWritten());
            }
        }
    }

    // skip has to be overridden. If not, bytes skipped would be written to outputStreams
    @Override
    public long skip(long n) throws IOException {

        if (n <= 0) {
            return 0;
        }

        long remaining = n;
        while (remaining > 0) {

            // Use read() only, NOT read array
            int b = sourceInputStream.read();
            if (b == -1) {
                // end of source input stream
                break;
            }
            remaining--;
        }
        return n - remaining;
    }

    @Override
    public synchronized void mark(int readlimit) {
        // As bytes are written to outputStream as they are read, mark cannot be supported
    }

    // As bytes are written to outputStream as they are read, mark cannot be supported
    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    // As bytes are written to outputStream as they are read, mark cannot be supported
    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int available() {
        return (int)Math.min(buffer.available(readerId), Integer.MAX_VALUE);
    }

    private void writeToForkedOutputStreams() throws IOException {

        for (ForkedOutputStream forkedOutputStream : forkedOutputStreams) {
                Integer b = 0;
                long nbBytesWritten = 0;
                while (b != null) {
                    b = buffer.read(forkedOutputStream.readerId);
                    if ((b != null) && (b != -1)) {
                        forkedOutputStream.write(b);
                        nbBytesWritten++;
                    }
                }
                forkedOutputStream.addNbBytesWritten(nbBytesWritten);
        }
    }

    private void closeForkedOutputStreams() throws IOException {
        for (ForkedOutputStream forkedOutputStream : forkedOutputStreams) {
            forkedOutputStream.close();
        }
    }

    private static class ForkedOutputStream {

        private final int readerId;
        private final OutputStream outputStream;
        private long nbBytesWritten;

        public ForkedOutputStream(int readerId, OutputStream outputStream) {
            this.readerId = readerId;
            this.outputStream = outputStream;
            this.nbBytesWritten = 0;
        }

        public void addNbBytesWritten(long n) {
            this.nbBytesWritten = this.nbBytesWritten + n;
        }

        public long getNbBytesWritten() {
            return this.nbBytesWritten;
        }

        public void write(int b) throws IOException {
            this.outputStream.write(b);
        }

        public void close() throws IOException {
            this.outputStream.flush();
            this.outputStream.close();
        }
    }
}