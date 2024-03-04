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

import java.lang.StackWalker.Option;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class FilterCounter implements Filter {

	public static class LogRecordCounter {
		
		private final String name;
		private final FilterCounter filterCounter;
		private final Logger logger;
		
		public LogRecordCounter(String name, FilterCounter filterCounter, Logger logger) {
			super();
			this.name = name;
			this.filterCounter = filterCounter;
			this.logger = logger;
		}

		public int getLogRecordCount() {
			return filterCounter.getLogRecordCount(name);
		}

		public int getLogRecordCount(Level level) {
			return filterCounter.getLogRecordCount(name, level);
		}
		
		public boolean isLoggable(Level level) {
			return logger.isLoggable(level);
		}
	}
	
	// Keys are fully qualified method names
	private Map<String, Map<Level, Integer>> logRecordCounts = new HashMap<>();
	
	@Override
	public synchronized boolean isLoggable(LogRecord record) {

		Level level = record.getLevel();

		Arrays.stream(Thread.currentThread().getStackTrace())
				.map(stackTraceElement -> stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName())
				.forEach(name -> {
					Map<Level, Integer> logRecordCountByLevels = logRecordCounts.get(name);
					if (logRecordCountByLevels != null) {
						logRecordCountByLevels.put(level,
								Optional.ofNullable(logRecordCountByLevels.get(level)).orElse(0) + 1);
					}
				});
		return false;
	}

	public synchronized void resetAllLogRecordCounts() {
		logRecordCounts.clear();
	}

	public synchronized void addLogRecordCounters(String name) {
		
		Map<Level, Integer> logRecordCountByLevels = logRecordCounts.get(name);
		if (logRecordCountByLevels != null) {
			logRecordCountByLevels.clear();
		} else {
			logRecordCounts.put(name, new HashMap<>());
		}
	}
	
	public int getLogRecordCount(String name) {
		
		Map<Level, Integer> logRecordCountByLevels = logRecordCounts.get(name);
		if (logRecordCountByLevels == null) {
			return 0;
		} else {
			return logRecordCountByLevels.values().stream().mapToInt(Integer::intValue).sum();
		}
	}
	
	public int getLogRecordCount(String name, Level level) {
		Map<Level, Integer> logRecordCountByLevels = logRecordCounts.get(name);
		if (logRecordCountByLevels == null) {
			return 0;
		} else {
			return Optional.ofNullable(logRecordCountByLevels.get(level)).orElse(0);
		}		
	}
	
	protected static synchronized FilterCounter setFilterCounter(String name, Logger logger) {
		
		FilterCounter filterCounter = null;
		Filter filter = logger.getFilter();
		if (filter == null) {
			
			filterCounter = new FilterCounter();
			filterCounter.addLogRecordCounters(name);
			logger.setFilter(filterCounter);
		} else if (filter instanceof FilterCounter){
			filterCounter = (FilterCounter)filter;
			filterCounter.addLogRecordCounters(name);
		}
		return filterCounter;
	}

	public static synchronized LogRecordCounter getLogRecordCounter(Logger logger) {
		
		String name = getCallerFullyQualifiedMethodName();
		
		return new LogRecordCounter(name, setFilterCounter(name, logger), logger);
	}
	
	private static String getCallerFullyQualifiedMethodName() {
		return StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
				.walk(stream -> getCallerFullyQualifiedMethodName(stream.skip(2).findFirst().get()));
	}

	private static String getCallerFullyQualifiedMethodName(StackWalker.StackFrame stackFrame) {
		return stackFrame.getDeclaringClass().getCanonicalName() + "." + stackFrame.getMethodName();
	}
}