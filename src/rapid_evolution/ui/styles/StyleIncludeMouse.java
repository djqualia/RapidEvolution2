package rapid_evolution.ui.styles;

import javax.swing.JMenuItem;
import rapid_evolution.audio.AudioPlayer;
import java.awt.event.ActionEvent;
import rapid_evolution.SongLinkedList;
import java.awt.event.MouseAdapter;
import javax.swing.DefaultListModel;
import rapid_evolution.ui.EditSongUI;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import java.awt.Point;
import java.util.Vector;
import rapid_evolution.SongDB;
import rapid_evolution.ui.SkinManager;

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

public class StyleIncludeMouse extends MouseAdapter implements ActionListener {

  public JPopupMenu m_pmnPopup;
  public int stylesongspopupx = 0;
  public int stylesongspopupy = 0;

  public JMenuItem edititem = new JMenuItem("edit");
  public JMenuItem playselection = new JMenuItem("play");
  public JMenuItem removeselection = new JMenuItem("remove");

  public void PostInit() {
    edititem.setText(SkinManager.instance.getMessageText("menu_option_edit"));
    playselection.setText(SkinManager.instance.getMessageText("menu_option_play"));
    removeselection.setText(SkinManager.instance.getMessageText("menu_option_remove"));
  }

  public void mouseClicked(MouseEvent e) {
        Point relative = e.getPoint();
        //  Get component associated with the event
        Component comp = e.getComponent();
        //  Get component's absolute screen location
        Point location = comp.getLocationOnScreen();
        //  Calculate cursor's absolute position on screen
//          Point absolute = new Point(relative.x + location.x, relative.y + location.y);

    stylesongspopupx = (int)relative.x;
    stylesongspopupy = (int)relative.y;
  }

  static public StyleIncludeMouse instance = null;
  public StyleIncludeMouse() {
    instance = this;

    JPopupMenu pmnPopup = new JPopupMenu();

    edititem.setEnabled(false);
    edititem.addActionListener(this);
    pmnPopup.add(edititem);

    playselection.setEnabled(false);
    playselection.addActionListener(this);
    pmnPopup.add(playselection);

    removeselection.setEnabled(false);
    removeselection.addActionListener(this);
    pmnPopup.add(removeselection);

    m_pmnPopup = pmnPopup;
  }
  public void actionPerformed(ActionEvent ae) {

    if (ae.getSource() == edititem) {
      // edit song
      DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.editstylekeywordslist.getModel();
      if (EditStyleUI.instance.editstylekeywordslist.getSelectedIndices().length == 1) {
        try {
          EditSongUI.instance.EditSong(SongDB.instance.NewGetSongPtr(((Long)EditStyleUI.instance.editedstyle.styleincludevector.get(EditStyleUI.instance.editstylekeywordslist.getSelectedIndex())).longValue()));
        } catch (Exception e) { }
      }
    } else if (ae.getSource() == playselection) {
      Vector songstoplay = new Vector();
      for (int i = 0; i < EditStyleUI.instance.editstylekeywordslist.getSelectedIndices().length; ++i) {
        try {
          SongLinkedList song = SongDB.instance.NewGetSongPtr(((Long)EditStyleUI.instance.editedstyle.styleincludevector.get(EditStyleUI.instance.editstylekeywordslist.getSelectedIndices()[i])).longValue());
          songstoplay.add(song);
        } catch (Exception e) {
          String keyword = (String)EditStyleUI.instance.editedstyle.styleincludevector.get(EditStyleUI.instance.editstylekeywordslist.getSelectedIndices()[i]);
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
              if (EditStyleUI.instance.editedstyle.containsDirect(iter)) {
                if (EditStyleUI.instance.editedstyle.matchesIncludeKeywords(iter, keyword)) {
                    boolean alreadyfound = false;
                    int j = 0;
                    while (!alreadyfound && (j < songstoplay.size())) {
                        SongLinkedList asong = (SongLinkedList)songstoplay.get(j);
                        if (asong == iter) alreadyfound = true;
                        ++j;
                    }
                    if (!alreadyfound) songstoplay.add(iter);
                }
              }
              iter = iter.next;
          }
        }
      }
      if (songstoplay.size() > 0) {
        AudioPlayer.songsplaying = songstoplay;
        AudioPlayer.PlaySongs();
      }
    } else if (ae.getSource() == removeselection) {
      EditStyleUI.instance.RemoveSelectedIncludeKeywords();
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
