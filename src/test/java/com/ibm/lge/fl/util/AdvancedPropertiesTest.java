package com.ibm.lge.fl.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class AdvancedPropertiesTest {

	@Test
	void testKeys() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("a.b.c.k2.z", "s2") ;
		advProps.setProperty("a.b.c.k1.e", "s1") ;
		advProps.setProperty("a.b.c.k4", "s4") ;
		advProps.setProperty("a.b.c.k3.t.v", "s3") ;
		
		assertEquals("s1", advProps.getProperty("a.b.c.k1.e")) ;
		assertEquals("s2", advProps.getProperty("a.b.c.k2.z")) ;
		assertEquals("s3", advProps.getProperty("a.b.c.k3.t.v")) ;
		assertEquals("s4", advProps.getProperty("a.b.c.k4")) ;
		
		ArrayList<String> keys = advProps.getKeysElements("a.b.c.") ;
		assertEquals(4, keys.size()) ;
		assertEquals("k1", keys.get(0)) ;
		assertEquals("k2", keys.get(1)) ;
		assertEquals("k3", keys.get(2)) ;
		assertEquals("k4", keys.get(3)) ;
	}

	@Test
	void testKeys2() {
		
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "10") ;	
		
		ArrayList<String> keys = advProps.getKeysElements("a.b.c.") ;
		assertNotNull(keys) ;
		assertEquals(0, keys.size()) ;
		
		keys = advProps.getKeysElements("a.b") ;
		assertNotNull(keys) ;
		assertEquals(0, keys.size()) ;
		
		keys = advProps.getKeysElements(".") ;
		assertNotNull(keys) ;
		assertEquals(0, keys.size()) ;
		
		keys = advProps.getKeysElements("") ;
		assertNotNull(keys) ;
		assertEquals(1, keys.size()) ;
		assertEquals("p1", keys.get(0)) ;
	}

	@Test
	void testKeys3() {
		AdvancedProperties advProps = new AdvancedProperties() ;
		advProps.setProperty("p1", "10") ;
		
		assertThrows(NullPointerException.class, () -> advProps.getKeysElements(null)) ;
	}

	
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
