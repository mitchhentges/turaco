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

    /**
     * Returns the next operator, where:
     * 0 => OR
     * 1 => XOR
     * 2 => AND
     * -1 => No operator
     *
     * @param startAt    where to start look for an operator from
     * @param expression string to search for an operator in
     * @return the type of the next operator
     */
    public static int nextOp(int startAt, String expression) {
        // Returns the integer value of the next operator in the expression, -1 if a
        // closing bracket is found or if we get to the end of the expression
        char aChar;
        int balance = 0; // The number of nesting-levels deep we are into brackets ()
        for (int i = startAt; i < expression.length(); i++) {
            aChar = expression.charAt(i);
            if (isOR(aChar))
                return 0;
            if (isXOR(aChar))
                return 1;
            if (isAND(aChar))
                return 2;
            if (aChar == '(') {
                balance++;
                do {
                    int start = expression.indexOf('(', i + 1);
                    int end = expression.indexOf(')', i + 1);

                    if (start < end && start != -1) {
                        i = start;
                        balance++;
                    } else if (end != -1) { // ')' is before '(', and isn't nonexistent (-1)
                        i = end;
                        balance--;
                    } else { // Neither type of bracket was found
                        return -1;
                    }
                } while (balance > 0);
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
        return aChar == '*' || aChar == '&';
    }

    public static boolean isOR(char aChar) {
        return aChar == '+' || aChar == '|';
    }

    public static boolean isXOR(char aChar) {
        return aChar == '^';
    }

    public static boolean isNOT(char aChar) {
        return aChar == '!' || aChar == '~';
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
     *
     * @param anOp converted to string representation
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