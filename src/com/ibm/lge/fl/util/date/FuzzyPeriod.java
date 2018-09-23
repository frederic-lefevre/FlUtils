package com.ibm.lge.fl.util.date;

import java.time.temporal.TemporalAccessor;
import java.util.logging.Logger;

public class FuzzyPeriod {

   	private TemporalAccessor debut ;
   	private TemporalAccessor fin ;
   	
   	private boolean isValid ;
	
	public FuzzyPeriod(TemporalAccessor d, TemporalAccessor f,  Logger fl) {
		
		debut 	= d ;
		fin   	= f ;
		isValid = true ;
		
		if ((debut != null) && (fin != null)) {
			if (TemporalUtils.compareTemporal(debut, fin, fl) > 0) {
				isValid = false ;
				fl.warning("Periode de dates invalides (début après fin)") ;
			}
		} else {
			isValid = false ;
			fl.warning("Periode de dates invalides (début ou fin invalides)") ;
		}

	}

	public TemporalAccessor getDebut() {
		return debut;
	}

	public TemporalAccessor getFin() {
		return fin;
	}

	public void setDebut(TemporalAccessor debut) {
		this.debut = debut;
	}

	public void setFin(TemporalAccessor fin) {
		this.fin = fin;
	}

	public boolean isValid() {
		return isValid ;
	}
}
