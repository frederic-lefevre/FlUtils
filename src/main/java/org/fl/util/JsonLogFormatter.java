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

import tools.jackson.core.JacksonException ;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public class JsonLogFormatter extends Formatter {

	private static int MAX_PRINTED_CAUSE_LEVEL = 20;
	
	private DateTimeFormatter dateTimeFormatter;

	private static final Logger safeLogger = Logger.getLogger("");
	
	public JsonLogFormatter() {
		super();
		dateTimeFormatter = DateTimeFormatter.ofPattern(LogRecordForJson.DATE_PATTERN);
	}

	protected String getDateFormatPattern() {
		return LogRecordForJson.DATE_PATTERN;
	}
	
	@Override
	public String format(LogRecord record) {

		// Build manually to be faster
		ObjectNode jsonLogRecord = JsonNodeFactory.instance.objectNode();
		
		jsonLogRecord.put(
				LogRecordForJson.DATE, 
				dateTimeFormatter.format(ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault())));
		jsonLogRecord.put(LogRecordForJson.SEQ_NUM, record.getSequenceNumber());
		jsonLogRecord.put(LogRecordForJson.LOGGER_NAME, record.getLoggerName());
		
		String srcClassName = record.getSourceClassName();
		if (srcClassName != null) {
			jsonLogRecord.put(LogRecordForJson.CLASS_NAME, srcClassName);
		}
		String methodName = record.getSourceMethodName();
		if (methodName != null) {
			jsonLogRecord.put(LogRecordForJson.METHOD_NAME, methodName);
		}
		
		// record.getLevel() cannot be null (trying to set level to null triggers a NPE)
		jsonLogRecord.put(LogRecordForJson.LEVEL, record.getLevel().getName());
		
		jsonLogRecord.put(LogRecordForJson.MESSAGE, formatMessage(record));
		
		Throwable thrown = record.getThrown();
		if (thrown != null) {
			jsonLogRecord.put(LogRecordForJson.EXCEPTION, ExceptionLogging.printExceptionInfos(thrown, MAX_PRINTED_CAUSE_LEVEL));
			
		}
		
		try {
			return JsonUtils.jsonPrettyPrint(jsonLogRecord);
		} catch (JacksonException  e) {
			
			// Log with the root logger in order to avoid infinite recursion, re-entering the same formatter
			safeLogger.log(Level.SEVERE, "JsonProcessingException when formatting the error", e);
			return jsonLogRecord.toString();
		}
	}
}
