package com.ibm.lge.fl.util.swing.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class SearchableTextPane extends JPanel  {

	private static final long serialVersionUID = 1L;

	private final JScrollPane 		 scrollInfos ;
	private final JTextArea 		 textArea ;
	
	// Component and text field for search
	private final JTextField 		 searchText ;
	private final JCheckBox  		 caseSensitive ;
	private final JCheckBox  		 ignoreAccent ;
	private final JCheckBox  		 ignoreFormatting ;
	private final TextAreaNavigation searchResultPanel ;
	private final JPanel 	 		 commandPanel ;
	
	private final SearcherHighLighter searcherHighLighter ;
	
	public SearchableTextPane(JTextArea ta, Logger logger) {
		
		setLayout(new BoxLayout(this,  BoxLayout.X_AXIS)) ;
		
		// Text area panel to search from
		textArea = ta ;
		textArea.setHighlighter(new DefaultHighlighter());
		scrollInfos = new JScrollPane(textArea) ;
		
		add(scrollInfos) ;
		
		// Command panel
		commandPanel = new JPanel() ;
		commandPanel.setLayout(new BoxLayout(commandPanel,  BoxLayout.Y_AXIS));
		
		// Panel to search string in the log
		JPanel searchPanel = new JPanel() ;
		searchPanel.setLayout(new BoxLayout(searchPanel,  BoxLayout.Y_AXIS));	
		searchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));		
		JPanel searchPanel1 = new JPanel() ;
		searchPanel1.setLayout(new BoxLayout(searchPanel1,  BoxLayout.X_AXIS));		
		searchText = new JTextField(20) ;
		searchText.setMaximumSize(new Dimension(400, 40));
		JButton searchButton = new JButton("Search") ;
		searchButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)) ;
		JButton resetHighLightButton = new JButton("Reset") ;
		resetHighLightButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)) ;
		JPanel searchOptionPanel = new JPanel() ;
		searchOptionPanel.setLayout(new BoxLayout(searchOptionPanel,  BoxLayout.Y_AXIS));
		caseSensitive = new JCheckBox("Case sensitive") ;
		caseSensitive.setSelected(true);
		ignoreAccent = new JCheckBox("Ignore accents") ;
		ignoreAccent.setSelected(false);
		ignoreFormatting = new JCheckBox("Ignore formatting") ;
		ignoreFormatting.setSelected(false);
		searchPanel1.add(searchText) ;
		searchPanel1.add(searchButton) ;
		searchPanel1.add(resetHighLightButton) ;	
		searchOptionPanel.add(caseSensitive) ;
		searchOptionPanel.add(ignoreAccent) ;
		searchOptionPanel.add(ignoreFormatting) ;
		searchPanel1.add(searchOptionPanel) ;
		searchPanel.add(searchPanel1);
		searchResultPanel = new TextAreaNavigation() ;
		searchPanel.add(searchResultPanel) ;
		commandPanel.add(searchPanel) ;
		
		add(commandPanel) ;
		
		searchButton.addActionListener(new searchListener()) ;
		resetHighLightButton.addActionListener(new resetHighLightListener()) ;
		
		searcherHighLighter = new SearcherHighLighter(textArea, logger) ;
	}

	public JPanel getCommandPanel() {
		return commandPanel;
	}

	private class searchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String searchStr = searchText.getText() ;
			
			if ((searchStr != null) && (! searchStr.isEmpty())) {
				boolean askCaseSensitive	= caseSensitive.isSelected() ;
				boolean askIgnoreAccent 	= ignoreAccent.isSelected() ;
				boolean askIgnoreFormatting = ignoreFormatting.isSelected() ;
				TextAreaElementList latestSearch = searcherHighLighter.searchAndHighlight(searchStr, askCaseSensitive, askIgnoreAccent, askIgnoreFormatting);
				if (latestSearch != null) {
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
			searchText.setText("") ;
			searcherHighLighter.removeHighlights() ;
			searchResultPanel.removeAll() ;
			validate();
			repaint();
		}
	}
}
