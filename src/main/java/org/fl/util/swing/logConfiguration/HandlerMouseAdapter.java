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
import java.util.logging.Handler;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class HandlerMouseAdapter extends MouseAdapter {

	private final JPopupMenu localJPopupMenu;

	public HandlerMouseAdapter(HandlerJTable handlerJTable) {
		super();

		localJPopupMenu = new JPopupMenu();

		JMenuItem localJMenuItem = new JMenuItem("Edit handler");
		localJMenuItem.addActionListener(new EditHandlerListener(handlerJTable));
		localJPopupMenu.add(localJMenuItem);
	}
	
	@Override
	public void mousePressed(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			localJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			localJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}
	
	private class EditHandlerListener implements ActionListener {

		private final HandlerJTable handlerJTable;
		
		public EditHandlerListener(HandlerJTable handlerJTable) {
			this.handlerJTable = handlerJTable;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			Handler handler = handlerJTable.getSelectedHandler();
			if (handler != null) {
				JOptionPane.showMessageDialog(null, 
						new ConfigureHandlerPane(handler),"Edit Handler", JOptionPane.INFORMATION_MESSAGE);
				((HandlerTableModel)handlerJTable.getModel()).fireTableDataChanged();
			}
		}
		
	}
}
