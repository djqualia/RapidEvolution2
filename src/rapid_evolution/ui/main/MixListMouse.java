package rapid_evolution.ui.main;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import rapid_evolution.Mixout;
import rapid_evolution.SongLinkedList;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.audio.DetectBPMs;
import rapid_evolution.audio.DetectColors;
import rapid_evolution.audio.tags.ReadBatchTags;
import rapid_evolution.audio.tags.WriteBatchTags;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

public class MixListMouse extends MouseAdapter implements ActionListener {

    private static Logger log = Logger.getLogger(MixListMouse.class);
    
    private int lastSelectedRow;
    
  public void mouseClicked(MouseEvent e) {
    Point pt = e.getPoint();
    mixoutpopupx = (int)pt.x;
    mixoutpopupy = (int)pt.y;
    int row = MixoutPane.instance.mixouttable.rowAtPoint(pt);
    if ((row >= RapidEvolutionUI.instance.currentsong.getNumMixoutSongs()) || (row < 0)) {
        lastSelectedRow = row;
    }
    if ((RapidEvolutionUI.instance.currentsong != null) && SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
      if ((row >= RapidEvolutionUI.instance.currentsong.getNumMixoutSongs()) || (row < 0)) {
        MixoutPane.instance.addoncheckbox.setSelected(false);
        MixoutPane.instance.addoncheckbox.setEnabled(false);
        MixoutPane.instance.mixoutcomments.setEnabled(false);
        MixoutPane.instance.mixoutcomments.setText("");
        MixoutPane.instance.mixoutscore.setEnabled(false);
        MixoutPane.instance.mixoutscore.setText("");
        MixoutPane.instance.bpmdifffield.setEnabled(false);
        MixoutPane.instance.bpmdifffield.setText("");
        MixoutPane.instance.calculatebpmdiffbutton.setEnabled(false);
        MixoutPane.instance.mixoutcommentslabel.setEnabled(false);
        MixoutPane.instance.scorefield.setEnabled(false);
        MixoutPane.instance.bpmdifflabel.setEnabled(false);
        return;
      }
      SongLinkedList song = (SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(row, MixoutPane.instance.mixoutcolumnconfig.num_columns);
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == song.uniquesongid) {
          RapidEvolutionUI.instance.change_current_song(song, RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i), true, false);
        }
      }
    } else if ((RapidEvolutionUI.instance.currentsong != null) && SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
        editSelectedMixout(row);
    }
  }

  public JPopupMenu m_pmnPopup2 = null;
  public int mixoutpopupx = 0;
  public int mixoutpopupy = 0;

  public JMenuItem edititem2 = new JMenuItem("song");
  public JMenuItem editmixout = new JMenuItem("mixout");
  public JMenu detectmenu = new JMenu("detect");
  public JMenu editmenu = new JMenu("edit");
  public JMenuItem setcurrent = new JMenuItem("set current song");
  public JMenuItem detectkeys2 = new JMenuItem("key");
  public JMenuItem detectbpms2 = new JMenuItem("bpm");
  public JMenuItem detectcolors2 = new JMenuItem("color");
  public JMenuItem playselection2 = new JMenuItem("play");
  public JMenu tagmenu = new JMenu("tag");
  public JMenuItem readtagsselected2 = new JMenuItem("read");
  public JMenuItem writetagsselected2 = new JMenuItem("write");
  public JMenuItem deleteselected2 = new JMenuItem("delete mixout");

  static public MixListMouse instance = null;

  public MixListMouse() {
    instance = this;
    m_pmnPopup2 = new JPopupMenu();
    edititem2.setEnabled(false);
    edititem2.addActionListener(this);
    setcurrent.setEnabled(false);
    setcurrent.addActionListener(this);
    m_pmnPopup2.add(editmenu);
    editmenu.add(edititem2);
    m_pmnPopup2.add(setcurrent);
    editmenu.add(editmixout);
    editmixout.setEnabled(false);
    editmixout.addActionListener(this);
    playselection2.setEnabled(false);
    playselection2.addActionListener(this);
    m_pmnPopup2.add(playselection2);
    m_pmnPopup2.add(detectmenu);
    detectmenu.add(detectkeys2);
    detectmenu.add(detectbpms2);
    // detectmenu.add(detectcolors2);
    detectbpms2.setEnabled(false);
    detectcolors2.setEnabled(false);
    detectbpms2.addActionListener(this);
    detectcolors2.addActionListener(this);
    detectkeys2.setEnabled(false);
    detectkeys2.addActionListener(this);
    readtagsselected2.setEnabled(false);
    readtagsselected2.addActionListener(this);
    m_pmnPopup2.add(tagmenu);
    tagmenu.add(readtagsselected2);
    writetagsselected2.setEnabled(false);
    writetagsselected2.addActionListener(this);
    tagmenu.add(writetagsselected2);
    m_pmnPopup2.add(deleteselected2);
    deleteselected2.addActionListener(this);
    deleteselected2.setEnabled(false);
  }
  public void actionPerformed(ActionEvent ae) {
    if (ae.getSource() == edititem2) {
      // edit song
      if (MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) {
        EditSongUI.instance.EditSong((SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(MixoutPane.instance.mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns));
      }
    } else if (ae.getSource() == detectkeys2) {
      if (MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) {
      SongLinkedList[] songs = MixoutPane.instance.getSelectedMixoutSongs();
      AudioEngine.instance.DetectBatchKeys(songs);
     }
   } else if (ae.getSource()== detectcolors2) {
     if (MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) {
         SongLinkedList[] songs = MixoutPane.instance.getSelectedMixoutSongs();
      new DetectColors(songs).start();
     }
   } else if (ae.getSource() == detectbpms2) {
     if (MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) {
         SongLinkedList[] songs = MixoutPane.instance.getSelectedMixoutSongs();
      new DetectBPMs(songs).start();
     }
   } else if (ae.getSource() == setcurrent) {
     SongLinkedList setcurrent = (SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(MixoutPane.instance.mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns);
     javax.swing.SwingUtilities.invokeLater(new changecurrentsongthread(setcurrent, 0.0f, false, false));
   } else if (ae.getSource() == writetagsselected2) {
      if (MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) {
          SongLinkedList[] songs = MixoutPane.instance.getSelectedMixoutSongs();
      new WriteBatchTags(songs).start();
     }
    } else if (ae.getSource() == playselection2) {
      Vector songsplaying = new Vector();
      songsplaying.add(MixoutPane.instance.mixouttable.getModel().getValueAt(MixoutPane.instance.mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns));
      AudioPlayer.PlaySongs();
    } else if (ae.getSource() == readtagsselected2) {
      if (MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) {
          SongLinkedList[] songs = MixoutPane.instance.getSelectedMixoutSongs();
          new ReadBatchTags(songs).start();
      }
    } else if (ae.getSource() == deleteselected2) {
      MixoutPane.instance.RemoveSelectedMixout();
    } else if (ae.getSource() == editmixout) {
        editSelectedMixout();
    }
  }

  
  
  private void editSelectedMixout() {
      editSelectedMixout(MixoutPane.instance.mixouttable.getSelectedRow());
  }
  private void editSelectedMixout(int selectedRow) {
      if (selectedRow != -1) {
          Mixout mixout = new Mixout("","");
          mixout.fromsong = RapidEvolutionUI.instance.currentsong;
          if (log.isDebugEnabled()) log.debug("editSelectedMixout(): selected row=" + selectedRow);
          mixout.tosong = (SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(selectedRow, MixoutPane.instance.mixoutcolumnconfig.num_columns);
          EditMixoutUI.instance.display_parameter = mixout;
          EditMixoutUI.instance.Display();
      }
  }
  
  public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
  }
  public void mouseReleased(MouseEvent e) {
    maybeShowPopup(e);
  }

  public void PostInit() {
    edititem2.setText(SkinManager.instance.getMessageText("menu_option_song"));
    editmixout.setText(SkinManager.instance.getMessageText("menu_option_mixout"));
    detectmenu.setText(SkinManager.instance.getMessageText("menu_option_detect"));
    editmenu.setText(SkinManager.instance.getMessageText("menu_option_edit"));
    setcurrent.setText(SkinManager.instance.getMessageText("menu_option_set_current_song"));
    detectkeys2.setText(SkinManager.instance.getMessageText("menu_option_key"));
    detectbpms2.setText(SkinManager.instance.getMessageText("menu_option_bpm"));
    detectcolors2.setText(SkinManager.instance.getMessageText("menu_option_color"));
    playselection2.setText(SkinManager.instance.getMessageText("menu_option_play"));
    tagmenu.setText(SkinManager.instance.getMessageText("menu_option_tags"));
    readtagsselected2.setText(SkinManager.instance.getMessageText("menu_option_read"));
    writetagsselected2.setText(SkinManager.instance.getMessageText("menu_option_write"));
    deleteselected2.setText(SkinManager.instance.getMessageText("menu_option_delete_mixout"));

  }

  void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
          m_pmnPopup2.show(e.getComponent(),
                      e.getX(), e.getY());
      }
  }
}
