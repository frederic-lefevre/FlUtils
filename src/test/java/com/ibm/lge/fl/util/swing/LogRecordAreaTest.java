package com.ibm.lge.fl.util.swing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.swing.logPane.LogRecordArea;

class LogRecordAreaTest {

	@Test
	void test() {
		
		LogRecord record = new LogRecord(Level.INFO, "Juste une info");
		
		LogRecordArea lra = new LogRecordArea(record, 10, 20, Logger.getAnonymousLogger()) ;
		
		assertEquals(lra.getEndPosition(),   20) ;
		assertEquals(lra.getStartPosition(), 10) ;

		lra.moveArea(-5);
		
		assertEquals(lra.getEndPosition(),   15) ;
		assertEquals(lra.getStartPosition(),  5) ;

	}

}
