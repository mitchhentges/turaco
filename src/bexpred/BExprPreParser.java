package bexpred;

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

public class BExprPreParser {
  private String expression;
  private boolean inverted = false;

  public BExprPreParser() throws BExprPreParseException {
    this("");
  }

  public BExprPreParser(String expression) throws BExprPreParseException {
    this.setExpression(expression);
  }

  public boolean isInverted() {
    return this.inverted;
  }

  public void setExpression(String expression) throws BExprPreParseException {
    this.expression = expression;
    this.validate();
    this.group();
  }

  public String getExpression() {
    return this.expression;
  }

  private void validate() throws BExprPreParseException {
    // This checks for matching parentheses, clears spaces and puts in ANDs
    // where no operators precede an opening bracket
    // Returns true if we're good to go, false if some kind of error occured

    int balance = 0, lastIteration; // This will end up as something other than 0 if unbalanced, if it ever dips below 0, we have an error
    char aChar, prevChar = '\0'; // The initial value is useless, I just like initial values =).
    String cleaned = ""; // This initial value is required!!

    // If enclosed is false at the end of the validation process, it'll
    // put the whole expression between brackets. If there aren't any
    // brackets in the expression, indexOf return -1 and enclosed is
    // initialised to false.
    boolean enclosed = this.expression.indexOf("(") == 0;
    boolean inverted = false; // Is the first char a NOT

    if (this.expression.length() > 0) {
      if (BExprParser.isNOT(this.expression.charAt(0))) {
        while (this.expression.length() > 0 && BExprParser.isNOT(this.expression.charAt(0))) {
          inverted ^= true;
          this.expression = this.expression.substring(1);
        }
      } else if (BExprParser.isOp(this.expression.charAt(0)) || BExprParser.isOp(this.expression.charAt(this.expression.length() - 1))) {
        throw new BExprPreParseException("Expression cannot start or end with an operator");
      }
    } else {
      throw new BExprPreParseException("Empty expression");
    }

    lastIteration = this.expression.length() - 1;
    for (int i = 0; i < this.expression.length(); i++) {
      aChar = this.expression.charAt(i);
      if (aChar == '(')
        balance++;
      else if (aChar == ')') {
        balance--;
        if (balance < 0) // Since balance dips below 0, the expression is invalid
          throw new BExprPreParseException("Unbalanced closing bracket at character " + i);
        else if (balance == 0 && enclosed && i != lastIteration) { // When balance reaches 0, it means we're either at the end of the expression, or the expression needs to be enclosed
          enclosed = false;
        }
      }
    }

    if (balance != 0) // Thank you come again.
      throw new BExprPreParseException("The expression is missing a closing bracket somewhere");

    // This loop plugs in AND operators where sequential brackets are found or when
    // an opening bracket immediately follows a variable.
    for (int i = 0; i < this.expression.length(); i++) {
      aChar = this.expression.charAt(i);

      if (aChar == ' ')
        continue;
      else if (aChar == '(' && i != 0 && prevChar != '(' && (!BExprParser.isOp(prevChar) && !BExprParser.isNOT(prevChar)))
        cleaned += '*';
      else if (prevChar == ')' && (aChar != '(' && aChar != ')' && !BExprParser.isOp(aChar)))
        cleaned += '*';
      else if (((BExprParser.isOp(aChar)) || aChar == ')') && prevChar == '(')
        throw new BExprPreParseException("The expression is invalid near character " + i);
      else if (BExprParser.isOp(aChar) && BExprParser.isOp(prevChar))
        throw new BExprPreParseException("Two consecutive operators near character " + i);

      cleaned += aChar;
      prevChar = aChar;
    }

    if (enclosed) {
      this.inverted = inverted;
      if (cleaned.length() > 1)
        cleaned = cleaned.substring(1, cleaned.length() - 1);
    } else if (inverted) {
      cleaned = '!' + cleaned;
    }

    // Finally, we're done validating.
    this.expression = cleaned;
  }

  private void group() {
    this.expression = this.getGroupedExpr(this.expression);
  }

  public static String getGroupedExpr(String expression) {
    String left = "", middle = "", right = "";
    String grouped;
    char aChar, bChar;
    boolean goRight = false;
    int nOp, tOp;
    int l_cnt = 0, r_cnt = 1; // r_cnt must start at one to catch the last var after the last op.
    int upto, balance;

    for (int i = 0; i < expression.length(); i++) {
      aChar = expression.charAt(i);
      if (BExprParser.isOp(aChar)) {
        tOp = BExprParser.getOp(aChar); // tOp stands for This Operator
        if (!goRight) {
          l_cnt++;
          nOp = BExprParser.nextOp(i+1, expression);
          //if (nOp == 2 || nOp == tOp || nOp == -1) {
          if ((nOp == 2 && tOp != 2) || nOp == -1) {
            middle += aChar;
            goRight = true;
          } else {
            left += aChar;
          }
        } else {
          right += aChar;
          r_cnt++;
        }
      } else if (aChar == '(') {
        balance = 0;
        upto = expression.length();
        for (int s = i; s < expression.length(); s++) {
          bChar = expression.charAt(s);
          if (bChar == '(')
            balance++;
          else if (bChar == ')') {
            balance--;

            if (balance == 0) {
              upto = s;
              break;
            }
          }
        }

        grouped = '(' + expression.substring(i+1, upto) + ')';
        i = upto;
        if (goRight)
          right += grouped;
        else
          left += grouped;
      } else {
        if (goRight) {
          right += aChar;
        } else {
          left += aChar;
        }
      }
    }

    if (l_cnt > 1)
      left = '(' + left + ')';

    if (r_cnt > 1)
      right = '(' + right + ')';

    return left + middle + right;
  }
}