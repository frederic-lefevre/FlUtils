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
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.fl.util.json.JsonUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonLogFormatter extends Formatter {

	private static int MAX_PRINTED_CAUSE_LEVEL = 20;
	
	private static final String DATE_PATTERN = "uuuu-MM-dd HH:mm:ss.SSS v ";

	private static final String DATE = "date";
	private static final String SEQ_NUM = "sequenceNumber";
	private static final String LOGGER_NAME = "loggerName";
	private static final String LEVEL = "level";
	private static final String CLASS_NAME = "class";
	private static final String METHOD_NAME = "method";
	private static final String MESSAGE = "message";
	private static final String EXCEPTION = "exception";
	
	private DateTimeFormatter dateTimeFormatter;

	private static final Logger safeLogger = Logger.getLogger("");
	
	public JsonLogFormatter() {
		super();
		dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
	}

	@Override
	public String format(LogRecord record) {

		ObjectNode jsonLogRecord = JsonNodeFactory.instance.objectNode();
		
		jsonLogRecord.put(
				DATE, 
				dateTimeFormatter.format(ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault())));
		jsonLogRecord.put(SEQ_NUM, record.getSequenceNumber());
		jsonLogRecord.put(LOGGER_NAME, record.getLoggerName());
		
		String srcClassName = record.getSourceClassName();
		if (srcClassName != null) {
			jsonLogRecord.put(CLASS_NAME, srcClassName);
		}
		String methodName = record.getSourceMethodName();
		if (methodName != null) {
			jsonLogRecord.put(METHOD_NAME, methodName);
		}
		
		// record.getLevel() cannot be null (trying to set level to null triggers a NPE)
		jsonLogRecord.put(LEVEL, record.getLevel().getName());
		
		jsonLogRecord.put(MESSAGE, formatMessage(record));
		
		Throwable thrown = record.getThrown();
		if (thrown != null) {
			jsonLogRecord.put(EXCEPTION, ExceptionLogging.printExceptionInfos(thrown, MAX_PRINTED_CAUSE_LEVEL));
			
		}
		
		try {
			return JsonUtils.jsonPrettyPrint(jsonLogRecord);
		} catch (JsonProcessingException e) {
			
			// Log with the root logger in order to avoid infinite recursion, re-entering the same formatter
			safeLogger.log(Level.SEVERE, "JsonProcessingException when formatting the error", e);
			return jsonLogRecord.toString();
		}
	}
}
