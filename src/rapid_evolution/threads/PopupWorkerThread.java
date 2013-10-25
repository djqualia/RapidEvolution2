package rapid_evolution.threads;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.ImageSet;
import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.ui.BrokenLinkSongsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.RenameFilesUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.main.SearchListMouse;

import com.ibm.iwt.IOptionPane;

public class PopupWorkerThread extends Thread {

    private static Logger log = Logger.getLogger(PopupWorkerThread.class);
    
    public static boolean querystopped;
    
    public PopupWorkerThread(ActionEvent a) { ae = a; }
  ActionEvent ae = null;
  public void run() {
    if (ae.getSource() == SearchListMouse.instance.assigntracknumbers) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("assign_track_numbers"),
          SkinManager.instance.getDialogMessageTitle("assign_track_numbers"),
          IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      String track = "01";
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      for (int i = 0; i < songs.length; ++i) {
        SongLinkedList song = songs[i];
        OldSongValues old_values = new OldSongValues(song);
        try {
          song.setTrack(track);
          track = StringUtil.track_increment(track);
          SongDB.instance.UpdateSong(song,old_values);
        } catch (Exception e) { }
      }
    }  else if (ae.getSource() == SearchListMouse.instance.queryserver) {
        // query server
        int n = IOptionPane.showConfirmDialog(
                SkinManager.instance.getFrame("main_frame"),
                SkinManager.instance.getDialogMessageText("query_server"),
                SkinManager.instance.getDialogMessageTitle("query_server"),
                IOptionPane.YES_NO_OPTION);
            if (n != 0) {
              return;
            }            
            querystopped = false;            
            SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
            RapidEvolutionUI.instance.queryserverprogress_ui.progressbar.setValue(0); // 0 to 100
            RapidEvolutionUI.instance.queryserverprogress_ui.Display();
            int i = 0;
            while (!querystopped && (i < songs.length)) {
              SongLinkedList song = songs[i];
              OldSongValues old_values = new OldSongValues(song);
              MixshareClient.instance.querySongDetails(song, true);
              SongDB.instance.UpdateSong(song,old_values);              
              int progress = (int)(((float)i + 1) / songs.length * 100.0f);
              RapidEvolutionUI.instance.queryserverprogress_ui.progressbar.setValue(progress);                    
              ++i;
            }
            RapidEvolutionUI.instance.queryserverprogress_ui.Hide();       
            
    } else if (ae.getSource() == SearchListMouse.instance.detectbrokenlinks) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
        Vector broken_songs = new Vector();
        for (int i = 0; i < songs.length; ++i) {
          SongLinkedList song = songs[i];
          String filename = song.getFileName();
          if ((filename != null) && (!filename.equals(""))) {
              File file = new File(filename);
              if (!file.exists())
                  broken_songs.add(song);
          }
        }        
        BrokenLinkSongsUI.instance.songvector = broken_songs;
        BrokenLinkSongsUI.instance.Display();
    }  else if (ae.getSource() == SearchListMouse.instance.parseremixer) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("parse_remix_field"),
          SkinManager.instance.getDialogMessageTitle("parse_remix_field"),
          IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      for (int i = 0; i < songs.length; ++i) {
        SongLinkedList song = songs[i];
        if (song.getRemixer().equals("")) {
            OldSongValues old_values = new OldSongValues(song);
          try {
            String name = song.getSongname();
            String title = StringUtil.parseTitle(name);
            String remix = StringUtil.parseRemix(name);
            song.setSongname(title);
            song.setRemixer(remix);
            SongDB.instance.UpdateSong(song,old_values);
          } catch (Exception e) { }
        }
      }
    } else if (ae.getSource() == SearchListMouse.instance.renamefiles) {
        RenameFilesUI.instance.Display();
    } else if (ae.getSource() == SearchListMouse.instance.resetPlayCount) {
        int n = IOptionPane.showConfirmDialog(
                SkinManager.instance.getFrame("main_frame"),
                SkinManager.instance.getDialogMessageText("reset_play_count"),
                SkinManager.instance.getDialogMessageTitle("reset_play_count"),
                IOptionPane.YES_NO_OPTION);
            if (n != 0) {
              return;
            }
            SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
            for (int i = 0; i < songs.length; ++i) {
              SongLinkedList song = songs[i];
              OldSongValues old_values = new OldSongValues(song);
              try {
            	  song.setTimesPlayed(0);
            	  SongDB.instance.UpdateSong(song,old_values);
              } catch (Exception e) { }
            }    	
    } else if (ae.getSource() == SearchListMouse.instance.removefileextensions) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("remove_file_extensions"),
          SkinManager.instance.getDialogMessageTitle("remove_file_extensions"),
          IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      for (int i = 0; i < songs.length; ++i) {
        SongLinkedList song = songs[i];
        OldSongValues old_values = new OldSongValues(song);
        try {
          if (song.getSongname().length() > 4) {
            if (song.getSongname().charAt(song.getSongname().length() - 4) == '.') song.setSongname(song.getSongname().substring(0, song.getSongname().length() - 4));
          }
          SongDB.instance.UpdateSong(song,old_values);
        } catch (Exception e) { }
      }
    } else if (ae.getSource() == SearchListMouse.instance.searchformissingfiles) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
       try {
         JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
         if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
         fc.setDialogTitle(SkinManager.instance.getMessageText("find_songs_search_location"));
         if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
         fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
         fc.setMultiSelectionEnabled(true);
         int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
         File tmp = fc.getSelectedFile();
         if (tmp != null) RapidEvolutionUI.instance.previousfilepath = FileUtil.getDirectoryFromFilename(tmp.getAbsolutePath());         
         if (returnVal == JFileChooser.APPROVE_OPTION) {             
           File[] files = fc.getSelectedFiles();
           files = FileUtil.sortfiles(files);
          Vector searchfiles = new Vector();
           int i = 0;
           while (i < files.length) {
             if (!files[i].isDirectory()) searchfiles.add(files[i]);
             else {
               FileUtil.RecurseFileTree2(files[i], searchfiles);
             }
             ++i;
           }
           for (int j = 0; j < songs.length; ++j) {
             SongLinkedList song = songs[j];
             if (!song.getFileName().equals("")) {
               File sfile = song.getFile();
               String sname = sfile.getName();
               for (i = 0; i < searchfiles.size(); ++i) {
                 File file =(File)searchfiles.get(i);
                 if (sname.equals(file.getName())) song.setFilename(file.getAbsolutePath());
               }
             }
           }
         }
       } catch (Exception e) { log.error("run(): error", e); }
    } else if (ae.getSource() == SearchListMouse.instance.correcttracknums) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("format_track_numbers"),
          SkinManager.instance.getDialogMessageTitle("format_track_numbers"),
          IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
