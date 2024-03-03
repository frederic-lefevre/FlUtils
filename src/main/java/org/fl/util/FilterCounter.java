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

public class FilterCounter implements Filter {

	private Map<Thread, Map<Level, Integer>> errorCounts = new HashMap<>();
	
	@Override
	public boolean isLoggable(LogRecord record) {

		Level level = record.getLevel();
		
		Map<Level, Integer> errorCountByLevels = errorCounts.get(Thread.currentThread());
		if (errorCountByLevels == null) {
			errorCountByLevels = new HashMap<>();
			errorCounts.put(Thread.currentThread(), errorCountByLevels);
		}
		errorCountByLevels.put(level, getErrorCount(level) + 1);
		return false;
	}
	
	public void resetErrorCount() {
		errorCounts.clear();
	}

	public int getErrorCount() {
		
		Map<Level, Integer> errorCountByLevels = errorCounts.get(Thread.currentThread());
		if (errorCountByLevels == null) {
			return 0;
		} else {
			return errorCountByLevels.values().stream().mapToInt(Integer::intValue).sum();
		}
	}
	
	public int getErrorCount(Level level) {
		Map<Level, Integer> errorCountByLevels = errorCounts.get(Thread.currentThread());
		if (errorCountByLevels == null) {
			return 0;
		} else {
			return Optional.ofNullable(errorCountByLevels.get(level)).orElse(0);
		}		
	}
}