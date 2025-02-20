/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

class LoggerCounterTest {

	@Test
	void loggerCounterTest() {
		
		LoggerCounter logCounter = LoggerCounter.getLogger();
		
		assertThat(logCounter).isNotNull();	
		assertThat(logCounter.getName()).isNull();
		
		assertThat(logCounter.isLoggable(Level.SEVERE)).isTrue();
		
		Filter logFiler = logCounter.getFilter();
		assertThat(logFiler).isNotNull().isInstanceOf(FilterCounter.class);
		
		assertThat(logFiler.isLoggable(new LogRecord(Level.SEVERE, ""))).isFalse();
		assertThat(logFiler.isLoggable(new LogRecord(Level.WARNING, ""))).isFalse();
		assertThat(logFiler.isLoggable(new LogRecord(Level.INFO, ""))).isFalse();
		assertThat(logFiler.isLoggable(new LogRecord(Level.FINE, ""))).isFalse();
	}
	
	@Test
	void severeErrorCountTest() {
		
		LoggerCounter logCounter = LoggerCounter.getLogger();
		
		assertThat(logCounter).isNotNull();	
		assertThat(logCounter.getName()).isNull();
		
		assertThat(logCounter.isLoggable(Level.SEVERE)).isTrue();
		logCounter.severe("severe error");
		
		assertThat(logCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logCounter.getLogRecordCount(Level.INFO)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.WARNING)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.FINE)).isZero();
	}
	
	@Test
	void warningAndSevereErrorCountTest() {
		
		LoggerCounter logCounter = LoggerCounter.getLogger();
		
		assertThat(logCounter).isNotNull();	
		assertThat(logCounter.getName()).isNull();
		
		assertThat(logCounter.isLoggable(Level.WARNING)).isTrue();
		logCounter.warning("warning error");
		logCounter.severe("severe error");
		logCounter.warning("warning error 2");
		
		assertThat(logCounter.getLogRecordCount()).isEqualTo(3);
		assertThat(logCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logCounter.getLogRecordCount(Level.INFO)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.WARNING)).isEqualTo(2);
		assertThat(logCounter.getLogRecordCount(Level.FINE)).isZero();
		
		logCounter.resetAllLogRecordCount();
		assertThat(logCounter.getLogRecordCount()).isZero();
		assertThat(logCounter.getLogRecordCount(Level.SEVERE)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.INFO)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.WARNING)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.FINE)).isZero();
	}
	
	@Test
	void fineErrorCountTest() {
		
		LoggerCounter logCounter = LoggerCounter.getLogger();
		
		assertThat(logCounter).isNotNull();	
		assertThat(logCounter.getName()).isNull();
		
		assertThat(logCounter.isLoggable(Level.FINE)).isFalse();
		logCounter.fine("fine logging");
		logCounter.fine("fine logging 2");
		
		assertThat(logCounter.getLogRecordCount()).isZero();
		assertThat(logCounter.getLogRecordCount(Level.SEVERE)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.INFO)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.WARNING)).isZero();
		assertThat(logCounter.getLogRecordCount(Level.FINE)).isZero();
	}
}
