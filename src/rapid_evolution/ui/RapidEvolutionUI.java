package rapid_evolution.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.plaf.RootPaneUI;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.tree.TreePath;

import org.apache.log4j.Level;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import rapid_evolution.FileUtil;
import rapid_evolution.Filter;
import rapid_evolution.ImportLib;
import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SearchParser;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongList;
import rapid_evolution.SongStack;
import rapid_evolution.SongUtil;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.audio.BPMTapper;
import rapid_evolution.audio.Bpm;
import rapid_evolution.filefilters.ExcelFileFilter;
import rapid_evolution.filefilters.FileFormats;
import rapid_evolution.filefilters.M3UFileFilter;
import rapid_evolution.filefilters.SaveFileFilter;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.threads.OutOfMemoryThread;
import rapid_evolution.threads.SearchThread;
import rapid_evolution.threads.UpdateThread;
import rapid_evolution.ui.main.AddIdentifyThread;
import rapid_evolution.ui.main.DragTextField;
import rapid_evolution.ui.main.DragTextFieldPrev;
import rapid_evolution.ui.main.EditMixoutUI;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.NormalizationProgressUI;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.main.changecurrentsongthread;
import rapid_evolution.ui.styles.AddStyleUI;
import rapid_evolution.ui.styles.AddToStylesUI;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.MyStyleTreeRenderer;
import rapid_evolution.ui.styles.RemoveFromStyles;
import rapid_evolution.ui.styles.SetStylesUI;
import rapid_evolution.ui.styles.StylesUI;
import rapid_evolution.ui.UpdateSongUIUpdateThread;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.dnd.GhostGlassPane;
import com.mixshare.rapid_evolution.ui.swing.lookfeel.LookAndFeelManager;
import com.mixshare.rapid_evolution.ui.swing.tooltip.ToolTipRemover;

import org.jvnet.substance.SubstanceRootPaneUI;
import com.toedter.calendar.JDateChooser;

import javax.swing.JRootPane;

public class RapidEvolutionUI implements ActionListener {

    private static Logger log = Logger.getLogger(RapidEvolutionUI.class);
    
    public RapidEvolutionUI() {
    instance = this;
    init();
    setupMainWindow();
    setupDialogs();
    setupActionListeners();
  }

  static public RapidEvolutionUI instance = null;

  // main screen
//  public IFrame mainWindow;
  public StylesPane stylespanel;
  public SearchPane searchpanel;
  public MixoutPane mixoutpanel;
  public JButton importbutton = new REButton();
  public JButton exportbutton = new REButton();
  public JButton addexcludebutton;
  public JButton editcurrentsongbutton;
  public JCheckBox rating1checkbox = new RECheckBox();
  public JCheckBox rating2checkbox = new RECheckBox();
  public JCheckBox rating3checkbox = new RECheckBox();
  public JCheckBox rating4checkbox = new RECheckBox();
  public JCheckBox rating5checkbox = new RECheckBox();
  public JCheckBox filter_include_unrated = new RECheckBox();
  public JCheckBox minrating1checkbox = new RECheckBox();
  public JCheckBox minrating2checkbox = new RECheckBox();
  public JCheckBox minrating3checkbox = new RECheckBox();
  public JCheckBox minrating4checkbox = new RECheckBox();
  public JCheckBox minrating5checkbox = new RECheckBox();   
  public JDateChooser dateAddedsince = new JDateChooser();
  public DragTextField currentsongfield;
  public DragTextFieldPrev previoussongfield = new DragTextFieldPrev(32);
  public JCheckBox keylockcurrentsong;
  public JButton savelistbutton = new REButton();
  public JButton backbutton;
  public JButton nextbutton;

  // ui variables
  public SongLinkedList currentsong = null;
  public SongLinkedList previoussong = null;
  public String previousfilepath = new String();
  public Vector excludesong = null;
  public float bpmscale;
  private Key currentKey = null;
  public SongStack prevstack = null;
  public SongStack nextstack = null;
  public Vector suggestedsongs = new Vector();
  public Vector suggestedinfo = null;
  public Vector lastqueryresults;
  public int lastdragsourceindex = -1;
  public int rootsinvoked = 0;
  BPMTapper bpmtapper = new BPMTapper();

  // sub dialogs
  public MixGeneratorUI mixset_ui;
  public OptionsUI options_ui;
  public AddSongsUI addsongs_ui;
  public EditSongUI editsong_ui;
  public SyncUI sync_ui;
  public RootsUI roots_ui;
  public ExcludeUI exclude_ui;
  public MIDIPiano midipiano;
  public AddMatchQueryUI addmatchquery_ui;
  public EditMatchQueryUI editmatchquery_ui;
  public SuggestedMixesUI suggested_ui;
  public MixStartsWithUI mixstartswith_ui;
  public SelectMissingSongUI selectmissingsong_ui;
  public SongTrailUI songtrail_ui;
  public AddExcludeUI addexclude_ui;
  public ImportSpreadsheetUI importspreadsheet_ui;
  public AddMixoutUI addmixout_ui;
  public EditMixoutSongTrailUI editmixoutsongtrail_ui;
  public EditMixoutMixGenUI editmixoutmixgen_ui;
  public SetStylesUI setstyles_ui;
  public AddToStylesUI addtostyles_ui;
  public RemoveFromStyles removefromstyles_ui;
  public MediaPlayerUI mediaplayer_ui;
  public GeneratePlaylistUI generateplaylist_ui;
  public OrganizeFilesUI organizefiles_ui;
  public RenameFilesUI renamefiles_ui;
  public DeleteSongsUI deletesongs_ui;
  public FormatBpmsUI formatbpms_ui;
  public AddStyleUI addstyle_ui;
  public SetFieldsUI setfields_ui;
  public CopyFieldsUI copyfields_ui;
  public ImportMixmeisterUI importmixmeister_ui;
  public ImportITunesUI importitunes_ui;
  public ImportTraktorUI importtraktor_ui;
  public ImportMixvibesUI importmixvibes_ui;
  public SetFlagsUI setflags_ui;
  public EditStyleUI editstyle_ui;
  public LoginPromptUI loginprompt_ui;
  public EditMixoutUI editmixout_ui;
  public NormalizationProgressUI normalizeprogress_ui;
  public DetectAllBatchProgressUI detectallbatchprogress_ui;
  public DetectBpmBatchProgressUI detectbpmbatchprogress_ui;
  public DetectBeatIntensityBatchProgressUI detectbeatintensitybatchprogress_ui;
  public DetectKeyBatchProgressUI detectkeybatchprogress_ui;
  public DetectRGABatchProgressUI detectrgabatchprogress_ui;
  public RetrieveAlbumCoversProgressUI retrievelabumcoversprogress_ui;
  public RenameFilesProgressUI renamefilesprogress_ui;
  public ImportProgressUI importprogress_ui;
  public ExportProgressUI exportprogress_ui;
  public WriteTagsProgressUI writetagsprogress_ui;
  public ReadTagsProgressUI readtagsprogress_ui;
  public QueryServerProgressUI queryserverprogress_ui;
  public OrganizeFilesProgressUI organizefilesprogress_ui;
  public BrokenLinkSongsUI brokenlink_ui;
  
  public GhostGlassPane glassPane = new GhostGlassPane();  
  
  private HashMap table_config_map = new HashMap();
  
  // look and feel
  static public Insets stdinsetsize = new Insets(0, 5, 0, 5);
  public static LookAndFeelInfo[] inf = null;
  private static LookAndFeelInfo[] sortLookAndFeels(LookAndFeelInfo[] inf) {
      if (log.isTraceEnabled())
          log.trace("sortLookAndFeels(): # infos=" + inf.length);
      LookAndFeelInfo[] result = new LookAndFeelInfo[inf.length];
      boolean[] used = new boolean[inf.length];
      for (int i = 0; i < result.length; ++i) {
          String minName = null;
          int minIndex = -1;
          for (int j = 0; j < result.length; ++j) {
              if (!used[j]) {
                  String name = inf[j].getName();
                  if ((minName == null) || (name.compareTo(minName) < 0)) {
                      minName = name;
                      minIndex = j;
                  }
              }
          }
          result[i] = inf[minIndex];
          if (log.isTraceEnabled())
              log.trace("sortLookAndFeels(): sorted L&F=" + result[i]);
          used[minIndex] = true;
      }      
      return result;
  }
  
