package com.ibm.lge.fl.util.swing.logPane;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.SwingUtilities;

import com.ibm.lge.fl.util.ExceptionLogging;

public class TextAreaLogHandler extends Handler {

	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS " ;
	private final static String BLANK 	  = " " ;
	private final static String NEWLINE   = "\n" ;
	private final static String SEPARATOR = ": " ;
	
	private static int MAX_PRINTED_CAUSE_LEVEL = 20 ;
	
	private final LogDisplayComponent logDisplayComponent ;
	private final DateTimeFormatter   dateTimeFormatter ;
	
	public TextAreaLogHandler(LogDisplayComponent ldc) {
		super();
		logDisplayComponent = ldc;
		dateTimeFormatter   = DateTimeFormatter.ofPattern(datePattern) ;
	}

	@Override
	public void publish(LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	
            	int startRecord ; 
            	int textLength = logDisplayComponent.textLength() ;
        		if (textLength > 0) {
        			startRecord = textLength - 1 ;
        		} else {
        			startRecord = 0 ;
        		}
            	
        		logDisplayComponent.appendToText(dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault()))) ;
        		logDisplayComponent.appendToText(Long.toString(record.getSequenceNumber())) ;
        		logDisplayComponent.appendToText(BLANK) ;
        		logDisplayComponent.appendToText(record.getLoggerName()) ;
        		logDisplayComponent.appendToText(BLANK) ;
        		String srcClassName = record.getSourceClassName() ;
        		if (srcClassName != null) {
        			logDisplayComponent.appendToText(record.getSourceClassName()) ;
        			logDisplayComponent.appendToText(BLANK) ;
        		}
        		String methodName = record.getSourceMethodName() ;
        		if (methodName != null) {
        			logDisplayComponent.appendToText(record.getSourceMethodName()) ;
        		}
        		logDisplayComponent.appendToText(NEWLINE) ;
        		logDisplayComponent.appendToText(record.getLevel().getName()) ;
        		logDisplayComponent.appendToText(SEPARATOR) ;
        		logDisplayComponent.appendToText(record.getMessage()) ;
        		logDisplayComponent.appendToText(NEWLINE) ;
        		
        		Throwable thrown = record.getThrown() ;
        		if (thrown != null) {
        			String thrownMsg = thrown.toString() ;
        			if ((thrownMsg != null) && (! thrownMsg.isEmpty())) {
        				logDisplayComponent.appendToText(ExceptionLogging.printExceptionInfos(thrown, MAX_PRINTED_CAUSE_LEVEL)) ;
        				logDisplayComponent.appendToText(NEWLINE) ;
        			}
        		}
        		logDisplayComponent.appendToText(NEWLINE) ;
        		
        		int endRecord = logDisplayComponent.textLength() - 1 ;
        		logDisplayComponent.addLogRecord(record.getLevel(), startRecord, endRecord);
        	}
		}) ;
	}

	@Override
	public void flush() {		
	}

	@Override
	public void close() throws SecurityException {
	}	
}
