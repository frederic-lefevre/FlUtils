/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringExtractorTest {

	private final static String DORMEUR_DU_VAL = """
C’est un trou de verdure où chante une rivière,
Accrochant follement aux herbes des haillons 
D’argent ; où le soleil, de la montagne fière,
Luit : c’est un petit val qui mousse de rayons.""";

	private final static String AUX_HERBES = "aux herbes" ;
	private final static String DES 		= " des " 	  ;
	private final static String HAILLONS 	= "haillons"  ;
	private final static String SOLEIL	 	= "solei" 	  ;

	@Test
	void test() {
			
		StringExtractor stringExtractor = new StringExtractor(DORMEUR_DU_VAL);
		
		assertThat(stringExtractor.getCurrentIndex()).isZero();
		
		String des = stringExtractor.extractString(AUX_HERBES, HAILLONS);
		assertThat(des).isEqualTo(DES);
		assertThat(stringExtractor.getCurrentIndex()).isEqualTo(DORMEUR_DU_VAL.indexOf(HAILLONS) + HAILLONS.length());
		
		int sIdx  = stringExtractor.gotoString(SOLEIL);
		int sIdx2 = DORMEUR_DU_VAL.indexOf(SOLEIL);
		assertThat(sIdx).isEqualTo(sIdx2);
		
		int ahIdx = stringExtractor.gotoString(AUX_HERBES);
		assertThat(ahIdx).isEqualTo(-1);
	}

	@Test
	void test2() {
			
		StringExtractor stringExtractor = new StringExtractor(DORMEUR_DU_VAL);
		
		String des = stringExtractor.extractString(AUX_HERBES, HAILLONS, DORMEUR_DU_VAL.indexOf(AUX_HERBES));
		assertThat(des).isEqualTo(DES);
		assertThat(stringExtractor.getCurrentIndex()).isEqualTo(DORMEUR_DU_VAL.indexOf(HAILLONS) + HAILLONS.length());
		
		// you can redo the same extraction ajusting "startIndex" param. current index is rewinded.
		des = stringExtractor.extractString(AUX_HERBES, HAILLONS, DORMEUR_DU_VAL.indexOf(AUX_HERBES));
		assertThat(des).isEqualTo(DES);
		assertThat(stringExtractor.getCurrentIndex()).isEqualTo(DORMEUR_DU_VAL.indexOf(HAILLONS) + HAILLONS.length());

		// you cannot redo the same extraction if you do not adjist the startIndex
		des = stringExtractor.extractString(AUX_HERBES, HAILLONS);
		assertThat(stringExtractor.getCurrentIndex()).isEqualTo(-1);
		assertThat(des).isNull();
	}
}
