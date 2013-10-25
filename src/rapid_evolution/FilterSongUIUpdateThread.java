package rapid_evolution;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.styles.StylesUI;

import org.apache.log4j.Logger;

import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.util.ListUtil;
import rapid_evolution.util.*;

public class FilterSongUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(FilterSongUIUpdateThread.class);
    
    private Filter filter;
    private SongLinkedList song;
    private boolean removed;
    public FilterSongUIUpdateThread(Filter filter, SongLinkedList song, boolean removed) {
        this.song = song;
        this.removed = removed;
        this.filter = filter;
    }
    
    public void run() {
        try {
            if (!OptionsUI.instance.enablefilter.isSelected()) return;
            if (filter.isDisabled()) return;
            filter.removeSelectionListeners();
            int[] indices = filter.list.getSelectedIndices();
            Vector selected_keys = new Vector();
            DefaultListModel dlm = (DefaultListModel) filter.list.getModel();        
            for (int i = 0; i < indices.length; ++i) {
                Object value = dlm.getElementAt(indices[i]);
                selected_keys.add(value);
            }           
            Vector removed_keys = new Vector();
            Collection coll = filter.keys.entrySet();        
            if (coll != null) {
                Iterator key_iter = coll.iterator();
                while (key_iter.hasNext()) {
                    Map.Entry entry = (Map.Entry)key_iter.next();
                    KeyRecord record = (KeyRecord)entry.getValue();
                    if (record.songs.containsKey(song)) {
                        record.songs.remove(song);                
                        if (record.songs.size() == 0) {
                            removed_keys.add(record.key);                    
                            boolean done = false;
                            int i = 0;
                            while ((i < dlm.size()) && !done) {
                                Object key2 = dlm.getElementAt(i);
                                if (record.key.equals(key2)) {
                                    dlm.removeElementAt(i);
                                    done = true;
                                }
                                ++i;
                            }                        
                        }
                    }
                }
            }
            for (int i = 0; i < removed_keys.size(); ++i) {
                filter.keys.remove(removed_keys.get(i));
            }
            if (!removed) {
            Filter actual_parent = filter.parent;
            while ((actual_parent != null) && actual_parent.allSelected()) {
                actual_parent = actual_parent.parent;
            }            
            if (actual_parent == null) {
                if (OptionsUI.instance.filterusestyles.isSelected()) {            
                    if (filter.column_id == 43) {
                        // styles
                    } else {
                        if (StylesPane.instance.isMemberOfCurrentStyle(song)) {
                            if (!OptionsUI.instance.filteraffectedbysearch.isSelected() || RapidEvolutionUI.instance.passesSearchTest(song, false)) {
                                filter.populateKeyFromSong(song);
                            }
                        }
                    }
                } else {
                    if (filter.column_id == 43) {
                        // styles
                    } else {
                        if (!OptionsUI.instance.filteraffectedbysearch.isSelected() || RapidEvolutionUI.instance.passesSearchTest(song, true)) {
                            filter.populateKeyFromSong(song);
                        }
                    }                
                }
            } else {
                Collection keyset = actual_parent.songs.keySet();
                if (keyset != null) {
                    Iterator s_iter = keyset.iterator();
                    while (s_iter.hasNext()) {
                        SongLinkedList iter = (SongLinkedList)s_iter.next();
                        if (iter == song) {
                            if (filter.column_id == 43) {
                                StyleLinkedList style = SongDB.instance.masterstylelist;
                                while (style != null) {
                                    if (style.containsLogical(iter))
                                        filter.populateKeyFromStyle(style, iter);
                                    style = style.next;
                                }                                                
                            } else {                            
                                filter.populateKeyFromSong(iter);
                            }
                        }
                    }
                }
            }
            coll = filter.keys.entrySet();
            if (coll != null) {
                Iterator key_iter = coll.iterator();
                while (key_iter.hasNext()) {
                    Map.Entry entry = (Map.Entry)key_iter.next();
                    KeyRecord record = (KeyRecord)entry.getValue();
                    Comparable key = (Comparable)record.key;
                    int index = ListUtil.sortedInsert(dlm, key);
                }
            }             
            }
            
            Vector selected_indices = new Vector();
            for (int s = 0; s < selected_keys.size(); ++s) {
                Object key = selected_keys.get(s);
                if (filter.keys.containsKey(key)) {
                    int i = 0;
                    boolean done = false;
                    while ((i < dlm.size()) && !done) {
                        Object key2 = dlm.getElementAt(i);
                        if (key.equals(key2)) {
                            done = true;
                            selected_indices.add(new Integer(i));                    
                        }
                        ++i;
                    }
                }
            }
            int[] selected = new int[selected_indices.size()];
            for (int s = 0; s < selected.length; ++s) {
                selected[s] = ((Integer)selected_indices.get(s)).intValue();
            }
            filter.list.setSelectedIndices(selected);
            //if (indices.length != selected.length) {
            filter.valueChanged(null);
            //}
            filter.addSelectionListener();        

        } catch (Exception e) {
            log.error("run(): error Exception", e);
        }
    }

}
