package com.ibm.lge.fl.util.swing.logPane;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class LogRecordAreas {

	private final HashMap<Level,LogRecordCategory> logRecordAreas ;
	
	public LogRecordAreas() {
		super() ;
		logRecordAreas = new HashMap<Level,LogRecordCategory>() ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		
		Level recordLevel = recordArea.getRecord().getLevel() ;
		LogRecordCategory recordsForTheSameLevel = logRecordAreas.get(recordLevel) ;
		if (recordsForTheSameLevel == null) {
			recordsForTheSameLevel = new LogRecordCategory() ;
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
