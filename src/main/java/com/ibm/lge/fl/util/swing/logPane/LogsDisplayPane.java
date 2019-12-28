package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import com.ibm.lge.fl.util.AdvancedProperties;

public class LogsDisplayPane  extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private final ArrayList<SearchableLogDisplay> searchableLogDisplays ;
	private final TextAreaLogHandler   logTextAreaHandler ;
	
	private int oldLogLastNum ;
	private int currentLogDisplayIndex ;
	private SearchableLogDisplay currentLogDisplay ;
	
	private final static Color[] DEFAULT_HIGHLIGHTCOLORS = {Color.CYAN, Color.LIGHT_GRAY, Color.YELLOW, Color.MAGENTA} ;
	
	private Color logTabSelectedColor = Color.GREEN ;
	private Color logTabRegularColor ;

	private final int logDisplaySubTabNumber ;
	private final int logDisplayMaxLength ;
	
	public LogsDisplayPane(AdvancedProperties props, int level, Color color, Logger logger) {
		
		super();
		
		logDisplaySubTabNumber = props.getInt("appTabbedPane.logging.subTabNumber", 3) ;
		logDisplayMaxLength	   = props.getInt("appTabbedPane.logging.logDisplayMaxLength", 100000) ;
		searchableLogDisplays = new ArrayList<SearchableLogDisplay>() ;
		for (int i=0; i < logDisplaySubTabNumber; i++) {
			SearchableLogDisplay logDisplay = new SearchableLogDisplay(level, DEFAULT_HIGHLIGHTCOLORS, color, logger) ;
			searchableLogDisplays.add(logDisplay) ;
			add(logDisplay.getPanel()) ;
		}
		oldLogLastNum = 1 ;
		currentLogDisplayIndex = 0 ;
		currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex) ;

		logTabRegularColor = getBackgroundAt(0) ;
		selectCurrentLogDisplay() ;
		
		logTextAreaHandler = new TextAreaLogHandler(currentLogDisplay, new SearchLogDisplayChanger()) ;
		logTextAreaHandler.setLevel(logger.getLevel()) ;
		logTextAreaHandler.setLogDisplayMaxLength(logDisplayMaxLength) ;
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
		setTitleAt(logTabIdx, "current") ;
	}
	
	private class SearchLogDisplayChanger implements LogDisplayChanger {

		@Override
		public LogDisplayComponent changeLogDisplayComponent() {
			int logTabIdx = indexOfComponent(currentLogDisplay.getPanel()) ;
			setBackgroundAt(logTabIdx,logTabRegularColor) ;
			setTitleAt(logTabIdx, Integer.toString(oldLogLastNum)) ;
			oldLogLastNum++ ;
			currentLogDisplayIndex = (currentLogDisplayIndex + 1) % logDisplaySubTabNumber ;
			currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex) ;
			currentLogDisplay.clear();
			selectCurrentLogDisplay() ;
			return currentLogDisplay ;
		}
		
	}
}
