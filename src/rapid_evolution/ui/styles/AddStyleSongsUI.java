package rapid_evolution.ui.styles;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JButton;
import rapid_evolution.RapidEvolution;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.util.Vector;
import rapid_evolution.SearchParser;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import java.awt.event.KeyListener;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.REListCellRenderer;
import rapid_evolution.ui.REList;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AddStyleSongsUI extends REDialog implements ActionListener {
    public AddStyleSongsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddStyleSongsUI instance = null;
    public REList addsongincludelist = new REList();
    public JButton addstyleincludebutton = new REButton();
    public JButton addstyleincludesong = new REButton();
    public JButton addstyleincludeokbutton = new REButton();
    public JTextField addstyleincludesearchfield = new RETextField();
    public Vector addstyleincludesongvectorresults = new Vector();
    public Vector addstyleincludesongvector = null;

    private void setupDialog() {
        addsongincludelist.setModel(new DefaultListModel());
        addstyleincludesearchfield.addKeyListener(new AddStyleIncludeSongKeyListener());
        addsongincludelist.addMouseListener(new AddSongIncludeListener());
        addsongincludelist.addMouseListener(new AddStyleIncludeSongMouseHandler());
        KeyListener[] listeners = addsongincludelist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) addsongincludelist.removeKeyListener(listeners[i]);
        addsongincludelist.addKeyListener(new AddIncludeSongsKeyListener());
    }

    public void ProcessAddIncludeStyle() {
      int[] indices = new int[addsongincludelist.getSelectedIndices().length];
      for (int i = 0; i < indices.length; ++i) indices[i] = addsongincludelist.getSelectedIndices()[i];
      for (int i = 0; i < indices.length; ++i) {
//        addstyleincludesongvectorresults.add(addstyleincludesongvector.get(indices[i]));
        SongLinkedList siter = (SongLinkedList)addstyleincludesongvector.get(indices[i]);
        SongDB.instance.addStyleSongKeyword(EditStyleUI.instance.editedstyle, siter);
      }
      setVisible(false);
    }

    class AddSongIncludeListener extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          ProcessAddIncludeStyle();
        }
      }
    }

    class AddStyleIncludeSongKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          DefaultListModel dlm = (DefaultListModel) addsongincludelist.getModel();
          dlm.removeAllElements();
          SearchParser include = new SearchParser(addstyleincludesearchfield.getText());
          addstyleincludesongvector = new Vector();
          addstyleincludesongvectorresults.removeAllElements();
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
            if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
              dlm.addElement(iter.getSongId());
              addstyleincludesongvector.add(iter);
            }
            iter = iter.next;
          }
        }
      }
    }

    class AddStyleIncludeSongMouseHandler extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          int index = addsongincludelist.getSelectedIndex();
          addstyleincludesongvectorresults.add(addstyleincludesongvector.get(index));
          setVisible(false);
        }
      }
    }

    private void setupActionListeners() {
       addstyleincludeokbutton.addActionListener(this);
       addstyleincludesong.addActionListener(this);
       addstyleincludebutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addstyleincludebutton) {
         DefaultListModel dlm = (DefaultListModel) addsongincludelist.getModel();
         dlm.removeAllElements();
         SearchParser include = new SearchParser(addstyleincludesearchfield.getText());
         addstyleincludesongvector = new Vector();
         addstyleincludesongvectorresults.removeAllElements();
         SongLinkedList iter = SongDB.instance.SongLL;
         while (iter != null) {
           if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
             dlm.addElement(iter.getSongId());
             addstyleincludesongvector.add(iter);
           }
           iter = iter.next;
         }
      } else if (ae.getSource() == addstyleincludesong) {
          CancelAddStyleSongs();
      } else if (ae.getSource() == addstyleincludeokbutton) {
        ProcessAddIncludeStyle();
      }
    }

    public void CancelAddStyleSongs() {
        setVisible(false);
    }

    public boolean PreDisplay() {
      addstyleincludesongvector = new Vector();
      DefaultListModel dlm = (DefaultListModel) addsongincludelist.getModel();
      dlm.removeAllElements();
      return true;
    }

    public void PostDisplay() {
      addstyleincludesearchfield.requestFocus();
    }
}
