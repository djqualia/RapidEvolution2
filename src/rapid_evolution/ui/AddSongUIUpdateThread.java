package rapid_evolution.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.Artist;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongList;
import rapid_evolution.ui.main.SearchPane;

public class AddSongUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(AddSongUIUpdateThread.class);
    
    private SongLinkedList addedsong;
    public AddSongUIUpdateThread(SongLinkedList song) {
        addedsong = song;
    }
    
    public void run() {
      try {
        if (SearchPane.searchdisplaylist == null) SearchPane.searchdisplaylist = new SongList(
                addedsong, null);
            else SearchPane.searchdisplaylist.sortedinsert(addedsong);

            Vector row = new Vector();
            for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i)
              row.add(addedsong.get_column_data(SearchPane.instance.searchcolumnconfig.columnindex[i]));
            row.add(addedsong);

            DefaultSortTableModel dstm = (DefaultSortTableModel) SearchPane.instance.searchtable.
                getModel();

            int index = SearchPane.instance.searchtable.getSortedColumnIndex();
            boolean ascending = SearchPane.instance.searchtable.isSortedColumnAscending();
            int insertindex = 0;
            if (index < 0) {
              boolean done = false;
              //String insertstr = addedsong.getSongIdShort();
              while (!done && (insertindex < SearchPane.instance.searchtable.getRowCount())) {
                SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable.getModel().
                    getValueAt(insertindex, SearchPane.instance.searchcolumnconfig.num_columns);
                if (song.compareTo(addedsong) < 0)
                    insertindex++;
                else done = true;
              }
            }
            else {
                index = SearchPane.instance.getSearchColumnModelIndex(SearchPane.instance.searchtable.getColumnName(index));
              boolean done = false;
              Comparable compobj = (Comparable) row.get(index);
              while (!done && (insertindex < SearchPane.instance.searchtable.getRowCount())) {
                Comparable comp = (Comparable) SearchPane.instance.searchtable.getModel().getValueAt(
                    insertindex, index);
                if (ascending) {
                  if (comp.compareTo(compobj) < 0) insertindex++;
                  else done = true;
                }
                else {
                  if (comp.compareTo(compobj) >= 0) insertindex++;
                  else done = true;
                }
              }
            }

            int[] selindices = SearchPane.instance.searchtable.getSelectedRows();
            if (insertindex < SearchPane.instance.searchtable.getRowCount())
              dstm.insertRow(insertindex, row);
            else dstm.addRow(row);
            
            //if (RapidEvolution.debugmode) System.out.println("AddSingleSong(): insertindex: " + insertindex);
            
//            for (int i = 0; i < dstm.getRowCount(); ++i) {
//                SongLinkedList song = (SongLinkedList)dstm.getValueAt(i, SearchPane.instance.searchcolumnconfig.num_columns);
                //if (song == addedsong) if (RapidEvolution.debugmode) System.out.println("AddSingleSong(): modelindex: " + i);
//            }
            
            SearchPane.instance.clearresultsbutton.setEnabled(true);
            RapidEvolutionUI.instance.savelistbutton.setEnabled(true);
            
            HashMap new_model_index = new HashMap();
            Set entries = SearchPane.instance.searchmodel_index.entrySet();
            if (entries != null) {
                Iterator model_iter = entries.iterator();
                while (model_iter.hasNext()) {
                    Map.Entry entry = (Map.Entry)model_iter.next();
                    Integer model_index = (Integer)entry.getValue();
                    if (model_index.intValue() >= insertindex) {
                        new_model_index.put(entry.getKey(), new Integer(model_index.intValue() + 1));
                    } else {
                        new_model_index.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            new_model_index.put(new Long(addedsong.uniquesongid), new Integer(insertindex));
            HashMap old_index = SearchPane.instance.searchmodel_index;
            SearchPane.instance.searchmodel_index = new_model_index;
            old_index.clear();
            if (dstm.row_model_to_view.size() != 0) {
                dstm.recomputeModelToViewMap();
            }
            
            SearchPane.instance.searchtable.getSelectionModel().removeSelectionInterval(insertindex, insertindex);
            for (int i = 0; i < selindices.length; ++i) {
              if (selindices[i] >= insertindex) {
                if (i >= 1) {
                  if (selindices[i - 1] != selindices[i] - 1) SearchPane.instance.searchtable.getSelectionModel().removeSelectionInterval(selindices[i], selindices[i]);
                } else SearchPane.instance.searchtable.getSelectionModel().removeSelectionInterval(selindices[i], selindices[i]);
                SearchPane.instance.searchtable.getSelectionModel().addSelectionInterval(selindices[i] + 1, selindices[i] + 1);
              }
            }
            
            if (log.isTraceEnabled()) log.trace("AddSingleSong(): updated tables");
            
            
//          UpdateSongStyleRoutine(addedsong);
            SongDB.instance.UpdateSongStyleRoutine(addedsong);
//            new UpdateSongStyleThread(addedsong).start();
            Artist.InsertToArtistList(addedsong);
            OptionsUI.instance.filter1.updatedSong(addedsong, false);
            OptionsUI.instance.filter2.updatedSong(addedsong, false);
            OptionsUI.instance.filter3.updatedSong(addedsong, false);        
      } catch (Exception e) {
          log.error("run(): error Exception", e);
      }
    }

}
