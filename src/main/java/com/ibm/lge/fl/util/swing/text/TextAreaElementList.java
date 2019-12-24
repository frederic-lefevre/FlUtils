package com.ibm.lge.fl.util.swing.text;

import java.awt.Color;
import java.util.ArrayList;

public class TextAreaElementList {

	private Color  hightLightColor ;
	private ArrayList<TextAreaElement> textElements ;
	private int currentTextElement ;
	
	public TextAreaElementList(Color hlc) {
		hightLightColor    = hlc ;
		textElements	   = new ArrayList<TextAreaElement>() ;
		currentTextElement = 0 ;
	}

	public void addTextElement(TextAreaElement res) {
		textElements.add(res) ;
	}
	
	public Color getHightLightColor() {
		return hightLightColor;
	}
	
	public void diplayFirstResult() {
		if ((textElements != null) && (textElements.size() > 0)) {
			textElements.get(0).goToResult() ;
		}
	}
	
	// Return the number of the displayed element, starting at 1
	public int displayNextResult() {
		if ((textElements != null) && (textElements.size() > 0)) {
			currentTextElement = (currentTextElement + 1)% textElements.size() ;
			textElements.get(currentTextElement).goToResult() ;
		}
		return currentTextElement + 1 ;
	}
	
	// Return the number of the displayed element, starting at 1
	public int displayPreviousResult() {
		if ((textElements != null) && (textElements.size() > 0)) {
			currentTextElement-- ;
			if (currentTextElement == -1) {
				currentTextElement = textElements.size() - 1 ;
			}
			textElements.get(currentTextElement).goToResult() ;
		}
		return currentTextElement + 1 ;
	}
	
	public int getNbOccurences() {
		if (textElements != null) {
			return textElements.size() ;
		} else {
			return 0 ;
		}
	}
}