  public static List discoverSubstanceSkins(String jarName,
            String packageName) {
        ArrayList classes = new ArrayList();
        packageName = packageName.replaceAll("\\.", "/");
        if (log.isDebugEnabled())
            log.debug("discoverSubstanceSkins(): jar=" + jarName + ", packageName=" + packageName);
        try {
            File test1 = new File("libs/" + jarName);
            if (!test1.exists())
                test1 = new File("lib/" + jarName);
            JarInputStream jarFile = new JarInputStream(new FileInputStream(test1));
            JarEntry jarEntry;

            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if ((jarEntry.getName().startsWith(packageName))
                        && (jarEntry.getName().endsWith(".class"))) {
                    String className = jarEntry.getName().replaceAll("/", "\\.");
                    className = className.substring(0, className.length() - 6);
                    try {
                        Object inst = Class.forName(className).newInstance();
                        if (inst instanceof LookAndFeel) {
                            LookAndFeel looknfeel = (LookAndFeel)inst;
                            if (log.isDebugEnabled())
                                log.debug("discoverSubstanceSkins(): found=" + className);
                            classes.add(looknfeel);
                        }
                    } catch (java.lang.InstantiationException e) {
                    } catch (java.lang.IllegalAccessException e) {                        
                    } catch (Exception e) {
                        log.trace("discoverSubstanceSkins(): error Exception", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("discoverSubstanceSkins(): error Exception", e);
        }
        return classes;
    }
  
  public static void setLookAndFeel() {
    try {
        UIManager.installLookAndFeel("Substance", "org.jvnet.substance.SubstanceLookAndFeel");
        List substanceSkins = discoverSubstanceSkins("substance.jar", "org.jvnet.substance.skin");
        log.debug("setLookAndFeel(): # substance skins=" + substanceSkins.size());
        Iterator ssIter = substanceSkins.iterator();
        while (ssIter.hasNext()) {
            LookAndFeel looknfeel = (LookAndFeel)ssIter.next();
            String name = looknfeel.getName();
            UIManager.installLookAndFeel(name, looknfeel.getClass().getName());            
        }
        
        try {
            String lcOSName = System.getProperty("os.name").toLowerCase();
            boolean MAC_OS_X = lcOSName.startsWith("mac os x");
            if (MAC_OS_X)
                UIManager.installLookAndFeel("Quaqua", "ch.randelshofer.quaqua.QuaquaLookAndFeel");
        } catch (Exception e) {
            log.error("setLookAndFeel(): error installing quaqua look and feel");
        }                        
        
        inf = UIManager.getInstalledLookAndFeels();
        inf = sortLookAndFeels(inf);
        
        LookAndFeelManager.loadLookAndFeel();
        
    } catch (Exception e) {
        log.error("setLookAndFeel(): error Exception", e);
    }
  }

  private void init() {
//        ColorUIResource selectcolor2 = (ColorUIResource/Color)UIManager.get("List.selectionForeground") ;
//        MyStyleRenderer.foregroundlistcolor = new Color(selectcolor2.getRed(), selectcolor2.getGreen(), selectcolor2.getBlue());
//        ColorUIResource selectcolor3 = (ColorUIResource/Color)UIManager.get("List.foreground") ;
//        MyStyleRenderer.foregroundlistnonselectedcolor = new Color(selectcolor3.getRed(), selectcolor3.getGreen(), selectcolor3.getBlue());
      
      ToolTipManager.sharedInstance().setDismissDelay(15000);
      if (inf != null) {
        String[] array = new String[inf.length];
        int index = -1;
        for (int i = 0; i < inf.length; ++i) {
          array[i] = inf[i].getName();
          if (LookAndFeelManager.getLookAndFeelName().equals(array[i])) index = i;
        }
        OptionsUI.lookandfeelcombo = new REComboBox(array);
        if (index >= 0) OptionsUI.lookandfeelcombo.setSelectedIndex(index);
      }

      // initialize main window (frame)
      JFrame.setDefaultLookAndFeelDecorated(true);
      JDialog.setDefaultLookAndFeelDecorated(true);
      Toolkit.getDefaultToolkit().setDynamicLayout(true);
      System.setProperty("sun.awt.noerasebackground","true");
      
//      mainWindow = new IFrame();
      midipiano = new MIDIPiano("midi_keyboard_dialog");
      midipiano.InitMIDI();
      mixset_ui = new MixGeneratorUI("mix_generator_dialog");
      options_ui = new OptionsUI("options_dialog");
      addsongs_ui = new AddSongsUI("add_songs_dialog");
      editsong_ui = new EditSongUI("edit_song_dialog");
      sync_ui = new SyncUI("sync_dialog");
      suggested_ui = new SuggestedMixesUI("suggested_mixouts_dialog");
      roots_ui = new RootsUI("roots_dialog");
      exclude_ui = new ExcludeUI("exclude_songs_dialog");
      addmatchquery_ui = new AddMatchQueryUI("add_songs_query_results_dialog");
      editmatchquery_ui = new EditMatchQueryUI("edit_song_query_results_dialog");
      mixstartswith_ui = new MixStartsWithUI("mix_generator_starts_with_dialog");
      setstyles_ui = new SetStylesUI("set_styles_dialog");
      addtostyles_ui = new AddToStylesUI("add_to_styles_dialog");
      removefromstyles_ui = new RemoveFromStyles("remove_from_styles_dialog");
      normalizeprogress_ui = new NormalizationProgressUI("normalization_progress_dialog");
      detectallbatchprogress_ui = new DetectAllBatchProgressUI("detect_all_batch_progress_dialog");
      detectkeybatchprogress_ui = new DetectKeyBatchProgressUI("detect_key_batch_progress_dialog");
      detectrgabatchprogress_ui = new DetectRGABatchProgressUI("detect_rga_batch_progress_dialog");
      retrievelabumcoversprogress_ui = new RetrieveAlbumCoversProgressUI("retrieve_album_cover_progress_dialog");
      renamefilesprogress_ui = new RenameFilesProgressUI("rename_files_progress_dialog");
      importprogress_ui = new ImportProgressUI("import_progress_dialog");
      exportprogress_ui = new ExportProgressUI("export_progress_dialog");
      writetagsprogress_ui = new WriteTagsProgressUI("write_tags_progress_dialog");
      queryserverprogress_ui = new QueryServerProgressUI("query_server_progress_dialog");
      organizefilesprogress_ui = new OrganizeFilesProgressUI("organize_songs_progress_dialog");
      readtagsprogress_ui = new ReadTagsProgressUI("read_tags_progress_dialog");
      brokenlink_ui = new BrokenLinkSongsUI("broken_file_links_dialog");
      detectbpmbatchprogress_ui = new DetectBpmBatchProgressUI("detect_bpm_batch_progress_dialog");
      detectbeatintensitybatchprogress_ui = new DetectBeatIntensityBatchProgressUI("detect_beat_intensity_batch_progress_dialog");
      
      // initialize gui elements:
      // main frame
      addexcludebutton = new REButton();
      addexcludebutton.setEnabled(false);

      editcurrentsongbutton = new REButton();
      editcurrentsongbutton.setEnabled(false);
      currentsongfield = new DragTextField(32);

//      GhostDropAdapter componentAdapter = new GhostComponentAdapter(RapidEvolutionUI.instance.glassPane, "button_pushed");
//      currentsongfield.addMouseListener(componentAdapter);
//      currentsongfield.addMouseMotionListener(new GhostMotionAdapter(RapidEvolutionUI.instance.glassPane)); 
      
      currentsongfield.setDragEnabled(true);
      previoussongfield.setDragEnabled(true);
      previoussongfield.setDropTarget(null);
      previoussongfield.setFocusable(false);

      bpmscale = 8;
      keylockcurrentsong = new RECheckBox();
      keylockcurrentsong.setEnabled(false);

      rating1checkbox.setEnabled(false);
      rating2checkbox.setEnabled(false);
      rating3checkbox.setEnabled(false);
      rating4checkbox.setEnabled(false);
      rating5checkbox.setEnabled(false);
      
      backbutton = new REButton();
      backbutton.setEnabled(false);
      nextbutton = new REButton();
      nextbutton.setEnabled(false);

      NumberFormat bpmFormat = NumberFormat.getNumberInstance();
      bpmFormat.setMaximumFractionDigits(2);
      
  }

  public ColumnConfig getTableConfig(JTable table) {
      return (ColumnConfig)table_config_map.get(table);
  }
  
  public ColumnConfig getTableConfig(DefaultSortTableModel ds) {
      ColumnConfig config = null;
      if ((SearchPane.instance != null) && (SearchPane.instance.searchtable != null) && (ds == SearchPane.instance.searchtable.getModel())) {
        config = SearchPane.instance.searchcolumnconfig;
      }
      else if ((MixoutPane.instance != null) && (MixoutPane.instance.mixouttable != null) && (ds == MixoutPane.instance.mixouttable.getModel())) {
        config = MixoutPane.instance.mixoutcolumnconfig;
      }
      else if ((SyncUI.instance != null) && (SyncUI.instance.synctable != null) && (ds == SyncUI.instance.synctable.getModel())) {
        config = SyncUI.instance.synccolumnconfig;
      }
      else if ((AddMatchQueryUI.instance != null) && (AddMatchQueryUI.instance.matchtable != null) && (ds == AddMatchQueryUI.instance.matchtable.getModel())) {
        config = AddMatchQueryUI.instance.matchcolumnconfig;
      }
      else if ((EditMatchQueryUI.instance != null) && (EditMatchQueryUI.instance.matchtable2 != null) && (ds == EditMatchQueryUI.instance.matchtable2.getModel())) {
        config = EditMatchQueryUI.instance.matchcolumnconfig2;
      }
      else if ((SuggestedMixesUI.instance != null) && (SuggestedMixesUI.instance.suggestedtable != null) && (ds == SuggestedMixesUI.instance.suggestedtable.getModel())) {
        config = SuggestedMixesUI.instance.suggestedcolumnconfig;
      }
      else if ((ExcludeUI.instance != null) && (ExcludeUI.instance.excludetable != null) && (ds == ExcludeUI.instance.excludetable.getModel())) {
        config = ExcludeUI.instance.excludecolumnconfig;
      }
      else if ((RootsUI.instance != null) && (RootsUI.instance.rootstable != null) && (ds == RootsUI.instance.rootstable.getModel())) {
        config = RootsUI.instance.rootscolumnconfig;
      } else if ((OptionsUI.instance != null) && (OptionsUI.instance.available_skins_table != null) && (ds == OptionsUI.instance.available_skins_table.getModel())) {
          config = OptionsUI.instance.availableskinconfig;
      }      
      return config;
  }
  
  // set up the main window in a relative layout so that it resizes correctly
  private void setupMainWindow() {

    //mainWindow.setIconImage(new ImageIcon(imgURL).getImage());
//    mainWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    stylespanel = new StylesPane();
    mixoutpanel = new MixoutPane();
    searchpanel = new SearchPane();

  }

  void setupDialogs() {

    // mix set generator dialog
    JDialog.setDefaultLookAndFeelDecorated(true);

//    RapidEvolution.instance.mixset_ui = new MixGeneratorUI(RapidEvolution.instance.mainWindow);

    editstyle_ui = new EditStyleUI("edit_style_dialog");
    selectmissingsong_ui = new SelectMissingSongUI("playlist_select_missing_song_dialog");
    songtrail_ui = new SongTrailUI("song_trail_dialog");
    addexclude_ui = new AddExcludeUI("add_exclude_dialog");
    importspreadsheet_ui = new ImportSpreadsheetUI("import_spreadsheet_dialog");
    addmixout_ui = new AddMixoutUI("add_mixout_dialog");
    editmixoutsongtrail_ui = new EditMixoutSongTrailUI("songtrail_edit_mixout_dialog");
    editmixoutmixgen_ui = new EditMixoutMixGenUI("mixgen_edit_mixout_dialog");
    mediaplayer_ui = new MediaPlayerUI("media_player_dialog");
    generateplaylist_ui = new GeneratePlaylistUI("generate_playlist_dialog");
    organizefiles_ui = new OrganizeFilesUI("organize_files_dialog");
    renamefiles_ui = new RenameFilesUI("rename_files_dialog");
    formatbpms_ui = new FormatBpmsUI("format_bpm_values_dialog");
    deletesongs_ui = new DeleteSongsUI("delete_songs_dialog");
    addstyle_ui = new AddStyleUI("add_style_dialog");
    setfields_ui = new SetFieldsUI("set_fields_dialog");
    copyfields_ui = new CopyFieldsUI("copy_fields_dialog");
    importmixmeister_ui = new ImportMixmeisterUI("import_mixmeister_dialog");
    importitunes_ui = new ImportITunesUI("import_itunes_dialog");
    importtraktor_ui = new ImportTraktorUI("import_traktor_dialog");
    importmixvibes_ui = new ImportMixvibesUI("import_mixvibes_dialog");
    setflags_ui = new SetFlagsUI("set_flags_dialog");
    loginprompt_ui = new LoginPromptUI("mixshare_login_dialog");
    editmixout_ui = new EditMixoutUI("edit_mixout_dialog");

//    mixoutpanel.mixoutpanel.setMinimumSize(new Dimension(270, 95));
//    stylespanel.stylepanel.setMinimumSize(new Dimension(115, 95));
//    searchpanel.mainpanel.setMinimumSize(new Dimension(350, 140));

  }

  static public SongLinkedList[] getSelectedSearchSongs() {
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      SongLinkedList[] songs = new SongLinkedList[selected.length];
    for (int i = 0; i < selected.length; ++i) {
      SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns);
      songs[i] = song;
    }
    return songs;
  }

  static public Vector getSelectedSearchSongsVector() {
      int[] selected = SearchPane.instance.searchtable.getSelectedRows();
      Vector songs = new Vector(selected.length);
    for (int i = 0; i < selected.length; ++i) {
        songs.add(SearchPane.instance.searchtable.getModel().getValueAt(selected[i], SearchPane.instance.searchcolumnconfig.num_columns));
    }
    return songs;
  }
  
  public void ConfirmAddExcludes() {
    addexclude_ui.Hide();
    SongLinkedList firstexclude = null;
    for (int i = 0; i < excludesong.size(); ++i) {
      SongLinkedList excludedsong = (SongLinkedList)excludesong.get(i);
      currentsong.insertExclude(excludedsong);
      if (i == 0) firstexclude = excludedsong;
    }
    if (addexclude_ui.addreverseexcludebt.isSelected() && (excludesong.size() == 1)) firstexclude.insertExclude(currentsong);
    exclude_ui.Redrawexclude();
    if (rootsinvoked == 0) { SearchPane.instance.searchtable.requestFocus(); addexcludebutton.setEnabled(false); }
    else if (rootsinvoked == 1) { roots_ui.rootstable.requestFocus(); roots_ui.rootsaddexclude.setEnabled(false); }
    else { suggested_ui.suggestedtable.requestFocus(); suggested_ui.suggestedaddexclude.setEnabled(false); }
//      RecreateExcludeColumns();
  }

  public void RemoveExclude(SongLinkedList song) {
    for (int i = 0; i < currentsong.getNumExcludeSongs(); ++i) {
      if (currentsong.exclude_songs[i] == song.uniquesongid) currentsong.removeExclude(i);
    }
  }

  public void UpdateSongRoutine(SongLinkedList song, OldSongValues old_values) {
      UpdateSongRoutine(song, old_values, true);
  }
  
  public void UpdateSongRoutine(SongLinkedList song, OldSongValues old_values, boolean redraw) {
      javax.swing.SwingUtilities.invokeLater(new UpdateSongUIUpdateThread(song, redraw, old_values));
  }

  public void change_current_song(SongLinkedList newsong, float bpmdiff, boolean usebpmdiff, boolean dontchangesongtrail) {
      javax.swing.SwingUtilities.invokeLater(new changecurrentsongthread(newsong, bpmdiff, usebpmdiff, dontchangesongtrail));
  }

  public Semaphore SearchingSemaphore = new Semaphore(1);
  boolean searching = false;
  public boolean bpmsearched = false;
  public boolean findsearched = false;
  public boolean keysearched = false;
  public void SearchRoutine() {
      SearchRoutine(false);
  }
  
  public boolean isSearching() { return searching; }
  
  public boolean searchpending = false;
  private SearchParser parsewords = null;
  public void SearchRoutine(boolean filter) {
      if (searching) {
          log.trace("SearchRoutine(): already searching, will set searchpending flag");
          searchpending = true;
          return;
      }
    try {
    SearchingSemaphore.acquire();
    log.trace("SearchRoutine(): starting");    
    searching = true;
    parsewords = new SearchParser((filter && !OptionsUI.instance.filteraffectedbysearch.isSelected()) ? "" : SearchPane.instance.searchfield.getText());
    if (!filter) {
        SearchPane.instance.searchhistorybutton.addSearchText(SearchPane.instance.searchfield.getText());
    }
    SearchPane.searchdisplaylist = new SongList();
    Iterator songiter = null;
    Filter use_filter = null;
    if (filter || (OptionsUI.instance.usefiltering.isSelected() && !OptionsUI.instance.filteraffectedbysearch.isSelected())) {
    if (OptionsUI.instance.enablefilter.isSelected()) {
        if (!OptionsUI.instance.filter2disable.isSelected() &&
                !OptionsUI.instance.filter3disable.isSelected()) {
            use_filter = OptionsUI.instance.filter3;
        } else if (!OptionsUI.instance.filter2disable.isSelected()) {
            use_filter = OptionsUI.instance.filter3;
        } else {
            use_filter = OptionsUI.instance.filter3;
        }
    }    
    while ((use_filter != null) && use_filter.allSelected()) use_filter = use_filter.parent;
    if (use_filter != null) {        
        Collection coll = use_filter.songs.keySet();
        if (coll != null) songiter = coll.iterator();
    }
    }
    if (songiter != null) {
        boolean oldkeysearch = keysearched;
        boolean oldbpmsearched = bpmsearched;
        boolean oldfidsearched = findsearched;
        if (filter) {
            keysearched = false;
            bpmsearched = false;
            findsearched = true;
        }
        SongList lastlink = null;
        while (songiter.hasNext()) {
            SongLinkedList song = (SongLinkedList)songiter.next();
            if (passesSearchTest(song, true)) {
                lastlink = SearchPane.searchdisplaylist.insert(song, lastlink);
            }
        }
        keysearched = oldkeysearch;
        bpmsearched = oldbpmsearched;
        findsearched = oldfidsearched;
    } else {
        SongList lastlink = null;
        SongLinkedList siter  = SongDB.instance.SongLL;
        while (siter != null) {        
            if (passesSearchTest(siter, true)) {
                lastlink = SearchPane.searchdisplaylist.insert(siter, lastlink);
            }
            siter = siter.next;
        }
    }
    if (SearchPane.searchdisplaylist.song == null) {
        SearchPane.searchdisplaylist = null;
        SearchPane.instance.clearresultsbutton.setEnabled(false);
         RapidEvolutionUI.instance.savelistbutton.setEnabled(false);
    }
    else {
        SearchPane.instance.clearresultsbutton.setEnabled(true);
        RapidEvolutionUI.instance.savelistbutton.setEnabled(true);
    }
    SearchPane.instance.RedrawSearchTable();
    if (OptionsUI.instance.disable_multiple_delect.isSelected()) 
        SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    else SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);                
    
    if (!filter) {
        if (OptionsUI.instance.filteraffectedbysearch.isSelected()) {
            OptionsUI.instance.filter1.updateList();
        }
    }
    
    } catch (Exception e) { }    
    searching = false;
    SearchingSemaphore.release();
    
    EditSongUI.instance.nextbutton.setEnabled(false);
    EditSongUI.instance.backbutton.setEnabled(false);
    
    if (searchpending) {
        searchpending = false;
        SearchRoutine();
    }
    if (log.isTraceEnabled()) log.trace("SearchRoutine(): used memory=" + String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "mb");
    
  }

  public Key getCurrentKey() {
      return currentKey;
  }
  public void setCurrentKey(Key key) {
      currentKey = key;
      SongLinkedList.resetColorCache();
  }
  
  public boolean passesSearchTest(SongLinkedList siter, boolean do_styles_test) {
      //if (log.isTraceEnabled()) 
          //log.trace("passesSearchTest(): song=" + siter + ", do_styles_test=" + do_styles_test);
      
      if (do_styles_test) {
          // filter styles
          if (!(!OptionsUI.instance.searchwithinstyles.isSelected() && findsearched)) {
              if (!stylespanel.isMemberOfCurrentStyle(siter)) return false;      
          }
      }
      
      // minimum rating
      int minrating = 0;
      if (minrating5checkbox.isSelected()) minrating = 5;
      else if (minrating4checkbox.isSelected()) minrating = 4;
      else if (minrating3checkbox.isSelected()) minrating = 3;
      else if (minrating2checkbox.isSelected()) minrating = 2;
      else if (minrating1checkbox.isSelected()) minrating = 1;
      if (!((siter.getRating() == 0) && filter_include_unrated.isSelected())) {
          if (siter.getRating() < minrating) return false;      
      }
      
      // date added since...
      try {
          Date date = dateAddedsince.getDate();
          if (date != null) {
              if (log.isTraceEnabled()) {
                  log.trace("passesSearchTest(): cutoff date=" + date + ", song date=" + siter.getDateAdded());
              }
              long cutoffTime = date.getTime();
              long songTime = siter.getDateAdded().getTime();
              long dayInMillis = 1000 * 60 * 60 * 24;
              if (songTime + dayInMillis - 1 < cutoffTime)
                  return false;
//              if (siter.getDateAdded().before(date))
//                  return false;
          }
      } catch (Exception e) {
          log.trace("passesSearchTest(): error Exception", e);
      }
      
      // do key/bpm filtering
      if (keysearched) {
          if ((siter.getStartbpm() == 0) || (!siter.getStartKey().isValid())) return false;
          else if (currentsong == null) {
              float bpmdiff = SongUtil.get_bpmdiff(siter.getStartbpm(), RapidEvolution.instance.getActualBpm());
              if (Math.abs(bpmdiff) > bpmscale) return false;
              else if (!Key.getClosestKeyRelation(siter, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey()).isCompatible()) return false;
          } else {
              float bpmdiff = SongUtil.get_bpmdiff(siter.getStartbpm(), RapidEvolution.instance.getActualBpm());
              if (Math.abs(bpmdiff) > bpmscale) return false;
              else if (!Key.getClosestKeyRelation(siter, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey()).isCompatible()) return false;
          }
      } else if (bpmsearched) {
          if (siter.getStartbpm() == 0) return false;
          else {
              float bpmdiff = SongUtil.get_bpmdiff(siter.getStartbpm(), RapidEvolution.instance.getActualBpm());
              if (Math.abs(bpmdiff) > bpmscale) return false;
              else { }
          }
      }      

      StringBuffer searchtext = new StringBuffer();
      searchtext.append(siter.getArtist());
      searchtext.append(" ");
      searchtext.append(siter.getAlbum());
      searchtext.append(" ");
      searchtext.append(siter.getTrack());
      searchtext.append(" ");
      searchtext.append(siter.getSongname());
      searchtext.append(" ");
      searchtext.append(siter.getRemixer());
      searchtext.append(" ");
      searchtext.append(siter.getComments());
      searchtext.append(" ");
      searchtext.append(siter.getUser1());
      searchtext.append(" ");
      searchtext.append(siter.getUser2());
      searchtext.append(" ");
      searchtext.append(siter.getUser3());
      searchtext.append(" ");
      searchtext.append(siter.getUser4());
      searchtext.append(" ");
      searchtext.append(siter.getFileName());

      // if it doesn't match any of the search terms, return false
      if (parsewords != null) {
          if (!parsewords.isEmpty() &&
                  !parsewords.getStatus(searchtext.toString())) return false;
      }
      
      if ((SearchPane.instance.shownonvinylradio.isSelected()) && siter.getVinylOnly()) return false;
      if ((SearchPane.instance.showvinylradio.isSelected()) && siter.getNonVinylOnly()) return false;
      if (OptionsUI.instance.preventrepeats.isSelected() && (!findsearched || OptionsUI.instance.findallpreventrepeats.isSelected())) {
        if (currentsong == siter) return false;
        SongStack ssiter = prevstack;
        while (ssiter != null) {
          // to do: optimize repeats to not do string comparisons...
          if (ssiter.songid == siter.uniquesongid) return false;
          ssiter = ssiter.next;
        }
      }
      if ((keysearched || bpmsearched) && siter.isDisabled() && OptionsUI.instance.donotshowdisabledosngs.isSelected()) return false;
      if ((currentsong != null) && (keysearched || bpmsearched) && OptionsUI.instance.excludesongsonsamerecord.isSelected()) {
        if ((siter.getAlbum().toLowerCase().equals(currentsong.getAlbum().toLowerCase())) && siter.getVinylOnly() && currentsong.getVinylOnly()) {
          int type1 = StringUtil.gettracktype(currentsong.getTrack());
          int type2 = StringUtil.gettracktype(siter.getTrack());
          if ((type1 >= 0) && (type2 >=0) && (type1 == type2)) return false;
        }
      }

      if (!findsearched && (OptionsUI.instance.excludesongsondonottrylist.isSelected() && (currentsong != null))) {
        // to do: optimize exclude list to not involve string comparison
        for (int i = 0; i < currentsong.getNumExcludeSongs(); ++i)
          if (currentsong.exclude_songs[i] == siter.uniquesongid) return false;
      }
      if ((currentsong != null) && !findsearched) {
        for (int i = 0; i < currentsong.getNumMixoutSongs(); ++i)
          if (currentsong.mixout_songs[i] == siter.uniquesongid) return false;
      }
      if ((!findsearched && (currentsong != null) && (!SongLinkedList.determinetimesigcompatibility(siter, currentsong)))) return false;
      return true;
  }
  
  public void ClearSearch() {
      SearchPane.searchdisplaylist = null;
      SearchPane.instance.RedrawSearchTable();
      if (OptionsUI.instance.disable_multiple_delect.isSelected()) 
          SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      else SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);                      
      if (OptionsUI.instance.filteraffectedbysearch.isSelected()) {
          OptionsUI.instance.filter1.updateList();
      }
  }
  
