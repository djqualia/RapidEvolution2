package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTextField;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.ui.main.SearchPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class GeneratePlaylistUI extends REDialog implements ActionListener {
    public GeneratePlaylistUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static GeneratePlaylistUI instance = null;
    public JTextField numgeneratefield = new  RETextField();
    public JButton numgenerateokbutton = new REButton();
    public JButton numgeneratecancelbutton = new REButton();

    private void setupDialog() {
            // numgeneratedlg dialog
            numgeneratefield.addKeyListener(new NumGenerateListener());
    }

    class NumGenerateListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          setVisible(false);
          numgenerateokproc();
        } else if (e.getKeyCode() == e.VK_ESCAPE) {
          setVisible(false);
        }
      }
    }

    void numgenerateokproc() {
      int num = 0;
      try {
        num = Integer.parseInt(numgeneratefield.getText());
      } catch (Exception e) { return; }
      SongLinkedList searchsong = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(SearchPane.instance.searchtable.getSelectedRow(), SearchPane.instance.searchcolumnconfig.num_columns);
      SongLinkedList iter = SongDB.instance.SongLL;
      Vector rankarray = new Vector();
      Vector songarray = new Vector();
      songarray.add(searchsong);
      rankarray.add(new Double(1.0));
      while (iter != null) {
        if (iter != searchsong) {
            float percentage = searchsong.getStyleSimilarity(iter).getFloatValue();
          boolean notdone = true;
          int count = 0;
          while (notdone && (count < rankarray.size())) {
            Double val = (Double)rankarray.get(count);
            if (val.doubleValue() < percentage) {
              rankarray.insertElementAt(new Double(percentage), count);
              songarray.insertElementAt(iter, count);
              notdone = false;
            }
            count++;
          }
          if (notdone) {
            rankarray.add(new Double(percentage));
            songarray.add(iter);
          }
        }
        iter = iter.next;
      }
      Vector newsongsplaying = new Vector();
      int count = 0;
      while ((newsongsplaying.size() < num) && (count < songarray.size())) {
        SongLinkedList s = (SongLinkedList)songarray.get((int)count);
        if (!s.getFileName().equals("")) newsongsplaying.add(songarray.get((int)count));
        count++;
      }
      AudioPlayer.songsplaying = newsongsplaying;
      AudioPlayer.PlaySongs();
      setVisible(false);
    }

    private void setupActionListeners() {
        numgeneratecancelbutton.addActionListener(this);
        numgenerateokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == numgeneratecancelbutton) {
              setVisible(false);
      } else if (ae.getSource() == numgenerateokbutton) {
              numgenerateokproc();
      }
    }
    
    public void Display() {
        super.Display();
        numgeneratefield.requestFocus();
    }
}
