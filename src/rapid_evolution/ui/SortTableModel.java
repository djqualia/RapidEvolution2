package rapid_evolution.ui;

import javax.swing.table.TableModel;
import rapid_evolution.RapidEvolution;
import javax.swing.Icon;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface SortTableModel
    extends TableModel
 {
    public boolean isSortable(int col);
    public void sortColumn(
       int col, boolean ascending);
 }
