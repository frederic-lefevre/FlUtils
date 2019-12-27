package com.ibm.lge.fl.util.swing.text;

import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class TextAreaElement {

	private final JTextComponent textComponent ;
	private final Logger		 lLog ;
	private final int 			 begin ;
	private final int 			 end ;
	
	public TextAreaElement(JTextComponent tc, int b, int e, Logger l) {
		begin 		  = b ;
		end   		  = e ;
		textComponent = tc ;
		lLog		  = l ;
		if (begin >= end) {
			lLog.severe("TextAreaElement created with a start superioor or equal to end");
		}
	}
	
	public int getBegin() { return begin; }
	public int getEnd()   {	return end;   }

	public JTextComponent getTextComponent() { return textComponent; }

	public void goToElement() {
		if ((begin > -1) && (end > -1)) {
			try {
				Rectangle viewRect = textComponent.modelToView(begin) ;
				viewRect.add(textComponent.modelToView(end)) ;
				textComponent.scrollRectToVisible(viewRect);
			} catch (BadLocationException e) {
				lLog.log(Level.WARNING, "Bad location when scrolling to search result", e);
			}
		}
	}
}
