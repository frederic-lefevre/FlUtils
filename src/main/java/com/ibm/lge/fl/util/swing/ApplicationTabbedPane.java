package com.ibm.lge.fl.util.swing;

import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ibm.lge.fl.util.RunningContext;

public class ApplicationTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private ApplicationInfoPane appInfoPane ;
	private LogsDisplayPane		logsDisplayPane ;
	
	private final static Color logHighLight = Color.RED ;
	
	public ApplicationTabbedPane(RunningContext runningContext) {
		super() ;
		
		// Tabbed Panel for application information
		appInfoPane = new ApplicationInfoPane(runningContext) ;
		addTab("Informations", appInfoPane) ;
		
		// Tabbed Panel for logs display
		logsDisplayPane =  new LogsDisplayPane(runningContext.getpLog()) ;
		addTab("Logs display", logsDisplayPane) ;
		
		addChangeListener(new BackUpTabChangeListener());
		
		LogTabColorChanger logTabColorChanger = new LogTabColorChanger() ;
		logsDisplayPane.addHighLightListener(logTabColorChanger) ;
	}
	
	private class BackUpTabChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			if (getSelectedComponent().equals(appInfoPane)) {
				appInfoPane.setInfos();
			}			
		}
	}
	
	private class LogTabColorChanger implements LogHighLightListener {

		@Override
		public void logsHightLighted() {
			int logTabIdx = indexOfComponent(logsDisplayPane) ;
			if (logTabIdx > -1) {
				setBackgroundAt(logTabIdx, logHighLight) ;
			}
		}
		
	}
	

}
