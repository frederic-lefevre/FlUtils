/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.fl.util.swing.GuiTexts;

public class HandlerTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;

	public final static int NAME_COL_IDX = 0;
	public final static int LEVEL_COL_IDX = 1;
	public final static int FORMATTER_COL_IDX = 2;
	public final static int ENCODING_COL_IDX = 3;
	public final static int FILTER_COL_IDX = 4;
	
	private final static String[] entetes = {"Handler Class", "Level", "Formatter", "Encoding", "Filter"};
	
	private final List<Handler> handlerList;
	
	public HandlerTableModel(List<Handler> handlerList) {
		super();
		this.handlerList = handlerList;
	}
	
	public void refreshHandlerList(Logger logger) {
		
		handlerList.clear();
		if (logger != null) {
			for (Handler handler : logger.getHandlers()) {
				handlerList.add(handler);
			}
		}
	}
	
	@Override
	public int getRowCount() {
		return handlerList.size();
	}

	@Override
	public int getColumnCount() {
		return entetes.length;
	}

	@Override
	public String getColumnName(int col) {
	    return entetes[col];
	}
	
    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }
    
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if (handlerList.size() < rowIndex +1) {
			return null;
		} else {
			return switch (columnIndex) {
				case NAME_COL_IDX -> handlerList.get(rowIndex).getClass().getName();
				case LEVEL_COL_IDX -> handlerList.get(rowIndex).getLevel();
				case FORMATTER_COL_IDX -> Optional.ofNullable(handlerList.get(rowIndex).getFormatter())
					.map(f -> f.getClass().getName())
					.orElseGet(() -> GuiTexts.getText("appTabbedPane.logConfiguration.noFormatter"));
				case ENCODING_COL_IDX -> handlerList.get(rowIndex).getEncoding();
				case FILTER_COL_IDX -> Optional.ofNullable(handlerList.get(rowIndex).getFilter())
					.map(f -> f.getClass().getName())
					.orElseGet(() -> GuiTexts.getText("appTabbedPane.logConfiguration.noFilter"));
				default -> null;
			};
		}
	}

	public Handler getHandlerAt(int rowIndex) {
		return handlerList.get(rowIndex);
	}

}
