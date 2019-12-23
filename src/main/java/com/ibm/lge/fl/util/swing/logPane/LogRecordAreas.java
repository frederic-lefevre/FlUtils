package com.ibm.lge.fl.util.swing.logPane;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class LogRecordAreas extends HashMap<Level,LogRecordCategory> {
	
	private static final long serialVersionUID = 1L;

	public LogRecordAreas() {
		super() ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		
		Level recordLevel = recordArea.getRecord().getLevel() ;
		LogRecordCategory recordsForTheSameLevel = get(recordLevel) ;
		if (recordsForTheSameLevel == null) {
			recordsForTheSameLevel = new LogRecordCategory() ;
			put(recordLevel, recordsForTheSameLevel) ;
		}
		recordsForTheSameLevel.addLogRecordArea(recordArea) ;		
	}
	
	public Set<Level> getRecordLevels() {
		return keySet() ;
	}
	
}