  public void Search() {
    if (!searching) {
        javax.swing.SwingUtilities.invokeLater(new SearchThread());
    }
  }

  public void changecurrentroutine() {
      log.debug("changecurrentroutine(): begin...");
    if (stylespanel.dynamixstylescheckbox.isSelected()) {
        StylesPane.instance.highlightCurrentSongStyles();
    }
    // removal of excludes
    if (OptionsUI.instance.excludesongsondonottrylist.isSelected() && bpmsearched) {
        if (currentsong.getNumExcludeSongs() > 0) {
      String tempstr;
      SongLinkedList[] exclude_list = new SongLinkedList[currentsong.getNumExcludeSongs()];
      for (int m = 0; m < currentsong.getNumExcludeSongs(); ++m) exclude_list[m] = SongDB.instance.NewGetSongPtr(currentsong.exclude_songs[m]);
      for (int c = 0; c < SearchPane.instance.searchtable.getModel().getRowCount(); ++c) {
        SongLinkedList srchsong = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(c, SearchPane.instance.searchcolumnconfig.num_columns);
        for (int m = 0; m < currentsong.getNumExcludeSongs(); ++m) {
          if (exclude_list[m] == srchsong) {
            SearchPane.instance.searchtable.remove(c);
            --c;
          }
        }
      }
        }
    }

    if (isViewExcludesVisible()) ExcludeUI.instance.Redrawexclude();
    
    if (isRootsButtonVisible()) RootsUI.instance.RedrawRoots();
 
    if (isMixesButtonVisible()) SuggestedMixesUI.instance.RedrawSuggested();
    
    if (isSyncButtonVisible()) {
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
          if (iter != currentsong) {
            if ( (iter.getArtist().equalsIgnoreCase(currentsong.getArtist())) &&
                 (iter.getSongname().equalsIgnoreCase(currentsong.getSongname()))) {
              boolean same = true;
              if ((!iter.getRemixer().equals("")) && (!currentsong.getRemixer().equals(""))) {
                if (!iter.getRemixer().equalsIgnoreCase(currentsong.getRemixer())) same = false;
              }
              if (same) {
                SyncUI.instance.addedsyncsongs.add(iter);
              }
            }
          }
          iter = iter.next;
        }
        SyncUI.instance.Redrawsync();
        }        
    
