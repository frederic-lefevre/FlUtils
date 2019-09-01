package com.ibm.lge.fl.util.swing;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import com.ibm.lge.fl.util.ExceptionLogging;

public class TextAreaLogHandler extends Handler {

	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS " ;
	private final static String BLANK 	  = " " ;
	private final static String NEWLINE   = "\n" ;
	private final static String SEPARATOR = ": " ;
	
	private static int MAX_PRINTED_CAUSE_LEVEL = 20 ;
	
	private final JTextArea 		textArea ;
	private final DateTimeFormatter dateTimeFormatter ;
	private final Highlighter    	highLighter ;
	 
	private DefaultHighlighter.DefaultHighlightPainter painter ;
	
	public TextAreaLogHandler(JTextArea ta) {
		super();
		textArea 		  = ta;
		dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern) ;
		highLighter 	  = textArea.getHighlighter() ;
		
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.PINK) ;
	}
	
	public void setHightColor(Color color) {
		if (color != null) {
			painter = new DefaultHighlighter.DefaultHighlightPainter(color) ;
		} else {
			painter = null ;
		}
	}
	
	@Override
	public void publish(LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	
            	int startHighlight = -1 ;
            	if ((painter != null) && (record.getLevel().intValue() > Level.INFO.intValue()))  {
            		startHighlight = textArea.getText().length() - 1 ;
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
        		if (startHighlight > -1) {
        			int endHighLight = textArea.getText().length() - 1 ;
        			try {
						highLighter.addHighlight(startHighlight, endHighLight, painter) ;
					} catch (BadLocationException e) {
						painter = null ;
						reportError("Exception in hightlightining", e, ErrorManager.FORMAT_FAILURE) ;
					}
        		}
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
