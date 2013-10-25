package com.mixshare.rapid_evolution.data;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.data.index.Index;

import rapid_evolution.Artist;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongList;
import rapid_evolution.SongStack;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.net.DeleteSongFromServer;
import rapid_evolution.ui.DefaultSortTableModel;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.ExcludeUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.RootsUI;
import rapid_evolution.ui.SuggestedMixesUI;
import rapid_evolution.ui.SyncUI;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;

public class UserLibrary {

    ////////////
    // STATIC //
    ////////////
    
    static private Logger log = Logger.getLogger(UserLibrary.class);
    
    static private int nextUniqueLibraryId = 0;
    static public int getUniqueLibraryId() {
        return nextUniqueLibraryId++;
    }

    //////////////
    // INSTANCE //
    //////////////

    private int libraryId;
    private String filename;
    private Index songIndex = new Index();
    private Index styleIndex = new Index();
    
    ////////////
    // PUBLIC //
    ////////////
    
    public UserLibrary(String filename) {
        this.filename = filename;
        libraryId = getUniqueLibraryId();
    }

    public boolean equals(Object o) {
        if (o instanceof UserLibrary) {
            UserLibrary oLibrary = (UserLibrary)o;
            return (libraryId == oLibrary.getLibraryId());
        }
        return false;
    }
    
    public int hashCode() { return libraryId; }
    
    
    public String getFilename() {
        return filename;
    }
    
    public int getLibraryId() {
        return libraryId;
    }
    
    public Index getSongIndex() {
        return songIndex;
    }
    
    public Index getStyleIndex() {
        return styleIndex;
    }  
    
    public void deleteSong(SongLinkedList song) {
        /*
        try {
            //DeletesongSem.acquire();  still needed?
            
            if (EditSongUI.instance.isVisible() && EditSongUI.instance.getEditedSong().equals(song)) {
                EditSongUI.instance.setVisible(false);
            }
            
            // update song/style indexes
            getSongIndex().remove(song);            
            Iterator styleIter = getStyleIndex().getIterator();
            while (styleIter.hasNext()) {
                StyleLinkedList style = (StyleLinkedList)styleIter.next();
                if (style.containsSong(song.uniquesongid))
                    style.removeSong(song);
                  if (style.containsExcludeSong(song.uniquesongid))
                      style.removeExcludeSong(song);
                
            }
                        
            // update back/next stacks
            SongStack piter = null;
            SongStack siter = RapidEvolutionUI.instance.prevstack;
            while (siter != null) {
              if (siter.songid == song.uniquesongid) {
                if (piter == null) {
                  RapidEvolutionUI.instance.prevstack = siter.next;
                } else {
                  piter.next = siter.next;
                }
              }
              piter = siter;
              siter = siter.next;
            }            
            piter = null;
            siter = RapidEvolutionUI.instance.nextstack;
            while (siter != null) {
              if (siter.songid == song.uniquesongid) {
                if (piter == null) {
                  RapidEvolutionUI.instance.nextstack = siter.next;
                } else {
                  piter.next = siter.next;
                }
              }
              piter = siter;
              siter = siter.next;
            }
            
            // special conditions if deleting the current song
            if (RapidEvolutionUI.instance.currentsong == song) {
              if (RapidEvolutionUI.instance.prevstack != null) {
                SearchPane.instance.bpmslider.setValue(0);
                switchto = NewGetSongPtr(RapidEvolutionUI.instance.prevstack.songid);
                RapidEvolutionUI.instance.prevstack = RapidEvolutionUI.instance.prevstack.next;
                if (RapidEvolutionUI.instance.prevstack == null) RapidEvolutionUI.instance.backbutton.setEnabled(false);
              } else if (RapidEvolutionUI.instance.nextstack != null) {
                SearchPane.instance.bpmslider.setValue(0);
                switchto = NewGetSongPtr(RapidEvolutionUI.instance.nextstack.songid);
                RapidEvolutionUI.instance.nextstack = RapidEvolutionUI.instance.nextstack.next;
                if ((RapidEvolutionUI.instance.nextstack == null) && (switchto != null) && (switchto.next == null)) RapidEvolutionUI.instance.nextbutton.setEnabled(false);
              } else {
                RapidEvolutionUI.instance.ClearCurrentSong();
              }
            }

            SongList slist = SearchPane.instance.searchdisplaylist;
            SongList pslist = null;
            while (slist != null) {
              if (slist.song == song) {
                if (pslist != null) {
                  pslist.next = slist.next;
                } else {
                  SearchPane.instance.searchdisplaylist = slist.next;
                }
              }
              pslist = slist;
              slist = slist.next;
            }

            dirtybit = true;

            SongLinkedList siter3 = SongLL;
            SongLinkedList psiter3 = null;
            while (siter3 != null) {
              if (siter3 == song) {
                if (psiter3 != null) psiter3.next = siter3.next;
                else {
                  addsinglesongsem.acquire();
                  SongLL = siter3.next;
                }
                addsinglesongsem.release();
              } else {
                for (int i = 0; i < siter3.getNumMixoutSongs(); ++i) {
                  if (siter3.mixout_songs[i] == song.uniquesongid) {
                    siter3.removeMixOut(i);
                    --i;
                  }
                }
                for (int i = 0; i < siter3.getNumExcludeSongs(); ++i) {
                  if (siter3.exclude_songs[i] == song.uniquesongid) {
                    siter3.removeExclude(i);
                    --i;
                  }
                }
              }
              psiter3 = siter3;
              siter3 = siter3.next;
            }
            if (switchto == song) switchto = null;

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

            removeAlbumIfEmpty(song.getAlbum(), song);
            
            } catch (Exception e) { log.error("DeleteSong(): error deleting song=" + song, e); }
            DeleteSongFromServer.addToQueue(song);
            DeletesongSem.release();
            Artist.RemoveSongFromArtistList(song);
            
            RapidEvolution.getMusicDatabase().getArtistIndex().removeItem(song.getArtist());
            RapidEvolution.getMusicDatabase().getAlbumIndex().removeItem(song.getAlbum());
            RapidEvolution.getMusicDatabase().getCustom1Index().removeItem(song.getUser1());
            RapidEvolution.getMusicDatabase().getCustom2Index().removeItem(song.getUser2());
            RapidEvolution.getMusicDatabase().getCustom3Index().removeItem(song.getUser3());
            RapidEvolution.getMusicDatabase().getCustom4Index().removeItem(song.getUser4());
            
            OptionsUI.instance.filter1.updatedSong(song, true);
            OptionsUI.instance.filter2.updatedSong(song, true);
            OptionsUI.instance.filter3.updatedSong(song, true);
        */
    }
    
}
