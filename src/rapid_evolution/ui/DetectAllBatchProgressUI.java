package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import rapid_evolution.RapidEvolution;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.REDialog;
import rapid_evolution.audio.DetectAll;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.thread.Task;

public class DetectAllBatchProgressUI extends REDialog implements ActionListener {
    public DetectAllBatchProgressUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static DetectAllBatchProgressUI instance = null;

    public JProgressBar progressbar = new JProgressBar(0, 100);
    public JButton cancelbutton = new REButton();

    private Task task = null;

    private void setupDialog() {
    }

    private void setupActionListeners() {
        cancelbutton.addActionListener(this);
    }
    
    public void setTask(Task task) {
        this.task = task;
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == cancelbutton) {
        setVisible(false);
        task.cancel();
      }
    }
}
