package rapid_evolution.ui;

import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ChangeEvent;
import rapid_evolution.RapidEvolution;
import javax.swing.event.ListSelectionEvent;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.MixoutPane;

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

public class NewTableColumnModelListener implements TableColumnModelListener {
  public NewTableColumnModelListener(JSortTable in_table) { table = in_table; }
  public JSortTable table = null;
  public void columnMarginChanged(ChangeEvent e) { DefaultSortTableModel ds = (DefaultSortTableModel) table.getModel();  ds.donotsort = true; }
  public void columnSelectionChanged(ListSelectionEvent le) {  }
  public void columnAdded(TableColumnModelEvent te) { }
  public void columnMoved(TableColumnModelEvent te) {
    // prevent sorting column when dragging and dropping, also must update the sort icon...
      ColumnConfig config = null;
      int from_index = te.getFromIndex();
      int to_index = te.getToIndex();
    if (from_index != to_index) {
      DefaultSortTableModel ds = (DefaultSortTableModel) table.getModel();
      if (ds == SearchPane.instance.searchtable.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns - 1; ++i) {
            if (SearchPane.instance.searchtable.getColumnName(i).equals(SearchPane.instance.searchcolumnconfig.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          SearchPane.instance.searchtable.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          SearchPane.instance.searchtable.sortedColumnIndex = lastsortedcolumn++;
        config = SearchPane.instance.searchcolumnconfig;
      }
      else if (ds == MixoutPane.instance.mixouttable.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns - 1; ++i) {
            if (MixoutPane.instance.mixouttable.getColumnName(i).equals(MixoutPane.instance.mixoutcolumnconfig.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          MixoutPane.instance.mixouttable.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          MixoutPane.instance.mixouttable.sortedColumnIndex = lastsortedcolumn++;
        config = MixoutPane.instance.mixoutcolumnconfig;
      }
      else if (ds == SyncUI.instance.synctable.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < SyncUI.instance.synccolumnconfig.num_columns - 1; ++i) {
            if (SyncUI.instance.synctable.getColumnName(i).equals(SyncUI.instance.synccolumnconfig.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          SyncUI.instance.synctable.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          SyncUI.instance.synctable.sortedColumnIndex = lastsortedcolumn++;
        config = SyncUI.instance.synccolumnconfig;
      }
      else if (ds == AddMatchQueryUI.instance.matchtable.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < AddMatchQueryUI.instance.matchcolumnconfig.num_columns - 1; ++i) {
            if (AddMatchQueryUI.instance.matchtable.getColumnName(i).equals(AddMatchQueryUI.instance.matchcolumnconfig.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          AddMatchQueryUI.instance.matchtable.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          AddMatchQueryUI.instance.matchtable.sortedColumnIndex = lastsortedcolumn++;
        config = AddMatchQueryUI.instance.matchcolumnconfig;
      }
      else if (ds == EditMatchQueryUI.instance.matchtable2.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < EditMatchQueryUI.instance.matchcolumnconfig2.num_columns - 1; ++i) {
            if (EditMatchQueryUI.instance.matchtable2.getColumnName(i).equals(EditMatchQueryUI.instance.matchcolumnconfig2.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          EditMatchQueryUI.instance.matchtable2.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          EditMatchQueryUI.instance.matchtable2.sortedColumnIndex = lastsortedcolumn++;
        config = EditMatchQueryUI.instance.matchcolumnconfig2;
      }
      else if (ds == SuggestedMixesUI.instance.suggestedtable.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns - 1; ++i) {
            if (SuggestedMixesUI.instance.suggestedtable.getColumnName(i).equals(SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          SuggestedMixesUI.instance.suggestedtable.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          SuggestedMixesUI.instance.suggestedtable.sortedColumnIndex = lastsortedcolumn++;
        config = SuggestedMixesUI.instance.suggestedcolumnconfig;
      }
      else if (ds == ExcludeUI.instance.excludetable.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < ExcludeUI.instance.excludecolumnconfig.num_columns - 1; ++i) {
            if (ExcludeUI.instance.excludetable.getColumnName(i).equals(ExcludeUI.instance.excludecolumnconfig.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          ExcludeUI.instance.excludetable.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          ExcludeUI.instance.excludetable.sortedColumnIndex = lastsortedcolumn++;
        config = ExcludeUI.instance.excludecolumnconfig;
      }
      else if (ds == RootsUI.instance.rootstable.getModel()) {
        int lastsortedcolumn = -1;
        if (ds.lastsortedcolumn >= 0) {
          for (int i = 0; i < RootsUI.instance.rootscolumnconfig.num_columns - 1; ++i) {
            if (RootsUI.instance.rootstable.getColumnName(i).equals(RootsUI.instance.rootscolumnconfig.columntitles[ds.lastsortedcolumn])) lastsortedcolumn = i;
          }
        }
        if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          RootsUI.instance.rootstable.sortedColumnIndex = lastsortedcolumn--;
        if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
          RootsUI.instance.rootstable.sortedColumnIndex = lastsortedcolumn++;
        config = RootsUI.instance.rootscolumnconfig;
      } else if (table == OptionsUI.instance.available_skins_table) {
          int lastsortedcolumn = -1;
          if (ds.lastsortedcolumn >= 0) {
            for (int i = 0; i < ds.getColumnCount() - 1; ++i) {
              if (OptionsUI.instance.available_skins_table.getColumnName(i).equals(OptionsUI.instance.available_skins_table.getColumnName(ds.lastsortedcolumn))) lastsortedcolumn = i;
            }
          }
          if ((te.getFromIndex() < te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
              OptionsUI.instance.available_skins_table.sortedColumnIndex = lastsortedcolumn--;
          if ((te.getFromIndex() > te.getToIndex()) && ((te.getToIndex() == lastsortedcolumn) || (te.getFromIndex() == lastsortedcolumn)))
              OptionsUI.instance.available_skins_table.sortedColumnIndex = lastsortedcolumn++;          
          config = OptionsUI.instance.availableskinconfig;
      }
      if (to_index < from_index) {
          if ((config.primary_sort_column >= to_index) && (config.primary_sort_column < from_index))
              config.primary_sort_column++;
          else if (config.primary_sort_column == from_index)
              config.primary_sort_column = to_index;
          if ((config.secondary_sort_column >= to_index) && (config.secondary_sort_column < from_index))
              config.secondary_sort_column++;
          else if (config.secondary_sort_column == from_index)
              config.secondary_sort_column = to_index;
          if ((config.tertiary_sort_column >= to_index) && (config.tertiary_sort_column < from_index))
              config.tertiary_sort_column++;
          else if (config.tertiary_sort_column == from_index)
              config.tertiary_sort_column = to_index;                
      } else {
          // to_index > from_index
          if ((config.primary_sort_column > from_index) && (config.primary_sort_column <= to_index))
              config.primary_sort_column--;
          else if (config.primary_sort_column == from_index)
              config.primary_sort_column = to_index;
          if ((config.secondary_sort_column > from_index) && (config.secondary_sort_column <= to_index))
              config.secondary_sort_column--;
          else if (config.secondary_sort_column == from_index)
              config.secondary_sort_column = to_index;
          if ((config.tertiary_sort_column > from_index) && (config.tertiary_sort_column <= to_index))
              config.tertiary_sort_column--;
          else if (config.tertiary_sort_column == from_index)
              config.tertiary_sort_column = to_index;                                
      }
      ds.donotsort = true;      
    }    
  }
  public void columnRemoved(TableColumnModelEvent te) { }
}
