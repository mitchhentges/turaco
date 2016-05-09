package bexpred;

import java.util.ArrayList;
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


class QMReducer {
    private final QMGroup[] groups; // The groped rows in the truth table containing same amount of 1's
    private boolean isConst = false; // Will be true for expressions such as A*!A or A+!A
    private boolean constVal; // Will contain the constant value

    QMReducer(int varCount) {
        this.groups = new QMGroup[varCount + 1];
        for (int i = 0; i < this.groups.length; i++) {
            this.groups[i] = new QMGroup();
        }
    }

    public void setBoolTT(boolean[][] aTT) {
        QMItem anItem;
        boolean isConst = true;
        int previousOutput = -1;

        for (int i = 0; i < aTT.length; i++) {
            if (aTT[i][this.groups.length - 1]) {
                if (isConst && previousOutput == 0) // Outputs have differed, can't be constant
                    isConst = false;
                previousOutput = 1;
                anItem = new QMItem(aTT[i]);
                this.groups[anItem.getOneCount()].add(anItem);
            } else {
                if (isConst && previousOutput == 1) // Outputs have differed, can't be constant
                    isConst = false;
                previousOutput = 0;
            }
        }

        if (isConst) {
            this.isConst = true;
            this.constVal = (previousOutput == 1);
        }
    }

    public String reduce(ArrayList varNames) {
        if (this.isConst)
            return (this.constVal ? "TRUE" : "FALSE");

        QMGroup reducedGroup = new QMGroup(),
                minimalGroup = new QMGroup();

        do {
            reducedGroup.clear();
            for (int i = 0; i < this.groups.length - 1; i++) {
                this.groups[i].reduceWith(this.groups[i + 1], reducedGroup);
            }

            for (int i = 0; i < this.groups.length; i++) {
                for (int s = 0; s < this.groups[i].size(); s++) {
                    if (!this.groups[i].get(s).isUsed())
                        minimalGroup.add(this.groups[i].get(s));
                }
                this.groups[i].clear();
            }

            for (int i = 0; i < reducedGroup.size(); i++) {
                this.groups[reducedGroup.get(i).getOneCount()].add(reducedGroup.get(i));
            }
        } while (reducedGroup.size() != 0);

        QMGroup reducedExpr = new CoverTable(minimalGroup).reduce();

        int[] aRow;
        String aLine = "", aTerm;
        for (int i = 0; i < reducedExpr.size(); i++) {
            aRow = reducedExpr.get(i).getRow();
            aTerm = "";
            for (int s = 0; s < aRow.length; s++) {
                if (aRow[s] != -1) {
                    if (aTerm.length() != 0)
                        aTerm += "*";

                    aTerm += aRow[s] == 1 ? (String) varNames.get(s) : "!" + (String) varNames.get(s);
                }
            }

            if (aLine.length() != 0)
                aLine += " + ";
            aLine += aTerm;
        }

        return aLine;
    }
}

class QMGroup {
    private ArrayList aList = new ArrayList();

    QMGroup() {

    }

    public void add(QMItem anItem) {
        if (!aList.contains(anItem))
            aList.add(anItem);
    }

    public QMItem get(int index) {
        return (QMItem) aList.get(index);
    }

    public int size() {
        return aList.size();
    }

    public void clear() {
        this.aList.clear();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QMGroup qmGroup = (QMGroup) o;

        return aList != null ? aList.equals(qmGroup.aList) : qmGroup.aList == null;

    }

    public int hashCode() {
        return aList != null ? aList.hashCode() : 0;
    }

    public Object clone() throws CloneNotSupportedException {
        QMGroup qmGroup = (QMGroup) super.clone();
        qmGroup.aList = (ArrayList) this.aList.clone();
        return qmGroup;
    }

    public void reduceWith(QMGroup aGroup, QMGroup reducedGroup) {
        for (int i = 0; i < this.size(); i++) {
            for (int s = 0; s < aGroup.size(); s++) {
                this.get(i).reduceWith(aGroup.get(s), reducedGroup);
            }
        }
    }
}

class QMItem {
    private final int[] row; // An entry in row is 0, 1 or -1 if it's a don't care
    private int oneCount = 0;
    private boolean used = false;
    public ArrayList coveredRows = new ArrayList();

    QMItem(boolean[] aRow) {
        int rowIndex = 0;

        this.row = new int[aRow.length - 1];

        for (int i = 0; i < this.row.length; i++) {
            if (aRow[i]) {
                this.oneCount++;
                this.row[i] = 1;
                rowIndex += (int) Math.pow(2.0, this.row.length - 1 - i);
            } else {
                this.row[i] = 0;
            }
        }

        this.coveredRows.add(new Integer(rowIndex));
    }

    private QMItem(QMItem anItem) {
        // Must not restore the "used" var.
        this.coveredRows = (ArrayList) anItem.coveredRows.clone();
        this.row = new int[anItem.row.length];
        for (int i = 0; i < this.row.length; i++) {
            this.row[i] = anItem.row[i];
            if (this.row[i] == 1)
                this.oneCount++;
        }
    }

    public int getOneCount() {
        return this.oneCount;
    }

    public boolean isUsed() {
        return this.used;
    }

    private void setUsed() {
        this.used = true;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QMItem qmItem = (QMItem) o;

        return Arrays.equals(row, qmItem.row);

    }

    public void reduceWith(QMItem anItem, QMGroup reducedGroup) {
        int diffColumn = -1;
        for (int i = 0; i < this.row.length; i++) {
            if (this.row[i] < 0 && anItem.row[i] >= 0)
                return;

            if (this.row[i] != anItem.row[i]) {
                if (diffColumn != -1) // There's more than one column different
                    return;
                diffColumn = i;
            }
        }

        if (diffColumn == -1)
            return;

        QMItem changedItem = new QMItem(anItem);
        changedItem.row[diffColumn] = -1;
        reducedGroup.add(changedItem);

        this.copyCoveredRows(changedItem); // Copy the covered rows over to the new QMItem
        // anItem.copyCoveredRows(changedItem); NOT Required since we instantiate QMItem from anItem

        anItem.setUsed();
        this.setUsed();
    }

    private void copyCoveredRows(QMItem to) {
        for (int i = 0; i < this.coveredRows.size(); i++) {
            if (!to.coveredRows.contains(this.coveredRows.get(i)))
                to.coveredRows.add(this.coveredRows.get(i));
        }
    }

    public int[] getRow() {
        return this.row;
    }
}