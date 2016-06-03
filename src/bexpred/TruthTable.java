package bexpred;

import java.util.ArrayList;

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

    public TruthTable(BExprTree aTree) {
        int varCount = aTree.getVarCount();
        this.row_count = two_exp(varCount);
        this.col_count = varCount + 1;
        this.initializeTT();

        boolean bArray[] = new boolean[this.col_count];
        int c;
        for (int i = 0; i < this.row_count; i++) {
            c = 0;
            for (int s = 0; s < varCount; s++) {
                bArray[c++] = ((i >> s) & 1) == 1;
            }
            bArray[this.col_count - 1] = aTree.evaluate(bArray);
            this.setRow(i, bArray);
        }
    }

    private boolean[][] getTT() {
        return this.tt;
    }

    private boolean[][] getInvertedTT() {
        boolean[][] inverted = new boolean[this.row_count][this.col_count];

        for (int i = 0; i < inverted.length; i++) {
            System.arraycopy(this.tt[i], 0, inverted[i], 0, inverted[i].length);
            inverted[i][this.col_count - 1] = (!this.tt[i][this.col_count - 1]);
        }

        return inverted;
    }

    private void setRow(int row, boolean aRow[]) {
        System.arraycopy(aRow, 0, this.tt[row], 0, this.col_count);
    }

    private static int two_exp(int exp) {
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
     *
     * @param varNames Names of the variables corresponding to the columns in the truth table
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
            return "AN ERROR OCCURRED";
        }
    }

    public Boolean[][] getWrappedTable() {
        // Returns the truth table as wrapped Boolean or Integer types
        Boolean[][] toRet = new Boolean[this.row_count][this.col_count];
        for (int i = 0; i < this.row_count; i++) {
            for (int s = 0; s < this.col_count; s++) {
                toRet[i][s] = (this.tt[i][s]) ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        return toRet;
    }

    public void reduceVars() {
        //reduces the truth table by getting rid of redundant variables
        //returns an ArrayList of the vars minus the redundant ones
        int patternLength;
        int patternCount = 0;
        String pattern, prevPattern;
        boolean patternsMatch;
        ArrayList toRemove = new ArrayList();

        for (int i = 0; i < this.col_count - 1; i++) {
            pattern = "";
            prevPattern = "";
            patternLength = this.row_count / (int) Math.pow(2.0, (double) (i + 1));
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
            this.removeCol(index);
        }
    }

    private void removeCol(int idx) {
        //removes a column (var) and it's associated inputs
        boolean[][] cleanTT = new boolean[this.row_count / 2][this.col_count - 1];
        int patternLength = this.row_count / (int) Math.pow(2.0, (double) (idx + 1));
        boolean saveRow = false;
        int newIndex = 0;
        for (int i = 0; i < this.row_count; i++) {
            if (i % patternLength == 0)
                saveRow = !saveRow;

            if (saveRow) {
                System.arraycopy(this.tt[i], 0, cleanTT[newIndex], 0, idx);

                System.arraycopy(this.tt[i], idx + 1, cleanTT[newIndex], idx + 1 - 1, this.col_count - (idx + 1));
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

}