package rapid_evolution.audio;

import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.MediaPlayerUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.PlayListUI;

import com.mixshare.rapid_evolution.audio.player.AudioPlayerFactory;
import com.mixshare.rapid_evolution.audio.player.PlayerCallBack;
import com.mixshare.rapid_evolution.audio.player.PlayerInterface;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;

public class AudioPlayer implements PlayerCallBack {
    
    private static Logger log = Logger.getLogger(AudioPlayer.class);
        
    private static PlayerInterface player = null;
    public AudioPlayer() { }

    public static Vector songsplaying = null;
    static private PlayerCallBack callback = new AudioPlayer();
    
    static public void PlaySongs() {

        if (OptionsUI.instance.useosplayer.isSelected()) {
            PlayListUI.instance.MakePlaylist(songsplaying);
            return;
        }

        for (int i = 0; i < songsplaying.size(); ++i) {
            SongLinkedList test = (SongLinkedList)songsplaying.get(i);
            if (!test.getFile().exists()) {
                songsplaying.removeElementAt(i);
                --i;
            }
            ++i;
        }
        if (songsplaying.size() == 0) {
            return;
        }
        
        MediaPlayerUI.instance.Display();
        PopulatePlaylist();

        try {
            if (player != null) player.stop();
            currentsongplaying = 0;
            MediaPlayerUI.instance.songplayingtracker.setValue(0);
            currentsongllplaying = (SongLinkedList)songsplaying.get(0);
            updateDisplay();

            if (log.isDebugEnabled()) log.debug("PlaySongs(): playing file=" + currentsongllplaying.getFileName());
            if (player != null)
                player.close();
            player = AudioPlayerFactory.getPlayer(currentsongllplaying.getFileName(), callback);
            if (player.isFileSupported())
                player.start();
            
        } catch (Exception e) {
            log.error("PlaySongs(): error", e);
        }
    }

    /**
     * Where value is like "4:20"
     */
    public void setTime(String value) {
        if (!MediaPlayerUI.instance.songplayingtracker.getValueIsAdjusting()) {
            songplayingtime.setText(value);
        }
    }        
    
    /**
     * Where value is between 0 and 1
     */
    public void setProgress(double value) {
        notuserplaying = true;
        if (!MediaPlayerUI.instance.songplayingtracker.getValueIsAdjusting()) MediaPlayerUI.instance.songplayingtracker.setValue((int)(value * 100000.0));
        lastProgress = value;
        if (lastProgress != 0.0)
            MediaPlayerUI.instance.songplayingstopbutton.setEnabled(true);
        notuserplaying = false;
    }        
    private double lastProgress;
    
    public void donePlayingSong() {
        if (!stopplayingsongs) {
            new StartPlayingNextSong(false).start();
          }        
    }
    
    public void setIsPlaying(boolean playing) {
        MediaPlayerUI.instance.songplayingplaybutton.setEnabled(!playing);
        if (lastProgress == 0.0) MediaPlayerUI.instance.songplayingstopbutton.setEnabled(playing);
        else MediaPlayerUI.instance.songplayingpausebutton.setEnabled(playing);
        if (playing) {
            setVolume();
        }
    }
    
    private static void setVolume() {
        double percentage = ((double)MediaPlayerUI.instance.songplayingvolume.getValue()) / 100000.0;
        player.setVolume(percentage);        
    }
        
    static public void playSongInPlaylist(int index) {
        new StartPlayingNextSong(index).start();
    }
    
  static public class StartPlayingNextSong extends Thread {
    public StartPlayingNextSong(boolean in_backwards) { backwards = in_backwards; }
    public StartPlayingNextSong(int index) { this.index = index; }
    int index = -1;
    boolean backwards = false;
    public void run() {
        if (songsplaying == null) return;
      try {
          player.stop();
          if (index != -1) {
              currentsongplaying = index;
          } else {
              if (MediaPlayerUI.instance.randomizeplaylist.isSelected()) {
                  Random r = new Random(System.currentTimeMillis());
                  double t = r.nextDouble();
                  double spacing = 1.0 / (double)songsplaying.size();
                  double start = 0.0;
                  double end = spacing;
                  for (int i = 0; i < songsplaying.size(); ++i) {
                      if ((t >= start) && (t < end)) currentsongplaying = i;
                      start+=spacing;
                      end+=spacing;
                  }
              } else if (!backwards) currentsongplaying++;
              else currentsongplaying--;
              if (currentsongplaying >= songsplaying.size()) {
                  currentsongplaying = 0;
              }
              if (currentsongplaying < 0) {
                  currentsongplaying =  songsplaying.size() - 1;
              }          
          }
          
          MediaPlayerUI.instance.songplayingtracker.setValue(0);
          currentsongllplaying = (SongLinkedList)songsplaying.get(currentsongplaying);
          updateDisplay();
                    
          if (player != null)
              player.close();
          player = AudioPlayerFactory.getPlayer(currentsongllplaying.getFileName(), callback);
          if (player.isFileSupported())
              player.start();
          
      } catch (Exception e) {
          log.error("StartPlayingNextSong(): error", e);
          new StartPlayingNextSong(backwards).start();
      }
    }
  }
  



  static public int currentsongplaying = 0;
  static public SongLinkedList currentsongllplaying = null;
  static public JLabel songplayingtime = new RELabel("0:00");
  static public boolean stopplayingsongs = false;

  static public boolean notuserplaying = false;

  static public void close() {
      try {
          if (player != null) {
              player.close();
              player = null;
          }
      } catch (Exception e) {
          log.error("close(): error Exception", e);
      }
  }
  
  static public void play() {
      if (player == null) {
          player = AudioPlayerFactory.getPlayer(currentsongllplaying.getFileName(), callback);          
      }
      if (player.isFileSupported())      
          player.start();
      stopplayingsongs = false;
  }

  static public void pause() {
      player.stop();
  }
  
  static public void stop() {
      player.stop();
      player.setPosition(0.0);
      stopplayingsongs = true;
      player.close();
      player = null;
  }
  
  static public void updateDisplay() {
      MediaPlayerUI.instance.songplayingfield.setText(currentsongllplaying.getSongId());      
  }

  static public class SongPlayingTracker implements ChangeListener {
     public void stateChanged(ChangeEvent event) {       
         if (event.getSource() == MediaPlayerUI.instance.songplayingtracker) {
             if (player != null) {
                 if (notuserplaying && !MediaPlayerUI.instance.songplayingtracker.getValueIsAdjusting()) return;
                 double percentage = ((double)MediaPlayerUI.instance.songplayingtracker.getValue()) / 100000.0;
                 String text = StringUtil.seconds_to_time((int)(player.getTotalTime() * percentage));
                 songplayingtime.setText(text);       
                 if (MediaPlayerUI.instance.songplayingtracker.getValueIsAdjusting()) return;
                 player.setPosition(percentage);           
             } else {
                 songplayingtime.setText("");
             }
         } else if (event.getSource() == MediaPlayerUI.instance.songplayingvolume) {
             setVolume();
         }
     }
   }

  static public void PopulatePlaylist() {
    DefaultListModel dlm = (DefaultListModel) PlayListUI.instance.playlistlist.getModel();
    dlm.removeAllElements();
    for (int i = 0; i < songsplaying.size(); ++i) {
      SongLinkedList iter = (SongLinkedList)songsplaying.get(i);
      dlm.addElement(iter.getSongId());
    }
  }
}
