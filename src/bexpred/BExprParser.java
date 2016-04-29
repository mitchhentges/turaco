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

public class BExprParser {

  public static int nextOp(int startAt, String expression) {
    // Returns the integer value of the next operator in the expression, -1 if a
    // closing bracket is found or if we get to the end of the expression
    char aChar;
    for (int i = startAt; i < expression.length(); i++) {
      aChar = expression.charAt(i);
      if (isOR(aChar))
        return 0;
      if (isXOR(aChar))
        return 1;
      if (isAND(aChar))
        return 2;
      if (aChar == '(') {
        i = expression.indexOf(')', i);
      }
      if (aChar == ')')
        return -1;
    }
    return -1;
  }

  public static int getOp(char aChar) {
    if (isOR(aChar))
      return 0;
    if (isXOR(aChar))
      return 1;
    if (isAND(aChar))
      return 2;
    return -1;
  }

  // The comparators can either take in a char, a string, or nothing:
  // char    => simply evaluates the char
  // string  => evaluates the first char of the string
  // nothing => evaluates the first char of the expression

  public static boolean isOp(char aChar) {
    return (isAND(aChar) || isOR(aChar) || isXOR(aChar));
  }

  public static boolean hasOp(String aStr) {
    for (int i = 0; i < aStr.length(); i++) {
      if (isOp(aStr.charAt(i)))
        return true;
    }
    return false;
  }

  public static boolean isAND(char aChar) {
    if (aChar == '*' || aChar == '&')
      return true;
    return false;
  }

  public static boolean isOR(char aChar) {
    if (aChar == '+' || aChar == '|')
      return true;
    return false;
  }

  public static boolean isXOR(char aChar) {
    if (aChar == '^')
      return true;
    return false;
  }

  public static boolean isNOT(char aChar) {
    if (aChar == '!' || aChar == '~')
      return true;
    return false;
  }

  public static boolean hasNOT(String aStr) {
    for (int i = 0; i < aStr.length(); i++) {
      if (isNOT(aStr.charAt(i)))
        return true;
    }
    return false;
  }


  public static boolean isVarChar(char aChar) {
    return (!isOp(aChar) && !isNOT(aChar) && aChar != ' ' && aChar != '(' && aChar != ')');
  }

  /**
   * Return the String representation of an operator
   * @param anOp
   * @return The string representation of an operator
   */
  public static String getStringOp(int anOp) {
    if (anOp == 0)
      return "+";
    if (anOp == 1)
      return "^";
    if (anOp == 2)
      return "*";
    return "";
  }
}