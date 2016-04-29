package bexpred;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Arrays;

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

public class CoverTable {
  boolean[][] table;
  ArrayList cols = new ArrayList();
  ArrayList usedCols = new ArrayList();
  Hashtable colIndices = new Hashtable(); // Key: Position in truth table, Value: Index in cols
  QMGroup minterms;

  CoverTable(QMGroup minterms) {
    this.minterms = minterms;

    for (int i = 0; i < minterms.size(); i++) {
      this.copyCoveredRows(((QMItem) minterms.get(i)).coveredRows, this.cols);
    }

    this.table = new boolean[minterms.size()][cols.size()];
    for (int i = 0; i < this.table.length; i++) {
      Arrays.fill(this.table[i], false);
    }

    for (int i = 0; i < minterms.size(); i++) {
      for (int s = 0; s < minterms.get(i).coveredRows.size(); s++) {
        table[i][((Integer) colIndices.get(minterms.get(i).coveredRows.get(s))).intValue()] = true;
      }
    }
  }

  private void copyCoveredRows(ArrayList from, ArrayList to) {
    for (int i = 0; i < from.size(); i++) {
      if (!to.contains(from.get(i))) {
        to.add(from.get(i));
        colIndices.put(from.get(i), new Integer(colIndices.size()));
      }
    }
  }

  public QMGroup reduce() {
    QMGroup required = new QMGroup(); // Contains column index of cols with a single checked item
    int cnt, maxCnt, row = -1;

    usedCols.clear();

    for (int s = 0; s < this.table[0].length; s++) {
      cnt = 0;
      for (int i = 0; i < this.table.length; i++) {
        if (this.table[i][s]) {
          row = i;
          cnt++;
        }
      }

      if (cnt == 1) {
        this.cover(row);
        required.add(this.minterms.get(row));
      }
    }

    while (usedCols.size() != cols.size()) {
      cnt = 0;
      maxCnt = 0;
      for (int i = 0; i < this.table.length; i++) {
        cnt = this.uncoveredCount(i);
        if (cnt > maxCnt) {
          maxCnt = cnt;
          row = i;
        }
      }

      if (maxCnt > 0) {
        this.cover(row);
        required.add(this.minterms.get(row));
      }
    }

    Boolean[][] dTable = new Boolean[this.table.length][this.table[0].length];

    for (int i = 0; i < dTable.length; i++) {
      for (int s = 0; s < dTable[i].length; s++) {
        dTable[i][s] = new Boolean(this.table[i][s]);
      }
    }

    return required;
  }

  private int uncoveredCount(int row) {
    int cnt = 0;
    for (int i = 0; i < this.table[row].length; i++) {
      if (this.table[row][i] && !this.usedCols.contains(new Integer(i))) {
        cnt++;
      }
    }
    return cnt;
  }

  private void cover(int row) {
    for (int i = 0; i < this.table[row].length; i++) {
      if (this.table[row][i] && !this.usedCols.contains(new Integer(i)))
        this.usedCols.add(new Integer(i));
    }
  }
}