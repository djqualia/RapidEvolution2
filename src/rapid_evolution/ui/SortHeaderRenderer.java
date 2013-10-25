package rapid_evolution.ui;

import javax.swing.JTable;

import rapid_evolution.Artist;
import rapid_evolution.RapidEvolution;
import rapid_evolution.StringUtil;
import javax.swing.Icon;
import javax.swing.table.JTableHeader;
import java.awt.Font;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import java.awt.FontMetrics;
import rapid_evolution.ui.SortArrowIcon;
import rapid_evolution.ui.RapidEvolutionUI;
import java.awt.Color;
import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.ui.swing.lookfeel.*;

public class SortHeaderRenderer
  extends DefaultTableCellRenderer
{
    
    private static Logger log = Logger.getLogger(SortHeaderRenderer.class);
    
  public Icon NONSORTED = new SortArrowIcon(0);
  public Icon ASCENDING = new SortArrowIcon(2);
  public Icon DECENDING = new SortArrowIcon(1);

  public SortHeaderRenderer()
  {
    setHorizontalTextPosition(LEFT);
    setHorizontalAlignment(CENTER);
  }

  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int col)
  {
      try {
    int index = -1;
    boolean ascending = true;

    if (table instanceof JSortTable)
    {
      JSortTable sortTable = (JSortTable)table;
      index = sortTable.getSortedColumnIndex();
      ascending = sortTable.isSortedColumnAscending();
    }
    if (table != null)
    {
      JTableHeader header = table.getTableHeader();
      if (header != null)
      {
        if (SkinManager.instance.colormap.get("table_header_foreground") != null)
          setForeground(SkinManager.instance.getColor("table_header_foreground"));
        else if (SkinManager.instance.colormap.get("default_foreground") != null)
          setForeground(SkinManager.instance.getColor("default_foreground"));
        else  setForeground(header.getForeground());
        if (SkinManager.instance.colormap.get("table_header_background") != null)
          setBackground(SkinManager.instance.getColor("table_header_background"));
        else if (SkinManager.instance.colormap.get("default_background") != null)
          setBackground(SkinManager.instance.getColor("default_background"));
        else setBackground(header.getBackground());
        setFont(header.getFont());
      }
    }
    Icon icon = ascending ? ASCENDING : DECENDING;
    setIcon(col == index ? icon : NONSORTED);

    if (value == null) value = new String("");
    setToolTipText(null);
    int maxWidth = table.getColumnModel().getColumn(col).getWidth() ;
    String testString = value.toString();
    Font f = table.getFont();
    FontMetrics fm = this.getFontMetrics(f);
    int valueLength = fm.stringWidth(testString);
    // TODO: this could be done much better probably
    if (StringUtil.substring("Windows", LookAndFeelManager.getLookAndFeelName())) if ( (valueLength + 18) >= maxWidth) { setToolTipText(testString); }
    else if (StringUtil.substring("CDE/Motif", LookAndFeelManager.getLookAndFeelName())) if ( (valueLength + 16) >= maxWidth) { setToolTipText(testString); }
    else if ( (valueLength + 12) >= maxWidth) { setToolTipText(testString); }
//      setText(testString);

    setText((value == null) ? "" : value.toString());
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      } catch (Exception e) { log.error("getTableCellRendererComponent(): error", e); }
    
    return this;
  }
}
