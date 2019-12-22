package com.ibm.lge.fl.util.swing;

import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LogRecordCategoriesPane extends JPanel  {

	private static final long serialVersionUID = 1L;
	
	private JButton showCategories ;
	
	public LogRecordCategoriesPane(LogRecordAreas lra) {
		super() ;
		
		setLayout(new BoxLayout(this,  BoxLayout.Y_AXIS)) ;
		showCategories = new JButton("Show categories") ;
		add(showCategories) ;
	}

	public void displayPane(LogRecordAreas logRecordAreas) {		
		for (Level level : logRecordAreas.getRecordLevels()) {
			add(oneCategoryPane(level)) ;
		}
	}
	
	private JPanel oneCategoryPane(Level level) {
		
		JPanel catPane = new JPanel() ;
		
		JLabel lvlLabel = new JLabel(level.getName()) ;
		catPane.add(lvlLabel) ;
		return catPane ;
	}
}
