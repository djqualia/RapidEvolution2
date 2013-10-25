package rapid_evolution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import rapid_evolution.comparables.MyString;
import rapid_evolution.comparables.MyStringFloat;
import rapid_evolution.comparables.MyUserObject;
import rapid_evolution.comparables.myBpmObject;
import rapid_evolution.comparables.myImageIcon;
import rapid_evolution.comparables.myLength;
import rapid_evolution.comparables.myLengthRange;
import rapid_evolution.ui.ColumnConfig;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.REList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.util.KeyRecord;
import rapid_evolution.util.ListUtil;
import rapid_evolution.util.OSHelper;

import com.mixshare.rapid_evolution.music.KeyRelation;
import com.mixshare.rapid_evolution.music.SongKey;
import com.mixshare.rapid_evolution.music.SongKeyCode;

public class Filter implements ListSelectionListener {
    
    private static Logger log = Logger.getLogger(Filter.class);
        
    public REList list;
    public int column_id;
    public HashMap keys = new HashMap();  
    public HashMap songs = new HashMap();
    public Filter parent = null;
    private Filter child = null;
    private JButton clearbutton;
    private JButton selectallbutton;
    private JCheckBox disabled;
    int orig_fixed_cell_height = -1;
    int orig_fixed_cell_width = -1;
    
    public Filter(REList list, String data_type, Filter parent, JButton clearbutton, JButton selectallbutton, JCheckBox disabled) {
        this.list = list;
        removeSelectionListeners(); 
        orig_fixed_cell_height = list.getFixedCellHeight();
        orig_fixed_cell_width = list.getFixedCellWidth();        
        if (data_type.equalsIgnoreCase(SkinManager.instance.getMessageText("column_title_album_cover"))) {
            column_id = -2;
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setIconMode(true);
            list.setFixedCellHeight(-1);
            list.setFixedCellWidth(-1);            
        } else {
            column_id = ColumnConfig.getIndex(data_type);
            list.setLayoutOrientation(JList.VERTICAL);
            list.setIconMode(false);
            list.setFixedCellHeight(orig_fixed_cell_height);
            list.setFixedCellWidth(orig_fixed_cell_width);
        }
        list.setVisibleRowCount(-1);
        this.parent = parent;
        this.clearbutton = clearbutton;
        this.selectallbutton = selectallbutton;
        this.disabled = disabled;
        populateList();
        addSelectionListener();        
    }       
    
    public boolean isDisabled() {
        if (disabled == null) return false;
        if (!OptionsUI.instance.enablefilter.isSelected()) return false;
        return disabled.isSelected();
    }
    
    public void changeFilterDataType(String data_type) {
        if (!OptionsUI.instance.enablefilter.isSelected()) return;
        if (data_type.equalsIgnoreCase(SkinManager.instance.getMessageText("column_title_album_cover"))) {
            column_id = -2;
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setIconMode(true);
            list.setFixedCellHeight(-1);
            list.setFixedCellWidth(-1);
        } else {
            column_id = ColumnConfig.getIndex(data_type);
            list.setLayoutOrientation(JList.VERTICAL);
            list.setIconMode(false);
            list.setFixedCellHeight(orig_fixed_cell_height);
            list.setFixedCellWidth(orig_fixed_cell_width);;
        }
        populateList();
    }
    
    public void setChild(Filter filter) { child = filter; }
    public void setParent(Filter filter) { parent = filter; }
    
    public boolean containsSong(SongLinkedList song) {
        return songs.containsKey(song);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        valueChanged(e, false);
    }
    public void valueChanged(ListSelectionEvent e, boolean selectall) {
        if ((e != null) && e.getValueIsAdjusting()) return; 
        log.debug("valueChanged(): starting");
        if (!OptionsUI.instance.enablefilter.isSelected()) return;
        boolean changed = computeSongMapAndChange();        
        if (changed && (child != null))
            child.updateList(true);
        if (list.getSelectedIndex() == -1) {
            clearbutton.setEnabled(false);
        } else {
            clearbutton.setEnabled(true);                
        }
        DefaultListModel dlm = (DefaultListModel) list.getModel();
        if (list.getSelectedIndices().length == dlm.getSize()) {
            selectallbutton.setEnabled(false);            
        } else {
            selectallbutton.setEnabled(true);            
        }
        if (OptionsUI.instance.filterautosearch.isSelected() && ((e != null) || selectall)) {
            boolean search = false;
            if (!OptionsUI.instance.filter2disable.isSelected() &&
                    !OptionsUI.instance.filter3disable.isSelected()) {
                if (OptionsUI.instance.filter3 == this)
                    search = true;
            } else if (!OptionsUI.instance.filter2disable.isSelected()) {
                if (OptionsUI.instance.filter2 == this)
                    search = true;
            } else {
                if (OptionsUI.instance.filter1 == this)
                    search = true;
            }
            if (search) {
                RapidEvolutionUI.instance.SearchRoutine(true);
            }
        }
        log.debug("valueChanged(): finished");
    }    

