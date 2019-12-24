package com.ibm.lge.fl.util.swing.logPane;

import java.util.ArrayList;

public class LogRecordCategory {

	private final ArrayList<LogRecordArea> records ;
	private int currentResultView ;
		
	public LogRecordCategory() {
		records 		  = new ArrayList<LogRecordArea>() ;
		currentResultView = 0 ;
	}

	public void addLogRecordArea(LogRecordArea recordArea) {
		records.add(recordArea) ;
	}

	public ArrayList<LogRecordArea> getRecords() {
		return records;
	}
	
	// Return the number of the displayed record, starting at 1
	public int displayNextRecord() {
		if ((records != null) && (! records.isEmpty())) {
			currentResultView = (currentResultView + 1)% records.size() ;
			goToResult() ;
		}
		return currentResultView + 1 ;
	}
	
	// Return the number of the displayed record, starting at 1
	public int displayPreviousRecord() {
		if ((records != null) && (! records.isEmpty())) {	
			currentResultView-- ;
			if (currentResultView == -1) {
				currentResultView = records.size() - 1 ;
			}
			goToResult() ;
		}
		return currentResultView + 1 ;
		
	}
	
	private void goToResult() {
		records.get(currentResultView).goToResult();
	}
	
	public int getNumberOfRecords() {
		if ((records != null) && (! records.isEmpty())) {
			return records.size() ;
		} else {
			return 0 ;
		}
	}
}
