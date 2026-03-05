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

package org.fl.util.swing;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.swing.logConfiguration.LogConfigurationPane;
import org.fl.util.swing.logPane.LogHighLightListener;
import org.fl.util.swing.logPane.LogsDisplayPane;

import tools.jackson.core.JacksonException;

public class ApplicationTabbedPane extends JTabbedPane {
			
	private static final long serialVersionUID = 1L;

	private final Logger logger;
	private final ApplicationInfoPane appInfoPane;
	private final LogsDisplayPane logsDisplayPane;
	private final LogConfigurationPane logConfigurationPane;

	private final Color logTabRegularColor;
	private Color logTabHighLightColor;

	public ApplicationTabbedPane(RunningContext runningContext) {
		super();

		// Get the root logger for the application
		logger = Logger.getLogger(runningContext.getName());
		
		AdvancedProperties props = runningContext.getProps();
		logTabHighLightColor = props.getColor("appTabbedPane.logging.logTabHighLightColor", Color.RED);
		
		String language = props.getProperty("appTabbedPane.locale.language");
		String country = props.getProperty("appTabbedPane.locale.country");
		GuiTexts.init(language, country);

		// Tabbed Panel for application information
		appInfoPane = new ApplicationInfoPane(runningContext);
		addTab(GuiTexts.getText("appTabbedPane.information.tabTitle"), appInfoPane);

		// Tabbed Panel for application information
		logConfigurationPane = new LogConfigurationPane(runningContext.getName());
		addTab(GuiTexts.getText("appTabbedPane.logConfiguration.tabTitle"), logConfigurationPane);
		
		// Tabbed Panel for logs display
		logsDisplayPane = new LogsDisplayPane(runningContext);
		addTab(GuiTexts.getText("appTabbedPane.logDisplay.tabTitle"), logsDisplayPane);
		int logTabIdx = indexOfComponent(logsDisplayPane);
		if (logTabIdx > -1) {
			logTabRegularColor = getBackgroundAt(logTabIdx);
		} else {
			// should not happen
			logTabRegularColor = Color.LIGHT_GRAY;
		}

		addChangeListener(new BackUpTabChangeListener());

		LogTabColorChanger logTabColorChanger = new LogTabColorChanger();
		logsDisplayPane.addHighLightListener(logTabColorChanger);
	}
	
	private class BackUpTabChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {

			if (getSelectedComponent().equals(appInfoPane)) {
				try {
					appInfoPane.setInfos();
				} catch (JacksonException e) {
					logger.log(Level.SEVERE, "Exception setting Application info pane", e);
				}
			} else if (getSelectedComponent().equals(logsDisplayPane)) {
				logsDisplayPane.refreshLogRecordCategories();
			}
		}
	}

	private class LogTabColorChanger implements LogHighLightListener {

		@Override
		public void logsHightLighted(boolean highLight) {
			int logTabIdx = indexOfComponent(logsDisplayPane);
			if (logTabIdx > -1) {
				if (highLight) {
					setBackgroundAt(logTabIdx, logTabHighLightColor);
				} else {
					setBackgroundAt(logTabIdx, logTabRegularColor);
				}
			}
		}
	}

	public void setLogTabHighLightColor(Color logHighLightColor) {
		this.logTabHighLightColor = logHighLightColor;
	}
	
}
