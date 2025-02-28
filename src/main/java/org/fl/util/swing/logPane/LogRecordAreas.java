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

package org.fl.util.swing.logPane;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.JTextComponent;

import org.fl.util.swing.text.TextAreaElement;
import org.fl.util.swing.text.TextAreaElementList;

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
