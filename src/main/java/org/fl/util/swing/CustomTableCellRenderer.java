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

package org.fl.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public abstract class CustomTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
    private Color unselectedForeground;
    private Color unselectedBackground;
    
	public CustomTableCellRenderer(Font font, int horizontalAlignment) {
		
		super();
		setFont(font);
		setHorizontalAlignment(horizontalAlignment);
	}
	
	public abstract void valueProcessor(Object value);
    
    @Override
    public void setForeground(Color c) {
        super.setForeground(c);
        unselectedForeground = c;
    }
    
    @Override
    public void setBackground(Color c) {
        super.setBackground(c);
        unselectedBackground = c;
    }
    
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		// (almost) same code as in DefaultTableCellRenderer except for setFont (omitted) and a simplified management of selected cell color
        if (table == null) {
            return this;
        }

        JTable.DropLocation dropLocation = table.getDropLocation();

        isSelected = isSelected || (dropLocation != null
                && !dropLocation.isInsertRow()
                && !dropLocation.isInsertColumn()
                && dropLocation.getRow() == row
                && dropLocation.getColumn() == column);

        if (isSelected) {
            super.setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            super.setForeground(unselectedForeground != null
                                    ? unselectedForeground
                                    : table.getForeground());
            super.setBackground(unselectedBackground != null
                    ? unselectedBackground
                    : table.getBackground());
        }
        
		if (hasFocus) {
		    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
		    if (!isSelected && table.isCellEditable(row, column)) {
	                Color col;
	                col = UIManager.getColor("Table.focusCellForeground");
	                if (col != null) {
	                    super.setForeground(col);
	                }
	                col = UIManager.getColor("Table.focusCellBackground");
	                if (col != null) {
	                    super.setBackground(col);
	                }
		    }
		} else {
		    setBorder(noFocusBorder);
		}
		
		// Responsible of setting text of JLabel, optionally setting background, foreground but that will override the selection colors
		valueProcessor(value);
		
		return this;
	}
}

