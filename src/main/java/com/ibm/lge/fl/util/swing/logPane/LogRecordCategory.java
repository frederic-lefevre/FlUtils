package com.ibm.lge.fl.util.swing.logPane;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.text.JTextComponent;

import com.ibm.lge.fl.util.swing.text.TextComponentHelpers;

public class LogRecordCategory {

	private final JTextComponent 		   textComponent ;
	private final ArrayList<LogRecordArea> records ;
	private final Logger 				   lLog ;
	private int currentResultView ;
		
	public LogRecordCategory(JTextComponent tc, Logger l) {
		records 		  = new ArrayList<LogRecordArea>() ;
		currentResultView = 0 ;
		textComponent 	  = tc ;
		lLog 			  = l ;
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
		int end = records.get(currentResultView).getEndPosition() ;
		int begin = records.get(currentResultView).getStartPosition() ;
		TextComponentHelpers.moveTo(textComponent, begin, end, lLog) ;
	}
	
	public int getNumberOfRecords() {
		if ((records != null) && (! records.isEmpty())) {
			return records.size() ;
		} else {
			return 0 ;
		}
	}
}
