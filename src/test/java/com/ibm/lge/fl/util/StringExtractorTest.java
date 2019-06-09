package com.ibm.lge.fl.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringExtractorTest {

	private final static String DORMEUR_DU_VAL = 
			
	"C’est un trou de verdure où chante une rivière,    " +
	"Accrochant follement aux herbes des haillons       " +
	"D’argent ; où le soleil, de la montagne fière,     " +
	"Luit : c’est un petit val qui mousse de rayons.    " ;

	@Test
	void test() {
			
		StringExtractor stringExtractor = new StringExtractor(DORMEUR_DU_VAL) ;
		
		assertEquals(0, stringExtractor.getCurrentIndex()) ;
	}

}
