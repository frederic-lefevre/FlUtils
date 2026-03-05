/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

import java.util.List;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class BufferLogHandlerTest {

	@Test
	void testZeroSize() {
		assertThatIllegalArgumentException().isThrownBy(() -> new BufferLogHandler("a name", 0));
	}
	
	@Test
	void testNullName() {
		
		BufferLogHandler bufferLogHandler = new BufferLogHandler(null, 1);
		
		assertThat(bufferLogHandler).isNotNull();
		assertThat(bufferLogHandler.getName()).isNull();
	}
	
	@Test
	void testBufferLogHandler() {
		
		final String name = "Regular Buffer log handler";
		final int capacity = 10;
		BufferLogHandler bufferLogHandler = new BufferLogHandler(name, capacity);
		
		assertThat(bufferLogHandler).isNotNull();
		assertThat(bufferLogHandler.getName()).isEqualTo(name);
		assertThat(bufferLogHandler.getMaxMemoryLogRecord()).isEqualTo(capacity);
		assertThat(bufferLogHandler.getFilter()).isNull();
		assertThat(bufferLogHandler.getErrorManager()).isNotNull();
		assertThat(bufferLogHandler.getEncoding()).isNull();
		assertThat(bufferLogHandler.getFormatter()).isNull();
		assertThat(bufferLogHandler.getLevel()).isEqualTo(Level.ALL);
		
		assertThat(bufferLogHandler.getMemoryLogs()).isNotNull().isEmpty();
		
		assertThat(bufferLogHandler.deleteMemoryLogs()).isZero();  // Zero remove done
		
		assertThat(bufferLogHandler.inMemoryRemainingCapacityRatio()).isEqualTo(100); // 100% remaining capacity
		
		// Publish 5 logRecord
		IntFunction<String> logMessageSupplier = recordNumber -> "Record log " + recordNumber;
		int nbLoggedRecords = 5;
		List<String> logRecordMessages = IntStream.rangeClosed(1, nbLoggedRecords)
				.mapToObj(logMessageSupplier)
				.collect(Collectors.toList());
		
		logRecordMessages
			.forEach(recordMessage -> bufferLogHandler.publish(new LogRecord(Level.WARNING, recordMessage)));
		
		assertThat(bufferLogHandler.getMaxMemoryLogRecord()).isEqualTo(capacity);
		assertThat(bufferLogHandler.inMemoryRemainingCapacityRatio()).isEqualTo(100*nbLoggedRecords/capacity);
		
		assertThat(bufferLogHandler.getMemoryLogs()).isNotNull()
			.contains(logRecordMessages);
		
		// Log records are still in memory
		assertThat(bufferLogHandler.inMemoryRemainingCapacityRatio()).isEqualTo(100*nbLoggedRecords/capacity);
		
		// Delete log records
		assertThat(bufferLogHandler.deleteMemoryLogs()).isEqualTo(nbLoggedRecords);
		
		assertThat(bufferLogHandler.getMemoryLogs()).isNotNull().isEmpty();
		assertThat(bufferLogHandler.inMemoryRemainingCapacityRatio()).isEqualTo(100); // 100% remaining capacity
	}
	
	@Test
	void testGetLogRecords() {
		
		final String name = "Regular Buffer log handler";
		final int capacity = 10;
		BufferLogHandler bufferLogHandler = new BufferLogHandler(name, capacity);
		
		assertThat(bufferLogHandler.getLogRecords()).isNotNull().isEmpty();
		
		// Publish 5 logRecord
		IntFunction<LogRecord> logRecordSupplier = recordNumber -> new LogRecord(Level.WARNING, "Record log " + recordNumber);
		int nbLoggedRecords = 5;
		List<LogRecord> logRecords = IntStream.rangeClosed(1, nbLoggedRecords)
				.mapToObj(logRecordSupplier)
				.collect(Collectors.toList());
		
		logRecords.forEach(lr -> bufferLogHandler.publish(lr));
		
		assertThat(bufferLogHandler.getLogRecords())
			.hasSize(nbLoggedRecords)
			.hasSameElementsAs(logRecords);
	}
	
	
	@Test
	void testGetAndDeleteLogRecords() {
		
		
		final String name = "Regular Buffer log handler";
		final int capacity = 10;
		BufferLogHandler bufferLogHandler = new BufferLogHandler(name, capacity);
		
		// Publish 5 logRecord
		IntFunction<LogRecord> logRecordSupplier = recordNumber -> new LogRecord(Level.WARNING, "Record log " + recordNumber);
		int nbLoggedRecords = 5;
		List<LogRecord> logRecords = IntStream.rangeClosed(1, nbLoggedRecords)
				.mapToObj(logRecordSupplier)
				.collect(Collectors.toList());
		
		logRecords.forEach(lr -> bufferLogHandler.publish(lr));
		
		assertThat(bufferLogHandler.getAndDeleteLogRecords())
			.hasSize(nbLoggedRecords)
			.hasSameElementsAs(logRecords);
		
		assertThat(bufferLogHandler.getLogRecords()).isEmpty();
	}
	
	@Test
	void testBufferLogHandlerWithGetAndDelete() {
		
		final String name = "Regular Buffer log handler";
		final int capacity = 5;
		BufferLogHandler bufferLogHandler = new BufferLogHandler(name, capacity);

		// Publish 4 logRecord
		IntFunction<String> logMessageSupplier = recordNumber -> "Record log " + recordNumber;
		int nbLoggedRecords = 4;
		List<String> logRecordMessages = IntStream.rangeClosed(1, nbLoggedRecords)
				.mapToObj(logMessageSupplier)
				.collect(Collectors.toList());
		
		logRecordMessages
			.forEach(recordMessage -> bufferLogHandler.publish(new LogRecord(Level.WARNING, recordMessage)));
		
		assertThat(bufferLogHandler.getMemoryAndDeleteLogs()).isNotNull()
			.contains(logRecordMessages);
		
		assertThat(bufferLogHandler.getMemoryLogs()).isNotNull().isEmpty();
		assertThat(bufferLogHandler.inMemoryRemainingCapacityRatio()).isEqualTo(100); // 100% remaining capacity
	}
	
	@Test
	void testLevel() {
		
		final String name = "Regular Buffer log handler";
		final int capacity = 2;
		BufferLogHandler bufferLogHandler = new BufferLogHandler(name, capacity);
		bufferLogHandler.setLevel(Level.WARNING);

		assertThat(bufferLogHandler.getLevel()).isEqualTo(Level.WARNING);
		
		String warningMessage = "Warning message";
		String infoMessage = "Info message";
		bufferLogHandler.publish(new LogRecord(Level.WARNING, warningMessage));
		bufferLogHandler.publish(new LogRecord(Level.INFO, infoMessage));

		// 50% remaining capacity, only 1 message has been retained
		assertThat(bufferLogHandler.inMemoryRemainingCapacityRatio()).isEqualTo(50);
		
		assertThat(bufferLogHandler.getMemoryLogs()).isNotNull()
			.doesNotContain(infoMessage)
			.contains(warningMessage);
		
	}
}
