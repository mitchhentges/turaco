package bexpred;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Hashtable;
import java.util.ArrayList;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

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

public class BExpredMain extends JFrame {
  JPanel jPanel1 = new JPanel();
  JTextField ExprField = new JTextField();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton EvaluateBtn = new JButton();
  TTFrame ttFrame;
  BExprCompare cmpFrame;

  BExprTree bexprtree = null;
  ArrayList var_inputs = new ArrayList();
  JTextField valueField = new JTextField();
  JButton TTBtn = new JButton();


  boolean needs_refresh = false;
  boolean showPlaceholder = true;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenu jMenu2 = new JMenu();
  JCheckBoxMenuItem jCheckBoxMenuItem1 = new JCheckBoxMenuItem();
  JCheckBoxMenuItem jCheckBoxMenuItem2 = new JCheckBoxMenuItem();
  JButton ReduceBtn = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField SOPField = new JTextField();
  JPanel jPanel3 = new JPanel();
  JTextField POSField = new JTextField();
  JTextField exceptionField = new JTextField();
  JScrollPane jScrollPane1 = new JScrollPane();
  JPanel jPanel2 = new JPanel();
  JCheckBox SOPCheck = new JCheckBox();
  JCheckBox POSCheck = new JCheckBox();
  //Construct the frame
  public BExpredMain() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  //Component initialization
  private void jbInit() throws Exception  {
    //this.setContentPane(jPanel1);
    this.setDefaultCloseOperation(HIDE_ON_CLOSE);
    this.setEnabled(true);
    this.setForeground(Color.black);
    this.setLocale(java.util.Locale.getDefault());
    this.setJMenuBar(jMenuBar1);
    this.setResizable(false);
    this.setSize(new Dimension(392, 385));
    this.setState(Frame.NORMAL);
    this.setTitle("Turaco");
    ExprField.setSelectionStart(19);
    ExprField.setText("Enter an expression");
    ExprField.setBounds(new Rectangle(7, 6, 378, 19));
    ExprField.addFocusListener(new BExpredMain_ExprField_focusAdapter(this));
    ExprField.addKeyListener(new BExpredMain_ExprField_keyAdapter(this));
    ExprField.addMouseListener(new BExpredMain_ExprField_mouseAdapter(this));
    ExprField.getDocument().addDocumentListener(new BExpredMain_ExprField_documentListener(this));
    jPanel1.setAlignmentX((float) 0.5);
    jPanel1.setAlignmentY((float) 0.5);
    jPanel1.setOpaque(true);
    jPanel1.setLayout(null);
    EvaluateBtn.setBounds(new Rectangle(236, 352, 86, 25));
    EvaluateBtn.setText("Evaluate");
    EvaluateBtn.addActionListener(new BExpredMain_EvaluateBtn_actionAdapter(this));
    valueField.setText("");
    valueField.setBounds(new Rectangle(326, 355, 56, 19));
    valueField.setEditable(false);
    ttFrame = new TTFrame();
    TTBtn.setBounds(new Rectangle(278, 30, 107, 25));
    TTBtn.setText("Truth Table");
    TTBtn.addActionListener(new BExpredMain_TTBtn_actionAdapter(this));
    cmpFrame = new BExprCompare();
    cmpFrame.setJMenuBar(null);
    jMenu1.setText("File");
    jMenuItem1.setText("Exit");
    jMenuItem1.addMouseListener(new BExpredMain_jMenuItem1_mouseAdapter(this));
    jMenu2.setText("Tools");
    jMenu2.addMouseListener(new BExpredMain_jMenu2_mouseAdapter(this));
    jCheckBoxMenuItem1.setText("Truth Table");
    jCheckBoxMenuItem1.addMouseListener(new BExpredMain_jCheckBoxMenuItem1_mouseAdapter(this));
    jCheckBoxMenuItem2.setText("Expression comparator");
    jCheckBoxMenuItem2.addMouseListener(new BExpredMain_jCheckBoxMenuItem2_mouseAdapter(this));
    ReduceBtn.setBounds(new Rectangle(7, 30, 88, 25));
    ReduceBtn.setActionCommand("Reduce");
    ReduceBtn.setText("Reduce");
    ReduceBtn.addActionListener(new BExpredMain_ReduceBtn_actionAdapter(this));
    jLabel1.setFont(new java.awt.Font("SansSerif", 0, 12));
    jLabel1.setToolTipText("");
    jLabel1.setText("Sum of Products");
    jLabel1.setBounds(new Rectangle(9, 64, 98, 15));
    jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12));
    jLabel2.setText("Product of Sums");
    jLabel2.setBounds(new Rectangle(9, 83, 98, 16));
    SOPField.setText("");
    SOPField.setBounds(new Rectangle(111, 62, 274, 19));
    SOPField.setEditable(false);
    jPanel3.setLayout(null);
    jPanel3.setAlignmentY((float) 0.5);
    jPanel3.setAlignmentX((float) 0.5);
    POSField.setText("");
    POSField.setBounds(new Rectangle(111, 82, 274, 19));
    POSField.setEditable(false);
    exceptionField.setText("");
    exceptionField.setBounds(new Rectangle(9, 355, 223, 19));
    exceptionField.setEditable(false);
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    jScrollPane1.setBounds(new Rectangle(9, 113, 373, 228));
    jPanel2.setBounds(new Rectangle(3, 3, 102, 80));
    jPanel2.setLayout(null);
    SOPCheck.setSelected(true);
    SOPCheck.setText("SoP");
    SOPCheck.setBounds(new Rectangle(96, 32, 50, 23));
    POSCheck.setSelected(true);
    POSCheck.setText("PoS");
    POSCheck.setBounds(new Rectangle(145, 32, 49, 23));
    jPanel1.add(ExprField, null);
    jPanel1.add(ReduceBtn, null);
    jPanel1.add(TTBtn, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(POSField, null);
    jPanel1.add(SOPField, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jScrollPane1, null);
    jPanel1.add(EvaluateBtn, null);
    jPanel1.add(valueField, null);
    jPanel1.add(SOPCheck, null);
    jPanel1.add(exceptionField, null);
    jScrollPane1.getViewport().add(jPanel2, null);
    jMenuBar1.add(jMenu1);
    jMenuBar1.add(jMenu2);
    jMenu1.add(jMenuItem1);
    jMenu2.add(jCheckBoxMenuItem1);
    jMenu2.add(jCheckBoxMenuItem2);
    this.getContentPane().add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(POSCheck, null);

    //Adjusting the size of the main window
    Dimension goodSize = this.jScrollPane1.getPreferredSize();
    goodSize.width += 2*(this.jScrollPane1.getX());
    goodSize.height += this.jScrollPane1.getY() + 10;
    jPanel1.setPreferredSize(new Dimension(390, 385));
  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  void refreshTree() {
    JCheckBox aCheck;
    Dimension dim;
    int col, lastCol, colPos, maxColWidth;

    this.needs_refresh = false;

    // Create the expression tree from the expression.
    try {
      this.bexprtree = new BExprTree(this.ExprField.getText());
      //System.out.println(this.bexprtree.getTruthTable().getExpr(this.bexprtree.getTruthTable().reduceVars(this.bexprtree.getVars())));
    } catch (BExprPreParseException ex) {
      this.bexprtree = null;
      this.exceptionField.setText(ex.getMessage());
      ex.printStackTrace();
    } catch (StackOverflowError ex) {
      this.bexprtree = null;
      this.exceptionField.setText("Stack Overflow: Expression too long");
      ex.printStackTrace();
    }

    // Must clear the panel of the labels and input fields
    for (int i = 0; i < var_inputs.size(); i++) {
      this.jPanel2.remove((JCheckBox) var_inputs.get(i));
      //this.jScrollPane1.getViewport().remove((JCheckBox) var_inputs.get(i));
    }

    this.var_inputs.clear();

    if (this.bexprtree != null) {
      // The list of vars in order of appearance
      ArrayList vars = this.bexprtree.getVars();

      // Let's now create the input fields and labels
      col = 0;
      lastCol = 0;
      colPos = 5;
      maxColWidth = 0;
      for (int i = 0; i < vars.size(); i++) {
        col = (i/9);
        if (col != lastCol) {
          colPos += maxColWidth + 10;
          maxColWidth = 0;
        }

        aCheck = new JCheckBox((String) vars.get(i));
        dim = aCheck.getPreferredSize();

        if ((int) dim.getWidth() > maxColWidth) {
          maxColWidth = (int) dim.getWidth();
        }

        //aCheck.setBounds(new Rectangle((i/9)*100 + 32, (i%9)*22 + 67, 68, 19));
        aCheck.setBounds(new Rectangle(colPos, (i%9)*22 + 5, (int) dim.getWidth(), (int) dim.getHeight()));
        this.jPanel2.add(aCheck, null);
        //this.jScrollPane1.getViewport().add(aCheck, null);
        this.var_inputs.add(aCheck);
        lastCol = col;
      }

      this.jPanel2.setPreferredSize(new Dimension(colPos + maxColWidth, this.jPanel2.getHeight()));
      this.jPanel2.updateUI();
      //this.jScrollPane1.getViewport().setPreferredSize(new Dimension(colPos + maxColWidth, this.jScrollPane1.getViewport().getHeight()));
      //this.jScrollPane1.getViewport().updateUI();

      this.updateTT(this.bexprtree.getTruthTable());
      //TESTING System.out.println(this.bexprtree.getTruthTable().getExpr(this.bexprtree.getVars()));
    }
  }

  void goReduce() {
    if (this.bexprtree == null) {
      return;
    }

    if (this.SOPCheck.isSelected())
      this.SOPField.setText(this.bexprtree.getTruthTable().getSOP(this.bexprtree.getVars()));
    else
      this.SOPField.setText("Option unchecked");

    if (this.POSCheck.isSelected())
      this.POSField.setText(this.bexprtree.getTruthTable().getPOS(this.bexprtree.getVars()));
    else
      this.POSField.setText("Option unchecked");
  }

  void updateTT(TruthTable aTT) {
    ArrayList vars = this.bexprtree.getVars();
    String[] colNames = new String[vars.size() + 1];

    for (int i = 0; i < vars.size(); i++) {
      colNames[i] = (String) vars.get(i);
    }

    colNames[vars.size()] = "Output";

    this.ttFrame.jScrollPane1.getViewport().removeAll();
    //this.ttFrame.jTable1 = new JTable(aTT.getWrappedTable(true), colNames);
    this.ttFrame.jTable1 = new JTTable(aTT.getWrappedTable(), colNames);
    this.ttFrame.jScrollPane1.getViewport().add(this.ttFrame.jTable1);

    // Set the dimensions of the TTFrame
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int ttHeight = (int) this.ttFrame.jTable1.getPreferredSize().getHeight() + 44;
    ttHeight = screenSize.getHeight() - 60 < ttHeight ? ((int) screenSize.getHeight() - 60) : ttHeight;
    this.ttFrame.setSize(this.ttFrame.jTable1.getColumnCount() * 60, ttHeight);
  }

  void ExprTreeBtn_actionPerformed(ActionEvent e) {
    this.refreshTree();
  }

  void EvaluateBtn_actionPerformed(ActionEvent e) {
    Hashtable values = new Hashtable();
    ArrayList vars = this.bexprtree.getVars();

    // Evaluate the expression by passing a HashTable of VAR:VALUE, if VALUE is
    // anything other than !, it will be taken as false.
    for (int i = 0; i < var_inputs.size(); i++) {
      values.put((String) vars.get(i), ((JCheckBox) var_inputs.get(i)).isSelected() ? "1" : "0");
    }
    //DEBUG System.out.println(bexprtree.evaluate(values));
    this.valueField.setText(bexprtree.evaluate(values) ? "True" : "False");
  }

  void ExprField_mouseClicked(MouseEvent e) {
    // This just clears the ExprField when it's clicked and contains "Enter an expression"
    if (this.showPlaceholder) {
      this.ExprField.setText("");
      this.showPlaceholder = false;
    }
  }

  void TTBtn_actionPerformed(ActionEvent e) {
    //Bah, just pops up what's going to be the truth table
    if (this.bexprtree != null) {
      this.ttFrame.show();
    } else {
      // Should be a dialog
      System.out.println("There are no expressions to display in the truth table");
    }

  }

  void ExprField_keyPressed(KeyEvent e) {
    if (e.getKeyCode() == 10) {
      this.refreshTree();
      this.goReduce();
    }
  }

  void ExprField_focusLost(FocusEvent e) {
    if (this.needs_refresh)
      this.refreshTree();
  }

  void ExprField_textChanged(DocumentEvent e) {
    if (this.showPlaceholder) {
      this.showPlaceholder = false;
      String notPlaceholder = "";
      try {
        notPlaceholder = e.getDocument().getText(e.getOffset(), e.getLength());
      } catch (BadLocationException ignored) {}
      final String replacement = notPlaceholder;
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          BExpredMain.this.ExprField.setText(replacement);
        }
      });
    }
  }

  void jMenuItem1_mouseReleased(MouseEvent e) {
    System.exit(0);
  }

  void jMenu2_mousePressed(MouseEvent e) {
    if (this.ttFrame.isVisible())
      jCheckBoxMenuItem1.setState(true);
    else
      jCheckBoxMenuItem1.setState(false);

    if (this.cmpFrame.isVisible())
      jCheckBoxMenuItem2.setState(true);
    else
      jCheckBoxMenuItem2.setState(false);
  }

  void jCheckBoxMenuItem1_mouseReleased(MouseEvent e) {
    if (jCheckBoxMenuItem1.getState()) {
      if (this.bexprtree != null)
        this.ttFrame.show();
      else
        System.out.println("There are no expressions to display in the truth table");
    } else {
      this.ttFrame.hide();
    }
  }

  void jCheckBoxMenuItem2_mouseReleased(MouseEvent e) {
    if (jCheckBoxMenuItem2.getState()) {
      this.cmpFrame.pack();
      this.cmpFrame.show();
    } else {
      this.cmpFrame.hide();
    }
  }

  void ReduceBtn_actionPerformed(ActionEvent e) {
    this.goReduce();
  }
}

