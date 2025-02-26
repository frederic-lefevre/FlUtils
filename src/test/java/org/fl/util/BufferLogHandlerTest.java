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

import java.util.logging.Level;

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
	void testNBufferLogHandler() {
		
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

	}
}
