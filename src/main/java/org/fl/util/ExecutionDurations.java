/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.ExecutionDurations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ExecutionDurations {

	public final static String THREAD_DURATION = "threadDuration";
	public final static String TOTAL_DURATION = "totalDuration";

	private String internalProcessName;
	private long now;
	private long duration;
	private long recuringCalls;
	private String totalDuration;
	private ObjectNode durationsJson;
	private int sequence;
	private Logger log;
	private Level triggerLevel;

	private long previousTimeStamp;
	private long startTimeStamp;

	public ExecutionDurations(String ipn) {
		initExecDuration(ipn);
	}

	public ExecutionDurations(String ipn, Logger l, Level lvl) {
		initExecDuration(ipn);
		setTriggerLevel(l, lvl);
	}

	private void initExecDuration(String ipn) {
		previousTimeStamp = System.nanoTime();
		startTimeStamp = previousTimeStamp;
		sequence = 0;
		recuringCalls = 0;
		internalProcessName = ipn;
		log = null;
		durationsJson = JsonNodeFactory.instance.objectNode();
		triggerLevel = Level.OFF;
	}

	public void setTriggerLevel(Logger l, Level lvl) {
		log = l;
		triggerLevel = lvl;
	}

	public void callExternalPoint() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now = System.nanoTime();
			sequence++;
			duration = now - previousTimeStamp;
			previousTimeStamp = now;
			durationsJson.put(internalProcessName + sequence, formatDuration(duration));
		}
	}

	public void returnFromExternalPoint(String pointName) {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now = System.nanoTime();
			sequence++;
			duration = now - previousTimeStamp;
			previousTimeStamp = now;
			durationsJson.put(pointName + sequence, formatDuration(duration));
		}
	}

	public void intermediatePoint(String pointName) {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now = System.nanoTime();
			sequence++;
			duration = now - previousTimeStamp;
			previousTimeStamp = now;
			durationsJson.put(pointName + sequence, formatDuration(duration));
		}
	}

	public void endProcedurePoint() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			now = System.nanoTime();
			sequence++;
			duration = now - previousTimeStamp;
			previousTimeStamp = now;
			durationsJson.put(internalProcessName + sequence, formatDuration(duration));
			duration = now - startTimeStamp;
			totalDuration = formatDuration(duration);
			durationsJson.put(TOTAL_DURATION, totalDuration);
		}
	}

	public JsonNode getJsonExecutionDuration() {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			return durationsJson;
		} else {
			return JsonNodeFactory.instance.objectNode();
		}
	}

	public void addThreadDurations(ExecutionDurations threadDuration) {
		if ((log != null) && (log.isLoggable(triggerLevel))) {
			sequence++;
			durationsJson.set(THREAD_DURATION + sequence, threadDuration.getJsonExecutionDuration());
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
