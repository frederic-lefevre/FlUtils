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
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fl.util.LoggerUtils;
import org.fl.util.RunningContext;

public class LogConfigurationPane extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final RunningContext runningContext;

	
	private final DefaultComboBoxModel<String> loggerNamesModel;
	private final JComboBox<String> loggerNameChoice;
	
	public LogConfigurationPane(RunningContext runningContext) {
		super();

		this.runningContext = runningContext;
		String applicationName = runningContext.getName();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
		
		JPanel selctLoggerPane = new JPanel();
		JLabel selectLoggerLabel = new JLabel("Select the logger");
		selctLoggerPane.add(selectLoggerLabel);
		List<String> applicationLoggerNames = LoggerUtils.getChildLoggerNames(applicationName);
		loggerNamesModel = new DefaultComboBoxModel<>();
		loggerNameChoice = new JComboBox<String>(loggerNamesModel);
		loggerNamesModel.addAll(applicationLoggerNames);
		selctLoggerPane.setPreferredSize(new Dimension(600, 30));
		
		selctLoggerPane.add(loggerNameChoice);
		
		add(selctLoggerPane);
	}
}
