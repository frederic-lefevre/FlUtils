package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

public class LogsDisplayPane  extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private final ArrayList<SearchableLogDisplay> searchableLogDisplays ;
	private final TextAreaLogHandler   logTextAreaHandler ;
	
	private int currentLogDisplayIndex ;
	private SearchableLogDisplay currentLogDisplay ;
	
	private Color logTabSelectedColor = Color.GREEN ;
	private Color logTabRegularColor ;

	private final static int LOG_DISPLAY_NUMBER 	= 2 ;
	private final static int LOG_DISPLAY_MAX_LENGTH = 100000 ;
	
	public LogsDisplayPane(int level, Color color, Logger logger) {
		
		super();
		
		searchableLogDisplays = new ArrayList<SearchableLogDisplay>() ;
		for (int i=0; i < LOG_DISPLAY_NUMBER; i++) {
			SearchableLogDisplay logDisplay = new SearchableLogDisplay(level, color, logger) ;
			searchableLogDisplays.add(logDisplay) ;
			add(logDisplay.getPanel()) ;
		}
		currentLogDisplayIndex = 0 ;
		currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex) ;

		logTabRegularColor = getBackgroundAt(0) ;
		selectCurrentLogDisplay() ;
		
		logTextAreaHandler = new TextAreaLogHandler(currentLogDisplay, new SearchLogDisplayChanger()) ;
		logTextAreaHandler.setLevel(logger.getLevel()) ;
		logTextAreaHandler.setLogDisplayMaxLength(LOG_DISPLAY_MAX_LENGTH) ;
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
	
	private void selectCurrentLogDisplay() {
		setSelectedIndex(currentLogDisplayIndex) ;
		int logTabIdx = indexOfComponent(currentLogDisplay.getPanel()) ;
		setBackgroundAt(logTabIdx,logTabSelectedColor) ;
	}
	
	private class SearchLogDisplayChanger implements LogDisplayChanger {

		@Override
		public LogDisplayComponent changeLogDisplayComponent() {
			int logTabIdx = indexOfComponent(currentLogDisplay.getPanel()) ;
			setBackgroundAt(logTabIdx,logTabRegularColor) ;
			currentLogDisplayIndex = (currentLogDisplayIndex + 1) % LOG_DISPLAY_NUMBER ;
			currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex) ;
			currentLogDisplay.clear();
			selectCurrentLogDisplay() ;
			return currentLogDisplay ;
		}
		
	}
}
