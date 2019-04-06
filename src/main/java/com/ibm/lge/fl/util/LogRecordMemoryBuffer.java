package com.ibm.lge.fl.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

public class LogRecordMemoryBuffer {

	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS " ;
	private final static String BLANK 	  = " " ;
	private final static String NEWLINE   = "\n" ;
	private final static String SEPARATOR = ": " ;
	
	private static int MAX_PRINTED_CAUSE_LEVEL = 20 ;
	
	private static int MEAN_PRINTED_LOG_RECORD_SIZE = 200 ;
	
	int maxLogRecord ;
	
	private LinkedBlockingQueue<LogRecord> logRecordBuffer ;
	
	private DateTimeFormatter dateTimeFormatter ;
	
	public LogRecordMemoryBuffer(int maxRecord) {
		
		maxLogRecord = maxRecord ;
		dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern) ;
		logRecordBuffer = new LinkedBlockingQueue<LogRecord>(maxRecord) ;
	}
	
	// Add a log record (remove oldest log records to make space if necessary)
	public synchronized void addLogRecord(LogRecord logRecord) {
		
		// Try to add the log and as long as there is no space, remove the oldest element
		while (!logRecordBuffer.offer(logRecord)) {
			 
			// remove the oldest element
			logRecordBuffer.poll() ;
		}
		
	}
	
	public StringBuilder getFormattedRecords() {
		
		StringBuilder result = new StringBuilder(logRecordBuffer.size()*MEAN_PRINTED_LOG_RECORD_SIZE) ;
		synchronized(logRecordBuffer) {
			
			if (logRecordBuffer.size() > 0) {
				for (LogRecord rec : logRecordBuffer) {
					appendLogRecord(result, rec) ;
				}
			}
		}
		return result ;
	}
	
	public StringBuilder getAndDeleteFormattedRecords() {
		
		StringBuilder result = new StringBuilder(logRecordBuffer.size()*MEAN_PRINTED_LOG_RECORD_SIZE) ;
		synchronized(logRecordBuffer) {
			
			if (logRecordBuffer.size() > 0) {
				LogRecord recd ;
				while ((recd = logRecordBuffer.poll()) != null) {
					appendLogRecord(result, recd) ;
				}
			}
		}
		return result ;
	}
	
	private void appendLogRecord(StringBuilder lBuff, LogRecord record) {
		// StringBuilder is always converting its argument to a String, even if it is a char
		// so it is better to always append String
		
		lBuff.append(dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault()))) ;
		lBuff.append(record.getSequenceNumber()).append(BLANK) ;
		lBuff.append(record.getLoggerName()).append(BLANK) ;
		String srcClassName = record.getSourceClassName() ;
		if (srcClassName != null) {
			lBuff.append(record.getSourceClassName()).append(BLANK) ;
		}
		String methodName = record.getSourceMethodName() ;
		if (methodName != null) {
			lBuff.append(record.getSourceMethodName()) ;
		}
		lBuff.append(NEWLINE) ;
		lBuff.append(record.getLevel().getName()).append(SEPARATOR) ;
		lBuff.append(record.getMessage()).append(NEWLINE) ;
		
		Throwable thrown = record.getThrown() ;
		if (thrown != null) {
			String thrownMsg = thrown.toString() ;
			if ((thrownMsg != null) && (! thrownMsg.isEmpty())) {
				lBuff.append(ExceptionLogging.printExceptionInfos(thrown, MAX_PRINTED_CAUSE_LEVEL)).append(NEWLINE) ;
			}
		}
		lBuff.append(NEWLINE) ;
	}

	public int remainingCapacity() {
		return logRecordBuffer.remainingCapacity() ;
	}
	
	public int logRecordNumber() {
		return logRecordBuffer.size() ;
	}
	
	public void clear() {
		logRecordBuffer.clear();
	}
	
	public void clearAndResize(int maxRecord) {
		logRecordBuffer.clear();
		maxLogRecord = maxRecord ;
		logRecordBuffer = new LinkedBlockingQueue<LogRecord>(maxRecord) ;

	}

	public int getMaxLogRecord() {
		return maxLogRecord;
	}
}
