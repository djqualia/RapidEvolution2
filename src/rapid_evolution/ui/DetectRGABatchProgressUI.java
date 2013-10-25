package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import rapid_evolution.audio.DetectBatchRGAsThread;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.thread.Task;

public class DetectRGABatchProgressUI extends REDialog implements ActionListener {
    public DetectRGABatchProgressUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static DetectRGABatchProgressUI instance = null;

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
