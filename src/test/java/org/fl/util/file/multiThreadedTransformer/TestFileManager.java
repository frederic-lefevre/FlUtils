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

package org.fl.util.file.multiThreadedTransformer;

import java.util.stream.IntStream;

public class TestFileManager {

	public static final long NUMBER_OF_REGULAR_LINE = 10000L;	
	public static final long NUMBER_OF_ATYPIC_LINE = 1000L;
	public static final long NUMBER_OF_WRONG_LINE = 1000L;
	
	public static final String ATYPIC_LINE_PREFIX = "ATYPIC LINE";
	public static final String WRONG_LINE_PREFIX = "WRONG LINE";
	
	public static final String ADDED_LINE_PREFIX_LINE_PREFIX = "Line prefix: ";
	
	public static final int NB_COLUMN = 10;
	
	protected static String produceLine(int lineNumber) {
		
		StringBuilder sb = new StringBuilder();		
		IntStream.rangeClosed('a', 'z')
			.limit(NB_COLUMN)
			.forEachOrdered(c -> sb.append((char)c).append(lineNumber).append(";"));
		return sb.toString();
	}
	
	protected static String produceAtypicLine(int lineNumber) {
		return ATYPIC_LINE_PREFIX + produceLine(lineNumber);
	}
	
	
	protected static String produceWrongLine(int lineNumber) {
		return WRONG_LINE_PREFIX + produceLine(lineNumber);
	}
}
