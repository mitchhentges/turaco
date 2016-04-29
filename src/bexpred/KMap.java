package bexpred;

import javax.swing.*;
import java.util.ArrayList;

public class KMap extends JFrame {
  private ArrayList vars;
  private ArrayList maps; // Contains the actual maps

  public KMap() {
  }

  public KMap(TruthTable aTT) {
    this.vars = (ArrayList) aTT.getVars().clone();
  }
}