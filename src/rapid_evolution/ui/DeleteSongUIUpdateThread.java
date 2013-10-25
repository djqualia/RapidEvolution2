package rapid_evolution.ui;

import org.apache.log4j.Logger;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.MixoutPane;
import java.util.HashMap;
import java.util.Iterator;

public class DeleteSongUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(DeleteSongUIUpdateThread.class);
    
    private SongLinkedList song;
    public DeleteSongUIUpdateThread(SongLinkedList song) {
        this.song = song;
    }
    
    public void run() {
        try {
            DefaultSortTableModel dstm = (DefaultSortTableModel) SearchPane.instance.searchtable.getModel();
            Integer searchindex = (Integer)SearchPane.instance.searchmodel_index.get(new Long(song.uniquesongid));
            if (searchindex != null) {
                int remove_index = SearchPane.instance.getSearchViewIndex(song);
                log.debug("DeleteSong(): removing searchtable row: " + remove_index);
                dstm.removeRow(remove_index);
                java.util.Set modelindices = SearchPane.instance.searchmodel_index.entrySet();
                HashMap new_searchmodel_index = new HashMap();
                if (modelindices != null) {
                    Iterator iter = modelindices.iterator();
                    while (iter.hasNext()) {
                        java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
                        Integer index = (Integer)entry.getValue();
                        if (index.intValue() > searchindex.intValue()) {
                            new_searchmodel_index.put(entry.getKey(), new Integer(index.intValue() - 1));
                        } else new_searchmodel_index.put(entry.getKey(), entry.getValue());                  
                    }
                }
                HashMap oldmap = SearchPane.instance.searchmodel_index;
                SearchPane.instance.searchmodel_index = new_searchmodel_index;
                oldmap.clear();          
                dstm.recomputeModelToViewMap();
            }      

            
            dstm = (DefaultSortTableModel) MixoutPane.instance.mixouttable.getModel();
            for (int z = 0; z < MixoutPane.instance.mixouttable.getRowCount(); ++z) {
              SongLinkedList s = (SongLinkedList)MixoutPane.instance.mixouttable.getValueAt(z, MixoutPane.instance.mixoutcolumnconfig.num_columns);
              if (s == song) {
                 dstm.removeRow(z);
                 --z;
              }
            }
            dstm = (DefaultSortTableModel) ExcludeUI.instance.excludetable.getModel();
            for (int z = 0; z < ExcludeUI.instance.excludetable.getRowCount(); ++z) {
              SongLinkedList s = (SongLinkedList)ExcludeUI.instance.excludetable.getValueAt(z, ExcludeUI.instance.excludecolumnconfig.num_columns);
              if (s == song) {
                 dstm.removeRow(z);
                 --z;
              }
            }
            dstm = (DefaultSortTableModel) SuggestedMixesUI.instance.suggestedtable.getModel();
            for (int z = 0; z < SuggestedMixesUI.instance.suggestedtable.getRowCount(); ++z) {
              SongLinkedList s = (SongLinkedList)SuggestedMixesUI.instance.suggestedtable.getValueAt(z, SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
              if (s == song) {
                 dstm.removeRow(z);
                 --z;
              }
            }
            dstm = (DefaultSortTableModel) RootsUI.instance.rootstable.getModel();
            for (int z = 0; z < RootsUI.instance.rootstable.getRowCount(); ++z) {
              SongLinkedList s = (SongLinkedList)RootsUI.instance.rootstable.getValueAt(z, RootsUI.instance.rootscolumnconfig.num_columns);
              if (s == song) {
                 dstm.removeRow(z);
                 --z;
              }
            }
            dstm = (DefaultSortTableModel) SyncUI.instance.synctable.getModel();
            for (int z = 0; z < SyncUI.instance.synctable.getRowCount(); ++z) {
              SongLinkedList s = (SongLinkedList)SyncUI.instance.synctable.getValueAt(z, SyncUI.instance.synccolumnconfig.num_columns);
              if (s == song) {
                 dstm.removeRow(z);
                 --z;
              }
            }          
            
            OptionsUI.instance.filter1.updatedSong(song, true);
            OptionsUI.instance.filter2.updatedSong(song, true);
            OptionsUI.instance.filter3.updatedSong(song, true);
            
        } catch (Exception e) {
            log.error("run(): error Exception", e);
        }
    }

}
