package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ibm.lge.fl.util.ExceptionLogging;
import com.ibm.lge.fl.util.swing.text.TextAreaElement;

public class TextAreaLogHandler extends Handler {

	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS " ;
	private final static String BLANK 	  = " " ;
	private final static String NEWLINE   = "\n" ;
	private final static String SEPARATOR = ": " ;
	
	private static int MAX_PRINTED_CAUSE_LEVEL = 20 ;
	
	private final JTextArea 		textArea ;
	private final DateTimeFormatter dateTimeFormatter ;
	private final Logger			tLog ;
	 
	private LogRecordAreas 			logRecordAreas ;
	
	public TextAreaLogHandler(JTextArea ta, int lastNonHighLighedLevel, Color color, Logger l) {
		super();
		textArea 		  = ta;
		tLog			  = l ;
		dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern) ;

		logRecordAreas			= new LogRecordAreas(textArea, lastNonHighLighedLevel, color, tLog) ;
	}
	
	public JTextArea getTextArea() {
		return textArea;
	}

	public LogRecordAreas getLogRecordAreas() {
		return logRecordAreas;
	}

	@Override
	public void publish(LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	
            	int startRecord ; 
            	int textLength = textArea.getText().length() ;
        		if (textLength > 0) {
        			startRecord = textLength - 1 ;
        		} else {
        			startRecord = 0 ;
        		}
            	
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
        		
        		int endRecord = textArea.getText().length() - 1 ;
        		logRecordAreas.addLogRecordArea(new TextAreaElement(textArea, startRecord, endRecord, tLog), record.getLevel()) ;
            }
		}) ;
	}

	@Override
	public void flush() {		
	}

	@Override
	public void close() throws SecurityException {
	}
	
	public boolean hasHighlight() {
		return logRecordAreas.hasHighlight() ;
	}
	
	public void addHighLightListener(LogHighLightListener highLightListener) {
		logRecordAreas.addHighLightListener(highLightListener);
	}

}
