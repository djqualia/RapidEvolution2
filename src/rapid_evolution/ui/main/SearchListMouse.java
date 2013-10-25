package rapid_evolution.ui.main;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import rapid_evolution.DataRetriever;
import rapid_evolution.FileUtil;
import rapid_evolution.FindLinkThread;
import rapid_evolution.ImportLib;
import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.audio.DetectAll;
import rapid_evolution.audio.DetectBPMs;
import rapid_evolution.audio.DetectColors;
import rapid_evolution.audio.Normalizer;
import rapid_evolution.audio.tags.ReadBatchTags;
import rapid_evolution.audio.tags.RemoveBatchTags;
import rapid_evolution.audio.tags.WriteBatchTags;
import rapid_evolution.filefilters.InputFileFilter;
import rapid_evolution.filefilters.WaveFileFilter;
import rapid_evolution.threads.ErrorMessageThread;
import rapid_evolution.threads.NormalizationWorker;
import rapid_evolution.threads.PopupWorkerThread;
import rapid_evolution.ui.AddMixoutUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.FormatBpmsUI;
import rapid_evolution.ui.GeneratePlaylistUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.RenameFilesUI;
import rapid_evolution.ui.RetrieveAlbumCoversProgressUI;
import rapid_evolution.ui.SetFieldsUI;
import rapid_evolution.ui.SetFlagsUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.SongTrailUI;
import rapid_evolution.ui.styles.AddToStylesUI;
import rapid_evolution.ui.styles.RemoveFromStyles;
import rapid_evolution.ui.styles.SetStylesUI;

import com.ibm.iwt.IOptionPane;

public class SearchListMouse extends MouseAdapter implements ActionListener {

    
    private static Logger log = Logger.getLogger(SearchListMouse.class);
    
  public JPopupMenu m_pmnPopup;
  public int searchpopupx = 0;
  public int searchpopupy = 0;

  public Vector menuitems = new Vector();
  public Vector menus = new Vector();

  public JMenuItem edititem;
  public JMenuItem generateplayselection = new JMenuItem("generate playlist");
  public JMenuItem setcurrent = new JMenuItem("set current song");
  public JMenuItem addmixout = new JMenuItem("add mixout");
  public JMenuItem playselection = new JMenuItem("play");
  public JMenuItem findlinkselection = new JMenuItem("find link");
  public JMenuItem detectall = new JMenuItem("all");
  public JMenuItem detectkeys = new JMenuItem("key");
  public JMenuItem detectrgad = new JMenuItem("replay gain");
  public JMenuItem detectbpms = new JMenuItem("bpm");
  public JMenuItem detectcolors = new JMenuItem("color");
  public JMenuItem detectbeatintensity = new JMenuItem("beat intensity");
  public JMenuItem normalize = new JMenuItem("normalize");
  public JMenuItem pitchshift = new JMenuItem("time/pitch shift");
  public JMenuItem audiomenu = new JMenu("audio");
  public JMenuItem setstyles = new JMenuItem("set");
  public JMenuItem addtostyles = new JMenuItem("add to");
  public JMenuItem removefromstyles = new JMenuItem("remove from");
  public JMenuItem readtagsselected = new JMenuItem("read");
  public JMenuItem writetagsselected = new JMenuItem("write");
  public JMenuItem removetagsselected = new JMenuItem("remove");
  public JMenuItem deleteselected = new JMenuItem("delete song");
  public JMenuItem importdata = new JMenuItem("import data");
  public JMenu setfields = new JMenu("set");
  public JMenu fields = new JMenu("fields");
  public JMenuItem copyfields = new JMenuItem("copy");
  public JMenuItem appendfields = new JMenuItem("append");
  public JMenuItem prependfields = new JMenuItem("prepend");
  public JMenuItem setalbum = new JMenuItem("album");
  public JMenuItem setartist = new JMenuItem("artist");
  public JMenuItem setcomments = new JMenuItem("comments");
  public JMenuItem setsongname = new JMenuItem("title");
  public JMenuItem setremixer = new JMenuItem("remix");
  public JMenuItem setstartbpm = new JMenuItem("start bpm");
  public JMenuItem setstartkey = new JMenuItem("start key");
  public JMenuItem setendbpm = new JMenuItem("end bpm");
  public JMenuItem setendkey = new JMenuItem("end key");
  public JMenuItem settimesig = new JMenuItem("time signature");
  public JMenuItem settrack = new JMenuItem("track");
  public JMenuItem settime = new JMenuItem("track time");
  public JMenuItem setuser1 = new JMenuItem("user field 1");
  public JMenuItem setuser2 = new JMenuItem("user field 2");
  public JMenuItem setuser3 = new JMenuItem("user field 3");
  public JMenuItem setuser4 = new JMenuItem("user field 4");
  public JMenuItem setfilename = new JMenuItem("filename");
  public JMenuItem setflags = new JMenuItem("flags");
  public JMenu detectmenu = new JMenu("detect");
  public JMenu styles = new JMenu("styles");
  public JMenu tagsmenu = new JMenu("tag(s)");
  public JMenu specialmenu = new JMenu("database");
  public JMenuItem dataretriever = new JMenuItem("retrieve album covers");
  public JMenuItem discogsretriever = new JMenuItem("retrieve info from discogs.com");
  public JMenuItem correctkeyformat = new JMenuItem("correct key format");
  public JMenuItem detectbrokenlinks = new JMenuItem("detect broken file links");
  public JMenuItem changedriveletter = new JMenuItem("change drive letters");
  public JMenuItem formatbpms = new JMenuItem("format bpm values");
  public JMenuItem renamefiles = new JMenuItem("rename files");
  public JMenuItem resetPlayCount = new JMenuItem("reset play count");
  public JMenuItem makelower = new JMenuItem("convert fields to lower case");
  public JMenuItem makeproper = new JMenuItem("convert fields to proper case");
  public JMenuItem makeupper = new JMenuItem("convert fields to upper case");
  public JMenuItem correcttracknums = new JMenuItem("format track numbers");
  public JMenuItem searchformissingfiles = new JMenuItem("update file locations");
  public JMenuItem assigntracknumbers = new JMenuItem("assign track numbers");
  public JMenuItem organizeFiles = new JMenuItem("organize songs into directories");
  public JMenuItem parseremixer = new JMenuItem("parse remix from song title fields");
  public JMenuItem queryserver = new JMenuItem("query mixshare server for song info");
  public JMenuItem removefileextensions = new JMenuItem("remove file extensions from song title fields");
  public boolean stopAlbumCoverRetrieval = false;

