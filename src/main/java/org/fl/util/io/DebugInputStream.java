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
