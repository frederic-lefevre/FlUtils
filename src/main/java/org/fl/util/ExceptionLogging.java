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

public class ExceptionLogging {

	public static String printExceptionInfos(Throwable e, int maxCauseLevel) {
					
		StringBuilder exceptionInfos = new StringBuilder() ;
				
		addExceptionInfos(e, exceptionInfos, 0, maxCauseLevel) ;
		
		return exceptionInfos.toString() ;
	}
	
	private static void addExceptionInfos(Throwable e, StringBuilder exceptionInfos, int currentCauseLevel, int maxCauseLevel) {
		
		if (e != null) {
			exceptionInfos.append("Exception " + e.getMessage()) ;
			
			StackTraceElement[] stackElems = e.getStackTrace() ;
			if (stackElems != null){
				for (StackTraceElement stackElem : stackElems) {
					exceptionInfos.append("\n\t\tat ")
								  .append(stackElem.getClassName())
								  .append(" ")
								  .append(stackElem.getMethodName())
								  .append(" (")
								  .append(stackElem.getFileName())
								  .append(":")
								  .append(stackElem.getLineNumber())
								  .append(")") ;
					
				}
			}
			Throwable cause = e.getCause() ;
			if (cause != null) {
				currentCauseLevel++ ;
				if (currentCauseLevel < maxCauseLevel) {
					exceptionInfos.append("\nCaused by ") ;
					addExceptionInfos(cause, exceptionInfos, currentCauseLevel, maxCauseLevel) ;
				} else {
					exceptionInfos.append("\n More cause omitted") ;
				}
			}
			exceptionInfos.append('\n') ;
		}
	}
}
