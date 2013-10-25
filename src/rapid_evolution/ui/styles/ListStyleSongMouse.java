package rapid_evolution.ui.styles;

import javax.swing.JMenuItem;
import java.awt.event.MouseAdapter;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.audio.AudioEngine;
import java.awt.event.MouseEvent;
import rapid_evolution.ui.RapidEvolutionUI;
import javax.swing.JMenu;
import java.io.File;
import rapid_evolution.ui.OptionsUI;
import java.util.Vector;
import rapid_evolution.audio.AudioPlayer;
import java.awt.event.ActionEvent;
import rapid_evolution.SongLinkedList;
import java.awt.event.ActionListener;
import java.awt.Component;
import rapid_evolution.audio.DetectColors;
import rapid_evolution.threads.PopupWorkerThread;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ImportLib;
import rapid_evolution.filefilters.InputFileFilter;
import javax.swing.JPopupMenu;
import java.awt.Point;
import javax.swing.DefaultListModel;
import rapid_evolution.ui.styles.RemoveSongsFromStyle;
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

public class ListStyleSongMouse extends MouseAdapter implements ActionListener {

  public JPopupMenu m_pmnPopup;
  public int stylesongspopupx = 0;
  public int stylesongspopupy = 0;

  public JMenuItem edititem = new JMenuItem();
  public JMenuItem playselection = new JMenuItem();
  public JMenuItem removeselection = new JMenuItem();

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

  static public ListStyleSongMouse instance = null;
  public ListStyleSongMouse() {
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
      DefaultListModel dlm = (DefaultListModel) ListStyleSongsUI.instance.liststylesongslist.getModel();
      if (ListStyleSongsUI.instance.liststylesongslist.getSelectedIndices().length == 1) {
        EditSongUI.instance.EditSong((SongLinkedList)ListStyleSongsUI.instance.stylesonglist.get(ListStyleSongsUI.instance.liststylesongslist.getSelectedIndex()));
      }
    } else if (ae.getSource() == playselection) {
      Vector songstoplay = new Vector();
      for (int i = 0; i < ListStyleSongsUI.instance.liststylesongslist.getSelectedIndices().length; ++i) {
          songstoplay.add((SongLinkedList)ListStyleSongsUI.instance.stylesonglist.get(ListStyleSongsUI.instance.liststylesongslist.getSelectedIndices()[i]));
      }
      if (songstoplay.size() > 0) {
        AudioPlayer.songsplaying = songstoplay;
        AudioPlayer.PlaySongs();
      }
    } else if (ae.getSource() == removeselection) {
      new RemoveSongsFromStyle().start();
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
