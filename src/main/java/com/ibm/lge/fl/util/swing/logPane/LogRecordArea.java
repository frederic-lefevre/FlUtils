package com.ibm.lge.fl.util.swing.logPane;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogRecordArea {

	private final LogRecord record ;
	private final Logger 	rLog ;
	private int	startPosition ;
	private int	endPosition ;
	
	
	public LogRecordArea(LogRecord r, int s, int e, Logger l) {
		record 		  = r ;
		startPosition = s ;
		endPosition   = e ;
		rLog		  = l ;
		if (startPosition >= endPosition) {
			rLog.severe("LogRecordArea created with a start superioor or equal to end");
		}
	}

	public LogRecord getRecord() 		{ return record; 		}
	public int 		 getStartPosition() { return startPosition; }
	public int 		 getEndPosition()   { return endPosition; 	}

	public void moveArea(int move) {
		
		startPosition = startPosition + move ;
		endPosition   = endPosition   + move ;
		
		if (startPosition < 0) {
			rLog.severe("LogRecordArea moved to a negative position");
		}
	}
}
