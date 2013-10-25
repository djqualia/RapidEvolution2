package rapid_evolution.ui;

import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JDialog;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import rapid_evolution.RapidEvolution;
import javax.swing.JTextField;
import rapid_evolution.SearchParser;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import rapid_evolution.SongLinkedList;
import java.util.Vector;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.REList;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class SelectMissingSongUI extends REDialog implements ActionListener {
    public SelectMissingSongUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static SelectMissingSongUI instance = null;

    public JButton selectmissingsongsearchbutton = new REButton();
    public JButton selectmissingsongcancel = new REButton();
    public REList selectmissingsonglist = new REList();
    public JButton selectmissingsongsokbutton = new REButton();
    public JTextField selectmissingsongsfield = new RETextField();

    private void setupDialog() {
        selectmissingsonglist.setModel(new DefaultListModel());
        selectmissingsongsfield.addKeyListener(new SelectMissingSongKeyListener());
    }

    public Vector selectmixxingsongsvector = new Vector();
    class SelectMissingSongKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          DefaultListModel dlm = (DefaultListModel) selectmissingsonglist.getModel();
          dlm.removeAllElements();
          SearchParser include = new SearchParser(selectmissingsongsfield.getText());
          selectmixxingsongsvector.removeAllElements();
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
            if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
              dlm.addElement(iter.getSongId());
              selectmixxingsongsvector.add(iter);
            }
            iter = iter.next;
          }
        }
      }
    }

    private void setupActionListeners() {
       selectmissingsongsearchbutton.addActionListener(this);
       selectmissingsongcancel.addActionListener(this);
       selectmissingsongsokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == selectmissingsongcancel) {
        setVisible(false);
      } else if (ae.getSource() == selectmissingsongsearchbutton) {
         DefaultListModel dlm = (DefaultListModel) selectmissingsonglist.getModel();
         dlm.removeAllElements();
         SearchParser include = new SearchParser(selectmissingsongsfield.getText());
         selectmixxingsongsvector.removeAllElements();
         SongLinkedList iter = SongDB.instance.SongLL;
         while (iter != null) {
           if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
             dlm.addElement(iter.getSongId());
             selectmixxingsongsvector.add(iter);
           }
           iter = iter.next;
         }
      } else if (ae.getSource() == selectmissingsongsokbutton) {
         if (selectmissingsonglist.getSelectedIndex() >= 0) {
           setVisible(false);
           //bleh
           SongTrailUI.instance.ReplaceMissingSong(selectmissingsonglist.getSelectedIndex());
         }
      }
    }

}
