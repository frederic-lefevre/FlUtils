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
