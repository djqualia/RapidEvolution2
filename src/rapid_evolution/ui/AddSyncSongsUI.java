package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import javax.swing.ListSelectionModel;
import javax.swing.JDialog;
import javax.swing.DefaultListModel;
import rapid_evolution.RapidEvolution;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextField;
import rapid_evolution.SearchParser;
import java.awt.event.KeyAdapter;
import rapid_evolution.SongLinkedList;
import java.util.Vector;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import java.awt.event.KeyListener;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.REList;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class AddSyncSongsUI extends REDialog implements ActionListener {
    public AddSyncSongsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddSyncSongsUI instance = null;

    public REList addsyncsongslist = new REList();
    public JButton addsyncsongssearchbutton = new REButton();
    public JButton addsyncsongsokbutton = new REButton();
    public JTextField addsyncsongssearchfield = new RETextField();
    public JButton addsyncsongscancelbutton = new REButton();

    private void setupDialog() {
        addsyncsongslist.setModel(new DefaultListModel());
        addsyncsongssearchfield.addKeyListener(new AddSyncFieldSongsListener());
        KeyListener[] listeners = addsyncsongslist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) addsyncsongslist.removeKeyListener(listeners[i]);
        addsyncsongslist.addKeyListener(new AddSyncSongsKeyListener());
    }

    public Vector addsyncsongsvector = null;
    class AddSyncFieldSongsListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          DefaultListModel dlm = (DefaultListModel) addsyncsongslist.getModel();
          dlm.removeAllElements();
          SearchParser include = new SearchParser(addsyncsongssearchfield.getText());
          addsyncsongsvector = new Vector();
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
            if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
              dlm.addElement(iter.getSongId());
              addsyncsongsvector.add(iter);
            }
            iter = iter.next;
          }

        }
      }
    }

    private void setupActionListeners() {
        addsyncsongscancelbutton.addActionListener(this);
        addsyncsongsokbutton.addActionListener(this);
        addsyncsongssearchbutton.addActionListener(this);
    }

    public void AddSyncSongsOK() {
        int[] indices = new int[addsyncsongslist.getSelectedIndices().length];
        for (int i = 0; i < indices.length; ++i) indices[i] = addsyncsongslist.getSelectedIndices()[i];
        for (int i = 0; i < indices.length; ++i) {
          SongLinkedList addsong = (SongLinkedList)addsyncsongsvector.get(indices[i]);
          boolean notfound = true;
          for (int j = 0; j < SyncUI.instance.addedsyncsongs.size(); ++j) {
            if (SyncUI.instance.addedsyncsongs.get(j) == addsong) notfound = false;
          }
          if ((notfound) && (addsong != RapidEvolutionUI.instance.currentsong)) SyncUI.instance.addedsyncsongs.add(addsong);
        }
        SyncUI.instance.Redrawsync();
        if (SyncUI.instance.synctable.getRowCount() > 0) SyncUI.instance.syncsongsbutton.setEnabled(true);
        setVisible(false);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addsyncsongsokbutton) {
          AddSyncSongsOK();
      } else if (ae.getSource() == addsyncsongssearchbutton) {
        DefaultListModel dlm = (DefaultListModel) addsyncsongslist.getModel();
        dlm.removeAllElements();
        SearchParser include = new SearchParser(addsyncsongssearchfield.getText());
        addsyncsongsvector = new Vector();
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
          if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
            dlm.addElement(iter.getSongId());
            addsyncsongsvector.add(iter);
          }
          iter = iter.next;
        }
      } else if (ae.getSource() == addsyncsongscancelbutton) {
        CancelAddSyncSongs();
      }
    }

    public void CancelAddSyncSongs() {
        setVisible(false);
    }

    public boolean PreDisplay(Object source) {
      DefaultListModel dlm = (DefaultListModel) addsyncsongslist.getModel();
      dlm.removeAllElements();
      addsyncsongsvector = new Vector();
      SongLinkedList iter = SongDB.instance.SongLL;
      while (iter != null) {
        addsyncsongsvector.add(iter);
        dlm.addElement(iter.getSongId());
        iter = iter.next;
      }
      return true;
    }

    protected void PostDisplay(Object source) {
      addsyncsongssearchfield.requestFocus();
    }

    public class AddSyncSongsKeyListener extends KeyAdapter {
      String quickstrokestartswith = new String("");
      long lastkeystroke = 0;

      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_BACK_SPACE) {
          if (quickstrokestartswith.length() >= 1) quickstrokestartswith = quickstrokestartswith.substring(0, quickstrokestartswith.length() - 1);
          return;
        } else if (e.getKeyCode() == e.VK_SPACE) {
          if (quickstrokestartswith.length() >= 1) quickstrokestartswith += " ";
          return;
        } else if (e.getKeyCode() == e.VK_ENTER) {
          AddSyncSongsOK();
        } else if (e.getKeyCode() == e.VK_ESCAPE) {
          CancelAddSyncSongs();
        }

        char c = Character.toLowerCase(e.getKeyChar());
        if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == ',') || (c == '-') || (c == '_') || (c == '+') || (c == '\'') || (c == ':') || (c == ';') || (c == '\\') || (c == '/') || (c == '[') || (c == ']') || (c == '{') || (c == '}') || (c == '`') || (c == '!') || (c == '@') || (c == '#') || (c == '$') || (c == '%') || (c == '^') || (c == '&') || (c == '*') || (c == '(') || (c == ')') || (c == '|') || (c == '~')) {
          if ((lastkeystroke == 0) || ((System.currentTimeMillis() - lastkeystroke) < 1500)) quickstrokestartswith += c;
          else quickstrokestartswith =  new String("" + c);
          lastkeystroke = System.currentTimeMillis();
          DefaultListModel dlm = (DefaultListModel) addsyncsongslist.getModel();
          int start = addsyncsongslist.getSelectedIndex();
          int index = start;
          if (index >= dlm.getSize()) index = 0;
          if (index < 0) index = 0;
          boolean found = false;
          while (!found) {
            String value = ((String)dlm.getElementAt(index)).toLowerCase();
            if (value.startsWith(quickstrokestartswith)) found = true;
            else index++;
            if (index >= dlm.getSize()) index = 0;
            if (index == start) found = true;
          }
          if (index != start) {
            addsyncsongslist.setSelectedIndex(index);
            addsyncsongslist.ensureIndexIsVisible(index);
          }
        }
      }
    }
}
