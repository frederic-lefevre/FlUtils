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

package org.fl.util.swing;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.DefaultCaret;

import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.json.JsonUtils;
import org.fl.util.swing.text.SearchableTextPane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class ApplicationInfoPane extends JPanel {

	private static final Logger logger = Logger.getLogger(ApplicationInfoPane.class.getName());
			
	private static final long serialVersionUID = 1L;

	private static final Color[] DEFAULT_SEARCH_HIGHLIGHTCOLORS = { Color.CYAN, Color.YELLOW, Color.MAGENTA };
	
	private final RunningContext runningContext;

	private final JTextArea infosText;
	private final JCheckBox doIpLookUp;
	private final SearchableTextPane searchableTextArea;

	public ApplicationInfoPane(RunningContext rc) {
		super();

		runningContext = rc;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));

		doIpLookUp = new JCheckBox(GuiTexts.getText("appTabbedPane.information.IPlookUp"));
		doIpLookUp.setSelected(false);
		doIpLookUp.addActionListener(new SetLookUpListener());

		infosText = new JTextArea(50, 120);

		// to always be on the top of the scrollPane
		DefaultCaret caret = (DefaultCaret) infosText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		AdvancedProperties props = runningContext.getProps();
		Color[] searchHighLightColors = props.getColors("appTabbedPane.logging.searchHighLightColors", DEFAULT_SEARCH_HIGHLIGHTCOLORS);
		searchableTextArea = new SearchableTextPane(infosText, searchHighLightColors, logger);
		
		add(doIpLookUp);
		add(searchableTextArea);
	}

	public void setInfos() throws JsonProcessingException {
		setInfos(doIpLookUp.isSelected());
	}

	private void setInfos(boolean withLookUp) throws JsonProcessingException {
		
		JsonNode infosJson = runningContext.getApplicationInfo(withLookUp);
		infosText.setText(JsonUtils.jsonPrettyPrint(infosJson));

		JScrollPane scrollInfos = searchableTextArea.getScrollTextPane();
		scrollInfos.getVerticalScrollBar().setValue(0);
		JViewport viewPort = scrollInfos.getViewport();
		viewPort.setViewPosition(new Point(0, 0));
	}

	private class SetLookUpListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (doIpLookUp.isSelected()) {
				infosText.setText(GuiTexts.getText("appTabbedPane.information.updating"));
				try {
					setInfos(true);
				} catch (JsonProcessingException e1) {
					logger.log(Level.SEVERE, "Exception setting Application pane infos", e);
				}
			}
		}

	}
}
