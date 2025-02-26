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
import static org.assertj.core.api.Assertions.within;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

class PlainLogFormatterTest {

	@Test
	void testLogErrorFormat() {
		
		PlainLogFormatter plainLogFormatter = new PlainLogFormatter();
		
		String loggerName = "org.fl.util.DummyLoggerName";
		String recordMessage = "A record log message";
		LogRecord logRecord = new LogRecord(Level.WARNING, recordMessage);
		String sourceMethod = "testLogErrorFormat";
		String exceptionMessage = "An special exception message";
		long dummySequenceNumber = System.currentTimeMillis() - 1234;

		logRecord.setLoggerName(loggerName);
		logRecord.setSourceClassName(PlainLogFormatterTest.class.getName());
		logRecord.setSourceMethodName(sourceMethod);
		logRecord.setSequenceNumber(dummySequenceNumber);
		
		logRecord.setThrown(new IllegalArgumentException(exceptionMessage));
		
		
		String logRecordExpectedDateTime = DateTimeFormatter.ofPattern(plainLogFormatter.getDateFormatPattern())
				.format(ZonedDateTime.ofInstant(logRecord.getInstant(), ZoneId.systemDefault()));
		
		assertThat(logRecord.getInstant()).isCloseTo(Instant.now(), within(2, ChronoUnit.SECONDS));
		
		String expectedStackTraceFragment = "at org.junit.platform.commons.util.ReflectionUtils invokeMethod";
		
		String formattedLogRecord = plainLogFormatter.format(logRecord);
		assertThat(formattedLogRecord)
			.isNotNull()
			.contains(recordMessage)
			.contains(Level.WARNING.getName())
			.contains(loggerName)
			.contains(PlainLogFormatterTest.class.getName())
			.contains(sourceMethod)
			.contains(Long.toString(dummySequenceNumber))
			.contains(logRecordExpectedDateTime)
			.contains(exceptionMessage)
			.contains(expectedStackTraceFragment);
		
		System.out.print(formattedLogRecord);
	}
}
