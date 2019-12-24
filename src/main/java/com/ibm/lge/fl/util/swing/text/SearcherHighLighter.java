package com.ibm.lge.fl.util.swing.text;

import java.awt.Color;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.JTextComponent;

public class SearcherHighLighter {

	private final JTextComponent textComponent ;
	private final Logger 		 shLog ;
	
	private Color[] highLightColors = {Color.CYAN, Color.LIGHT_GRAY, Color.YELLOW, Color.MAGENTA} ;
	private int currentColorIdx ;
	
	private ArrayList<SearchElement> currentSearches ;
		
	public SearcherHighLighter(JTextComponent tc, Logger l) {
		
		textComponent = tc ;
		shLog		  = l ;
		
		currentColorIdx   = -1 ;
		currentSearches   = new ArrayList<SearchElement>() ;
	}

	public void searchAndHighlight(String toFind, boolean caseSensitive, boolean ignoreAccent, boolean ignoreFormatting) {
		
        String txt = null ;
        try {
            txt = textComponent.getText() ;
        } catch (Exception e) {
            shLog.log(Level.WARNING, "Exception getting text in searchAndHightlight", e);
        }
        
       
        if ((txt != null) && (toFind != null)) {
        	
			String txtToFind, text ;
			if (caseSensitive) {
				txtToFind = toFind ;
				text 	  = txt ;
			} else {
				txtToFind = toFind.toLowerCase() ;
				text 	  = txt.toLowerCase() ;
			}
			
			if (ignoreAccent) {
				txtToFind = ignoreAccents(txtToFind) ;
				text 	  = ignoreAccents(text) ;
			}
			
			// Set highlight color
			Color hiColor ;
			if ((highLightColors == null) || (highLightColors.length == 0)) {
				hiColor = Color.LIGHT_GRAY ;
			} else if (highLightColors.length > 1) {
				currentColorIdx = (++currentColorIdx)% highLightColors.length ;
				hiColor = highLightColors[currentColorIdx] ;
			} else {
				// only one color
				hiColor = highLightColors[0] ;
			}
			
			SearchElement searchElement = new SearchElement(toFind, hiColor) ;
			currentSearches.add(searchElement) ;
						
	        // Search for pattern
	        int currIdx  =  0 ;

	        while (currIdx > -1) {
	        	
	        	if (ignoreFormatting) {
	        		TextAreaElement result = indexOfIgnoreFormat(text, txtToFind, currIdx) ;
	        		if (result.getBegin() > -1) {
		        		currIdx = result.getEnd() ;	        		
						searchElement.addTextElement(result);
		        	} else {
		        		currIdx = -1 ;
		        	}
	        	} else {
	        		int foundIdx = text.indexOf(txtToFind, currIdx) ;		        		
	        		if (foundIdx > -1) {
		        		currIdx = foundIdx + txtToFind.length() ;	        		
						searchElement.addTextElement(foundIdx, currIdx);
		        	} else {
		        		currIdx = -1 ;
		        	}
	        	} 
	        }
        }   
	}
	
	public void searchAndHighlight(String toFind, boolean caseSensitive) {	
		searchAndHighlight(toFind, caseSensitive, false, false) ;   
	}

	public void removeHighlights() {
		for (SearchElement searcheElement : currentSearches) {
			searcheElement.removeHighLights() ;
		}
		currentSearches   = new ArrayList<SearchElement>() ;		
	}
	
	public void setHighLightColors(Color[] highLightColors) {
		this.highLightColors = highLightColors;
	}

	private String ignoreAccents(String in) {
		String out = Normalizer.normalize(in, Normalizer.Form.NFD);
		return out.replaceAll("\\p{M}", ""); 
	}
	
	private TextAreaElement indexOfIgnoreFormat(String text, String toFind, int from) {
		
		TextAreaElement result = new TextAreaElement(textComponent, shLog) ;
		int currIdx  		= from ;
		boolean endOfString = ((text == null) || (text.isEmpty()) || (toFind == null) || (toFind.isEmpty())) ;
		while ((result.getBegin() < 0) && (! endOfString)) {
			
			if (currIdx < text.length()) {
				result = compareIgnoreFormat(text, toFind, currIdx) ;
				currIdx++ ;
			} else {
				endOfString = true ;
			}
		}
		return result ;
	}
	
	private TextAreaElement compareIgnoreFormat(String text, String toFind, int from) {
		
		boolean equal = true ;
		boolean blank = false ;
		int currTextIdx   = from ;
		int currToFindIdx = 0 ;
		int begin = -1 ;
		while ((equal) && (currToFindIdx < toFind.length())) {
			if (currTextIdx >= text.length()) {
				equal = false ;
			} else if (isFormatChar(text.charAt(currTextIdx))) {
				currTextIdx++ ;
				blank = true ;
				// will cause to ignore blank after formatting char
			} else if (text.charAt(currTextIdx) == toFind.charAt(currToFindIdx)) {			
				if (text.charAt(currTextIdx) == ' ') {
					blank = true ;
				} else {
					blank = false ;
				}
				if (begin < 0) {
					begin = currTextIdx ;
				}
				currTextIdx++ ;
				currToFindIdx++ ;
			} else if (blank)  {
				// previous char was a blank
				// ignore the blank after a blank in text or text to find
				if (text.charAt(currTextIdx) == ' ') {
					currTextIdx++ ;
				} else if (toFind.charAt(currToFindIdx) == ' ') {
					currToFindIdx++ ;
				} else {
					equal = false ;
				}
			} else {
				equal = false ;
			}
		}
		TextAreaElement result = new TextAreaElement(textComponent, shLog) ;
		if (equal) {
			result.setBegin(begin);
			result.setEnd(currTextIdx);
		}
		return result ;
	}
	
	private boolean isFormatChar(char c) {		
		if ((c == '\n') ||
			(c == '\t') ||
			(c == '\r') ||
			(c == '\b') ||
			(c == '\f') ) {
			return true ;
		} else {
			return false ;
		}
	}
		
	public class SearchElement extends TextAreaElementList{
		
		private String searchedString ;		
		public SearchElement(String ss, Color hlc) {
			super(textComponent, hlc, shLog);
			searchedString    = ss;
		}

		public String getSearchedString() {
			return searchedString;
		}
	}

	public ArrayList<SearchElement> getCurrentSearches() {
		return currentSearches;
	}
}
