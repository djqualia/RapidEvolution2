package rapid_evolution.ui;

import javax.swing.JTable;

public class TableConfig {
  public TableConfig() { };
  public TableConfig(boolean _cellselection, boolean _columnselection, boolean _rowselection, int _autoresize, int _selectionmode) {
    cellselection = _cellselection;
    rowselection = _rowselection;
    autoresize = _autoresize;
    selectionmode = _selectionmode;
    columnselection = _columnselection;
  }
  public boolean cellselection = false;
  public boolean rowselection = true;
  public boolean columnselection = false;
  public int autoresize = JTable.AUTO_RESIZE_OFF;
  public int selectionmode;
  public void setConfig(JTable table) {
    table.setCellSelectionEnabled(cellselection);
    table.setAutoResizeMode(autoresize);
    table.setRowSelectionAllowed(rowselection);
    table.setSelectionMode(selectionmode);
    table.setColumnSelectionAllowed(columnselection);
  }
}
