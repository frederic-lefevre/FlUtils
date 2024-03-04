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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerCounter extends Logger {

	// fully qualified method name
	private final String name;
	
	public static LoggerCounter getLogger() {
		String name = getCallerFullyQualifiedMethodName();
		LoggerCounter newLoggerCounter = new LoggerCounter(name);
		FilterCounter.setFilterCounter(name, newLoggerCounter);
		return newLoggerCounter;
	}
	
	private LoggerCounter(String name) {
		super(null, null);
		this.name = name;
	}

	public int getLogRecordCount() {
		return ((FilterCounter)getFilter()).getLogRecordCount(name);
	}
	
	public int getLogRecordCount(Level level) {
		return ((FilterCounter)getFilter()).getLogRecordCount(name, level);
	}
	
	// Reset counts for the current thread
	public  void resetLogRecordCount() {
		((FilterCounter)getFilter()).addLogRecordCounters(name);
	}
	
	// Reset counts for all threads
	public  void resetAllLogRecordCount() {
		((FilterCounter)getFilter()).resetAllLogRecordCounts();
	}
	
	private static String getCallerFullyQualifiedMethodName() {
	   return StackWalker
	      .getInstance(Option.RETAIN_CLASS_REFERENCE)
	      .walk(stream -> getCallerFullyQualifiedMethodName(stream.skip(2).findFirst().get()));
	}
	
	private static String getCallerFullyQualifiedMethodName(StackWalker.StackFrame stackFrame) {
		return stackFrame.getDeclaringClass().getCanonicalName() + "." + stackFrame.getMethodName();
	}
}
