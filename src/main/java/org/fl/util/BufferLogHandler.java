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

import java.util.logging.Handler;
import java.util.logging.LogRecord;

// Contrary to java.util.logging.MemoryBuffer, BufferLogHandler has no target handler
// So log records which are not picked on time are lost
public class BufferLogHandler extends Handler {

	private LogRecordMemoryBuffer logRecordBuffer;
	private String name;

	public BufferLogHandler(String n, int nbRecords) {

		super();
		name = n;
		logRecordBuffer = new LogRecordMemoryBuffer(nbRecords);
	}

	@Override
	public void publish(LogRecord record) {
		if (isLoggable(record)) {
			logRecordBuffer.addLogRecord(record);
		}
	}

	@Override
	public void flush() {
		// just clear - nowhere to flush
		logRecordBuffer.clear();
	}

	@Override
	public void close() throws SecurityException {
		// no close operation to implement
	}

	// Get all the logs memorized (in a StringBuilder)
	public StringBuilder getMemoryLogs() {

		return logRecordBuffer.getFormattedRecords();
	}

	// Get all the logs memorized (in a StringBuilder) and delete them
	public StringBuilder getMemoryAndDeleteLogs() {

		return logRecordBuffer.getAndDeleteFormattedRecords();
	}

	// Delete all the logs in memory
	public int deleteMemoryLogs() {

		int nbRemove = logRecordBuffer.logRecordNumber();
		logRecordBuffer.clear();
		return nbRemove;
	}

	// Delete all the logs in memory and resize the log buffer
	public int deleteAndResizeMemoryLogs(int nbRecords) {

		int nbRemove = logRecordBuffer.logRecordNumber();
		logRecordBuffer.clearAndResize(nbRecords);
		return nbRemove;
	}

	// Get the maximum number of log records in memory
	public int getMaxMemoryLogRecord() {
		return logRecordBuffer.getMaxLogRecord();
	}

	public int inMemoryRemainingCapacityRatio() {
		return (logRecordBuffer.remainingCapacity() * 100) / logRecordBuffer.getMaxLogRecord();
	}

	public String getName() {
		return name;
	}
}
