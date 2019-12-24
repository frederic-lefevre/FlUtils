package com.ibm.lge.fl.util.swing.text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class TextAreaElementList {

	private final JTextComponent textComponent ;
	private final Logger		 lLog ;
	private Highlighter highLighter ;	
	private Color  		hightLightColor ;
	private ArrayList<TextAreaElement> textElements ;
	private int currentTextElement ;
	
	public TextAreaElementList(JTextComponent tc, Color hlc, Logger l) {
		textComponent 	   = tc ;
		hightLightColor    = hlc ;
		lLog			   = l ;
		textElements	   = new ArrayList<TextAreaElement>() ;
		currentTextElement = 0 ;
		if (hightLightColor != null) {
			highLighter = textComponent.getHighlighter() ;
		}
	}

	public void addTextElement(TextAreaElement res) {
		if (res.getTextComponent().equals(textComponent)) {
			textElements.add(res) ;
		} else {
			lLog.severe("TextAreaElement add to list with a different JTextComponent");
		}
	}
	
	public void addTextElement(int b, int e) {
		textElements.add(new TextAreaElement(textComponent, b, e, lLog)) ;
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
