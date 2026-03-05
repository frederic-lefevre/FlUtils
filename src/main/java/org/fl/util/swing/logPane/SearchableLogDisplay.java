/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

package org.fl.util.swing.logPane;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import org.fl.util.swing.text.SearchableTextPane;
import org.fl.util.swing.text.TextAreaElement;

public class SearchableLogDisplay implements LogDisplayComponent  {

	private final JTextArea logArea;
	private final SearchableTextPane searchableTextArea;
	private final LogRecordCategoriesPane logRecordCategoriesPane;
	private final LogRecordAreas logRecordAreas;
	private final Logger sLog;

	public SearchableLogDisplay(Level lastNonHighLighedLevel, Color[] colorsForSearchResults, Color colorForHighLevel,
			Logger l) {

		sLog = l;
		logArea = new JTextArea(50, 120);
		logArea.setEditable(false);
		logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		searchableTextArea = new SearchableTextPane(logArea, colorsForSearchResults, sLog);
		logRecordAreas = new LogRecordAreas(logArea, lastNonHighLighedLevel, colorForHighLevel, sLog);
		logRecordCategoriesPane = new LogRecordCategoriesPane(logRecordAreas);
		searchableTextArea.getCommandPanel().add(logRecordCategoriesPane);
	}

	@Override
	public void appendToText(String s) {
		logArea.append(s);		
	}

	@Override
	public int textLength() {
		return logArea.getText().length();
	}

	@Override
	public void addLogRecord(Level level, int start, int end) {
		logRecordAreas.addLogRecordArea(new TextAreaElement(logArea, start, end, sLog), level);
	}

	@Override
	public JTextComponent getTextComponent() {
		return logArea;
	}

	@Override
	public void clear() {
		logArea.setText("");
		logRecordAreas.clear();
		logRecordCategoriesPane.clear();
		searchableTextArea.clear();
	}

	public boolean hasHighlight() {
		return logRecordAreas.hasHighlight();
	}

	public void addHighLightListener(LogHighLightListener highLightListener) {
		logRecordAreas.addHighLightListener(highLightListener);
	}

	public void refreshLogRecordCategories() {
		logRecordCategoriesPane.displayPane();
	}

	public JPanel getPanel() {
		return searchableTextArea;
	}
}
