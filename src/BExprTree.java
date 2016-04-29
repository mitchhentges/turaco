package bexpred;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

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

public class BExprTree {
  private String expression; // The expression representing the expression tree
  public String orig_expression; // Contains the original expression as entered by the user
  private int var_count; // The amount of unique variables
  private ArrayList vars; // The list of variables, strings.
  private boolean case_sensitive = false; // Is the expression case sensitive?
  private BExprNode root; // The root node.
  private TruthTable truth_table;

  public BExprTree() {
    //This shouldn't be called, but we'll leave it in case it becomes handy
    this.var_count = 0;
  }

  public BExprTree(String expression) throws BExprPreParseException {
    this.setExpression(expression);
  }

  public BExprTree(String expression, boolean case_sensitive) throws BExprPreParseException {
    this.case_sensitive = case_sensitive;
    this.setExpression(expression);
  }

  public TruthTable getTruthTable() {
    return this.truth_table;
  }

  public void setExpression(String expression) throws BExprPreParseException {
    //DEBUG System.out.println(expression);
    BExprPreParser preParser;
    this.orig_expression = expression;
    this.expression = expression.trim();

    try {
      //DEBUG System.out.println(this.expression);
      preParser = new BExprPreParser(this.expression);
      this.expression = preParser.getExpression();
      this.var_count = this.updateVars();
      this.root = new BExprNode(this.expression);
      this.root.setInverted(preParser.isInverted() || this.root.isInverted());
      this.truth_table = new TruthTable(this);
    } catch (BExprPreParseException e) {
      System.out.println(e);
      this.vars = new ArrayList();
      this.var_count = 0;
      throw e;
    }
  }

  public ArrayList getVars() {
    // Basic accessor method
    return this.vars;
  }

  public int getVarCount() {
    return this.var_count;
  }

  private int updateVars() {
    // This method simply sets the vars List and returns the amount of unique
    // variables taking into account case if desired.
    //
    // These are almost all state variables
    char aChar;
    String aVar = "";
    ArrayList boolVars = new ArrayList();
    boolean inVar = false;
    boolean isVar = true;
    boolean varExists = false;
    if (!this.case_sensitive) {
      this.expression = this.expression.toUpperCase();
    }

    this.expression += " "; // Pad the expression to catch the last "value"
    for (int i = 0; i < this.expression.length(); i++) {
      aChar = this.expression.charAt(i);
      isVar = BExprParser.isVarChar(aChar);
      if (!inVar && isVar) {
        inVar = true;
        aVar += aChar;
      } else if (inVar) {
        if (!isVar) {
          inVar = false;
          for (int s = 0; s < boolVars.size(); s++) {
            if (aVar.compareTo(boolVars.get(s)) == 0)
              varExists = true;
          }

          if (!varExists)
            boolVars.add(aVar);
          else
            varExists = false;

          aVar = "";
        } else {
          aVar += aChar;
        }
      }
    }

    Object[] a;
    a = boolVars.toArray();
    Arrays.sort(a);
    this.vars = new ArrayList(Arrays.asList(a));
    return this.vars.size();
  }

  public boolean evaluate(Hashtable values) {
    // This is the evaluate method that should be called (should work with calling it on root directly, but that may change)
    return this.root.evaluate(values);
  }

  public boolean evaluate(boolean values[]) {
    Hashtable h_values = new Hashtable(this.var_count);

    for (int i = 0; i < this.var_count; i++) {
      h_values.put((String) this.vars.get(i), values[i] ? "1" : "0");
    }
    return this.evaluate(h_values);
  }

  public boolean compareTo(BExprTree aTree) {
    // Returns true if the aTree expression is equivalent to this tree
    // It just uses truth tables to determine this
    if (this.getVarCount() == aTree.getVarCount())
      return this.getVars().equals(aTree.getVars()) && this.getTruthTable().equals(aTree.getTruthTable());

    this.getTruthTable().reduceVars();
    aTree.getTruthTable().reduceVars();

    return this.getTruthTable().equals(aTree.getTruthTable());
  }

  /**
   * Invert the current expression by converting ORs to ANDs, and ANDs to ORs,
   * will fail if the tree contains operators other than ORs and ANDs. It also
   * inverts and input variables.
   * @throws Exception
   */
  public void invert() throws Exception {
    this.root.invert();
  }

  public String toString() {
    return this.root.toString();
  }
}