package rapid_evolution;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.styles.*;
import rapid_evolution.SongDB;
import java.util.Vector;
import javax.swing.DefaultListModel;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.io.*;
import java.awt.datatransfer.*;
import java.util.*;

import com.mixshare.rapid_evolution.data.index.IndexItem;
import rapid_evolution.ui.RETree;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class StyleLinkedList implements Transferable, Comparable, IndexItem {

    private static Logger log = Logger.getLogger(StyleLinkedList.class);
    
  // PUBLIC:

    private static boolean debug = false;
    
    private static int next_unique_style_id = -1;
    private int styleid; //invariant        
    public static synchronized int getNextStyleId() {
        int return_val = ++next_unique_style_id;
        return return_val;
    }
    
    private String description = "";
    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (!description.equals(this.description)) {
            if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;            
            this.description = description;
        }
    }

    private boolean categoryOnly = false;
    public boolean isCategoryOnly() { return categoryOnly; }
    public void setCategoryOnly(boolean categoryOnly) {
        if (categoryOnly != this.categoryOnly) {
            if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;
            this.categoryOnly = categoryOnly;
        }
    }
    
    public int compareTo(Object o) {
        if (o instanceof StyleLinkedList) {
            StyleLinkedList s2 = (StyleLinkedList)o;
            int compare = stylename.compareToIgnoreCase(s2.getName());
            if (compare != 0) return compare;
            if (getStyleId() < s2.getStyleId()) return -1;
            else if (getStyleId() > s2.getStyleId()) return 1;
            else return 0;
        }
        return -1;
    }
    

    public StyleLinkedList(String stylename, StyleLinkedList nextptr, int styleid) {
    this.stylename = stylename;
    keywords = new HashMap();
    songs = new HashMap();
    excludekeywords = new HashMap();
    excludesongs = new HashMap();
    next = nextptr;
    set_sindex(-1);
    this.styleid = styleid;
    next_unique_style_id = Math.max(styleid, next_unique_style_id);
    styles.put(new Integer(styleid), this);
  }
    
    // this constructor is only used temporarily for sorting styles, whichshould be written and this can be removed
  public StyleLinkedList(StyleLinkedList rhs, StyleLinkedList nextptr) {
    this.stylename = rhs.stylename;
    keywords = (HashMap)rhs.keywords.clone();
    songs = (HashMap)rhs.songs.clone();
    excludekeywords = (HashMap)rhs.excludekeywords.clone();
    excludesongs = (HashMap)rhs.excludesongs.clone();
    styleid = rhs.styleid;
    for (int i = 0; i < rhs.styleincludedisplayvector.size(); ++i) {
      styleincludedisplayvector.add(rhs.styleincludedisplayvector.get(i));
      styleincludevector.add(rhs.styleincludevector.get(i));
    }
    for (int i = 0; i < rhs.styleexcludedisplayvector.size(); ++i) {
      styleexcludedisplayvector.add(rhs.styleexcludedisplayvector.get(i));
      styleexcludevector.add(rhs.styleexcludevector.get(i));
    }
    set_sindex(rhs._sindex);
    next = nextptr;
  }

  private static HashMap styles = new HashMap();
  public static StyleLinkedList getStyle(int styleid) {
      if (styleid == -1) return root_style;
      return (StyleLinkedList)styles.get(new Integer(styleid));
  }
  
  private String stylename;
  public String getName() { return stylename; }
  public void setName(String name) { stylename = name; }
  public String toString() { return getName(); }
  public StyleLinkedList next;
  
  public static StyleLinkedList root_style = new StyleLinkedList("root", null, -1);
  public HashMap child_style_ids = new HashMap();
  public HashMap parent_style_ids = new HashMap();
  public void removeParentStyle(StyleLinkedList style) { parent_style_ids.remove(new Integer(style.getStyleId())); }
  public void removeChildStyle(StyleLinkedList style) { child_style_ids.remove(new Integer(style.getStyleId())); }
  public void addParentStyle(StyleLinkedList style) { parent_style_ids.put(new Integer(style.getStyleId()), null); }
  public void addParentStyle(int styleid) { parent_style_ids.put(new Integer(styleid), null); }
  public void addChildStyle(StyleLinkedList style) { child_style_ids.put(new Integer(style.getStyleId()), null); }  
  public void addChildStyle(int styleid) { child_style_ids.put(new Integer(styleid), null); }  
  public String getParentIdsString() {
      StringBuffer result = new StringBuffer();
      Set keys = parent_style_ids.keySet();
      if (keys != null) {
          Iterator iter = keys.iterator();
          while (iter.hasNext()) {
              int styleid = ((Integer)iter.next()).intValue();
              result.append(styleid);
              if (iter.hasNext()) result.append(",");
          }
      }
      return result.toString();
  }
  public String getChildIdsString() {
      StringBuffer result = new StringBuffer();
      Set keys = child_style_ids.keySet();
      if (keys != null) {
          Iterator iter = keys.iterator();
          while (iter.hasNext()) {
              int styleid = ((Integer)iter.next()).intValue();
              result.append(styleid);
              if (iter.hasNext()) result.append(",");
          }
      }
      return result.toString();
  }  
  public boolean isRootStyle() {
      return parent_style_ids.containsKey(new Integer(-1));
  }
  public StyleLinkedList[] getChildStyles() {
      StyleLinkedList[] result = new StyleLinkedList[child_style_ids.size()];
      int count = 0;
      Set keys = child_style_ids.keySet();
      if (keys != null) {
          Iterator iter = keys.iterator();
          while (iter.hasNext()) {
              int styleid = ((Integer)iter.next()).intValue();
              result[count++] = getStyle(styleid);
          }
      }
      return result;
  }
  
  public void countParentStyles(HashMap style_map) {
      StyleLinkedList[] parents = getParentStyles();
      for (int p = 0; p < parents.length; ++p) {
          StyleLinkedList style = parents[p];
    	    Integer count = (Integer)style_map.get(style);
      	    if (count != null) {
      	        count = new Integer(count.intValue() + 1);
      	        style_map.put(style, count);
      	    } else {
      	        style_map.put(style, new Integer(1));
      	    }
          parents[p].countParentStyles(style_map);
      }      
  }
  public StyleLinkedList[] getParentStyles() {
      StyleLinkedList[] result = new StyleLinkedList[parent_style_ids.size()];
      int count = 0;
      Set keys = parent_style_ids.keySet();
      if (keys != null) {
          Iterator iter = keys.iterator();
          while (iter.hasNext()) {
              int styleid = ((Integer)iter.next()).intValue();
              result[count++] = getStyle(styleid);
          }
      }
      return result;     
  }
  
  public int getDistanceToRoot() {
      if (this.isRootStyle()) return 1;
      StyleLinkedList[] styles = getParentStyles();
      int min_distance = Integer.MAX_VALUE;
      for (int s = 0; s < styles.length; ++s) {
          int distance = 1 + styles[s].getDistanceToRoot();
          if (distance < min_distance) min_distance = distance;
      }
      return min_distance;
  }
  
  public int hashCode() { return styleid; }
  public boolean equals(Object o) {
      if (o instanceof StyleLinkedList) {
          return styleid == ((StyleLinkedList)o).styleid;
//          return name.equals(((StyleLinkedList)o).name);
          
      }
      return false;
  }
  public int getStyleId() { return styleid; }

  public int getTotalEntries() { return keywords.size() + songs.size(); }
  public int getTotalExcludeEntries() { return excludekeywords.size() + excludesongs.size(); }
  public int getNumKeywords() { return keywords.size(); }
  public int getNumSongs() { return songs.size(); }
  public int getNumExcludeKeywords() { return excludekeywords.size(); }
  public int getNumExcludeSongs() { return excludesongs.size(); }
  public void setKeywords(HashMap values) {
      keywords = values;
  }
  public void setSongs(HashMap values) {
      songs = values;
  }
  public void setExcludeKeywords(HashMap values) {
      excludekeywords = values;
  }
  public void setExcludeSongs(HashMap values) {
      excludesongs = values;
  }
  public boolean containsKeyword(String keyword) { return keywords.containsKey(keyword); }
  public boolean containsSong(long uniquesongid) { return songs.containsKey(new Long(uniquesongid)); }
  public boolean containsExcludeKeyword(String excludekeyword) { return excludekeywords.containsKey(excludekeyword); }
  public boolean containsExcludeSong(long uniquesongid) { return excludesongs.containsKey(new Long(uniquesongid)); }

  public long[] getSongs() {
      long[] result = new long[getNumSongs()];
      Set set = songs.keySet();
      if (set != null) {
          Iterator iter = set.iterator();
          int i = 0;
          while (iter.hasNext()) {
              Long songid = (Long)iter.next();
              result[i++] = songid.longValue();
          }
      }
      return result;       
  }

  public long[] getExcludeSongs() {
      long[] result = new long[getNumExcludeSongs()];
      Set set = excludesongs.keySet();
      if (set != null) {
          Iterator iter = set.iterator();
          int i = 0;
          while (iter.hasNext()) {
              Long songid = (Long)iter.next();
              result[i++] = songid.longValue();
          }
      }
      return result;       
  }
  
  public String[] getExcludeKeywords() {
      String[] result = new String[getNumExcludeKeywords()];
      Set set = excludekeywords.keySet();
      if (set != null) {
          Iterator iter = set.iterator();
          int i = 0;
          while (iter.hasNext()) {
              String keyword = (String)iter.next();
              result[i++] = keyword;
          }
      }
      return result; 
  }
  
  public String[] getKeywords() {
      String[] result = new String[getNumKeywords()];
      Set set = keywords.keySet();
      if (set != null) {
          Iterator iter = set.iterator();
          int i = 0;
          while (iter.hasNext()) {
              String keyword = (String)iter.next();
              result[i++] = keyword;
          }
      }
      return result; 
  }
  
  public HashMap keywords;
  public HashMap excludekeywords;
  public HashMap songs;
  public HashMap excludesongs;
  public Semaphore insertKeywordSem = new Semaphore(1);
  public Semaphore insertSongSem = new Semaphore(1);
  public Semaphore insertExcludeKeywordSem = new Semaphore(1);
  public Semaphore insertExcludeSongSem = new Semaphore(1);

  public int getUniqueId() {
      return get_sindex();
  }
  
  public String getUniqueStringId() {
      return getName().toLowerCase();
  }
  
  public int get_sindex() {
    int val = -1;
    try {
      SongDB.instance.StyleSIndexSem.acquire();
      val = _sindex;
    } catch (Exception e) { } finally {
    	SongDB.instance.StyleSIndexSem.release();
    }
    return val;
  }
  public void set_sindex(int val) {
    _sindex = val;
  }

  //check me!!!
  public boolean update(SongLinkedList song, int styleindex) {
    boolean wasmember = song.getStyle(styleindex);
    boolean ismember = containsA(song);
    song.setStyle(styleindex, ismember);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible() && ListStyleSongsUI.instance.isVisible()) {
      if (!wasmember && ismember) {
        ListStyleSongsUI.instance.InsertSongStyleList(song);
      } else if (wasmember && !ismember) {
        ListStyleSongsUI.instance.RemoveSongStyleList(song);
      } else if (ismember) {
        DefaultListModel dlm = (DefaultListModel) ListStyleSongsUI.instance.liststylesongslist.getModel();
        int i = 0;
        boolean found = false;
        while (!found && (i < ListStyleSongsUI.instance.stylesonglist.size())) {
          SongLinkedList iter = (SongLinkedList)ListStyleSongsUI.instance.stylesonglist.get(i);
          if (iter == song) {
            boolean selected = ListStyleSongsUI.instance.liststylesongslist.isSelectedIndex(i);
            dlm.removeElementAt(i);
            ListStyleSongsUI.instance.stylesonglist.removeElementAt(i);
            boolean inserted = false;
            int j = 0;
            while (!inserted && (j < ListStyleSongsUI.instance.stylesonglist.size())) {
              SongLinkedList iter2 = (SongLinkedList)ListStyleSongsUI.instance.stylesonglist.get(j);
              if (iter.getSongIdShort().compareToIgnoreCase(iter2.getSongIdShort()) < 0) {
                inserted = true;
                dlm.insertElementAt(iter.getSongIdShort(), j);
                ListStyleSongsUI.instance.stylesonglist.insertElementAt(iter, j);
                if (selected) ListStyleSongsUI.instance.AddToSelectedIndices(j);
              }
              ++j;
            }
            if (!inserted) {
              dlm.addElement(iter.getSongIdShort());
              ListStyleSongsUI.instance.stylesonglist.add(iter);
              if (selected) ListStyleSongsUI.instance.AddToSelectedIndices(j);
            }
            found = true;
          }
          ++i;
        }
      }
    }

    try {
      IncludeStyleListSem.acquire();
      int i = 0;
      boolean found = false;
      while (!found && (i < styleincludevector.size())) {
        try {
          long val = ((Long)styleincludevector.get(i)).longValue();
          SongLinkedList iter = SongDB.instance.NewGetSongPtr(val);
          if (song == iter) {
            boolean selected = EditStyleUI.instance.editstylekeywordslist.isSelectedIndex(i);
            if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
              DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.editstylekeywordslist.getModel();
              dlm.removeElementAt(i);
            }
            styleincludevector.removeElementAt(i);
            styleincludedisplayvector.removeElementAt(i);
            int j = 0;
            boolean inserted = false;
            String sortkeyword = song.getSongIdShort();
            while (!inserted && (j < styleincludevector.size())) {
              String cmpstr = ((String)styleincludedisplayvector.get(j));
              if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
              if (sortkeyword.compareToIgnoreCase(cmpstr) < 0) {
                inserted = true;
                if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
                  DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.editstylekeywordslist.getModel();
                  dlm.insertElementAt(song.getSongIdShort(), j);
                }
                styleincludevector.insertElementAt(new Long(song.uniquesongid), j);
                styleincludedisplayvector.insertElementAt(song.getSongIdShort(), j);
                if (selected) StylesUI.AddIncludeSelectedIndex(j);
              }
              ++j;
            }
            if (!inserted) {
              if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
                DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.editstylekeywordslist.getModel();
                dlm.addElement(song.getSongIdShort());
              }
              styleincludevector.add(new Long(song.uniquesongid));
              styleincludedisplayvector.add(song.getSongIdShort());
              if (selected) StylesUI.AddIncludeSelectedIndex(j);
            }
            found = true;
          }
        } catch (Exception e) { }
        ++i;
      }
    } catch (Exception e) { log.error("update(): error", e); }
    IncludeStyleListSem.release();
    try {
      ExcludeStyleListSem.acquire();
      int i = 0;
      boolean found = false;
      while (!found && (i < styleexcludevector.size())) {
        try {
          long val = ((Long)styleexcludevector.get(i)).longValue();
          SongLinkedList iter = SongDB.instance.NewGetSongPtr(val);
          if (song == iter) {
            boolean selected = EditStyleUI.instance.styleexcludelist.isSelectedIndex(i);
            if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
              DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.styleexcludelist.getModel();
              dlm.removeElementAt(i);
            }
            styleexcludevector.removeElementAt(i);
            styleexcludedisplayvector.removeElementAt(i);
            int j = 0;
            boolean inserted = false;
            String sortkeyword = song.getSongIdShort();
            while (!inserted && (j < styleexcludevector.size())) {
              String cmpstr = ((String)styleexcludedisplayvector.get(j));
              if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
              if (sortkeyword.compareToIgnoreCase(cmpstr) < 0) {
                inserted = true;
                if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
                  DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.styleexcludelist.getModel();
                  dlm.insertElementAt(song.getSongIdShort(), j);
                }
                styleexcludevector.insertElementAt(new Long(song.uniquesongid), j);
                styleexcludedisplayvector.insertElementAt(song.getSongIdShort(), j);
                if (selected) StylesUI.AddExcludeSelectedIndex(j);
              }
              ++j;
            }
            if (!inserted) {
              if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
                DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.styleexcludelist.getModel();
                dlm.addElement(song.getSongIdShort());
              }
              styleexcludevector.add(new Long(song.uniquesongid));
              styleexcludedisplayvector.add(song.getSongIdShort());
              if (selected) StylesUI.AddExcludeSelectedIndex(j);
            }
            found = true;
          }
        } catch (Exception e) { }
        ++i;
      }
    } catch (Exception e) { log.error("update(): error", e); }
    ExcludeStyleListSem.release();
    return ismember;
  }

  // insert song operations:
  //  a) check for pre-existing song entry, if so abort
  //  b) remove opposite include/exclude song, if any
  //  c) update song/excludesong array
  //  d) update song style cache
  //  e) set songdb dirty bit
  //  f) update user interface, if necessary (edit style dlg, list style songs dlg)
  public void insertSong(SongLinkedList song) {
    boolean alreadyinstyle = song.getStyle(get_sindex());
    if (songs.containsKey(new Long(song.uniquesongid))) return;
    if (excludesongs.containsKey(new Long(song.uniquesongid))) removeExcludeSong(song);
    try {
      insertSongSem.acquire();
      songs.put(new Long(song.uniquesongid), null);
      song.setStyle(get_sindex(), this.containsA(song));
      if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;
    } catch (Exception e) { log.error("insertSong(): error", e); }
    insertSongSem.release();
    InsertStyleUISong(song);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (song.getStyle(get_sindex()) && !alreadyinstyle) {
        if (ListStyleSongsUI.instance.isVisible()) ListStyleSongsUI.instance.InsertSongStyleList(song);
      }
    }
  }
  public void insertSong(long uniquesongid) {
    boolean alreadyinstyle = false;
    if (songs.containsKey(new Long(uniquesongid))) return;
    if (excludesongs.containsKey(new Long(uniquesongid))) removeExcludeSong(uniquesongid);
    SongLinkedList song = null;
    try {
      insertSongSem.acquire();
      songs.put(new Long(uniquesongid), null);
      if (RapidEvolution.instance.loaded) {
        song = SongDB.instance.NewGetSongPtr(uniquesongid);
        alreadyinstyle = song.getStyle(get_sindex());
        song.setStyle(get_sindex(), this.containsA(song));
        SongDB.instance.dirtybit = true;
      }
    } catch (Exception e) { log.error("insertSong(): error", e); }
    insertSongSem.release();
    InsertStyleUISong(song);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (song.getStyle(get_sindex()) && !alreadyinstyle) {
        if (ListStyleSongsUI.instance.isVisible()) ListStyleSongsUI.instance.InsertSongStyleList(song);
      }
    }
  }
  public void insertExcludeSong(SongLinkedList song) {
    boolean alreadyamember = song.getStyle(get_sindex());
    if (excludesongs.containsKey(new Long(song.uniquesongid))) return;
    if (songs.containsKey(new Long(song.uniquesongid))) removeSong(song);    
    try {
      insertExcludeSongSem.acquire();
      excludesongs.put(new Long(song.uniquesongid), null);
      song.setStyle(get_sindex(), false);
      if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;
    } catch (Exception e) { log.error("insertExcludeSong(): error", e); }
    insertExcludeSongSem.release();
    InsertStyleUIExcludeSong(song);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (ListStyleSongsUI.instance.isVisible() && alreadyamember) ListStyleSongsUI.instance.RemoveSongStyleList(song);
    }
  }
  public void insertExcludeSong(long uniquesongid) {
    boolean alreadyamember = false;
    if (excludesongs.containsKey(new Long(uniquesongid))) return;
    if (songs.containsKey(new Long(uniquesongid))) removeSong(uniquesongid);    
    SongLinkedList song = null;
    try {
      insertExcludeSongSem.acquire();
      excludesongs.put(new Long(uniquesongid), null);
      if (RapidEvolution.instance.loaded) {
        song = SongDB.instance.NewGetSongPtr(uniquesongid);
        alreadyamember = song.getStyle(get_sindex());
        song.setStyle(get_sindex(), false);
        SongDB.instance.dirtybit = true;
      }
    } catch (Exception e) { log.error("insertExcludeSong(): error", e); }
    insertExcludeSongSem.release();
    InsertStyleUIExcludeSong(song);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (ListStyleSongsUI.instance.isVisible() && alreadyamember) ListStyleSongsUI.instance.RemoveSongStyleList(song);
    }
  }

  // remove song operations:
  //  a) update song/excludesong array
  //  b) update song style cache
  //  c) set songdb dirty bit
  //  d) update user interface, if necessary (edit style dlg, list style songs dlg)
  public void removeSong(long uniqueid) {
    boolean alreadyinstyle = true;
    SongLinkedList song = null;
    try {
      song = SongDB.instance.NewGetSongPtr(uniqueid);
      insertSongSem.acquire();
      if (songs.containsKey(new Long(uniqueid))) {
          songs.remove(new Long(uniqueid));
        if (RapidEvolution.instance.loaded) {
          alreadyinstyle = song.getStyle(get_sindex());
          song.setStyle(get_sindex(), this.containsA(song));
          SongDB.instance.dirtybit = true;
        }
      }
    } catch (Exception e) { }
    insertSongSem.release();
    RemoveStyleUISong(uniqueid);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (ListStyleSongsUI.instance.isVisible() && !song.getStyle(get_sindex()) && alreadyinstyle) ListStyleSongsUI.instance.RemoveSongStyleList(uniqueid);
    }
  }
  public void removeSong(SongLinkedList song) {
    boolean alreadyinstyle = song.getStyle(get_sindex());
    try {
      insertSongSem.acquire();
      if (songs.containsKey(new Long(song.uniquesongid))) {
          songs.remove(new Long(song.uniquesongid));
          if (RapidEvolution.instance.loaded) {
          song.setStyle(get_sindex(), this.containsA(song));
          SongDB.instance.dirtybit = true;
        }
      }
    } catch (Exception e) { }
    insertSongSem.release();
    RemoveStyleUISong(song.uniquesongid);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (ListStyleSongsUI.instance.isVisible() && !song.getStyle(get_sindex()) && alreadyinstyle) ListStyleSongsUI.instance.RemoveSongStyleList(song.uniquesongid);
    }

  }
  public void removeExcludeSong(long uniqueid) {
    SongLinkedList song = null;
    try {
      song = SongDB.instance.NewGetSongPtr(uniqueid);
      insertExcludeSongSem.acquire();
      if (excludesongs.containsKey(new Long(uniqueid))) {
          excludesongs.remove(new Long(uniqueid));
        if (RapidEvolution.instance.loaded) {
          song.setStyle(get_sindex(), this.containsA(song));
          SongDB.instance.dirtybit = true;
        }
      }
    } catch (Exception e) { }
    insertExcludeSongSem.release();
    RemoveStyleUIExcludeSong(uniqueid);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (ListStyleSongsUI.instance.isVisible() && song.getStyle(get_sindex())) ListStyleSongsUI.instance.InsertSongStyleList(song);
    }
  }
  public void removeExcludeSong(SongLinkedList song) {
    try {
      insertExcludeSongSem.acquire();
      if (excludesongs.containsKey(new Long(song.uniquesongid))) {
          excludesongs.remove(new Long(song.uniquesongid));
        if (RapidEvolution.instance.loaded) {
          song.setStyle(get_sindex(), this.containsA(song));
          SongDB.instance.dirtybit = true;
        }
      }
    } catch (Exception e) { }
    insertExcludeSongSem.release();
    RemoveStyleUIExcludeSong(song.uniquesongid);
    if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
      if (ListStyleSongsUI.instance.isVisible() && song.getStyle(get_sindex())) ListStyleSongsUI.instance.InsertSongStyleList(song);
    }
  }

  // insert keyword operations:
  //  a) check for pre-existing keyword entry, if so abort
  //  b) remove opposite include/exclude keyword, if any
  //  c) update keyword/excludekeyword array
  //  d) spawn thread to update song membership
  //  e) set songdb dirty bit
  //  f) update user interface (edit style dlg)
  public void insertKeyword(String keyword) {
    if (keywords.containsKey(keyword)) return;
    if (excludekeywords.containsKey(keyword)) removeExcludeKeyword(keyword);
    try {
      insertKeywordSem.acquire();
      insertSort(keyword);
      if (RapidEvolution.instance.loaded) {
        SongDB.instance.dirtybit = true;
        new UpdateStyleAddKeywordThread(this, keyword).start();
      }
    } catch (Exception e) { log.error("insertKeyword(): error", e); }
    insertKeywordSem.release();
    InsertStyleUIKeyword(keyword);
  }
  public void insertExcludeKeyword(String keyword) {
    if (excludekeywords.containsKey(keyword)) return;
    if (keywords.containsKey(keyword)) removeKeyword(keyword);
    try {
      insertExcludeKeywordSem.acquire();
      insertSortExclude(keyword);
      if (RapidEvolution.instance.loaded) {
        SongDB.instance.dirtybit = true;
        new UpdateStyleAddExcludeKeywordThread(this, keyword).start();
      }
    } catch (Exception e) { log.error("insertExcludeKeyword(): error", e); }
    insertExcludeKeywordSem.release();
    InsertStyleUIExcludeKeyword(keyword);
  }

  // delete keyword operations:
  //  a) update keyword/excludekeyword array
  //  b) spawn thread to update song membership
  //  c) set songdb dirty bit
  //  d) update user interface (edit style dlg)
  public void removeExcludeKeyword(String keyword) {
    String keywordlc = keyword.toLowerCase();
    try {
      insertExcludeKeywordSem.acquire();
      if (excludekeywords.containsKey(keyword)) {
          excludekeywords.remove(keyword);
      }
      if (RapidEvolution.instance.loaded) {
        new UpdateStyleDeleteExcludeKeywordThread(this, keyword).start();
        SongDB.instance.dirtybit = true;
      }
    } catch (Exception e) { }
    insertExcludeKeywordSem.release();
    RemoveStyleUIExcludeKeyword(keyword);
  }
  public void removeKeyword(String keyword) {
    String keywordlc = keyword.toLowerCase();
    try {
      insertKeywordSem.acquire();
      if (keywords.containsKey(keyword)) {
          keywords.remove(keyword);
        if (RapidEvolution.instance.loaded) {
          new UpdateStyleDeleteKeywordThread(this, keyword).start();
          SongDB.instance.dirtybit = true;
        }
      }
    } catch (Exception e) { }
    insertKeywordSem.release();
    RemoveStyleUIKeyword(keyword);
  }

  // returns true if the style contains the song
  public boolean containsDirect(SongLinkedList song) {
      if (song == null) return false;
      if (song.getStyle(get_sindex())) return true;
      return false;
  }

  public boolean containsLogical(SongLinkedList song) {
      if (song == null) return false;
      if (song.getStyle(get_sindex())) return true;
      // check child styles
      Set child_set = child_style_ids.keySet();
      if (child_set != null) {
          Iterator iter = child_set.iterator();
          while (iter.hasNext()) {
              int style_id = ((Integer)iter.next()).intValue();
              StyleLinkedList child = getStyle(style_id);
              if ((child != null) && child.containsLogical(song)) return true;
          }
      }
      return false;
  }
  
  public boolean matchesExcludeKeywords(SongLinkedList song, String keyword) {
    if (StringUtil.substring(keyword, song.getArtist())) return true;
    if (StringUtil.substring(keyword, song.getAlbum())) return true;
    if (StringUtil.substring(keyword, song.getTrack())) return true;
    if (StringUtil.substring(keyword, song.getSongname())) return true;
    if (StringUtil.substring(keyword, song.getRemixer())) return true;
    if (StringUtil.substring(keyword, song.getComments())) return true;
    if (StringUtil.substring(keyword, song.getUser1())) return true;
    if (StringUtil.substring(keyword, song.getUser2())) return true;
    if (StringUtil.substring(keyword, song.getUser3())) return true;
    if (StringUtil.substring(keyword, song.getUser4())) return true;
    return false;
  }

  public boolean matchesExcludeKeywords(SongLinkedList song) {
      Set keyset = excludekeywords.keySet();
      if (keyset != null) {
          Iterator iter = keyset.iterator();
          while (iter.hasNext()) {
              String keyword = (String)iter.next();
              if (matchesExcludeKeywords(song, keyword)) return true;
          }
      }
    return false;
  }

  public boolean matchesIncludeKeywords(SongLinkedList song, String keyword) {
    if (StringUtil.substring(keyword, song.getArtist())) return true;
    if (StringUtil.substring(keyword, song.getAlbum())) return true;
    if (StringUtil.substring(keyword, song.getTrack())) return true;
    if (StringUtil.substring(keyword, song.getSongname())) return true;
    if (StringUtil.substring(keyword, song.getRemixer())) return true;
    if (StringUtil.substring(keyword, song.getComments())) return true;
    if (StringUtil.substring(keyword, song.getUser1())) return true;
    if (StringUtil.substring(keyword, song.getUser2())) return true;
    if (StringUtil.substring(keyword, song.getUser3())) return true;
    if (StringUtil.substring(keyword, song.getUser4())) return true;
    return false;
  }

  public boolean matchesIncludeKeywords(SongLinkedList song) {
      Set keyset = keywords.keySet();
      if (keyset != null) {
          Iterator iter = keyset.iterator();
          while (iter.hasNext()) {
              String keyword = (String)iter.next();
              if (matchesIncludeKeywords(song, keyword)) return true;
          }
      }
    return false;
  }

  public int countTotalLogicalSongs() {
      SongLinkedList iter = SongDB.instance.SongLL;
      int count = 0;
      while (iter != null) {
          if (containsLogical(iter)) ++count;
          iter = iter.next;
      }
      return count;
  }
  
  // PRIVATE:  
  
  private void insertSort(String keyword) {
      keywords.put(keyword, null);
  }
  private void insertSortExclude(String keyword) {
excludekeywords.put(keyword, null);  }

  private boolean containsA(SongLinkedList song) {
    // if updating, change UpdateStyleAddKeywordThread and others too
    boolean valid = false;
    if (!valid && (songs.containsKey(new Long(song.uniquesongid))))
        valid = true;
    if (!valid) valid = matchesIncludeKeywords(song);
    if (valid) {
        if (excludesongs.containsKey(new Long(song.uniquesongid))) return false;
      if (matchesExcludeKeywords(song)) return false;
    }
    return valid;
  }

  private int _sindex = -1;

  public class UpdateStyleAddKeywordThread extends Thread {
    public UpdateStyleAddKeywordThread(StyleLinkedList inputstyle, String key)  { style = inputstyle; keyword = key; }
    StyleLinkedList style;
    String keyword;
    public void run()  {
      if (style == null) return;
      SongDB.instance.stylesdirtybit++;
      try {
      int styleindex = style.get_sindex();
      SongLinkedList iter = SongDB.instance.SongLL;
      while (iter != null) {
        if (!iter.getStyle(styleindex)) {
          boolean valid = matchesIncludeKeywords(iter, keyword);
          if (valid) {
            valid = !matchesExcludeKeywords(iter);
            if (valid) {
                if (valid && excludesongs.containsKey(new Long(iter.uniquesongid))) valid = false;
            }
          }
          if (valid) {
            iter.setStyle(styleindex, true);
            if ((EditStyleUI.instance.editedstyle == style) && ListStyleSongsUI.instance.isVisible()) ListStyleSongsUI.instance.InsertSongStyleList(iter);
          }
        }
        iter = iter.next;
      }
      } catch (Exception e) { log.error("UpdateStyleAddKeywordThread(): error", e); }
      SongDB.instance.stylesdirtybit--;
    }
  }

  public class UpdateStyleAddExcludeKeywordThread extends Thread {
    public UpdateStyleAddExcludeKeywordThread(StyleLinkedList inputstyle, String key)  { style = inputstyle; keyword = key; }
    StyleLinkedList style;
    String keyword;
    public void run()  {
      if (style == null) return;
      SongDB.instance.stylesdirtybit++;
      try {
      int styleindex = style.get_sindex();
      SongLinkedList iter = SongDB.instance.SongLL;
      while (iter != null) {
        if (iter.getStyle(styleindex)) {
          boolean valid = !matchesIncludeKeywords(iter, keyword);
          if (!valid) {
            iter.setStyle(styleindex, false);
            if ((style == EditStyleUI.instance.editedstyle) && ListStyleSongsUI.instance.isVisible()) ListStyleSongsUI.instance.RemoveSongStyleList(iter);
          }
        }
        iter = iter.next;
      }
      } catch (Exception e) { log.error("UpdateStyleAddExcludeKeywordThread(): error", e); }
      SongDB.instance.stylesdirtybit--;
    }
  }

  public class UpdateStyleDeleteKeywordThread extends Thread {
    public UpdateStyleDeleteKeywordThread(StyleLinkedList inputstyle, String key)  { style = inputstyle; keyword = key; }
    StyleLinkedList style;
    String keyword;
    public void run()  {
      if (style == null) return;
      SongDB.instance.stylesdirtybit++;
      try {
      int styleindex = style.get_sindex();
      SongLinkedList iter = SongDB.instance.SongLL;
      while (iter != null) {
        if (iter.getStyle(styleindex)) {
          boolean valid = !matchesIncludeKeywords(iter, keyword);
          if (valid == false) {
            valid = matchesExcludeKeywords(iter);
            if (!valid && songs.containsKey(new Long(iter.uniquesongid))) valid = false;
            if (!valid) {
              iter.setStyle(styleindex, false);
              if ((EditStyleUI.instance.editedstyle == style) && ListStyleSongsUI.instance.isVisible()) ListStyleSongsUI.instance.RemoveSongStyleList(iter);
            }
          }
        }
        iter = iter.next;
      }
      } catch (Exception e) { log.error("UpdateStyleDeleteKeywordThread(): error", e); }
      SongDB.instance.stylesdirtybit--;
    }
  }

  public class UpdateStyleDeleteExcludeKeywordThread extends Thread {
    public UpdateStyleDeleteExcludeKeywordThread(StyleLinkedList inputstyle, String key)  { style = inputstyle; keyword = key; }
    StyleLinkedList style;
    String keyword;
    public void run()  {
      if (style == null) return;
      SongDB.instance.stylesdirtybit++;
      try {
      int styleindex = style.get_sindex();
      SongLinkedList iter = SongDB.instance.SongLL;
      while (iter != null) {
        if (!iter.getStyle(styleindex)) {
          boolean valid = !matchesIncludeKeywords(iter, keyword);
          if (valid == false) {
            valid = matchesIncludeKeywords(iter);
            if (!valid && songs.containsKey(new Long(iter.uniquesongid))) valid = true;
            if (valid) {
              valid = !matchesExcludeKeywords(iter);
              if (valid && excludesongs.containsKey(new Long(iter.uniquesongid))) valid = false;
            }
            if (valid) {
              iter.setStyle(styleindex, true);
              if ((EditStyleUI.instance.editedstyle == style) && ListStyleSongsUI.instance.isVisible()) ListStyleSongsUI.instance.InsertSongStyleList(iter);
            }
          }
        }
        iter = iter.next;
      }
      } catch (Exception e) { log.error("UpdateStyleDeleteExcludeKeywordThread(): error", e); }
      SongDB.instance.stylesdirtybit--;
    }
  }

  // for style display
  public Semaphore ExcludeStyleListSem = new Semaphore(1);
  public Vector styleexcludevector = new Vector();
  public Vector styleexcludedisplayvector = new Vector();

  public Semaphore IncludeStyleListSem = new Semaphore(1);
  public Vector styleincludevector = new Vector();
  public Vector styleincludedisplayvector = new Vector();

  public void InsertStyleUIExcludeKeyword(String keyword) {
    if (!RapidEvolution.instance.loaded) return;
    try {
    ExcludeStyleListSem.acquire();
    String sortkeyword = keyword.toLowerCase();
    String displaykeyword = "<" + keyword + ">";
    boolean inserted = false;
    int z = 0;
    while ((z < styleexcludedisplayvector.size()) && !inserted) {
      String cmpstr = ((String)styleexcludedisplayvector.get(z)).toLowerCase();
      if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
      if (sortkeyword.compareTo(cmpstr) < 0) {
        inserted = true;
        if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
          DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.styleexcludelist.getModel();
          dlm2.insertElementAt(displaykeyword, z);
        }
        styleexcludevector.insertElementAt(keyword, z);
        styleexcludedisplayvector.insertElementAt(displaykeyword, z);
      }
      ++z;
    }
    if (!inserted) {
      if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
        DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.styleexcludelist.getModel();
        dlm2.addElement(displaykeyword);
      }
      styleexcludevector.add(keyword);
      styleexcludedisplayvector.add(displaykeyword);
    }
    } catch (Exception e) { log.error("InsertStyleUIExcludeKeyword(): error", e); }
    ExcludeStyleListSem.release();
  }

  public void RemoveStyleUIExcludeKeyword(String keyword) {
    if (!RapidEvolution.instance.loaded) return;
    try {
    ExcludeStyleListSem.acquire();
    boolean removed = false;
    int z = 0;
    while ((z < styleexcludevector.size()) && !removed) {
      try {
        String val = (String)styleexcludevector.get(z);
        if (val.equals(keyword)) {
          if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
            DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.styleexcludelist.getModel();
            dlm2.removeElementAt(z);
          }
          styleexcludevector.removeElementAt(z);
          styleexcludedisplayvector.removeElementAt(z);
          removed = true;
        }
      } catch (Exception e) { } // was of type Long...
      ++z;
    }
    } catch (Exception e) { log.error("RemoveStyleUIExcludeKeyword(): error", e); }
    ExcludeStyleListSem.release();
  }

  public void RemoveStyleUIExcludeSong(SongLinkedList song) {
    if (song == null) return;
    RemoveStyleUIExcludeSong(song.uniquesongid);
  }

  public void RemoveStyleUIExcludeSong(long uniqueid) {
    if (!RapidEvolution.instance.loaded) return;
    try {
    ExcludeStyleListSem.acquire();
    boolean removed = false;
    int z = 0;
    while ((z < styleexcludevector.size()) && !removed) {
      try {
        long val = ((Long)styleexcludevector.get(z)).longValue();
        if (val == uniqueid) {
          if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
            DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.styleexcludelist.getModel();
            dlm2.removeElementAt(z);
          }
          styleexcludevector.removeElementAt(z);
          styleexcludedisplayvector.removeElementAt(z);
          removed = true;
        }
      } catch (Exception e) { } // was type String
      ++z;
    }
    } catch (Exception e) { log.error("RemoveStyleUIExcludeSong(): error", e); }
    ExcludeStyleListSem.release();
  }

  public void InsertStyleUISong(SongLinkedList song) {
    if (!RapidEvolution.instance.loaded)
        return;
    try {
        IncludeStyleListSem.acquire();
        String keyword = song.getSongIdShort();
        boolean inserted = false;
        int z = 0;
        while ((z < styleincludedisplayvector.size()) && !inserted) {
            String cmpstr = ((String) styleincludedisplayvector.get(z));
            if (cmpstr.startsWith("<") && cmpstr.endsWith(">"))
                cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
            if (keyword.compareToIgnoreCase(cmpstr) < 0) {
                inserted = true;
                if ((EditStyleUI.instance.editedstyle == this)
                        && EditStyleUI.instance.isVisible()) {
                    DefaultListModel dlm2 = (DefaultListModel) EditStyleUI.instance.editstylekeywordslist
                            .getModel();
                    dlm2.insertElementAt(keyword, z);
                }
                styleincludevector.insertElementAt(new Long(
                        song.uniquesongid), z);
                styleincludedisplayvector.insertElementAt(keyword, z);
            }
            ++z;
        }
        if (!inserted) {
            if ((EditStyleUI.instance.editedstyle == this)
                    && EditStyleUI.instance.isVisible()) {
                DefaultListModel dlm2 = (DefaultListModel) EditStyleUI.instance.editstylekeywordslist
                        .getModel();
                dlm2.addElement(keyword);
            }
            styleincludevector.add(new Long(song.uniquesongid));
            styleincludedisplayvector.add(keyword);
        }
    } catch (Exception e) {
        log.error("InsertStyleUISong(): error", e);
    }
    IncludeStyleListSem.release();
  }

  public void RemoveStyleUISong(long uniqueid) {
    if (!RapidEvolution.instance.loaded) return;
    try {
    IncludeStyleListSem.acquire();
    boolean removed = false;
    int z = 0;
    while ((z < styleincludevector.size()) && !removed) {
      try {
        long val = ((Long)styleincludevector.get(z)).longValue();
        if (val == uniqueid) {
          if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
            DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.editstylekeywordslist.getModel();
            dlm2.removeElementAt(z);
          }
          styleincludevector.removeElementAt(z);
          styleincludedisplayvector.removeElementAt(z);
          removed = true;
        }
      } catch (Exception e) { }
      ++z;
    }
    } catch (Exception e) { log.error("RemoveStyleUISong(): error", e); }
    IncludeStyleListSem.release();
  }

  public void RemoveStyleUISong(SongLinkedList song) {
    if (song == null) return;
    RemoveStyleUISong(song.uniquesongid);
  }

  public void RemoveStyleUIKeyword(String keyword) {
    if (!RapidEvolution.instance.loaded) return;
    try {
    IncludeStyleListSem.acquire();
    boolean removed = false;
    int z = 0;
    while ((z < styleincludevector.size()) && !removed) {
      try {
        String val = (String)styleincludevector.get(z);
        if (val.equals(keyword)) {
          if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
            DefaultListModel dlm2 = (DefaultListModel) EditStyleUI.instance.editstylekeywordslist.getModel();
            dlm2.removeElementAt(z);
          }
          styleincludevector.removeElementAt(z);
          styleincludedisplayvector.removeElementAt(z);
          removed = true;
        }
      } catch (Exception e) { }
      ++z;
    }
    } catch (Exception e) { log.error("RemoveStyleUIKeyword(): error", e); }
    IncludeStyleListSem.release();
  }

  public void InsertStyleUIKeyword(String keyword) {
    if (!RapidEvolution.instance.loaded) return;
    try {
    IncludeStyleListSem.acquire();
    String sortkeyword = keyword.toLowerCase();
    String displaykeyword = "<" + keyword + ">";
    boolean inserted = false;
    int z = 0;
    while ((z < styleincludedisplayvector.size()) && !inserted) {
      String cmpstr = ((String)styleincludedisplayvector.get(z)).toLowerCase();
      if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
      if (sortkeyword.compareTo(cmpstr) < 0) {
        inserted = true;
        if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
          DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.editstylekeywordslist.getModel();
          dlm2.insertElementAt(displaykeyword, z);
        }
        styleincludevector.insertElementAt(keyword, z);
        styleincludedisplayvector.insertElementAt(displaykeyword, z);
      }
      ++z;
    }
    if (!inserted) {
      if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
        DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.editstylekeywordslist.getModel();
        dlm2.addElement(displaykeyword);
      }
      styleincludevector.add(keyword);
      styleincludedisplayvector.add(displaykeyword);
    }
    } catch (Exception e) { log.error("InsertStyleUIKeyword(): error", e); }
    IncludeStyleListSem.release();
  }

  public void InsertStyleUIExcludeSong(SongLinkedList song) {
    if (!RapidEvolution.instance.loaded) return;
    try {
    ExcludeStyleListSem.acquire();
    String keyword = song.getSongIdShort();
    boolean inserted = false;
    int z = 0;
    while ((z < styleexcludedisplayvector.size()) && !inserted) {
      String cmpstr = ((String)styleexcludedisplayvector.get(z));
      if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
      if (keyword.compareToIgnoreCase(cmpstr) < 0) {
        inserted = true;
        if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
          DefaultListModel dlm2 = (DefaultListModel) EditStyleUI.instance.styleexcludelist.getModel();
          dlm2.insertElementAt(keyword, z);
        }
        styleexcludevector.insertElementAt(new Long(song.uniquesongid), z);
        styleexcludedisplayvector.insertElementAt(keyword, z);
      }
      ++z;
    }
    if (!inserted) {
      if ((EditStyleUI.instance.editedstyle == this) && EditStyleUI.instance.isVisible()) {
        DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.styleexcludelist.getModel();
        dlm2.addElement(keyword);
      }
      styleexcludevector.add(new Long(song.uniquesongid));
      styleexcludedisplayvector.add(keyword);
    }
    } catch (Exception e) { log.error("InsertStyleUIExcludeSong(): error", e); }
    ExcludeStyleListSem.release();
  }
  
  // --------- Transferable --------------

  final public static DataFlavor INFO_FLAVOR =
      new DataFlavor(StyleLinkedList.class, "Style");
  static DataFlavor flavors[] = {INFO_FLAVOR };

  
  public boolean isDataFlavorSupported(DataFlavor df) {
    return df.equals(INFO_FLAVOR);
  }

  /** implements Transferable interface */
  public Object getTransferData(DataFlavor df)
      throws UnsupportedFlavorException, IOException {
    if (df.equals(INFO_FLAVOR)) {
      return String.valueOf(getStyleId());
    }
    else throw new UnsupportedFlavorException(df);
  }

  /** implements Transferable interface */
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  
  // selection state stuff
  private boolean is_excluded;
  private boolean is_required;
  public boolean isExcluded() { return is_excluded; }
  public boolean isRequired() { return is_required; }
  public void setExcluded(boolean is_excluded) {
      if (log.isDebugEnabled()) log.debug("setExcluded(): is_excluded=" + is_excluded + ", this=" + this);
      this.is_excluded = is_excluded;
  }
  public void setRequired(boolean is_required) {
      if (log.isDebugEnabled()) log.debug("setRequired(): is_required=" + is_required + ", this=" + this);
      this.is_required = is_required;
  }
  
};
