package rapid_evolution.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import rapid_evolution.audio.AudioPlayer;

public class MediaPlayerMouseEditListener extends MouseAdapter {

    private static Logger log = Logger.getLogger(MediaPlayerMouseEditListener.class);
        
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            EditSongUI.instance.EditSong(AudioPlayer.currentsongllplaying);
        }
    }
}
