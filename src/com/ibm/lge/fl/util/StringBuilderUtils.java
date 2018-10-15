package com.ibm.lge.fl.util;

public class StringBuilderUtils {

	public static int indexOf(StringBuilder buff, String searchedString, int from, int to) {
		
		int res = -1 ;
		
		if ((buff != null) 				  && 
			(searchedString != null) 	  && 
			(from > -1) 				  && 
			(searchedString.length() > 0) &&
			(buff.length() > 0)) {
			int searchStringSize = searchedString.length() ;
			
			int currIdx = from ;
			while ((currIdx < to) && (res == -1)) {
				
				if  (buff.charAt(currIdx) == searchedString.charAt(0)) {
					
					// compare the other chars
					res = currIdx ;
					currIdx++ ;
					int searchedStringIdx = 1 ;
					while ((currIdx < to) && 
						   (searchedStringIdx < searchStringSize) && 
						   (buff.charAt(currIdx) == searchedString.charAt(searchedStringIdx))) {
						
						searchedStringIdx++ ;
						currIdx++ ;
					}
					
					if (searchedStringIdx != searchStringSize) {
						// not found
						res = -1 ;
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