//        if ((songs.size() >= 10) && (songs.size() <= 99)) {
        for (int i = 0; i < songs.length; ++i) {
          SongLinkedList song = songs[i];
          OldSongValues old_values = new OldSongValues(song);
          try {
            if (song.getTrack().length() == 1) {
              int num = Integer.parseInt(song.getTrack());
              song.setTrack(new String("0" + song.getTrack()));
              SongDB.instance.UpdateSong(song,old_values);
            } else if ((song.getTrack().indexOf("/")) == 1) {
              song.setTrack(new String("0" + song.getTrack()));
              SongDB.instance.UpdateSong(song,old_values);
            }
          } catch (Exception e) { }
        }
//        }
    } else if (ae.getSource() == SearchListMouse.instance.makelower) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("make_lower_case"),
          SkinManager.instance.getDialogMessageTitle("make_lower_case"),
          IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      for (int i = 0; i < songs.length; ++i) {
        SongLinkedList song = songs[i];
        OldSongValues old_values = new OldSongValues(song);
        song.setArtist( song.getArtist().toLowerCase());
        song.setAlbum(song.getAlbum().toLowerCase());
        song.setTrack(song.getTrack().toLowerCase());
        song.setSongname(song.getSongname().toLowerCase());
        song.setRemixer(song.getRemixer().toLowerCase());
        if (song.getUser1() != null) song.setUser1(song.getUser1().toLowerCase());
        if (song.getUser2() != null) song.setUser2(song.getUser2().toLowerCase());
        if (song.getUser3() != null) song.setUser3(song.getUser3().toLowerCase());
        if (song.getUser4() != null) song.setUser4(song.getUser4().toLowerCase());
        SongDB.instance.UpdateSong(song,old_values);
      }
    } else if (ae.getSource() == SearchListMouse.instance.changedriveletter) {
      String driveletter = (String)IOptionPane.showInputDialog(
                          SkinManager.instance.getFrame("main_frame"),
                          SkinManager.instance.getDialogMessageText("change_drive_letter"),
                         SkinManager.instance.getDialogMessageTitle("change_drive_letter"),
                         IOptionPane.PLAIN_MESSAGE,
                          "");
      if ((driveletter != null) && (driveletter.length() == 1)) {
          SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
        for (int i = 0; i < songs.length; ++i) {
          SongLinkedList song = songs[i];
          if (!song.getFileName().equals("")) {
            song.setFilename(driveletter + song.getFileName().substring(1, song.getFileName().length()));
          }
          SongDB.instance.changeAlbumCoverDriveLetter(song, driveletter);
        }
      }
    } else if (ae.getSource() == SearchListMouse.instance.makeproper) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("make_proper_case"),
         SkinManager.instance.getDialogMessageTitle("make_proper_case"),
         IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      for (int i = 0; i < songs.length; ++i) {
        SongLinkedList song = songs[i];
        OldSongValues old_values = new OldSongValues(song);
        song.setArtist(StringUtil.makeProper(song.getArtist()));
        song.setAlbum(StringUtil.makeProper(song.getAlbum()));
        song.setTrack(StringUtil.makeProper(song.getTrack()));
        song.setSongname(StringUtil.makeProper(song.getSongname()));
        song.setRemixer(StringUtil.makeProper(song.getRemixer()));
        if (song.getUser1() != null) song.setUser1(StringUtil.makeProper(song.getUser1()));
        if (song.getUser2() != null) song.setUser2(StringUtil.makeProper(song.getUser2()));
        if (song.getUser3() != null) song.setUser3(StringUtil.makeProper(song.getUser3()));
        if (song.getUser4() != null) song.setUser4(StringUtil.makeProper(song.getUser4()));
        SongDB.instance.UpdateSong(song,old_values);
      }
    } else if (ae.getSource() == SearchListMouse.instance.makeupper) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("make_upper_case"),
         SkinManager.instance.getDialogMessageTitle("make_upper_case"),
         IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      for (int i = 0; i < songs.length; ++i) {
        SongLinkedList song = songs[i];
        OldSongValues old_values = new OldSongValues(song);
        song.setArtist(song.getArtist().toUpperCase());
        song.setAlbum(song.getAlbum().toUpperCase());
        song.setTrack(song.getTrack().toUpperCase());
        song.setSongname(song.getSongname().toUpperCase());
        song.setRemixer(song.getRemixer().toUpperCase());
        if (song.getUser1() != null) song.setUser1(song.getUser1().toUpperCase());
        if (song.getUser2() != null) song.setUser2(song.getUser2().toUpperCase());
        if (song.getUser3() != null) song.setUser3(song.getUser3().toUpperCase());
        if (song.getUser4() != null) song.setUser4(song.getUser4().toUpperCase());
        SongDB.instance.UpdateSong(song,old_values);
      }
    } else if (ae.getSource() == SearchListMouse.instance.correctkeyformat) {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("correct_key_format"),
         SkinManager.instance.getDialogMessageTitle("correct_key_format"),
         IOptionPane.YES_NO_OPTION);
      if (n != 0) {
        return;
      }
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      for (int i = 0; i < songs.length; ++i) {
        SongLinkedList song = songs[i];
        song.setStartkey(song.getStartKey());
        song.setEndkey(song.getEndKey());
      }
    }
  }
  
  
}