class BExpredMain_EvaluateBtn_actionAdapter implements java.awt.event.ActionListener {
  BExpredMain adaptee;

  BExpredMain_EvaluateBtn_actionAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.EvaluateBtn_actionPerformed(e);
  }
}

class BExpredMain_ExprField_mouseAdapter extends java.awt.event.MouseAdapter {
  BExpredMain adaptee;

  BExpredMain_ExprField_mouseAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.ExprField_mouseClicked(e);
  }
}

class BExpredMain_TTBtn_actionAdapter implements java.awt.event.ActionListener {
  BExpredMain adaptee;

  BExpredMain_TTBtn_actionAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.TTBtn_actionPerformed(e);
  }
}

class BExpredMain_ExprField_keyAdapter extends java.awt.event.KeyAdapter {
  BExpredMain adaptee;

  BExpredMain_ExprField_keyAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void keyPressed(KeyEvent e) {
    adaptee.ExprField_keyPressed(e);
  }
}

class BExpredMain_ExprField_focusAdapter extends java.awt.event.FocusAdapter {
  BExpredMain adaptee;

  BExpredMain_ExprField_focusAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void focusLost(FocusEvent e) {
    adaptee.ExprField_focusLost(e);
  }
}

class BExpredMain_ExprField_documentListener implements DocumentListener {
  BExpredMain adaptee;

