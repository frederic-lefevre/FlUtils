package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.ibm.lge.fl.util.swing.text.SearchableTextPane;

public class LogsDisplayPane  extends JPanel {

	private static final long serialVersionUID = 1L;

	private final TextAreaLogHandler logTextAreaHandler ;
	private final JTextArea 		 logArea ;
	private final SearchableTextPane searchableTextArea ;
	private final LogRecordCategoriesPane logRecordCategoriesPane ;
	
	public LogsDisplayPane(int level, Color color, Logger logger) {
		
		super();
		setLayout(new BoxLayout(this,  BoxLayout.X_AXIS)) ;
		setBorder(BorderFactory.createLineBorder(Color.BLACK,5,true)) ;
		
		logArea = new JTextArea(50, 120) ;
		logArea.setEditable(false);
		logArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		searchableTextArea = new SearchableTextPane(logArea, logger) ;
		add(searchableTextArea) ;
		
		logTextAreaHandler = new TextAreaLogHandler(logArea, level, color, logger) ;
		logTextAreaHandler.setLevel(logger.getLevel());
		logger.addHandler(logTextAreaHandler);
			
		logRecordCategoriesPane = new LogRecordCategoriesPane(logTextAreaHandler.getLogRecordAreas()) ;
		searchableTextArea.getCommandPanel().add(logRecordCategoriesPane) ;
	}
	
	public void setRowsCols(int rows, int cols) {
		logArea.setColumns(cols) ;
		logArea.setRows(rows) ;
	}
	
	public boolean hasHighlight() {
		return logTextAreaHandler.hasHighlight() ;
	}
	
	public void addHighLightListener(LogHighLightListener highLightListener) {
		logTextAreaHandler.addHighLightListener(highLightListener) ;
	}
	
	public void refreshLogRecordCategories() {
		logRecordCategoriesPane.displayPane();
	}
}
