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
	
	private static final String[] AvailableLoggingFormatterName = {
			SimpleFormatter.class.getName(),
			XMLFormatter.class.getName(),
			JsonLogFormatter.class.getName(), 
			PlainLogFormatter.class.getName()};
	
	private final JComboBox<Level> handlerLevelChoice;
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
		
		JLabel formatterLabel = new JLabel();
		formatterLabel.setFont(font);
		formatterLabel.setText(handler.getFormatter().getClass().getName());
		add(formatterLabel);
	}

	private class  HandlerLevelListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if ((e.getStateChange() == ItemEvent.SELECTED) && (handlerToConfigure != null)) {				
				handlerToConfigure.setLevel((Level)(handlerLevelChoice.getSelectedItem()));			
			}			
		}		
	}
}