package com.ibm.lge.fl.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringExtractorTest {

	private final static String DORMEUR_DU_VAL = 
			
	"C’est un trou de verdure où chante une rivière,    " +
	"Accrochant follement aux herbes des haillons       " +
	"D’argent ; où le soleil, de la montagne fière,     " +
	"Luit : c’est un petit val qui mousse de rayons.    " ;

	private final static String AUX_HERBES = "aux herbes" ;
	private final static String DES 		= " des " 	  ;
	private final static String HAILLONS 	= "haillons"  ;
	private final static String SOLEIL	 	= "solei" 	  ;

	@Test
	void test() {
			
		StringExtractor stringExtractor = new StringExtractor(DORMEUR_DU_VAL) ;
		
		assertEquals(0, stringExtractor.getCurrentIndex()) ;
		
		String des = stringExtractor.extractString(AUX_HERBES, HAILLONS) ;
		assertEquals(DES, des) ;
		assertEquals(DORMEUR_DU_VAL.indexOf(HAILLONS) + HAILLONS.length(), stringExtractor.getCurrentIndex()) ;
		
		int sIdx  = stringExtractor.gotoString(SOLEIL) ;
		int sIdx2 = DORMEUR_DU_VAL.indexOf(SOLEIL) ;
		assertEquals(sIdx2, sIdx) ;
		
		int ahIdx = stringExtractor.gotoString(AUX_HERBES) ;		
		assertEquals(-1, ahIdx) ;
	}

	@Test
	void test2() {
			
		StringExtractor stringExtractor = new StringExtractor(DORMEUR_DU_VAL) ;
		
		String des = stringExtractor.extractString(AUX_HERBES, HAILLONS, DORMEUR_DU_VAL.indexOf(AUX_HERBES)) ;
		assertEquals(DES, des) ;
		assertEquals(DORMEUR_DU_VAL.indexOf(HAILLONS) + HAILLONS.length(), stringExtractor.getCurrentIndex()) ;
		
		// you can redo the same extraction ajusting "startIndex" param. current index is rewinded.
		des = stringExtractor.extractString(AUX_HERBES, HAILLONS, DORMEUR_DU_VAL.indexOf(AUX_HERBES)) ;
		assertEquals(DES, des) ;
		assertEquals(DORMEUR_DU_VAL.indexOf(HAILLONS) + HAILLONS.length(), stringExtractor.getCurrentIndex()) ;

		// you cannot redo the same extraction if you do not adjist the startIndex
		des = stringExtractor.extractString(AUX_HERBES, HAILLONS) ;
		assertEquals(-1, stringExtractor.getCurrentIndex()) ;
		assertNull(des) ;
	}
}
