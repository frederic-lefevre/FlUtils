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
import java.util.Arrays;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fl.util.JsonLogFormatter;
import org.fl.util.LoggerUtils;
import org.fl.util.PlainLogFormatter;

public class ConfigureHandlerPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Font font = new Font("Verdana", Font.BOLD, 14);
	
	private static final String[] AVAILABLE_FORMATTER_NAMES = {
			SimpleFormatter.class.getName(),
			XMLFormatter.class.getName(),
			JsonLogFormatter.class.getName(), 
			PlainLogFormatter.class.getName()};
	
	private final JComboBox<Level> handlerLevelChoice;
	private final JComboBox<String> formatterChoice;
	private final Handler handlerToConfigure;
	
	public ConfigureHandlerPane(Handler handler) {
		
		super();
		handlerToConfigure = handler;
		
		JLabel handlerTitle = new JLabel();
		handlerTitle.setFont(font);		
		handlerTitle.setText("Handler " + handlerToConfigure.getClass().getName());
		add(handlerTitle);
		
		handlerLevelChoice = new JComboBox<>(LoggerUtils.LEVELS);
		handlerLevelChoice.setSelectedItem(handler.getLevel());
		handlerLevelChoice.addItemListener(new HandlerLevelListener());
		add(handlerLevelChoice);
		
		
		JLabel formatterTitle = new JLabel();
		formatterTitle.setFont(font);
		formatterTitle.setText("Formatter: ");
		add(formatterTitle);
		
		formatterChoice = new JComboBox<>(AVAILABLE_FORMATTER_NAMES);
		setSelectedFormatterName();
		formatterChoice.addItemListener(new FormatterListener());
		add(formatterChoice);
	}

	private class  HandlerLevelListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if ((e.getStateChange() == ItemEvent.SELECTED) && (handlerToConfigure != null)) {				
				handlerToConfigure.setLevel((Level)(handlerLevelChoice.getSelectedItem()));			
			}			
		}		
	}
	
	private void setSelectedFormatterName() {
		
		Formatter formatter = handlerToConfigure.getFormatter();
		if (formatter != null) {
			String formatterName = formatter.getClass().getName();
			if (! Arrays.stream(AVAILABLE_FORMATTER_NAMES).anyMatch(formatterName::equals)) {
				// Formatter ins not in the available ones
				formatterChoice.addItem(formatterName);
			}
			formatterChoice.setSelectedItem(formatterName);
		}
	}
	
	private class FormatterListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if ((e.getStateChange() == ItemEvent.SELECTED) && (handlerToConfigure != null)) {
				String selectedFormatterName = (String)formatterChoice.getSelectedItem();
				if (SimpleFormatter.class.getName().equals(selectedFormatterName)) {
					handlerToConfigure.setFormatter(new SimpleFormatter());
				} else if (PlainLogFormatter.class.getName().equals(selectedFormatterName)) {
					handlerToConfigure.setFormatter(new PlainLogFormatter());
				} else if (JsonLogFormatter.class.getName().equals(selectedFormatterName)) {
					handlerToConfigure.setFormatter(new JsonLogFormatter());
				} else if (XMLFormatter.class.getName().equals(selectedFormatterName)) {
					handlerToConfigure.setFormatter(new XMLFormatter());
				}			
			}			
		}		
	}
	
}