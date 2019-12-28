package com.ibm.lge.fl.util.swing;

import java.awt.Color;
import java.util.logging.Level;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.RunningContext;
import com.ibm.lge.fl.util.swing.logPane.LogHighLightListener;
import com.ibm.lge.fl.util.swing.logPane.LogsDisplayPane;

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
		logsDisplayPane =  new LogsDisplayPane(props, lastNonHighLightedLevel, recordHighLightColor, runningContext.getpLog()) ;
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
