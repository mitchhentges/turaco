package bexpred;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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

public class BExprCompare extends JFrame {
    JPanel jPanel1 = new JPanel();
    JTextField jTextField1 = new JTextField();
    JTextField jTextField2 = new JTextField();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JButton jButton1 = new JButton();
    JTextField jTextField3 = new JTextField();

    public BExprCompare() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        jTextField1.setText("");
        jTextField1.setBounds(new Rectangle(96, 8, 275, 19));
        jTextField1.addKeyListener(new BExprCompare_jTextField1_keyAdapter(this));
        jPanel1.setLayout(null);
        jTextField2.setText("");
        jTextField2.setBounds(new Rectangle(96, 32, 275, 19));
        jTextField2.addKeyListener(new BExprCompare_jTextField2_keyAdapter(this));
        jLabel1.setText("Expression 1");
        jLabel1.setBounds(new Rectangle(4, 10, 87, 15));
        jLabel2.setBounds(new Rectangle(4, 34, 87, 15));
        jLabel2.setText("Expression 2");
        jButton1.setBounds(new Rectangle(220, 59, 96, 25));
        jButton1.setText("Compare");
        jButton1.addActionListener(new BExprCompare_jButton1_actionAdapter(this));
        jTextField3.setText("");
        jTextField3.setBounds(new Rectangle(321, 62, 50, 19));
        this.setResizable(false);
        this.setTitle("Expression Compare");
        //this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        jPanel1.setMinimumSize(new Dimension(385, 90));
        jPanel1.setPreferredSize(new Dimension(380, 90));
        jPanel1.add(jTextField2, null);
        jPanel1.add(jLabel1, null);
        jPanel1.add(jTextField1, null);
        jPanel1.add(jLabel2, null);
        jPanel1.add(jButton1, null);
        jPanel1.add(jTextField3, null);
        this.getContentPane().add(jPanel1, null);
    }

    void doCompare() {
        try {
            BExprTree t1 = new BExprTree(this.jTextField1.getText());
            BExprTree t2 = new BExprTree(this.jTextField2.getText());
            this.jTextField3.setText(t1.compareTo(t2) ? "True" : "False");
        } catch (Exception ex) {
            this.jTextField3.setText(this.jTextField1.getText().equals(this.jTextField2.getText()) ? "True" : "False");
        }
    }

    void jButton1_actionPerformed() {
        this.doCompare();
    }

    void jTextField1_keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10)
            this.doCompare();
    }

    void jTextField2_keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10)
            this.doCompare();
    }
}

class BExprCompare_jButton1_actionAdapter implements java.awt.event.ActionListener {
    BExprCompare adaptee;

    BExprCompare_jButton1_actionAdapter(BExprCompare adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed();
    }
}

class BExprCompare_jTextField1_keyAdapter extends java.awt.event.KeyAdapter {
    BExprCompare adaptee;

    BExprCompare_jTextField1_keyAdapter(BExprCompare adaptee) {
        this.adaptee = adaptee;
    }

    public void keyPressed(KeyEvent e) {
        adaptee.jTextField1_keyPressed(e);
    }
}

class BExprCompare_jTextField2_keyAdapter extends java.awt.event.KeyAdapter {
    BExprCompare adaptee;

    BExprCompare_jTextField2_keyAdapter(BExprCompare adaptee) {
        this.adaptee = adaptee;
    }

    public void keyPressed(KeyEvent e) {
        adaptee.jTextField2_keyPressed(e);
    }
}