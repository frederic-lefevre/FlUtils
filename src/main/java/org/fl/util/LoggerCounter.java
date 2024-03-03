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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerCounter extends Logger {

	public static LoggerCounter getLogger() {
		LoggerCounter newLoggerCounter = new LoggerCounter();
		newLoggerCounter.setFilter(new FilterCounter());
		return newLoggerCounter;
	}
	
	private LoggerCounter() {
		super(null, null);
	}

	private static class FilterCounter implements Filter {

		private int errorCount = 0;
		
		private Map<Level, Integer> errorCounts = new HashMap<>();

		@Override
		public boolean isLoggable(LogRecord record) {
			errorCount++;
			Level level = record.getLevel();
			errorCounts.put(level, getErrorCount(level) + 1);
			return false;
		}
		
		public void resetErrorCount() {
			errorCount = 0;
			errorCounts.clear();
		}

		public int getErrorCount() {
			return errorCount;
		}
		
		public int getErrorCount(Level level) {
			return Optional.ofNullable(errorCounts.get(level)).orElse(0);
		}
	}

	public int getErrorCount() {
		return ((FilterCounter)getFilter()).getErrorCount();
	}
	
	public int getErrorCount(Level level) {
		return ((FilterCounter)getFilter()).getErrorCount(level);
	}
	
	public  void resetErrorCount() {
		((FilterCounter)getFilter()).resetErrorCount();
	}
}