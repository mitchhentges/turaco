package bexpred;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/*
 * BExpred - Boolean Expression Reducer
 * Goal: To reduce specified boolean expressions
 * Current state: Evaluates a given expression with input values
 * Copyright (c) 2003 Benjamin Biron
 * License: GPL

    This file is part of BExpred.

    BExpred is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BExpred is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BExpred; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */

public class TTFrame extends JFrame {
    JScrollPane jScrollPane1 = new JScrollPane();
    JTTable jTable1;

    public TTFrame() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setSize(new Dimension(120, 120));
        this.setTitle("Truth Table");
        this.jScrollPane1.setMinimumSize(new Dimension(120, 120));
        this.jScrollPane1.getViewport().add(this.jTable1);
        this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    }
}

class JTTable extends JTable {
    JTTable(Boolean[][] data, String[] colNames) {
        super(new JTTModel(data, colNames));
        this.getTableHeader().setReorderingAllowed(false);
    }
}

class JTTModel extends AbstractTableModel {
    private Object[][] data;
    private String[] colNames;

    JTTModel(Boolean[][] data, String[] colNames) {
        if (data.length == 0)
            throw new IllegalArgumentException("Data must not be empty");

        this.data = data;
        this.colNames = colNames;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public int getColumnCount() {
        return colNames == null ? 0 : this.colNames.length;
    }

    public String getColumnName(int col) {
        return this.colNames[col];
    }

    public int getRowCount() {
        return data == null ? 0 : this.data.length;
    }

    public Object getValueAt(int row, int col) {
        if (row < 0 || col < 0 || row >= this.getRowCount() || col >= this.getColumnCount())
            throw new IllegalArgumentException("Invalid row or column index");

        return this.data[row][col];
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setValueAt(boolean val, int row, int col) {
        if (row < 0 || col < 0 || row >= this.getRowCount() || col >= this.getColumnCount())
            throw new IllegalArgumentException("Invalid row or column index");

        this.data[row][col] = (val) ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setValueAt(Object val, int row, int col) {
        boolean boolVal;
        if (val instanceof JCheckBox)
            boolVal = ((JCheckBox) val).isSelected();

        else if (val instanceof Boolean)
            boolVal = ((Boolean) val).booleanValue();

        else if (val instanceof Integer)
            boolVal = ((Integer) val).intValue() != 0;

        else if (val instanceof JRadioButton)
            boolVal = ((JRadioButton) val).isSelected();

        else
            throw new IllegalArgumentException("val must be one of the following types: JCheckBox, Boolean, Integer, JRadioButton");

        this.setValueAt(boolVal, row, col);
    }
}