    public boolean allSelected() {
        DefaultListModel dlm = (DefaultListModel) list.getModel();
        if (list.getSelectedIndices().length == dlm.getSize()) {
            return true;            
        } else {
            return false;            
        }
    }
    
    public void changeCurrentSong() {
        if (!OptionsUI.instance.enablefilter.isSelected()) return;
        if (column_id == 26) { // key type
            updateList();
        } else if (column_id == 18) { // bpm shift
            updateList();
        } else if (column_id == 25) { // bpm diff
            updateList();
        }
    }
    
    public void addStyle(StyleLinkedList style) {
        if (!OptionsUI.instance.enablefilter.isSelected()) return;
        if (column_id != 43) return;
        if (parent == null) {
            DefaultListModel dlm = (DefaultListModel) list.getModel();
            populateKeyFromStyle(style, null);
            Collection coll = keys.entrySet();
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
    }
    public void removeStyle(StyleLinkedList style) {
        if (!OptionsUI.instance.enablefilter.isSelected()) return;
        if (column_id != 43) return;
        DefaultListModel dlm = (DefaultListModel) list.getModel();
        boolean done = false;
        int i = 0;
        while ((i < dlm.size()) && !done) {
            Object key2 = dlm.getElementAt(i);
            if (key2.equals(style)) {
                dlm.removeElementAt(i);
                done = true;
            }
            ++i;
        }    
        keys.remove(style);
        valueChanged(null);
    }
    
    public void changedIconSize() {
        if (isDisabled()) return;
        if (column_id == -2) {
            populateList();
        }
    }
    
    public void changedSkins() {
        if (isDisabled()) return;
        if (column_id == -2) {
            if (column_id == -2) {
                list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                list.setIconMode(true);
                list.setFixedCellWidth(-1);
                list.setFixedCellHeight(-1);
            } else {
                list.setLayoutOrientation(JList.VERTICAL);
                list.setIconMode(false);
                list.setFixedCellWidth(orig_fixed_cell_width);;
                list.setFixedCellHeight(orig_fixed_cell_height);
            }
        }        
    }
    
    public void renameStyle(StyleLinkedList style) {
        if (!OptionsUI.instance.enablefilter.isSelected()) return;        
        if (column_id != 43) return;
        DefaultListModel dlm = (DefaultListModel) list.getModel();
        boolean done = false;
        int i = 0;
        while ((i < dlm.size()) && !done) {
            Object key2 = dlm.getElementAt(i);
            if (key2.equals(style)) {
                dlm.setElementAt(new MyString(style.getName()), i);
                done = true;
            }
            ++i;
        }    
    }
    
    public void updatedSong(SongLinkedList song, boolean removed) {
        javax.swing.SwingUtilities.invokeLater(new FilterSongUIUpdateThread(this, song, removed));
    }
    
    // should be private, made public for FilterUIUpdateThread
    public void populateList() {
        if (!OptionsUI.instance.enablefilter.isSelected()) return;
        if (isDisabled()) return;
        log.debug("populateList(): starting");
        keys.clear();
        Filter actual_parent = parent;
        while ((actual_parent != null) && actual_parent.allSelected()) {
            actual_parent = actual_parent.parent;
        }
        if (actual_parent == null) {
            if (OptionsUI.instance.filterusestyles.isSelected()) {
                if (column_id == 43) {
                    // styles
                    StyleLinkedList iter = SongDB.instance.masterstylelist;
                    while (iter != null) {
                        if (StylesPane.instance.styletree.isALogicalSelectedStyle(iter)) {
                            populateKeyFromStyle(iter, null);
                        }
                        iter = iter.next;
                    }
                } else {
                    SongLinkedList iter = SongDB.instance.SongLL;
                    while (iter != null) {
                        if (StylesPane.instance.isMemberOfCurrentStyle(iter)) {
                            if (!OptionsUI.instance.filteraffectedbysearch.isSelected() || RapidEvolutionUI.instance.passesSearchTest(iter, false)) {
                                populateKeyFromSong(iter);
                            }
                        }
                        iter = iter.next;
                    }
                }
            } else {
                if (column_id == 43) {
                    // styles
                    StyleLinkedList iter = SongDB.instance.masterstylelist;
                    while (iter != null) {
                        populateKeyFromStyle(iter, null);
                        iter = iter.next;
                    }
                } else {
                    SongLinkedList iter = SongDB.instance.SongLL;
                    while (iter != null) {
                        if (!OptionsUI.instance.filteraffectedbysearch.isSelected() || RapidEvolutionUI.instance.passesSearchTest(iter, true)) {                        
                            populateKeyFromSong(iter);
                        }
                        iter = iter.next;
                    }
                }                
            }
        } else {
            Collection keyset = actual_parent.songs.keySet();
            if (keyset != null) {
                Iterator s_iter = keyset.iterator();
                while (s_iter.hasNext()) {
                    SongLinkedList iter = (SongLinkedList)s_iter.next();
                    if (column_id == 43) {
                        StyleLinkedList style = SongDB.instance.masterstylelist;
                        while (style != null) {
                            if (style.containsLogical(iter))
                                populateKeyFromStyle(style, iter);
                            style = style.next;
                        }                                                
                    } else {
                        populateKeyFromSong(iter);
                    }
                }
            }
        }
        DefaultListModel dlm = (DefaultListModel) list.getModel();
        dlm.removeAllElements();
        Collection coll = keys.entrySet();
        if (coll != null) {
            Iterator key_iter = coll.iterator();
            while (key_iter.hasNext()) {
                Map.Entry entry = (Map.Entry)key_iter.next();
                KeyRecord record = (KeyRecord)entry.getValue();
                Comparable key = null;
                if (record.key instanceof JLabel) {
                    JLabel label = (JLabel)record.key;
                    key = (Comparable)label.getIcon();
                } else {
                    key = (Comparable)record.key;
                }
                int index = ListUtil.sortedInsert(dlm, key);
                if (log.isTraceEnabled()) log.trace("populateList(): sorted insert, key=" + key + ", index=" + index);
            }
        } 
        if (column_id == -2) {
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setFixedCellHeight(-1);
            list.setFixedCellWidth(-1);

            //System.out.println("visible rows: " + list.getVisibleRowCount());
        } else {
            list.setLayoutOrientation(JList.VERTICAL);
            list.setFixedCellWidth(orig_fixed_cell_width);;
            list.setFixedCellHeight(orig_fixed_cell_height);
        }
        log.debug("populateList(): finished");
    }
    
    public void updateList() {
        updateList(false);
    }
    
    public void updateList(boolean selectionChanged) {
        javax.swing.SwingUtilities.invokeLater(new FilterUIUpdateThread(this, selectionChanged));        
    }
        
    public void populateKeyFromSong(SongLinkedList iter) {
        Object key = getKey(iter);
        if (log.isTraceEnabled())
            log.trace("populateKeyFromSong(): key=" + key + ", hashcode=" + ((key != null) ? key.hashCode() : null) + ", song=" + iter);
        if (key != null) {
            KeyRecord record = (KeyRecord)keys.get(key);
            if (record != null) {
                record.songs.put(iter, null);                
                if (log.isTraceEnabled())
                    log.trace("populateKeyFromSong(): key record already exists, # songs=" + record.songs.size());
            } else {
                record = new KeyRecord(key);
                record.songs.put(iter, null);
                keys.put(key, record);
                if (log.isTraceEnabled())
                    log.trace("populateKeyFromSong(): new key set created");
            }
        }
    }
    
    public void populateKeyFromStyle(StyleLinkedList iter, SongLinkedList song) {        
        Object key = iter;
        if (keys.containsKey(key)) {
            KeyRecord record = (KeyRecord)keys.get(key);
            addSongsToRecord(iter, record, song);                                     
        } else {
            KeyRecord record = new KeyRecord(key);
            addSongsToRecord(iter, record, song);
            keys.put(key, record);            
        }
    }
    
    private void addSongsToRecord(StyleLinkedList style, KeyRecord record, SongLinkedList song) {
        if (song == null) {
            SongLinkedList iter = SongDB.instance.SongLL;
            while (iter != null) {
                if (style.containsLogical(iter)) {
                    record.songs.put(iter, null);
                }
                iter = iter.next;
            }
        } else {
            if (style.containsLogical(song)) {
                record.songs.put(song, null);
            }
        }
    }
    
    private Object getKey(SongLinkedList song) {
        Object data = null;
        if (column_id == -2) { // album cover
            ImageSet imageset = SongDB.instance.getAlbumCoverImageSet(song);            
            if (log.isTraceEnabled())
                log.trace("getKey(): album cover for song=" + song + ", is=" + imageset);
            String artist = song.isCompilationAlbum() ? "various" : song.getArtist();
            if (imageset != null) {
                data = ImageIconFactory.getImageIcon(imageset.getThumbnailFilename(), song.getAlbum(), myImageIcon.formAlbumCoverToolTip(artist, song.getAlbum()));
            } else {
                data = new myImageIcon("albumcovers/noalbumcover.gif", song.getAlbum(), myImageIcon.formAlbumCoverToolTip(artist, song.getAlbum()));
            }
        } else {
            data = song.get_column_data(column_id);
        }        
        // filter down (mod) if necessary...
        if (column_id == 9) {
            // bpm
            myBpmObject mybpm = (myBpmObject)data;
            float bpm = mybpm.startbpm;
            if (bpm == 0.0f) bpm = mybpm.endbpm;
            if (bpm != 0.0f) {
                int maxbpm = 0;
                while (maxbpm <= bpm) {
                    maxbpm += 5;
                }
                int minbpm = maxbpm - 5;
                return new myBpmObject(minbpm, maxbpm);
            } else {
                return new myBpmObject();
            }
        } else if (column_id == 5) {
            // time            
            myLength mylength = (myLength)data;
            if (mylength.seconds == 0)
                return new myLengthRange();
            int max_length = 0;
            while (max_length <= mylength.seconds) {
                max_length += 30;
            }
            int min_length = max_length - 30;
            return new myLengthRange(min_length, max_length);
        } else if (column_id == 18) { // bpm shift
            MyStringFloat myfloat = (MyStringFloat)data;
            if (myfloat.isNaN()) return new myBpmObject();
            float min = (float)(Math.floor(myfloat.getFloatValue()) - 0.5);
            while (min + 1.0f < myfloat.getFloatValue()) min += 1.0f;
            float max = min + 1.0f;
            return new myBpmObject(min, max);
        } else if (column_id == 25) { // bpm diff
            MyStringFloat myfloat = (MyStringFloat)data;
            if (myfloat.isNaN()) return new myBpmObject();
            float min = (float)(Math.floor(myfloat.getFloatValue()) - 0.5);
            while (min + 1.0f < myfloat.getFloatValue()) min += 1.0f;
            float max = min + 1.0f;
            return new myBpmObject(min, max);
        } else if (column_id == ColumnConfig.COLUMN_KEY) {
            SongKey songKey = (SongKey)data;
            return new FilterMusicKey(songKey);
        } else if (column_id == ColumnConfig.COLUMN_KEYCODE) {
            SongKeyCode songKeyCode = (SongKeyCode)data;
            return new FilterMusicKeyCode(songKeyCode);
        } else if (column_id == ColumnConfig.COLUMN_KEYRELATION) {
            KeyRelation keyRelation = (KeyRelation)data;
            return new FilterKeyRelation(keyRelation);
        }
        if ((data != null) && (data.toString().equals(""))) {
            if (data instanceof MyString)
                data = new MyString(noValueString);
            if (data instanceof MyUserObject)
                data = new FilterUserObject((MyUserObject)data);
        }
        return data;
    }
    
    static public String noValueString = "<<no value>>";
        
    // returns true if song list changes
    public boolean computeSongMapAndChange() {
        try {
            log.debug("computeSongMapAndChange(): starting");
            int pre_size = songs.size();
            songs.clear();
            DefaultListModel dlm = (DefaultListModel) list.getModel();
            int[] indices = list.getSelectedIndices();
            for (int i = 0; i < indices.length; ++i) {
                Object value = dlm.getElementAt(indices[i]);
                KeyRecord record = (KeyRecord)keys.get(value);
                Collection coll = record.songs.entrySet();
                if (coll != null) {
                    Iterator song_iter = coll.iterator();
                    while (song_iter.hasNext()) {
                        Map.Entry entry = (Map.Entry)song_iter.next();
                        SongLinkedList song = (SongLinkedList)entry.getKey();
                        if (!songs.containsKey(song))
                            songs.put(song, null);
                    }
                }            
            }
            int post_size = songs.size();            
            log.debug("computeSongMapAndChange(): finished");
            if (OptionsUI.instance.filtersingleselectmode.isSelected()) return true;
            return (!(pre_size == post_size)); // this works because changes are incremental when single select mode is disabled...
        } catch (Exception e) {
            log.error("computeSongMapAndChange(): error", e);
        }
        return false;
    }

    public void removeSelectionListeners() {
        ListSelectionListener[] listeners = list.getListSelectionListeners();
        for (int i = 0; i < listeners.length; ++i) {
            list.removeListSelectionListener(listeners[i]);
        }        
    }
    
    public void addSelectionListener() {
        list.addListSelectionListener(this);
    }
}
