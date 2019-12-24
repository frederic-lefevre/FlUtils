package com.ibm.lge.fl.util.swing.logPane;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.text.JTextComponent;

import com.ibm.lge.fl.util.swing.text.TextAreaElement;

public class LogRecordArea extends TextAreaElement {

	private final LogRecord 	 record ;
		
	public LogRecordArea(JTextComponent tc, LogRecord r, int s, int e, Logger l) {
		super(tc, s, e, l) ;
		record 		  = r ;
	}

	public LogRecord getRecord() { return record; }
}
