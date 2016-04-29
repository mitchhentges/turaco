package bexpred;
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

class BExprNode {
  // NOTE: The node_type var is defined as follows:
  //       For a variable node, node_type = 0
  //       For an operator node, node_type = 1
  //       If node_type == -1, the type is "undefined"
  //
  // NOTE: The operator var is defined as follows:
  //       For an OR operator, operator = 0
  //       For an XOR operator, operator = 1
  //       For an AND operator, operator = 2
  //       If operator == -1, the node is either undefined, or of type variable

  public String orig_expression;
  private String expression;
  private BExprNode left, right;
  private int node_type = -1;
  private int operator = -1;
  private String var_name = "";
  private boolean inverted = false;

  public BExprNode() {
    this.expression = "";
    this.orig_expression = "";
    this.left = null;
    this.right = null;
    this.inverted = false;
  }

  public BExprNode(String expression) {
    this.left = null;
    this.right = null;
    this.inverted = false;
    this.expression = expression.trim();
    this.orig_expression = expression.trim();
    //if (BExprParser.opCount(this.expression) > 1)
    this.expression = BExprPreParser.getGroupedExpr(this.expression);
    this.parse();
  }

  public static String stripParentheses(String aStr) {
    // Blindly strips leading and trailing parentheses
    int start = 0, end = aStr.length();

    for (int i = 0; i < aStr.length(); i++) {
      if (aStr.charAt(i) != '(') {
        start = i;
        break;
      }
    }

    for (int i = (aStr.length() - 1); i >= 0; i--) {
      if (aStr.charAt(i) != ')') {
        end = i + 1;
        break;
      }
    }
    return aStr.substring(start, end);
  }

  private void parse() {
    String aVar;
    boolean inverted;
    if (this.expression.length() > 0) {
      // Check if first char is a NOT
      inverted = this.checkInverted();
      aVar = this.parseVar();
      if (this.expression.length() != 0) { // We are not at the bottom of the tree
        this.node_type = 1;
        // Set left node to the one just parsed
        this.left = new BExprNode(aVar);
        this.left.setInverted(inverted);
        // Get the operator
        this.operator = this.parseOp();
        // Parse the RHS of the operator and set as right node
        inverted = this.checkInverted();
        aVar = this.parseVar();
        this.right = new BExprNode(aVar);
        this.right.setInverted(inverted);
      } else {
        this.node_type = 0;
        this.var_name = aVar;
        this.setInverted(this.isInverted() ^ inverted); // The XOR trick is necessary for sub-parses such as the !(!A) case
      }

      // The following deals with cases with extra (useless) parenteses
      if (this.right == null && this.left == null && this.node_type == 0) {
        if (BExprParser.hasOp(this.var_name) || BExprParser.hasNOT(this.var_name)) {
          // Must reparse the expression 'til we get by the useless parentheses
          this.expression = BExprPreParser.getGroupedExpr(this.var_name);
          this.parse();
        } else {
          // Must ensure no extra parentheses surround the variable
          this.var_name = this.stripParentheses(this.var_name);
        }
      }

    } else {
      this.node_type = -1;
      this.var_name = "";
    }
  }

  private String parseVar() {
    int balance = 0, i;
    char aChar;
    String aVar = "";

    if (this.expression.length() > 0) {
      if (this.expression.startsWith("(")) {
        balance = 1;
        for (i = 1; i < this.expression.length(); i++) {
          aChar = this.expression.charAt(i);
          if (aChar == ')')
            balance--;

          if (balance == 0)
            break;

          if (aChar == '(')
            balance++;

          aVar += aChar;
        }

        if ((i+1) >= this.expression.length())
          this.expression = "";
        else
          this.expression = expression.substring(i + 1); // We must specify the character after the last one in aVar, and also must drop the closing bracket

      } else {
        for (i = 0; i < this.expression.length(); i++) {
          aChar = this.expression.charAt(i);
          if (!BExprParser.isVarChar(aChar))
            break;

          aVar += aChar;
        }

        if (i >= this.expression.length())
          this.expression = "";
        else
          this.expression = expression.substring(i);
      }
    }

    return aVar;
  }

  private int parseOp() {
    char aChar;
    int toRet;
    if (this.expression.length() > 0) {
      aChar = this.expression.charAt(0);
      toRet = BExprParser.getOp(aChar);
      if (toRet == -1)
        return -1;
    } else {
      return -1;
    }

    this.expression = this.expression.substring(1);
    return toRet;
  }

  public boolean evaluate(Hashtable values) {
    // Note the use of an XOR as an inverter
    boolean l, r;
    if (this.node_type == -1)
      return false;

    if (this.node_type == 0)
      return (values.get(this.var_name)).equals("1") ^ this.inverted;

    if (this.node_type == 1) {
      l = this.left.evaluate(values);
      r = this.right.evaluate(values);
      if (this.operator == 0)
        return (l || r) ^ this.inverted;

      if (this.operator == 1)
        return (l ^ r) ^ this.inverted;

      if (this.operator == 2)
        return (l && r) ^ this.inverted;

      return false;
    }

    return false;
  }

  public boolean isInverted() {
    return this.inverted;
  }

  public void setInverted(boolean inverted) {
    this.inverted = inverted;
  }

  private boolean checkInverted() {
    boolean toRet = false;
    while (this.expression.length() > 0 && BExprParser.isNOT(this.expression.charAt(0))) {
      this.expression = this.expression.substring(1);
      toRet ^= true;
    }
    return toRet;
  }

  public void invert() throws Exception {
    if (this.node_type == 0)
      this.inverted = (this.inverted ? false : true);
    else if (this.node_type == 1) {
      if (this.operator == 0)
        this.operator = 2;
      else if (this.operator == 2)
        this.operator = 0;
      else
        throw new Exception("Can only invert trees with ANDs and ORs exclusively");

      this.left.invert();
      this.right.invert();
    }
  }

  public String toString() {
    String l, r;

    if (this.node_type == 0)
      return (this.inverted ? "!" + this.var_name : this.var_name);
    else if (this.node_type == 1) {
      if (this.left.node_type == 0 || this.left.operator == this.operator)
        l = this.left.toString();
      else
        l = "(" + this.left.toString() + ")";

      if (this.right.node_type == 0 || this.right.operator == this.operator)
        r = this.right.toString();
      else
        r = "(" + this.right.toString() + ")";

      if (this.operator == 0)
        return l + " " + BExprParser.getStringOp(this.operator) + " " + r; // Pad the expression for readability
      else
        return l + BExprParser.getStringOp(this.operator) + r;
    }
    return "";
    /*
    if (this.node_type == 0)
      return this.var_name;
    else if (this.node_type == 1) {
      if (this.operator == 0) {
        if (this.left.node_type == 0)
          l = this.left.toString();
        else if (this.left.operator == 2)
          l = "(" + this.left.toString() + ")";
        else
          l = this.left.toString();
      } else if (this.operator == 2) {
        if (this.left.node_type == 0)
          l = "(" + this.left.toString();
        else if (this.left.operator == 2)
          l = this.left.toString();
        else
          l = this.left.toString();
      }
    }
    */
  }
}