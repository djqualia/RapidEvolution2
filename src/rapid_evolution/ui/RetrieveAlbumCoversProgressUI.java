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
import rapid_evolution.ui.main.*;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class RetrieveAlbumCoversProgressUI extends REDialog implements ActionListener {
    public RetrieveAlbumCoversProgressUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static RetrieveAlbumCoversProgressUI instance = null;
    public JProgressBar progressbar = new JProgressBar(0, 100);
    public JButton cancelbutton = new REButton();

    private void setupDialog() {
    }

    private void setupActionListeners() {
        cancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == cancelbutton) {
          Hide();
          SearchListMouse.instance.stopAlbumCoverRetrieval = true;
      }
    }
}
