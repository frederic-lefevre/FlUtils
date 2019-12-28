package com.ibm.lge.fl.util.swing;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class ColorHelpersTests {

	@Test
	void testBlue() {

		Color c = ColorHelpers.parse("BLUE") ;
		assertEquals(c, Color.BLUE) ;
		
		c = ColorHelpers.parse("blue") ;
		assertEquals(c, Color.blue) ;

		c = ColorHelpers.parse("Blue") ;
		assertNull(c) ;
	}

	@Test
	void testGreen() {

		Color c = ColorHelpers.parse("GREEN") ;
		assertEquals(c, Color.GREEN) ;
		
		c = ColorHelpers.parse("green") ;
		assertEquals(c, Color.green) ;

		c = ColorHelpers.parse("Green") ;
		assertNull(c) ;
	}
	
	@Test
	void testWrongString() {

		Color c = ColorHelpers.parse("WRONG") ;
		assertNull(c) ;
	}
	
	@Test
	void testRGB() {

		Color c = ColorHelpers.parse("#C6C6C6") ;
		assertNotNull(c) ;
	}
	
	@Test
	void testRGB2() {

		Color c = ColorHelpers.parse("0") ;
		assertNotNull(c) ;
		assertEquals(c, Color.black) ;
	}
	
	@Test
	void testEmpy() {

		Color c = ColorHelpers.parse("") ;
		assertNull(c) ;
	}
	
	@Test
	void testNull() {

		Color c = ColorHelpers.parse(null) ;
		assertNull(c) ;
	}
}
