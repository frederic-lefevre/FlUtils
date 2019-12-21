package com.ibm.lge.fl.util.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ibm.lge.fl.util.swing.SearcherHighLighter.SearchElement;

public class SearchableTextPane extends JPanel  {

	private static final long serialVersionUID = 1L;

	private final JScrollPane 		 scrollInfos ;
	private final JTextArea 		 textArea ;
	
	// Component and text field for search
	private final JTextField searchText ;
	private final JCheckBox  caseSensitive ;
	private final JCheckBox  ignoreAccent ;
	private final JCheckBox  ignoreFormatting ;
	private final JPanel	 searchResultPanel ;
	private final JPanel 	 commandPanel ;
	
	// Buttons to navigate the search results
	private ArrayList<JButton> occurencesButton ;
	
	private final SearcherHighLighter searcherHighLighter ;
	
	public SearchableTextPane(JTextArea ta, Logger logger) {
		
		occurencesButton = new ArrayList<JButton>() ;
		
		setLayout(new BoxLayout(this,  BoxLayout.X_AXIS)) ;
		
		// Text area panel to search from
		textArea = ta ;
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
		searchResultPanel = new JPanel() ;
		searchResultPanel.setLayout(new BoxLayout(searchResultPanel,  BoxLayout.Y_AXIS));	
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
				searcherHighLighter.searchAndHighlight(searchStr, askCaseSensitive, askIgnoreAccent, askIgnoreFormatting);
				searchResultPanel.removeAll() ;
				ArrayList<SearchElement> currentSearches = searcherHighLighter.getCurrentSearches() ;
				if ((currentSearches != null) && (currentSearches.size() > 0)) {
					SearchElement latestSearch = currentSearches.get(currentSearches.size()-1) ;
					latestSearch.diplayFirstResult() ;
					for (SearchElement searchElem : currentSearches) {
						JPanel elemPanel = new JPanel() ;
						elemPanel.setLayout(new BoxLayout(elemPanel,  BoxLayout.X_AXIS));	
						JLabel searchedStringLbl = new JLabel(searchElem.getSearchedString() + " ") ;
						JButton next = new JButton("next") ;
						occurencesButton.add(next) ;
						JLabel occurences = new JLabel(" 1 of " + searchElem.getNbOccurences() + " occurences") ;
						next.addActionListener(new OcccurenceButtonListener(searchElem, occurences));
						next.setBackground(searchElem.getHightLightColor());					
						elemPanel.add(searchedStringLbl);
						elemPanel.add(next) ;
						elemPanel.add(occurences);
						searchResultPanel.add(elemPanel) ;
					}
					validate();
					repaint();
					requestFocus();
				}
			}
		}
	}
	
	private class OcccurenceButtonListener implements ActionListener {

		private SearchElement searchElem ;
		private JLabel 		  occurences ;
		public OcccurenceButtonListener(SearchElement searchElem, JLabel occurences) {
			super();
			this.searchElem = searchElem;
			this.occurences = occurences ;
		}
	
		// Go to the next occurence
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int occurenceNum = searchElem.displayNextResult() ;
			occurences.setText(" " + occurenceNum + " of " + searchElem.getNbOccurences() + " occurences");
		}		
	}
	
	private class resetHighLightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			searchText.setText("") ;
			searcherHighLighter.removeHighlights() ;
			searchResultPanel.removeAll() ;
			occurencesButton = new ArrayList<JButton>() ;
			validate();
			repaint();
		}
	}
}
