package com.ibm.lge.fl.util.swing;

import java.util.logging.LogRecord;

public class LogRecordArea {

	private final LogRecord record ;
	private final int		startPosition ;
	private final int		endPosition ;
	
	public LogRecordArea(LogRecord r, int s, int e) {
		record 		  = r ;
		startPosition = s ;
		endPosition   = e ;
	}

	public LogRecord getRecord() 		{ return record; 		}
	public int 		 getStartPosition() { return startPosition; }
	public int 		 getEndPosition()   { return endPosition; 	}

}
