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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultHighlighter;

import org.fl.util.swing.GuiTexts;

public class SearchableTextPane extends JPanel  {

	private static final long serialVersionUID = 1L;

	private final JScrollPane scrollTextPane;
	private final JTextArea textArea;

	// Component and text field for search
	private final JTextField searchText;
	private final JCheckBox caseSensitive;
	private final JCheckBox ignoreAccent;
	private final JCheckBox ignoreFormatting;
	private final TextAreaNavigation searchResultPanel;
	private final JPanel commandPanel;

	private final SearcherHighLighter searcherHighLighter;

	private List<TextAreaElementList> currentSearches;
	
	public SearchableTextPane(JTextArea ta, Color[] highLightColors, Logger logger) {
		
		currentSearches   = new ArrayList<>();
		
		setLayout(new BoxLayout(this,  BoxLayout.X_AXIS));
		
		// Text area panel to search from
		textArea = ta;
		textArea.setHighlighter(new DefaultHighlighter());
		scrollTextPane = new JScrollPane(textArea);
		
		add(scrollTextPane);
		
		// Command panel
		commandPanel = new JPanel();
		commandPanel.setLayout(new BoxLayout(commandPanel,  BoxLayout.Y_AXIS));
		
		// Panel to search string in the log
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel,  BoxLayout.Y_AXIS));	
		searchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));		
		JPanel searchPanel1 = new JPanel();
		searchPanel1.setLayout(new BoxLayout(searchPanel1,  BoxLayout.X_AXIS));		
		searchText = new JTextField(20);
		searchText.setMaximumSize(new Dimension(400, 40));
		JButton searchButton = new JButton(GuiTexts.getText("appTabbedPane.logDisplay.searchButton"));
		searchButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JButton resetHighLightButton = new JButton(GuiTexts.getText("appTabbedPane.logDisplay.resetButton"));
		resetHighLightButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JPanel searchOptionPanel = new JPanel();
		searchOptionPanel.setLayout(new BoxLayout(searchOptionPanel,  BoxLayout.Y_AXIS));
		caseSensitive = new JCheckBox(GuiTexts.getText("appTabbedPane.logDisplay.caseSensitiveCheckBox"));
		caseSensitive.setSelected(true);
		ignoreAccent = new JCheckBox(GuiTexts.getText("appTabbedPane.logDisplay.ignoreAccentsCheckBox"));
		ignoreAccent.setSelected(false);
		ignoreFormatting = new JCheckBox(GuiTexts.getText("appTabbedPane.logDisplay.ignoreFormattingCheckBox"));
		ignoreFormatting.setSelected(false);
		searchPanel1.add(searchText);
		searchPanel1.add(searchButton);
		searchPanel1.add(resetHighLightButton);
		searchOptionPanel.add(caseSensitive);
		searchOptionPanel.add(ignoreAccent);
		searchOptionPanel.add(ignoreFormatting);
		searchPanel1.add(searchOptionPanel);
		searchPanel.add(searchPanel1);
		searchResultPanel = new TextAreaNavigation();
		searchPanel.add(searchResultPanel);
		commandPanel.add(searchPanel);

		add(commandPanel);

		searchButton.addActionListener(new searchListener());
		resetHighLightButton.addActionListener(new resetHighLightListener());

		searcherHighLighter = new SearcherHighLighter(textArea, highLightColors, logger);
	}

	public JScrollPane getScrollTextPane() {
		return scrollTextPane;
	}

	public JPanel getCommandPanel() {
		return commandPanel;
	}

	private class searchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String searchStr = searchText.getText();
			
			if ((searchStr != null) && (! searchStr.isEmpty())) {
				boolean askCaseSensitive = caseSensitive.isSelected();
				boolean askIgnoreAccent = ignoreAccent.isSelected();
				boolean askIgnoreFormatting = ignoreFormatting.isSelected();
				TextAreaElementList latestSearch = searcherHighLighter.searchAndHighlight(searchStr, askCaseSensitive, askIgnoreAccent, askIgnoreFormatting);
				if (latestSearch != null) {
					currentSearches.add(latestSearch);
					searchResultPanel.addNavigation(latestSearch, true);
					
					validate();
					repaint();
					requestFocus();
				}
			}
		}
	}
	
	private class resetHighLightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			clearSearches();
			validate();
			repaint();
		}
	}

	private void clearSearches() {
		searchText.setText("");
		for (TextAreaElementList searcheElement : currentSearches) {
			searcheElement.removeHighLights();
		}
		currentSearches.clear();
		searchResultPanel.removeAll();
	}

	public void clear() {
		textArea.setText("");
		clearSearches();
	}
}
