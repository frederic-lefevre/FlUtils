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

public class StringExtractor {

	private final String source ;
	private int	   		 currentIndex ;
	
	public StringExtractor(String s) {
		source 		 = s ;
		currentIndex = 0 ;
	}

	public StringExtractor(String s, int idx) {
		source 		 = s ;
		currentIndex = idx ;
	}
	
	// Extract a substring between 2 given string. Update the index to the end of the endMark (-1 if not found)
	public String extractString(String beginMark, String endMark, int startIdx) {
		
		String result = null ;
		if (source != null) {
			int idxBegin = source.indexOf(beginMark, startIdx) ;
			if (idxBegin != -1) {
				idxBegin = idxBegin + beginMark.length() ;
				
				int idxEnd = source.indexOf(endMark, idxBegin) ;
				if (idxEnd != -1) {
					result = source.substring(idxBegin, idxEnd) ;
					currentIndex = idxEnd + endMark.length() ;
				} else {
					currentIndex = -1 ;
				}
			} else {
				currentIndex = -1 ;
			}
		}
		return result ;
	}

	public int gotoString(String s) {
		currentIndex = source.indexOf(s, currentIndex) ;
		return currentIndex ;
	}
	
	public String extractString(String beginMark, String endMark) {
		if (currentIndex < 0) {
			return null ;
		} else {
			return extractString(beginMark, endMark, currentIndex) ;
		}		
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
}
