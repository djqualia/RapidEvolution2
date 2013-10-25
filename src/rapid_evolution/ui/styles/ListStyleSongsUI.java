package rapid_evolution.ui.styles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.REList;
import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class ListStyleSongsUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(ListStyleSongsUI.class);

    public ListStyleSongsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ListStyleSongsUI instance = null;
    public REList liststylesongslist = new REList();
    public JButton removesongfromstylebt = new REButton();
    public JButton liststylesongsokbutton = new REButton();

    private void setupDialog() {
        liststylesongslist.setModel(new DefaultListModel());
        KeyListener[] listeners = liststylesongslist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) liststylesongslist.removeKeyListener(listeners[i]);
        liststylesongslist.addKeyListener(new StyleSongListKeyListener());
        liststylesongslist.addMouseListener(new ListStyleSongMouse());
        liststylesongslist.getSelectionModel().addListSelectionListener(new StyleSongSelectionListener());
        removesongfromstylebt.setEnabled(false);
    }

    public Vector stylesonglist = new Vector();
    public Semaphore PopulateStyleSongListSem = new Semaphore(1);
    public class PopulateStyleSongListThread extends Thread {
      public PopulateStyleSongListThread() {}
      public void run() {
        if (liststylesongslist == null) return;
        try {
          PopulateStyleSongListSem.acquire();
          DefaultListModel dlm = (DefaultListModel) liststylesongslist.getModel();
          dlm.removeAllElements();
          stylesonglist = new Vector();
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
            if (EditStyleUI.instance.editedstyle.containsDirect(iter)) {
              dlm.addElement(iter.getSongIdShort());
              stylesonglist.add(iter);
            }
            iter = iter.next;
          }
        } catch (Exception e) { }
        PopulateStyleSongListSem.release();
      }
    }

    public void InsertSongStyleList(SongLinkedList song) {

      for (int i = 0; i < stylesonglist.size(); ++i) {
        SongLinkedList ssong = (SongLinkedList)stylesonglist.get(i);
        if (song == ssong) {
          DefaultListModel dlm = (DefaultListModel) liststylesongslist.getModel();
          dlm.setElementAt(song.getSongIdShort(), i);
          return;
        }
      }
      try {
        PopulateStyleSongListSem.acquire();
        DefaultListModel dlm = (DefaultListModel) liststylesongslist.getModel();
        int i = 0;
        boolean inserted = false;
        String songcmp = song.getSongIdShort();
        while ((i < stylesonglist.size()) && !inserted) {
          String value = ((SongLinkedList)stylesonglist.get(i)).getSongIdShort();
          if (songcmp.compareToIgnoreCase(value) < 0) {
            dlm.insertElementAt(song.getSongIdShort(), i);
            stylesonglist.insertElementAt(song, i);
            inserted = true;
          }
          ++i;
        }
        if (!inserted) {
          dlm.addElement(song.getSongIdShort());
          stylesonglist.add(song);
        }
      } catch (Exception e) { log.error("InsertSongStyleList(): error", e); }
      PopulateStyleSongListSem.release();
    }

    public void PopulateStyleSongList() {
      new PopulateStyleSongListThread().start();
    }

    public void PopulateStyleSongList(boolean donow) {
      if (donow) {
        if (liststylesongslist == null) return;
        try {
          PopulateStyleSongListSem.acquire();
          DefaultListModel dlm = (DefaultListModel) liststylesongslist.getModel();
          dlm.removeAllElements();
          SongLinkedList iter = SongDB.instance.SongLL;
          stylesonglist = new Vector();
          while (iter != null) {
            if (EditStyleUI.instance.editedstyle.containsDirect(iter)) {
              dlm.addElement(iter.getSongIdShort());
              stylesonglist.add(iter);
            }
            iter = iter.next;
          }
        } catch (Exception e) { }
        PopulateStyleSongListSem.release();
      } else new PopulateStyleSongListThread().start();
    }

    public void RemoveSongStyleList(SongLinkedList song) {
      try {
        PopulateStyleSongListSem.acquire();
        DefaultListModel dlm = (DefaultListModel) liststylesongslist.getModel();
        int i = 0;
        boolean removed = false;
        while ((i < stylesonglist.size()) && !removed) {
          SongLinkedList value = ((SongLinkedList)stylesonglist.get(i));
          if (value == song) {
            dlm.removeElementAt(i);
            stylesonglist.remove(i);
            removed = true;
          }
          ++i;
        }
      } catch (Exception e) { log.error("RemoveSongStyleList(): error", e); }
      PopulateStyleSongListSem.release();
    }

    public void RemoveSongStyleList(long uniqueid) {
      try {
        PopulateStyleSongListSem.acquire();
        DefaultListModel dlm = (DefaultListModel) liststylesongslist.getModel();
        int i = 0;
        boolean removed = false;
        while ((i < stylesonglist.size()) && !removed) {
          SongLinkedList value = ((SongLinkedList)stylesonglist.get(i));
          if (value.uniquesongid == uniqueid) {
            dlm.removeElementAt(i);
            stylesonglist.remove(i);
            removed = true;
          }
          ++i;
        }
      } catch (Exception e) { log.error("RemoveSongStyleList(): error", e); }
      PopulateStyleSongListSem.release();
    }

    private void setupActionListeners() {
        liststylesongsokbutton.addActionListener(this);
        removesongfromstylebt.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == liststylesongsokbutton) {
        CloseListStyleSongs();
      } else if (ae.getSource() == removesongfromstylebt) {
        new RemoveSongsFromStyle().start();
      }
    }

    public void CloseListStyleSongs() {
        setVisible(false);
    }

    public boolean PreDisplay() {
      PopulateStyleSongList(true);
      String stylename = EditStyleUI.instance.editedstyle.getName(); //(String)display_parameter;
      setTitle(SkinManager.instance.getMessageText("songs_in_style_prefix") + stylename);
      return true;
    }

    public void AddToSelectedIndices(int index) {
        int[] selectedindices = new int[liststylesongslist.getSelectedIndices().length + 1];
        for (int i = 0; i < liststylesongslist.getSelectedIndices().length; ++i) {
            selectedindices[i] = liststylesongslist.getSelectedIndices()[i];
        }
        selectedindices[liststylesongslist.getSelectedIndices().length] = index;
        liststylesongslist.setSelectedIndices(selectedindices);
    }
}
