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

import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class HandlerJTable extends JTable {

	private static final long serialVersionUID = 1L;

	private final HandlerMouseAdapter handlerMouseAdapter;
	
	public HandlerJTable(HandlerTableModel handlerTableModel) {
		super(handlerTableModel);
		
		setFillsViewportHeight(true);
		setAutoCreateRowSorter(true);
		
		getColumnModel().getColumn(HandlerTableModel.NAME_COL_IDX).setPreferredWidth(400);
		getColumnModel().getColumn(HandlerTableModel.LEVEL_COL_IDX).setPreferredWidth(100);
		getColumnModel().getColumn(HandlerTableModel.FORMATTER_COL_IDX).setPreferredWidth(400);
		getColumnModel().getColumn(HandlerTableModel.ENCODING_COL_IDX).setPreferredWidth(100);
		getColumnModel().getColumn(HandlerTableModel.FILTER_COL_IDX).setPreferredWidth(400);
		
		// Allow single row selection only
		ListSelectionModel listSelectionModel = new DefaultListSelectionModel();
		listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectionModel(listSelectionModel);
			
		this.handlerMouseAdapter = new HandlerMouseAdapter(this);
		addMouseListener(handlerMouseAdapter);
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	public Handler getSelectedHandler() {
		
		int[] rowIdxs = getSelectedRows();
		if (rowIdxs.length == 0) {
			return null;
		} else {
			return ((HandlerTableModel)getModel()).getHandlerAt(convertRowIndexToModel(rowIdxs[0]));
		}
	}
	
	public void setLoggerToBeConfigured(Logger logger) {
		handlerMouseAdapter.setLoggerToConfigure(logger);
	}
}
