package com.ibm.lge.fl.util.swing.logPane;

import java.util.logging.Level;

import javax.swing.text.JTextComponent;

public interface LogDisplayComponent {

	// Append a string to the log text
	public void appendToText(String s) ;
	
	// Return the length in character of the log text
	public int textLength() ;
	
	// Add a reference to a logged record with its level, the start and end index in the log text
	public void addLogRecord(Level level, int start, int end) ;
	
	// Get the underlying JTextComponent that contain the log text
	public JTextComponent getTextComponent() ;
}
