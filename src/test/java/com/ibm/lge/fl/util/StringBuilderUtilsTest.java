package com.ibm.lge.fl.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringBuilderUtilsTest {

	private final static String DORMEUR_DU_VAL = 
			
	"C’est un trou de verdure où chante une rivière,    " +
	"Accrochant follement aux herbes des haillons       " +
	"D’argent ; où le soleil, de la montagne fière,     " +
	"Luit : c’est un petit val qui mousse de rayons.    " ;

	private final static String HERBES = "herbes" ;
	
	@Test
	void test() {
		
		
		StringBuilder buffer = new StringBuilder(DORMEUR_DU_VAL) ;
		
		int idx  = StringBuilderUtils.indexOf(buffer, HERBES, 0, DORMEUR_DU_VAL.length()-1) ;
		int idx2 = buffer.indexOf(HERBES) ;
		
		assertEquals(idx2, idx) ;

	}

}
