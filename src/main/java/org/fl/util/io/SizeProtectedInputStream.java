package org.fl.util.io;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;

public class SizeProtectedInputStream extends FilterInputStream {

	private long receivedBytesCount;

    private final long maxSize;

    public SizeProtectedInputStream(InputStream sourceInputStream, long maxSize) {
        super(sourceInputStream);
        this.maxSize = maxSize;
        receivedBytesCount = 0;
    }

    @Override
    public int read() throws IOException {
        final int byteRead = super.read();
        if (byteRead != -1) {
            receivedBytesCount++;
            checkOverflow();
        }
        return byteRead;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytesRead = super.read(b, off, len);
        if (bytesRead > 0) {
            receivedBytesCount = receivedBytesCount + bytesRead;
            checkOverflow();
        }
        return bytesRead;
    }

    public long getMaxSize() {
        return maxSize;
    }

    private void checkOverflow() throws OverflowIOException {
        if (receivedBytesCount > maxSize) {
            throw new OverflowIOException("Input overflow. Bytes read count=" + receivedBytesCount);
        }
    }
}
