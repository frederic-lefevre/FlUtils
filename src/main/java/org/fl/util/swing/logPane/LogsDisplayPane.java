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

package org.fl.util.swing.logPane;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;

public class LogsDisplayPane  extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private final ArrayList<SearchableLogDisplay> searchableLogDisplays;
	private final TextAreaLogHandler textAreaLogHandler;

	private int oldLogLastNum;
	private int currentLogDisplayIndex;
	private SearchableLogDisplay currentLogDisplay;

	private final static Color[] DEFAULT_SEARCH_HIGHLIGHTCOLORS = { Color.CYAN, Color.YELLOW, Color.MAGENTA };

	private final Color logTabSelectedColor;
	private final Color logTabRegularColor;

	private final int logDisplaySubTabNumber;
	private final int logDisplayMaxLength;
	
	public LogsDisplayPane(RunningContext runningContext) {
		
		super();
		
		AdvancedProperties props = runningContext.getProps();
		Level lastNonHighLightedLevel = props.getLevel("appTabbedPane.logging.lastNonHighLighedLevel", Level.INFO);
		Color recordHighLightColor = props.getColor("appTabbedPane.logging.recordHighLightColor", Color.PINK) ;
		logDisplaySubTabNumber = props.getInt("appTabbedPane.logging.subTabNumber", 3);
		logDisplayMaxLength = props.getInt("appTabbedPane.logging.logDisplayMaxLength", 100000);
		logTabSelectedColor = props.getColor("appTabbedPane.logging.logTabSelectedColor", Color.GREEN);
		
		Color[] searchHighLightColors = props.getColors("appTabbedPane.logging.searchHighLightColors", DEFAULT_SEARCH_HIGHLIGHTCOLORS);
		
		Logger logger = Logger.getLogger(runningContext.getName());
		searchableLogDisplays = new ArrayList<SearchableLogDisplay>();
		for (int i = 0; i < logDisplaySubTabNumber; i++) {
			SearchableLogDisplay logDisplay = new SearchableLogDisplay(lastNonHighLightedLevel, searchHighLightColors, recordHighLightColor, logger);
			searchableLogDisplays.add(logDisplay);
			add(logDisplay.getPanel());
		}
		oldLogLastNum = 1;
		currentLogDisplayIndex = 0;
		currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex);

		logTabRegularColor = getBackgroundAt(0);
		selectCurrentLogDisplay();

		textAreaLogHandler = new TextAreaLogHandler(currentLogDisplay, new SearchLogDisplayChanger());
		Level level = logger.getLevel();
		boolean noLevelDefined = level == null;
		if (noLevelDefined) {
			// The textAreaLogHandler will display everything 
			level = Level.ALL;
		}
		textAreaLogHandler.setLevel(level);
		textAreaLogHandler.setLogDisplayMaxLength(logDisplayMaxLength);
		textAreaLogHandler.setFormatter(runningContext.getCommonLogFormatter());
		logger.addHandler(textAreaLogHandler);
		if (noLevelDefined) {
			// Log the warning after adding the handler so that it gets displayed in GUI
			logger.warning("The application logger named " + runningContext.getName() + " has no level defined");
		}
	}

	public boolean hasHighlight() {
		return currentLogDisplay.hasHighlight();
	}

	public void addHighLightListener(LogHighLightListener highLightListener) {
		for (SearchableLogDisplay logDisplay : searchableLogDisplays) {
			logDisplay.addHighLightListener(highLightListener);
		}
	}

	public void refreshLogRecordCategories() {
		currentLogDisplay.refreshLogRecordCategories();
	}

	private void selectCurrentLogDisplay() {
		setSelectedIndex(currentLogDisplayIndex);
		int logTabIdx = indexOfComponent(currentLogDisplay.getPanel());
		setBackgroundAt(logTabIdx, logTabSelectedColor);
		setTitleAt(logTabIdx, "current");
	}
	
	private class SearchLogDisplayChanger implements LogDisplayChanger {

		@Override
		public LogDisplayComponent changeLogDisplayComponent() {
			int logTabIdx = indexOfComponent(currentLogDisplay.getPanel());
			setBackgroundAt(logTabIdx, logTabRegularColor);
			setTitleAt(logTabIdx, Integer.toString(oldLogLastNum));
			oldLogLastNum++;
			currentLogDisplayIndex = (currentLogDisplayIndex + 1) % logDisplaySubTabNumber;
			currentLogDisplay = searchableLogDisplays.get(currentLogDisplayIndex);
			currentLogDisplay.clear();
			selectCurrentLogDisplay();
			return currentLogDisplay;
		}

	}
}
