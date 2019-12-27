package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class LogsDisplayPane  extends JPanel {

	private static final long serialVersionUID = 1L;

	private final SearchableLogDisplay searchableLogDisplay ;	
	private final TextAreaLogHandler   logTextAreaHandler ;
	
	public LogsDisplayPane(int level, Color color, Logger logger) {
		
		super();
		setLayout(new BoxLayout(this,  BoxLayout.X_AXIS)) ;
		setBorder(BorderFactory.createLineBorder(Color.BLACK,5,true)) ;
		
		searchableLogDisplay = new SearchableLogDisplay(level, color, logger) ;
		add(searchableLogDisplay.getPanel()) ;
		
		logTextAreaHandler = new TextAreaLogHandler(searchableLogDisplay) ;
		logTextAreaHandler.setLevel(logger.getLevel());
		logger.addHandler(logTextAreaHandler);
	}
	
	public boolean hasHighlight() {
		return searchableLogDisplay.hasHighlight() ;
	}
	
	public void addHighLightListener(LogHighLightListener highLightListener) {
		searchableLogDisplay.addHighLightListener(highLightListener) ;
	}
	
	public void refreshLogRecordCategories() {
		searchableLogDisplay.refreshLogRecordCategories();
	}
}
