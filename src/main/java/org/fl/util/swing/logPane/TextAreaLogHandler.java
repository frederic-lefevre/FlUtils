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

package org.fl.util.swing.logPane;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;

public class TextAreaLogHandler extends Handler {

	private LogDisplayComponent logDisplayComponent;
	private final LogDisplayChanger logDisplayChanger;

	private int logDisplayMaxLength;

	private final static int DEFAULT_LOG_DISPLAY_MAX_LENGTH = 100000;

	public TextAreaLogHandler(LogDisplayComponent ldc, LogDisplayChanger lChanger) {
		super();
		logDisplayComponent = ldc;
		logDisplayChanger = lChanger;
		logDisplayMaxLength = DEFAULT_LOG_DISPLAY_MAX_LENGTH;
	}

	@Override
	public void publish(LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				if (isLoggable(record)) {
					int startRecord;
					int textLength = logDisplayComponent.textLength();
	
					if (textLength > logDisplayMaxLength) {
						logDisplayComponent = logDisplayChanger.changeLogDisplayComponent();
						textLength = logDisplayComponent.textLength();
					}
	
					if (textLength > 0) {
						startRecord = textLength - 1;
					} else {
						startRecord = 0;
					}
	
					Formatter formatter = getFormatter();
					if (formatter == null) {
						formatter = new SimpleFormatter();
						setFormatter(formatter);	
					}
					
					logDisplayComponent.appendToText(formatter.format(record));
	
					int endRecord = logDisplayComponent.textLength() - 1;
					logDisplayComponent.addLogRecord(record.getLevel(), startRecord, endRecord);
				}
			}
		});
	}

	@Override
	public void flush() {		
	}

	@Override
	public void close() throws SecurityException {
	}

	public void setLogDisplayMaxLength(int logDisplayMaxLength) {
		this.logDisplayMaxLength = logDisplayMaxLength;
	}	
}
