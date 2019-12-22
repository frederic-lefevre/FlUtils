package com.ibm.lge.fl.util.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class LogRecordAreas {

	private HashMap<Level,ArrayList<LogRecordArea>> logRecordAreasByLevel ;
	
	public LogRecordAreas() {
		logRecordAreasByLevel = new HashMap<Level,ArrayList<LogRecordArea>>() ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		
		Level recordLevel = recordArea.getRecord().getLevel() ;
		ArrayList<LogRecordArea> recordsForTheSameLevel = logRecordAreasByLevel.get(recordLevel) ;
		if (recordsForTheSameLevel == null) {
			recordsForTheSameLevel = new ArrayList<LogRecordArea>() ;
			logRecordAreasByLevel.put(recordLevel, recordsForTheSameLevel) ;
		}
		recordsForTheSameLevel.add(recordArea) ;		
	}
	

}
