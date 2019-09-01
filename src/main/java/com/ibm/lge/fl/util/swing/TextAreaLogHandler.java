package com.ibm.lge.fl.util.swing;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ibm.lge.fl.util.ExceptionLogging;

public class TextAreaLogHandler  extends Handler {

	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS " ;
	private final static String BLANK 	  = " " ;
	private final static String NEWLINE   = "\n" ;
	private final static String SEPARATOR = ": " ;
	
	private static int MAX_PRINTED_CAUSE_LEVEL = 20 ;
	
	private final JTextArea textArea ;
	private final DateTimeFormatter dateTimeFormatter ;
	 
	public TextAreaLogHandler(JTextArea textArea) {
		super();
		this.textArea = textArea;
		dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern) ;
	}
	
	@Override
	public void publish(LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	
            	textArea.append(dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault()))) ;
            	textArea.append(Long.toString(record.getSequenceNumber())) ;
            	textArea.append(BLANK) ;
            	textArea.append(record.getLoggerName()) ;
            	textArea.append(BLANK) ;
        		String srcClassName = record.getSourceClassName() ;
        		if (srcClassName != null) {
        			textArea.append(record.getSourceClassName()) ;
        			textArea.append(BLANK) ;
        		}
        		String methodName = record.getSourceMethodName() ;
        		if (methodName != null) {
        			textArea.append(record.getSourceMethodName()) ;
        		}
        		textArea.append(NEWLINE) ;
        		textArea.append(record.getLevel().getName()) ;
        		textArea.append(SEPARATOR) ;
        		textArea.append(record.getMessage()) ;
        		textArea.append(NEWLINE) ;
        		
        		Throwable thrown = record.getThrown() ;
        		if (thrown != null) {
        			String thrownMsg = thrown.toString() ;
        			if ((thrownMsg != null) && (! thrownMsg.isEmpty())) {
        				textArea.append(ExceptionLogging.printExceptionInfos(thrown, MAX_PRINTED_CAUSE_LEVEL)) ;
        				textArea.append(NEWLINE) ;
        			}
        		}
        		textArea.append(NEWLINE) ;
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