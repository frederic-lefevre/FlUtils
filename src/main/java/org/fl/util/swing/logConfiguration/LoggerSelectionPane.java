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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.fl.util.LoggerUtils;
import org.fl.util.swing.GuiTexts;

public class LoggerSelectionPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final ConfigureLoggerPane configureLoggerPane;
	private final DefaultComboBoxModel<String> loggerNamesModel;
	private final JComboBox<String> loggerNameChoice;
	private final JTextField loggersRootField;
	private String selectedLoggerName;
	
	public LoggerSelectionPane(String selectedLoggerName, ConfigureLoggerPane configureLoggerPane) {
		
		super();
		this.configureLoggerPane = configureLoggerPane;
		this.selectedLoggerName = selectedLoggerName;
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));

		JLabel selectLoggerLabel = new JLabel(GuiTexts.getText("appTabbedPane.logConfiguration.selectLogger"));
		Font labelFont = new Font("Verdana", Font.BOLD, 16);
		selectLoggerLabel.setFont(labelFont);
		add(selectLoggerLabel);
		
		Font loggerNamesFont = new Font("Courier New", Font.BOLD, 14);
		loggerNamesModel = new DefaultComboBoxModel<>();
		loggerNameChoice = new JComboBox<String>(loggerNamesModel);
		loggerNameChoice.setFont(loggerNamesFont);
		
		LoggersRootSelectionListener loggersRootSelectionListener = new LoggersRootSelectionListener();
		loggersRootField = new JTextField(80);
		loggersRootField.setFont(loggerNamesFont);
		add(loggersRootField);
		loggersRootField.addActionListener(loggersRootSelectionListener);
		loggersRootField.setText(selectedLoggerName);
		updateLoggersList();
		
		loggerNameChoice.addPopupMenuListener(loggersRootSelectionListener);
		loggerNameChoice.addItemListener(new LoggerSelectionListener());
		
		add(loggerNameChoice);
	}
	
	public String getSelectedLoggerName() {
		return selectedLoggerName;
	}

	private class LoggersRootSelectionListener implements ActionListener,PopupMenuListener  {

		@Override
		public void actionPerformed(ActionEvent e) {
			updateLoggersList();			
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			updateLoggersList();			
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {}
	}
	
	private class  LoggerSelectionListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selectedLoggerName = (String)loggerNameChoice.getSelectedItem();
				configureLoggerPane.setLoggerToBeConfigured(selectedLoggerName);
			}
		}
		
	}
	private void updateLoggersList() {
		
		List<String> applicationLoggerNames = LoggerUtils.getChildLoggerNames(loggersRootField.getText());
		Collections.sort(applicationLoggerNames, String.CASE_INSENSITIVE_ORDER);
		loggerNamesModel.removeAllElements();
		loggerNamesModel.addAll(applicationLoggerNames);
	}
}
