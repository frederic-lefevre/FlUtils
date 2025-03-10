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

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;
import javax.swing.JTextField;

public class CreateFileHandlerPane extends CreateHandlerPane {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_LOG_FILE_PATTERN = "/tmp/yourApp/app%g.log";
	private static final Long DEFAULT_LOG_FILE_SIZE = 80000L;
	private static final Integer DEFAULT_NUMBER_OF_LOG_FILE = 3;
	
	private final JTextField logFilePattern;
	private final JFormattedTextField logFileSize;
	private final JFormattedTextField numberOfLogFile;
	
	public CreateFileHandlerPane() {
		super();
		
		JPanel logFilePatternPane = new JPanel();
		JLabel logFilePatternTitle = new JLabel("Log file name pattern: ");
		logFilePatternTitle.setFont(font);
		logFilePattern = new JTextField(50);
		logFilePattern.setText(DEFAULT_LOG_FILE_PATTERN);
		logFilePatternPane.add(logFilePatternTitle);
		logFilePatternPane.add(logFilePattern);		
		add(logFilePatternPane);
		
		JPanel logFileSizePane = new JPanel();
		JLabel logFileSizeTitle = new JLabel("Log file maximum bytes number: ");
		logFileSizeTitle.setFont(font);

		NumberFormatter fileSizeFormatter = new NumberFormatter(NumberFormat.getInstance());
		fileSizeFormatter.setValueClass(Long.class);
		fileSizeFormatter.setMaximum(Long.MAX_VALUE);
		fileSizeFormatter.setAllowsInvalid(false);
		  
		logFileSize = new JFormattedTextField(fileSizeFormatter);
		logFileSize.setColumns(15);
		logFileSize.setValue(DEFAULT_LOG_FILE_SIZE);
		logFileSizePane.add(logFileSizeTitle);
		logFileSizePane.add(logFileSize);
		add(logFileSizePane);
		
		JPanel numberOfLogFilePane = new JPanel();
		JLabel numberOfLogFileTitle = new JLabel("Number of log files: ");
		numberOfLogFileTitle.setFont(font);
		
		NumberFormatter numberOfFileFormatter = new NumberFormatter(NumberFormat.getInstance());
		numberOfFileFormatter.setValueClass(Integer.class);
		numberOfFileFormatter.setMaximum(Integer.MAX_VALUE);
		numberOfFileFormatter.setAllowsInvalid(false);
		
		numberOfLogFile = new JFormattedTextField(numberOfFileFormatter);
		numberOfLogFile.setColumns(6);
		numberOfLogFile.setValue(DEFAULT_NUMBER_OF_LOG_FILE);
		numberOfLogFilePane.add(numberOfLogFileTitle);
		numberOfLogFilePane.add(numberOfLogFile);
		add(numberOfLogFilePane);
	}

	public String getSelectedFilePattern() {
		return logFilePattern.getText();
	}

	public Long getSelectedFileSize() {
		return (Long)logFileSize.getValue();
	}

	public Integer getSelectedNamberOfFiles() {
		return (Integer)numberOfLogFile.getValue();
	}
	
}
