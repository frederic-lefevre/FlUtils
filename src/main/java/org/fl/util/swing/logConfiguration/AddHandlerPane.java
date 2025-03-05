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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AddHandlerPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private final Logger logger;
	
	private final JButton addConsoleHandlerButton;
	private final JButton addFileHandlerButton;
	
	public AddHandlerPane(Logger logger) {
		
		super();
		this.logger = logger;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel title = new JLabel("Add a handler to " + logger.getName());
		add(title);
		
		addConsoleHandlerButton = new JButton("Add a ConsoleHandler");
		add(addConsoleHandlerButton);
		
		JPanel addFileHandlerPane = new JPanel();
		addFileHandlerPane.setLayout(new BoxLayout(addFileHandlerPane, BoxLayout.Y_AXIS));
		
		addFileHandlerButton = new JButton("Add a FileHandler");
		
		addFileHandlerPane.add(addFileHandlerButton);
		add(addFileHandlerPane);
	}
	
	private class addConsoleHanlerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (logger != null) {
				ConsoleHandler consoleHandler = new ConsoleHandler();
				logger.addHandler(consoleHandler);
			}
			
		}
		
	}
}
