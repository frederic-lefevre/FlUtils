package org.fl.util.os;

public class Chronometre {

	private boolean running ;
	private long 	lastStartTime ;
	private long 	previousValue ;
	private long 	lastGetTime;
	
	public Chronometre() {
		running 	  = false ;
		previousValue = 0 ;
	}

	public void start() {
		lastStartTime = System.currentTimeMillis() ;
		lastGetTime   = lastStartTime ;
		running 	  = true ;
	}

	public long getValue() {
		if (running) {
			lastGetTime = System.currentTimeMillis() ;
			return previousValue + (lastGetTime - lastStartTime) ;
		} else {
			return previousValue;
		}
	}
	
	public long getDeltaValue() {
		if (running) {
			long t = lastGetTime ;
			lastGetTime = System.currentTimeMillis()  ;
			return (lastGetTime - t) ;
		} else {
			return 0;
		}
	}
	
	public long pause() {
		if (running) {
			lastGetTime = System.currentTimeMillis() ;
			previousValue = previousValue + (lastGetTime - lastStartTime) ;
			running = false ;
		}
		return previousValue ;
	}
}
