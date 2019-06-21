package com.ibm.lge.fl.util.date;

import static org.junit.jupiter.api.Assertions.*;

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
	
	private static Personne toto = new Personne("2000-01-30") ;
	private static Personne titi = new Personne("2000-01-30") ;
	private static Personne tata = new Personne("2000-01-29") ;
	
	@Test
	void test() {
				
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>() ;
		
		ac.addAnniversary(toto, toto.getBirthDate()) ;
		
		List<Personne> annivs = ac.getAnniversaries(toto.getBirthDate()) ;
		assertEquals(1, annivs.size()) ;
		assertEquals(toto, annivs.get(0)) ;
	}

	@Test
	void test2() {
				
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>() ;
		
		List<Personne> annivs = ac.getAnniversaries(toto.getBirthDate()) ;
		assertNull(annivs) ;
	}
	
	@Test
	void test3() {
				
		AnniversaryCalendar<Personne> ac = new AnniversaryCalendar<Personne>() ;
		
		ac.addAnniversary(titi, titi.getBirthDate()) ;
		ac.addAnniversary(toto, toto.getBirthDate()) ;
		ac.addAnniversary(tata, tata.getBirthDate()) ;
		
		List<Personne> annivs = ac.getAnniversaries(toto.getBirthDate()) ;
		assertEquals(2, annivs.size()) ;
		assertTrue(annivs.contains(toto)) ;
		assertTrue(annivs.contains(titi)) ;
		assertFalse(annivs.contains(tata)) ;
	}
}
