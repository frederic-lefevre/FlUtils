package com.ibm.lge.fl.util.swing.logPane;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.JTextComponent;

import com.ibm.lge.fl.util.swing.text.TextAreaElementList;

public class LogRecordAreas {

	private final JTextComponent textComponent ;
	private final Logger		 lLog ;
	private final HashMap<Level,TextAreaElementList> logRecordAreas ;
	
	public LogRecordAreas(JTextComponent tc,Logger l) {
		super() ;
		textComponent  = tc ;
		lLog		   = l ;
		logRecordAreas = new HashMap<Level,TextAreaElementList>() ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		
		Level recordLevel = recordArea.getRecord().getLevel() ;
		TextAreaElementList recordsForTheSameLevel = logRecordAreas.get(recordLevel) ;
		if (recordsForTheSameLevel == null) {
			recordsForTheSameLevel = new TextAreaElementList(textComponent, null, lLog) ;
			logRecordAreas.put(recordLevel, recordsForTheSameLevel) ;
		}
		recordsForTheSameLevel.addTextElement(recordArea) ;		
	}
	
	public Set<Level> getRecordLevels() {
		return logRecordAreas.keySet() ;
	}
	
	public TextAreaElementList getLogRecordsForThisLevel(Level level) {
		return logRecordAreas.get(level) ;
	}
}
