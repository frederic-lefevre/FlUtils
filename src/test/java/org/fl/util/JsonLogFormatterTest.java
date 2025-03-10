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

import static org.assertj.core.api.Assertions.*;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

class JsonLogFormatterTest {

	private static final ObjectMapper mapper = JsonMapper.builder().build();
	
	@Test
	void testMinimalLogErrorFormat() throws JsonMappingException, JsonProcessingException {
		
		JsonLogFormatter jsonLogFormatter = new JsonLogFormatter();
		
		LogRecord logRecord = new LogRecord(Level.WARNING, null);		
		assertThat(logRecord).isNotNull();
		
		String logRecordExpectedDateTime = DateTimeFormatter.ofPattern(jsonLogFormatter.getDateFormatPattern())
				.format(ZonedDateTime.ofInstant(logRecord.getInstant(), ZoneId.systemDefault()));
		
		String formattedLogRecord = jsonLogFormatter.format(logRecord);
		
		assertThat(formattedLogRecord)
			.isNotNull()
			.contains(logRecordExpectedDateTime)
			.contains("WARNING");
		
		LogRecordForJson record = mapper.readValue(formattedLogRecord, LogRecordForJson.class);
		
		assertThat(record).isNotNull();
		assertThat(record.getLevel()).isEqualTo(Level.WARNING.getName());
		assertThat(record.getSequenceNumber()).isNotNull();
		assertThat(record.getDate()).isEqualTo(logRecordExpectedDateTime);
		assertThat(record.getClassName()).isNull();
		assertThat(record.getMethodName()).isNull();
		assertThat(record.getMessage()).isNull();
		assertThat(record.getLoggerName()).isNull();
		assertThat(record.getException()).isNull();
		
	}
	
	@Test
	void testLogErrorFormat() throws JsonMappingException, JsonProcessingException {
		
		JsonLogFormatter jsonLogFormatter = new JsonLogFormatter();
		
		String loggerName = "org.fl.util.DummyLoggerName";
		String recordMessage = "A record log message with parameters: {0} {1}";
		Object[] parameters = new Object[] { "mon paramètre", 987 };
		LogRecord logRecord = new LogRecord(Level.WARNING, recordMessage);
		String sourceMethod = "testLogErrorFormat";
		String exceptionMessage = "An special exception message";
		long dummySequenceNumber = System.currentTimeMillis() - 1234;
		
		logRecord.setLoggerName(loggerName);
		logRecord.setSourceClassName(JsonLogFormatterTest.class.getName());
		logRecord.setSourceMethodName(sourceMethod);
		logRecord.setSequenceNumber(dummySequenceNumber);
		logRecord.setParameters(parameters);
		
		logRecord.setThrown(new IllegalArgumentException(exceptionMessage));
		
		String logRecordExpectedDateTime = DateTimeFormatter.ofPattern(jsonLogFormatter.getDateFormatPattern())
				.format(ZonedDateTime.ofInstant(logRecord.getInstant(), ZoneId.systemDefault()));
		
		assertThat(logRecord.getInstant()).isCloseTo(Instant.now(), within(2, ChronoUnit.SECONDS));
		
		String expectedFormattedMessage = MessageFormat.format(recordMessage, parameters);
		String expectedStackTraceFragment = "at org.junit.platform.commons.util.ReflectionUtils invokeMethod";
		
		String formattedLogRecord = jsonLogFormatter.format(logRecord);
		
		assertThat(formattedLogRecord)
			.isNotNull();
		
		LogRecordForJson record = mapper.readValue(formattedLogRecord, LogRecordForJson.class);
		
		assertThat(record).isNotNull();
		assertThat(record.getLevel()).isEqualTo(Level.WARNING.getName());
		assertThat(record.getSequenceNumber()).isEqualTo(Long.toString(dummySequenceNumber));
		assertThat(record.getDate()).isEqualTo(logRecordExpectedDateTime);
		assertThat(record.getClassName()).isEqualTo(JsonLogFormatterTest.class.getName());
		assertThat(record.getMethodName()).isEqualTo(sourceMethod);
		assertThat(record.getMessage()).isEqualTo(expectedFormattedMessage);
		assertThat(record.getLoggerName()).isEqualTo(loggerName);
		assertThat(record.getException())
			.contains(exceptionMessage)
			.contains(expectedStackTraceFragment);
		
		assertThat(record.getException().lines().count()).isGreaterThan(60);
	}
}
