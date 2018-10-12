package com.ibm.lge.fl.util;

public class StringBuilderUtils {

	public static int indexOf(StringBuilder buff, String searchedString, int from, int to) {
		
		int res = -1 ;
		
		int currIdx = from ;
		while (currIdx < to) {
			
			if  (buff.charAt(currIdx) == searchedString.charAt(0)) {
				
			} else {
				currIdx++ ;
			}
		}
		
		return res ;
	}
	
	public static boolean startWithAtIndex(StringBuilder buff, String searchedString, int from) {
		
		return true ;
		
	}
}
