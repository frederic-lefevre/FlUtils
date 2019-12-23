package com.ibm.lge.fl.util.swing.logPane;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LogRecordCategoriesPane extends JPanel  {

	private static final long serialVersionUID = 1L;
	
	private final JButton showCategories ;
	private final LogRecordAreas logRecordAreas ;
	private final JPanel		 resultPane ;
	
	public LogRecordCategoriesPane(LogRecordAreas lra) {
		super() ;
		logRecordAreas = lra ;
		
		setLayout(new BoxLayout(this,  BoxLayout.Y_AXIS)) ;
		setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		JPanel ctrl = new JPanel() ;
		showCategories = new JButton("Show categories") ;
		ctrl.add(showCategories) ;
		add(ctrl) ;
		
		resultPane = new JPanel() ;
		resultPane.setLayout(new BoxLayout(resultPane,  BoxLayout.Y_AXIS)) ;
		add(resultPane) ;
		
		showCategories.addActionListener(new refreshListener());
	}

	public void displayPane() {		
		resultPane.removeAll() ;
		for (Level level : logRecordAreas.getRecordLevels()) {
			resultPane.add(oneCategoryPane(level)) ;
		}
		validate();
		repaint();
		requestFocus();
	}
	
	private JPanel oneCategoryPane(Level level) {
		
		ArrayList<LogRecordArea> recordAreas = logRecordAreas.get(level) ;
		JPanel catPane = new JPanel() ;
		catPane.setLayout(new BoxLayout(catPane,  BoxLayout.X_AXIS)) ;
		
		JLabel lvlLabel = new JLabel(level.getName()) ;
		catPane.add(lvlLabel) ;
		JButton next 	 = new JButton("next") ;
		JButton previous = new JButton("previous") ;
		JLabel occurences = new JLabel(" 1 of " + recordAreas.size() + " occurences") ;
		catPane.add(previous) ;
		catPane.add(next) ;		
		catPane.add(occurences) ;
		return catPane ;
	}
	
	private class refreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			displayPane() ;
			
		}
		
	}
}