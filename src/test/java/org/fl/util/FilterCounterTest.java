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

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class FilterCounterTest {

	private class ForTest {
		
		private static final Logger logger = Logger.getLogger(ForTest.class.getName());
		
		public static void logAnError() {
			logger.severe("Severe error");
		}
		
		public static void logAnErrorAfterRecursiveCall(int nbCall) {
			
			if (nbCall < 0) {
				logger.severe("Severe error");
			} else {
				logAnErrorAfterRecursiveCall(nbCall-1);
			}
		}
	}
	
	@Test
	void filterCounterETest() {
		
		Logger loggerForTest = Logger.getLogger(ForTest.class.getName());
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(loggerForTest);
		
		assertThat(logRecordCounter.isLoggable(Level.SEVERE)).isTrue();
		
		Filter logFilter = loggerForTest.getFilter();
		assertThat(logFilter).isNotNull().isInstanceOf(FilterCounter.class);
		
		assertThat(logFilter.isLoggable(new LogRecord(Level.SEVERE, ""))).isFalse();
		assertThat(logFilter.isLoggable(new LogRecord(Level.WARNING, ""))).isFalse();
		assertThat(logFilter.isLoggable(new LogRecord(Level.INFO, ""))).isFalse();
		assertThat(logFilter.isLoggable(new LogRecord(Level.FINE, ""))).isFalse();	
	}
	
	@Test
	void filterCounterErrorLogTest() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()));
		
		ForTest.logAnError();
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isZero();
	}
	
	@Test
	void filterCounterOnMediumStackTest() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()));
		
		ForTest.logAnErrorAfterRecursiveCall(10);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isZero();
	}
	
	@Test
	void filterCounterOnMLargeStackTest() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()));
		
		ForTest.logAnErrorAfterRecursiveCall(500);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isZero();
	}
}
