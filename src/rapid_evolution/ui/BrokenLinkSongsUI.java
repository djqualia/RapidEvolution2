package rapid_evolution.ui;

import java.awt.Component;
import java.awt.Point;
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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class BrokenLinkSongsUI extends REDialog implements ActionListener {
    private static Logger log = Logger.getLogger(BrokenLinkSongsUI.class);

    
    public BrokenLinkSongsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static BrokenLinkSongsUI instance = null;
    public REList songlist = new REList();
    public JButton deletesongs = new REButton();
    public JButton okbutton = new REButton();
    public ListMouse listmouse = new ListMouse();

    private void setupDialog() {
        songlist.setModel(new DefaultListModel());
        KeyListener[] listeners = songlist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) songlist.removeKeyListener(listeners[i]);
        songlist.addKeyListener(new SongListKeyListener());
        songlist.addMouseListener(listmouse);
        songlist.getSelectionModel().addListSelectionListener(new SongSelectionListener());
        deletesongs.setEnabled(false);
    }

    public Vector songvector = null;
    public void PopulateStyleSongList() {
        if (songvector == null) return;
        try {
          DefaultListModel dlm = (DefaultListModel) BrokenLinkSongsUI.instance.songlist.getModel();
          dlm.removeAllElements();
          for (int i = 0; i < songvector.size(); ++i) {
              SongLinkedList song = (SongLinkedList)songvector.get(i);
              dlm.addElement(song.getSongIdShort());
          }
        } catch (Exception e) { log.error("PopulateStyleSongList(): error", e); }
    }

    private void setupActionListeners() {
        okbutton.addActionListener(this);
        deletesongs.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == okbutton) {
        Hide();
      } else if (ae.getSource() == deletesongs) {
          deleteSelectedSongs();
      }
    }

    public void CloseListStyleSongs() {
        setVisible(false);
    }

    public boolean PreDisplay() {
        PopulateStyleSongList();
      return true;
    }
    
    public class SongListKeyListener extends KeyAdapter {

        String quickstrokestartswith = new String("");
        long lastkeystroke = 0;

        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == e.VK_BACK_SPACE) {
            if (quickstrokestartswith.length() >= 1) quickstrokestartswith = quickstrokestartswith.substring(0, quickstrokestartswith.length() - 1);
            return;
          } else if (e.getKeyCode() == e.VK_SPACE) {
            if (quickstrokestartswith.length() >= 1) quickstrokestartswith += " ";
            return;
          } else if (e.getKeyCode() == e.VK_ESCAPE) {
              Hide();
          } else if (e.getKeyCode() == e.VK_ALT) {
              if (!BrokenLinkSongsUI.instance.listmouse.m_pmnPopup.isVisible()) {
                  BrokenLinkSongsUI.instance.listmouse.m_pmnPopup.show(e.getComponent(), BrokenLinkSongsUI.instance.listmouse.stylesongspopupx, BrokenLinkSongsUI.instance.listmouse.stylesongspopupy);
                }
                else BrokenLinkSongsUI.instance.listmouse.m_pmnPopup.setVisible(false);
                return;
              }

          char c = Character.toLowerCase(e.getKeyChar());
          if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == ',') || (c == '-') || (c == '_') || (c == '+') || (c == '\'') || (c == ':') || (c == ';') || (c == '\\') || (c == '/') || (c == '[') || (c == ']') || (c == '{') || (c == '}') || (c == '`') || (c == '!') || (c == '@') || (c == '#') || (c == '$') || (c == '%') || (c == '^') || (c == '&') || (c == '*') || (c == '(') || (c == ')') || (c == '|') || (c == '~')) {
            if ((lastkeystroke == 0) || ((System.currentTimeMillis() - lastkeystroke) < 1500)) quickstrokestartswith += c;
            else quickstrokestartswith =  new String("" + c);
            lastkeystroke = System.currentTimeMillis();
            DefaultListModel dlm = (DefaultListModel) BrokenLinkSongsUI.instance.songlist.getModel();
            int start = BrokenLinkSongsUI.instance.songlist.getSelectedIndex();
            int index = start;
            if (index >= dlm.getSize()) index = 0;
            if (index < 0) index = 0;
            boolean found = false;
            while (!found) {
              String value = ((String)dlm.getElementAt(index)).toLowerCase();
              if (value.startsWith("<")) value = value.substring(1, value.length());
              if (value.startsWith(quickstrokestartswith)) found = true;
              else index++;
              if (index >= dlm.getSize()) index = 0;
              if (index == start) found = true;
            }
            if (index !=  start) {
                BrokenLinkSongsUI.instance.songlist.setSelectedIndex(index);
                BrokenLinkSongsUI.instance.songlist.ensureIndexIsVisible(index);
            }
          }
        }
      }    
    
    public class SongSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (BrokenLinkSongsUI.instance.songlist.getSelectedIndices().length > 1) {
                listmouse.edititem.setEnabled(false);
                listmouse.deleteitems.setEnabled(true);
            } else if (BrokenLinkSongsUI.instance.songlist.getSelectedIndices().length == 1) {
                listmouse.edititem.setEnabled(true);
                listmouse.deleteitems.setEnabled(true);
            }
            DefaultListModel dlm = (DefaultListModel) BrokenLinkSongsUI.instance.songlist.getModel();
            if (dlm.getSize() > 0) BrokenLinkSongsUI.instance.deletesongs.setEnabled(true);
            else BrokenLinkSongsUI.instance.deletesongs.setEnabled(false);
        }
    }
    
    public void deleteSelectedSongs() {
        int n = IOptionPane.showConfirmDialog(
                SkinManager.instance.getFrame("main_frame"),
                SkinManager.instance.getDialogMessageText("delete_songs"),
                SkinManager.instance.getDialogMessageTitle("delete_songs"),
                IOptionPane.YES_NO_OPTION);
            if (n != 0) {
              return;
            }
            int[] selected = songlist.getSelectedIndices();
            DefaultListModel dlm = (DefaultListModel) BrokenLinkSongsUI.instance.songlist.getModel();
            for (int i = 0; i < selected.length; ++i) {
                int index = selected[i];
                SongLinkedList song = (SongLinkedList)songvector.get(index);
                songvector.remove(index);
                SongDB.instance.DeleteSong(song);
                dlm.removeElementAt(index);
                for (int j = i + 1; j < selected.length; ++j) {
                    if (selected[j] > index)
                        --selected[j];
                }
            }        
    }
    
    public class ListMouse extends MouseAdapter implements ActionListener {

        public JPopupMenu m_pmnPopup;
        public int stylesongspopupx = 0;
        public int stylesongspopupy = 0;

        public JMenuItem edititem = new JMenuItem();
        public JMenuItem deleteitems = new JMenuItem();

        public void PostInit() {
          edititem.setText(SkinManager.instance.getMessageText("menu_option_edit"));
          deleteitems.setText(SkinManager.instance.getMessageText("menu_option_delete_songs"));
        }

        public void mouseClicked(MouseEvent e) {
              Point relative = e.getPoint();
              //  Get component associated with the event
              Component comp = e.getComponent();
              //  Get component's absolute screen location
              Point location = comp.getLocationOnScreen();
              //  Calculate cursor's absolute position on screen
//                Point absolute = new Point(relative.x + location.x, relative.y + location.y);

          stylesongspopupx = (int)relative.x;
          stylesongspopupy = (int)relative.y;
        }

        public ListMouse() {

          JPopupMenu pmnPopup = new JPopupMenu();

          edititem.setEnabled(false);
          edititem.addActionListener(this);
          pmnPopup.add(edititem);

          deleteitems.setEnabled(false);
          deleteitems.addActionListener(this);
          pmnPopup.add(deleteitems);

          m_pmnPopup = pmnPopup;
        }
        public void actionPerformed(ActionEvent ae) {

          if (ae.getSource() == edititem) {
            // edit song
            DefaultListModel dlm = (DefaultListModel) BrokenLinkSongsUI.instance.songlist.getModel();
            if (BrokenLinkSongsUI.instance.songlist.getSelectedIndices().length == 1) {
              EditSongUI.instance.EditSong((SongLinkedList)BrokenLinkSongsUI.instance.songvector.get(BrokenLinkSongsUI.instance.songlist.getSelectedIndex()));
            }
          } else if (ae.getSource() == deleteitems) {
              BrokenLinkSongsUI.instance.deleteSelectedSongs();
          }
        }

        public void mousePressed(MouseEvent e) {
         maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
          maybeShowPopup(e);
        }

        void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                m_pmnPopup.show(e.getComponent(),
                            e.getX(), e.getY());
            }
        }
      }    
}
