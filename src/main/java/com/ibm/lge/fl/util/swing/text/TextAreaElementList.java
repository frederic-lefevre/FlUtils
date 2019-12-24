package com.ibm.lge.fl.util.swing.text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

public class TextAreaElementList {

	private JTextComponent 			   textComponent ;
	private Logger		 			   lLog ;
	private Highlighter 			   highLighter ;
	private HighlightPainter 		   painter ;
	private Color  					   hightLightColor ;
	private ArrayList<TextAreaElement> textElements ;
	private int 					   currentTextElement ;
	private ArrayList<Object> 		   currentHighLights ;
	
	public TextAreaElementList(JTextComponent tc, Logger l) {
		init(tc, null, l) ;
	}
	
	public TextAreaElementList(JTextComponent tc, Color hlc, Logger l) {
		init(tc, hlc, l) ;
	}
	
	private void init(JTextComponent tc, Color hlc, Logger l) {
		textComponent 	   = tc ;
		hightLightColor    = hlc ;
		lLog			   = l ;
		textElements	   = new ArrayList<TextAreaElement>() ;
		currentTextElement = 0 ;
		highLighter 	   = textComponent.getHighlighter() ;
		if (hightLightColor != null) {
			painter			   = new MultiHighLightPainter(hightLightColor) ;
			currentHighLights  = new ArrayList<Object>() ;
		}
	}

	public void addTextElement(TextAreaElement res) {
		if (res.getTextComponent().equals(textComponent)) {
			textElements.add(res) ;
			if (hightLightColor != null) {
				try {
					currentHighLights.add(highLighter.addHighlight(res.getBegin(), res.getEnd(), painter)) ;
				} catch (BadLocationException e) {
					lLog.log(Level.WARNING, "Bad location exception when highlightning pos=" + res.getBegin() + " y=" + res.getEnd(), e);
				}
			}
		} else {
			lLog.severe("TextAreaElement add to list with a different JTextComponent");
		}
	}
	
	public void removeHighLights() {
		if (currentHighLights != null) {
			for (Object oneHighLight : currentHighLights) {
				highLighter.removeHighlight(oneHighLight) ; 
			}
			currentHighLights  = new ArrayList<Object>() ;
		}
	}
	
	public void addTextElement(int b, int e) {
		addTextElement(new TextAreaElement(textComponent, b, e, lLog)) ;
	}
	
	public Color getHightLightColor() {
		return hightLightColor;
	}
	
	public void diplayFirstElement() {
		if ((textElements != null) && (textElements.size() > 0)) {
			textElements.get(0).goToResult() ;
		}
	}
	
	// Return the number of the displayed element, starting at 1
	public int displayNextElement() {
		if ((textElements != null) && (textElements.size() > 0)) {
			currentTextElement = (currentTextElement + 1)% textElements.size() ;
			textElements.get(currentTextElement).goToResult() ;
		}
		return currentTextElement + 1 ;
	}
	
	// Return the number of the displayed element, starting at 1
	public int displayPreviousElement() {
		if ((textElements != null) && (textElements.size() > 0)) {
			currentTextElement-- ;
			if (currentTextElement == -1) {
				currentTextElement = textElements.size() - 1 ;
			}
			textElements.get(currentTextElement).goToResult() ;
		}
		return currentTextElement + 1 ;
	}
	
	public int getNbElements() {
		if (textElements != null) {
			return textElements.size() ;
		} else {
			return 0 ;
		}
	}
}
