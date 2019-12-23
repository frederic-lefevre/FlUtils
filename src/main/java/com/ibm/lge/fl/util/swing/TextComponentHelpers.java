package com.ibm.lge.fl.util.swing;

import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class TextComponentHelpers {

	public static void moveTo(JTextComponent textComponent, int begin, int end, Logger lLog) {
		if ((begin > -1) && (end > -1)) {
			try {
				Rectangle viewRect = textComponent.modelToView(end);
				textComponent.scrollRectToVisible(viewRect);
				Rectangle viewRect2 = textComponent.modelToView(begin);
				textComponent.scrollRectToVisible(viewRect2);
			} catch (BadLocationException e) {
				lLog.log(Level.WARNING, "Bad location when scrolling to search result", e);
			}
		}
	}
}
