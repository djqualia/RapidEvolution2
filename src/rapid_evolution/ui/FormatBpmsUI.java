package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JTextField;

import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.audio.Bpm;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class FormatBpmsUI extends REDialog implements ActionListener {
    public FormatBpmsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static FormatBpmsUI instance = null;
    public JTextField numdecimalplaces = new  RETextField();
    public JButton formatbpmsokbutton = new  REButton();
    public JTextField scale = new RETextField();
    
    private void setupDialog() {
            // numgeneratedlg dialog
            numdecimalplaces.addKeyListener(new NumDecimalListener());
    }

    class NumDecimalListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          setVisible(false);
          formatbpmsokproc();
        } else if (e.getKeyCode() == e.VK_ESCAPE) {
          setVisible(false);
        }
      }
    }

    public boolean PreDisplay() {
      if (numdecimalplaces.getText().equals("")) numdecimalplaces.setText("1");
      if (scale.getText().equals("")) scale.setText("1.0");
      return true;
    }

    public void PostDisplay() {
      songs = RapidEvolutionUI.getSelectedSearchSongs();
    }

    SongLinkedList[] songs = null;
    static int decimalplaces = 1;
    static double scalefactor = 1.0;

    void formatbpmsokproc() {
      int num = 1;
      try {
        num = Integer.parseInt(numdecimalplaces.getText());
      } catch (Exception e) { }
      double scale_double = 1.0;
      try {
        scale_double = Double.parseDouble(scale.getText());
      } catch (Exception e) { }
      decimalplaces = num;
      scalefactor = scale_double;
      new Thread() {
        int num = decimalplaces;
        public void run() {
          for (int i = 0; i < songs.length; ++i) {
            SongLinkedList song = songs[i];
            song.setStartbpm( (float) Bpm.round(song.getStartbpm() * scalefactor, num));
            song.setEndbpm( (float) Bpm.round(song.getEndbpm() * scalefactor, num));
            SongDB.instance.UpdateSong(song, new OldSongValues(song));
          }
        }
      }.start();

      setVisible(false);
    }

    private void setupActionListeners() {
        formatbpmsokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == formatbpmsokbutton) {
              formatbpmsokproc();
      }
    }
}
