package com.ibm.lge.fl.util.date;

import java.time.temporal.TemporalAccessor;
import java.time.MonthDay ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnniversaryCalendar <T> {

	private HashMap<MonthDay,List<T>> anniversaires ; 	
	
	public AnniversaryCalendar()  {
		anniversaires = new HashMap<MonthDay,List<T>>() ;
	}

	public void addAnniversary(T a, TemporalAccessor date) {
		
		MonthDay monthDay = MonthDay.from(date) ;
		List<T> annivs = anniversaires.get(monthDay) ;
		if (annivs == null) {
			annivs = new ArrayList<T>() ;
			anniversaires.put(monthDay, annivs) ;
		}
		annivs.add(a) ;
	}

	public List<T> getAnniversaries(TemporalAccessor date) {
		return anniversaires.get(MonthDay.from(date)) ;
	}
}
