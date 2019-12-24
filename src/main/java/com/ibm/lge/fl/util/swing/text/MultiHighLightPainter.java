package com.ibm.lge.fl.util.swing.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class MultiHighLightPainter implements Highlighter.HighlightPainter {

	private final DefaultHighlightPainter defaultPainter ;
	
	public MultiHighLightPainter(Color color) {
		defaultPainter = new DefaultHighlighter.DefaultHighlightPainter(color);
	}

	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		defaultPainter.paint(g, p0, p1, bounds, c);		
	}

}
