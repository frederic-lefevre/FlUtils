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
	private ArrayList<TextAreaElement> textElements ;
	private int 					   currentTextElement ;
	
	private Highlighter 			   highLighter ;
	
	// HighLights for all elements
	private HighlightPainter 		   painter ;
	private Color  					   highLightColor ;
	private ArrayList<Object> 		   currentHighLights ;
		
	// HighLights for current element
	private Object					   currentElementHighLight ;
	private Color  					   currentElementHighLightColor ;
	private HighlightPainter 		   currentElementPainter ;
	
	public TextAreaElementList(JTextComponent tc, Logger l) {
		init(tc, null, l) ;
	}
	
	public TextAreaElementList(JTextComponent tc, Color hlc, Logger l) {
		init(tc, hlc, l) ;
	}
	
	private void init(JTextComponent tc, Color hlc, Logger l) {
		textComponent 	   = tc ;
		highLightColor     = hlc ;
		lLog			   = l ;
		textElements	   = new ArrayList<TextAreaElement>() ;
		currentTextElement = 0 ;
		highLighter 	   = textComponent.getHighlighter() ;
		if (highLightColor != null) {
			painter			   = new MultiHighLightPainter(highLightColor) ;
			currentHighLights  = new ArrayList<Object>() ;
		}
		currentElementHighLight = null ;
		currentElementHighLightColor = Color.GREEN ;
		if (currentElementHighLightColor != null) {
			currentElementPainter = new MultiHighLightPainter(currentElementHighLightColor) ;
		}
	}

	public void addTextElement(TextAreaElement res) {
		if (res.getTextComponent().equals(textComponent)) {
			textElements.add(res) ;
			if (highLightColor != null) {
				Object hiliRef = addHighLightToTextElement(res, painter) ;
				if (hiliRef != null) {
					currentHighLights.add(hiliRef) ;
				}
			}
		} else {
			lLog.severe("TextAreaElement add to list with a different JTextComponent");
		}
	}
	
	public void removeHighLights() {
		removeCurrentElementHighLight() ;
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
		return highLightColor;
	}
	
	public void diplayFirstElement() {
		removeCurrentElementHighLight() ;
		if ((textElements != null) && (textElements.size() > 0)) {
			TextAreaElement firstElement = textElements.get(0) ;
			firstElement.goToElement() ;
			currentElementHighLight = addHighLightToTextElement(firstElement, currentElementPainter) ;
		}
	}
	
	// Return the number of the displayed element, starting at 1
	public int displayNextElement() {
		removeCurrentElementHighLight() ;
		if ((textElements != null) && (textElements.size() > 0)) {
			currentTextElement = (currentTextElement + 1)% textElements.size() ;
			textElements.get(currentTextElement).goToElement() ;
			currentElementHighLight = addHighLightToTextElement(textElements.get(currentTextElement), currentElementPainter) ;
		}
		return currentTextElement + 1 ;
	}
	
	// Return the number of the displayed element, starting at 1
	public int displayPreviousElement() {
		removeCurrentElementHighLight() ;
		if ((textElements != null) && (textElements.size() > 0)) {
			currentTextElement-- ;
			if (currentTextElement == -1) {
				currentTextElement = textElements.size() - 1 ;
			}
			textElements.get(currentTextElement).goToElement() ;
			currentElementHighLight = addHighLightToTextElement(textElements.get(currentTextElement), currentElementPainter) ;
		}
		return currentTextElement + 1 ;
	}
	
	private void removeCurrentElementHighLight() {
		if ((highLighter != null) && (currentElementHighLight != null)) {
			highLighter.removeHighlight(currentElementHighLight) ;
			currentElementHighLight = null ;
		}
	}
	
	private Object addHighLightToTextElement(TextAreaElement element,  HighlightPainter elementPainter) {
		if (elementPainter != null) {
			try {
				return highLighter.addHighlight(element.getBegin(), element.getEnd(), elementPainter) ;
			} catch (BadLocationException e) {
				lLog.log(Level.WARNING, "Bad location exception when highlightning position x=" + element.getBegin() + " y=" + element.getEnd(), e);
				return null ;
			}
		} else {
			return null ;
		}
	}
	
	public int getNbElements() {
		if (textElements != null) {
			return textElements.size() ;
		} else {
			return 0 ;
		}
	}
}
