package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import rapid_evolution.audio.tags.*;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class ReadTagsProgressUI extends REDialog implements ActionListener {
    public ReadTagsProgressUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ReadTagsProgressUI instance = null;
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
          ReadBatchTags.stop = true;
      }
    }
}