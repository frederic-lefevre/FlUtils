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

import java.util.List;
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
		List<String> logRecordMessagess = IntStream.rangeClosed(1, nbLoggedRecords)
				.mapToObj(recordNumber -> "Record log " + recordNumber)
				.collect(Collectors.toList());
		
		logRecordMessagess
			.forEach(recordNumber -> logMemoryBuffer.addLogRecord(new LogRecord(Level.WARNING, "Record log " + recordNumber)));
		
		assertThat(logMemoryBuffer.getMaxLogRecord()).isEqualTo(bufferSize);
		assertThat(logMemoryBuffer.remainingCapacity()).isEqualTo(bufferSize - nbLoggedRecords);
		assertThat(logMemoryBuffer.logRecordNumber()).isEqualTo(nbLoggedRecords);
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull().contains(logRecordMessagess);
		// Log records are still in the buffer
		assertThat(logMemoryBuffer.logRecordNumber()).isEqualTo(nbLoggedRecords);
		
		// Read and clear
		assertThat(logMemoryBuffer.getAndDeleteFormattedRecords()).isNotNull().contains(logRecordMessagess);
		assertThat(logMemoryBuffer.getMaxLogRecord())
			.isEqualTo(logMemoryBuffer.remainingCapacity())
			.isEqualTo(bufferSize);
		assertThat(logMemoryBuffer.logRecordNumber()).isZero();
		assertThat(logMemoryBuffer.getFormattedRecords()).isNotNull().isEmpty();
		
	}
}
