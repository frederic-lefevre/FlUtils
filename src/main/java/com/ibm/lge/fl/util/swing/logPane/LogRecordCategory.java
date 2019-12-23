package com.ibm.lge.fl.util.swing.logPane;

import java.util.ArrayList;

public class LogRecordCategory {

	private ArrayList<LogRecordArea> records ;
	private int currentOccurence ;
	
	public LogRecordCategory() {
		records = new ArrayList<LogRecordArea>() ;
		currentOccurence = 0 ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		records.add(recordArea) ;
	}

	public ArrayList<LogRecordArea> getRecords() {
		return records;
	}
	
	
}
