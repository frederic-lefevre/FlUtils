package com.ibm.lge.fl.util.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class LogRecordAreas extends HashMap<Level,ArrayList<LogRecordArea>> {
	
	private static final long serialVersionUID = 1L;

	public LogRecordAreas() {
		super() ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		
		Level recordLevel = recordArea.getRecord().getLevel() ;
		ArrayList<LogRecordArea> recordsForTheSameLevel = get(recordLevel) ;
		if (recordsForTheSameLevel == null) {
			recordsForTheSameLevel = new ArrayList<LogRecordArea>() ;
			put(recordLevel, recordsForTheSameLevel) ;
		}
		recordsForTheSameLevel.add(recordArea) ;		
	}
	
	public Set<Level> getRecordLevels() {
		return keySet() ;
	}
	
}
