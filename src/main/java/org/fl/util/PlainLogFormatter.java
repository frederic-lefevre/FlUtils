package org.fl.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class PlainLogFormatter extends Formatter {

	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS " ;
	
	private final static String BLANK 	  = " " ;
	private final static String NEWLINE   = "\n" ;
	private final static String SEPARATOR = ": " ;
	private static int MAX_PRINTED_CAUSE_LEVEL = 20 ;
	private static int MIN_PRINTED_LOG_RECORD_SIZE = 128 ;
	
	private DateTimeFormatter dateTimeFormatter ;
	
	public PlainLogFormatter() {
		super() ;
		dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern) ;
	}

	@Override
	public String format(LogRecord record) {
		
		int recordAllocSize = MIN_PRINTED_LOG_RECORD_SIZE ;
		String msg = record.getMessage() ;
		recordAllocSize = recordAllocSize + msg.length() ;
		
		String exceptionMsg = null ;
		Throwable thrown = record.getThrown() ;
		if (thrown != null) {
			String thrownMsg = thrown.toString() ;
			if ((thrownMsg != null) && (! thrownMsg.isEmpty())) {
				exceptionMsg = ExceptionLogging.printExceptionInfos(thrown, MAX_PRINTED_CAUSE_LEVEL) ;
				recordAllocSize = recordAllocSize + exceptionMsg.length() ;
			}
		}
		
		// StringBuilder is always converting its argument to a String, even if it is a char
		// so it is better to always append String
		StringBuilder lBuff = new StringBuilder(recordAllocSize) ;
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
		lBuff.append(msg).append(NEWLINE) ;
		
		if (exceptionMsg != null) {			
				lBuff.append(exceptionMsg).append(NEWLINE) ;			
		}
		lBuff.append(NEWLINE) ;
		
		return lBuff.toString() ;
	}
}
