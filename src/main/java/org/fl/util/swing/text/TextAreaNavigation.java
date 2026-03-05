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

package org.fl.util.swing.text;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fl.util.swing.GuiTexts;

public class TextAreaNavigation extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<TextAreaElementList> textAreaElementLists;
	
	public TextAreaNavigation() {

		textAreaElementLists = new ArrayList<>();

		setLayout(new GridBagLayout());
	}

	public TextAreaNavigation(List<TextAreaElementList> tal) {

		textAreaElementLists = tal;

		setLayout(new GridBagLayout());

		for (TextAreaElementList elementList : textAreaElementLists) {
			addNavigation(elementList, false);
		}
	}

	public void addNavigation(TextAreaElementList elementList, boolean displayFirst) {
		
		GridBagConstraints c = new GridBagConstraints();
		textAreaElementLists.add(elementList);

		int rowNum = textAreaElementLists.size() - 1;

		JLabel searchedStringLbl = new JLabel(elementList.getName() + " ");
		JButton next = new JButton(GuiTexts.getText("appTabbedPane.logDisplay.nextButton"));
		JButton previous = new JButton(GuiTexts.getText("appTabbedPane.logDisplay.previousButton"));
		String occLbl;
		if (elementList.getNbElements() > 0) {
			if (displayFirst) {
				elementList.diplayFirstElement();
				occLbl = GuiTexts.getText("appTabbedPane.logDisplay.occurence") + " 1 " 
						+ GuiTexts.getText("appTabbedPane.logDisplay.of") + " " + elementList.getNbElements();
			} else {
				occLbl = elementList.getNbElements() + " " + GuiTexts.getText("appTabbedPane.logDisplay.occurences");
			}
		} else {
			occLbl = GuiTexts.getText("appTabbedPane.logDisplay.nooccurence");
			next.setEnabled(false);
			previous.setEnabled(false);
		}
		JLabel occurences = new JLabel(occLbl);
		previous.setBackground(elementList.getHightLightColor());
		next.setBackground(elementList.getHightLightColor());
		previous.addActionListener(new OcccurenceButtonListener(elementList, occurences, false));
		next.addActionListener(new OcccurenceButtonListener(elementList, occurences, true));

		c.gridy = rowNum;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 3, 3, 3);

		c.gridx = 0;
		add(searchedStringLbl, c);
		c.gridx = 1;
		add(previous, c);
		c.gridx = 2;
		add(next, c);
		c.gridx = 3;
		add(occurences, c);
	}
	
	@Override
	public void removeAll() {
		super.removeAll();
		textAreaElementLists = new ArrayList<>();
	}

	private class OcccurenceButtonListener implements ActionListener {

		private TextAreaElementList elementList;
		private JLabel occurences;
		private boolean forward;

		public OcccurenceButtonListener(TextAreaElementList elementList, JLabel occurences, boolean forward) {
			super();
			this.elementList = elementList;
			this.occurences = occurences;
			this.forward = forward;
		}

		// Go to the next occurence or previous occurence
		@Override
		public void actionPerformed(ActionEvent e) {

			int occurenceNum;
			if (forward) {
				occurenceNum = elementList.displayNextElement();
			} else {
				occurenceNum = elementList.displayPreviousElement();
			}
			occurences.setText("occurence " + occurenceNum + " of " + elementList.getNbElements());
		}
	}
}
