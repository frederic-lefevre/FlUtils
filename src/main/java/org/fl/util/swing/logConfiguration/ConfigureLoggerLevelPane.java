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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fl.util.LoggerUtils;

public class ConfigureLoggerLevelPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private Logger loggerToConfigure;
	
	private final JComboBox<Level> loggerLevelChoice;
	private final JLabel hierarchyLevelLabel;
	
	public ConfigureLoggerLevelPane() {
		
		loggerToConfigure = null;
		
		Font font = new Font("Verdana", Font.BOLD, 14);
		
		JLabel loggerLevelTitle = new JLabel("Logger level");
		loggerLevelTitle.setFont(font);
		add(loggerLevelTitle);
		
		// Combo Box to select logger level
		loggerLevelChoice = new JComboBox<>(LoggerUtils.LEVELS);
		loggerLevelChoice.setSelectedItem(null);
		loggerLevelChoice.addItemListener(new LoggerLevelListener());
		add(loggerLevelChoice);
		
		hierarchyLevelLabel = new JLabel();
		hierarchyLevelLabel.setFont(font);
		add(hierarchyLevelLabel);
	}
	
	public void setLoggerToBeConfigured(Logger logger) {

		if (logger != null) {
			loggerToConfigure = logger;
			
			Level loggerLevel = loggerToConfigure.getLevel();

			loggerLevelChoice.setSelectedItem(loggerLevel);

			if (loggerLevel == null) {
				hierarchyLevelLabel.setText("Level from logger hierarchy: " + LoggerUtils.getLevelFromHierarchy(loggerToConfigure));
			} else {
				hierarchyLevelLabel.setText("");
			}
		}
	}
	
	private class  LoggerLevelListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if ((e.getStateChange() == ItemEvent.SELECTED) && (loggerToConfigure != null)) {
				
				loggerToConfigure.setLevel((Level)(loggerLevelChoice.getSelectedItem()));			
			}			
		}		
	}
}
