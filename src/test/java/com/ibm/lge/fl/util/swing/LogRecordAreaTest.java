package com.ibm.lge.fl.util.swing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

class LogRecordAreaTest {

	@Test
	void test() {
		
		LogRecord record = new LogRecord(Level.INFO, "Juste une info");
		
		LogRecordArea lra = new LogRecordArea(record, 10, 20) ;
		
		int begin = lra.getStartPosition() ;
		int end   = lra.getEndPosition() ;
		
		assertEquals(end,   20) ;
		assertEquals(begin, 10) ;

	}

}
