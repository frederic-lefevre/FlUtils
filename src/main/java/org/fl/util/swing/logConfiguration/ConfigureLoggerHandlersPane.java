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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ConfigureLoggerHandlersPane extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private Logger loggerToConfigure;
	private final List<ConfigureHandlerPane> configureHandlerPanes;
	private final JPanel contentPane;
	
	public ConfigureLoggerHandlersPane() {
		
		super();
		loggerToConfigure = null;
		configureHandlerPanes = new ArrayList<>();
		
		setPreferredSize(new Dimension(1800,700));
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setViewportView(contentPane);
	}
	
	public void setLoggerToBeConfigured(Logger logger) {

		if (logger != null) {
			
			// Remove previous handler panes
			for (ConfigureHandlerPane configureHandlerPane : configureHandlerPanes) {
				contentPane.remove(configureHandlerPane);
			}
			
			loggerToConfigure = logger;
			
			// Add handler to configure
			Handler[] handlers = loggerToConfigure.getHandlers();
			if (handlers != null) {
				for (Handler handler : handlers) {
					ConfigureHandlerPane handlerPane = new ConfigureHandlerPane(handler);
					configureHandlerPanes.add(handlerPane);
					contentPane.add(handlerPane);
				}
			}
		}
	}
}
