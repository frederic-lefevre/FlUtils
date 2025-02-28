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

public class StringBuilderUtils {

	// To be used only with large string a possible negative results
	// Execution time is 2 times the regular JRE indexOf when the string is found
	public static int indexOf(StringBuilder buff, String searchedString, int from, int to) {
		
		int res = -1 ;
		
		if ((buff != null) 				  && 
			(searchedString != null) 	  && 
			(from > -1) 				  && 
			(searchedString.length() > 0) &&
			(buff.length() > 0)) {
			int searchStringSize = searchedString.length() ;
			
			if (to > buff.length() - 1) {
				to = buff.length() - 1 ;
			}
			int currIdx = from ;
			char firstSearchedChar = searchedString.charAt(0) ;
			int maxIdx = to + 1 ;
			while (currIdx < maxIdx) {
				
				if  (buff.charAt(currIdx) == firstSearchedChar) {
					
					// compare the other chars
					currIdx++ ;
					int searchedStringIdx = 1 ;
					while ((currIdx < maxIdx) && 
						   (searchedStringIdx < searchStringSize) && 
						   (buff.charAt(currIdx) == searchedString.charAt(searchedStringIdx))) {
						
						searchedStringIdx++ ;
						currIdx++ ;
					}
					
					if (searchedStringIdx == searchStringSize) {
						// found
						res = currIdx - searchedStringIdx ;
						break ;
					}
				} else {
					currIdx++ ;
				}
			}
		}
		return res ;
	}
	
	public static boolean startWithAtIndex(StringBuilder buff, String searchedString, int from) {
		
		return true ;
		
	}
}
