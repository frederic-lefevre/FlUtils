/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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

package org.fl.util.swing;

import java.awt.Color;
import java.util.logging.Level;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;

import org.fl.util.swing.logPane.LogHighLightListener;
import org.fl.util.swing.logPane.LogsDisplayPane;

public class ApplicationTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private ApplicationInfoPane appInfoPane ;
	private LogsDisplayPane		logsDisplayPane ;
	
	private Color logTabHighLightColor ;
	private Color logTabRegularColor ;
	
	public ApplicationTabbedPane(RunningContext runningContext) {
		super() ;
		
		AdvancedProperties props = runningContext.getProps();
		int lastNonHighLightedLevel = props.getLevel("appTabbedPane.logging.lastNonHighLighedLevel", Level.INFO).intValue() ;
		
		logTabHighLightColor = props.getColor("appTabbedPane.logging.logTabHighLightColor", Color.RED) ;
		
		Color recordHighLightColor = props.getColor("appTabbedPane.logging.recordHighLightColor", Color.PINK) ;
		
		// Tabbed Panel for application information
		appInfoPane = new ApplicationInfoPane(runningContext) ;
		addTab("Informations", appInfoPane) ;
		
		// Tabbed Panel for logs display
		logsDisplayPane =  new LogsDisplayPane(props, lastNonHighLightedLevel, recordHighLightColor, runningContext.getName()) ;
		addTab("Logs display", logsDisplayPane) ;
		int logTabIdx = indexOfComponent(logsDisplayPane) ;
		if (logTabIdx > -1) {
			logTabRegularColor = getBackgroundAt(logTabIdx) ;
		}
		
		addChangeListener(new BackUpTabChangeListener());
		
		LogTabColorChanger logTabColorChanger = new LogTabColorChanger() ;
		logsDisplayPane.addHighLightListener(logTabColorChanger) ;
	}
	
	private class BackUpTabChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			if (getSelectedComponent().equals(appInfoPane)) {
				appInfoPane.setInfos();
			} else if (getSelectedComponent().equals(logsDisplayPane)) {
				logsDisplayPane.refreshLogRecordCategories() ;
			}
		}
	}
	
	private class LogTabColorChanger implements LogHighLightListener {

		@Override
		public void logsHightLighted(boolean highLight) {
			int logTabIdx = indexOfComponent(logsDisplayPane) ;
			if (logTabIdx > -1) {
				if (highLight) {
					setBackgroundAt(logTabIdx, logTabHighLightColor) ;
				} else {
					setBackgroundAt(logTabIdx, logTabRegularColor) ;
				}
			}
		}
		
	}

	public void setLogTabHighLightColor(Color logHighLightColor) {
		this.logTabHighLightColor = logHighLightColor;
	}
	
}
