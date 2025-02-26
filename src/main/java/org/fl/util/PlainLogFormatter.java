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

package org.fl.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class PlainLogFormatter extends Formatter {

	private static final String DATE_PATTERN = "uuuu-MM-dd HH:mm:ss.SSS ";

	private static final String BLANK = " ";
	private static final String NEWLINE = "\n";
	private static final String SEPARATOR = ": ";
	private static int MAX_PRINTED_CAUSE_LEVEL = 20;
	private static int MIN_PRINTED_LOG_RECORD_SIZE = 128;

	private DateTimeFormatter dateTimeFormatter;

	public PlainLogFormatter() {
		super();
		dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
	}

	protected String getDateFormatPattern() {
		return DATE_PATTERN;
	}
	
	@Override
	public String format(LogRecord record) {

		int recordAllocSize = MIN_PRINTED_LOG_RECORD_SIZE;
		String msg = record.getMessage();
		recordAllocSize = recordAllocSize + msg.length();

		String exceptionMsg = null;
		Throwable thrown = record.getThrown();
		if (thrown != null) {
			String thrownMsg = thrown.toString();
			if ((thrownMsg != null) && (!thrownMsg.isEmpty())) {
				exceptionMsg = ExceptionLogging.printExceptionInfos(thrown, MAX_PRINTED_CAUSE_LEVEL);
				recordAllocSize = recordAllocSize + exceptionMsg.length();
			}
		}

		// StringBuilder is always converting its argument to a String, even if it is a
		// char
		// so it is better to always append String
		StringBuilder lBuff = new StringBuilder(recordAllocSize);
		lBuff.append(dateTimeFormatter
				.format(ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault())));
		lBuff.append(record.getSequenceNumber()).append(BLANK);
		lBuff.append(record.getLoggerName()).append(BLANK);

		String srcClassName = record.getSourceClassName();
		if (srcClassName != null) {
			lBuff.append(record.getSourceClassName()).append(BLANK);
		}
		String methodName = record.getSourceMethodName();
		if (methodName != null) {
			lBuff.append(record.getSourceMethodName());
		}
		lBuff.append(NEWLINE);
		lBuff.append(record.getLevel().getName()).append(SEPARATOR);
		lBuff.append(msg).append(NEWLINE);

		if (exceptionMsg != null) {
			lBuff.append(exceptionMsg).append(NEWLINE);
		}
		lBuff.append(NEWLINE);

		return lBuff.toString();
	}
}
