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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.charset.Charset;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fl.util.LoggerUtils;


public class CreateHandlerPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final String[] SUPPORTED_ENCODING = Charset.availableCharsets().keySet().toArray(new String[0]);
	protected static final Font font = new Font("Verdana", Font.BOLD, 14);
	
	private Level selectedLevel;
	private String selectedFormatterName;
	private String selectedEncoding;
	private final JComboBox<Level> handlerLevelChoice;
	private final FormatterComboBox formatterChoice;
	private final JComboBox<String> encodingChoice;
	
	public CreateHandlerPane() {
		super();
		
		selectedLevel = null;
		selectedFormatterName = null;
		selectedEncoding = null;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel levelPane = new JPanel();
		JLabel levelTitle = new JLabel();
		levelTitle.setFont(font);
		levelTitle.setText("Level: ");
		levelPane.add(levelTitle);
		
		handlerLevelChoice = new JComboBox<>(LoggerUtils.LEVELS);
		handlerLevelChoice.addItemListener(new HandlerLevelListener());
		handlerLevelChoice.setSelectedItem(null);
		levelPane.add(handlerLevelChoice);
		levelPane.setAlignmentX(CENTER_ALIGNMENT);
		add(levelPane);
		
		JPanel formatterPane = new JPanel();
		JLabel formatterTitle = new JLabel();
		formatterTitle.setFont(font);
		formatterTitle.setText("Formatter: ");
		formatterPane.add(formatterTitle);
		
		formatterChoice = new FormatterComboBox();
		formatterChoice.addItemListener(new FormatterListener());
		formatterChoice.setSelectedItem(null);
		formatterPane.add(formatterChoice);
		formatterPane.setAlignmentX(CENTER_ALIGNMENT);
		add(formatterPane);
		
		JPanel encodingPane = new JPanel();
		JLabel encodingTitle = new JLabel();
		encodingTitle.setFont(font);
		encodingTitle.setText("Encoding: ");
		encodingPane.add(encodingTitle);
		encodingChoice = new JComboBox<String>(SUPPORTED_ENCODING);
		encodingChoice.addItemListener(new EncodingListener());
		encodingChoice.setSelectedItem(null);
		encodingPane.add(encodingChoice);
		add(encodingPane);
	}
	
	public Level getSelectedLevel() {
		return selectedLevel;
	}

	public String getSelectedFormatterName() {
		return selectedFormatterName;
	}
	
	public String getSelectedEncoding() {
		return selectedEncoding;
	}

	private class  HandlerLevelListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selectedLevel = (Level)(handlerLevelChoice.getSelectedItem());
			}	
		}	
	}
	
	private class FormatterListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selectedFormatterName = (String)formatterChoice.getSelectedItem();
			}
		}
	}
		
	private class EncodingListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selectedEncoding = (String)encodingChoice.getSelectedItem();
			}
		}
	}
}
