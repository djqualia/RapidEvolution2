package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import rapid_evolution.RapidEvolution;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.ui.REDialog;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class AddSongKeyProgressUI extends REDialog implements ActionListener {
    public AddSongKeyProgressUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddSongKeyProgressUI instance = null;
    public JProgressBar progressbar2 = new JProgressBar(0, 100);
    public JButton addkeycancelbutton = new REButton();

    private void setupDialog() {
    }

    private void setupActionListeners() {
        addkeycancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addkeycancelbutton) {
          Hide();
          AudioEngine.instance.adddetectingfromfile = false;
      }
    }
}
