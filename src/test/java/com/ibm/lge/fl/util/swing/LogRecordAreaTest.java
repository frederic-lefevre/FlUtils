package com.ibm.lge.fl.util.swing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JTextArea;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.swing.logPane.LogRecordArea;

class LogRecordAreaTest {

	@Test
	void test() {
		
		LogRecord record = new LogRecord(Level.INFO, "Juste une info");
		
		LogRecordArea lra = new LogRecordArea(new JTextArea(), record, 10, 20, Logger.getAnonymousLogger()) ;
		
		assertEquals(lra.getEnd(),   20) ;
		assertEquals(lra.getBegin(), 10) ;

		lra.moveArea(-5);
		
		assertEquals(lra.getEnd(),   15) ;
		assertEquals(lra.getBegin(),  5) ;

	}

}
