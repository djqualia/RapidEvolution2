package rapid_evolution.ui.styles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import rapid_evolution.SearchParser;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.REList;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;


public class ExcludeStyleSongsUI extends REDialog implements ActionListener {
    public ExcludeStyleSongsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ExcludeStyleSongsUI instance = null;
    public REList addsongexcludelist = new REList();
    public JTextField addstyleexcludesearchfield = new RETextField();
    public JButton addstyleexcludebutton = new REButton();
    public JButton addstyleexcludesong = new REButton();
    public JButton addstyleexcludeokbutton = new REButton();
    public Vector addstyleexcludesongvector = null;
    public Vector addstyleexcludesongvectorresults = new Vector();
    private void setupDialog() {
        addsongexcludelist.setModel(new DefaultListModel());
        addsongexcludelist.addMouseListener(new AddStyleExcludeSongMouseHandler());
        addstyleexcludesearchfield.addKeyListener(new AddStyleExcludeSongKeyListener());
        addsongexcludelist.addMouseListener(new AddSongExcludeListener());
        KeyListener[] listeners = addsongexcludelist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) addsongexcludelist.removeKeyListener(listeners[i]);
        addsongexcludelist.addKeyListener(new AddExcludeSongsKeyListener());
    }

    public void ProcessAddExcludeStyle() {
      int[] indices = new int[addsongexcludelist.getSelectedIndices().length];
      for (int i = 0; i < indices.length; ++i) indices[i] = addsongexcludelist.getSelectedIndices()[i];
      for (int i = 0; i < indices.length; ++i) {
//        addstyleexcludesongvectorresults.add(addstyleexcludesongvector.get(indices[i]));
        SongLinkedList siter = (SongLinkedList)addstyleexcludesongvector.get(indices[i]);
        SongDB.instance.addStyleExcludeSongKeyword(EditStyleUI.instance.editedstyle, siter);
      }
      setVisible(false);
    }

    class AddSongExcludeListener extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          ProcessAddExcludeStyle();
        }
      }
    }

    class AddStyleExcludeSongMouseHandler extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          int index = addsongexcludelist.getSelectedIndex();
          addstyleexcludesongvectorresults.add(addstyleexcludesongvector.get(index));
          setVisible(false);
        }
      }
    }

    class AddStyleExcludeSongKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          DefaultListModel dlm = (DefaultListModel) addsongexcludelist.getModel();
          dlm.removeAllElements();
          SearchParser include = new SearchParser(addstyleexcludesearchfield.getText());
          addstyleexcludesongvector = new Vector();
          addstyleexcludesongvectorresults.removeAllElements();
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
            if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
              dlm.addElement(iter.getSongId());
              addstyleexcludesongvector.add(iter);
            }
            iter = iter.next;
          }
        }
      }
    }

    private void setupActionListeners() {
       addstyleexcludeokbutton.addActionListener(this);
       addstyleexcludesong.addActionListener(this);
       addstyleexcludebutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addstyleexcludebutton) {
        DefaultListModel dlm = (DefaultListModel) addsongexcludelist.getModel();
        dlm.removeAllElements();
        SearchParser include = new SearchParser(addstyleexcludesearchfield.getText());
        addstyleexcludesongvector = new Vector();
        addstyleexcludesongvectorresults.removeAllElements();
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
          if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
            dlm.addElement(iter.getSongId());
            addstyleexcludesongvector.add(iter);
          }
          iter = iter.next;
        }
      } else if (ae.getSource() == addstyleexcludeokbutton) {
        ProcessAddExcludeStyle();
      } else if (ae.getSource() == addstyleexcludesong) {
        CancelAddExcludeSongs();
      }
    }

    public void CancelAddExcludeSongs() {
        setVisible(false);
    }

    public boolean PreDisplay() {
      addstyleexcludesongvector = new Vector();
      DefaultListModel dlm = (DefaultListModel) addsongexcludelist.getModel();
      dlm.removeAllElements();
      return true;
    }

    public void PostDisplay() {
      addstyleexcludesearchfield.requestFocus();
    }

}
