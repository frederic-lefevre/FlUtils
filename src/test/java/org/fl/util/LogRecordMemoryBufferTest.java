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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class LogRecordMemoryBufferTest {
	
	@Test
	void testCreateBuffer() {
		
		int bufferSize = 100;
		LogRecordMemoryBuffer logMemoryBuffer = new LogRecordMemoryBuffer(bufferSize);
		
		assertThat(logMemoryBuffer).isNotNull();
		assertThat(logMemoryBuffer.getMaxLogRecord())
			.isEqualTo(logMemoryBuffer.remainingCapacity())
			.isEqualTo(bufferSize);
		assertThat(logMemoryBuffer.logRecordNumber()).isZero();
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull().isEmpty();
		
		// Put 10 log records in the buffer
		int nbLoggedRecords = 10;
		List<String> logRecordMessages = IntStream.rangeClosed(1, nbLoggedRecords)
				.mapToObj(recordNumber -> "Record log " + recordNumber)
				.collect(Collectors.toList());
		
		logRecordMessages
			.forEach(recordMessage -> logMemoryBuffer.addLogRecord(new LogRecord(Level.WARNING, recordMessage)));
		
		assertThat(logMemoryBuffer.getMaxLogRecord()).isEqualTo(bufferSize);
		assertThat(logMemoryBuffer.remainingCapacity()).isEqualTo(bufferSize - nbLoggedRecords);
		assertThat(logMemoryBuffer.logRecordNumber()).isEqualTo(nbLoggedRecords);
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull().contains(logRecordMessages);
		// Log records are still in the buffer
		assertThat(logMemoryBuffer.logRecordNumber()).isEqualTo(nbLoggedRecords);
		
		// Read and clear
		assertThat(logMemoryBuffer.getAndDeleteFormattedRecords()).isNotNull().contains(logRecordMessages);
		assertThat(logMemoryBuffer.getMaxLogRecord())
			.isEqualTo(logMemoryBuffer.remainingCapacity())
			.isEqualTo(bufferSize);
		assertThat(logMemoryBuffer.logRecordNumber()).isZero();
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull().isEmpty();
	}
	
	@Test
	void testLogErrorContent() {
		
		int bufferSize = 3;
		LogRecordMemoryBuffer logMemoryBuffer = new LogRecordMemoryBuffer(bufferSize);

		String loggerName = "org.fl.util.DummyLoggerName";
		String recordMessage = "A record log message";
		LogRecord logRecord = new LogRecord(Level.WARNING, recordMessage);
		String sourceMethod = "testLogErrorContent";
		long dummySequenceNumber = System.currentTimeMillis() - 1234;

		logRecord.setLoggerName(loggerName);
		logRecord.setSourceClassName(LogRecordMemoryBufferTest.class.getName());
		logRecord.setSourceMethodName(sourceMethod);
		logRecord.setSequenceNumber(dummySequenceNumber);
		
		
		String logRecordExpectedDateTime = DateTimeFormatter.ofPattern(logMemoryBuffer.getDatePatternFormat())
				.format(ZonedDateTime.ofInstant(logRecord.getInstant(), ZoneId.systemDefault()));
		assertThat(logRecord.getInstant()).isCloseTo(Instant.now(), within(2, ChronoUnit.SECONDS));
		
		logMemoryBuffer.addLogRecord(logRecord);
		
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull()
			.contains(recordMessage)
			.contains(Level.WARNING.getName())
			.contains(loggerName)
			.contains(LogRecordMemoryBufferTest.class.getName())
			.contains(sourceMethod)
			.contains(Long.toString(dummySequenceNumber))
			.contains(logRecordExpectedDateTime);
	}
	
	@Test
	void zeroCapacityShouldThrowExceptio() {
		assertThatIllegalArgumentException().isThrownBy(() -> new LogRecordMemoryBuffer(0));
	}
	
	@Test
	void testOverflowAndClear() {
		
		int bufferSize = 3;
		LogRecordMemoryBuffer logMemoryBuffer = new LogRecordMemoryBuffer(bufferSize);
		
		assertThat(logMemoryBuffer.getMaxLogRecord())
			.isEqualTo(logMemoryBuffer.remainingCapacity())
			.isEqualTo(bufferSize);
		
		IntFunction<String> logMessageSupplier = recordNumber -> "Record log " + recordNumber;
		
		// Put 3 log records in the buffer to 
		int nbLoggedRecords = bufferSize;
		List<String> logRecordMessages = IntStream.rangeClosed(1, nbLoggedRecords)
				.mapToObj(logMessageSupplier)
				.collect(Collectors.toList());
		
		logRecordMessages
			.forEach(recordMessage -> logMemoryBuffer.addLogRecord(new LogRecord(Level.WARNING, recordMessage)));
		
		assertThat(logMemoryBuffer.remainingCapacity()).isZero();
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull().contains(logRecordMessages);
		
		// Log one more message (overflow)
		logRecordMessages.add(logMessageSupplier.apply(nbLoggedRecords + 1));
		logMemoryBuffer.addLogRecord(new LogRecord(Level.WARNING, logRecordMessages.getLast()));
		
		// Check that all log records are in the buffer except the first one
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull()
			.contains(logRecordMessages.subList(1, logRecordMessages.size()))
			.doesNotContain(logRecordMessages.get(0));
		
		// Test clear
		logMemoryBuffer.clear();
		assertThat(logMemoryBuffer.getMaxLogRecord())
			.isEqualTo(logMemoryBuffer.remainingCapacity())
			.isEqualTo(bufferSize);
	}
	
	@Test
	void testClearAndResize() {
		
		int bufferSize = 3;
		LogRecordMemoryBuffer logMemoryBuffer = new LogRecordMemoryBuffer(bufferSize);
		
		assertThat(logMemoryBuffer.getMaxLogRecord())
			.isEqualTo(logMemoryBuffer.remainingCapacity())
			.isEqualTo(bufferSize);
		
		logMemoryBuffer.addLogRecord(new LogRecord(Level.WARNING, "A message"));
		
		assertThat(logMemoryBuffer.remainingCapacity())
			.isEqualTo(bufferSize - 1);
		
		int newSize = 5;
		logMemoryBuffer.clearAndResize(newSize);
		assertThat(logMemoryBuffer.getMaxLogRecord())
			.isEqualTo(logMemoryBuffer.remainingCapacity())
			.isEqualTo(newSize);
	}
}
