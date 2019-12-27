package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class LogsDisplayPane  extends JPanel {

	private static final long serialVersionUID = 1L;

	private final ArrayList<SearchableLogDisplay> searchableLogDisplays ;
	private final TextAreaLogHandler   logTextAreaHandler ;
	
	private int currentLogDisplayIndex ;
	private SearchableLogDisplay currentLogDisplay ;
	
	private final static int LOG_DISPLAY_NUMBER = 2 ;
	
	public LogsDisplayPane(int level, Color color, Logger logger) {
		
		super();
		setLayout(new BoxLayout(this,  BoxLayout.X_AXIS)) ;
		setBorder(BorderFactory.createLineBorder(Color.BLACK,5,true)) ;
		
		searchableLogDisplays = new ArrayList<SearchableLogDisplay>() ;
		for (int i=0; i < LOG_DISPLAY_NUMBER; i++) {
			searchableLogDisplays.add(new SearchableLogDisplay(level, color, logger)) ;
		}
		currentLogDisplayIndex = 0 ;
		currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex) ;

		add(currentLogDisplay.getPanel()) ;
		
		logTextAreaHandler = new TextAreaLogHandler(currentLogDisplay, new SearchLogDisplayChanger()) ;
		logTextAreaHandler.setLevel(logger.getLevel());
		logger.addHandler(logTextAreaHandler);
	}
	
	public boolean hasHighlight() {
		return currentLogDisplay.hasHighlight() ;
	}
	
	public void addHighLightListener(LogHighLightListener highLightListener) {
		for (SearchableLogDisplay logDisplay : searchableLogDisplays) {
			logDisplay.addHighLightListener(highLightListener) ;
		}
	}
	
	public void refreshLogRecordCategories() {
		currentLogDisplay.refreshLogRecordCategories();
	}
	
	private class SearchLogDisplayChanger implements LogDisplayChanger {

		@Override
		public LogDisplayComponent changeLogDisplayComponent() {
			remove(currentLogDisplay.getPanel());
			currentLogDisplayIndex = (currentLogDisplayIndex + 1) % LOG_DISPLAY_NUMBER ;
			currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex) ;
			currentLogDisplay.clear();
			add(currentLogDisplay.getPanel()) ;
			return currentLogDisplay ;
		}
		
	}
}
