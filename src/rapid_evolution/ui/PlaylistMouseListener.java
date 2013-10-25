package rapid_evolution.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import rapid_evolution.SongLinkedList;

import com.mixshare.rapid_evolution.audio.player.AudioPlayerFactory;

import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.ui.main.SearchPane;

public class PlaylistMouseListener extends MouseAdapter {

    private static Logger log = Logger.getLogger(PlaylistMouseListener.class);
        
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
            int row = SearchPane.instance.searchtable.rowAtPoint(e.getPoint());
            if (row >= 0) {
                AudioPlayer.playSongInPlaylist(row);
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            int row = SearchPane.instance.searchtable.rowAtPoint(e.getPoint());
            if (row >= 0) {
                EditSongUI.instance.EditSong((SongLinkedList)AudioPlayer.songsplaying.get(row));
            }            
        }
    }
}
