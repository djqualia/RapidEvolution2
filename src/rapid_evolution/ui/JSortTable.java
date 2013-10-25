package rapid_evolution.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import rapid_evolution.ui.main.SearchPane;

public class JSortTable extends JTable
  implements MouseListener
{

    private static Logger log = Logger.getLogger(JSortTable.class);

    public int sortedColumnIndex = -1;
  public boolean sortedColumnAscending = true;

  public JSortTable()
  {
    this(new DefaultSortTableModel());

  }

  public JSortTable(int rows, int cols)
  {
    this(new DefaultSortTableModel(rows, cols));
  }

  public JSortTable(Object[][] data, Object[] names)
  {
    this(new DefaultSortTableModel(data, names));
  }

  public JSortTable(Vector data, Vector names)
  {
    this(new DefaultSortTableModel(data, names));
  }

  public JSortTable(SortTableModel model)
  {
    super(model);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel)
  {
    super(model, colModel);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel,
    ListSelectionModel selModel)
  {
    super(model, colModel, selModel);
    initSortHeader();
  }

  protected void initSortHeader()
  {
    JTableHeader header = getTableHeader();
    header.setDefaultRenderer(new SortHeaderRenderer());
    header.addMouseListener(this);
  }

  public int getSortedColumnIndex()
  {
    return sortedColumnIndex;
  }

  public boolean isSortedColumnAscending()
  {
    return sortedColumnAscending;
  }

  public void sort(ColumnConfig config) {
      DefaultSortTableModel model = (DefaultSortTableModel)this.getModel();
      if (config.tertiary_sort_column != -1) {
          model.sortColumn(config.tertiary_sort_column, config.tertiary_sort_ascending, true, false);
          sortedColumnAscending = config.tertiary_sort_ascending;
          sortedColumnIndex = config.tertiary_sort_column;
      }
      if (config.secondary_sort_column != -1) {
          model.sortColumn(config.secondary_sort_column, config.secondary_sort_ascending, true, false);   
          sortedColumnAscending = config.secondary_sort_ascending;
          sortedColumnIndex = config.secondary_sort_column;
      }
      if (config.primary_sort_column != -1) {
          model.sortColumn(config.primary_sort_column, config.primary_sort_ascending, false, false);
          sortedColumnAscending = config.primary_sort_ascending;
          sortedColumnIndex = config.primary_sort_column;
      } else {
          model.sortColumn(0, true, false, false);
          sortedColumnAscending = true;
          sortedColumnIndex = 0;          
      }
      repaint();
  }
  
  
  public void mouseReleased(MouseEvent event)
  {
    TableColumnModel colModel = getColumnModel();
    int index = colModel.getColumnIndexAtX(event.getX());
    if (index < 0) return;
    int modelIndex = colModel.getColumn(index).getModelIndex();    
    
    /*
    int num_columns = getColumnModel().getColumnCount();
    Vector columns = new Vector();
    for (int c = 0; c < num_columns; ++c) {
        columns.add(getColumnModel().getColumn(c).getHeaderValue());
    }
    getColumnModel().getColumnCount();    
    if (log.isTraceEnabled()) log.trace("mouseReleased(): columns=" + columns);    
    */
    
    SortTableModel model = (SortTableModel)getModel();
    if ((this == SearchPane.instance.searchtable) && RapidEvolutionUI.instance.isSearching()) return;
    if (model.isSortable(modelIndex))
    {
      // toggle ascension, if already sorted
      if (sortedColumnIndex == index)
      {
        sortedColumnAscending = !sortedColumnAscending;
      }
      sortedColumnIndex = index;

      model.sortColumn(modelIndex, sortedColumnAscending);
      repaint();
    }
  }

  public void mousePressed(MouseEvent event) {}
  public void mouseClicked(MouseEvent event) {}
  public void mouseEntered(MouseEvent event) {}
  public void mouseExited(MouseEvent event) {}
}
