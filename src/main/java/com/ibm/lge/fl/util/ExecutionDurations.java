package com.ibm.lge.fl.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

public class ExecutionDurations {

	public final static String THREAD_DURATION = "threadDuration" ;
	public final static String TOTAL_DURATION  = "totalDuration" ;
	
	private String internalProcessName;
	private long now ;
	private long duration ;
	private long recuringCalls ;
	private String totalDuration ;
	private JsonObject durationsJson ;
	private int sequence ;
	private Logger log;
	private Level triggerLevel ;
	
	private long previousTimeStamp ;
	private long startTimeStamp ;
	
	public ExecutionDurations(String ipn) {
		initExecDuration(ipn) ;
	}

	public ExecutionDurations(String ipn, Logger l, Level lvl) {
		initExecDuration(ipn) ;
		setTriggerLevel(l, lvl) ;	}
	
	private void initExecDuration(String ipn) {
		previousTimeStamp   = System.nanoTime() ;
		startTimeStamp		= previousTimeStamp ;
		sequence 			= 0 ;
		recuringCalls		= 0 ;
		internalProcessName = ipn ;
		log 				= null ;
		durationsJson 	    = new JsonObject() ;
		triggerLevel		= Level.OFF ;
	}
	
	public void setTriggerLevel(Logger l, Level lvl) {
		log 				= l ;
		triggerLevel		= lvl ;	
	}
	
	public void callExternalPoint() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now 			  = System.nanoTime() ;
			sequence++ ;
			duration		  = now - previousTimeStamp ;
			previousTimeStamp = now ;
			durationsJson.addProperty(internalProcessName + sequence, formatDuration(duration));
		}
	}
	
	public void returnFromExternalPoint(String pointName) {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now 			  = System.nanoTime() ;
			sequence++ ;
			duration		  = now - previousTimeStamp ;
			previousTimeStamp = now ;
			durationsJson.addProperty(pointName + sequence, formatDuration(duration));
		}
	}
	
	public void intermediatePoint(String pointName) {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now 			  = System.nanoTime() ;
			sequence++ ;
			duration		  = now - previousTimeStamp ;
			previousTimeStamp = now ;
			durationsJson.addProperty(pointName + sequence, formatDuration(duration));
		}
	}
	
	public void endProcedurePoint() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now 			  = System.nanoTime() ;
			sequence++ ;
			duration		  = now - previousTimeStamp ;
			previousTimeStamp = now ;
			durationsJson.addProperty(internalProcessName + sequence, formatDuration(duration));
			duration		  = now - startTimeStamp ;
			totalDuration	  =  formatDuration(duration) ;
			durationsJson.addProperty(TOTAL_DURATION, totalDuration);
		}
	}
		
	public JsonObject getJsonExecutionDuration() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			return durationsJson ;
		} else {
			return new JsonObject() ;
		}
	}
	
	public void addThreadDurations(ExecutionDurations threadDuration) {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			sequence++ ;
			durationsJson.add(THREAD_DURATION + sequence, threadDuration.getJsonExecutionDuration());
		}
	}

	public Level getTriggerLevel() {
		return triggerLevel;
	}
	
	private String formatDuration(long dur) {
		long ms = dur / 1000000 ;
		long ns = dur % 1000000 ;
		return ms + "ms " + ns + "ns" ;
	}

	public String getTotalDuration() {
		return totalDuration;
	}
	
	// For recuring procedure calls, to calculate mean execution duration
	public void startRecurring() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			previousTimeStamp = System.nanoTime() ;
		}
	}
	
	public void stopRecurring() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now 			  = System.nanoTime() ;
			recuringCalls++ ;
			duration		  = duration + (now - previousTimeStamp) ;
		}
	}
	
	public long getMeanRecuringDuration() {
		if (recuringCalls > 0) {
			return duration/recuringCalls ;
		} else {
			return 0 ;
		}
	}
}
