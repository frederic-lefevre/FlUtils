package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.ibm.lge.fl.util.swing.text.TextAreaNavigation;

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
		resetHighLight.addActionListener(new resetListener());
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
	
	private class refreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			displayPane() ;			
		}		
	}
	
	private class resetListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			logRecordAreas.removeHighLight() ;			
		}
		
	}
}
