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

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogRecordForJson {

	public static final String DATE_PATTERN = "uuuu-MM-dd HH:mm:ss.SSS v ";
	
	public static final String DATE = "date";
	public static final String SEQ_NUM = "sequenceNumber";
	public static final String LOGGER_NAME = "loggerName";
	public static final String LEVEL = "level";
	public static final String CLASS_NAME = "className";
	public static final String METHOD_NAME = "methodName";
	public static final String MESSAGE = "message";
	public static final String EXCEPTION = "exception";
	
	@JsonProperty(DATE)
	private String date;
	@JsonProperty(SEQ_NUM)
	private String sequenceNumber;
	@JsonProperty(LOGGER_NAME)
	private String loggerName;
	@JsonProperty(LEVEL)
	private String level;
	@JsonProperty(CLASS_NAME)
	private String className;
	@JsonProperty(METHOD_NAME)
	private String methodName;
	@JsonProperty(MESSAGE)
	private String message;
	@JsonProperty(EXCEPTION)
	private String exception;
	
	public LogRecordForJson() {
		
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
	
}
