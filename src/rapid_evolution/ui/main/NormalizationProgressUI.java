package rapid_evolution.ui.main;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import com.brunchboy.util.swing.relativelayout.AttributeConstraint;
import javax.swing.JDialog;
import com.brunchboy.util.swing.relativelayout.AttributeType;
import com.brunchboy.util.swing.relativelayout.DependencyManager;
import com.brunchboy.util.swing.relativelayout.RelativeLayout;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.adddetectfilethread;
import rapid_evolution.audio.Normalizer;
import rapid_evolution.ui.REDialog;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class NormalizationProgressUI extends REDialog implements ActionListener {
    public NormalizationProgressUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static NormalizationProgressUI instance = null;
    public JProgressBar progressbar2 = new JProgressBar(0, 100);
    public JButton cancelbutton = new REButton();

    private void setupDialog() {
    }


    private void setupActionListeners() {
        cancelbutton.addActionListener(this);
    }

    public Normalizer normalizer;

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == cancelbutton) {
           setVisible(false);
           normalizer.stopnormalizing = true;
      }
    }

    public void PostDisplay() {
      progressbar2.setValue(0);
    }
}
