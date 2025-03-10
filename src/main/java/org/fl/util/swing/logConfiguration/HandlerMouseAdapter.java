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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class HandlerMouseAdapter extends MouseAdapter {

	private static final Logger internalLogger = Logger.getLogger(HandlerMouseAdapter.class.getName());
	
	private final JPopupMenu localJPopupMenu;
	private final HandlerJTable handlerJTable;
	private final HandlerTableModel handlerTableModel;
	private final JMenuItem editMenuItem;
	private final JMenuItem addConsoleHandlerMenuItem;
	private final JMenuItem addFileHandlerMenuItem;
	private Logger loggerToConfigure;

	public HandlerMouseAdapter(HandlerJTable handlerJTable) {
		super();

		localJPopupMenu = new JPopupMenu();
		this.handlerJTable = handlerJTable;
		this.handlerTableModel = (HandlerTableModel)handlerJTable.getModel();
		loggerToConfigure = null;

		editMenuItem = addMenuItem("Edit handler", new EditHandlerListener());
		addConsoleHandlerMenuItem = addMenuItem("Add ConsoleHandler", new CreateConsoleHandlerListener());
		addFileHandlerMenuItem = addMenuItem("Add FileHandler", new CreateFileHandlerListener());
	}
	
	@Override
	public void mousePressed(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			enableMenuItems();
			localJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			enableMenuItems();
			localJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}
	
	public void setLoggerToConfigure(Logger logger) {
		loggerToConfigure = logger;
	}
	
	private JMenuItem addMenuItem(String title, ActionListener act) {
		JMenuItem localJMenuItem = new JMenuItem(title);
		localJMenuItem.addActionListener(act);
		localJPopupMenu.add(localJMenuItem);
		return localJMenuItem;
	}
	
	private void enableMenuItems() {	
		editMenuItem.setEnabled((handlerJTable.getSelectedHandler() != null));
		addConsoleHandlerMenuItem.setEnabled(loggerToConfigure != null);
		addFileHandlerMenuItem.setEnabled(loggerToConfigure != null);
	}
	
	private class EditHandlerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			Handler handler = handlerJTable.getSelectedHandler();
			if (handler != null) {
				JOptionPane.showMessageDialog(null, 
						new ConfigureHandlerPane(handler),"Edit Handler", JOptionPane.INFORMATION_MESSAGE);
				handlerTableModel.fireTableDataChanged();
			}
		}
		
	}
	
	private void setCommonHandlerParameter(Handler handler, CreateHandlerPane createPane) {
		
		Level level = createPane.getSelectedLevel();
		if (level != null) {
			handler.setLevel(level);
		}
		String formatterName = createPane.getSelectedFormatterName();
		if (formatterName != null) {
			Formatter formatter = FormatterComboBox.getNewChoosenFormatter(formatterName);
			if (formatter != null) {
				handler.setFormatter(formatter);
			}
		}
		String encoding = createPane.getSelectedEncoding();
		if (encoding != null) {
			try {
				handler.setEncoding(encoding);
			} catch (Exception e) {
				internalLogger.log(Level.SEVERE, "Exception when setting handler encoding to :" + encoding, e);
			}
		}
	}
	
	private static final int CREATE_HANDLER_OPTION = 0;
	private static final int CANCEL_OPTION = 1;
	private static final Object[] options = {"Create Handler", "Cancel"};
	
	private class CreateConsoleHandlerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			CreateHandlerPane createPane = new CreateHandlerPane();
			int choosen_option = JOptionPane.showOptionDialog(null, 
					createPane, 
					"Create ConsoleHandler", 
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[CANCEL_OPTION]);
			
			if (choosen_option == CREATE_HANDLER_OPTION) {
				ConsoleHandler consoleHandler = new ConsoleHandler();
				setCommonHandlerParameter(consoleHandler, createPane);
				loggerToConfigure.addHandler(consoleHandler);
				handlerTableModel.refreshHandlerList(loggerToConfigure);
				handlerTableModel.fireTableDataChanged();
			}
			
		}	
	}
	
	private class CreateFileHandlerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			CreateFileHandlerPane createPane = new CreateFileHandlerPane();
			int choosen_option = JOptionPane.showOptionDialog(null, 
					createPane, 
					"Create FileHandler", 
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[CANCEL_OPTION]);
			if (choosen_option == CREATE_HANDLER_OPTION) {
				try {
					FileHandler fileHandler = new FileHandler(
							createPane.getSelectedFilePattern(), 
							createPane.getSelectedFileSize(),
							createPane.getSelectedNamberOfFiles(),
							true);
					setCommonHandlerParameter(fileHandler, createPane);
					loggerToConfigure.addHandler(fileHandler);
					handlerTableModel.refreshHandlerList(loggerToConfigure);
					handlerTableModel.fireTableDataChanged();
				} catch (IOException e1) {
					internalLogger.log(Level.SEVERE, "Exception when creating a file handler", e);
				}
				
			}
		}
	}
}
