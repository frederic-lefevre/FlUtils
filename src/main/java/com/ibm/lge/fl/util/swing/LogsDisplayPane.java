package com.ibm.lge.fl.util.swing;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogsDisplayPane  extends JPanel{

	private static final long serialVersionUID = 1L;

	private final JScrollPane 		 scrollInfos ;
	private final TextAreaLogHandler logTextAreaHandler ;
	private final JTextArea 		 logArea ;

	public LogsDisplayPane(Logger logger) {
		super();
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK,5,true)) ;
		
		logArea = new JTextArea(50, 120) ;
		logArea.setEditable(false);
		logArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		scrollInfos = new JScrollPane(logArea) ;
		
		logTextAreaHandler = new TextAreaLogHandler(logArea) ;
		logTextAreaHandler.setLevel(logger.getLevel());
		logger.addHandler(logTextAreaHandler);
		
		add(scrollInfos) ;
	}
	
	public void setHighLightColor(Color color) {
		logTextAreaHandler.setHighLightColor(color) ;
	}
	
	public void setLastNonHighLighedLevel(Level level) {
		logTextAreaHandler.setLastNonHighLighedLevel(level) ;
	}
	
	public void setRowsCols(int rows, int cols) {
		logArea.setColumns(cols) ;
		logArea.setRows(rows) ;
	}
	
	public boolean hasHighlight() {
		return logTextAreaHandler.hasHighlight() ;
	}
}
