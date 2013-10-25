package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import rapid_evolution.audio.AudioPlayer;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.slider.MediaSlider;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class MediaPlayerUI extends REDialog implements ActionListener {
    private static Logger log = Logger.getLogger(MediaPlayerUI.class);
    public MediaPlayerUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
        playlist_ui = new PlayListUI("media_playlist_dialog");
    }

    public static MediaPlayerUI instance = null;
    public JTextField songplayingfield = new RETextField();
    public JSlider songplayingtracker = new MediaSlider(JSlider.HORIZONTAL, 0, 100000, 0, false);
    public JSlider songplayingvolume = new MediaSlider(JSlider.HORIZONTAL, 0, 100000, 0, false);
    public static JCheckBox randomizeplaylist = new RECheckBox();
    public JButton songplayingokbutton = new REButton();
    public JButton songplayingbackbutton = new REButton();
    public JButton songplayingplaybutton = new REButton();
    public JButton songplayingpausebutton = new REButton();
    public JButton songplayingstopbutton = new REButton();
    public JButton songplayingnextbutton = new REButton();

    public PlayListUI playlist_ui;

    private void setupDialog() {
        songplayingvolume.setValue(100000);
    }

    private void setupActionListeners() {
        songplayingtracker.addChangeListener(new AudioPlayer.SongPlayingTracker());
        songplayingbackbutton.addActionListener(this);
        songplayingplaybutton.addActionListener(this);
        songplayingpausebutton.addActionListener(this);
        songplayingokbutton.addActionListener(this);
        songplayingstopbutton.addActionListener(this);
        songplayingnextbutton.addActionListener(this);
        songplayingfield.addMouseListener(new MediaPlayerMouseEditListener());
        songplayingvolume.addChangeListener(new AudioPlayer.SongPlayingTracker());
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == songplayingnextbutton) {
          new AudioPlayer.StartPlayingNextSong(false).start();
      } else if (ae.getSource() == songplayingbackbutton) {
          new AudioPlayer.StartPlayingNextSong(true).start();
      } else if (ae.getSource() == songplayingplaybutton) {
          AudioPlayer.play();
      } else if (ae.getSource() == songplayingpausebutton) {
          AudioPlayer.pause();
      } else if (ae.getSource() == songplayingokbutton) {
            setVisible(false);
      } else if (ae.getSource() == songplayingstopbutton) {
          AudioPlayer.stop();
          songplayingtracker.setValue(0);
      }
    }

}
