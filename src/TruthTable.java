package bexpred;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;

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

public class TruthTable {
  private boolean tt[][];
  private int row_count;
  private int col_count;
  private ArrayList vars;

  public TruthTable() {
    this(0, 0);
  }

  public TruthTable(int rows, int cols) {
    this.row_count = rows;
    this.col_count = cols;
    this.vars = new ArrayList();
    this.initializeTT();
  }

  public TruthTable(BExprTree aTree) {
    int varCount = aTree.getVarCount();
    this.row_count = two_exp(varCount);
    this.col_count = varCount + 1;
    this.vars = (ArrayList) aTree.getVars().clone();
    this.initializeTT();

    boolean bArray[] = new boolean[this.col_count];
    int c;
    for (int i = 0; i < this.row_count; i++) {
      c = 0;
      for (int s = varCount - 1; s >= 0; s--) {
        bArray[c++] = ((i >> s) & 1) == 1 ? true : false;
      }
      bArray[this.col_count - 1] = aTree.evaluate(bArray);
      this.setRow(i, bArray);
    }
  }

  public boolean[][] getTT() {
    return this.tt;
  }

  public boolean[][] getInvertedTT() {
    boolean[][] inverted = new boolean[this.row_count][this.col_count];

    for (int i = 0; i < inverted.length; i++) {
      for (int s = 0; s < inverted[i].length; s++) {
        inverted[i][s] = this.tt[i][s];
      }
      inverted[i][this.col_count - 1] = (this.tt[i][this.col_count - 1] ? false : true);
    }

    return inverted;
  }

  public ArrayList getVars() {
    return this.vars;
  }

  public boolean[] getRow(int row) {
    boolean aRow[] = new boolean[this.col_count];
    for (int i = 0; i < this.col_count; i++) {
      aRow[i] = this.tt[row][i];
    }
    return aRow;
  }

  public void setRow(int row, boolean aRow[]) {
    for (int i = 0; i < this.col_count; i++) {
      this.tt[row][i] = aRow[i];
    }
  }

  public static int two_exp(int exp) {
    int n = 1;
    for (int i = 1; i <= exp; i++) {
      n <<= 1;
    }
    return n;
  }

  private void initializeTT() {
    this.tt = new boolean[this.row_count][this.col_count];
    for (int i = 0; i < this.row_count; i++) {
      for (int s = 0; s < this.col_count; s++) {
        this.tt[i][s] = false;
      }
    }
  }

  /**
   * Get Sum Of Products
   * @param varNames Names of the variables corresponing to the columns in the truth table
   * @return The sum of products reduced form of the expression
   */
  public String getSOP(ArrayList varNames) {
    QMReducer aReducer = new QMReducer(varNames.size());
    aReducer.setBoolTT(this.getTT());
    return aReducer.reduce(varNames);
  }

  public String getPOS(ArrayList varNames) {
    BExprTree pos;
    QMReducer aReducer = new QMReducer(varNames.size());
    if (varNames.size() > 1)
      aReducer.setBoolTT(this.getInvertedTT());
    else
      aReducer.setBoolTT(this.getTT());

    try {
      pos = new BExprTree(aReducer.reduce(varNames));
      //pos.invert();
      if (pos.getVarCount() > 1)
        pos.invert();
      return pos.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "AN ERROR OCCURED";
    }
  }

  private boolean[][] initVarTable() {
    boolean[][] table = new boolean[this.row_count][this.col_count - 1];
    boolean initVal;

    for (int i = 0; i < this.row_count; i++) {
      initVal = this.tt[i][this.col_count - 1];
      for (int s = 0; s < this.col_count - 1; s++) {
        table[i][s] = initVal;
      }
    }

    return table;
  }

  private boolean dropVars(int row, boolean[][] varTable, ArrayList usedLines) {
    int singleChangeIndex;
    int pow;
    int checkRow;
    ArrayList additional = new ArrayList(), temp = new ArrayList();

    additional.add(new Integer(0));
    for (int s = 0; s < this.col_count - 1; s++) {
      pow = this.col_count - 2 - s;
      singleChangeIndex = (int) Math.pow(2.0, pow);

      if (!varTable[row][s]) { //!!!  && !this.tt[row][s]
        if (this.tt[row][s])
          singleChangeIndex = -singleChangeIndex;

        for (int i = 0; i < additional.size(); i++) {
          temp.add(new Integer(((Integer) additional.get(i)).intValue() + singleChangeIndex));
        }

        additional.addAll(temp);
      }
    }

    for (int s = 0; s < this.col_count - 1; s++) {
      pow = this.col_count - 2 - s;
      singleChangeIndex = (int) Math.pow(2.0, pow);
      if (varTable[row][s]) { //!!!  && !this.tt[row][s]
        if (this.tt[row][s])
          singleChangeIndex = -singleChangeIndex;

        for (int i = 0; i < additional.size(); i++) {
          checkRow = row + singleChangeIndex + ((Integer)additional.get(i)).intValue();
          if (this.tt[checkRow][this.col_count - 1] && Arrays.equals(varTable[row], varTable[checkRow])) { // NOT SURE ABOUT THE 2ND CONDITION!!
            varTable[row][s] = false;
            if (!usedLines.contains(new Integer(checkRow)))
              usedLines.add(new Integer(checkRow));
            return true;
          }
        }
      }
    }
    return false;
  }

