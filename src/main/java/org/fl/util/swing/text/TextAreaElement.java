/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util.swing.text;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
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
				Rectangle2D viewRect = textComponent.modelToView2D(begin) ;
				viewRect.add(textComponent.modelToView2D(end)) ;
				Rectangle r = new Rectangle() ;
				r.setRect(viewRect) ;
				textComponent.scrollRectToVisible(r);
			} catch (BadLocationException e) {
				lLog.log(Level.WARNING, "Bad location when scrolling to search result", e);
			}
		}
	}
}
