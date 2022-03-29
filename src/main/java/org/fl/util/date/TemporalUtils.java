package org.fl.util.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemporalUtils {

	// (Try to) compare 2 TemporalAccessors
	public static int compareTemporal(TemporalAccessor t1, TemporalAccessor t2, Logger log) {
		
		if (t1 == null) {
			if (t2 == null) return 0 ;
			return 1 ;
		} else if (t2 == null) {
			return -1 ;
		} else {
			try {
				
				LocalDateTime d1 = getRoundedLocalDateTime(t1) ;
				LocalDateTime d2 = getRoundedLocalDateTime(t2) ;
				
				if ((d1 != null) && (d2 != null)) {
					return d1.compareTo(d2) ;
				} else {
					log.severe("Cannot convert to LocalDate comparing 2 TemporalAccessor ");
					return 0 ;
				}
				
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception comparing 2 TemporalAccessor ", e);
				return 0 ;
			}
		}
	}
	
	public static LocalDateTime getRoundedLocalDateTime(TemporalAccessor t) {
		
		if (t instanceof LocalDateTime) {
			return (LocalDateTime)t ;
		} else if (t instanceof LocalDate) {
			return ((LocalDate)t).atStartOfDay() ;
		} else if (t instanceof YearMonth) {
			return ((YearMonth)t).atDay(1).atStartOfDay() ;
		} else if (t instanceof Year) {
			return ((Year)t).atDay(1).atStartOfDay() ;
		} else {
			// try to get (some) TemporalField manually 
						
			if (t.isSupported(ChronoField.YEAR)) {
				
				int year =  t.get(ChronoField.YEAR) ;
				
				int month 		 = getRoundedField(t, ChronoField.MONTH_OF_YEAR) ;
				int day 		 = getRoundedField(t, ChronoField.DAY_OF_MONTH) ;
				int hour 		 = getRoundedField(t, ChronoField.HOUR_OF_DAY) ;
				int minute 		 = getRoundedField(t, ChronoField.MINUTE_OF_HOUR) ;
				int second 		 = getRoundedField(t, ChronoField.SECOND_OF_MINUTE) ;
				int nanoOfsecond = getRoundedField(t, ChronoField.NANO_OF_SECOND) ;
				
				return LocalDateTime.of(year, month, day, hour, minute,second, nanoOfsecond)  ;
				
			} else {
				return null ;
			}
		}

	}
	
    public static String printDetails(TemporalAccessor t) {
    	
    	StringBuilder details = new StringBuilder() ;
    	ChronoField[] allFields = ChronoField.values() ;
    	for (ChronoField field : allFields) {
    		if (t.isSupported(field)) {
    			details.append(field.name()).append(" is supported. Value=").append( t.getLong(field)).append("\n");
    		} else {
    			details.append(field.name()).append(" is not supported").append("\n");
    		}
    	}
    	return details.toString() ;
    }
    
    private static int getRoundedField(TemporalAccessor t, ChronoField c) {
    	
    	if (t.isSupported(c)) {
    		return t.get(c) ;
    	} else {
    		return (int)c.range().getMinimum() ;
    	}
    }
}
