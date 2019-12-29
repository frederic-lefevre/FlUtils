package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.JTextComponent;

import com.ibm.lge.fl.util.swing.text.TextAreaElement;
import com.ibm.lge.fl.util.swing.text.TextAreaElementList;

public class LogRecordAreas {

	private final JTextComponent 					 textComponent ;
	private final Logger		 					 lLog ;
	private HashMap<Level,TextAreaElementList> 		 logRecordAreas ;
	
	private final int 								 lastNonHighLighedLevel ;
	private final Color 							 colorForHigLevelRecords ;
	private final ArrayList<LogHighLightListener> 	 highLightListeners ;
	
	private boolean hasHighLight ;
	
	public LogRecordAreas(JTextComponent tc, int lvl, Color c, Logger l) {
		textComponent  	   = tc ;
		lLog		   	   = l ;
		logRecordAreas	   = new HashMap<Level,TextAreaElementList>() ;
		highLightListeners = new ArrayList<LogHighLightListener>() ;
		
		lastNonHighLighedLevel 	= lvl ;
		colorForHigLevelRecords	= c ;
		hasHighLight			= false ;
	}
	
	public void addLogRecordArea(TextAreaElement recordArea, Level recordLevel) {
		
		TextAreaElementList recordsForTheSameLevel = logRecordAreas.get(recordLevel) ;
		Color highLightRecord = null ;
		if (recordLevel.intValue() > lastNonHighLighedLevel) {
			highLightRecord = colorForHigLevelRecords ;
		}
		if (recordsForTheSameLevel == null) {			
			recordsForTheSameLevel = new TextAreaElementList(textComponent, recordLevel.getName(), highLightRecord, lLog) ;	    			    	
			logRecordAreas.put(recordLevel, recordsForTheSameLevel) ;
		}
		
		if ((! hasHighLight) && (highLightRecord != null))  {
			
			hasHighLight = true ;
			for (LogHighLightListener highLightListener : highLightListeners) {
				highLightListener.logsHightLighted(true) ;
			}
		}
		recordsForTheSameLevel.addTextElement(recordArea) ;		
	}
	
	public Set<Level> getRecordLevels() {
		return logRecordAreas.keySet() ;
	}
	
	public TextAreaElementList getLogRecordsForThisLevel(Level level) {
		return logRecordAreas.get(level) ;
	}
	
	public void addHighLightListener(LogHighLightListener highLightListener) {
		highLightListeners.add(highLightListener) ;
	}
	
	public boolean hasHighlight() {
		return hasHighLight ;
	}
	
	public void removeHighLight() {
		for (TextAreaElementList elementList : logRecordAreas.values()) {
			elementList.removeHighLights() ;
		}
		for (LogHighLightListener highLightListener : highLightListeners) {
			highLightListener.logsHightLighted(false) ;
		}
		hasHighLight = false ;
	}

	public void clear() {
		removeHighLight() ;
		logRecordAreas 	= new HashMap<Level,TextAreaElementList>() ;		
	}
}
