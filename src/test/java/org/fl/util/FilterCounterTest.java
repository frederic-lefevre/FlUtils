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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class FilterCounterTest {

	private class ForTest {
		
		private static final Logger logger = Logger.getLogger(ForTest.class.getName());
		
		public static void logAnError(String message) {
			logger.severe(message);
		}
		
		public static void logErrors(String message, int nbError) {
			for (int i = 0; i < nbError; i++) {
				logger.severe(message + i);
			}
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
	void filterCounterTest() {
		
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
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	@Test
	void filterCounterErrorLogTest() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()));
		
		String errorMessage = "The error message";
		ForTest.logAnError(errorMessage);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isZero();
		
		assertThat(logRecordCounter.getLogRecords()).isNotNull().singleElement()
			.satisfies(logRecord -> {
				assertThat(logRecord.getLevel()).isEqualTo(Level.SEVERE);
				assertThat(logRecord.getMessage()).isEqualTo(errorMessage);
			});
		logRecordCounter.stopLogCountAndFilter();
	}
	
	
	@Test
	void filterCounterErrorLogTestMultipleError() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()));
		
		ForTest.logErrors("The error message", logRecordCounter.getMaxLogRecordKept());
		
		assertThat(logRecordCounter.getLogRecords()).isNotNull().hasSize(logRecordCounter.getMaxLogRecordKept());
		
		ForTest.logAnError("last error");
		assertThat(logRecordCounter.getLogRecords())
			.isNotNull()
			.hasSize(logRecordCounter.getMaxLogRecordKept())
			.satisfiesOnlyOnce(logRecord -> assertThat(logRecord.getMessage()).isEqualTo("last error"));
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	@Test
	void filterCounterErrorLogTestMultipleError2() {
		
		int nbErrorKept = 5;
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()), nbErrorKept);
		
		assertThat(logRecordCounter.getMaxLogRecordKept()).isEqualTo(nbErrorKept);
		
		ForTest.logErrors("The error message", nbErrorKept*200);
		
		assertThat(logRecordCounter.getLogRecords()).isNotNull().hasSize(nbErrorKept)
			.satisfiesOnlyOnce(logRecord -> 
				assertThat(logRecord.getMessage()).isEqualTo("The error message" + Integer.toString(nbErrorKept*200 - 1)));
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	@Test
	void filterCounterOnMediumStackTest() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()));
		
		ForTest.logAnErrorAfterRecursiveCall(10);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isZero();
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	@Test
	void filterCounterOnMLargeStackTest() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(ForTest.class.getName()));
		
		ForTest.logAnErrorAfterRecursiveCall(500);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isZero();
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	@AfterAll
	static void check() {
		
		// Filter on logger should be removed because all log counter have been stopped
		assertThat(Logger.getLogger(ForTest.class.getName()).getFilter()).isNull();
	}
}
