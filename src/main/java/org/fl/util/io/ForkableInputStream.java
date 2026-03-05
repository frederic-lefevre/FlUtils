/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util.io;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForkableInputStream extends InputStream {

	private static final int DEFAULT_BUFFER_SIZE = 16384;

	private final Logger logger;
	private final int readerId;
	private final MultiReaderCircularBuffer<Integer> buffer;
	private final InputStream sourceInputStream;
	private final InputStream originInputStream;
	private List<ForkedOutputStream> forkedOutputStreams;
	private long nbBytesRead;
	private boolean isClosed = false;

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

    /**
     * The maximum size of array to allocate
     */
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    
    @Override
    public byte[] readAllBytes() throws IOException {
        return readNBytes(Integer.MAX_VALUE);
    }
    
    // The readNBytes(int len) has to be overridden in case the source InputStream has itself overridden it
    // without calling the read() in its implementation
    // This ensures that all the bytes are going into the MultiReaderCircularBuffer
    @Override
    public byte[] readNBytes(int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                if (nread < buf.length) {
                    buf = Arrays.copyOfRange(buf, 0, nread);
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0)
                break;
            n += count;
        }
        return n;
    }
    
    @Override
    public void close() throws IOException {

    	if (! isClosed) {

    		super.close();
    		originInputStream.close();
    		isClosed = true;
    		closeForkedOutputStreams();
    		if (logger.isLoggable(Level.FINEST)) {
    			logger.finest(() -> "Number of bytes read on " + this + " : " + this.nbBytesRead);
    			for (ForkedOutputStream forkedOutputStream : forkedOutputStreams) {
    				logger.finest(() -> "Number of bytes written on " + forkedOutputStream + " : " + forkedOutputStream.getNbBytesWritten());
    			}
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
    public void skipNBytes(long n) throws IOException {
        while (n > 0) {
            long ns = skip(n);
            if (ns > 0 && ns <= n) {
                // adjust number to skip
                n -= ns;
            } else if (ns == 0) { // no bytes skipped
                // read one byte to check for EOS
                if (read() == -1) {
                    throw new EOFException();
                }
                // one byte read so decrement number to skip
                n--;
            } else { // skipped negative or too many bytes
                throw new IOException("Unable to skip exactly");
            }
        }
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