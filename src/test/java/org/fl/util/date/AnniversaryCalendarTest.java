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

package org.fl.util.date;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class AnniversaryCalendarTest {

	private final static String datePatternParse  = "uuuu[-MM[-dd]]" ;
   	private final static DateTimeFormatter dateTimeParser    = DateTimeFormatter.ofPattern(datePatternParse,  Locale.FRANCE).withResolverStyle(ResolverStyle.STRICT) ;

	private static class Personne {
		
		public TemporalAccessor getBirthDate() { return birthDate; }
		
		public Personne(String birthDate) {
			super();
			this.birthDate =  dateTimeParser.parseBest(birthDate, LocalDate::from, YearMonth::from, Year::from) ;
		}
		private TemporalAccessor birthDate ;
	}
	
	private static Personne toto = new Personne("2000-01-30");
	private static Personne titi = new Personne("2000-01-30");
	private static Personne tata = new Personne("2000-01-29");
	private static Personne tutu = new Personne("2005-01-30");
	private static Personne bad1 = new Personne("1954");
	
	@Test
	void test() {
				
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>() ;
		
		ac.addAnniversary(toto, toto.getBirthDate()) ;
		
		List<Personne> annivs = ac.getAnniversaries(toto.getBirthDate());
		assertThat(annivs).isNotNull()
			.singleElement()
			.matches(pers -> pers.equals(toto));
	}

	@Test
	void test2() {
				
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>();
		
		List<Personne> annivs = ac.getAnniversaries(toto.getBirthDate());
		assertThat(annivs).isNull();
	}
	
	@Test
	void test3() {
				
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>();

		ac.addAnniversary(titi, titi.getBirthDate());
		ac.addAnniversary(toto, toto.getBirthDate());
		ac.addAnniversary(tata, tata.getBirthDate());

		List<Personne> annivs = ac.getAnniversaries(toto.getBirthDate());
		assertThat(annivs).hasSize(2).hasSameElementsAs(List.of(toto, titi));
	}
	
	@Test
	void test4() {
				
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>();
		
		ac.addAnniversary(titi, titi.getBirthDate());
		ac.addAnniversary(toto, toto.getBirthDate());
		
		List<Personne> annivs = ac.getAnniversaries(tutu.getBirthDate());
		assertThat(annivs).hasSize(2).hasSameElementsAs(List.of(toto, titi));
	}
	
	@Test
	void test5() {
		
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>() ;
		
		ac.addAnniversary(bad1, bad1.getBirthDate());
		assertThat(ac.getNbAnniversaryDate()).isZero();
	}
}
