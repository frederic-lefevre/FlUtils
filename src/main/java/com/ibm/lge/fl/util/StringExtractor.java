package com.ibm.lge.fl.util;

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
