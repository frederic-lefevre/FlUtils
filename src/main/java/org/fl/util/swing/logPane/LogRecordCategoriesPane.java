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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.fl.util.swing.text.TextAreaNavigation;

public class LogRecordCategoriesPane extends JPanel  {

	private static final long serialVersionUID = 1L;
	
	private final JButton 			 showCategories ;
	private final JButton 			 resetHighLight ;
	private final LogRecordAreas 	 logRecordAreas ;
	private final TextAreaNavigation resultPane ;
	
	public LogRecordCategoriesPane(LogRecordAreas lra) {
		super() ;
		logRecordAreas = lra ;
		
		setLayout(new BoxLayout(this,  BoxLayout.Y_AXIS)) ;
		setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		JPanel ctrl = new JPanel() ;
		showCategories = new JButton("Show categories") ;
		resetHighLight = new JButton("Reset highlight") ;
		ctrl.add(showCategories) ;
		ctrl.add(resetHighLight) ;
		add(ctrl) ;
		
		resultPane = new TextAreaNavigation() ;
		add(resultPane) ;
		
		showCategories.addActionListener(new refreshListener());
		resetHighLight.addActionListener(new resetHighLightListener());
	}

	public void displayPane() {		
		resultPane.removeAll() ;
		for (Level level : logRecordAreas.getRecordLevels()) {
			resultPane.addNavigation(logRecordAreas.getLogRecordsForThisLevel(level), false) ;
		}
		validate();
		repaint();
		requestFocus();
	}
	
	public void clear() {
		resultPane.removeAll() ;
	}
	
	private class refreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			displayPane() ;			
		}		
	}
	
	private class resetHighLightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			logRecordAreas.removeHighLight() ;			
		}		
	}
}
