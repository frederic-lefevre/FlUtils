package com.ibm.lge.fl.util.swing.text;

import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class TextAreaElement {

	private final JTextComponent textComponent ;
	private final Logger		 lLog ;
	private int begin ;
	private int end ;
	
	public TextAreaElement(JTextComponent tc, Logger l) {
		begin 		  = -1 ;
		end   		  = -1 ;
		textComponent = tc ;
		lLog		  = l ;
	}

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

	public void setBegin(int begin) { this.begin = begin; }
	public void setEnd  (int end)   { this.end   = end;   }
	
	public JTextComponent getTextComponent() { return textComponent; }

	public void moveArea(int move) {
		
		begin = begin + move ;
		end   = end   + move ;		
		if (begin < 0) {
			lLog.severe("TextAreaElement moved to a negative position");
		}
	}
	
	public void goToResult() {
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