  private String strExpression(boolean colMask[], boolean colVals[], ArrayList colNames) {
    String out = "";
    for (int i = 0; i < colMask.length; i++) {
      if (colMask[i]) {
        if (!colVals[i])
          out += '!';
        out += (String) colNames.get(i) + "*";
      }
    }
    while (out.endsWith("*"))
      out = out.substring(0, out.length() - 1);

    return out;
  }

  public Boolean[][] getWrappedTable() {
    // Returns the truth table as wrapped Boolean or Integer types
    Boolean[][] toRet = new Boolean[this.row_count][this.col_count];
    for (int i = 0; i < this.row_count; i++) {
      for (int s = 0; s < this.col_count; s++) {
        toRet[i][s] = new Boolean(this.tt[i][s]);
      }
    }
    return toRet;
  }

  public ArrayList reduceVars() {
    return this.reduceVars(null);
  }

  public ArrayList reduceVars(ArrayList varNames) {
    //reduces the truth table by getting rid of redundant variables
    //returns an ArrayList of the vars minus the redundant ones
    int patternLength;
    int patternCount = 0;
    String pattern = "", prevPattern = "";
    boolean patternsMatch = true;
    ArrayList toRemove = new ArrayList();

    for (int i = 0; i < this.col_count - 1; i++) {
      pattern = "";
      prevPattern = "";
      patternLength = this.row_count / (int) Math.pow(2.0, (double) (i+1));
      patternsMatch = true;
      for (int s = 0; s < this.row_count; s++) {
        if (patternsMatch) {
          pattern += (this.tt[s][this.col_count - 1] ? "1" : "0");
          if ((s + 1) % patternLength == 0) {
            patternCount++;
            if (patternCount % 2 == 0) {
              if (!prevPattern.equals(pattern))
                patternsMatch = false;
            }
            prevPattern = pattern;
            pattern = "";
          }

        }
      }

      if (patternsMatch) {
        toRemove.add(new Integer(i));
      }
    }

    int index;
    for (int i = 0; i < toRemove.size(); i++) {
      index = ((Integer) toRemove.get(i)).intValue() - i; // Must subtract i because the cols will be shifting
      if (varNames != null)
        varNames.remove(index);
      this.removeCol(index);
    }
    return varNames;
  }

  public void removeCol(int idx) {
    //removes a column (var) and it's associated inputs
    boolean[][] cleanTT = new boolean[this.row_count / 2][this.col_count - 1];
    int patternLength = this.row_count / (int) Math.pow(2.0, (double) (idx+1));
    boolean saveRow = false;
    int newIndex = 0;
    for (int i = 0; i < this.row_count; i++) {
      if (i % patternLength == 0)
        saveRow = saveRow ? false : true;

      if (saveRow) {
        for (int s = 0; s < idx; s++) {
          cleanTT[newIndex][s] = this.tt[i][s];
        }

        for (int s = idx + 1; s < this.col_count; s++) {
          cleanTT[newIndex][s-1] = this.tt[i][s];
        }
        newIndex++;
      }
    }

    this.row_count /= 2;
    this.col_count--;
    this.tt = cleanTT;
  }

  public boolean equals(Object o) {
    if (o == null || !(o instanceof TruthTable))
      return false;

    TruthTable aTT = (TruthTable) o;

    if (aTT.row_count != this.row_count || aTT.col_count != this.col_count)
      return false;

    for (int i = 0; i < this.row_count; i++) {
      if (this.tt[i][this.col_count - 1] != aTT.tt[i][this.col_count - 1])
        return false;
    }
    return true;
  }

  public static int singleVarChange(boolean a[], boolean b[]) {
    // -1 Means more than 1 var changed
    int toRet = -1;

    if (a.length != b.length)
      return -1;

    for (int i = 0; i < a.length; i++) {
      if (a[i] != b[i]) {
        if (toRet != -1)
          return -1;
        toRet = i;
      }
    }
    return toRet;
  }

  public static void initBoolArray(boolean bArray[], boolean initVal) {
    for (int i = 0; i < bArray.length; i++)
      bArray[i] = initVal;

  }
}