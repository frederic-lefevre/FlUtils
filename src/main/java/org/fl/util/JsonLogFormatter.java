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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class JsonLogFormatter extends Formatter {

	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS v ";

	private final static String SEP = "\",";
	private final static String DATE = "\"date\":\"";
	private final static String SEQ_NUM = "\"number\":\"";
	private final static String LOGGER_NAME = "\"logger name\":\"";
	private final static String LEVEL = "\"level\":\"";
	private final static String CLASS_NAME = "\"class\":\"";
	private final static String METHOD_NAME = "\"method\":\"";
	private final static String MESSAGE = "\"message\":\"";
	private final static String EXCEPTION = "\"exception\":\"";
	private final static String END = "\"}";

	private DateTimeFormatter dateTimeFormatter;

	public JsonLogFormatter() {
		super();
		dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
	}

	@Override
	public String format(LogRecord record) {

		StringBuilder sb = new StringBuilder();

		sb.append('{');
		sb.append(DATE)
				.append(dateTimeFormatter.format(
						LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault())))
				.append(SEP);
		sb.append(SEQ_NUM).append(record.getSequenceNumber()).append(SEP);
		sb.append(LOGGER_NAME).append(record.getLoggerName()).append(SEP);
		sb.append(LEVEL).append(record.getLevel().getName()).append(SEP);
		sb.append(CLASS_NAME).append(record.getSourceClassName()).append(SEP);
		sb.append(METHOD_NAME).append(record.getSourceMethodName()).append(SEP);
		sb.append(MESSAGE).append(formatMessage(record));

		Throwable thrown = record.getThrown();
		if (thrown != null) {
			String thrownMsg = thrown.getMessage();

			if ((thrownMsg != null) && (!thrownMsg.isEmpty())) {
				sb.append(SEP).append(EXCEPTION).append(thrownMsg).append(END);
			} else {
				sb.append(END);
			}
		} else {
			sb.append(END);
		}
		return sb.toString();
	}

}
