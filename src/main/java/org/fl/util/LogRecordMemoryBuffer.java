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

package org.fl.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

public class LogRecordMemoryBuffer {

	private static final int MEAN_PRINTED_LOG_RECORD_SIZE = 200;

	private int maxLogRecord;
	private LinkedBlockingQueue<LogRecord> logRecordBuffer;

	private static final PlainLogFormatter logRecordFormatter = new PlainLogFormatter();

	public LogRecordMemoryBuffer(int maxRecord) {

		maxLogRecord = maxRecord;
		logRecordBuffer = new LinkedBlockingQueue<LogRecord>(maxRecord);
	}

	// Add a log record (remove oldest log records to make space if necessary)
	public synchronized void addLogRecord(LogRecord logRecord) {

		// Try to add the log and as long as there is no space, remove the oldest
		// element
		while (!logRecordBuffer.offer(logRecord)) {

			// remove the oldest element
			logRecordBuffer.poll();
		}

	}
	
	public StringBuilder getFormattedRecords() {

		StringBuilder result = new StringBuilder(logRecordBuffer.size() * MEAN_PRINTED_LOG_RECORD_SIZE);
		synchronized (logRecordBuffer) {

			if (logRecordBuffer.size() > 0) {
				for (LogRecord rec : logRecordBuffer) {
					appendLogRecord(result, rec);
				}
			}
		}
		return result;
	}

	public StringBuilder getAndDeleteFormattedRecords() {

		StringBuilder result = new StringBuilder(logRecordBuffer.size() * MEAN_PRINTED_LOG_RECORD_SIZE);
		synchronized (logRecordBuffer) {

			if (logRecordBuffer.size() > 0) {
				LogRecord logRecord;
				while ((logRecord = logRecordBuffer.poll()) != null) {
					result.append(logRecordFormatter.format(logRecord));
				}
			}
		}
		return result;
	}
	
	private void appendLogRecord(StringBuilder lBuff, LogRecord record) {
		lBuff.append(logRecordFormatter.format(record));
	}

	public int remainingCapacity() {
		return logRecordBuffer.remainingCapacity();
	}

	public int logRecordNumber() {
		return logRecordBuffer.size();
	}

	public void clear() {
		logRecordBuffer.clear();
	}
	
	public void clearAndResize(int maxRecord) {
		logRecordBuffer.clear();
		maxLogRecord = maxRecord;
		logRecordBuffer = new LinkedBlockingQueue<LogRecord>(maxRecord);

	}

	public int getMaxLogRecord() {
		return maxLogRecord;
	}
	
	protected String getDatePatternFormat() {
		return logRecordFormatter.getDateFormatPattern();
	}
}
