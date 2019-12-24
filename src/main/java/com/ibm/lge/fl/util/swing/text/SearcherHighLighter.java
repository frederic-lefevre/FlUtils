package com.ibm.lge.fl.util.swing.text;

import java.awt.Color;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

public class SearcherHighLighter {

	private final JTextComponent textComponent ;
	private final Highlighter    highLighter ;	
	private final Logger 		 shLog ;
	
	private Color[] highLightColors = {Color.CYAN, Color.LIGHT_GRAY, Color.YELLOW, Color.MAGENTA} ;
	private int currentColorIdx ;
	
	private ArrayList<SearchElement> currentSearches ;
	
	private ArrayList<Object> currentHighLights ;
	
	public SearcherHighLighter(JTextComponent tc, Logger l) {
		
		textComponent = tc ;
		shLog		  = l ;
		
		highLighter = textComponent.getHighlighter() ;
		
		currentColorIdx   = -1 ;
		currentSearches   = new ArrayList<SearchElement>() ;
		currentHighLights = new ArrayList<Object>() ;

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
			
			HighlightPainter matchPainter =  new MultiHighLightPainter(hiColor) ;
			
	        // Search for pattern
	        int currIdx  =  0 ;
	        try {
		        while (currIdx > -1) {
		        	
		        	TextAreaElement result ;
		        	if (ignoreFormatting) {
		        		result = indexOfIgnoreFormat(text, txtToFind, currIdx) ;
		        		if (result.getBegin() > -1) {
			        		currIdx = result.getEnd() ;	        		
			        		currentHighLights.add(highLighter.addHighlight(result.getBegin(), result.getEnd(), matchPainter)) ;
							searchElement.addSearchResult(result);
			        	} else {
			        		currIdx = -1 ;
			        	}
		        	} else {
		        		int foundIdx = text.indexOf(txtToFind, currIdx) ;		        		
		        		if (foundIdx > -1) {
			        		currIdx = foundIdx + txtToFind.length() ;	        		
			        		currentHighLights.add(highLighter.addHighlight(foundIdx, currIdx, matchPainter)) ;
							result = new TextAreaElement(textComponent, foundIdx, currIdx) ;
							searchElement.addSearchResult(result);
			        	} else {
			        		currIdx = -1 ;
			        	}
		        	} 
		        }
	        } catch (BadLocationException e) {
				shLog.log(Level.WARNING, "Bad location exception when highlightning pos=" + currIdx, e);
			}
        }   
	}
	
	public void searchAndHighlight(String toFind, boolean caseSensitive) {
		
		searchAndHighlight(toFind, caseSensitive, false, false) ;
   
	}

	public void removeHighlights() {
		for (Object oneHighLight : currentHighLights) {
			highLighter.removeHighlight(oneHighLight) ; 
		}
		currentSearches   = new ArrayList<SearchElement>() ;
		currentHighLights = new ArrayList<Object>() ;
	}
	
	public void setHighLightColors(Color[] highLightColors) {
		this.highLightColors = highLightColors;
	}

	private String ignoreAccents(String in) {
		String out = Normalizer.normalize(in, Normalizer.Form.NFD);
		return out.replaceAll("\\p{M}", ""); 
	}
	
	private TextAreaElement indexOfIgnoreFormat(String text, String toFind, int from) {
		
		TextAreaElement result = new TextAreaElement(textComponent) ;
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
		TextAreaElement result = new TextAreaElement(textComponent) ;
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
	
	
	public class SearchElement {
		
		private Color  hightLightColor ;
		private String searchedString ;
		private ArrayList<TextAreaElement> searchResults ;
		private int currentResultView ;
		
		public SearchElement(String ss, Color hlc) {
			super();
			searchedString    = ss;
			hightLightColor   = hlc;
			searchResults	  = new ArrayList<TextAreaElement>() ;
			currentResultView = 0 ;
		}

		public Color getHightLightColor() {
			return hightLightColor;
		}

		public String getSearchedString() {
			return searchedString;
		}
		
		public void addSearchResult(TextAreaElement res) {
			searchResults.add(res) ;
		}
		
		public void diplayFirstResult() {
			if ((searchResults != null) && (searchResults.size() > 0)) {
				searchResults.get(0).goToResult(shLog) ;
			}
		}
		
		// Return the number of the displayed result, starting at 1
		public int displayNextResult() {
			if ((searchResults != null) && (searchResults.size() > 0)) {
				currentResultView = (currentResultView + 1)% searchResults.size() ;
				searchResults.get(currentResultView).goToResult(shLog) ;
			}
			return currentResultView + 1 ;
		}
		
		// Return the number of the displayed result, starting at 1
		public int displayPreviousResult() {
			if ((searchResults != null) && (searchResults.size() > 0)) {
				currentResultView-- ;
				if (currentResultView == -1) {
					currentResultView = searchResults.size() - 1 ;
				}
				searchResults.get(currentResultView).goToResult(shLog) ;
			}
			return currentResultView + 1 ;
		}
		
		public int getNbOccurences() {
			if (searchResults != null) {
				return searchResults.size() ;
			} else {
				return 0 ;
			}
		}
	}

	public ArrayList<SearchElement> getCurrentSearches() {
		return currentSearches;
	}
}
