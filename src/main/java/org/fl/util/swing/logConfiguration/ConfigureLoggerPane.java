/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util.swing.logConfiguration;


import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConfigureLoggerPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final String TITLE_PREFIX = "Configure logger ";
	
	private static final Level[] LEVELS = new Level[] {
			Level.ALL, 
			Level.FINEST, 
			Level.FINER, 
			Level.FINE, 
			Level.CONFIG, 
			Level.INFO, 
			Level.WARNING, 
			Level.SEVERE, 
			Level.OFF};
	
	private final JLabel configurationTitleLabel;
	
	private final JComboBox<Level> loggerLevelChoice;
	
	public ConfigureLoggerPane() {
		super();
		
		loggerLevelChoice = new JComboBox<>(LEVELS);
		loggerLevelChoice.setSelectedItem(null);
		
		configurationTitleLabel = new JLabel();
		Font font = new Font("Verdana", Font.BOLD, 14);
		configurationTitleLabel.setFont(font);
		add(configurationTitleLabel);
		
		add(new JLabel("Logger level"));
		add(loggerLevelChoice);
	}

	public void setLoggerToBeConfigured(String loggerName) {
		
		if (loggerName != null) {
			
			configurationTitleLabel.setText(TITLE_PREFIX + loggerName);
			
			Logger logger = Logger.getLogger(loggerName);
			
			 Level loggerLevel = logger.getLevel();
			 
			 loggerLevelChoice.setSelectedItem(loggerLevel);
		}
	}
	
}
