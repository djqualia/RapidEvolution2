package rapid_evolution.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JTable;

import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

public class RETable extends JSortTable {
    
    private static Logger log = Logger.getLogger(RETable.class);
    
    public RETable() {
        //new ExcelAdapter(this);
        this.setDefaultRenderer(JComponent.class, new JComponentCellRenderer());
  }	 
	
  public Component prepareRenderer(TableCellRenderer r, int row, int col) {
    Component c = null;
    try {
      c = super.prepareRenderer(r, row, col);
      if (c != null) {
          int[] selected = this.getSelectedRows();
          for (int i = 0; i < selected.length; ++i) if (selected[i] == row) {
              Color selectionColor = (Color)SkinManager.instance.colormap.get("table_selection_background");
              if (selectionColor != null) {                  
                  c.setBackground(selectionColor);
              }
              return c;
          }
          if (SkinManager.instance.colormap.get("table_cell_background") != null) c.setBackground(SkinManager.instance.getColor("table_cell_background"));
          else if (SkinManager.instance.colormap.get("default_background") != null) c.setBackground(SkinManager.instance.getColor("default_background"));
          else c.setBackground(UIManager.getColor("Table.background"));
          if (SkinManager.instance.colormap.get("table_cell_foreground") != null) c.setForeground(SkinManager.instance.getColor("table_cell_foreground"));
          /*
          if (c instanceof JComponent) {
              JComponent jc = (JComponent)c;
              String tooltipText = (String)getValueAt(row, col);              
              jc.setToolTipText(tooltipText);
              if (log.isTraceEnabled()) log.trace("prepareRenderer(): setting tooltip text, row=" + row + ", col=" + col + ", text=" + tooltipText);
          }
          */
          //      if (this.isRowSelected(row)) c.setForeground(UIManager.getColor("Table.selectionForeground"));
      }
    } catch (Exception e) { }
    return c;
  }
  
  public TableCellRenderer getCellRenderer(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellRenderer renderer = tableColumn.getCellRenderer();
        if (renderer == null) {
            Class c = getColumnClass(column);
            if( c.equals(Object.class) )
            {
                Object o = getValueAt(row,column);
                if( o != null )
                    c = getValueAt(row,column).getClass();
            }
            renderer = getDefaultRenderer(c);
        }
        return renderer;
    }  
  
  public TableCellEditor getCellEditor(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellEditor editor = tableColumn.getCellEditor();
        if (editor == null) {
            Class c = getColumnClass(column);
            if( c.equals(Object.class) )
            {
                Object o = getValueAt(row,column);
                if( o != null )
                    c = getValueAt(row,column).getClass();
            }
            editor = getDefaultEditor(c);
        }
        return editor;
    }  
  
  protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      if (rapid_evolution.RapidEvolution.aaEnabled)
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      super.paintComponent(g);
  }

  public boolean getScrollableTracksViewportHeight()
  {
      Component parent = getParent();

      if (parent instanceof JViewport)
          return parent.getHeight() > getPreferredSize().height;

      return false;
  }
  
  class JComponentCellRenderer implements TableCellRenderer
  {
      public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
          return (JComponent)value;
      }
  }

}
