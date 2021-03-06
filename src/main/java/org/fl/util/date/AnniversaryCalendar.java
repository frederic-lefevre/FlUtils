package org.fl.util.date;

import java.time.temporal.TemporalAccessor;
import java.time.MonthDay ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnniversaryCalendar <T> {

	private Map<MonthDay,List<T>> anniversaires ; 	
	
	public AnniversaryCalendar()  {
		anniversaires = new HashMap<>() ;
	}

	public MonthDay addAnniversary(T a, TemporalAccessor date) {
		
		try {
			MonthDay monthDay = MonthDay.from(date) ;
			List<T> annivs = anniversaires.get(monthDay) ;
			if (annivs == null) {
				annivs = new ArrayList<T>() ;
				anniversaires.put(monthDay, annivs) ;
			}
			annivs.add(a) ;
			return monthDay ;
		} catch (Exception e) {
			return null ;
		}
	}

	public List<T> getAnniversaries(TemporalAccessor date) {
		try {
			MonthDay monthDay = MonthDay.from(date) ;
			return anniversaires.get(monthDay) ;
		} catch (Exception e) {
			return null ;
		}
	}
	
	public int getNbAnniversaryDate() {
		return anniversaires.size() ;
	}
}
