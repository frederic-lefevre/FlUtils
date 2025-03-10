/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugInputStream extends FilterInputStream {

	private long receivedBytesCount;

	private Logger logger;

	protected DebugInputStream(InputStream in, Logger l) {
		super(in);
		this.logger = l;
		logger.info("init debugStream");
		receivedBytesCount = 0;
	}

	@Override
	public int read() throws IOException {

		int b = super.read();
		if ((b == -1) && logger.isLoggable(Level.FINEST)) {
			logger.finest("end of inputStream. Bytes read=" + receivedBytesCount + "\n" + getStackTrace());
		} else {
			logger.info("read=" + b);
			receivedBytesCount++;
		}
		return b;
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		final int bytesRead = super.read(b, off, len);

		if (bytesRead > 0) {
			receivedBytesCount = receivedBytesCount + bytesRead;
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Array read. Nb bytes read=" +  bytesRead);
			}
		} else if (logger.isLoggable(Level.FINEST)) {
			logger.finest("end of inputStream. Bytes read=" + receivedBytesCount + "\n" + getStackTrace());
		}
		return bytesRead;

	}

	private String getStackTrace() {

		StringBuilder stackTraces = new StringBuilder() ;
		StackTraceElement[] stackElems = (new Throwable()).getStackTrace() ;
		if (stackElems != null) {
			for (StackTraceElement stackElem : stackElems) {
				stackTraces.append("\n\t\tat ")
				.append(stackElem.getClassName())
				.append(" ")
				.append(stackElem.getMethodName())
				.append(" (")
				.append(stackElem.getFileName())
				.append(":")
				.append(stackElem.getLineNumber())
				.append(")");

			}
		}
		return stackTraces.toString();
	}
}