    songtrail_ui.RedrawSongTrailRoutine();
  }

  public boolean isViewExcludesVisible() {
      try {
          JButton excludesbutton = (JButton)SkinManager.instance.getObject("view_excludes_button");
          if ((excludesbutton != null) && excludesbutton.isVisible()) {
              return true;
          }
      } catch (Exception e) { }
      return false;
  }
  
  public boolean isRootsButtonVisible() {
      try {
          JButton rootsbutton = (JButton)SkinManager.instance.getObject("current_song_roots_button");
          if ((rootsbutton != null) && rootsbutton.isVisible()) {
              return true;
          }        
      } catch (Exception e) { }
      return false;
  }
  
  public boolean isMixesButtonVisible() {
      try {
          JButton mixesbutton = (JButton)SkinManager.instance.getObject("current_song_mixes_button");
          if ((mixesbutton != null) && mixesbutton.isVisible()) {
              return true;
          }        
      } catch (Exception e) { }        
      return false;
  }
  
  public boolean isSyncButtonVisible() {
      try {
          JButton syncbutton = (JButton)SkinManager.instance.getObject("current_song_sync_button");
          if ((syncbutton != null) && syncbutton.isVisible()) {
              return true;
          }        
      } catch (Exception e) { }        
      return false;            
  }
  
  public void UpdateRoutine() {
      UpdateRoutine(false);
  }
  public void UpdateRoutine(boolean changecurrent) {
      javax.swing.SwingUtilities.invokeLater(new UpdateTempoUIThread(changecurrent));
  }

  public SongLinkedList currentlyselectedsong = null;
  
  public boolean isStyleOfSelected(StyleLinkedList style) {
      try {
          if (currentlyselectedsong != null) {
              if (style.containsLogical(currentlyselectedsong)) return true;
          }
        } catch (Exception e) { log.error("isStyleOfSelected(): error", e); }
        return false;
  }

  
  public void MatchSelected(boolean editdlgmode) {
		if (!editdlgmode) {
			AddMatchQueryUI.instance.Hide();
			if (AddMatchQueryUI.instance.matchtable.getSelectedRow() < 0)
				return;
			// SongLinkedList onesong =
			// (SongLinkedList)AddMatchQueryUI.instance.matchesvector.get(AddMatchQueryUI.instance.matchtable.getSelectedRow());
			SongLinkedList onesong = (SongLinkedList) AddMatchQueryUI.instance.matchtable
					.getModel()
					.getValueAt(
							AddMatchQueryUI.instance.matchtable
									.getSelectedRow(),
							AddMatchQueryUI.instance.matchcolumnconfig.num_columns);

			if (OptionsUI.instance.donotquerycomments.isSelected())
				onesong.setComments(new String(""));
			if (OptionsUI.instance.donotquerystyles.isSelected())
				onesong.stylelist = new String[0];

			AddSongsUI.instance.PopulateAddSongDialog(onesong, OptionsUI.instance.overwritewhenquerying.isSelected(), !OptionsUI.instance.donotquerystyles.isSelected());

		} else {
			EditMatchQueryUI.instance.setVisible(false);
			if (EditMatchQueryUI.instance.matchtable2.getSelectedRow() < 0)
				return;
			// SongLinkedList onesong =
			// (SongLinkedList)EditMatchQueryUI.instance.matchesvector2.get(EditMatchQueryUI.instance.matchtable2.getSelectedRow());
			SongLinkedList onesong = (SongLinkedList) EditMatchQueryUI.instance.matchtable2
					.getModel()
					.getValueAt(
							EditMatchQueryUI.instance.matchtable2
									.getSelectedRow(),
							EditMatchQueryUI.instance.matchcolumnconfig2.num_columns);

			if (OptionsUI.instance.donotquerycomments.isSelected())
				onesong.setComments(new String(""));
			if (OptionsUI.instance.donotquerystyles.isSelected())
				onesong.stylelist = new String[0];

			EditSongUI.instance.PopulateEditSongDialog(onesong, OptionsUI.instance.overwritewhenquerying.isSelected(), !OptionsUI.instance.donotquerystyles.isSelected());
			
		}

	}

  public Vector filenames = new Vector();
  public void AcceptBrowseAddSongs(File[] files, boolean show) {
      if ((files != null) && (files.length == 1)) {
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
              File file = iter.getFile();
              if (file.equals(files[0])) {
                  // edit song instead of adding
                  EditSongUI.instance.EditSong(iter);
                  return;
              }
              iter = iter.next;
          }
      }
      filenames.clear();
    files = FileUtil.sortfiles(files);
    int i = 0;
    while (i < files.length) {
      if (!files[i].isDirectory()) {
        String extension = FileUtil.getExtension(files[i]);

        if (extension != null) {
          if (FileFormats.acceptsFile(files[i].getName()))
            filenames.add(files[i].getAbsolutePath());
        }
      } else {
        FileUtil.RecurseFileTree(files[i].getAbsolutePath(), filenames);
      }
      ++i;
    }
    if (filenames.size() >= 1) {
      if (show) AddSongsUI.instance.InitiateAddSongs(false);
      AddSongsUI.instance.addsongsfilenamefield.setText((String)filenames.get(0));
      AddSongsUI.instance.addsongsreadtagbutton.setEnabled(true);
      AddSongsUI.instance.addsongswritetagbutton.setEnabled(true);
      AddSongsUI.instance.addsongsplaybutton.setEnabled(true);
      new AddIdentifyThread(AddIdentifyThread.addseqnum).start();
      filenames.remove(0);
    }
    if (filenames.size() >= 1) {
      AddSongsUI.instance.addsongsskipbutton.setEnabled(true);
      AddSongsUI.instance.addallbutton.setEnabled(true);
    } else {
      AddSongsUI.instance.addsongsskipbutton.setEnabled(false);
      AddSongsUI.instance.addallbutton.setEnabled(false);
    }
  }

  public void UpdateStyleSimilarityRoutine() {
    try {
      if (!SearchPane.instance.recreatingsearch) {
          
          int stylesimilarity_index = SearchPane.instance.getSearchColumnModelIndex(SkinManager.instance.getMessageText("column_title_style_similarity"));
          if (stylesimilarity_index >= 0) {
              for (int r = 0; r < SearchPane.instance.searchtable.getRowCount(); ++r) {
                  SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable.getModel().
                      getValueAt(r, SearchPane.instance.searchcolumnconfig.num_columns);
                  SearchPane.instance.searchtable.getModel().setValueAt(song.get_column_data(34), r,
                          stylesimilarity_index);
                }            
          }
          
      }
    } catch (Exception e) { log.error("UpdateStyleSimilarityRoutine(): error", e); }
  }

  private void setupActionListeners() {
      savelistbutton.addActionListener(this);
      editcurrentsongbutton.addActionListener(this);
      keylockcurrentsong.addActionListener(this);
      backbutton.addActionListener(this);
      nextbutton.addActionListener(this);
      importbutton.addActionListener(this);
      exportbutton.addActionListener(this);
      rating1checkbox.addActionListener(this);
      rating2checkbox.addActionListener(this);
      rating3checkbox.addActionListener(this);
      rating4checkbox.addActionListener(this);
      rating5checkbox.addActionListener(this);
      minrating1checkbox.addActionListener(this);
      minrating2checkbox.addActionListener(this);
      minrating3checkbox.addActionListener(this);
      minrating4checkbox.addActionListener(this);
      minrating5checkbox.addActionListener(this);
  }

  public void actionPerformed(ActionEvent ae) {
      if (log.isDebugEnabled()) log.debug("actionPerformed(): ae=" + ae);
      if (ae.getSource() == savelistbutton){
      if (SearchPane.instance.searchtable.getRowCount() <= 0) {
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("save_list_no_songs"),
          SkinManager.instance.getDialogMessageTitle("save_list_no_songs"),
          IOptionPane.ERROR_MESSAGE);
        return;
      }
      JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
      if (!previousfilepath.equals("")) fc.setCurrentDirectory(new File(previousfilepath));
      ExcelFileFilter excel = new ExcelFileFilter();
      fc.addChoosableFileFilter(new SaveFileFilter());
      fc.addChoosableFileFilter(excel);
      M3UFileFilter m3ufilter = new M3UFileFilter();
      fc.addChoosableFileFilter(m3ufilter);
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.setMultiSelectionEnabled(false);
      int returnVal = fc.showSaveDialog(SkinManager.instance.getFrame("main_frame"));
      File tmp = fc.getSelectedFile();
      if (tmp != null) previousfilepath = FileUtil.getDirectoryFromFilename(tmp.getAbsolutePath());
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        String filestr = (String)tmp.getAbsolutePath();
        if (fc.getFileFilter() == m3ufilter) {
            try {
                if (!filestr.toLowerCase().endsWith(".m3u"))
                    filestr += ".m3u";
                FileWriter outputstream = new FileWriter(filestr);
                BufferedWriter outputbuffer = new BufferedWriter(outputstream);
                outputbuffer.write("#EXTM3U");
                outputbuffer.newLine();
                for (int j = 0; j < SearchPane.instance.searchtable.getRowCount(); ++j) {
                  SongLinkedList sotiersong = (SongLinkedList) SearchPane.instance.searchtable.getModel().
                      getValueAt(j, SearchPane.instance.searchcolumnconfig.num_columns);
                  String filename = sotiersong.getRealFileName();
                  if ((filename != null) && !filename.equals("")) {
                      outputbuffer.write(filename);
                      outputbuffer.newLine();
                  }
                }
                outputbuffer.close();
                outputstream.close();
              }
              catch (Exception e) {
                log.error("actionPerformed(): error", e);
              }                    
        } else if (fc.getFileFilter() == excel) {
          if (!filestr.toLowerCase().endsWith(".xls")) filestr += ".xls";
          try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFFont f = workbook.createFont();
            f.setFontHeightInPoints((short) 12);
            f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            HSSFCellStyle cs = workbook.createCellStyle();
            cs.setFont(f);
            cs.setWrapText(true);
            HSSFSheet sheet = workbook.createSheet("rapid evolution song list");
            HSSFRow row = sheet.createRow((short)0);
            for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
              row.createCell((short) i).setCellValue(SearchPane.instance.searchtable.getColumnName(i));
              sheet.setColumnWidth((short)i,(short)(SearchPane.instance.searchtable.getColumnModel().getColumn(i).getWidth() * 50));
              HSSFCell cell = row.getCell((short)i);
              cell.setCellStyle(cs);
//              cell.setCellValue(searchtable.getColumnName(i));
            }
            for (int j = 0; j < SearchPane.instance.searchtable.getRowCount(); ++j) {
              SongLinkedList sotiersong = (SongLinkedList) SearchPane.instance.searchtable.getModel().getValueAt(j, SearchPane.instance.searchcolumnconfig.num_columns);
              row = sheet.createRow((short)j+1);
              for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
                  String columnname = SearchPane.instance.searchtable.getColumnName(i);
                  int index = SearchPane.instance.searchcolumnconfig.getIndex(columnname);
                  Object object = sotiersong.get_column_data(index);
                  String text = "";
                  if (object != null) text = object.toString();
                  else {
                      log.debug("actionPerformed(): column name: " + columnname + ", index: " + index + ", object: " + object);
                  }
                  row.createCell((short) i).setCellValue(text);                
              }
            }
            FileOutputStream fOut = new FileOutputStream(filestr);
            workbook.write(fOut);
            fOut.close();
          }
          catch (java.lang.OutOfMemoryError e) { new OutOfMemoryThread().start(); }
          catch (Exception e) {                 log.error("actionPerformed(): error", e);  }

        } else {
          if (!filestr.toLowerCase().endsWith(".txt")) filestr += ".txt";
          try {
            FileWriter outputstream = new FileWriter(filestr);
            BufferedWriter outputbuffer = new BufferedWriter(outputstream);
            for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
              outputbuffer.write(SearchPane.instance.searchtable.getColumnName(i));
              if ( (i + 1) < SearchPane.instance.searchcolumnconfig.num_columns) outputbuffer.write(
                  "\t");
            }
            outputbuffer.newLine();
            for (int j = 0; j < SearchPane.instance.searchtable.getRowCount(); ++j) {
              SongLinkedList sotiersong = (SongLinkedList) SearchPane.instance.searchtable.getModel().
                  getValueAt(j, SearchPane.instance.searchcolumnconfig.num_columns);
              for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
                outputbuffer.write(sotiersong.get_column_data(
                    SearchPane.instance.searchcolumnconfig.getIndex(SearchPane.instance.searchtable.getColumnName(i))).
                                   toString());
                SearchPane.instance.searchcolumnconfig.setIndex(i);
                if ( (i + 1) < SearchPane.instance.searchcolumnconfig.num_columns) outputbuffer.
                    write("\t");
              }
              outputbuffer.newLine();
            }
            outputbuffer.close();
            outputstream.close();
          }
          catch (Exception e) {
              log.error("actionPerformed(): error", e);
          }
        }
      }
    }  else if (ae.getSource() == editcurrentsongbutton) {
      if (currentsong != null) {
        editsong_ui.EditSong(currentsong);
      }
    } else if (ae.getSource() == exportbutton) {
        ImportLib.InitiateExport();        
    } else if (ae.getSource() == importbutton) {
      ImportLib.InitiateImport();
    } else if (ae.getSource() == rating1checkbox) {
        if (rating2checkbox.isSelected() || rating3checkbox.isSelected() || rating4checkbox.isSelected() || rating5checkbox.isSelected())
            rating1checkbox.setSelected(true);
        rating2checkbox.setSelected(false);
        rating3checkbox.setSelected(false);
        rating4checkbox.setSelected(false);
        rating5checkbox.setSelected(false);
        setRatingOnCurrentSong();
    } else if (ae.getSource() == rating2checkbox) {
        if (rating3checkbox.isSelected() || rating4checkbox.isSelected() || rating5checkbox.isSelected())
            rating2checkbox.setSelected(true);
        rating3checkbox.setSelected(false);
        rating4checkbox.setSelected(false);
        rating5checkbox.setSelected(false);
        if (rating2checkbox.isSelected()) {
            rating1checkbox.setSelected(true);
        } else {
            rating1checkbox.setSelected(false);              
        }
        setRatingOnCurrentSong();
    } else if (ae.getSource() == rating3checkbox) {
        if (rating4checkbox.isSelected() || rating5checkbox.isSelected())
            rating3checkbox.setSelected(true);
        rating4checkbox.setSelected(false);
        rating5checkbox.setSelected(false);
        if (rating3checkbox.isSelected()) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
        } else {
            rating1checkbox.setSelected(false);
            rating2checkbox.setSelected(false);              
        }
        setRatingOnCurrentSong();
    } else if (ae.getSource() == rating4checkbox) {
        if (rating5checkbox.isSelected())
            rating4checkbox.setSelected(true);
        rating5checkbox.setSelected(false);
        if (rating4checkbox.isSelected()) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
        } else {
            rating1checkbox.setSelected(false);
            rating2checkbox.setSelected(false);
            rating3checkbox.setSelected(false);              
        }
        setRatingOnCurrentSong();
    } else if (ae.getSource() == rating5checkbox) {
        if (rating5checkbox.isSelected()) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
            rating4checkbox.setSelected(true);
        } else {
            rating1checkbox.setSelected(false);
            rating2checkbox.setSelected(false);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);              
        }
        setRatingOnCurrentSong();

    } else if (ae.getSource() == minrating1checkbox) {
        if (minrating2checkbox.isSelected() || minrating3checkbox.isSelected() || minrating4checkbox.isSelected() || minrating5checkbox.isSelected())
            minrating1checkbox.setSelected(true);
        minrating2checkbox.setSelected(false);
        minrating3checkbox.setSelected(false);
        minrating4checkbox.setSelected(false);
        minrating5checkbox.setSelected(false);
    } else if (ae.getSource() == minrating2checkbox) {
        if (minrating3checkbox.isSelected() || minrating4checkbox.isSelected() || minrating5checkbox.isSelected())
            minrating2checkbox.setSelected(true);
        minrating3checkbox.setSelected(false);
        minrating4checkbox.setSelected(false);
        minrating5checkbox.setSelected(false);
        if (minrating2checkbox.isSelected()) {
            minrating1checkbox.setSelected(true);
        } else {
            minrating1checkbox.setSelected(false);              
        }
    } else if (ae.getSource() == minrating3checkbox) {
        if (minrating4checkbox.isSelected() || minrating5checkbox.isSelected())
            minrating3checkbox.setSelected(true);
        minrating4checkbox.setSelected(false);
        minrating5checkbox.setSelected(false);
        if (minrating3checkbox.isSelected()) {
            minrating1checkbox.setSelected(true);
            minrating2checkbox.setSelected(true);
        } else {
            minrating1checkbox.setSelected(false);
            minrating2checkbox.setSelected(false);              
        }
    } else if (ae.getSource() == minrating4checkbox) {
        if (minrating5checkbox.isSelected())
            minrating4checkbox.setSelected(true);
        minrating5checkbox.setSelected(false);
        if (minrating4checkbox.isSelected()) {
            minrating1checkbox.setSelected(true);
            minrating2checkbox.setSelected(true);
            minrating3checkbox.setSelected(true);
        } else {
            minrating1checkbox.setSelected(false);
            minrating2checkbox.setSelected(false);
            minrating3checkbox.setSelected(false);              
        }
    } else if (ae.getSource() == minrating5checkbox) {
        if (minrating5checkbox.isSelected()) {
            minrating1checkbox.setSelected(true);
            minrating2checkbox.setSelected(true);
            minrating3checkbox.setSelected(true);
            minrating4checkbox.setSelected(true);
        } else {
            minrating1checkbox.setSelected(false);
            minrating2checkbox.setSelected(false);
            minrating3checkbox.setSelected(false);
            minrating4checkbox.setSelected(false);              
        }
    } else if (ae.getSource() == keylockcurrentsong) {
      if (currentsong != null) {
        if (currentsong.getStartKey().isValid() ||
            currentsong.getEndKey().isValid()) {
          if ( (currentsong.getEndbpm() != 0) || (currentsong.getStartbpm() != 0)) {
            Key fromkey = currentsong.getStartKey();
            if (currentsong.getEndKey().isValid())
              fromkey = currentsong.getEndKey();
            float frombpm = currentsong.getStartbpm();
            if (currentsong.getEndbpm() != 0)
              frombpm = currentsong.getEndbpm();
            float fromdiff = SongUtil.get_bpmdiff(frombpm, RapidEvolution.instance.getActualBpm());
            if (!keylockcurrentsong.isSelected())
                setCurrentKey(fromkey.getShiftedKeyByBpmDifference(fromdiff));
            else
                setCurrentKey(fromkey);
            SearchPane.instance.keyfield.setText(getCurrentKey().toString());
          }
        }
        UpdateThread ut = new UpdateThread();
        ut.start();
      }
    } else if (ae.getSource() == nextbutton) {
      if (nextstack != null) {
        SongLinkedList song = SongDB.instance.NewGetSongPtr(nextstack.songid);
        change_current_song(song, 0, false, false);
      } else {
        change_current_song(currentsong.next, 0, false, false);
      }
    } else if (ae.getSource() == backbutton) {
      nextstack = new SongStack(currentsong.uniquesongid, nextstack);
      nextbutton.setEnabled(true);
      SongLinkedList song = SongDB.instance.NewGetSongPtr(prevstack.songid);
      prevstack = prevstack.next;
      change_current_song(song, 0, false, true);
    }
  }

  public static void CenterComponent(JDialog obj) {
    double objwidth = obj.getSize().getWidth();
    double objheight = obj.getSize().getHeight();
    double screenwidth = SkinManager.instance.getFrame("main_frame").getSize().getWidth();
    double screenheight = SkinManager.instance.getFrame("main_frame").getSize().getHeight();
    double xshift = (screenwidth - objwidth) / 2.0;
    double yshift = (screenheight - objheight) / 2.0;
    int x = (int)(SkinManager.instance.getFrame("main_frame").getLocation().getX() + xshift);
    int y = (int)(SkinManager.instance.getFrame("main_frame").getLocation().getY() + yshift);
    // Get the default toolkit
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    // Get the current screen size
    Dimension scrnsize = toolkit.getScreenSize();
    int screenresright = scrnsize.width;
    int screenresbottom = scrnsize.height;
    if ((x + (int)objwidth) > screenresright) x = screenresright - (int)objwidth;
    if (x < 0) x = 0;
    if ((y + (int)objheight) > screenresbottom) y = screenresbottom - (int)objheight;
    if (y < 0) y = 0;
    obj.setLocation(new Point(x, y));
  }
  
  public static void CenterComponent(JFrame obj) {
      double objwidth = obj.getSize().getWidth();
      double objheight = obj.getSize().getHeight();
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      // Get the current screen size
      Dimension scrnsize = toolkit.getScreenSize();
      int screenresright = scrnsize.width;
      int screenresbottom = scrnsize.height;
      int x = (int)((scrnsize.width - objwidth) / 2);
      int y = (int)((scrnsize.height - objheight) / 2);
      obj.setLocation(new Point(x, y));
    }  

  public static void CenterComponent(JDialog obj, JDialog src) {
    double objwidth = obj.getSize().getWidth();
    double objheight = obj.getSize().getHeight();
    double screenwidth = src.getSize().getWidth();
    double screenheight = src.getSize().getHeight();
    double xshift = (src.getSize().getWidth() - objwidth) / 2.0;
    double yshift = (src.getSize().getHeight() - objheight) / 2.0;
    obj.setLocation(new Point((int)(src.getLocation().getX() + xshift), (int)(src.getLocation().getY() + yshift)));
  }
    
  // necessary because many variables are not correct until loaded from mixguide.dat
  public void PostLoadInit() {
    MyStyleTreeRenderer.backgroundlistcolor = SkinManager.instance.getColor("style_background_selected");
    RETreeCellRenderer.backgroundlistcolor = SkinManager.instance.getColor("style_background_selected");
    
//    SkinManager.instance.getFrame("main_frame").setGlassPane(glassPane);
    
      for (int i = 0; i < midipiano.num_keys; ++i) {
        midipiano.keyspressed[i] = false;
        midipiano.keyspressed2[i] = 0;
        midipiano.masterkeyspressed[i] = 0;
      }
      if (OptionsUI.instance.keychordtype1 >= 0) options_ui.row1chordtype.setSelectedIndex(OptionsUI.instance.keychordtype1);
      else options_ui.row1chordtype.setSelectedIndex(1);
      midipiano.chordrow1 = (Vector)midipiano.chordbook.get(options_ui.row1chordtype.getSelectedIndex());
      if (OptionsUI.instance.keychordtype2 >= 0) options_ui.row2chordtype.setSelectedIndex(OptionsUI.instance.keychordtype2);
      else     options_ui.row2chordtype.setSelectedIndex(2);
      midipiano.chordrow2 = (Vector)midipiano.chordbook.get(options_ui.row2chordtype.getSelectedIndex());

      StylesUI.createStyleNodes();
            
      //AddSongsUI.instance.addsongsalbumcover_ui.imagepanel.setLayout(new RelativeLayout());
      //JScrollPane parent = (JScrollPane)SkinManager.instance.getObject("add_songs_album_cover_image_scrollpanel");
      //parent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      //parent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      //EditSongUI.instance.editsongalbumcover_ui.imagepanel.setLayout(new RelativeLayout());
      //parent = (JScrollPane)SkinManager.instance.getObject("edit_song_album_cover_image_scrollpanel");
      //parent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      //parent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      
      if (options_ui.useosplayer.isSelected()) {
        SkinManager.instance.setEnabled("media_player_button", false);
        SkinManager.instance.setVisible("media_player_button", false);
      }

      rating1checkbox.setSelected(false);
      rating2checkbox.setSelected(false);
      rating3checkbox.setSelected(false);
      rating4checkbox.setSelected(false);
      rating5checkbox.setSelected(false);
      
      SearchPane.instance.searchtable.setAutoscrolls(OptionsUI.instance.searchtableautoscroll.isSelected());
      MixoutPane.instance.mixouttable.setAutoscrolls(OptionsUI.instance.mixouttableautoscroll.isSelected());

      if (OptionsUI.instance.disabletooltips.isSelected()) ToolTipManager.sharedInstance().setEnabled(false);
      else ToolTipManager.sharedInstance().setEnabled(true);

      midipiano.PostLoadInit();
      editstyle_ui.PostLoadInit();
      options_ui.PostLoadInit();
      
      SkinManager.instance.setColorChooserBackground(OptionsUI.instance.colorchooser,OptionsUI.instance.colorchooser.getBackground());

      Hashtable pitchlabelTable = new Hashtable();
      Hashtable volumelabelTable = new Hashtable();
      volumelabelTable.put(new Integer(0), new MySliderLabel(" 0", MIDIPiano.instance.midivolume));
      volumelabelTable.put(new Integer(100), new MySliderLabel("100", MIDIPiano.instance.midivolume));
      volumelabelTable.put(new Integer(127), new MySliderLabel("127", MIDIPiano.instance.midivolume));
      volumelabelTable.put(new Integer(50), new MySliderLabel("50", MIDIPiano.instance.midivolume));
      MIDIPiano.instance.midivolume.setLabelTable(volumelabelTable);
      pitchlabelTable.put(new Integer(8192), new MySliderLabel(" 0", MIDIPiano.instance.pitchbend));
      pitchlabelTable.put(new Integer(8192+4096), new MySliderLabel("+100", MIDIPiano.instance.pitchbend));
      pitchlabelTable.put(new Integer(8192-4096), new MySliderLabel("-100", MIDIPiano.instance.pitchbend));
      pitchlabelTable.put(new Integer(8192+2048), new MySliderLabel("+50", MIDIPiano.instance.pitchbend));
      pitchlabelTable.put(new Integer(8192-2048), new MySliderLabel("-50", MIDIPiano.instance.pitchbend));
      MIDIPiano.instance.pitchbend.setLabelTable(pitchlabelTable);
      
      OptionsUI.instance.checkAdvancedFeatures();
      /*
      Hashtable bpmlabelTable = new Hashtable();
      JLabel label1 = new RELabel(" 0"); label1.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label2 = new RELabel("-2"); label2.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label3 = new RELabel("+2"); label3.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label4 = new RELabel("-4"); label4.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label5 = new RELabel("+4"); label5.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label6 = new RELabel("-6"); label6.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label7 = new RELabel("+6"); label7.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label8 = new RELabel("-8"); label8.setForeground(SkinManager.instance.getColor("default_foregroud"));
      JLabel label9 = new RELabel("+8"); label9.setForeground(SkinManager.instance.getColor("default_foregroud"));
      bpmlabelTable.put(new Integer(0), label1);
      bpmlabelTable.put(new Integer(200), label2);
      bpmlabelTable.put(new Integer(-200), label3);
      bpmlabelTable.put(new Integer(400), label4);
      bpmlabelTable.put(new Integer(-400), label5);
      bpmlabelTable.put(new Integer(600), label6);
      bpmlabelTable.put(new Integer(-600), label7);
      bpmlabelTable.put(new Integer(800), label8);
      bpmlabelTable.put(new Integer(-800), label9);
      SearchPane.instance.bpmslider.setLabelTable(bpmlabelTable);
      */
      
  }

  public void PostInit() {
    SkinManager.instance.setEnabled("add_mixout_button", false);
    SkinManager.instance.setEnabled("view_excludes_button", false);
    SkinManager.instance.setEnabled("current_song_mixes_button", false);
    SkinManager.instance.setEnabled("current_song_roots_button", false);
    SkinManager.instance.setEnabled("current_song_sync_button", false);
    SkinManager.instance.setEnabled("current_song_sync_button", false);
    SkinManager.instance.setEnabled("style_edit_button", false);
    SkinManager.instance.setText("options_bpm_detection_range_start", String.valueOf(Bpm.minbpm));
    SkinManager.instance.setText("options_bpm_detection_range_end", String.valueOf(Bpm.maxbpm));
    SkinManager.instance.enableChangeListener("options_bpm_detection_range_start");
    SkinManager.instance.enableChangeListener("options_bpm_detection_range_end");
    
    SearchPane.instance.bpmslider_tickmark_combo.removeAllItems();
    SearchPane.instance.bpmslider_tickmark_combo.addItem(SkinManager.instance.getMessageText("options_smooth_bpm_slider_default"));
    SearchPane.instance.bpmslider_tickmark_combo.addItem("4 / 8");
    SearchPane.instance.bpmslider_tickmark_combo.addItem("5 / 10");
    SearchPane.instance.bpmslider_tickmark_combo.setSelectedIndex(bpmslider_tickmark_index);    
    SearchPane.instance.setBpmSliderRangeLabels();   
    BrokenLinkSongsUI.instance.listmouse.PostInit();
 
    table_config_map.put(SearchPane.instance.searchtable, SearchPane.instance.searchcolumnconfig);
    table_config_map.put(MixoutPane.instance.mixouttable, MixoutPane.instance.mixoutcolumnconfig);
    table_config_map.put(ExcludeUI.instance.excludetable, ExcludeUI.instance.excludecolumnconfig);
    table_config_map.put(RootsUI.instance.rootstable, RootsUI.instance.rootscolumnconfig);
    table_config_map.put(SyncUI.instance.synctable, SyncUI.instance.synccolumnconfig);
    table_config_map.put(SuggestedMixesUI.instance.suggestedtable, SuggestedMixesUI.instance.suggestedcolumnconfig);
    table_config_map.put(EditMatchQueryUI.instance.matchtable2, EditMatchQueryUI.instance.matchcolumnconfig2);
    table_config_map.put(AddMatchQueryUI.instance.matchtable, AddMatchQueryUI.instance.matchcolumnconfig);
    
    OptionsUI.instance.setupAutocomplete(!OptionsUI.instance.disableautocomplete.isSelected());

    if (OptionsUI.instance.enabledebug.isSelected()) {
        Logger.getRoot().setLevel(Level.DEBUG);              
    } else {
        Logger.getRoot().setLevel(RapidEvolution.original_log_level);              
    }          
    
    if (log.isDebugEnabled()) {
        log.debug("PostInit(): default label font=" + new JLabel().getFont());
    }
    
    if (rapid_evolution.Database.databaseVersion < 1.04f) {
        OptionsUI.instance.id3reader.setSelectedIndex(0);
        OptionsUI.instance.id3writer.setSelectedIndex(0);
    }
    
    /*
    new FileDrop( SearchPane.instance.searchtable, new FileDrop.Listener()
  	{   public void filesDropped( java.io.File[] files )
  		{   
  			if (log.isDebugEnabled())
  				log.debug("filesDropped(): files dropped=" + files);
  		}
  	});            
    */
        
}

  public int bpmslider_tickmark_index = 0;
  
