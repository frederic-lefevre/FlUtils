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

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ConfigureLoggerPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final String TITLE_PREFIX = "Configure logger ";
	
	private final JLabel configurationTitleLabel;
	private final JLabel loggerNameLabel;
	private final ConfigureLoggerLevelPane configureLoggerLevelPane;
	private final List<Handler> handlerList;
	private final HandlerTableModel handlerTableModel;
	private final HandlerJTable handlerJTable;
	
	private Logger loggerToConfigure;
	
	public ConfigureLoggerPane() {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		loggerToConfigure = null;
		
		// Title
		JPanel configureLoggerTitlePane = new JPanel();
		configurationTitleLabel = new JLabel(TITLE_PREFIX);
		loggerNameLabel = new JLabel();
		Font titleFont = new Font("Verdana", Font.BOLD, 16);
		configurationTitleLabel.setFont(titleFont);
		loggerNameLabel.setFont(titleFont);
		configureLoggerTitlePane.add(configurationTitleLabel);
		configureLoggerTitlePane.add(loggerNameLabel);
		add(configureLoggerTitlePane);
		
		// Logger Level and handler Configuration
		configureLoggerLevelPane = new ConfigureLoggerLevelPane();
		add(configureLoggerLevelPane);
		
		handlerList = new ArrayList<>();
		handlerTableModel = new HandlerTableModel(handlerList);
		handlerJTable = new HandlerJTable(handlerTableModel);
		
		JScrollPane handlersScrollTable = new JScrollPane(handlerJTable);
		handlersScrollTable.setPreferredSize(new Dimension(1800,700));
		
		add(handlersScrollTable);
	}

	public void setLoggerToBeConfigured(String loggerName) {

		handlerList.clear();
		if (loggerName != null) {

			loggerNameLabel.setText("\"" + loggerName + "\"");

			loggerToConfigure = Logger.getLogger(loggerName);

			configureLoggerLevelPane.setLoggerToBeConfigured(loggerToConfigure);
			handlerJTable.setLoggerToBeConfigured(loggerToConfigure);
			
			if (loggerToConfigure.getLevel() != null) {
				for (Handler handler : loggerToConfigure.getHandlers()) {
					handlerList.add(handler);
				}
			}
			
		} else {
			loggerNameLabel.setText("");
		}
		handlerTableModel.fireTableDataChanged();
	}
	
}
