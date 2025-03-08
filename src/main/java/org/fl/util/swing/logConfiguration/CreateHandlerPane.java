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

package org.fl.util.swing.logConfiguration;

import java.awt.Font;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fl.util.LoggerUtils;


public class CreateHandlerPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final Font font = new Font("Verdana", Font.BOLD, 14);
	
	public CreateHandlerPane() {
		super();
		
		JPanel levelPane = new JPanel();
		JLabel levelTitle = new JLabel();
		levelTitle.setFont(font);
		levelTitle.setText("Level: ");
		levelPane.add(levelTitle);
		
		JComboBox<Level> handlerLevelChoice = new JComboBox<>(LoggerUtils.LEVELS);
//		handlerLevelChoice.addItemListener(new HandlerLevelListener());
		levelPane.add(handlerLevelChoice);
		levelPane.setAlignmentX(CENTER_ALIGNMENT);
		add(levelPane);
		
		JPanel formatterPane = new JPanel();
		JLabel formatterTitle = new JLabel();
		formatterTitle.setFont(font);
		formatterTitle.setText("Formatter: ");
		formatterPane.add(formatterTitle);
		
		JComboBox<String> formatterChoice = new FormatterComboBox();
//		formatterChoice.addItemListener(new FormatterListener());
		formatterPane.add(formatterChoice);
		formatterPane.setAlignmentX(CENTER_ALIGNMENT);
		add(formatterPane);
		
	}

}