// should be called postLoadINit
  public void postSkinInit() {
    PostInit();
    OptionsUI.instance.colorchooser.setColor(SkinManager.instance.getColor("song_background_disabled"));
  }
  
  private RatingCellRenderer ratingcellrenderer = new RatingCellRenderer();
  public RatingCellRenderer getRatingCellRenderer() { return ratingcellrenderer; }
  
  public void ClearCurrentSong() {
      javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() {       
          SkinManager.instance.setEnabled("current_song_mixes_button", false);
          SearchPane.instance.bpmslider.setValue(0);
          RapidEvolutionUI.instance.currentsong = null;
          RapidEvolutionUI.instance.keylockcurrentsong.setEnabled(false);
          RapidEvolutionUI.instance.setCurrentKey(null);
          SearchPane.instance.keyfield.setText("");
          SearchPane.instance.keysearchbutton.setEnabled(false);
          RapidEvolutionUI.instance.currentsongfield.setText("");
          RapidEvolutionUI.instance.rating1checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating2checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating3checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating4checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating5checkbox.setSelected(false);            
          RapidEvolutionUI.instance.rating1checkbox.setEnabled(false);
          RapidEvolutionUI.instance.rating2checkbox.setEnabled(false);
          RapidEvolutionUI.instance.rating3checkbox.setEnabled(false);
          RapidEvolutionUI.instance.rating4checkbox.setEnabled(false);
          RapidEvolutionUI.instance.rating5checkbox.setEnabled(false);                              
          RapidEvolutionUI.instance.editcurrentsongbutton.setEnabled(false);
          SkinManager.instance.setEnabled("current_song_sync_button", false);
          SkinManager.instance.setEnabled("view_excludes_button", false);
          RapidEvolutionUI.instance.addexcludebutton.setEnabled(false);
          if (RapidEvolution.instance.getActualBpm() != 0) SearchPane.instance.bpmsearchbutton.setEnabled(true);
          else SearchPane.instance.bpmsearchbutton.setEnabled(false);
          SkinManager.instance.setEnabled("add_mixout_button", false);
          SkinManager.instance.setEnabled("current_song_roots_button", false);
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
          SkinManager.instance.setEnabled("view_excludes_button", false);
          RapidEvolutionUI.instance.nextbutton.setEnabled(false);
          RapidEvolutionUI.instance.backbutton.setEnabled(false);
          MixoutPane.instance.RedrawMixoutTable();
      } });
  }
  
  private void setRatingOnCurrentSong() {
      OldSongValues oldvalues = new OldSongValues(currentsong);
      char rating_before = currentsong.getRating();
      if (rating5checkbox.isSelected()) currentsong.setRating((char)5);
      else if (rating4checkbox.isSelected()) currentsong.setRating((char)4);
      else if (rating3checkbox.isSelected()) currentsong.setRating((char)3);
      else if (rating2checkbox.isSelected()) currentsong.setRating((char)2);
      else if (rating1checkbox.isSelected()) currentsong.setRating((char)1);                          
      else currentsong.setRating((char)0);
      if (rating_before != currentsong.getRating()) SongDB.instance.UpdateSong(currentsong, oldvalues);
  }
  
  public void setRatingFromCurrent() {
      RapidEvolutionUI.instance.rating1checkbox.setEnabled(true);
      RapidEvolutionUI.instance.rating2checkbox.setEnabled(true);
      RapidEvolutionUI.instance.rating3checkbox.setEnabled(true);
      RapidEvolutionUI.instance.rating4checkbox.setEnabled(true);
      RapidEvolutionUI.instance.rating5checkbox.setEnabled(true);            
      if (RapidEvolutionUI.instance.currentsong.getRating() == 0) {
          RapidEvolutionUI.instance.rating1checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating2checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating3checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating4checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating5checkbox.setSelected(false);            
      } else if (RapidEvolutionUI.instance.currentsong.getRating() == 1) {
          RapidEvolutionUI.instance.rating1checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating2checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating3checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating4checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating5checkbox.setSelected(false);            
      } else if (RapidEvolutionUI.instance.currentsong.getRating() == 2) {
          RapidEvolutionUI.instance.rating1checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating2checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating3checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating4checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating5checkbox.setSelected(false);            
      } else if (RapidEvolutionUI.instance.currentsong.getRating() == 3) {
          RapidEvolutionUI.instance.rating1checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating2checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating3checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating4checkbox.setSelected(false);
          RapidEvolutionUI.instance.rating5checkbox.setSelected(false);            
      } else if (RapidEvolutionUI.instance.currentsong.getRating() == 4) {
          RapidEvolutionUI.instance.rating1checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating2checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating3checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating4checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating5checkbox.setSelected(false);            
      } else if (RapidEvolutionUI.instance.currentsong.getRating() == 5) {
          RapidEvolutionUI.instance.rating1checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating2checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating3checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating4checkbox.setSelected(true);
          RapidEvolutionUI.instance.rating5checkbox.setSelected(true);            
      }                
  }
  
}
