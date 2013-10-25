package rapid_evolution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.net.DeleteSongFromServer;
import rapid_evolution.net.UpdateServerSong;
import rapid_evolution.ui.AddSongUIUpdateThread;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.AddStyleRunnable;
import rapid_evolution.ui.DeleteSongUIUpdateThread;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.SyncUI;
import rapid_evolution.ui.main.AddIdentifyThread;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.StylesUI;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.audio.tags.TagWriteJobBuffer;
import com.mixshare.rapid_evolution.library.RootMusicExcludeList;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class SongDB {

    private static Logger log = Logger.getLogger(SongDB.class);
    
    public SongDB() {
        instance = this;
    }        
    
    public static SongDB instance = null;

    public SongLinkedList SongLL = null;
    public HashMap songmap = new HashMap();
    public HashMap uniquesongstring_songmap = new HashMap();

    public int getSongCount() {
        int count = 0;
        SongLinkedList iter = SongLL;
        while (iter != null) {
            ++count;
            iter = iter.next;
        }
        return count;
    }
    
    public Semaphore addsinglesongsem = new Semaphore(1);
    long total_songs = 0;

    public StyleLinkedList masterstylelist = null;
    public int num_styles = 0;
    public boolean dirtybit = false;

    public SongLinkedList switchto;
    private Semaphore DeletesongSem = new Semaphore(1);
    public void DeleteSong(SongLinkedList song) {
      try {
      DeletesongSem.acquire();
      if (EditSongUI.instance.isVisible() && EditSongUI.instance.getEditedSong().equals(song)) {
          EditSongUI.instance.setVisible(false);
      }
      StyleLinkedList styleiter = masterstylelist;      
      songmap.remove(new Long(song.uniquesongid));
      uniquesongstring_songmap.remove(song.getUniqueStringId());
      while (styleiter != null) {
          if (styleiter.containsSong(song.uniquesongid))
            styleiter.removeSong(song);
          if (styleiter.containsExcludeSong(song.uniquesongid))
            styleiter.removeExcludeSong(song);
        styleiter = styleiter.next;
      }

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

      javax.swing.SwingUtilities.invokeLater(new DeleteSongUIUpdateThread(song));

      removeAlbumIfEmpty(song.getAlbum(), song);
      
      RootMusicExcludeList.addExcludedFile(song.getFileName());
      
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
      
    }

    public void SyncSongs(SongLinkedList mastersong, Vector synclist) {
      Object[] options = {
          SkinManager.instance.getDialogOption("sync_songs_prompt", 1),
          SkinManager.instance.getDialogOption("sync_songs_prompt", 2),
          SkinManager.instance.getDialogOption("sync_songs_prompt", 3)};
      int n = IOptionPane.showOptionDialog(SyncUI.instance.getDialog(),
          SkinManager.instance.getDialogMessageText("sync_songs_prompt"),
          SkinManager.instance.getDialogMessageTitle("sync_songs_prompt"),
          IOptionPane.YES_NO_CANCEL_OPTION,
          options);

      boolean deletesongs = false;
      if (n == 2) return;
      if (n == 0) deletesongs = true;

      for (int i = 0; i < synclist.size(); ++i) {
        SongLinkedList syncsong = (SongLinkedList)synclist.get(i);
        for (int m = 0; m < syncsong.getNumMixoutSongs(); ++m) {
          boolean alreadyexists = false;
          for (int h = 0; h < mastersong.getNumMixoutSongs(); ++h) {
            if (mastersong.mixout_songs[h] == syncsong.mixout_songs[m]) alreadyexists = true;
          }
          if (!alreadyexists) {
            mastersong.insertMixOut(NewGetSongPtr(syncsong.mixout_songs[m]), syncsong._mixout_comments[m], syncsong._mixout_bpmdiff[m], syncsong.getMixoutRank(m), syncsong._mixout_addons[m]);
          }
        }
        for (int m = 0; m < syncsong.getNumExcludeSongs(); ++m) {
          boolean alreadyexists = false;
          for (int h = 0; h < mastersong.getNumExcludeSongs(); ++h) {
            if (mastersong.exclude_songs[h] == syncsong.exclude_songs[m]) alreadyexists = true;
          }
          if (!alreadyexists) {
            mastersong.insertExclude(NewGetSongPtr(syncsong.exclude_songs[m]));
          }
        }

        StyleLinkedList styleiter = masterstylelist;
        int scount = 0;
        while (styleiter != null) {
          if (styleiter.containsDirect(syncsong) && !styleiter.containsDirect(mastersong)) {
            styleiter.insertSong(mastersong);
           }
           styleiter = styleiter.next;
           scount++;
        }
        if ((mastersong == EditSongUI.instance.getEditedSong()) && (EditSongUI.instance.isVisible())) {
            EditSongUI.instance.populateSongStyles(mastersong);
        }

        SongLinkedList iter = SongLL;
        while (iter != null) {
          if ((iter != mastersong) && (iter != syncsong)) {
            boolean hasmaster = false;
            boolean hassync = false;
            int syncindex = -1;
            for (int m = 0; m < iter.getNumMixoutSongs(); ++m) {
              if (iter.mixout_songs[m] == mastersong.uniquesongid) hasmaster = true;
              if (iter.mixout_songs[m] == syncsong.uniquesongid) { hassync = true; syncindex = m; }
            }
            if (hassync && !hasmaster) {
              iter.insertMixOut(mastersong, iter._mixout_comments[syncindex], iter._mixout_bpmdiff[syncindex], iter.getMixoutRank(syncindex), iter._mixout_addons[syncindex]);
            }
            hasmaster = false;
            hassync = false;
            syncindex = -1;
            for (int m = 0; m < iter.getNumExcludeSongs(); ++m) {
              if (iter.exclude_songs[m] == mastersong.uniquesongid) hasmaster = true;
              if (iter.exclude_songs[m] == syncsong.uniquesongid) { hassync = true; syncindex = m; }
            }
            if (hassync && !hasmaster) {
              iter.insertExclude(mastersong);
            }
          }
          iter = iter.next;
        }
      }

      if (deletesongs) {
        for (int i = 0; i < synclist.size(); ++i) {
          SongLinkedList syncsong = (SongLinkedList)synclist.get(i);
          DeleteSong(syncsong);
        }
        SyncUI.instance.addedsyncsongs = new Vector();
        SyncUI.instance.Redrawsync();
      }
      if (mastersong == RapidEvolutionUI.instance.currentsong) MixoutPane.instance.RedrawMixoutTable();
    }

    public SongLinkedList getSongLinkedList(SongLinkedList input, String filename) {
      if (input == null) return null;
      SongLinkedList iter = SongLL;
      if ((filename != null) && (!filename.equals(""))) {
        while (iter != null) {
          if (iter.getFileName().equalsIgnoreCase(filename)) return iter;
          iter = iter.next;
        }
      }
      String uniqueid = SongLinkedList.calculate_unique_id(input.getArtist(), input.getAlbum(), input.getTrack(), input.getSongname(), input.getRemixer());
      iter = SongLL;
      while (iter != null) {
        if (uniqueid.toLowerCase().equals(iter.uniquestringid)) return iter;
        iter = iter.next;
      }

      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getAlbum().equals("")) &&
          (!input.getTrack().equals("")) &&
          (!input.getSongname().equals("")) &&
          (!input.getRemixer().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
              (iter.getTrack().toLowerCase().equals(input.getTrack().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())) &&
              (iter.getRemixer().toLowerCase().equals(input.getRemixer().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getAlbum().equals("")) &&
          (!input.getTrack().equals("")) &&
          (!input.getSongname().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
              (iter.getTrack().toLowerCase().equals(input.getSongname().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getAlbum().equals("")) &&
          (!input.getSongname().equals("")) &&
          (!input.getRemixer().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())) &&
              (iter.getRemixer().toLowerCase().equals(input.getRemixer().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getAlbum().equals("")) &&
          (!input.getTrack().equals("")) &&
          (!input.getSongname().equals("")) &&
          (!input.getRemixer().equals(""))) {
        while (iter != null) {
          if ( (iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
              (iter.getTrack().toLowerCase().equals(input.getTrack().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())) &&
              (iter.getRemixer().toLowerCase().equals(input.getRemixer().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getAlbum().equals("")) &&
          (!input.getSongname().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getSongname().equals("")) &&
          (!input.getRemixer().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())) &&
              (iter.getRemixer().toLowerCase().equals(input.getRemixer().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getTrack().equals("")) &&
          (!input.getRemixer().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getTrack().toLowerCase().equals(input.getTrack().toLowerCase())) &&
              (iter.getRemixer().toLowerCase().equals(input.getRemixer().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getAlbum().equals("")) &&
          (!input.getTrack().equals("")) &&
          (!input.getSongname().equals(""))) {
        while (iter != null) {
          if ( (iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
              (iter.getTrack().toLowerCase().equals(input.getTrack().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getAlbum().equals("")) &&
          (!input.getRemixer().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
              (iter.getRemixer().toLowerCase().equals(input.getRemixer().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getTrack().equals("")) &&
          (!input.getSongname().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getTrack().toLowerCase().equals(input.getTrack().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getArtist().equals("")) &&
          (!input.getSongname().equals(""))) {
        while (iter != null) {
          if ( (iter.getArtist().toLowerCase().equals(input.getArtist().toLowerCase())) &&
              (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())))return
              iter;
          iter = iter.next;
        }
      }
      iter = SongLL;
      if ((!input.getAlbum().equals("")) &&
          (!input.getSongname().equals(""))) {
      while (iter != null) {
        if ((iter.getAlbum().toLowerCase().equals(input.getAlbum().toLowerCase())) &&
            (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase()))) return iter;
        iter = iter.next;
      }
      }
      iter = SongLL;
      if ((!input.getRemixer().equals("")) &&
          (!input.getSongname().equals(""))) {

      while (iter != null) {
        if ((iter.getRemixer().toLowerCase().equals(input.getRemixer().toLowerCase())) &&
            (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase()))) return iter;
        iter = iter.next;
      }
      }
      iter = SongLL;
      if ((!input.getSongname().equals(""))) {

      while (iter != null) {
        if (iter.getSongname().toLowerCase().equals(input.getSongname().toLowerCase())) return iter;
        iter = iter.next;
      }
      }
      return null;
    }

    static public boolean isupdatingsong = false;
    private static Semaphore UpdateSem = new Semaphore(1);
    public void UpdateSong(SongLinkedList editsong, OldSongValues old_values, String[] stylelist) {
        if (stylelist != null) {
	        for (int i = 0; i < stylelist.length; ++i) {
	            boolean found = false;
	            StyleLinkedList siter = SongDB.instance.masterstylelist;
	            while ((siter != null) && !found) {
	              if (StringUtil.areStyleNamesEqual(siter.getName(), stylelist[i])) {
	                  siter.insertSong(editsong);
	                  found = true;
	              }
	              siter = siter.next;
	            }
	            if (!found) {
                  StyleLinkedList newstyle = SongDB.instance.addStyle(stylelist[i], false);	                
                  newstyle.insertSong(editsong);
	            }
	        }
        }
        UpdateSong(editsong, old_values);
    }
    
    public void UpdateSong(SongLinkedList editsong, OldSongValues old_values) {

        try {
            UpdateSem.acquire();
        isupdatingsong = true;
        SearchPane.instance.dontfiretablechange++;    
        log.debug("UpdateSong(): edited song=" + editsong);
        
        RapidEvolutionUI.instance.UpdateSongRoutine(editsong, old_values);
              
      Artist.RemoveSongFromArtistList(editsong);

      dirtybit = true;
      if (!editsong.uniquestringid.equals(old_values.getUniqueStringId())) {
          UpdateServerSong.addToQueue(editsong, old_values);
          uniquesongstring_songmap.remove(old_values.getUniqueStringId());
          uniquesongstring_songmap.put(editsong.getUniqueStringId(), editsong);
      }
      
      SongLinkedList iter = SongLL;
      SongLinkedList prev = null;
      while (iter != editsong) {
        prev = iter;
        iter = iter.next;
      }
      if (prev == null) { try { addsinglesongsem.acquire(); SongLL = SongLL.next; } catch (Exception e) { } addsinglesongsem.release(); }
      else prev.next = iter.next;
      iter = SongLL;
      prev = null;
      while ((iter != null) && (iter.compareTo(editsong) < 0)) {
        prev = iter;
        iter = iter.next;
      }
      if (prev == null) {
        try {
          addsinglesongsem.acquire();
          editsong.next = SongLL;
          SongLL = editsong;
        } catch (Exception e) { }
        addsinglesongsem.release();
      } else {
        prev.next = editsong;
        editsong.next = iter;
      }      

      UpdateStylesRoutine(editsong);
//    new UpdateStyles(editsong).start();
      Artist.InsertToArtistList(editsong);

      if (!old_values.getAlbum().equals(editsong.getAlbum())) {
          SongDB.instance.renameAlbum(editsong, old_values.getAlbum());
          SongDB.instance.invalidateAlbumCoverCacheForAlbum(old_values.getAlbum());
          SongDB.instance.invalidateAlbumCoverCacheForAlbum(editsong.getAlbum());
          RapidEvolution.getMusicDatabase().getAlbumIndex().removeItem(old_values.getAlbum());
          RapidEvolution.getMusicDatabase().getAlbumIndex().addItem(editsong.getAlbum());
      }
      if (!old_values.getArtist().equals(editsong.getArtist())) {
          RapidEvolution.getMusicDatabase().getArtistIndex().removeItem(old_values.getArtist());
          RapidEvolution.getMusicDatabase().getArtistIndex().addItem(editsong.getArtist());
      }
      if (!old_values.getUser1().equals(editsong.getUser1())) {
          RapidEvolution.getMusicDatabase().getCustom1Index().removeItem(old_values.getUser1());
          RapidEvolution.getMusicDatabase().getCustom1Index().addItem(editsong.getUser1());
      }
      if (!old_values.getUser2().equals(editsong.getUser2())) {
          RapidEvolution.getMusicDatabase().getCustom2Index().removeItem(old_values.getUser2());
          RapidEvolution.getMusicDatabase().getCustom2Index().addItem(editsong.getUser2());
      }
      if (!old_values.getUser3().equals(editsong.getUser3())) {
          RapidEvolution.getMusicDatabase().getCustom3Index().removeItem(old_values.getUser3());
          RapidEvolution.getMusicDatabase().getCustom3Index().addItem(editsong.getUser3());
      }
      if (!old_values.getUser4().equals(editsong.getUser4())) {
          RapidEvolution.getMusicDatabase().getCustom4Index().removeItem(old_values.getUser4());
          RapidEvolution.getMusicDatabase().getCustom4Index().addItem(editsong.getUser4());
      }
            
        } catch (Exception e) {
            log.error("UpdateSong(): error updating song=" + editsong, e);
        }
        isupdatingsong = false;
        if (OptionsUI.instance.tagautoupdate.isSelected() && tagNeedsWriting(editsong, old_values)) {
        	TagWriteJobBuffer.addSong(editsong);
        }
        SearchPane.instance.dontfiretablechange--;        
        UpdateSem.release();
    }
    
    private static boolean tagNeedsWriting(SongLinkedList editsong, OldSongValues old_values) {
    	try {
	    	if ((OptionsUI.instance.tagwriteartist.isSelected()) && (!old_values.getArtist().equals(editsong.getArtist())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritealbum.isSelected()) && (!old_values.getAlbum().equals(editsong.getAlbum())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritingalbumcover.isSelected()) && (!old_values.getAlbumCoverLabel().equals(editsong.getAlbumCoverLabel())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritebpm.isSelected()) && (old_values.getStartBpm() != editsong.getStartbpm()))
	    		return true;
	    	if ((OptionsUI.instance.tagwritebpm.isSelected()) && (old_values.getEndBpm() != editsong.getEndbpm()))
	    		return true;
	    	if ((OptionsUI.instance.tagwritecomments.isSelected()) && (!old_values.getComments().equals(editsong.getComments())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritingcustomfields.isSelected() && (OptionsUI.instance.custom_field_1_tag_combo.getSelectedIndex() != 0)) && (!old_values.getUser1().equals(editsong.getUser1())))
	    		return true;	    	
	    	if ((OptionsUI.instance.tagwritingcustomfields.isSelected() && (OptionsUI.instance.custom_field_2_tag_combo.getSelectedIndex() != 0)) && (!old_values.getUser2().equals(editsong.getUser2())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritingcustomfields.isSelected() && (OptionsUI.instance.custom_field_3_tag_combo.getSelectedIndex() != 0)) && (!old_values.getUser3().equals(editsong.getUser3())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritingcustomfields.isSelected() && (OptionsUI.instance.custom_field_4_tag_combo.getSelectedIndex() != 0)) && (!old_values.getUser4().equals(editsong.getUser4())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritegenre.isSelected()) && (!old_values.getGenre().equals(editsong.getGenre())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritekeytogroupingtag.isSelected()) && (!old_values.getStartKey().equals(editsong.getStartKey())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritekeytogroupingtag.isSelected()) && (!old_values.getEndKey().equals(editsong.getEndKey())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritekey.isSelected()) && (!old_values.getStartKey().equals(editsong.getStartKey())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritekey.isSelected()) && (!old_values.getEndKey().equals(editsong.getEndKey())))
	    		return true;
	    	if ((OptionsUI.instance.tagwriterating.isSelected()) && (old_values.getRating() != editsong.getRatingInt()))
	    		return true;
	    	if ((OptionsUI.instance.tagwriteremixer.isSelected()) && (!old_values.getRemix().equals(editsong.getRemixer())))
	    		return true;
	    	if ((OptionsUI.instance.tagwriterga.isSelected()) && (!old_values.getRGA().equals(editsong.getRGA())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritingstyles.isSelected()) && (!old_values.areStylesEqualWith(editsong.getStyleStrings())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritetime.isSelected()) && (!old_values.getTime().equals(editsong.getTime())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritetimesig.isSelected()) && (!old_values.getTimesig().equals(editsong.getTimesig())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritesongname.isSelected()) && (!old_values.getTitle().equals(editsong.getSongname())))
	    		return true;
	    	if ((OptionsUI.instance.tagwritetrack.isSelected()) && (!old_values.getTrack().equals(editsong.getTrack())))
	    		return true;
    	} catch (Exception e) {
    		log.error("tagNeedsWriting(): error", e);
    		return true;
    	}
    	return false;
    }
    
    

    public SongLinkedList AddSingleSong(SongLinkedList newsong, String[] initial_styles) {
        Vector styles = new Vector();
        if (initial_styles != null) {
            for (int i = 0; i < initial_styles.length; ++i) {
                StyleLinkedList siter = SongDB.instance.masterstylelist;
                while (siter != null) {
                  if (StringUtil.areStyleNamesEqual(siter.getName(), initial_styles[i])) {
                      styles.add(siter);
                  }
                  siter = siter.next;
                }
            }
        }
        return AddSingleSong(newsong, styles);
    }
    
    public SongLinkedList AddSingleSong(SongLinkedList newsong, Vector initial_styles) {
        if (log.isDebugEnabled()) log.debug("AddSingleSong(): adding newsong=" + newsong + ", initial_styles=" + initial_styles);
        SongLinkedList addedsong = null;
        try {            
          if (newsong.getArtist().equals("") &&
              newsong.getAlbum().equals("") &&
              newsong.getTrack().equals("") &&
              newsong.getSongname().equals("") &&
              newsong.getRemixer().equals("")) return null;
          
          if (AddIdentifyThread.formatTrack(newsong.getTrack())) {
              char firstcharacter = newsong.getTrack().charAt(0);
              if ((firstcharacter >= '1') && (firstcharacter <= '9')) {
                  newsong.setTrack("0" + newsong.getTrack());
              }
          }
          
          newsong.setArtist(StringUtil.cleanString(newsong.getArtist()));
          newsong.setAlbum(StringUtil.cleanString(newsong.getAlbum()));
          newsong.setTrack(StringUtil.cleanString(newsong.getTrack()));
          newsong.setSongname(StringUtil.cleanString(newsong.getSongname()));
          newsong.setRemixer(StringUtil.cleanString(newsong.getRemixer()));
          
          AddSingleSongSem.acquire();
          dirtybit = true;
          String uniquesongid = SongLinkedList.calculate_unique_id(newsong.getArtist(),
                                                    newsong.getAlbum(),
                                                    newsong.getTrack(),
                                                    newsong.getSongname(),
                                                    newsong.getRemixer());
          SongLinkedList existingsong = OldGetSongPtr(uniquesongid);
          if ((existingsong == null) && (!newsong.getFileName().equals(""))) {
        	  SongLinkedList iter = SongDB.instance.SongLL;
        	  while ((iter != null) && (existingsong == null)) {
        		  if (iter.getFileName().equals(newsong.getFileName()))
        				  existingsong = iter;
        		  iter = iter.next;
        	  }
          }
          if (log.isTraceEnabled()) log.trace("AddSingleSong(): existingsong=" + existingsong);
          if (existingsong != null) {
              if (OptionsUI.instance.autoupdatepaths.isSelected()
                      && !newsong.getFileName().equals("")) {
                  // auto update path       
                   existingsong.setFilename(newsong.getFileName());
              }              
          } else  {
            String songid = SongLinkedList.song_to_string_short(newsong.getArtist(),
                                                 newsong.getAlbum(),
                                                 newsong.getTrack(),
                                                 newsong.getSongname(), newsong.getRemixer()).
                toLowerCase();

            SongLinkedList iter = SongLL;
            SongLinkedList prev = null;
            int uniqueid = SongLinkedList.getNextUniqueID();
            float startbpm = newsong.getStartbpm();
            float endbpm = newsong.getEndbpm();
            while ( ( (iter != null) &&
                     (iter.getSongIdShort().compareToIgnoreCase(songid) < 0))) {
              prev = iter;
              iter = iter.next;
            }
            addsinglesongsem.acquire();
            try {
            if (iter == null) {
              if (prev == null) {
                SongLL = new SongLinkedList(uniqueid, newsong.getArtist(),
                                            newsong.getAlbum(),
                                            newsong.getTrack(),
                                            newsong.getSongname(),
                                            newsong.getRemixer(),
                                            newsong.getComments(),
                                            newsong.getVinylOnly(),
                                            newsong.getNonVinylOnly(),
                                            startbpm, endbpm,
                                            0, 0, newsong.getStartKey(),
                                            newsong.getEndKey(),
                                            newsong.getFileName(),
                                            newsong.getTime(),
                                            newsong.getTimesig(),
                                            newsong.isDisabled(), false,
                                            newsong.getUser1(),
                                            newsong.getUser2(),
                                            newsong.getUser3(),
                                            newsong.getUser4(),
                                            null);
                addedsong = SongLL;
              }
              else {
                prev.next = new SongLinkedList(uniqueid, newsong.getArtist(),
                                               newsong.getAlbum(),
                                               newsong.getTrack(),
                                               newsong.getSongname(),
                                               newsong.getRemixer(),
                                               newsong.getComments(),
                                               newsong.getVinylOnly(),
                                               newsong.getNonVinylOnly(),
                                               startbpm, endbpm, 0, 0,
                                               newsong.getStartKey(),
                                               newsong.getEndKey(),
                                               newsong.getFileName(),
                                               newsong.getTime(),
                                               newsong.getTimesig(),
                                               newsong.isDisabled(), false,
                                               newsong.getUser1(),
                                               newsong.getUser2(),
                                               newsong.getUser3(),
                                               newsong.getUser4(), null);
                addedsong = prev.next;
              }
            }
            else {
              if (prev == null) {
                SongLL = new SongLinkedList(uniqueid, newsong.getArtist(),
                                            newsong.getAlbum(),
                                            newsong.getTrack(),
                                            newsong.getSongname(),
                                            newsong.getRemixer(),
                                            newsong.getComments(),
                                            newsong.getVinylOnly(),
                                            newsong.getNonVinylOnly(),
                                            startbpm, endbpm,
                                            0, 0, newsong.getStartKey(),
                                            newsong.getEndKey(),
                                            newsong.getFileName(),
                                            newsong.getTime(),
                                            newsong.getTimesig(),
                                            newsong.isDisabled(), false,
                                            newsong.getUser1(),
                                            newsong.getUser2(),
                                            newsong.getUser3(),
                                            newsong.getUser4(), iter);
                addedsong = SongLL;
              }
              else {
                prev.next = new SongLinkedList(uniqueid, newsong.getArtist(),
                                               newsong.getAlbum(),
                                               newsong.getTrack(),
                                               newsong.getSongname(),
                                               newsong.getRemixer(),
                                               newsong.getComments(),
                                               newsong.getVinylOnly(),
                                               newsong.getNonVinylOnly(),
                                               startbpm, endbpm,
                                               0, 0, newsong.getStartKey(),
                                               newsong.getEndKey(),
                                               newsong.getFileName(),
                                               newsong.getTime(),
                                               newsong.getTimesig(),
                                               newsong.isDisabled(), false,
                                               newsong.getUser1(),
                                               newsong.getUser2(),
                                               newsong.getUser3(),
                                               newsong.getUser4(), iter);
                addedsong = prev.next;
              }
            }
            } catch (Exception e) { log.error("AddSingleSong(): error adding song, newsong=" + newsong, e); }
            addsinglesongsem.release();
            if (log.isTraceEnabled()) log.trace("AddSingleSong(): inserted into SongLL");
            addedsong.itunes_id = newsong.itunes_id;
            addedsong.setDateAdded(newsong.getDateAddedAsMyString());
            addedsong.setBpmAccuracy(newsong.getBpmAccuracy());
            addedsong.setKeyAccuracy(newsong.getKeyAccuracy());
            addedsong.setRGA(newsong.getRGA());
            addedsong.setBeatIntensity(newsong.getBeatIntensity());
            addedsong.calculateSongDisplayIds();
            addedsong.setRating(newsong.getRating());
            addedsong.servercached = false;
            songmap.put(new Long(addedsong.uniquesongid), addedsong);
            uniquesongstring_songmap.put(addedsong.getUniqueStringId(), addedsong);

            RapidEvolution.getMusicDatabase().getArtistIndex().addItem(addedsong.getArtist());
            RapidEvolution.getMusicDatabase().getAlbumIndex().addItem(addedsong.getAlbum());
            RapidEvolution.getMusicDatabase().getCustom1Index().addItem(addedsong.getUser1());
            RapidEvolution.getMusicDatabase().getCustom2Index().addItem(addedsong.getUser2());
            RapidEvolution.getMusicDatabase().getCustom3Index().addItem(addedsong.getUser3());
            RapidEvolution.getMusicDatabase().getCustom4Index().addItem(addedsong.getUser4());
            
//          if (connectedtoserver) new UpdateServerThread(addedsong).start();

            HashMap selected_styles = new HashMap();
            
            if (initial_styles != null) {
              for (int is = 0; is < initial_styles.size(); ++is) {
                  StyleLinkedList style = (StyleLinkedList)initial_styles.get(is);
                  selected_styles.put(style, null);
              }
            }

            /*
            TreePath[] paths = AddSongsUI.instance.addsongstylestree.getSelectionPaths();
            for (int i = 0; i < paths.length; ++i) {
                MyMutableStyleNode node = (MyMutableStyleNode)paths[i].getLastPathComponent();
                selected_styles.put(node.getStyle(), null);
            }
            */

            if (newsong.stylelist != null) {
              for (int is = 0; is < newsong.stylelist.length; ++is) {
                  String g = newsong.stylelist[is];
                  if ((g != null) && g.length() > 0) {
                      if (log.isTraceEnabled())
                          log.trace("addSong(): stylelist=" + g);
                      boolean found = false;
                      
                      StyleLinkedList siter = SongDB.instance.masterstylelist;
                      while (siter != null) {
                          if (StringUtil.areStyleNamesEqual(siter.getName(), g)) {
                              selected_styles.put(siter, null);
                              found = true;
                          }
                          siter = siter.next;
                      }
                      if (!found) {
                          StyleLinkedList newstyle = SongDB.instance.addStyle(g, false);
                          selected_styles.put(newstyle, null);
                      }
                  }
              }
            }

            if (log.isTraceEnabled())
                log.trace("addSong(): selected_styles=" + selected_styles);
            Set keys = selected_styles.keySet();
            if (keys != null) {
                Iterator siter2 = keys.iterator();
                while (siter2.hasNext()) {
                    StyleLinkedList styleiter = (StyleLinkedList)siter2.next();
                     if (styleiter != null) {
                      if (!styleiter.containsDirect(addedsong)) {
                          String[] excludekeywords = styleiter.getExcludeKeywords();
                        for (int i = 0; i < excludekeywords.length; ++i) {
                          if (styleiter.matchesExcludeKeywords(addedsong, excludekeywords[i])) {
                            String display = SkinManager.instance.getDialogMessageText("delete_style_exclude_keyword");
                            display = StringUtil.ReplaceString("%style%", display, styleiter.getName());
                            display = StringUtil.ReplaceString("%keyword%", display,  excludekeywords[i]);
                            display = StringUtil.ReplaceString("%songid%", display,  addedsong.getShortId());
                            int n = IOptionPane.showConfirmDialog(
                                SkinManager.instance.getFrame("main_frame"),
                                display,
                                SkinManager.instance.getDialogMessageTitle("delete_style_exclude_keyword"),
                                IOptionPane.YES_NO_OPTION);
                            if (n == 0) {
                              styleiter.removeExcludeKeyword(excludekeywords[i]);
                              --i;
                            }
                          }
                        }
                        styleiter.insertSong(addedsong);
                      }                    
                     }
                }
            }
            if (log.isTraceEnabled()) log.trace("AddSingleSong(): updated styles");
         
            invalidateAlbumCoverCacheForAlbum(addedsong.getAlbum());
            
            // add addedsong to search table
            if (log.isTraceEnabled()) log.trace("AddSingleSong(): updated tables");

            javax.swing.SwingUtilities.invokeLater(new AddSongUIUpdateThread(addedsong));

          }
        } catch (Exception e) { log.error("AddSingleSong(): error adding newsong: " + newsong, e); }
        if (log.isDebugEnabled()) log.debug("AddSingleSong(): finished adding newsong");        
        AddSingleSongSem.release();
        return addedsong;
    }

    public void invalidateAlbumCoverCacheForAlbum(String album) {
        SongLinkedList siter = SongLL;
        while (siter != null) {
            if (siter.getAlbum().equalsIgnoreCase(album)) {
                siter.iscompilation = null;
            }
            siter = siter.next;
        }        
    }
    
    Semaphore AddSingleSongSem = new Semaphore(1);
    public SongLinkedList AddSingleSong() {

        try {
      TreePath[] nodes = AddSongsUI.instance.addsongstylestree.getSelectionPaths();
      Vector selstyles = new Vector();
      for (int i = 0; i < nodes.length; ++i) {
          MyMutableStyleNode node = (MyMutableStyleNode)nodes[i].getLastPathComponent();
          selstyles.add(node.getStyle());
      }

      SongLinkedList newsong = AddSongsUI.instance.createSongFromValues();

      return AddSingleSong(newsong, selstyles);
        } catch (Exception e) { 
            log.error("AddSingleSong(): error", e);
        }
        return null;
    }


    Semaphore SortSongLLSem = new Semaphore(1);
    public void SortSongLL() {
      try {
      if (SongLL == null) return;
      SortSongLLSem.acquire();
      addsinglesongsem.acquire();
      SongList newlist = new SongList();
      SongLinkedList iter = SongLL;
      while (iter != null) {
        newlist.sortedinsert(iter);
        iter = iter.next;
      }
      SongLL = newlist.song;
      while (newlist != null) {
        if ((newlist.next != null) && (newlist.next.song != null)) newlist.song.next = newlist.next.song;
        else newlist.song.next = null;
        newlist = newlist.next;
      }
      } catch (Exception e) { }
      addsinglesongsem.release();
      SortSongLLSem.release();
    }

    public Semaphore UpdateSongStyleArraysSem = new Semaphore(1);
    void UpdateSongStyleArrays(int insertindex) {
      try {
      SongLinkedList iter = SongLL;
      while (iter != null) {
        boolean[] newbool = new boolean[num_styles];
        int count = 0;
        for (int i = 0; i < num_styles; ++i) {
          if (i == insertindex) newbool[i] = false;
          else newbool[i] = iter.getStyle(count++);
        }
        iter.setStyles(newbool);
        iter = iter.next;
      }
      } catch (Exception e) { log.error("UpdateSongStyleArrays(): error", e); }
    }

    public boolean isAddingSong = false;
    public Semaphore StyleSIndexSem = new Semaphore(1);
    public StyleLinkedList addStyle(String stylename, boolean doEditStyle) {
        return addStyle(stylename, doEditStyle, null);
    }
    public StyleLinkedList addStyle(String stylename, boolean doEditStyle, AddStyleRunnable r) {
        StyleLinkedList thisstyle = null;
      try {
    	  if (log.isDebugEnabled())
    		  log.debug("addStyle(): stylename=" + stylename);
      isAddingSong = true;
      UpdateSongStyleArraysSem.acquire();
      StyleSIndexSem.acquire();
      if (!stylename.equals("")) {
        StyleLinkedList siter = masterstylelist;
          // to do: optimize insertion
        int new_styleid = StyleLinkedList.getNextStyleId();
          masterstylelist = new StyleLinkedList(stylename, masterstylelist, new_styleid);
          num_styles++;
          masterstylelist = sortStyleList(masterstylelist);
          StyleLinkedList usiter = masterstylelist;          
          int sindex = 0;
          int insertindex = -1;
          while (usiter != null) {
            if (usiter.getStyleId() == new_styleid) {
              insertindex = sindex;
              thisstyle = usiter;
            }
            usiter.set_sindex(sindex++);
            usiter = usiter.next;
          }
          UpdateSongStyleArrays(insertindex);
//        thisstyle.insertKeyword(stylename);

          //TODO: add way to add styles directy as childnodes?
          thisstyle.addParentStyle(StyleLinkedList.root_style);
          
          if (r != null)
              r.setStyle(thisstyle);
          StylesUI.insertStyle(thisstyle, r);
                              
          EditStyleUI.instance.display_parameter = thisstyle;
            if (doEditStyle) EditStyleUI.instance.Display();
        }      	
      } catch (Exception e) { } finally {
    	  StyleSIndexSem.release();
    	  UpdateSongStyleArraysSem.release();
      }
      isAddingSong = false;
      
      /*
      // auto hierarchy creation
      // TODO: need to figure out where to put this so selections work properly...
      try {
    	  // look for possible parents
	      int parentSeperatorIndex = stylename.lastIndexOf(", ");
	      if (parentSeperatorIndex >= 0) {
	    	  String parentName = stylename.substring(0, parentSeperatorIndex);
	    	  if (log.isDebugEnabled())
	    		  log.debug("addStyle(): looking for parent style=" + parentName);
	    	  
	    	  boolean parentFound = false;
	    	  StyleLinkedList siter = masterstylelist;
	    	  while (!parentFound && (siter != null)) {
	    		  if (siter.getName().equalsIgnoreCase(parentName)) {
	    			  parentFound = true;
	    	    	  if (log.isDebugEnabled())
	    	    		  log.debug("addStyle(): parent found=" + siter.getName());
	    			  StylesUI.updateHierarchy(StyleLinkedList.root_style, siter, thisstyle, false);
	    		  }
	    		  siter = siter.next;
	    	  }
	      }
	      // look for possible children (can't guarantee the parent is added first)
	      String target = stylename.toLowerCase() + ", ";
    	  StyleLinkedList siter = masterstylelist;
    	  while (siter != null) {
    		  if (siter.getName().toLowerCase().startsWith(target)) {
    	    	  if (log.isDebugEnabled())
    	    		  log.debug("addStyle(): child found=" + siter.getName());
    			  StylesUI.updateHierarchy(siter.isRootStyle() ? StyleLinkedList.root_style : null, thisstyle, siter, siter.isRootStyle() ? false : true);
    		  }
    		  siter = siter.next;
    	  }
	      
      } catch (Exception e) {
    	  log.error("addStyle(): error during auto hierarchy creation", e);
      }
      */
      
      return thisstyle;
    }

    boolean stylesortexception = false;
    public int stylesdirtybit = 0;
    Semaphore sortStyleListSem = new Semaphore(1);
    public StyleLinkedList sortStyleList(StyleLinkedList stylelist) {
      try {
      if ((num_styles == 0) || (stylelist == null)) return null;
      if (num_styles == 1) { stylelist.set_sindex(0); return stylelist; }
      sortStyleListSem.acquire();
      StyleLinkedList newlist = null;
      boolean[] used = new boolean[num_styles];
      for (int i = 0; i < num_styles; ++i) used[i] = false;
      HashMap original_styles = new HashMap();
      StyleLinkedList siter = stylelist;
      while (siter != null) {
          original_styles.put(new Integer(siter.getStyleId()), siter);
          siter = siter.next;
      }
      for (int i = 0; i < num_styles; ++i) {
        StyleLinkedList highest = null;
        siter = stylelist;
        int count = 0;
        int highestindex = 0;
        while (siter != null) {
          if ((highest == null) && !used[count]) {
            highest = siter;
            highestindex = count;
          }
          else if (!used[count] && (highest.getName().compareToIgnoreCase(siter.getName()) <= 0)) {
            highest = siter;
            highestindex = count;
          }
          siter = siter.next;
          count++;
        }
        if (highest != null) {
          used[highestindex] = true;
          newlist = new StyleLinkedList(highest, newlist);
          if (EditStyleUI.instance.editedstyle == highest) {
              EditStyleUI.instance.editedstyle = newlist;
          }
        }
      }  
      StyleLinkedList niter = newlist;
      StyleLinkedList sortedlist = null;      
      StyleLinkedList last_sorted = null;
      int sindex = 0;
      while (niter != null) {
          StyleLinkedList original_style = (StyleLinkedList)original_styles.get(new Integer(niter.getStyleId()));
          original_style.next = null;
          if (sortedlist == null) sortedlist = original_style;
          else last_sorted.next = original_style;          
          original_style.set_sindex(sindex++);
        niter = niter.next;
        last_sorted = original_style;
      }
      sortStyleListSem.release();
      return sortedlist;
      } catch (Exception e) { log.error("sortStyleList(): error", e); stylesortexception = true; }
      sortStyleListSem.release();
      return null;
    }

    public void RemoveStyle(StyleLinkedList style) {
      try {
      UpdateSongStyleArraysSem.acquire();
      StyleSIndexSem.acquire();
      StyleLinkedList siter = masterstylelist;
      StyleLinkedList prev = null;
      int count = 0;
      int removedindex = 0;
      while (siter != null) {
        if (siter.equals(style)) {
          if (siter == masterstylelist) {
            removedindex = count;
            masterstylelist = masterstylelist.next;
            siter = siter.next;
          }
          else {
            removedindex = count;
            prev.next = siter.next;
            siter = siter.next;
          }
        } else {
            siter.removeParentStyle(style);
            siter.removeChildStyle(style);
          siter.set_sindex(count);
          prev = siter;
          count++;
          siter = siter.next;
        }
      }
      UpdateSongStyleArrays2(removedindex);
      } catch (Exception e) { } finally {
    	  StyleSIndexSem.release();
    	  UpdateSongStyleArraysSem.release();
      }
    }

    void UpdateSongStyleArrays2(int removedindex) {
      SongLinkedList iter = SongLL;
      while (iter != null) {
        boolean[] newbool = new boolean[num_styles - 1];
        int count = 0;
        for (int i = 0; i < num_styles; ++i) {
          if (i != removedindex) newbool[count++] = iter.getStyle(i);
        }
        iter.setStyles(newbool);
        iter = iter.next;
      }
    }

    void UpdateStylesRoutine(SongLinkedList song) {
      stylesdirtybit++;
      try {
        if (song == null) {
          if (SongLL != null) log.debug("UpdateStylesRoutine(): starting validation of all styles");
          SongLinkedList siter = SongLL;
          while (siter != null) {
            StyleLinkedList stiter = masterstylelist;
            int styleindex = 0;
            if (RapidEvolution.instance.terminatesignal)return;
            while (stiter != null) {
              stiter.update(siter, styleindex);
              stiter = stiter.next;
              styleindex++;
            }
            siter = siter.next;
          }
          if (SongLL != null) log.debug("UpdateStylesRoutine(): styles validation complete");
        } else {
          StyleLinkedList stylealreadyprocessed = null;
          if ((EditStyleUI.instance.editedstyle != null) && EditStyleUI.instance.isVisible()) {
            EditStyleUI.instance.editedstyle.update(song, EditStyleUI.instance.editedstyle.get_sindex());
            stylealreadyprocessed = EditStyleUI.instance.editedstyle;
          }
          StyleLinkedList stiter = masterstylelist;
          int styleindex = 0;
          while (stiter != null) {
            if (stylealreadyprocessed != stiter) stiter.update(song, styleindex);
            stiter = stiter.next;
            styleindex++;
          }
        }
      } catch (Exception e) { log.error("UpdateStylesRoutine(): error", e); }
      stylesdirtybit--;
    }

//    public SongLinkedList NewGetSongPtrLC(String uniquesongid) {
//      SongLinkedList iter = SongLL;
//      while (iter != null) {
//        if (iter.uniquesongid.toLowerCase().equals(uniquesongid)) return iter;
//        iter = iter.next;
//      }
//      return null;
//    }
    
    public SongLinkedList NewGetSongPtr(long uniquesongid) {
      return ((SongLinkedList)songmap.get(new Long(uniquesongid)));
//      SongLinkedList iter = SongLL;
//      while (iter != null) {
//        if (iter.uniquesongid == uniquesongid) return iter;
//        iter = iter.next;
//      }
//      return null;
    }

    public SongLinkedList OldGetSongPtr(String uniquesongid) {
        return ((SongLinkedList)uniquesongstring_songmap.get(uniquesongid));   
    }

    public void UpdateSongStyleRoutine(SongLinkedList addedsong) {
      StyleLinkedList stiter = masterstylelist;
      int styleindex = 0;
      while (stiter != null) {
        stiter.update(addedsong, styleindex);
        stiter = stiter.next;
        styleindex++;
      }
    }

    public void addStyleKeyword(StyleLinkedList style, String keyword) {
      if (keyword.equals("")) return;
      style.insertKeyword(keyword);
    }

    public void addStyleSongKeyword(StyleLinkedList style, SongLinkedList song) {
      if (song == null) return;
      style.insertSong(song);
    }

    public void addStyleExcludeKeyword(StyleLinkedList style, String keyword) {
      if (keyword.equals("")) return;
      style.insertExcludeKeyword(keyword);
    }

    public void addStyleExcludeSongKeyword(StyleLinkedList style, SongLinkedList song) {
      if (song == null) return;
      style.insertExcludeSong(song);
    }
    
    
    
    
    /////////////////
    // ALBUM COVER //
    /////////////////
    
    public HashMap albumcover_filenames = new HashMap();
    public static String getAlbumID(String artist, String album) {
        if ((artist == null) || artist.equals("")) return "various - " + album.toLowerCase();
        return artist.toLowerCase() + " - " + album.toLowerCase();
    }
    public ImageSet getAlbumCoverImageSet(SongLinkedList song) {
        if (song.isCompilationAlbum())
            return (ImageSet)albumcover_filenames.get(getAlbumID("Various", song.getAlbum()));
        return (ImageSet)albumcover_filenames.get(getAlbumID(song.getArtist(), song.getAlbum()));
     }
    public void removeAlbumCoverImageSet(String artist, String album) {
        if (SongLinkedList.isCompilationAlbum(artist,album))
            albumcover_filenames.remove(getAlbumID("Various", album));
        else
            albumcover_filenames.remove(getAlbumID(artist, album));        
    }
    
    public void renameAlbum(SongLinkedList song, String old_album) {
//        if (!old_album.equalsIgnoreCase(song.getAlbum())) {
	        String old_id1;
	        String old_id2;
	        String new_id;
	        old_id1 = getAlbumID("Various", old_album);
	        old_id2 = getAlbumID(song.getArtist(), old_album);
	        if (song.isCompilationAlbum()) {
	            new_id = getAlbumID("Various", song.getAlbum());
	        } else {
	            new_id = getAlbumID(song.getArtist(), song.getAlbum());
	        }
	        if (!albumcover_filenames.containsKey(new_id)) {
	            if (albumcover_filenames.containsKey(old_id2))
	                albumcover_filenames.put(new_id, albumcover_filenames.get(old_id2));
	            else
	                albumcover_filenames.put(new_id, albumcover_filenames.get(old_id1));
	        }
	        removeAlbumIfEmpty(old_album, old_id1);
	        removeAlbumIfEmpty(old_album, old_id2);
//        }
    }
    public void changeAlbumCoverDriveLetter(SongLinkedList song, String driveletter) {
        ImageSet imageset = getAlbumCoverImageSet(song);
        if (imageset != null) {
            String[] images = imageset.getFiles();
            for (int i = 0; i < images.length; ++i) {
                images[i] = driveletter + images[i].substring(1, images[i].length());
            }
            imageset.setFiles(images);
            String thumbnail = imageset.getThumbnailFilename();
            if (thumbnail != null) {
                imageset.setThumbnailFilename(driveletter + thumbnail.substring(1, thumbnail.length()));
            }
        }
    }
    public void removeAlbumIfEmpty(String album, SongLinkedList song) {
        // ugly but the way isCompilationAlbum works, don't know if the last song of a comp
        // being deleted is comp or not so have to try to delete both ids
        removeAlbumIfEmpty(album, getAlbumID("Various", song.getAlbum()));
        removeAlbumIfEmpty(album, getAlbumID(song.getArtist(), song.getAlbum()));
    }
    public void removeAlbumIfEmpty(String album, String id) {
        SongLinkedList iter = SongLL;
        boolean found = false;
        while ((iter != null) && !found) {
            if (iter.getAlbum().equalsIgnoreCase(album)) found = true;
            iter = iter.next;
        }
        if (!found) {
            albumcover_filenames.remove(id);
        }        
    }
    public void setAlbumCoverFilename(SongLinkedList song, ImageSet imageset) {
        if (song.isCompilationAlbum())
            albumcover_filenames.put(getAlbumID("Various", song.getAlbum()), imageset);
        else albumcover_filenames.put(getAlbumID(song.getArtist(), song.getAlbum()), imageset);
        song.hasAlbumCover = null;
        }
    public ImageSet getAlbumCoverImageSet(String artist, String album) {
        if (SongLinkedList.isCompilationAlbum(artist,album))
            return (ImageSet)albumcover_filenames.get(getAlbumID("Various", album));
        return (ImageSet)albumcover_filenames.get(getAlbumID(artist, album));
     }
    public void setAlbumCoverFilename(String artist, String album, ImageSet imageset) {
        if (SongLinkedList.isCompilationAlbum(artist,album))
            albumcover_filenames.put(getAlbumID("Various", album), imageset);
        else albumcover_filenames.put(getAlbumID(artist, album), imageset);
        SongLinkedList iter = SongLL;
        while (iter != null) {
            if (iter.getArtist().equals(artist) && iter.getAlbum().equals(album)) {
                iter.hasAlbumCover = null;
            }
            iter = iter.next;
        }
        }
    
    
    public StyleLinkedList getStyle(String styleName) {
        StyleLinkedList siter = masterstylelist;
        while (siter != null) {
            if (StringUtil.areStyleNamesEqual(siter.getName(), styleName))
                return siter;
            siter = siter.next;
        }
        return null;
    }
}
