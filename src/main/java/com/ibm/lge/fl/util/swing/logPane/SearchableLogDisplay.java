package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import com.ibm.lge.fl.util.swing.text.SearchableTextPane;
import com.ibm.lge.fl.util.swing.text.TextAreaElement;

public class SearchableLogDisplay implements LogDisplayComponent  {

	private final JTextArea 		 	  logArea ;
	private final SearchableTextPane 	  searchableTextArea ;
	private final LogRecordCategoriesPane logRecordCategoriesPane ;	
	private final LogRecordAreas 		  logRecordAreas ;
	private final Logger 				  sLog ;
	
	public SearchableLogDisplay(int lastNonHighLighedLevel, Color color, Logger l) {
		
		sLog = l ;
		logArea = new JTextArea(50, 120) ;
		logArea.setEditable(false);
		logArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		searchableTextArea 		= new SearchableTextPane(logArea, sLog) ;		
		logRecordAreas	   		= new LogRecordAreas(logArea, lastNonHighLighedLevel, color, sLog) ;		
		logRecordCategoriesPane = new LogRecordCategoriesPane(logRecordAreas) ;
		searchableTextArea.getCommandPanel().add(logRecordCategoriesPane) ;	
	}

	@Override
	public void appendToText(String s) {
		logArea.append(s);		
	}

	@Override
	public int textLength() {
		return logArea.getText().length();
	}

	@Override
	public void addLogRecord(Level level, int start, int end) {		
		logRecordAreas.addLogRecordArea(new TextAreaElement(logArea, start, end, sLog), level) ;
	}

	@Override
	public JTextComponent getTextComponent() {
		return logArea;
	}
	
	@Override
	public void clear() {
		logArea.setText("") ;
		logRecordAreas.clear() ;
		logRecordCategoriesPane.clear() ;
		searchableTextArea.clear() ;
	}
	
	public boolean hasHighlight() {
		return logRecordAreas.hasHighlight() ;
	}
	
	public void addHighLightListener(LogHighLightListener highLightListener) {
		logRecordAreas.addHighLightListener(highLightListener);
	}
	
	public void refreshLogRecordCategories() {
		logRecordCategoriesPane.displayPane();
	}
	
	public JPanel getPanel() {
		return searchableTextArea ;
	}
}
