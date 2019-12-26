package com.ibm.lge.fl.util.swing.text;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TextAreaNavigation extends JPanel {

	private static final long serialVersionUID = 1L;

	private ArrayList<TextAreaElementList> textAreaElementLists ;
	
	public TextAreaNavigation() {
		
		textAreaElementLists = new ArrayList<TextAreaElementList>() ;
		
		setLayout(new GridBagLayout()) ;
	}
	
	public TextAreaNavigation(ArrayList<TextAreaElementList> tal) {
		
		textAreaElementLists = tal ;
		
		setLayout(new GridBagLayout()) ;
		
		for (TextAreaElementList elementList : textAreaElementLists) {
			addNavigation(elementList, false) ;
		}
	}

	public void addNavigation(TextAreaElementList elementList, boolean displayFirst) {
		GridBagConstraints c = new GridBagConstraints() ;
		textAreaElementLists.add(elementList) ;

		int rowNum = textAreaElementLists.size() - 1 ;
		
		JLabel searchedStringLbl = new JLabel(elementList.getName() + " ") ;
		JButton next 	 = new JButton("next") ;
		JButton previous = new JButton("previous") ;
		String occLbl ;
		if (displayFirst) {
			elementList.diplayFirstElement() ;
			occLbl = "occurence 1 of " + elementList.getNbElements() ;
		} else {
			occLbl = elementList.getNbElements() + " occurences" ;
		}
		JLabel occurences = new JLabel(occLbl) ;
		previous.setBackground(elementList.getHightLightColor());
		next.setBackground(elementList.getHightLightColor());
		previous.addActionListener(new OcccurenceButtonListener(elementList, occurences, false));
		next.addActionListener(new OcccurenceButtonListener(elementList, occurences, true));
		c.gridx = 0 ;
		c.gridy = rowNum ;
		c.fill = GridBagConstraints.HORIZONTAL ;
		add(searchedStringLbl, c) ;
		c.gridx = 1 ;
		add(previous, c) ;
		c.gridx = 2 ;
		add(next, c) ;
		c.gridx = 3 ;
		add(occurences, c) ;
	}
	
	private class OcccurenceButtonListener implements ActionListener {

		private TextAreaElementList elementList ;
		private JLabel 		  		occurences ;
		private boolean		  		forward ;
		public OcccurenceButtonListener(TextAreaElementList elementList, JLabel occurences, boolean forward) {
			super();
			this.elementList = elementList;
			this.occurences  = occurences ;
			this.forward	 = forward ;
		}
	
		// Go to the next occurence or previous occurence
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int occurenceNum ;
			if (forward) {
				occurenceNum = elementList.displayNextElement() ;
			} else {
				occurenceNum = elementList.displayPreviousElement() ;
			}
			occurences.setText("occurence " + occurenceNum + " of " + elementList.getNbElements());
		}		
	}
}
