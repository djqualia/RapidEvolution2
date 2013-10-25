package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.REDialog;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.button.RERadioButton;

public class KeyFormatUI extends REDialog implements ActionListener {
    public KeyFormatUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static KeyFormatUI instance = null;
    static public JRadioButton csharpoption = new RERadioButton();
    static public JRadioButton dflatoption = new RERadioButton();
    static public JRadioButton dsharpoption = new RERadioButton();
    static public JRadioButton eflatoption = new RERadioButton();
    static public JRadioButton fsharpoption = new RERadioButton();
    static public JRadioButton gflatoption = new RERadioButton();
    static public JRadioButton gsharpoption = new RERadioButton();
    static public JRadioButton aflatoption = new RERadioButton();
    static public JRadioButton asharpoption = new RERadioButton();
    static public JRadioButton bflatoption = new RERadioButton();
    JButton customizekeysbutton = new REButton();

    private void setupDialog() {
            ButtonGroup csharpgroup = new ButtonGroup();
            csharpgroup.add(csharpoption);
            csharpgroup.add(dflatoption);
            ButtonGroup dsharpgroup = new ButtonGroup();
            dsharpgroup.add(dsharpoption);
            dsharpgroup.add(eflatoption);
            ButtonGroup fsharpgroup = new ButtonGroup();
            fsharpgroup.add(fsharpoption);
            fsharpgroup.add(gflatoption);
            ButtonGroup gsharpgroup = new ButtonGroup();
            gsharpgroup.add(gsharpoption);
            gsharpgroup.add(aflatoption);
            ButtonGroup asharpgroup = new ButtonGroup();
            asharpgroup.add(asharpoption);
            asharpgroup.add(bflatoption);


    }

    private void setupActionListeners() {
        customizekeysbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == customizekeysbutton) {
              setVisible(false);
              SongLinkedList siter = SongDB.instance.SongLL;
              while (siter != null) {
                  siter.getKey().invalidate();
                  siter = siter.next;
              }              
      }
    }

    public void PostDisplay() {
      OptionsUI.instance.keyformatcombo.setSelectedIndex(3);
    }
}
