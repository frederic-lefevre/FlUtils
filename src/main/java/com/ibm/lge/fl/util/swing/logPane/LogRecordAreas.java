package com.ibm.lge.fl.util.swing.logPane;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.JTextComponent;

public class LogRecordAreas {

	private final JTextComponent 				   textComponent ;
	private final HashMap<Level,LogRecordCategory> logRecordAreas ;
	private final Logger 				     	   lLog ;
	
	public LogRecordAreas(JTextComponent tc, Logger l) {
		super() ;
		textComponent  = tc ;
		lLog		   = l ;
		logRecordAreas = new HashMap<Level,LogRecordCategory>() ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		
		Level recordLevel = recordArea.getRecord().getLevel() ;
		LogRecordCategory recordsForTheSameLevel = logRecordAreas.get(recordLevel) ;
		if (recordsForTheSameLevel == null) {
			recordsForTheSameLevel = new LogRecordCategory(textComponent, lLog) ;
			logRecordAreas.put(recordLevel, recordsForTheSameLevel) ;
		}
		recordsForTheSameLevel.addLogRecordArea(recordArea) ;		
	}
	
	public Set<Level> getRecordLevels() {
		return logRecordAreas.keySet() ;
	}
	
	public LogRecordCategory getLogRecordsForThisLevel(Level level) {
		return logRecordAreas.get(level) ;
	}
}
