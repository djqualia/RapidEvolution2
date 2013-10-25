package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.util.OSHelper;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class PlayListUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(PlayListUI.class);
    
    public PlayListUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }
    
    
    public static PlayListUI instance = null;
    public REList playlistlist = new REList();
    public JButton playlistokbutton = new REButton();

    private void setupDialog() {
        playlistlist.setModel(new DefaultListModel());
        playlistlist.addMouseListener(new PlaylistMouseListener());
    }

    private void setupActionListeners() {
        playlistokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == playlistokbutton) setVisible(false);
    }

    public void MakePlaylist(Vector songs) {
      try {
        FileWriter outputstream = new FileWriter(OSHelper.getWorkingDirectory() + "/" + "playlist.m3u");
        BufferedWriter outputbuffer = new BufferedWriter(outputstream);
        outputbuffer.write("#EXTM3U"); outputbuffer.newLine();
        for (int i = 0; i < songs.size(); ++i) {
          SongLinkedList song = (SongLinkedList)songs.get(i);
          if (!song.getFileName().equals("")) outputbuffer.write(song.getRealFileName()); outputbuffer.newLine();
        }
        outputbuffer.close();
        outputstream.close();

        Process p = null;
        boolean onmac = false;
        if (RapidEvolution.instance.osName.toLowerCase().equals("mac os x")) onmac = true;
        if (!onmac && ((OptionsUI.instance.OSMediaPlayer == null) || (OptionsUI.instance.OSMediaPlayer.equals("")))) p = Runtime.getRuntime().exec("playlist.bat");
        else if (((OptionsUI.instance.OSMediaPlayer == null) || (OptionsUI.instance.OSMediaPlayer.equals(""))) || OptionsUI.instance.OSMediaPlayer.toLowerCase().endsWith("itunes.app")) {
          outputstream = new FileWriter("itunes.script");
          outputbuffer = new BufferedWriter(outputstream);
          int count = 0;
          for (int i = 0; i < songs.size(); ++i) {
            SongLinkedList song = (SongLinkedList)songs.get(i);
            if (!song.getFileName().equals("")) {
              count++;
              outputbuffer.write("set unixFile" + String.valueOf(count) + " to \"" + song.getRealFileName() + "\""); outputbuffer.newLine();
              outputbuffer.write("set macFile" + String.valueOf(count) + " to POSIX file unixFile" + String.valueOf(count)); outputbuffer.newLine();
              outputbuffer.write("set fileRef" + String.valueOf(count) + " to (macFile" + String.valueOf(count) + " as alias)"); outputbuffer.newLine();
            }
          }

          outputbuffer.write("tell application \"iTunes\""); outputbuffer.newLine();
          outputbuffer.write("\tactivate"); outputbuffer.newLine();
          outputbuffer.write("\tset newPlaylist to make new user playlist"); outputbuffer.newLine();
          outputbuffer.write("\tset name of newPlaylist to (current date) as string"); outputbuffer.newLine();
          count = 0;
          for (int i = 0; i < songs.size(); ++i) {
            SongLinkedList song = (SongLinkedList)songs.get(i);
            if (!song.getFileName().equals("")) {
              count++;
              outputbuffer.write("\tadd fileRef" + String.valueOf(count) + " to newPlaylist"); outputbuffer.newLine();
            }
          }
          outputbuffer.write("\tset the view of browser window 1 to newPlaylist"); outputbuffer.newLine();
          outputbuffer.write("\tplay newPlaylist"); outputbuffer.newLine();
          outputbuffer.write("end tell"); outputbuffer.newLine();
          outputbuffer.close();
          outputstream.close();
          String[] command = new String[2];
          command[0] = "osascript";
          command[1] = "itunes.script";
          p = Runtime.getRuntime().exec(command);
        } else {
          String[] command = new String[2];
          command[0] = OptionsUI.instance.OSMediaPlayer;
          command[1] = OSHelper.getWorkingDirectory() + "/" + "playlist.m3u";
          p = Runtime.getRuntime().exec(command);
        }
      } catch (Exception e) { log.error("MakePlaylist(): error", e); }
    }
}