  public BExpredMain_ExprField_documentListener(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }

  public void insertUpdate(DocumentEvent e) {
    adaptee.ExprField_textChanged(e);
  }

  public void removeUpdate(DocumentEvent e) {
    adaptee.ExprField_textChanged(e);
  }

  public void changedUpdate(DocumentEvent documentEvent) {
  }
}

class BExpredMain_jMenuItem1_mouseAdapter extends java.awt.event.MouseAdapter {
  BExpredMain adaptee;

  BExpredMain_jMenuItem1_mouseAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.jMenuItem1_mouseReleased(e);
  }
}

class BExpredMain_jMenu2_mouseAdapter extends java.awt.event.MouseAdapter {
  BExpredMain adaptee;

  BExpredMain_jMenu2_mouseAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.jMenu2_mousePressed(e);
  }
}

class BExpredMain_jCheckBoxMenuItem1_mouseAdapter extends java.awt.event.MouseAdapter {
  BExpredMain adaptee;

  BExpredMain_jCheckBoxMenuItem1_mouseAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.jCheckBoxMenuItem1_mouseReleased(e);
  }
}

class BExpredMain_jCheckBoxMenuItem2_mouseAdapter extends java.awt.event.MouseAdapter {
  BExpredMain adaptee;

  BExpredMain_jCheckBoxMenuItem2_mouseAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.jCheckBoxMenuItem2_mouseReleased(e);
  }
}

class BExpredMain_ReduceBtn_actionAdapter implements java.awt.event.ActionListener {
  BExpredMain adaptee;

  BExpredMain_ReduceBtn_actionAdapter(BExpredMain adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ReduceBtn_actionPerformed(e);
  }
}