  PitchTimeShiftUI pitchshiftui;

  public void mouseClicked(MouseEvent e) {
        Point relative = e.getPoint();
        //  Get component associated with the event
//        Component comp = e.getComponent();
        //  Get component's absolute screen location
//        Point location = comp.getLocationOnScreen();
        //  Calculate cursor's absolute position on screen
//          Point absolute = new Point(relative.x + location.x, relative.y + location.y);

    searchpopupx = (int)relative.x;
    searchpopupy = (int)relative.y;

    if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
      int row = SearchPane.instance.searchtable.rowAtPoint(relative);
      if (row < 0) return;
      if (row >= SearchPane.instance.searchtable.getRowCount()) return;
//        table.getColumnModel().getColumn(0).
//        SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).getV
      Object obj = SearchPane.instance.searchtable.getModel().getValueAt(row, SearchPane.instance.searchcolumnconfig.num_columns);
      if (obj == null) return;
      if ((RapidEvolutionUI.instance.currentsong != null) && OptionsUI.instance.addmixoutondoubleclick.isSelected()) {
          AddMixoutUI.instance.Display(setcurrent);          
      } else {
          RapidEvolutionUI.instance.change_current_song((SongLinkedList)obj, 0.0f, false, false);
      }
    }
    else if (SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 2)) {
/*
        int row = SearchPane.instance.searchtable.rowAtPoint(relative);
        if (row < 0) return;
        if (row >= SearchPane.instance.searchtable.getRowCount()) return;
        EditSongUI.instance.setTableRow(SearchPane.instance.searchtable, row);
        Object obj = SearchPane.instance.searchtable.getModel().getValueAt(row, SearchPane.instance.searchcolumnconfig.num_columns);
        EditSongUI.instance.EditSong((SongLinkedList)obj);
        */
    }
  }

  static public SearchListMouse instance = null;
  public SearchListMouse() {
    instance = this;
/*
    menuitems.add(edititem);
    menuitems.add(generateplayselection);
    menuitems.add(setcurrent);
    menuitems.add(playselection);
    menuitems.add(findlinkselection);
    menuitems.add(detectkeys);
    menuitems.add(detectbpms);
    menuitems.add(detectcolors);
    menuitems.add(normalize);
    menuitems.add(pitchshift);
    menuitems.add(audiomenu);
    menuitems.add(setstyles);
    menuitems.add(addtostyles);
    menuitems.add(readtagsselected);
    menuitems.add(writetagsselected);
    menuitems.add(deleteselected);
    menuitems.add(importdata);
    public JMenu setfields);
    menuitems.add(setalbum);
    menuitems.add(setartist);
    menuitems.add(setcomments);
    menuitems.add(setsongname);
    menuitems.add(setremixer);
    menuitems.add(setstartbpm);
    menuitems.add(setstartkey);
    menuitems.add(setendbpm);
    menuitems.add(setendkey);
    menuitems.add(settimesig);
    menuitems.add(settrack);
    menuitems.add(settime);
    menuitems.add(setuser1);
    menuitems.add(setuser2);
    menuitems.add(setuser3);
    menuitems.add(setuser4);
    menuitems.add(setfilename);
    menuitems.add(setflags);
    public JMenu detectmenu = new JMenu("detect");
    public JMenu tagsmenu = new JMenu("tag(s)");
    public JMenu specialmenu = new JMenu("database");
    menuitems.add(copyfields = new JMenuItem("copy start bpm to end bpm");
    menuitems.add(correctkeyformat = new JMenuItem("correct key format");
    menuitems.add(changedriveletter = new JMenuItem("change drive letters");
    menuitems.add(makelower = new JMenuItem("convert fields to lower case");
    menuitems.add(makeproper = new JMenuItem("convert fields to proper case");
    menuitems.add(makeupper = new JMenuItem("convert fields to upper case");
    menuitems.add(correcttracknums = new JMenuItem("format track numbers");
    menuitems.add(searchformissingfiles = new JMenuItem("update file locations");
    menuitems.add(assigntracknumbers = new JMenuItem("assign track numbers");
    menuitems.add(parseremixer = new JMenuItem("parse remix from song title fields");
    menuitems.add(removefileextensions = new JMenuItem("remove file extensions from song title fields");
  */

    JPopupMenu pmnPopup = new JPopupMenu();
    edititem = new JMenuItem("edit");
    edititem.setEnabled(false);
    edititem.addActionListener(this);
    setcurrent.addActionListener(this);
    addmixout.addActionListener(this);
    pmnPopup.add(edititem);
    setcurrent.setEnabled(false);
    pmnPopup.add(setcurrent);
    pmnPopup.add(addmixout);
    setcurrent.setEnabled(false);
    addmixout.setEnabled(false);
    generateplayselection.setEnabled(false);
     generateplayselection.addActionListener(this);
    playselection.setEnabled(false);
    audiomenu.setEnabled(false);
    playselection.addActionListener(this);
    pmnPopup.add(playselection);
    pmnPopup.add(generateplayselection);
    findlinkselection.setEnabled(false);
    pitchshift.setEnabled(false);
    findlinkselection.addActionListener(this);
    pmnPopup.add(findlinkselection);
    setstyles.setEnabled(false);
    styles.setEnabled(false);
    addtostyles.setEnabled(false);
    removefromstyles.setEnabled(false);
    detectmenu.setEnabled(false);
    pmnPopup.add(detectmenu);
    detectmenu.add(detectall);
    detectmenu.add(detectkeys);
    detectall.setEnabled(false);
    detectkeys.setEnabled(false);
    detectrgad.setEnabled(false);
    detectbpms.setEnabled(false);
    detectbeatintensity.setEnabled(false);
    detectcolors.setEnabled(false);
    detectmenu.add(detectbpms);
    detectmenu.add(detectbeatintensity);
    detectmenu.add(detectrgad);
//      detectmenu.add(detectcolors);
    pmnPopup.add(audiomenu);
    normalize.addActionListener(this);
    normalize.setEnabled(false);
    audiomenu.add(normalize);
    audiomenu.add(pitchshift);
    detectkeys.addActionListener(this);
    detectrgad.addActionListener(this);
    detectall.addActionListener(this);
    detectbpms.addActionListener(this);
    detectcolors.addActionListener(this);
    detectbeatintensity.addActionListener(this);
    pitchshift.addActionListener(this);
    readtagsselected.setEnabled(false);
    readtagsselected.addActionListener(this);
    pmnPopup.add(tagsmenu);

    tagsmenu.setEnabled(false);
    tagsmenu.add(readtagsselected);
    writetagsselected.setEnabled(false);
    removetagsselected.setEnabled(false);
    writetagsselected.addActionListener(this);
    removetagsselected.addActionListener(this);
    tagsmenu.add(writetagsselected);
    tagsmenu.add(removetagsselected);
    specialmenu.setEnabled(false);

    pmnPopup.add(fields);
    fields.add(setfields);
    fields.add(copyfields);
    //fields.add(appendfields);
    //fields.add(prependfields);
    
    copyfields.addActionListener(this);
    appendfields.addActionListener(this);
    prependfields.addActionListener(this);
    
    fields.setEnabled(false);
    setfields.add(setartist);
    setartist.addActionListener(this);
    setfields.add(setalbum); setalbum.addActionListener(this);
    setfields.add(settrack); settrack.addActionListener(this);
    setfields.add(settime); settime.addActionListener(this);
    setfields.add(settimesig); settimesig.addActionListener(this);
    setfields.add(setsongname); setsongname.addActionListener(this);
    setfields.add(setremixer); setremixer.addActionListener(this);
    setfields.add(setstartbpm); setstartbpm.addActionListener(this);
    setfields.add(setstartkey); setstartkey.addActionListener(this);
    setfields.add(setendbpm); setendbpm.addActionListener(this);
    setfields.add(setendkey); setendkey.addActionListener(this);
    setfields.add(setuser1); setuser1.addActionListener(this);
    setfields.add(setuser2); setuser2.addActionListener(this);
    setfields.add(setuser3); setuser3.addActionListener(this);
    setfields.add(setuser4); setuser4.addActionListener(this);
    setfields.add(setcomments); setcomments.addActionListener(this);
    setfields.add(setfilename); setfilename.addActionListener(this);
    setfields.add(setflags); setflags.addActionListener(this);

    styles.add(addtostyles);
    styles.add(removefromstyles);
    styles.add(setstyles);    
    pmnPopup.add(styles);
    addtostyles.addActionListener(this);
    setstyles.addActionListener(this);
    removefromstyles.addActionListener(this);
//      pmnPopup.add(importdata);
//      importdata.addActionListener(this);
    deleteselected.setEnabled(false);
    deleteselected.addActionListener(this);
    pmnPopup.add(specialmenu);

    specialmenu.add(assigntracknumbers); assigntracknumbers.addActionListener(this);
    specialmenu.add(changedriveletter); changedriveletter.addActionListener(this);
    specialmenu.add(makelower); makelower.addActionListener(this);
    specialmenu.add(makeproper); makeproper.addActionListener(this);
    specialmenu.add(makeupper); makeupper.addActionListener(this);    
    specialmenu.add(correctkeyformat); correctkeyformat.addActionListener(this);
    specialmenu.add(detectbrokenlinks); detectbrokenlinks.addActionListener(this);
    specialmenu.add(formatbpms); formatbpms.addActionListener(this);
    specialmenu.add(correcttracknums); correcttracknums.addActionListener(this);
    specialmenu.add(organizeFiles); organizeFiles.addActionListener(this);
    specialmenu.add(parseremixer); parseremixer.addActionListener(this);
    specialmenu.add(queryserver); queryserver.addActionListener(this);
    specialmenu.add(removefileextensions); removefileextensions.addActionListener(this);
    specialmenu.add(renamefiles); renamefiles.addActionListener(this);
    specialmenu.add(resetPlayCount); resetPlayCount.addActionListener(this);
    specialmenu.add(dataretriever); dataretriever.addActionListener(this);
    specialmenu.add(discogsretriever); discogsretriever.addActionListener(this);
    specialmenu.add(searchformissingfiles); searchformissingfiles.addActionListener(this);

    pmnPopup.add(deleteselected);
    m_pmnPopup = pmnPopup;

    pitchshiftui = new PitchTimeShiftUI("time_pitch_shift_dialog");

    // TODO: figure out how not to set fill kind to none and still get substance to obey background colors
    // set by skins...
    /*
    javax.swing.UIManager.put(SubstanceLookAndFeel.MENU_GUTTER_FILL_KIND, MenuGutterFillKind.NONE);
    fields.setOpaque(false);
    audiomenu.setOpaque(false);
    setfields.setOpaque(false);
    detectmenu.setOpaque(false);
    styles.setOpaque(false);
    tagsmenu.setOpaque(false);
    specialmenu.setOpaque(false);
    */

  }
  public void actionPerformed(ActionEvent ae) {
          
      if (!SetFieldsUI.instance.isVisible())
          SetFieldsUI.instance.setfieldfield.setFieldIndex(null);
      
    if (ae.getSource() == edititem) {
      // edit song
      if (SearchPane.instance.searchtable.getSelectedRowCount() == 1) {
          int selrow = SearchPane.instance.searchtable.getSelectedRow();
          EditSongUI.instance.setTableRow(SearchPane.instance.searchtable, selrow);
        EditSongUI.instance.EditSong((SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selrow, SearchPane.instance.searchcolumnconfig.num_columns));
      }
    } else if (ae.getSource() == addmixout) {
        AddMixoutUI.instance.Display(addmixout);
    } else if (ae.getSource() == setcurrent) {        
      SongLinkedList setcurrent = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(SearchPane.instance.searchtable.getSelectedRow(), SearchPane.instance.searchcolumnconfig.num_columns);
      javax.swing.SwingUtilities.invokeLater(new changecurrentsongthread(setcurrent, 0.0f, false, false));
    } else if (ae.getSource() == findlinkselection) {
      SongLinkedList findsong = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(SearchPane.instance.searchtable.getSelectedRow(), SearchPane.instance.searchcolumnconfig.num_columns);
      new FindLinkThread(findsong, true).start();
    } else if (ae.getSource() == detectbeatintensity) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      new DetectColors(songs, true).start();
    } else if (ae.getSource() == detectcolors) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      new DetectColors(songs).start();
    } else if (ae.getSource() == normalize) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      SongLinkedList song = songs[0];
      if (song.getFileName().equals("")) {
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
            SkinManager.instance.getDialogMessageText("normalization_no_file_error"),
           SkinManager.instance.getDialogMessageTitle("normalization_no_file_error"),
           IOptionPane.ERROR_MESSAGE);
      } else {
        JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
        fc.setSelectedFile(new File(StringUtil.RemoveExtension(song.getRealFileName()) + "-normalized"));
        if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
        fc.addChoosableFileFilter(new WaveFileFilter());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        int returnVal = fc.showSaveDialog(SkinManager.instance.getFrame("main_frame"));
        File tmp = fc.getSelectedFile();
        if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          String filestr = (String)tmp.getAbsolutePath();
          Normalizer normalizer = new Normalizer();
          NormalizationProgressUI.instance.normalizer = normalizer;
          new NormalizationWorker(normalizer, song.getRealFileName(), filestr, null).start();
          NormalizationProgressUI.instance.Display();
        }
      }
    } else if (ae.getSource() == pitchshift) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      SongLinkedList song = songs[0];
      if (song.getFileName().equals("")) {
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
           SkinManager.instance.getDialogMessageText("time_pitch_shift_no_file_error"),
          SkinManager.instance.getDialogMessageTitle("time_pitch_shift_no_file_error"),
          IOptionPane.ERROR_MESSAGE);
      } else if (!song.hasBpmOrKey()) {
        IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
        SkinManager.instance.getDialogMessageText("time_pitch_shift_missing_info_error"),
       SkinManager.instance.getDialogMessageTitle("time_pitch_shift_missing_info_error"),
       IOptionPane.ERROR_MESSAGE);
      } else {
        pitchshiftui.display_parameter = song;
        pitchshiftui.Display();
      }
    } else if (ae.getSource() == detectbpms) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      new DetectBPMs(songs).start();
    } else if (ae.getSource() == detectall) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
        new DetectAll(songs).start();
    } else if (ae.getSource() == detectrgad) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
        AudioEngine.instance.DetectRGAs(songs);        
    } else if (ae.getSource() == detectkeys) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      AudioEngine.instance.DetectBatchKeys(songs);
    } else if (ae.getSource() == setflags) {
        SetFlagsUI.instance.Display();
    } else if (ae.getSource() == setstyles) {
      if (!SetStylesUI.instance.isVisible()) {
          int[] selected = SearchPane.instance.searchtable.getSelectedRows();
          SetStylesUI.instance.setstylesongs = new SongLinkedList[selected.length];
        for (int i = 0; i < selected.length; ++i) {
          SetStylesUI.instance.setstylesongs[i] = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
        }
        SetStylesUI.instance.SetStyles();
      }
    } else if (ae.getSource() == removefromstyles) {
        if (!RemoveFromStyles.instance.isVisible()) {
            int[] selected = SearchPane.instance.searchtable.getSelectedRows();
            RemoveFromStyles.instance.removefromstylesongs = new SongLinkedList[selected.length];
            for (int i = 0; i < selected.length; ++i) {
              RemoveFromStyles.instance.removefromstylesongs[i] = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
            }
            RemoveFromStyles.instance.Display();
        }        
    } else if (ae.getSource() == addtostyles) {
      if (!AddToStylesUI.instance.isVisible()) {
          int[] selected_rows = SearchPane.instance.searchtable.getSelectedRows();
          SongLinkedList[] songs = new SongLinkedList[selected_rows.length];
        for (int i = 0; i < SearchPane.instance.searchtable.getSelectedRowCount(); ++i) {
          songs[i] = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected_rows[i], SearchPane.instance.searchcolumnconfig.num_columns);
        }
        AddToStylesUI.instance.display_parameter = songs;
        AddToStylesUI.instance.Display();
      }
    } else if (ae.getSource() == removetagsselected) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
        new RemoveBatchTags(songs).start();
    } else if (ae.getSource() == writetagsselected) {
        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      new WriteBatchTags(songs).start();
    } else if (ae.getSource() == generateplayselection) {
        GeneratePlaylistUI.instance.Display();
    } else if (ae.getSource() == playselection) {
      AudioPlayer.songsplaying = RapidEvolutionUI.getSelectedSearchSongsVector();
      AudioPlayer.PlaySongs();
    } else if (ae.getSource() == setartist) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_artist"));
      String initialvalue = new String("");
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      SongLinkedList[] songs = new SongLinkedList[selected.length];
      for (int i = 0; i < selected.length; ++i) {
        SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
        if (initialvalue.equals("")) initialvalue = song.getArtist();
        songs[i] = song;
      }
      if (!OptionsUI.instance.disableautocomplete.isSelected()) 
          SetFieldsUI.instance.setfieldfield.setFieldIndex(RapidEvolution.getMusicDatabase().getArtistIndex());
      SetFieldsUI.instance.setfieldtype = 0;
      SetField(songs, initialvalue);
    } else if (ae.getSource() == setfilename) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_filename"));
      SetFilename(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == setcomments) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_song_comments"));
      SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
      SetFieldsUI.instance.setfieldtype = 10;
      SetField(songs);
    } else if (ae.getSource() == setendkey) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_key_end"));
      SetFieldsUI.instance.setfieldtype = 9;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == setendbpm) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_bpm_end"));
      SetFieldsUI.instance.setfieldtype = 8;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == setstartkey) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_key_start"));
      SetFieldsUI.instance.setfieldtype = 7;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == setstartbpm) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_bpm_start"));
      SetFieldsUI.instance.setfieldtype = 6;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == setsongname) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_title"));
      SetFieldsUI.instance.setfieldtype = 5;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == setremixer) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_remix"));
      SetFieldsUI.instance.setfieldtype = 24;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == settimesig) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_time_signature"));
      SetFieldsUI.instance.setfieldtype = 4;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == setuser1) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + "" + OptionsUI.instance.customfieldtext1.getText());
      String initialvalue = new String("");
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      SongLinkedList[] songs = new SongLinkedList[selected.length];
      for (int i = 0; i < selected.length; ++i) {
        SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
        if (initialvalue.equals("")) initialvalue = song.getUser1();
        songs[i] = song;
      }
      SetFieldsUI.instance.setfieldtype = 20;
      if (OptionsUI.instance.donotclearuserfield1whenadding.isSelected() && !OptionsUI.instance.disableautocomplete.isSelected()) SetFieldsUI.instance.setfieldfield.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom1Index());
      if (OptionsUI.instance.donotclearuserfield1whenadding.isSelected()) SetField(songs, initialvalue);
      else SetField(songs);
    } else if (ae.getSource() == setuser2) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + "" + OptionsUI.instance.customfieldtext2.getText());
      String initialvalue = new String("");
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      SongLinkedList[] songs = new SongLinkedList[selected.length];
      for (int i = 0; i < selected.length; ++i) {
        SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
        if (initialvalue.equals("")) initialvalue = song.getUser2();
        songs[i] = song;
      }
      SetFieldsUI.instance.setfieldtype = 21;
      if (OptionsUI.instance.donotclearuserfield2whenadding.isSelected() && !OptionsUI.instance.disableautocomplete.isSelected()) SetFieldsUI.instance.setfieldfield.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom2Index());
      if (OptionsUI.instance.donotclearuserfield2whenadding.isSelected()) SetField(songs, initialvalue);
      else SetField(songs);
    } else if (ae.getSource() == setuser3) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + "" + OptionsUI.instance.customfieldtext3.getText());
      String initialvalue = new String("");
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      SongLinkedList[] songs = new SongLinkedList[selected.length];
      for (int i = 0; i < selected.length; ++i) {
        SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
        if (initialvalue.equals("")) initialvalue = song.getUser3();
        songs[i] = song;
      }
      SetFieldsUI.instance.setfieldtype = 22;
      if (OptionsUI.instance.donotclearuserfield3whenadding.isSelected() && !OptionsUI.instance.disableautocomplete.isSelected()) SetFieldsUI.instance.setfieldfield.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom3Index());
      if (OptionsUI.instance.donotclearuserfield3whenadding.isSelected()) SetField(songs, initialvalue);
      else SetField(songs);
    } else if (ae.getSource() == setuser4) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + "" + OptionsUI.instance.customfieldtext4.getText());
      String initialvalue = new String("");
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      SongLinkedList[] songs = new SongLinkedList[selected.length];
      for (int i = 0; i < selected.length; ++i) {
        SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
        if (initialvalue.equals("")) initialvalue = song.getUser4();
        songs[i] = song;
      }
      SetFieldsUI.instance.setfieldtype = 23;
      if (OptionsUI.instance.donotclearuserfield4whenadding.isSelected() && !OptionsUI.instance.disableautocomplete.isSelected()) SetFieldsUI.instance.setfieldfield.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom4Index());
      if (OptionsUI.instance.donotclearuserfield4whenadding.isSelected()) SetField(songs, initialvalue);
      else SetField(songs);
    } else if (ae.getSource() == settrack) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_track"));
      SetFieldsUI.instance.setfieldtype = 2;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == assigntracknumbers) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == parseremixer) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == organizeFiles) {
//        new PopupWorkerThread(ae).start();
        if (RapidEvolutionUI.instance.organizefiles_ui.isVisible()) return;
        RapidEvolutionUI.instance.organizefiles_ui.Display();        
    } else if (ae.getSource() == queryserver) {
        new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == removefileextensions) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == renamefiles) {
        RenameFilesUI.instance.songs = RapidEvolutionUI.instance.getSelectedSearchSongs();
        RenameFilesUI.instance.source_id = 0;
        new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == resetPlayCount) {
        RenameFilesUI.instance.songs = RapidEvolutionUI.instance.getSelectedSearchSongs();
        RenameFilesUI.instance.source_id = 0;
        new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == searchformissingfiles) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == correcttracknums) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == formatbpms) {
      FormatBpmsUI.instance.Display();
    } else if (ae.getSource() == makelower) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == changedriveletter) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == makeproper) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == makeupper) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == correctkeyformat) {
      new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == detectbrokenlinks) {
        int n = IOptionPane.showConfirmDialog(
                SkinManager.instance.getFrame("main_frame"),
                SkinManager.instance.getDialogMessageText("detect_broken_file_links"),
                SkinManager.instance.getDialogMessageTitle("detect_broken_file_links"),
                IOptionPane.YES_NO_OPTION);
            if (n != 0) {
              return;
            }
            new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == dataretriever) {
        if (RetrieveAlbumCoversProgressUI.instance.isVisible()) {
            new ErrorMessageThread("retrieval already in progress", "please wait for the other retrieval task to finish");
            return;
        }
        new Thread() {
            public void run() {
                SearchListMouse.instance.stopAlbumCoverRetrieval = false;
                RetrieveAlbumCoversProgressUI.instance.progressbar.setValue(0);
                RetrieveAlbumCoversProgressUI.instance.Display();
                SongLinkedList[] songs = RapidEvolutionUI.instance.getSelectedSearchSongs();
        for (int i = 0; i < songs.length; ++i) {
            if (RapidEvolution.instance.terminatesignal || SearchListMouse.instance.stopAlbumCoverRetrieval ) return;
            SongLinkedList song = songs[i];
            DataRetriever.updateInfo(song, true);   
            int progress = (i * 100) / songs.length;
            RetrieveAlbumCoversProgressUI.instance.progressbar.setValue(progress);
        }
        RetrieveAlbumCoversProgressUI.instance.Hide();
        }
        }.start();
    } else if (ae.getSource() == discogsretriever) {
        if (RetrieveAlbumCoversProgressUI.instance.isVisible()) {
            new ErrorMessageThread("retrieval already in progress", "please wait for the other retrieval task to finish");
            return;
        }
        new Thread() {
            public void run() {
                SearchListMouse.instance.stopAlbumCoverRetrieval = false;
                RetrieveAlbumCoversProgressUI.instance.progressbar.setValue(0);
                RetrieveAlbumCoversProgressUI.instance.Display();
                SongLinkedList[] songs = RapidEvolutionUI.instance.getSelectedSearchSongs();
        for (int i = 0; i < songs.length; ++i) {
            if (RapidEvolution.instance.terminatesignal || SearchListMouse.instance.stopAlbumCoverRetrieval ) return;
            SongLinkedList song = (SongLinkedList)songs[i];
            DataRetriever.updateInfo(song, false);   
            int progress = (i * 100) / songs.length;
            RetrieveAlbumCoversProgressUI.instance.progressbar.setValue(progress);
        }
        RetrieveAlbumCoversProgressUI.instance.Hide();
        }
        }.start();
    } else if (ae.getSource() == copyfields) {
        if (!RapidEvolutionUI.instance.copyfields_ui.isVisible()) {
            SongLinkedList[] songs = RapidEvolutionUI.instance.getSelectedSearchSongs();
            RapidEvolutionUI.instance.copyfields_ui.copyfieldsongs = songs;      
            RapidEvolutionUI.instance.copyfields_ui.Display();
        }
      //new PopupWorkerThread(ae).start();
    } else if (ae.getSource() == setalbum) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_album"));
      String initialvalue = new String("");
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      SongLinkedList[] songs = new SongLinkedList[selected.length];
      for (int i = 0; i < selected.length; ++i) {
        SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
        if (initialvalue.equals("")) initialvalue = song.getAlbum();
        songs[i] = song;
      }
      SetFieldsUI.instance.setfieldtype = 1;
      if (!OptionsUI.instance.disableautocomplete.isSelected()) 
          SetFieldsUI.instance.setfieldfield.setFieldIndex(RapidEvolution.getMusicDatabase().getAlbumIndex());
      SetField(songs, initialvalue);
    } else if (ae.getSource() == settime) {
      if (SetFieldsUI.instance.isVisible()) { SetFieldsUI.instance.requestFocus(); return; }
      SetFieldsUI.instance.setTitle(SkinManager.instance.getMessageText("set_fields_dialog_title_prefix") + SkinManager.instance.getMessageText("column_title_time"));
      SetFieldsUI.instance.setfieldtype = 3;
      SetField(RapidEvolutionUI.getSelectedSearchSongs());
    } else if (ae.getSource() == readtagsselected) {
      new ReadBatchTags(RapidEvolutionUI.getSelectedSearchSongs()).start();
    } else if (ae.getSource() == deleteselected) {
		RapidEvolutionUI.instance.deletesongs_ui.deleteSongs(RapidEvolutionUI.getSelectedSearchSongs());
    } else if ((ae.getSource() == importdata)) {
      ImportLib.InitiateImport();
    }
  }

  public void mousePressed(MouseEvent e) {
/*
    double x1 = e.getX();
    double y1 = e.getY();
    int[] rows = SearchPane.instance.searchtable.getSelectedRows();
    if (rows != null) {
    for (int i = 0; i < rows.length; i++) {
    for (int j = 0; j < SearchPane.instance.searchcolumnconfig.num_columns; ++j) {
    Rectangle rect3 = SearchPane.instance.searchtable.getCellRect(rows[i], j, true);
    if (rect3.contains(x1, y1)) {
    System.out.println("hrmm");
    e.consume();
    break;
    }
    }
    }
    }
*/
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

  public void SetField(SongLinkedList[] songs) {
    SetFieldsUI.instance.setfieldssongs = songs;
    SetFieldsUI.instance.display_parameter = (Object)"";
    SetFieldsUI.instance.Display();
  }

  public void SetField(SongLinkedList[] songs, String initialvalue) {
    SetFieldsUI.instance.setfieldssongs = songs;
    SetFieldsUI.instance.display_parameter = initialvalue;
    SetFieldsUI.instance.Display();
  }

  void SetFilename(SongLinkedList[] songs) {
    JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
    if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
    fc.addChoosableFileFilter(new InputFileFilter());
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setMultiSelectionEnabled(true);
    int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
    File tmp = fc.getSelectedFile();
    if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File[] files = fc.getSelectedFiles();
      files = FileUtil.sortfiles(files);
      int i = 0;
      if (files.length != songs.length) return;
      while (i < files.length) {
        SongLinkedList thissong = songs[i];
        thissong.setFilename(files[i].getAbsolutePath());
        SongDB.instance.UpdateSong(thissong, new OldSongValues(thissong));
        ++i;
      }
    }
  }

  public void PostInit() {
    generateplayselection.setText(SkinManager.instance.getMessageText("menu_option_generate_playlist"));
    setcurrent.setText(SkinManager.instance.getMessageText("menu_option_set_current_song"));
    addmixout.setText(SkinManager.instance.getMessageText("menu_option_add_mixout"));
    playselection.setText(SkinManager.instance.getMessageText("menu_option_play"));
    findlinkselection.setText(SkinManager.instance.getMessageText("menu_option_find_link"));
    detectkeys.setText(SkinManager.instance.getMessageText("menu_option_key"));
    detectrgad.setText(SkinManager.instance.getMessageText("menu_option_rga"));
    detectall.setText(SkinManager.instance.getMessageText("menu_option_all"));
    dataretriever.setText(SkinManager.instance.getMessageText("menu_option_retrieve_album_covers"));
    discogsretriever.setText(SkinManager.instance.getMessageText("menu_option_retrieve_info_from_discogs"));
    detectbpms.setText(SkinManager.instance.getMessageText("menu_option_bpm"));
    detectcolors.setText(SkinManager.instance.getMessageText("menu_option_color"));
    detectbeatintensity.setText(SkinManager.instance.getMessageText("menu_option_beat_intensity"));
    normalize.setText(SkinManager.instance.getMessageText("menu_option_normalize"));
    pitchshift.setText(SkinManager.instance.getMessageText("menu_option_time_pitch_shift"));
    audiomenu.setText(SkinManager.instance.getMessageText("menu_option_audio"));
    setstyles.setText(SkinManager.instance.getMessageText("menu_option_set_styles"));
    styles.setText(SkinManager.instance.getMessageText("menu_option_styles"));
    addtostyles.setText(SkinManager.instance.getMessageText("menu_option_add_to_styles"));
    removefromstyles.setText(SkinManager.instance.getMessageText("menu_option_remove_from_styles"));
    readtagsselected.setText(SkinManager.instance.getMessageText("menu_option_read"));
    writetagsselected.setText(SkinManager.instance.getMessageText("menu_option_write"));
    removetagsselected.setText(SkinManager.instance.getMessageText("menu_option_remove"));
    deleteselected.setText(SkinManager.instance.getMessageText("menu_option_delete_song"));
    importdata.setText(SkinManager.instance.getMessageText("import_data"));
    setfields.setText(SkinManager.instance.getMessageText("menu_option_set_fields"));
    fields.setText(SkinManager.instance.getMessageText("menu_option_fields"));
    copyfields.setText(SkinManager.instance.getMessageText("menu_option_copy_fields"));
    appendfields.setText(SkinManager.instance.getMessageText("menu_option_append_fields"));
    prependfields.setText(SkinManager.instance.getMessageText("menu_option_prepend_fields"));   
    setalbum.setText(SkinManager.instance.getMessageText("column_title_album"));
    setartist.setText(SkinManager.instance.getMessageText("column_title_artist"));
    setcomments.setText(SkinManager.instance.getMessageText("column_title_comments"));
    setsongname.setText(SkinManager.instance.getMessageText("column_title_title"));
    setremixer.setText(SkinManager.instance.getMessageText("column_title_remix"));
    setstartbpm.setText(SkinManager.instance.getMessageText("column_title_bpm_start"));
    setstartkey.setText(SkinManager.instance.getMessageText("column_title_key_start"));
    setendbpm.setText(SkinManager.instance.getMessageText("column_title_bpm_end"));
    setendkey.setText(SkinManager.instance.getMessageText("column_title_key_end"));
    settimesig.setText(SkinManager.instance.getMessageText("column_title_time_signature"));
    settrack.setText(SkinManager.instance.getMessageText("column_title_track"));
    settime.setText(SkinManager.instance.getMessageText("column_title_time"));
    setuser1.setText(SkinManager.instance.getMessageText("default_custom_field_1_1abel"));
    setuser2.setText(SkinManager.instance.getMessageText("default_custom_field_2_1abel"));
    setuser3.setText(SkinManager.instance.getMessageText("default_custom_field_3_1abel"));
    setuser4.setText(SkinManager.instance.getMessageText("default_custom_field_4_1abel"));
    setfilename.setText(SkinManager.instance.getMessageText("column_title_filename"));
    setcomments.setText(SkinManager.instance.getMessageText("column_title_song_comments"));
    setflags.setText(SkinManager.instance.getMessageText("menu_option_flags"));
    detectmenu.setText(SkinManager.instance.getMessageText("menu_option_detect"));
    tagsmenu.setText(SkinManager.instance.getMessageText("menu_option_tags"));
    specialmenu.setText(SkinManager.instance.getMessageText("menu_option_database"));
    correctkeyformat.setText(SkinManager.instance.getMessageText("menu_option_format_key_format"));
    detectbrokenlinks.setText(SkinManager.instance.getMessageText("menu_option_detect_broken_file_links"));
    changedriveletter.setText(SkinManager.instance.getMessageText("menu_option_change_drive_letters"));
    makelower.setText(SkinManager.instance.getMessageText("menu_option_convert_lower_case"));
    formatbpms.setText(SkinManager.instance.getMessageText("menu_option_format_bpm_values"));
    makeproper.setText(SkinManager.instance.getMessageText("menu_option_convert_proper_case"));
    makeupper.setText(SkinManager.instance.getMessageText("menu_option_convert_upper_case"));
    correcttracknums.setText(SkinManager.instance.getMessageText("menu_option_format_track_numbers"));
    searchformissingfiles.setText(SkinManager.instance.getMessageText("menu_option_update_file_locations"));
    assigntracknumbers.setText(SkinManager.instance.getMessageText("menu_option_assign_track_numbers"));
    parseremixer.setText(SkinManager.instance.getMessageText("menu_option_parse_remix_fields"));
    organizeFiles.setText(SkinManager.instance.getMessageText("menu_option_organize_song_directories"));
    queryserver.setText(SkinManager.instance.getMessageText("menu_option_query_server"));
    removefileextensions.setText(SkinManager.instance.getMessageText("menu_option_remove_file_extensions"));
    renamefiles.setText(SkinManager.instance.getMessageText("menu_option_rename_files"));
    resetPlayCount.setText(SkinManager.instance.getMessageText("menu_option_reset_play_count"));
    edititem.setText(SkinManager.instance.getMessageText("menu_option_edit"));
  }

}
