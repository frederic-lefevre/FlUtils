package com.ibm.lge.fl.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class AdvancedPropertiesTest {

	@Test
	void testInt() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "10") ;		
		assertEquals("10", advProps.getProperty("p1")) ;
		
		int i = advProps.getInt("p1", 9) ;		
		assertEquals(10, i) ;
	}

	@Test
	void testInt2() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "10") ;		
		assertNull(advProps.getProperty("unknown")) ;
		
		int i = advProps.getInt("unknown", 9) ;		
		assertEquals(9, i) ;
	}
	
	@Test
	void testInt3() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		Logger noLog = Logger.getLogger("testNoLog") ;
		advProps.setLog(noLog);
		noLog.setLevel(Level.OFF);

		advProps.setProperty("p1", "notAnumber") ;		
		assertEquals("notAnumber", advProps.getProperty("p1")) ;
		
		int i = advProps.getInt("p1", 9) ;		
		assertEquals(9, i) ;
	}
	
	@Test
	void testLong() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "10") ;		
		assertEquals("10", advProps.getProperty("p1")) ;
		
		long i = advProps.getLong("p1", 9) ;		
		assertEquals(10, i) ;
	}

	@Test
	void testLong2() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		Logger noLog = Logger.getLogger("testNoLog") ;
		advProps.setLog(noLog);
		noLog.setLevel(Level.OFF);

		advProps.setProperty("p1", "notAnumber") ;		
		assertEquals("notAnumber", advProps.getProperty("p1")) ;
		
		long i = advProps.getLong("p1", 1000000000) ;		
		assertEquals(1000000000, i) ;
	}
	
	@Test
	void testLong3() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;

		advProps.setProperty("p1", "10") ;		
		assertNull(advProps.getProperty("unknown")) ;
		
		long i = advProps.getLong("unknown", 9) ;		
		assertEquals(9, i) ;
	}
}
