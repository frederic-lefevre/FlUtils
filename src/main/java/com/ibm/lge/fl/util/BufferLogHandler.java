package com.ibm.lge.fl.util;


import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BufferLogHandler extends Handler  {
	
	private LogRecordMemoryBuffer logRecordBuffer ;
	private String name ;
	
	public BufferLogHandler(String n, int nbRecords) {
		
		super() ;
		name = n ;
		logRecordBuffer = new LogRecordMemoryBuffer(nbRecords) ;	
	}

	@Override
	public void publish(LogRecord record) {		
		logRecordBuffer.addLogRecord(record) ;
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
		
		return logRecordBuffer.getFormattedRecords() ;
	}
	
	// Get all the logs memorized (in a StringBuilder) and delete them
	public StringBuilder getMemoryAndDeleteLogs() {
		
		return logRecordBuffer.getAndDeleteFormattedRecords() ;
	}
	
	// Delete all the logs in memory
	public int deleteMemoryLogs() {
		
		int nbRemove = logRecordBuffer.logRecordNumber() ;
		logRecordBuffer.clear();
		return nbRemove ;
	}
	
	// Delete all the logs in memory and resize the log buffer
	public int deleteAndResizeMemoryLogs(int nbRecords) {
		
		int nbRemove = logRecordBuffer.logRecordNumber() ;
		logRecordBuffer.clearAndResize(nbRecords);
		return nbRemove ;
	}
	
	// Get the maximum number of log records in memory
	public int getMaxMemoryLogRecord() {
		return logRecordBuffer.getMaxLogRecord() ;
	}
	
	public int inMemoryRemainingCapacityRatio() {
		return (logRecordBuffer.remainingCapacity()*100)/logRecordBuffer.getMaxLogRecord() ;
	}

	public String getName() {
		return name;
	}
}
