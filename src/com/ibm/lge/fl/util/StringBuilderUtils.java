package com.ibm.lge.fl.util;

public class StringBuilderUtils {

	public static int indexOf(StringBuilder buff, String searchedString, int from, int to) {
		
		int res = -1 ;
		int possibleRes = -1 ;
		
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
					possibleRes = currIdx ;
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
						res = possibleRes ;
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
