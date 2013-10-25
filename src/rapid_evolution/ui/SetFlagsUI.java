package rapid_evolution.ui;

import javax.swing.JDialog;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import rapid_evolution.RapidEvolution;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.util.Vector;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.REDialog;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class SetFlagsUI extends REDialog implements ActionListener {
    public SetFlagsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static SetFlagsUI instance = null;
    public JCheckBox setflaganalogonly = new RECheckBox();
    public JCheckBox setflagdigitalonly = new RECheckBox();
    public JCheckBox setflagdisabled = new RECheckBox();
    public JButton setflagsokbutton = new REButton();
    public JButton setflagscancelbutton = new REButton();

    private void setupDialog() {
            // set flags dialog
    }

    public class SetFlagsThread extends Thread {
      public SetFlagsThread(SongLinkedList[] in_songs, boolean in_analog, boolean in_digital, boolean in_disabled)  { songs = in_songs; analog = in_analog; digital = in_digital; disabled = in_disabled; }
      public SongLinkedList[] songs;
      public boolean analog;
      public boolean digital;
      public boolean disabled;
      public void run()  {
        for (int i = 0; i < songs.length; ++i) {
          SongLinkedList song = songs[i];
          song.setVinylOnly( analog);
          song.setNonVinylOnly( digital);
          song.setDisabled( disabled);
        }
        SearchPane.instance.searchtable.repaint();
        MixoutPane.instance.mixouttable.repaint();
        RootsUI.instance.rootstable.repaint();
        SyncUI.instance.synctable.repaint();
        SuggestedMixesUI.instance.suggestedtable.repaint();
        ExcludeUI.instance.excludetable.repaint();
      }
    }

    private void setupActionListeners() {
       setflagsokbutton.addActionListener(this);
       setflagscancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == setflagsokbutton) {
              new SetFlagsThread(setflagssongs, setflaganalogonly.isSelected(), setflagdigitalonly.isSelected(), setflagdisabled.isSelected()).start();
              setVisible(false);
      } else if (ae.getSource() == setflagscancelbutton) {
              setVisible(false);
      }
    }

    public SongLinkedList[] setflagssongs = null;
    public boolean PreDisplay() {
        setflagssongs = (SongLinkedList[])display_parameter;
      if (setflagssongs == null) setflagssongs = RapidEvolutionUI.getSelectedSearchSongs();
      setflaganalogonly.setSelected(false);
      setflagdigitalonly.setSelected(false);
      setflagdisabled.setSelected(false);
      return true;
    }

}
