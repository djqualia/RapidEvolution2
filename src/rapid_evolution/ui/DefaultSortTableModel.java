package rapid_evolution.ui;

import javax.swing.table.DefaultTableModel;
import java.util.Collections;
import rapid_evolution.SongLinkedList;
import java.util.Vector;
import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.SortTableModel;
import rapid_evolution.comparables.ColumnComparator;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.MixListMouse;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import java.util.HashMap;
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
public class DefaultSortTableModel
  extends DefaultTableModel
  implements SortTableModel
{
    private static Logger log = Logger.getLogger(DefaultSortTableModel.class);
    
   public boolean donotsort = false;
  public int lastsortedcolumn = -1;
  public DefaultSortTableModel() {}

  public DefaultSortTableModel(int rows, int cols)
  {
    super(rows, cols);
  }

  public DefaultSortTableModel(Object[][] data, Object[] names)
  {
    super(data, names);
  }

  public DefaultSortTableModel(Object[] names, int rows)
  {
    super(names, rows);
  }

  public DefaultSortTableModel(Vector names, int rows)
  {
    super(names, rows);
  }

  public DefaultSortTableModel(Vector data, Vector names)
  {
    super(data, names);
  }

  public boolean isSortable(int col)
  {
    if (donotsort) { donotsort = false; return false;} else return true;
  }

  public HashMap row_model_to_view = new HashMap();
  
  public int convertRowToViewIndex(int model_index) {
      if (row_model_to_view.size() == 0) return model_index;
      Integer val = (Integer)row_model_to_view.get(new Integer(model_index));
      if (val != null) return val.intValue();
      return -1;
  }
  
  public void recomputeModelToViewMap() {
      row_model_to_view.clear();
      for (int r = 0; r < getDataVector().size(); ++r) {
          Vector row = (Vector)getDataVector().get(r);
          SongLinkedList song = (SongLinkedList)row.get(SearchPane.instance.searchcolumnconfig.num_columns);
          if (song != null) {
              Integer model_index = (Integer)SearchPane.instance.searchmodel_index.get(new Long(song.uniquesongid));
              if (model_index != null) {
                  row_model_to_view.put(model_index, new Integer(r));
              }
          }
      }
      
  }  
  
  public void sortColumn(int col, boolean ascending) {
      sortColumn(col, ascending, false, true);
  }
  
  public void sortColumn(int col, boolean ascending, boolean subsort, boolean persistSort)
  {
      if (log.isTraceEnabled()) log.trace("sortColumn(): col=" + col + ", ascending=" + ascending);
      if (col >= this.getColumnCount()) return;
      try {
    lastsortedcolumn = col;
    ColumnConfig config = null;
    Collections.sort(getDataVector(),
      new ColumnComparator(col, ascending));
        
    if (!subsort) {
    try {
      if ((MixoutPane.instance != null) && (MixoutPane.instance.mixouttable != null)  && (this == MixoutPane.instance.mixouttable.getModel())) {
          config = MixoutPane.instance.mixoutcolumnconfig;
        if ((MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) && !MixoutPane.instance.recreatingmixouts) {
          MixListMouse.instance.edititem2.setEnabled(true);
          MixListMouse.instance.detectkeys2.setEnabled(true);
          MixListMouse.instance.detectbpms2.setEnabled(true);
          MixListMouse.instance.detectcolors2.setEnabled(true);
          MixListMouse.instance.playselection2.setEnabled(true);
          MixListMouse.instance.readtagsselected2.setEnabled(true);
          MixListMouse.instance.writetagsselected2.setEnabled(true);
          MixListMouse.instance.deleteselected2.setEnabled(true);
        } else {
          MixListMouse.instance.edititem2.setEnabled(false);
          MixListMouse.instance.detectkeys2.setEnabled(false);
          MixListMouse.instance.detectbpms2.setEnabled(false);
          MixListMouse.instance.detectcolors2.setEnabled(false);
          MixListMouse.instance.playselection2.setEnabled(false);
          MixListMouse.instance.readtagsselected2.setEnabled(false);
          MixListMouse.instance.writetagsselected2.setEnabled(false);
          MixListMouse.instance.deleteselected2.setEnabled(false);
        }
        if ((MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) && (!MixoutPane.instance.mixoutscore.hasFocus()) && (!MixoutPane.instance.mixoutcomments.hasFocus()) && (!MixoutPane.instance.addoncheckbox.hasFocus()) && (!MixoutPane.instance.bpmdifffield.hasFocus())) {
          try {
            MixoutPane.instance.selectedmixout = (SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(MixoutPane.instance.mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns);
          } catch (Exception e1) { }
        }
        if (((RapidEvolutionUI.instance.currentsong != null) && !MixoutPane.instance.recreatingmixouts) && (!MixoutPane.instance.mixoutscore.hasFocus()) && (!MixoutPane.instance.mixoutcomments.hasFocus()) && (!MixoutPane.instance.addoncheckbox.hasFocus()) && (!MixoutPane.instance.bpmdifffield.hasFocus())) {
          int row = MixoutPane.instance.mixouttable.getSelectedRow();
          if ((row >= RapidEvolutionUI.instance.currentsong.getNumMixoutSongs()) || (row < 0)) {
            MixoutPane.instance.addoncheckbox.setSelected(false);
            MixoutPane.instance.addoncheckbox.setEnabled(false);
            MixoutPane.instance.mixoutcomments.setEnabled(false);
            MixoutPane.instance.mixoutcomments.setText("");
            MixoutPane.instance.mixoutscore.setEnabled(false);
            MixoutPane.instance.mixoutscore.setText("");
            MixoutPane.instance.bpmdifffield.setEnabled(false);
            MixoutPane.instance.bpmdifffield.setText("");
            MixoutPane.instance.calculatebpmdiffbutton.setEnabled(false);
            MixoutPane.instance.mixoutcommentslabel.setEnabled(false);
            MixoutPane.instance.scorefield.setEnabled(false);
            MixoutPane.instance.bpmdifflabel.setEnabled(false);
            return;
          }
          SongLinkedList song = (SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(MixoutPane.instance.mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns);
          MixoutPane.instance.selectedmixout = song;
          MixoutPane.instance.addoncheckbox.setEnabled(true);
          MixoutPane.instance.mixoutcomments.setEnabled(true);
          MixoutPane.instance.mixoutscore.setEnabled(true);
          MixoutPane.instance.bpmdifffield.setEnabled(true);
          MixoutPane.instance.calculatebpmdiffbutton.setEnabled(true);
          MixoutPane.instance.mixoutcommentslabel.setEnabled(true);
          MixoutPane.instance.scorefield.setEnabled(true);
          MixoutPane.instance.bpmdifflabel.setEnabled(true);
          for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
            if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == song.uniquesongid) {
              MixoutPane.instance.mixoutcomments.setText(RapidEvolutionUI.instance.currentsong.getMixoutComments(i));
              MixoutPane.instance.mixoutscore.setText(String.valueOf(RapidEvolutionUI.instance.currentsong.getMixoutRank(i)));
              String bpmtext = String.valueOf(RapidEvolutionUI.instance.currentsong.getMixoutComments(i));
              int maxlen = 6;
              if (RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i) > 0) bpmtext = new String("+" + bpmtext);
              if (bpmtext.length() < maxlen) maxlen = bpmtext.length();
              MixoutPane.instance.bpmdifffield.setText(bpmtext.substring(0, maxlen));
              if (RapidEvolutionUI.instance.currentsong.getMixoutAddon(i)) MixoutPane.instance.addoncheckbox.setSelected(true);
              else MixoutPane.instance.addoncheckbox.setSelected(false);
            }
          }
        }
      } else if ((SearchPane.instance != null) && (SearchPane.instance.searchtable != null) && (this == SearchPane.instance.searchtable.getModel())) {
          config = SearchPane.instance.searchcolumnconfig;
          // bad attempt at fix:
          //SearchPane.searchmodel_index.clear();
//          for (int i = 0; i < getDataVector().size(); ++i) {
//              Long uniquesongid = (Long)((Vector)getDataVector().get(i)).get(config.num_columns);
//              SearchPane.searchmodel_index.put(uniquesongid, new Integer(i));
//          }
          recomputeModelToViewMap();
      } else {
          config = RapidEvolutionUI.instance.getTableConfig(this);
      }
    } catch (Exception e) {     	log.error("sortColumn(): error Exception", e); }
    }
    
    if ((config != null) && persistSort) {
        if (config.primary_sort_column != col) {
            config.tertiary_sort_column = config.secondary_sort_column;
            config.tertiary_sort_ascending = config.secondary_sort_ascending;
            config.secondary_sort_column = config.primary_sort_column;
            config.secondary_sort_ascending = config.primary_sort_ascending;
            config.primary_sort_column = col;
            config.primary_sort_ascending = ascending;
        } else {
            config.primary_sort_ascending = ascending;
        }
        if (config.primary_sort_column == config.tertiary_sort_column) {
            config.tertiary_sort_column = -1;
        }
    }
      } catch (Exception e) {
          log.error("sortColumn(): error Exception", e);
      }
  }
}
