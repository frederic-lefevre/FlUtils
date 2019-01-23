package com.ibm.lge.fl.util.swing;

import java.awt.Color;
import java.text.Normalizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class SearcherHighLighter {

	private JTextComponent textComponent ;
	private Highlighter    highLighter ;
	
	private Logger shLog ;
	
	private Color[] highLightColors = {Color.CYAN, Color.LIGHT_GRAY, Color.YELLOW, Color.MAGENTA} ;
	private int currentColorIdx ;
	
	public SearcherHighLighter(JTextComponent tc, Logger l) {
		
		textComponent = tc ;
		shLog		  = l ;
		
		highLighter = textComponent.getHighlighter() ;
		
		currentColorIdx = -1 ;

	}

	public void searchAndHighlight(String toFind, boolean caseSensitive) {
		
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
			
			DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(hiColor) ;
			
	        // Search for pattern
	        int currIdx = 0;
	        try {
		        while (currIdx > -1) {
		        	
		        	int foundIdx = text.indexOf(txtToFind, currIdx) ;
		        	if (foundIdx > -1) {
		        		currIdx = foundIdx + txtToFind.length() ;	        		
						highLighter.addHighlight(foundIdx, currIdx, painter) ;					
		        	} else {
		        		currIdx = -1 ;
		        	}
		        }
	        } catch (BadLocationException e) {
				shLog.log(Level.WARNING, "Bad location exception when highlightning pos=" + currIdx, e);
			}
        }   
	}

	public void removeHighlights() {
		highLighter.removeAllHighlights() ; 
	}
	
	public void setHighLightColors(Color[] highLightColors) {
		this.highLightColors = highLightColors;
	}

	private String ignoreAccents(String in) {
		String out = Normalizer.normalize(in, Normalizer.Form.NFD);
		return out.replaceAll("\\p{M}", ""); 
	}
}
