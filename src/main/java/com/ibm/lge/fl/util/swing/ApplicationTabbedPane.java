package com.ibm.lge.fl.util.swing;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ibm.lge.fl.util.RunningContext;

public class ApplicationTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private ApplicationInfoPane appInfoPane ;
	
	public ApplicationTabbedPane(RunningContext runningContext) {
		super() ;
		
		// Tabbed Panel for application information
		appInfoPane = new ApplicationInfoPane(runningContext) ;
		addTab("Informations", appInfoPane) ;
		
		// Tabbed Panel for logs display
		addTab("Logs display", new LogsDisplayPane(runningContext.getpLog())) ;
		
		addChangeListener(new BackUpTabChangeListener());
	}
	
	private class BackUpTabChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			if (getSelectedComponent().equals(appInfoPane)) {
				appInfoPane.setInfos();
			}			
		}
	}

}
