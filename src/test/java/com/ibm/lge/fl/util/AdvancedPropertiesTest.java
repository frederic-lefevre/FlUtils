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
	
	@Test
	void testDouble() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "-10.2") ;		
		assertEquals("-10.2", advProps.getProperty("p1")) ;
		
		double i = advProps.getDouble("p1", 9.5) ;		
		assertEquals(-10.2, i) ;
	}

	@Test
	void testDouble2() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		Logger noLog = Logger.getLogger("testNoLog") ;
		advProps.setLog(noLog);
		noLog.setLevel(Level.OFF);

		advProps.setProperty("p1", "notAnumber") ;		
		assertEquals("notAnumber", advProps.getProperty("p1")) ;
		
		double i = advProps.getDouble("p1", 1000000000.1) ;		
		assertEquals(1000000000.1, i) ;
	}
	
	@Test
	void testDouble3() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;

		advProps.setProperty("p1", "10.7") ;		
		assertNull(advProps.getProperty("unknown")) ;
		
		double i = advProps.getDouble("unknown", 9.8) ;		
		assertEquals(9.8, i) ;
	}
	
	@Test
	void testBoolean() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "true") ;		
		assertEquals("true", advProps.getProperty("p1")) ;
			
		assertTrue(advProps.getBoolean("p1", false)) ;
	}
	
	@Test
	void testBoolean2() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		Logger noLog = Logger.getLogger("testNoLog") ;
		advProps.setLog(noLog);
		noLog.setLevel(Level.OFF);
		
		advProps.setProperty("p1", "notBool") ;		
		assertEquals("notBool", advProps.getProperty("p1")) ;
			
		assertTrue(advProps.getBoolean("p1", true)) ;
		assertFalse(advProps.getBoolean("p1", false)) ;
	}
	
	@Test
	void testBoolean3() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "True") ;		
		assertEquals("True", advProps.getProperty("p1")) ;
			
		assertTrue(advProps.getBoolean("p1", false)) ;
	}
	
	@Test
	void testBoolean4() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "True") ;		
		assertEquals("True", advProps.getProperty("p1")) ;
			
		assertTrue(advProps.getBoolean("unknown", true)) ;
		assertFalse(advProps.getBoolean("unknown", false)) ;
	}
	
	@Test
	void testChar() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "c") ;		
		assertEquals("c", advProps.getProperty("p1")) ;
			
		char i = advProps.getChar("p1", 'r') ;		
		assertEquals('c', i) ;
	}
	
	@Test
	void testChar2() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "char") ;		
		assertEquals("char", advProps.getProperty("p1")) ;
			
		char i = advProps.getChar("p1", 'r') ;		
		assertEquals('c', i) ;
	}
	
	@Test
	void testChar3() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "c") ;		
		assertEquals("c", advProps.getProperty("p1")) ;
			
		char i = advProps.getChar("unknown", 'r') ;		
		assertEquals('r', i) ;
	}
	
	@Test
	void testChar4() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "") ;		
		assertEquals("", advProps.getProperty("p1")) ;
			
		char i = advProps.getChar("p1", 'r') ;		
		assertEquals('r', i) ;
	}
}
