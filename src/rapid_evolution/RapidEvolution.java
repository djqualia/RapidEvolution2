package rapid_evolution;

/**
 * <p>Title: Rapid Evolution</p>
 * <p>Description: DJ Mixing Tool</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Author: Jesse Bickmore</p>
 *
 
======
TO DO:
======

==Bugs==
	==Sev1==
	    * actual key mode (tonic, etc) relative to current bpm
	==Sev2==
		* Maximizing problem on Mac
		* Install on 64 bit fails (at shortcut part)
		* Fix dragging multiple songs with left mouse button
		* Sorting of changed ratings
		* when detecting key , scale window size to samplerate?
	==Sev3==
		* when adding a mixout with double click option and say no, should change current song still
		* when stretching window with styles on left full the styles expands not search table
		* when on a key locked song going to mixout with key lock no, doesn't automaticlly change
		* Add beat intensity to detect all?
		* Remove deprecated APIs
		* keyboard controlling combobox?
		* change song display format, make custom fields actual labels
		* show a mix if immediately added? (and it's on song trail)
		* left justify current song

==Performance==
	==Sev1==
	==Sev2==
		* Reduce memory usage
		* test bug when use selected styles is on, that changing song repeatedly recomputes style similartiy
		* choose searchable fields
		* Updates styles efficiency with hashmaps?
		* bpm optimization: only compute fft that you need...
	==Sev3==
		* Improve performance of style similarity calculation?
		* redrawing of song trail could be just an update (songtrail_ui.RedrawSongTrailRoutine())
		* removal of excludes could be made order 1 (RapidEvolutionUI.instance.changecurrentroutine())
		* Send style rename rather than flag all songs
		* better multithreaded management
		
==Features==
	==Priority1==
		* Always on top feature
		* Seperate music and options in xml -> have music libraries "pluggable" on the fly, like if i went to robbies, also make profiles for settings...
	==Priority2==	
	    * New columns "ableton ready" and "id3 version" and "# styles"
		* Improve key detection by looking for fifths?
		* # Times Mixed
		* command line usage
		* Add shift click to add/remove songs of current song in main search window?
		* Add actual key filter
		* Add bitrate
		* Add stars next to media player in RE2
		* When deleting a file, add "delete actual file" option
		* Add apple about menu
		* Preserve main tree state
		* Audoupdater
		* Option not to write ID3 v1 tag
		* Add %rating% to filename renamer
		* add prepend/append to fields
		* pause/resume on batch proceses
		* Playlists/Cases Tab
		* live bpm detection   http://www.pyramidedata.dk/
	==Priority3==
	    * Add JVM version in "About" section
		* Traktor import import date
		* Mix set generator utilize ranks and style similarity
		* Notes for a given style
		* Automatically detect new songs in directories
		* Option to keep song tags in sync
		* Option to automatically detect keys/bpms
		* Drag and drop album cover from RE2
		* Send style hierarchy info to server
		* add ability to control column header fonts
		* Ability to control autosave interval
		* add color for song that's a mixout
		* Option to use styles from server
		* Add more options under play->i.e. selected,similar,album,songs by artist, etc
		* Option to read track times
		* Add limit results field
		* add color for current song
		* syncing sholud sync styles?
		* error/exception reporting to mixshare?
		* cue points/ song sections
		* generate random mixes
		* undo?
		* create/view stripes


  **/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;

import net.roydesign.mac.MRJAdapter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.audio.Bpm;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.net.MyConnectThread;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.threads.AutoSaveThread;
import rapid_evolution.threads.ErrorMessageThread;
import rapid_evolution.ui.AddMatchQueryUI;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditMatchQueryUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.ExcludeUI;
import rapid_evolution.ui.KeyFormatUI;
import rapid_evolution.ui.MediaPlayerUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.RootsUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.SuggestedMixesUI;
import rapid_evolution.ui.SyncUI;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.PitchTimeShiftUI;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.StylesUI;
import rapid_evolution.util.OSHelper;
import rapid_evolution.util.UTF8BufferedReader;

import com.brunchboy.util.swing.relativelayout.AttributeConstraint;
import com.brunchboy.util.swing.relativelayout.AttributeType;
import com.brunchboy.util.swing.relativelayout.DependencyManager;
import com.brunchboy.util.swing.relativelayout.RelativeLayout;
import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.audio.tags.TagWriteJobBuffer;
import com.mixshare.rapid_evolution.data.MusicDatabase;
import com.mixshare.rapid_evolution.library.RootMusicDirectoryScanner;
import com.mixshare.rapid_evolution.library.RootMusicExcludeList;
import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.net.DataMinerHelper;
import com.mixshare.rapid_evolution.net.mining.LastfmArtistJob;
import com.mixshare.rapid_evolution.qt.QTUtil;
import com.mixshare.rapid_evolution.ui.swing.animation.InfiniteProgressPanel;
import com.mixshare.rapid_evolution.ui.swing.background.TransparentBackground;
import com.mixshare.rapid_evolution.ui.swing.lookfeel.LookAndFeelManager;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

import de.ueberdosis.util.OutputCtr;

public class RapidEvolution implements ActionListener {

    private static Logger log = Logger.getLogger(RapidEvolution.class);
    
  public static String versionString = "@@@RE2_VERSION@@@";
  public static boolean testservermode = false;

  public SongDB songdb;
  private MusicDatabase musicDatabase;
  public static MusicDatabase getMusicDatabase() { return instance.musicDatabase; }
  
  
  public RapidEvolutionUI rapidevolution_ui;

  public boolean loaded = false;

  AudioEngine audioengine = new AudioEngine();

  public String osName = null;
  private com.mixshare.rapid_evolution.net.MixshareClient mixshare_client;
  static public com.mixshare.rapid_evolution.net.MixshareClient getMixshareClient() {
      return instance.mixshare_client;
  }
  
  // constructor
  public RapidEvolution() {
    instance = this;
    masterdateformat = new java.text.SimpleDateFormat(MASTER_DATE_FORMAT);
    masterdateformat.setTimeZone(TimeZone.getDefault());
    yeardateformat = new java.text.SimpleDateFormat(YEAR_DATE_FORMAT);
    yeardateformat.setTimeZone(TimeZone.getDefault());
        
    osName = System.getProperty("os.name");

    log.debug("** DEBUG flag is set **");
    if (testservermode) log.debug("** Using TEST mixshare server **");

    log.info("Rapid Evolution " + versionString);
    log.info("Detected OS: " + osName);
    log.info("JVM Version: " + System.getProperty("java.version"));    
    try {
        String QTVersion = QTUtil.getVersionString();
        if (QTVersion != null) {
            log.info(QTVersion);        
        }
    } catch (java.lang.Error e) { 
    } catch (Exception e) { }
    log.info("Max Memory: " + String.valueOf(Runtime.getRuntime().maxMemory() / 1048576) + "mb");
    if (log.isDebugEnabled()) {
        String classPath = System.getProperty("java.class.path",".");    
        log.debug("Classpath: " + classPath);
    }
        
    mixshare_client = new com.mixshare.rapid_evolution.net.MixshareClient();    
    init();    
        
    /*
    try {
        FileOutputStream fos = new FileOutputStream("/users/curtis/out.txt");
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);
    } catch (Exception e) { System.exit(1); }
    debugmode = true;
    */
  }
 
  
  public void init() {
      songdb = new SongDB();    
      musicDatabase = new MusicDatabase();
      rapidevolution_ui = new RapidEvolutionUI();
      Artist.masterartistlist = new HashMap();
  }
  
  String MASTER_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss z";
  public static java.text.SimpleDateFormat masterdateformat = null;

  String YEAR_DATE_FORMAT = "yyyy-MM-dd";
  public static java.text.SimpleDateFormat yeardateformat = null;
  
  Semaphore SaveSem = new Semaphore(1);
  boolean Save(String filename, boolean use_pacer) {
    if ((filename == null) || filename.equals("")) filename = "music_database.xml";
    if (filename.toLowerCase().endsWith("music_database.xml")) {
      SkinManager.instance.Save("skin.dat");
    }
    return Database.saveDatabase(filename, use_pacer);

  }

  Vector triedtoload = new Vector();
  public int oldload(String loadfilename) {
    Vector oldactions = new Vector();
    String version = null;
    float versionnum = 0.0f;
    float dirtycachever = 2.37f;
    int split1 = 0;
    int split2 = 0;
    int split3 = 0;
    try {
      int total_mixes = 0;
//      FileReader inputstream = new FileReader(programdirectory + "\\" + "mixguide.dat");
      FileReader inputstream = new FileReader(OSHelper.getFileBackwardsCompatible(loadfilename));
      triedtoload.add(loadfilename);
      BufferedReader inputbuffer = new BufferedReader(inputstream);
      version = inputbuffer.readLine();
      versionnum = Float.parseFloat(version);

      if (version != null) {
        if (versionnum >= 1.4f) {
          // read initial screen position
          String line = inputbuffer.readLine();
          int screen_initialx = Integer.parseInt(line);
          line = inputbuffer.readLine();
          int screen_initialy = Integer.parseInt(line);
          SkinManager.instance.getFrame("main_frame").setLocation(screen_initialx, screen_initialy);
          line = inputbuffer.readLine();
          int screenwidth = Integer.parseInt(line);
          line = inputbuffer.readLine();
          int screenheight = Integer.parseInt(line);
          SkinManager.instance.getFrame("main_frame").setSize(screenwidth, screenheight);

          // Get the default toolkit
          Toolkit toolkit = Toolkit.getDefaultToolkit();
          // Get the current screen size
          Dimension scrnsize = toolkit.getScreenSize();
          int screenresright = scrnsize.width;
          int screenresbottom = scrnsize.height;
//          if ((rapidevolution_ui.screen_initialx + rapidevolution_ui.screenwidth) < 0) rapidevolution_ui.screen_initialx = 0;
//          if (rapidevolution_ui.screen_initialx > screenresright) rapidevolution_ui.screen_initialx = screenresright - rapidevolution_ui.screenwidth;
//          if ((rapidevolution_ui.screen_initialy + rapidevolution_ui.screenheight) < 0) rapidevolution_ui.screen_initialy = 0;
//          if (rapidevolution_ui.screen_initialy > screenresbottom) rapidevolution_ui.screen_initialy = screenresbottom - rapidevolution_ui.screenheight;
        if (versionnum >= 2.15f) {
            line = inputbuffer.readLine();
          }
          OptionsUI.instance.disableonline.setSelected(false);

          if (versionnum >= 2.78f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.searchinplaceediting.setSelected(true);
            else
              OptionsUI.instance.searchinplaceediting.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.mixoutinplaceediting.setSelected(true);
            else
              OptionsUI.instance.mixoutinplaceediting.setSelected(false);

          }

          if (versionnum >= 2.76f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              PitchTimeShiftUI.instance.normalize.setSelected(true);
            else
              PitchTimeShiftUI.instance.normalize.setSelected(false);
          }
          if (versionnum >= 2.74f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.mixouttablehide.setSelected(true);
            else
              OptionsUI.instance.mixouttablehide.setSelected(false);
            oldactions.add(OptionsUI.instance.mixouttablehide);
          }
          if (versionnum >= 2.64f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.disableautosave.setSelected(true);
            else
              OptionsUI.instance.disableautosave.setSelected(false);
          }

          if (versionnum >= 2.63f) {
            SearchPane.bpmrangespinner.getModel().setValue(inputbuffer.readLine());
          }
          if (versionnum >= 2.62f) {
            line = inputbuffer.readLine();
            // used to be readv1tags only
          }

          if (versionnum >= 2.60f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.usefilenameastag.setSelected(true);
            else
              OptionsUI.instance.usefilenameastag.setSelected(false);
          }

          if (versionnum >= 2.59f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.addsongalbumstyle.setSelected(true);
            else
              OptionsUI.instance.addsongalbumstyle.setSelected(false);
          }

          if (versionnum >= 2.58f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              EditStyleUI.instance.showexcludekeywords.setSelected(true);
            else
              EditStyleUI.instance.showexcludekeywords.setSelected(false);
            oldactions.add(EditStyleUI.instance.showexcludekeywords);
          }
          if (versionnum >= 2.54f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.donotsharemixcomments.setSelected(true);
            else
              OptionsUI.instance.donotsharemixcomments.setSelected(false);
          }
          if (versionnum >= 2.53f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.showprevioustrack.setSelected(true);
            else
              OptionsUI.instance.showprevioustrack.setSelected(false);
            oldactions.add(OptionsUI.instance.showprevioustrack);
          }
          if (versionnum >= 2.52f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.writeremixertotitle.setSelected(true);
            else
              OptionsUI.instance.writeremixertotitle.setSelected(false);
          }
          if (versionnum >= 2.51f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
                RapidEvolution.getMixshareClient().setOutOfSync(true);              
            else
                RapidEvolution.getMixshareClient().setOutOfSync(false);
          }

          if (versionnum >= 2.50f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              netclient.neverconnectedtoserver = true;
            else
              netclient.neverconnectedtoserver = false;
          } else netclient.neverconnectedtoserver = false;

          if (versionnum >= 2.49f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
                RapidEvolution.getMixshareClient().setIsSyncing(true);
            else
                RapidEvolution.getMixshareClient().setIsSyncing(false);
          }
          if (versionnum >= 2.48f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.useselectedstylesimilarity.setSelected(true);
            else
              OptionsUI.instance.useselectedstylesimilarity.setSelected(false);
          }
          if (versionnum >= 2.45f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              SuggestedMixesUI.instance.showallsuggested.setSelected(true);
            else
              SuggestedMixesUI.instance.showallsuggested.setSelected(false);
          }
          if (versionnum >= 2.44f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.autobpmkeysearch.setSelected(true);
            else
              OptionsUI.instance.autobpmkeysearch.setSelected(false);
          }
          if (versionnum >= 2.43f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayartist.setSelected(true);
            else
              OptionsUI.instance.songdisplayartist.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayalbum.setSelected(true);
            else
              OptionsUI.instance.songdisplayalbum.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplaytrack.setSelected(true);
            else
              OptionsUI.instance.songdisplaytrack.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplaysongname.setSelected(true);
            else
              OptionsUI.instance.songdisplaysongname.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayremixer.setSelected(true);
            else
              OptionsUI.instance.songdisplayremixer.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplaystartbpm.setSelected(true);
            else
              OptionsUI.instance.songdisplaystartbpm.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayendbpm.setSelected(true);
            else
              OptionsUI.instance.songdisplayendbpm.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplaystartkey.setSelected(true);
            else
              OptionsUI.instance.songdisplaystartkey.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayendkey.setSelected(true);
            else
              OptionsUI.instance.songdisplayendkey.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplaytracktime.setSelected(true);
            else
              OptionsUI.instance.songdisplaytracktime.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplaytimesig.setSelected(true);
            else
              OptionsUI.instance.songdisplaytimesig.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayfield1.setSelected(true);
            else
              OptionsUI.instance.songdisplayfield1.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayfield2.setSelected(true);
            else
              OptionsUI.instance.songdisplayfield2.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayfield3.setSelected(true);
            else
              OptionsUI.instance.songdisplayfield3.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.songdisplayfield4.setSelected(true);
            else
              OptionsUI.instance.songdisplayfield4.setSelected(false);
          }
          if (versionnum >= 2.42f) {
            line = inputbuffer.readLine();
            OptionsUI.instance.keyformatcombo.setSelectedIndex(Integer.parseInt(line));
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              KeyFormatUI.instance.csharpoption.setSelected(true);
            else
              KeyFormatUI.instance.dflatoption.setSelected(true);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              KeyFormatUI.instance.dsharpoption.setSelected(true);
            else
              KeyFormatUI.instance.eflatoption.setSelected(true);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              KeyFormatUI.instance.fsharpoption.setSelected(true);
            else
              KeyFormatUI.instance.gflatoption.setSelected(true);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              KeyFormatUI.instance.gsharpoption.setSelected(true);
            else
              KeyFormatUI.instance.aflatoption.setSelected(true);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              KeyFormatUI.instance.asharpoption.setSelected(true);
            else
              KeyFormatUI.instance.bflatoption.setSelected(true);
          } else if (versionnum >= 2.41f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1') {
              OptionsUI.instance.keyformatcombo.setSelectedIndex(1);
              KeyFormatUI.instance.dflatoption.setSelected(true);
              KeyFormatUI.instance.eflatoption.setSelected(true);
              KeyFormatUI.instance.gflatoption.setSelected(true);
              KeyFormatUI.instance.aflatoption.setSelected(true);
              KeyFormatUI.instance.bflatoption.setSelected(true);
            } else {
              OptionsUI.instance.keyformatcombo.setSelectedIndex(0);
              KeyFormatUI.instance.csharpoption.setSelected(true);
              KeyFormatUI.instance.dsharpoption.setSelected(true);
              KeyFormatUI.instance.fsharpoption.setSelected(true);
              KeyFormatUI.instance.gsharpoption.setSelected(true);
              KeyFormatUI.instance.asharpoption.setSelected(true);
            }
          }
          if (versionnum >= 2.40f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwriteartist.setSelected(true);
            else
              OptionsUI.instance.tagwriteartist.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwritealbum.setSelected(true);
            else
              OptionsUI.instance.tagwritealbum.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwritetrack.setSelected(true);
            else
              OptionsUI.instance.tagwritetrack.setSelected(false);
            OptionsUI.instance.tagwritekeytogroupingtag.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwritesongname.setSelected(true);
            else
              OptionsUI.instance.tagwritesongname.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwriteremixer.setSelected(true);
            else
              OptionsUI.instance.tagwriteremixer.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwritebpm.setSelected(true);
            else
              OptionsUI.instance.tagwritebpm.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwritekey.setSelected(true);
            else
              OptionsUI.instance.tagwritekey.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.tagwritecomments.setSelected(true);
            else
              OptionsUI.instance.tagwritecomments.setSelected(false);
          }
          if (versionnum >= 2.33f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.autohighlightstyles.setSelected(true);
            else
              OptionsUI.instance.autohighlightstyles.setSelected(false);
          }
         if (versionnum >= 2.32f) {
           line = inputbuffer.readLine();
           if (line.charAt(0) == '1')
             rapidevolution_ui.stylespanel.dynamixstylescheckbox.setSelected(true);
           else
             rapidevolution_ui.stylespanel.dynamixstylescheckbox.setSelected(false);
         }
         if (versionnum >= 2.30f) {
           OptionsUI.OSMediaPlayer = inputbuffer.readLine();
         }
         if (versionnum >= 2.29f) {
           line = inputbuffer.readLine();
           try {
             OptionsUI.instance.bpmdetectionquality.setValue(Integer.parseInt(line));
           } catch (Exception e) { }
         } else if (versionnum >= 2.28f) {
             line = inputbuffer.readLine();
//             if (line.charAt(0) == '1') highqualitybpmdetection.setSelected(true);
//             else highqualitybpmdetection.setSelected(false);
         }
         if (versionnum >= 2.75f) {
           line = inputbuffer.readLine();
           try {
             OptionsUI.instance.timepitchshiftquality.setValue(Integer.parseInt(line));
           } catch (Exception e) { }
         }
         if (versionnum >= 2.27f) { line = inputbuffer.readLine(); if (line.charAt(0) == '1') OptionsUI.instance.disabletooltips.setSelected(true); else OptionsUI.instance.disabletooltips.setSelected(false); }
         if (OptionsUI.instance.disabletooltips.isSelected()) ToolTipManager.sharedInstance().setEnabled(false);
         else ToolTipManager.sharedInstance().setEnabled(true);
          if (versionnum >= 2.25f) {
              line = inputbuffer.readLine();
              if (line.charAt(0) == '1')
                OptionsUI.instance.writekeycodes.setSelected(true);
              else
                OptionsUI.instance.writekeycodes.setSelected(false);
            }
          if (versionnum >= 2.24f) {
              line = inputbuffer.readLine();
              if (line.charAt(0) == '0') {
                SearchPane.showallradio.setSelected(true);
                SearchPane.showvinylradio.setSelected(false);
                SearchPane.shownonvinylradio.setSelected(false);
              } else if (line.charAt(0) == '1') {
                SearchPane.showallradio.setSelected(false);
                SearchPane.showvinylradio.setSelected(true);
                SearchPane.shownonvinylradio.setSelected(false);
              } else {
                SearchPane.showallradio.setSelected(false);
                SearchPane.showvinylradio.setSelected(false);
                SearchPane.shownonvinylradio.setSelected(true);
              }
              line = inputbuffer.readLine();
              if (line.charAt(0) == '1')
                OptionsUI.instance.automaticallyquerywhenadding.setSelected(true);
              else
                OptionsUI.instance.automaticallyquerywhenadding.setSelected(false);
              if (versionnum >= 2.47f) {
                  line = inputbuffer.readLine();
                  if (line.charAt(0) == '1')
                    OptionsUI.instance.strictsearch.setSelected(true);
                  else
                    OptionsUI.instance.strictsearch.setSelected(false);
              }
              if (versionnum >= 2.46f) {
                  line = inputbuffer.readLine();
                  if (line.charAt(0) == '1')
                    OptionsUI.instance.donotquerycomments.setSelected(true);
                  else
                    OptionsUI.instance.donotquerycomments.setSelected(false);
                line = inputbuffer.readLine();
                if (line.charAt(0) == '1')
                  OptionsUI.instance.donotquerystyles.setSelected(true);
                else
                  OptionsUI.instance.donotquerystyles.setSelected(false);
              }
              if (versionnum >= 2.38f) {
                line = inputbuffer.readLine();
                if (line.charAt(0) == '1')
                  OptionsUI.instance.overwritewhenquerying.setSelected(true);
                else
                  OptionsUI.instance.overwritewhenquerying.setSelected(false);
              }
          }

        if (versionnum >= 2.21f) {
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            MediaPlayerUI.instance.randomizeplaylist.setSelected(true);
          else
            MediaPlayerUI.instance.randomizeplaylist.setSelected(false);
        }
        if (versionnum >= 2.2f) {
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.useosplayer.setSelected(true);
          else
            OptionsUI.instance.useosplayer.setSelected(false);
        }
        if (versionnum >= 2.17f) {
            line = inputbuffer.readLine();
            try {
              OptionsUI.instance.keydetectionquality.setValue(Integer.parseInt(line));
            } catch (Exception e) { }
          }
        if (versionnum >= 2.02f) {
            line = inputbuffer.readLine();
            MIDIPiano.instance.pianowidth = Integer.parseInt(line);
            line = inputbuffer.readLine();
            MIDIPiano.instance.pianoheight = Integer.parseInt(line);
            line = inputbuffer.readLine();
            ((SpinnerNumberModel)MIDIPiano.instance.octavesspinner).setValue(new Integer(Integer.parseInt(line)));
            MIDIPiano.instance.setkeyboardshift(Integer.parseInt(line));
            MIDIPiano.instance.num_keys = 12 * Integer.parseInt(line);
            MIDIPiano.instance.keyspressed = new boolean[MIDIPiano.instance.num_keys];
            MIDIPiano.instance.keyspressed2 = new int[MIDIPiano.instance.num_keys];
            MIDIPiano.instance.masterkeyspressed = new int[MIDIPiano.instance.num_keys];
            for (int i = 0; i < MIDIPiano.instance.num_keys; ++i) {
              MIDIPiano.instance.keyspressed[i] = false;
              MIDIPiano.instance.keyspressed2[i] = 0;
              MIDIPiano.instance.masterkeyspressed[i] = 0;
            }


            line = inputbuffer.readLine();
            ((SpinnerNumberModel)MIDIPiano.instance.shiftspinner).setValue(new Integer(Integer.parseInt(line)));
            line = inputbuffer.readLine();
            if (versionnum <= 2.30) MIDIPiano.instance.initialcombovalue = Integer.parseInt(line);
            else {
              MIDIPiano.instance.initialcombovalue = -1;
              MIDIPiano.instance.initialmidivalue = line;
            }
            line = inputbuffer.readLine();
            OptionsUI.keychordtype1 = Integer.parseInt(line);
            line = inputbuffer.readLine();
            OptionsUI.keychordtype2 = Integer.parseInt(line);
          }
        if (versionnum >= 2.03f) {
           line = inputbuffer.readLine();
//           if (line.charAt(0) == '1')
//             OptionsUI.instance.searchexcludeyeskeylock.setSelected(true);
//           else
//             OptionsUI.instance.searchexcludeyeskeylock.setSelected(false);
           line = inputbuffer.readLine();
           if (line.charAt(0) == '1')
             OptionsUI.instance.searchexcludenokeylock.setSelected(true);
           else
             OptionsUI.instance.searchexcludenokeylock.setSelected(false);
         }
        if (versionnum >= 2.04f) {
           line = inputbuffer.readLine();
           if (line.charAt(0) == '1')
             OptionsUI.instance.donotshowdisabledosngs.setSelected(true);
           else
             OptionsUI.instance.donotshowdisabledosngs.setSelected(false);
         }

        if (versionnum >= 2.01f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.searchtableautoscroll.setSelected(true);
            else
              OptionsUI.instance.searchtableautoscroll.setSelected(false);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.mixouttableautoscroll.setSelected(true);
            else
              OptionsUI.instance.mixouttableautoscroll.setSelected(false);
          }
        if (versionnum >= 2.06f) {
            line = inputbuffer.readLine();
            OptionsUI.instance.customfieldtext1.setText(line);
            line = inputbuffer.readLine();
            OptionsUI.instance.customfieldtext2.setText(line);
            line = inputbuffer.readLine();
            OptionsUI.instance.customfieldtext3.setText(line);
            line = inputbuffer.readLine();
            OptionsUI.instance.customfieldtext4.setText(line);
          }

        if (versionnum >= 2.19) {
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.smoothbpmslider.setSelected(true);
          else
            OptionsUI.instance.smoothbpmslider.setSelected(false);
        }
        if (versionnum >= 2.07f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.disableautotagreading.setSelected(true);
            else
              OptionsUI.instance.disableautotagreading.setSelected(false);
          }
        if (versionnum >= 2.08f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.disablekeylockfunctionality.setSelected(true);
            else
              OptionsUI.instance.disablekeylockfunctionality.setSelected(false);
            oldactions.add(OptionsUI.instance.disablekeylockfunctionality);
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.lockpitchshift.setSelected(true);
            else
              OptionsUI.instance.lockpitchshift.setSelected(false);
          }
        if (versionnum >= 2.09f) {
            line = inputbuffer.readLine();
            if (line.charAt(0) == '1')
              OptionsUI.instance.findallpreventrepeats.setSelected(true);
            else
              OptionsUI.instance.findallpreventrepeats.setSelected(false);
          }

        if (versionnum >= 2.71f) {
            int[] columnindices = new int[Integer.parseInt(inputbuffer.readLine())];
            for (int c = 0; c < columnindices.length; ++c) {
                columnindices[c] = Integer.parseInt(inputbuffer.readLine());
            }
            if (columnindices.length == SearchPane.instance.defaultColumnIndices.length) SearchPane.instance.defaultColumnIndices = columnindices;
            else SearchPane.instance.defaultColumnIndices = SearchPane.instance.determineOrdering(SearchPane.instance.defaultColumnIndices, columnindices, false);

            columnindices = new int[Integer.parseInt(inputbuffer.readLine())];
            for (int c = 0; c < columnindices.length; ++c) {
                columnindices[c] = Integer.parseInt(inputbuffer.readLine());
            }
            if (columnindices.length == MixoutPane.instance.defaultColumnIndices.length) MixoutPane.instance.defaultColumnIndices = columnindices;
            else MixoutPane.instance.defaultColumnIndices = MixoutPane.instance.determineOrdering(MixoutPane.instance.defaultColumnIndices, columnindices, false);

            int totalunique = 39;
            if (versionnum >= 2.73f)
              totalunique = Integer.parseInt(inputbuffer.readLine());

            for (int c = 0; c < totalunique; ++c) {
                SearchPane.instance.searchcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
                MixoutPane.instance.mixoutcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
                ExcludeUI.instance.excludecolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
                RootsUI.instance.rootscolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
                SuggestedMixesUI.instance.suggestedcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
                SyncUI.instance.synccolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
                AddMatchQueryUI.instance.matchcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
                EditMatchQueryUI.instance.matchcolumnconfig2.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
            }
        }

        if (versionnum >= 2.0f) {
            line = inputbuffer.readLine();
            SearchPane.instance.searchcolumnconfig.num_columns = Integer.parseInt(line);
            SearchPane.instance.searchcolumnconfig.columnindex = new int[SearchPane.instance.searchcolumnconfig.
                num_columns];
            SearchPane.instance.searchcolumnconfig.columntitles = new String[SearchPane.instance.searchcolumnconfig.
                num_columns];
            SearchPane.instance.searchcolumnconfig.setPreferredWidth(new int[SearchPane.instance.searchcolumnconfig.
                num_columns]);
            for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
              line = inputbuffer.readLine();
              SearchPane.instance.searchcolumnconfig.columnindex[i] = Integer.parseInt(line);
              line = inputbuffer.readLine();
              SearchPane.instance.searchcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
              SearchPane.instance.searchcolumnconfig.columntitles[i] = SearchPane.instance.searchcolumnconfig.
                  getKeyword(
                  SearchPane.instance.searchcolumnconfig.columnindex[i]);

            }
            for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
              int cindex = SearchPane.instance.searchcolumnconfig.columnindex[i];
              OptionsUI.instance.SetSelectedSearchColumn(cindex);
            }
            line = inputbuffer.readLine();
            MixoutPane.instance.mixoutcolumnconfig.num_columns = Integer.parseInt(line);
            MixoutPane.instance.mixoutcolumnconfig.columnindex = new int[MixoutPane.instance.mixoutcolumnconfig.
                num_columns];
            MixoutPane.instance.mixoutcolumnconfig.columntitles = new String[MixoutPane.instance.mixoutcolumnconfig.
                num_columns];
            MixoutPane.instance.mixoutcolumnconfig.setPreferredWidth(new int[MixoutPane.instance.mixoutcolumnconfig.
                num_columns]);
            for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
              line = inputbuffer.readLine();
              MixoutPane.instance.mixoutcolumnconfig.columnindex[i] = Integer.parseInt(line);
              line = inputbuffer.readLine();
              MixoutPane.instance.mixoutcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
              MixoutPane.instance.mixoutcolumnconfig.columntitles[i] = MixoutPane.instance.mixoutcolumnconfig.
                  getKeyword(
                  MixoutPane.instance.mixoutcolumnconfig.columnindex[i]);
            }
            for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
              int cindex = MixoutPane.instance.mixoutcolumnconfig.columnindex[i];
              OptionsUI.instance.SetSelectedMixoutColumn(cindex);
            }
            if (versionnum >= 2.26f) {
              line = inputbuffer.readLine();
              ExcludeUI.instance.excludecolumnconfig.num_columns = Integer.parseInt(line);
              ExcludeUI.instance.excludecolumnconfig.columnindex = new int[ExcludeUI.instance.excludecolumnconfig.num_columns];
              ExcludeUI.instance.excludecolumnconfig.columntitles = new String[ExcludeUI.instance.excludecolumnconfig.num_columns];
              ExcludeUI.instance.excludecolumnconfig.setPreferredWidth(new int[ExcludeUI.instance.excludecolumnconfig.num_columns]);
              for (int i = 0; i < ExcludeUI.instance.excludecolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                ExcludeUI.instance.excludecolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                ExcludeUI.instance.excludecolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                ExcludeUI.instance.excludecolumnconfig.columntitles[i] = ExcludeUI.instance.excludecolumnconfig.
                    getKeyword(
                    ExcludeUI.instance.excludecolumnconfig.columnindex[i]);
              }
              line = inputbuffer.readLine();
              RootsUI.instance.rootscolumnconfig.num_columns = Integer.parseInt(line);
              RootsUI.instance.rootscolumnconfig.columnindex = new int[RootsUI.instance.rootscolumnconfig.num_columns];
              RootsUI.instance.rootscolumnconfig.columntitles = new String[RootsUI.instance.rootscolumnconfig.num_columns];
              RootsUI.instance.rootscolumnconfig.setPreferredWidth(new int[RootsUI.instance.rootscolumnconfig.num_columns]);
              for (int i = 0; i < RootsUI.instance.rootscolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                RootsUI.instance.rootscolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                RootsUI.instance.rootscolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                RootsUI.instance.rootscolumnconfig.columntitles[i] = RootsUI.instance.rootscolumnconfig.
                    getKeyword(
                    RootsUI.instance.rootscolumnconfig.columnindex[i]);
              }
              line = inputbuffer.readLine();
              SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns = Integer.parseInt(line);
              SuggestedMixesUI.instance.suggestedcolumnconfig.columnindex = new int[SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns];
              SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles = new String[SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns];
              SuggestedMixesUI.instance.suggestedcolumnconfig.setPreferredWidth(new int[SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns]);
              for (int i = 0; i < SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                SuggestedMixesUI.instance.suggestedcolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                SuggestedMixesUI.instance.suggestedcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i] = SuggestedMixesUI.instance.suggestedcolumnconfig.
                    getKeyword(
                    SuggestedMixesUI.instance.suggestedcolumnconfig.columnindex[i]);
              }
              line = inputbuffer.readLine();
              SyncUI.instance.synccolumnconfig.num_columns = Integer.parseInt(line);
              SyncUI.instance.synccolumnconfig.columnindex = new int[SyncUI.instance.synccolumnconfig.num_columns];
              SyncUI.instance.synccolumnconfig.columntitles = new String[SyncUI.instance.synccolumnconfig.num_columns];
              SyncUI.instance.synccolumnconfig.setPreferredWidth(new int[SyncUI.instance.synccolumnconfig.num_columns]);
              for (int i = 0; i < SyncUI.instance.synccolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                SyncUI.instance.synccolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                SyncUI.instance.synccolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                SyncUI.instance.synccolumnconfig.columntitles[i] = SyncUI.instance.synccolumnconfig.
                    getKeyword(
                    SyncUI.instance.synccolumnconfig.columnindex[i]);
              }

              if (versionnum >= 2.39) {
                line = inputbuffer.readLine();
                AddMatchQueryUI.instance.matchcolumnconfig.num_columns = Integer.parseInt(line);
                AddMatchQueryUI.instance.matchcolumnconfig.columnindex = new int[AddMatchQueryUI.instance.matchcolumnconfig.num_columns];
                AddMatchQueryUI.instance.matchcolumnconfig.columntitles = new String[AddMatchQueryUI.instance.matchcolumnconfig.num_columns];
                AddMatchQueryUI.instance.matchcolumnconfig.setPreferredWidth(new int[AddMatchQueryUI.instance.matchcolumnconfig.num_columns]);
                for (int i = 0; i < AddMatchQueryUI.instance.matchcolumnconfig.num_columns; ++i) {
                  line = inputbuffer.readLine();
                  AddMatchQueryUI.instance.matchcolumnconfig.columnindex[i] = Integer.parseInt(line);
                  line = inputbuffer.readLine();
                  AddMatchQueryUI.instance.matchcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                  AddMatchQueryUI.instance.matchcolumnconfig.columntitles[i] = AddMatchQueryUI.instance.matchcolumnconfig.getKeyword(AddMatchQueryUI.instance.matchcolumnconfig.columnindex[i]);
                }
                line = inputbuffer.readLine();
                EditMatchQueryUI.instance.matchcolumnconfig2.num_columns = Integer.parseInt(line);
                EditMatchQueryUI.instance.matchcolumnconfig2.columnindex = new int[EditMatchQueryUI.instance.matchcolumnconfig2.num_columns];
                EditMatchQueryUI.instance.matchcolumnconfig2.columntitles = new String[EditMatchQueryUI.instance.matchcolumnconfig2.num_columns];
                EditMatchQueryUI.instance.matchcolumnconfig2.setPreferredWidth(new int[EditMatchQueryUI.instance.matchcolumnconfig2.num_columns]);
                for (int i = 0; i < EditMatchQueryUI.instance.matchcolumnconfig2.num_columns; ++i) {
                  line = inputbuffer.readLine();
                  EditMatchQueryUI.instance.matchcolumnconfig2.columnindex[i] = Integer.parseInt(line);
                  line = inputbuffer.readLine();
                  EditMatchQueryUI.instance.matchcolumnconfig2.setPreferredWidth(i, Integer.parseInt(line));
                  EditMatchQueryUI.instance.matchcolumnconfig2.columntitles[i] = EditMatchQueryUI.instance.matchcolumnconfig2.getKeyword(EditMatchQueryUI.instance.matchcolumnconfig2.columnindex[i]);
                }
              }
            }
          }

        if (versionnum < 2.79f) {
          if (versionnum >= 2.00f) {
              line = inputbuffer.readLine();
              split1 = Integer.parseInt(line);
              line = inputbuffer.readLine();
              split2 = Integer.parseInt(line);
              line = inputbuffer.readLine();
              split3 = Integer.parseInt(line);
            }
          if (versionnum >= 2.18f) {
              line = inputbuffer.readLine();
              if (line.charAt(0) == '1') OptionsUI.instance.fullstyleslayout.setSelected(true);
              else OptionsUI.instance.fullstyleslayout.setSelected(false);
              oldactions.add(OptionsUI.instance.fullstyleslayout);
              line = inputbuffer.readLine();
              split1 = Integer.parseInt(line);
              line = inputbuffer.readLine();
              split2 = Integer.parseInt(line);
              line = inputbuffer.readLine();
              split3 = Integer.parseInt(line);
            }
          }
          // read options
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.excludesongsondonottrylist.setSelected(true);
          else
            OptionsUI.instance.excludesongsondonottrylist.setSelected(false);
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.preventrepeats.setSelected(true);
          else
            OptionsUI.instance.preventrepeats.setSelected(false);
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.searchwithinstyles.setSelected(true);
          else
            OptionsUI.instance.searchwithinstyles.setSelected(false);
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.evenbpmmultiples.setSelected(true);
          else
            OptionsUI.instance.evenbpmmultiples.setSelected(false);
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.keysearchonly = true;
          else
            OptionsUI.instance.keysearchonly = false;
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.ignoremajorminor.setSelected(true);
          else
            OptionsUI.instance.ignoremajorminor.setSelected(false);
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.clearsearchautomatically.setSelected(true);
          else
            OptionsUI.instance.clearsearchautomatically.setSelected(false);
          line = inputbuffer.readLine();
          line = inputbuffer.readLine();
//          if (line.charAt(0) == '1')
//            timestamps = true;
//          else
//            timestamps = false;
          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.excludesongsonsamerecord.setSelected(true);
          else
            OptionsUI.instance.excludesongsonsamerecord.setSelected(false);
        if (versionnum >= 1.42f) {
            line = inputbuffer.readLine();
//            if (line.charAt(0) == '1')
//              shadecolors = true;
//            else
//              shadecolors = false;
          }
          if (versionnum >= 1.43f) {
            line = inputbuffer.readLine();
//            if (line.charAt(0) == '1')
//              detectedkeysuseshorthand = true;
//            else
//              detectedkeysuseshorthand = false;
          }
          if (versionnum >= 1.44f) {
            line = inputbuffer.readLine();
//            if (line.charAt(0) == '1')
//              savefieldstocomments.setSelected(true);
//            else
//              savefieldstocomments.setSelected(false);
          }
          if (versionnum >= 1.47f) {
            line = inputbuffer.readLine();
//            if (line.charAt(0) == '1')
//              consistencycheck = true;
//            else
//              consistencycheck = false;
          }
          if (versionnum >= 2.0f) {
            line = inputbuffer.readLine();
          }
          line = inputbuffer.readLine();
          rapidevolution_ui.bpmscale = Float.parseFloat(line);
          //to do: load bpmscale to slider...
          rapidevolution_ui.bpmscale = 8;
          // read in colors
          int r;
          int g;
          int b;

          if (versionnum >= 2.33f) {
            if (versionnum >= 2.34f) {
              line = inputbuffer.readLine();
              r = Integer.parseInt(line);
              line = inputbuffer.readLine();
              g = Integer.parseInt(line);
              line = inputbuffer.readLine();
              b = Integer.parseInt(line);
              SkinManager.instance.setColor("style_background_selected", new Color(r, g, b));
            }
            line = inputbuffer.readLine();
            r = Integer.parseInt(line);
            line = inputbuffer.readLine();
            g = Integer.parseInt(line);
            line = inputbuffer.readLine();
            b = Integer.parseInt(line);
            SkinManager.instance.setColor("style_background_of_selected_song", new Color(r, g, b));
            line = inputbuffer.readLine();
            r = Integer.parseInt(line);
            line = inputbuffer.readLine();
            g = Integer.parseInt(line);
            line = inputbuffer.readLine();
            b = Integer.parseInt(line);
            SkinManager.instance.setColor("style_background_of_current_song", new Color(r, g, b));
          }

          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("song_background_disabled", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("song_background_in_key", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("song_background_in_key_with_mixouts", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("song_background_with_mixouts", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("mixout_background_disabled", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("mixout_background_addon", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("mixout_background_ranked", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("mixout_background_ranked_with_mixouts", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("mixout_background_with_mixouts", new Color(r, g, b));
          line = inputbuffer.readLine();
          r = Integer.parseInt(line);
          line = inputbuffer.readLine();
          g = Integer.parseInt(line);
          line = inputbuffer.readLine();
          b = Integer.parseInt(line);
          SkinManager.instance.setColor("mixout_background_addon_ranked", new Color(r, g, b));
          if (versionnum >= 2.00f) {
            line = inputbuffer.readLine();
            r = Integer.parseInt(line);
            line = inputbuffer.readLine();
            g = Integer.parseInt(line);
            line = inputbuffer.readLine();
            b = Integer.parseInt(line);
            SkinManager.instance.setColor("song_background_default", new Color(r, g, b));
            line = inputbuffer.readLine();
            r = Integer.parseInt(line);
            line = inputbuffer.readLine();
            g = Integer.parseInt(line);
            line = inputbuffer.readLine();
            b = Integer.parseInt(line);
            SkinManager.instance.setColor("mixout_background_default", new Color(r, g, b));
          }
          // more parameters...
          if (versionnum >= 1.46f) {
            line = inputbuffer.readLine();
            rapidevolution_ui.keylockcurrentsong.setSelected(false);
            if (line.charAt(0) == '1')
              rapidevolution_ui.keylockcurrentsong.setSelected(true);
            line = inputbuffer.readLine();
//            keylock2 = false;
//            if (line.charAt(0) == '1')
//              keylock2 = true;
          }
          line = inputbuffer.readLine();
//          rankthreshold = Integer.parseInt(line);
          if (version.matches("1.4") == false) {
            line = inputbuffer.readLine();
            //mixoutthresholdrank = Integer.parseInt(line);
          }
          if (versionnum >= 1.45f) {
            OptionsUI.instance.username.setText(inputbuffer.readLine());
            OptionsUI.instance.password.setText(inputbuffer.readLine());
            OptionsUI.instance.username.setEnabled(false);
            OptionsUI.instance.emailaddress.setText(inputbuffer.readLine());
            if (versionnum >= 2.65f) OptionsUI.instance.userwebsite.setText(inputbuffer.readLine());
          }
          if (versionnum >= 2.12f) {
            line = inputbuffer.readLine();
            OptionsUI.instance.donotclearuserfield1whenadding.setSelected(false);
            if (line.charAt(0) == '1')
              OptionsUI.instance.donotclearuserfield1whenadding.setSelected(true);
            line = inputbuffer.readLine();
            OptionsUI.instance.donotclearuserfield2whenadding.setSelected(false);
            if (line.charAt(0) == '1')
              OptionsUI.instance.donotclearuserfield2whenadding.setSelected(true);
            line = inputbuffer.readLine();
            OptionsUI.instance.donotclearuserfield3whenadding.setSelected(false);
            if (line.charAt(0) == '1')
              OptionsUI.instance.donotclearuserfield3whenadding.setSelected(true);
            line = inputbuffer.readLine();
            OptionsUI.instance.donotclearuserfield4whenadding.setSelected(false);
            if (line.charAt(0) == '1')
              OptionsUI.instance.donotclearuserfield4whenadding.setSelected(true);
          }
          if (versionnum >= 2.11f) {
            line = inputbuffer.readLine();
            SongDB.instance.stylesdirtybit = Integer.parseInt(line);
          } else SongDB.instance.stylesdirtybit = 1;

          // read the styles
          line = inputbuffer.readLine();
          SongDB.instance.num_styles = Integer.parseInt(line);
          StyleLinkedList lastaddedstyle = null;
          int sindex = 0;
          for (int i = 0; i < SongDB.instance.num_styles; ++i) {
            String stylename = inputbuffer.readLine();
            if (SongDB.instance.masterstylelist == null) lastaddedstyle = SongDB.instance.masterstylelist = new StyleLinkedList(stylename, null, StyleLinkedList.getNextStyleId());
            else lastaddedstyle = lastaddedstyle.next = new StyleLinkedList(stylename, null, StyleLinkedList.getNextStyleId());
            lastaddedstyle.set_sindex(sindex++);

            int num_keywords = Integer.parseInt(inputbuffer.readLine());
            for (int j = 0; j < num_keywords; ++j) {
              String keyword = inputbuffer.readLine();
              lastaddedstyle.insertKeyword(keyword);
            }
            if (versionnum >= 2.05f) {
              int num_excludekeywords = Integer.parseInt(inputbuffer.readLine());
              for (int j = 0; j < num_excludekeywords; ++j) {
                String keyword = inputbuffer.readLine();
                lastaddedstyle.insertExcludeKeyword(keyword);
              }
            }
            if (versionnum >= 2.70f) {
              int num_songs = Integer.parseInt(inputbuffer.readLine());
              for (int j = 0; j < num_songs; ++j) {
                long song = Long.parseLong(inputbuffer.readLine());
                lastaddedstyle.insertSong(song);
              }
              int num_excludesongs = Integer.parseInt(inputbuffer.readLine());
              for (int j = 0; j < num_excludesongs; ++j) {
                long song = Long.parseLong(inputbuffer.readLine());
                lastaddedstyle.insertExcludeSong(song);
              }
            }
            if (versionnum >= 2.72f) {
              int num_include = Integer.parseInt(inputbuffer.readLine());
              for (int z = 0; z < num_include; ++z) {
                lastaddedstyle.styleincludedisplayvector.add(inputbuffer.readLine());
                int type = Integer.parseInt(inputbuffer.readLine());
                if (type == 0) {
                  lastaddedstyle.styleincludevector.add(new Long(Long.parseLong(inputbuffer.readLine())));
                } else {
                  lastaddedstyle.styleincludevector.add(inputbuffer.readLine());
                }
              }

              int num_exclude = Integer.parseInt(inputbuffer.readLine());
              for (int z = 0; z < num_exclude; ++z) {
                lastaddedstyle.styleexcludedisplayvector.add(inputbuffer.readLine());
                int type = Integer.parseInt(inputbuffer.readLine());
                if (type == 0) {
                  lastaddedstyle.styleexcludevector.add(new Long(Long.parseLong(inputbuffer.readLine())));
                } else {
                  lastaddedstyle.styleexcludevector.add(inputbuffer.readLine());
                }
              }

            }
          }
          if (versionnum < 2.72f) SongDB.instance.masterstylelist = SongDB.instance.sortStyleList(SongDB.instance.masterstylelist);
         if (SongDB.instance.stylesortexception) { SongDB.instance.stylesortexception = false; throw new Exception(); }

         if (versionnum >= 2.70f) {
           SongLinkedList.minuniqueid = Integer.parseInt(inputbuffer.readLine());
           line = inputbuffer.readLine();
           if (line.equals("1")) SongLinkedList.checkuniqueid = true;
           else SongLinkedList.checkuniqueid = false;
         }

          SongLinkedList AddPtr = null;
          line = inputbuffer.readLine();
          while ((line != null) && line.equals("-")) {
            ++SongDB.instance.total_songs;
//          ++initialsongsloaded;
            int uniqueid = 0;
            if (versionnum >= 2.70f) uniqueid = Integer.parseInt(inputbuffer.readLine());
            else {
              uniqueid = SongLinkedList.getNextUniqueID();
            }
            String album = inputbuffer.readLine();
            String artist = inputbuffer.readLine();
            String track = inputbuffer.readLine();
            String songname = inputbuffer.readLine();
            String remixer = new String("");
            String user1 = new String("");
            String user2 = new String("");
            String user3 = new String("");
            String user4 = new String("");
            if (versionnum >= 2.06f) {
              remixer = inputbuffer.readLine();
              user1 = inputbuffer.readLine();
              user2 = inputbuffer.readLine();
              user3 = inputbuffer.readLine();
              user4 = inputbuffer.readLine();
            }
            int numcommentlines = 1;
            if (versionnum >= 2.00f)
              numcommentlines = Integer.parseInt(inputbuffer.readLine());
            String comments = new String("");
            for (int cl = 1; cl <= numcommentlines; ++cl) {
              comments += inputbuffer.readLine();
              if (cl != numcommentlines)
                comments += "\n";
            }
            String timesig = inputbuffer.readLine();
            line = inputbuffer.readLine();
            boolean vinylbit = false;
            if (line.charAt(0) == '1')
              vinylbit = true;
            line = inputbuffer.readLine();
            boolean nonvinylbit = false;
            if (line.charAt(0) == '1')
              nonvinylbit = true;
            line = inputbuffer.readLine();
            boolean disabled = false;
            if (line.charAt(0) == '1')
              disabled = true;
            boolean servercached = false;
            if (versionnum >= 2.13f) {
              line = inputbuffer.readLine();
              if (line.charAt(0) == '1')
                servercached = true;
            }
            if (versionnum < dirtycachever) servercached = false;
            String filename = inputbuffer.readLine();
            String startkey = inputbuffer.readLine();
            String endkey = inputbuffer.readLine();
            String tracktime = inputbuffer.readLine();
            float startbpm = Float.parseFloat(inputbuffer.readLine());
            float endbpm = Float.parseFloat(inputbuffer.readLine());

            int times_played = 0;
            if (versionnum >= 2.77f) {
              times_played = Integer.parseInt(inputbuffer.readLine());
            }

            java.util.Date dateadded = new java.util.Date();
            if (versionnum >= 2.61f) {
              dateadded = masterdateformat.parse(inputbuffer.readLine());
            }
            String itunes_id = null;
            if (versionnum >= 2.67f) {
              itunes_id = inputbuffer.readLine();
              if (itunes_id.equals("")) itunes_id = null;
            }
            int num_mixouts = Integer.parseInt(inputbuffer.readLine());
            int num_excludes = Integer.parseInt(inputbuffer.readLine());

            try {
              SongDB.instance.addsinglesongsem.acquire();
              if (AddPtr == null)
                AddPtr = SongDB.instance.SongLL = new SongLinkedList(uniqueid, artist, album, track,
                    songname, remixer, comments, vinylbit, nonvinylbit,
                    startbpm, endbpm,
                    num_mixouts, num_excludes, Key.getKey(startkey), Key.getKey(endkey), filename,
                    tracktime, timesig, disabled, servercached, user1, user2,
                    user3, user4, null);
              else
                AddPtr = AddPtr.next = new SongLinkedList(uniqueid, artist, album, track,
                    songname, remixer, comments, vinylbit, nonvinylbit,
                    startbpm, endbpm,
                    num_mixouts, num_excludes, Key.getKey(startkey), Key.getKey(endkey), filename,
                    tracktime, timesig, disabled, servercached, user1, user2,
                    user3, user4, null);

            } catch (Exception e) { }
            SongDB.instance.addsinglesongsem.release();
            AddPtr.setDateAdded(SongLinkedList.getStringDateFormat(dateadded));
            AddPtr.itunes_id = itunes_id;

            AddPtr.setTimesPlayed(times_played);
            AddPtr.setStyles(new boolean[SongDB.instance.num_styles]);
            if (versionnum >= 2.10f) {
              String stylestring = inputbuffer.readLine();
              for (int z = 0; z < SongDB.instance.num_styles; ++z) {
                if (stylestring.charAt(z) == '1') AddPtr.setStyle(z, true);
                else AddPtr.setStyle(z, false);
              }
            } else {
              for (int z = 0; z < SongDB.instance.num_styles; ++z) AddPtr.setStyle(z, false);
            }
            total_mixes += num_mixouts;
            for (int i = 0; i < num_mixouts; ++i) {
              String mixoutsong = inputbuffer.readLine();
              String mixoutsongname = null;
              float bpmdiff = Float.parseFloat(inputbuffer.readLine());
              int rank = Integer.parseInt(inputbuffer.readLine());
              boolean mixoutcache = false;
              if (versionnum >= 2.14f) {
                String tmp = inputbuffer.readLine();
                if (tmp.charAt(0) == '1') mixoutcache = true;
              }
              if (versionnum < dirtycachever) mixoutcache = false;
              numcommentlines = 1;
              if (versionnum >= 2.00f)
                numcommentlines = Integer.parseInt(inputbuffer.readLine());
              String mixcomments = new String("");
              for (int cl = 1; cl <= numcommentlines; ++cl) {
                mixcomments += inputbuffer.readLine();
                if (cl != numcommentlines)
                  mixcomments += "\n";
              }
              boolean addon = false;
              line = inputbuffer.readLine();
              if (line.charAt(0) == '1')
                addon = true;
              if (versionnum < 2.44f) mixoutsong = mixoutsong.toLowerCase();
              if (versionnum >= 2.70f) {
                AddPtr.mixout_songs[i] = Long.parseLong(mixoutsong);
              } else AddPtr.setTempMixoutSongname(i, mixoutsong);
              AddPtr._mixout_ranks[i] = rank;
              AddPtr._mixout_comments[i] = mixcomments;
              AddPtr._mixout_bpmdiff[i] =  bpmdiff;
              AddPtr.mixout_servercache[i] = mixoutcache;
              AddPtr._mixout_addons[i] = addon;
            }
            //AddPtr.sort_mixouts();

            AddPtr.calculateSongDisplayIds();
            SongDB.instance.songmap.put(new Long(AddPtr.uniquesongid), AddPtr);
            SongDB.instance.uniquesongstring_songmap.put(AddPtr.getUniqueStringId(), AddPtr);

            for (int i = 0; i < num_excludes; ++i) {
              String excludesong = inputbuffer.readLine();
              String excludesongname = null;
              if (versionnum < 2.44f) excludesong = excludesong.toLowerCase();
              if (versionnum >= 2.70f) {
                AddPtr.exclude_songs[i] = Long.parseLong(excludesong);
              } else AddPtr.setTempExcludeSongname(i, excludesong);
            }
            //AddPtr.sort_excludes();

            if (versionnum >= 2.35f) {
              line = inputbuffer.readLine();
              int scolorver = Integer.parseInt(line);
              if (scolorver == 1) {
                line = inputbuffer.readLine();
                int windowsize = Integer.parseInt(line);
                line = inputbuffer.readLine();
                float samplerate = Float.parseFloat(line);
                line = inputbuffer.readLine();
                double tracktime2 = Double.parseDouble(line);
                SongColor color = new SongColor(windowsize, samplerate, tracktime2, 1);
                for (int e = 0; e < color.num_bands; ++e) {
                line = inputbuffer.readLine();
                color.avgcentroid[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.variancecentroid[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.avgrolloff[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.variancerolloff[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.avgflux[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.varianceflux[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.avgzerocrossings[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.variancezerocrossing[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.lowenergy[e] = Float.parseFloat(line);
                }
                line = inputbuffer.readLine();
                color.period0 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude0 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.ratioperiod1 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude1 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.ratioperiod2 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude2 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.ratioperiod3 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude3 = Double.parseDouble(line);
                AddPtr.color = color;
              }
            }
            line = inputbuffer.readLine();
          }
        }
      }
      if (versionnum >= 2.66f) {
        int numartists = Integer.parseInt(inputbuffer.readLine());
        for (int i = 0; i < numartists; ++i) {
          String name = inputbuffer.readLine();
          String searchname = inputbuffer.readLine();
          int songcount = Integer.parseInt(inputbuffer.readLine());
          Vector songs = new Vector();
          for (int j = 0; j < songcount; ++j) {
            String val = inputbuffer.readLine();
            if (versionnum >= 2.70f) {
              songs.add(new Long(Long.parseLong(val)));
            } else {
              SongLinkedList song = SongDB.instance.OldGetSongPtr(val);
              if (song != null) songs.add(new Long(song.uniquesongid));
            }
          }
          boolean cached = false;
          if (inputbuffer.readLine().equals("1")) cached = true;
          int numsearchstyles = Integer.parseInt(inputbuffer.readLine());
          Vector searchstyles = new Vector();
          Vector searchcount = new Vector();
          for (int j = 0; j < numsearchstyles; ++j) {
            searchstyles.add(inputbuffer.readLine());
            searchcount.add(new Integer(Integer.parseInt(inputbuffer.readLine())));
          }
          boolean stylescached = false;
          if (inputbuffer.readLine().equals("1")) stylescached = true;
          boolean profilecached = false;
          if (inputbuffer.readLine().equals("1")) profilecached = true;
          int numprofiles = Integer.parseInt(inputbuffer.readLine());
          Vector styles = new Vector();
          Vector weights = new Vector();
          for (int j = 0; j < numprofiles; ++j) {
            styles.add(inputbuffer.readLine());
            weights.add(new Double(Double.parseDouble(inputbuffer.readLine())));
          }
        }
      }
      inputbuffer.close();
      inputstream.close();
    } catch (FileNotFoundException fnfe) { }
    catch (IOException jio) { }
    catch (Exception e) {
        log.debug("Load failed! Finding most recent backup...", e);
        String backupfilename = new String("");
        File dir = new File(".");
        File files[] = new File[dir.listFiles().length];
        files = dir.listFiles();
        files = FileUtil.sortfiles(files);
        for (int i = 0; i < files.length; ++i) {
          if (!files[i].isDirectory()) {
            if (files[i].getName().toLowerCase().endsWith(".dat")) {
              if (files[i].getName().toLowerCase().startsWith("backup")) {
                boolean found = false;
                for (int z = 0; z < triedtoload.size(); ++z) {
                  String s = (String)triedtoload.get(z);
                  if (s.equals(files[i].getName())) found = true;
                }
                if (!found) backupfilename = files[i].getName();
              }
            }
          }
        }
        if (!backupfilename.equals("")) {
          String messagetext = SkinManager.instance.getDialogMessageText("error_loading_database");
          messagetext = StringUtil.ReplaceString("%filename%", messagetext, loadfilename);
          messagetext = StringUtil.ReplaceString("%backupfilename%", messagetext, backupfilename);
          int n = IOptionPane.showConfirmDialog(
              SkinManager.instance.getFrame("main_frame"),
              messagetext,
              SkinManager.instance.getDialogMessageTitle("error_loading_database"),
              IOptionPane.YES_NO_OPTION);
          RapidEvolution.getMixshareClient().setOutOfSync(true);
          if (n == 0)
            return load(backupfilename);
          else return 0;
        } else return 0;
    }

    OptionsUI.instance.searchcolumn_user1.setText(OptionsUI.instance.customfieldtext1.getText());
    OptionsUI.instance.searchcolumn_user2.setText(OptionsUI.instance.customfieldtext2.getText());
    OptionsUI.instance.searchcolumn_user3.setText(OptionsUI.instance.customfieldtext3.getText());
    OptionsUI.instance.searchcolumn_user4.setText(OptionsUI.instance.customfieldtext4.getText());
    OptionsUI.instance.mixoutcolumn_user1.setText(OptionsUI.instance.customfieldtext1.getText());
    OptionsUI.instance.mixoutcolumn_user2.setText(OptionsUI.instance.customfieldtext2.getText());
    OptionsUI.instance.mixoutcolumn_user3.setText(OptionsUI.instance.customfieldtext3.getText());
    OptionsUI.instance.mixoutcolumn_user4.setText(OptionsUI.instance.customfieldtext4.getText());
    OptionsUI.instance.oldcustomfield1 = OptionsUI.instance.customfieldtext1.getText();
    OptionsUI.instance.oldcustomfield2 = OptionsUI.instance.customfieldtext2.getText();
    OptionsUI.instance.oldcustomfield3 = OptionsUI.instance.customfieldtext3.getText();
    OptionsUI.instance.oldcustomfield4 = OptionsUI.instance.customfieldtext4.getText();
    SearchPane.asearchlistmouse.setuser1.setText(OptionsUI.instance.customfieldtext1.getText());
    SearchPane.asearchlistmouse.setuser2.setText(OptionsUI.instance.customfieldtext2.getText());
    SearchPane.asearchlistmouse.setuser3.setText(OptionsUI.instance.customfieldtext3.getText());
    SearchPane.asearchlistmouse.setuser4.setText(OptionsUI.instance.customfieldtext4.getText());
    AddSongsUI.instance.addcustomfieldlabel1.setText(OptionsUI.instance.customfieldtext1.getText() + ":");
    AddSongsUI.instance.addcustomfieldlabel2.setText(OptionsUI.instance.customfieldtext2.getText() + ":");
    AddSongsUI.instance.addcustomfieldlabel3.setText(OptionsUI.instance.customfieldtext3.getText() + ":");
    AddSongsUI.instance.addcustomfieldlabel4.setText(OptionsUI.instance.customfieldtext4.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel1.setText(OptionsUI.instance.customfieldtext1.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel2.setText(OptionsUI.instance.customfieldtext2.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel3.setText(OptionsUI.instance.customfieldtext3.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel4.setText(OptionsUI.instance.customfieldtext4.getText() + ":");

//    OptionsUI.instance.searchexcludeyeskeylock.setSelected(false);
    if (OptionsUI.instance.disablekeylockfunctionality.isSelected()) {
      rapidevolution_ui.keylockcurrentsong.setSelected(false);
      rapidevolution_ui.keylockcurrentsong.setEnabled(false);
    }

    if (versionnum < 2.55f) {
      SongDB.instance.SortSongLL();
    }

    if (versionnum < 2.56f) {
      StyleLinkedList siter = SongDB.instance.masterstylelist;
      while (siter != null) {
          String[] keywords = siter.getKeywords();
        for (int i = 0; i < keywords.length; ++i) {
          String klc = keywords[i].toLowerCase();
          if (!klc.equals(keywords[i])) {
            if (SongDB.instance.OldGetSongPtr(klc) != null) {
                siter.keywords.remove(keywords[i]);
                siter.keywords.put(klc, null);
            }
          }
        }
        String[] excludekeywords = siter.getExcludeKeywords();
        for (int i = 0; i < excludekeywords.length; ++i) {
          String klc = excludekeywords[i].toLowerCase();          
          if (!klc.equals(excludekeywords[i])) {
            if (SongDB.instance.OldGetSongPtr(klc) != null) {
                siter.excludekeywords.remove(excludekeywords[i]);
                siter.excludekeywords.put(klc, null);
            }
          }
        }
        siter = siter.next;
      }
    }

    if (versionnum < 2.70f) {
      SongLinkedList siter = SongDB.instance.SongLL;
      while (siter != null) {
        for (int i = 0; i < siter.getNumMixoutSongs(); ++i) {
          SongLinkedList mixsong = SongDB.instance.OldGetSongPtr(siter.getTempMixoutSongname(i));
          siter.mixout_songs[i] = mixsong.uniquesongid;
        }
        for (int i = 0; i < siter.getNumExcludeSongs(); ++i) {
          SongLinkedList excludesong = SongDB.instance.OldGetSongPtr(siter.getTempExcludeSongname(i));
          siter.exclude_songs[i] = excludesong.uniquesongid;
        }
        siter = siter.next;
      }
      StyleLinkedList iter = SongDB.instance.masterstylelist;
      while (iter != null) {
        HashMap keywords = new HashMap();
        HashMap songs = new HashMap();
        HashMap excludekeywords = new HashMap();
        HashMap excludesongs = new HashMap();
        String[] ikeywords = iter.getKeywords();
        for (int i = 0; i < ikeywords.length; ++i) {
          SongLinkedList test = SongDB.instance.OldGetSongPtr(ikeywords[i]);
          if (test != null) songs.put(new Long(test.uniquesongid), null);
          else keywords.put(ikeywords[i], null);
        }
        String[] ekeywords = iter.getExcludeKeywords();
        for (int i = 0; i < ekeywords.length; ++i) {
          SongLinkedList test = SongDB.instance.OldGetSongPtr(ekeywords[i]);
          if (test != null) excludesongs.put(new Long(test.uniquesongid), null);
          else excludekeywords.put(ekeywords[i], null);
        }
        iter.setKeywords(keywords);
        iter.setSongs(songs);
        iter.setExcludeKeywords(excludekeywords);
        iter.setExcludeSongs(excludesongs);
        iter = iter.next;
      }
    }

    if (versionnum < 2.72f) {
      StyleLinkedList siter = SongDB.instance.masterstylelist;
      while (siter != null) {
          String[] keywords = siter.getKeywords();
        for (int j = 0; j < keywords.length; ++j) {
          String keyword = keywords[j];
          String sortkeyword = keyword.toLowerCase();
          keyword = new String("<" + keyword + ">");
          boolean inserted = false;
          int z = 0;
          while ((z < siter.styleincludedisplayvector.size()) && !inserted) {
            String cmpstr = ((String)siter.styleincludedisplayvector.get(z)).toLowerCase();
            if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
            if (sortkeyword.compareTo(cmpstr) < 0) {
              inserted = true;
              siter.styleincludevector.insertElementAt(keywords[j], z);
              siter.styleincludedisplayvector.insertElementAt(keyword, z);
            }
            ++z;
          }
          if (!inserted) {
            siter.styleincludevector.add(keywords[j]);
            siter.styleincludedisplayvector.add(keyword);
          }
        }

        long[] songs = siter.getSongs();
        for (int j = 0; j < songs.length; ++j) {
          SongLinkedList sll = SongDB.instance.NewGetSongPtr(songs[j]);
          String sortkeyword = sll.getSongIdShort();
          String keyword = sll.getSongIdShort();
          boolean inserted = false;
          int z = 0;
          while ((z < siter.styleincludedisplayvector.size()) && !inserted) {
            String cmpstr = ((String)siter.styleincludedisplayvector.get(z));
            if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
            if (sortkeyword.compareToIgnoreCase(cmpstr) < 0) {
              inserted = true;
              siter.styleincludevector.insertElementAt(new Long(songs[j]), z);
              siter.styleincludedisplayvector.insertElementAt(keyword, z);
            }
            ++z;
          }
          if (!inserted) {
            siter.styleincludevector.add(new Long(songs[j]));
            siter.styleincludedisplayvector.add(keyword);
          }
        }

        String[] excludekeywords = siter.getExcludeKeywords();
        for (int i = 0; i < excludekeywords.length; ++i) {
          String keyword = excludekeywords[i];
          String sortkeyword = keyword.toLowerCase();
          keyword = new String("<" + keyword + ">");
          boolean inserted = false;
          int j = 0;
          while ((j < siter.styleexcludedisplayvector.size()) && !inserted) {
            String cmpstr = ((String)siter.styleexcludedisplayvector.get(j)).toLowerCase();
            if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
            if (sortkeyword.compareTo(cmpstr) < 0) {
              inserted = true;
              siter.styleexcludevector.insertElementAt(excludekeywords[i], j);
              siter.styleexcludedisplayvector.insertElementAt(keyword, j);
            }
            ++j;
          }
          if (!inserted) {
            siter.styleexcludevector.add(excludekeywords[i]);
            siter.styleexcludedisplayvector.add(keyword);
          }
        }

        long[] excludesongs = siter.getExcludeSongs();
        for (int i = 0; i < excludesongs.length; ++i) {
          SongLinkedList sll = SongDB.instance.NewGetSongPtr(excludesongs[i]);
          String keyword = sll.getSongIdShort();
          boolean inserted = false;
          int j = 0;
          while ((j < siter.styleexcludedisplayvector.size()) && !inserted) {
            String cmpstr = ((String)siter.styleexcludedisplayvector.get(j));
            if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
            if (keyword.compareToIgnoreCase(cmpstr) < 0) {
              inserted = true;
              siter.styleexcludevector.insertElementAt(new Long(excludesongs[i]), j);
              siter.styleexcludedisplayvector.insertElementAt(keyword, j);
            }
            ++j;
          }
          if (!inserted) {
            siter.styleexcludevector.add(new Long(excludesongs[i]));
            siter.styleexcludedisplayvector.add(keyword);
          }
        }

        siter = siter.next;
      }
    }

    SongLinkedList iter = SongDB.instance.SongLL;
    while (iter != null) {
      Artist.InsertToArtistList(iter);
      iter = iter.next;
    }
    
    SearchPane.searchdisplaylist = new SongList();
    SongLinkedList siter = SongDB.instance.SongLL;
    while (siter != null) {
      SearchPane.searchdisplaylist.insert(siter);
      if (versionnum < 2.24) {
        for (int i = 0; i < siter.getNumMixoutSongs(); ++i) {
          SongLinkedList miter = SongDB.instance.NewGetSongPtr(siter.mixout_songs[i]);
          if ((miter == null) || (miter == siter)) {
              log.debug("NULL mixout found!");
            siter.removeMixOut(i--);
          }
        }
        for (int i = 0; i < siter.getNumExcludeSongs(); ++i) {
          SongLinkedList miter = SongDB.instance.NewGetSongPtr(siter.exclude_songs[i]);
          if ((miter == null) || (miter == siter)) {
              log.debug("NULL exclude found!");
            siter.removeExclude(i--);
          }
        }
      }
      siter = siter.next;
    }
    if ((version != null) && (version.matches("2.15") || version.matches("2.14") || version.matches("2.13") || version.matches("2.12") || version.matches("2.11") || version.matches("2.10") || version.matches("2.09") || version.matches("2.08") || version.matches("2.07"))) SongDB.instance.stylesdirtybit = 1;
    if ((SongDB.instance.stylesdirtybit != 0) || (versionnum < 2.57f)) {
//      stylesdirtybit = 0;
      new UpdateStyles().start();
    }

    if (SearchPane.searchdisplaylist.song == null) SearchPane.searchdisplaylist = null;

    //KeyDetector.CreateMatrix(44100);

//        RapidEvolutionUI.instance.mainWindow.setVisible(true);
//    RapidEvolutionUI.instance.mainWindow.doLayout();
    skin_manager.ProcessAllConditions();

    for (int i = 0; i < oldactions.size(); ++i) {
      JCheckBox checkbox = (JCheckBox)oldactions.get(i);
      SkinManager.instance.triggerAction(checkbox);
    }

    JSplitPane mixout_splitpanel = (JSplitPane)SkinManager.instance.getInstance("mixout_splitpanel", JSplitPane.class, null);
    if (OptionsUI.instance.fullstyleslayout.isSelected()) if ((mixout_splitpanel != null) && (split1 != 0)) mixout_splitpanel.setDividerLocation(split1);
    JSplitPane top_splitpanel = (JSplitPane)SkinManager.instance.getInstance("top_splitpanel", JSplitPane.class, null);
    if (OptionsUI.instance.fullstyleslayout.isSelected()) if ((top_splitpanel != null) && (split2 != 0)) top_splitpanel.setDividerLocation(split2);
    JSplitPane main_splitpanel = (JSplitPane)SkinManager.instance.getInstance("main_splitpanel", JSplitPane.class, null);
    if (OptionsUI.instance.fullstyleslayout.isSelected()) if ((main_splitpanel != null) && (split3 != 0)) main_splitpanel.setDividerLocation(split3);

    skin_manager.instance.Load("skin.dat");
    skin_manager.setdefaults = false;
    RapidEvolutionUI.instance.postSkinInit();
    rapidevolution_ui.PostLoadInit();
    //new AutoUpdateThread().start();

    // temporary
    PitchTimeShiftUI.instance.normalize.setSelected(true);

    LookAndFeelManager.saveCurrentLookAndFeel();

    loaded = true;
    return 1;
  }

  public SkinManager skin_manager = null;
  static public RapidEvolution instance;
  public boolean terminatesignal = false;
  
  private void handleQuitWorker() {
      try {
          log.debug("handleQuit() - Running");
          AudioPlayer.close();
          SkinManager.instance.getFrame("main_frame").setVisible(false);
//          new com.mixshare.rapid_evolution.ui.swing.animation.Dissolver().dissolveExit(SkinManager.instance.getFrame("main_frame"));
          if (SongDB.instance.stylesdirtybit != 0)
              log.debug("handleQuit(): *** Styles dirty bit still set ***");
          AudioPlayer.stopplayingsongs = true;
          MixshareClient.instance.DisconnectFromServer();
          instance.terminatesignal = true;
          MixshareClient.instance.serverupdater.stopthread = true;
          for (int i = 0; i < Math
                  .min(
                          SearchPane.instance.searchcolumnconfig.num_columns,
                          SearchPane.instance.searchtable
                                  .getColumnCount()); ++i) {
              SearchPane.instance.searchcolumnconfig.columntitles[i] = SearchPane.instance.searchtable
                      .getColumnName(i);
              SearchPane.instance.searchcolumnconfig.setIndex(i);
              SearchPane.instance.searchcolumnconfig
                      .setPreferredWidth(i,
                              SearchPane.instance.searchtable
                                      .getColumnModel()
                                      .getColumn(i).getWidth());
          }
          for (int i = 0; i < Math
                  .min(
                          MixoutPane.instance.mixouttable
                                  .getColumnCount(),
                          MixoutPane.instance.mixoutcolumnconfig.num_columns); ++i) {
              MixoutPane.instance.mixoutcolumnconfig.columntitles[i] = MixoutPane.instance.mixouttable
                      .getColumnName(i);
              MixoutPane.instance.mixoutcolumnconfig.setIndex(i);
              MixoutPane.instance.mixoutcolumnconfig
                      .setPreferredWidth(i,
                              MixoutPane.instance.mixouttable
                                      .getColumnModel()
                                      .getColumn(i).getWidth());
          }
          Calendar cal = Calendar.getInstance(TimeZone.getDefault());
          String DATE_FORMAT = "yyyy-MM-dd";
          java.text.SimpleDateFormat sdf =  new java.text.SimpleDateFormat(DATE_FORMAT);
          sdf.setTimeZone(TimeZone.getDefault());
          String backupFilename = "backup" + sdf.format(cal.getTime()) + ".xml";
          File backupFile = new File(OSHelper.getWorkingDirectory() + "/" + backupFilename);
          if (backupFile.exists())
        	  backupFile.delete();
          File currentDb = OSHelper.getFileBackwardsCompatible("music_database.xml");
          if (currentDb.exists())
        	  currentDb.renameTo(backupFile);
          if (instance.Save(null, false))
        	  log.debug("Save successful");
          else
        	  log.error("Save FAILED!");
          if ((MIDIPiano.instance.mididevice != null)
                  && (MIDIPiano.instance.mididevice.isOpen()))
              MIDIPiano.instance.mididevice.close();
          if (MIDIPiano.instance.synth != null)
              MIDIPiano.instance.synth.close();

          //AutoUpdateThread.UpdateVersion(false);

          //      if (instance.timetoupgrade) {
          //        String[] command = new String[3];
          //        command[0] = "java";
          //        command[1] = "-jar";
          //        command[2] = "upgrade.jar";
          //        Process p = Runtime.getRuntime().exec(command);
          //      }
          
          try {
              QTUtil.closeQT();
          } catch (java.lang.Error e) {
          } catch (Exception e) { }
                  
          RootMusicExcludeList.save();
          
          log.debug("handleQuit() - Finished");
          Exit();

      } catch (Exception e) {
          log.error("error closing re2", e);
          new ErrorMessageThread("error closing re2", e.getMessage()).start();
      }      
  }
  
  private boolean handled_quit = false;
  public void handleQuit() {
      try {
          if (handled_quit) return;
          handled_quit = true;
          log.debug("handleQuit() - Start");
            //javax.swing.SwingUtilities.invokeLater(
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleQuitWorker();

                }
            });

        } catch (Exception e) {
            log.error("error closing re2", e);
            handled_quit = false;
            new ErrorMessageThread("error closing re2", e.getMessage()).start();
        }
  }
  
  public static Level original_log_level = null;
  
  public void actionPerformed(ActionEvent e) {
      // quit action
      handleQuit();
  }
  
  public static boolean aaEnabled = false;
  
  // main function...
  public static void main(String args[]) {
    try {        
        PropertyConfigurator.configure("log4j.properties");
    } catch (Exception e) {
        System.out.println("*** could not find log4j.properties! logging will not work ***");
    }
    Toolkit.getDefaultToolkit().setDynamicLayout(true);
    System.setProperty("sun.awt.noerasebackground", "true");    
    String aaProperty = System.getProperty("swing.aatext", "true");
    if ((aaProperty != null) && aaProperty.equalsIgnoreCase("true"))
        aaEnabled = true;
    
    JFrame f = null;
    InfiniteProgressPanel glassPane = null;
    try {        
        
        f = new JFrame("loading re2");
        TransparentBackground bg = new TransparentBackground(f);        
        glassPane = new InfiniteProgressPanel();
        f.setSize(200,200);
        f.setUndecorated(true);
        //f.setBackground(Color.black);
        f.getContentPane( ).add(bg);
        bg.setLayout(new BorderLayout( ));    
        RelativeLayout layout = new RelativeLayout();
        bg.setLayout(layout);
        JLabel loadingLabel = new JLabel("loading");
//        loadingLabel.setForeground(Color.black);
        JLabel reLabel = new JLabel("rapid evolution");
//        reLabel.setForeground(Color.black);
        bg.add("label1", loadingLabel);        
        bg.add("label2", reLabel);        
        layout.addConstraint("label1", AttributeType.HORIZONTAL_CENTER, new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.HORIZONTAL_CENTER, 0));                
        layout.addConstraint("label1", AttributeType.VERTICAL_CENTER, new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.VERTICAL_CENTER, -10));                
        layout.addConstraint("label2", AttributeType.HORIZONTAL_CENTER, new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.HORIZONTAL_CENTER, 0));                
        layout.addConstraint("label2", AttributeType.VERTICAL_CENTER, new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.VERTICAL_CENTER, 5));                
//        f.getContentPane().add(new javax.swing.JLabel("loading rapid evolution"));
        RapidEvolutionUI.CenterComponent(f);
        f.setGlassPane(glassPane);        
        f.setVisible(true);
        glassPane.start();        
        
        original_log_level = Logger.getRoot().getLevel();
        if (args.length > 0) {
        if (args[0].toLowerCase().equals("debug")) {
            Logger.getRoot().setLevel(Level.DEBUG);
        }
                
      }
      File currentdir = new File (".");
//      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
      RapidEvolutionUI.setLookAndFeel();
      instance = new RapidEvolution();
      
            
      MRJAdapter.addQuitApplicationListener(instance);
      
      if (instance.load("music_database.xml") == 0) {
          glassPane.stop();
          f.setVisible(false);          
        Exit();
      }

      // setup dialogs and display the main window
      SearchPane.instance.CreateSearchColumns();
      MixoutPane.instance.CreateMixoutColumns();
      RootsUI.instance.CreateRootsColumns();
      SuggestedMixesUI.instance.CreateSuggestedColumns();
      ExcludeUI.instance.CreateExcludeColumns();
      SyncUI.instance.CreateSyncColumns();
      AddMatchQueryUI.instance.CreateMatchColumns();
      EditMatchQueryUI.instance.CreateMatchColumns2();

//      SkinManager.instance.printTypeMaps();
//      SkinManager.instance.printMessageMaps();
//      SkinManager.instance.printDialogMessageMaps();

      if (OptionsUI.instance.disable_multiple_delect.isSelected()) 
          SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      else SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);            

      MixoutPane.instance.mixouttable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      glassPane.stop();
      f.setVisible(false);
            
      SkinManager.instance.getFrame("main_frame").setVisible(true);
      SkinManager.instance.getFrame("main_frame").requestFocus();
      
      log.debug("Used Memory: " + String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "mb");
      log.debug("Anti-Aliasing: " + aaEnabled);

      /*
      // temp code
      FileWriter outputstream = new FileWriter("d:/temp/collectionInfo.txt");
      BufferedWriter outputbuffer = new BufferedWriter(outputstream);
      SongLinkedList iter = SongDB.instance.SongLL;
      while (iter != null) {
          String id = iter.getArtist() + " - " + iter.getSongname();
          if (!iter.getRemixer().equals(""))
              id += " (" + iter.getRemixer() + ")";                            
          outputbuffer.write(com.chango.data.search.util.SearchEncoder.encodeString(id));
          outputbuffer.newLine();
          outputbuffer.write(String.valueOf(iter.getBeatIntensity()));
          outputbuffer.newLine();
          outputbuffer.write(iter.getFileName());
          outputbuffer.newLine();
          iter = iter.next;
      }      
      outputbuffer.close();
      outputstream.close();
      */
      
      new MyConnectThread().start();
      new AutoSaveThread().start();
      new TagWriteJobBuffer().start();
      if (OptionsUI.instance.autoScanForNewMusic.isSelected())
    	  new RootMusicDirectoryScanner().start();
      
      // wait until the main window is closed then exit
      while (SkinManager.instance.getFrame("main_frame").isVisible() || SkinManager.instance.isChanging()) {
        try {
          Thread.sleep(2000);
          StylesUI.rolloverFixCheck();
        }
        catch (InterruptedException ie) {
            log.error("interrupted exception", ie);
          //Exit();
        }
      }

      instance.handleQuit();
      
    } catch (Exception e) { log.error("main(): error", e); }
    if (glassPane != null)
        glassPane.stop();
    if (f != null)
        f.setVisible(false);    
  }

  public void SaveRoutine(boolean use_pacer) {	  
      Calendar cal = Calendar.getInstance(TimeZone.getDefault());
      String DATE_FORMAT = "yyyy-MM-dd";
      java.text.SimpleDateFormat sdf =  new java.text.SimpleDateFormat(DATE_FORMAT);
      sdf.setTimeZone(TimeZone.getDefault());
      String backupFilename = "backup" + sdf.format(cal.getTime()) + ".xml";
      File backupFile = new File(OSHelper.getWorkingDirectory() + "/" + backupFilename);
      if (backupFile.exists())
    	  backupFile.delete();
      File currentDb = OSHelper.getFileBackwardsCompatible("music_database.xml");
      if (currentDb.exists())
    	  currentDb.renameTo(backupFile);
      Save(null, use_pacer);
      /*
    if (Save(null, use_pacer)) {
      Calendar cal = Calendar.getInstance(TimeZone.getDefault());
      String DATE_FORMAT = "yyyy-MM-dd";
      java.text.SimpleDateFormat sdf =  new java.text.SimpleDateFormat(DATE_FORMAT);
      sdf.setTimeZone(TimeZone.getDefault());
      Save("backup" + sdf.format(cal.getTime()) + ".xml", use_pacer);
    }
    */
  }

  MixshareClient netclient = new MixshareClient();



  public static boolean first_time_to_run = true;
  public int load(String loadfilename) {
    loaded = false;

    skin_manager = new SkinManager();        
    if (!skin_manager.loadSkin()) return 0;
    
    OptionsUI.instance.Init();
    EditStyleUI.instance.showexcludekeywords.setSelected(false);
    OptionsUI.instance.disableonline.setSelected(false);

    OptionsUI.instance.smoothbpmslider.setSelected(false);
    int total_mixes = 0;
    String version = null;
    float versionnum = 0.0f;
    OutputCtr.setLevel(0);
    boolean success = false;
    try {      
      File test_for_xml = OSHelper.getFileBackwardsCompatible(loadfilename);
      if (!test_for_xml.exists()) {
          test_for_xml = OSHelper.getFileBackwardsCompatible("mixguide.dat");
          if (test_for_xml.exists()) loadfilename = "mixguide.dat";
      }
      
      if (loadfilename.toLowerCase().endsWith("xml")) {
          triedtoload.add(loadfilename);    
          versionnum = 3.0f;
          success = Database.loadDatabase(loadfilename);
          if (success) {
              try {
                  File del_old_mixguide = OSHelper.getFileBackwardsCompatible("mixguide.dat");
                  del_old_mixguide.delete();
              } catch (Exception e) { }
          }
      } else {          
      //      FileReader inputstream = new FileReader(programdirectory + "\\" + "mixguide.dat");
      FileReader inputstream = new FileReader(loadfilename);
      triedtoload.add(loadfilename);
      UTF8BufferedReader inputbuffer = new UTF8BufferedReader(inputstream, versionnum);
      version = inputbuffer.readLine();
      versionnum = Float.parseFloat(version);
      if (versionnum < 2.79f) {
        inputbuffer.close();
        inputstream.close();
        return oldload(loadfilename);
      }

      if (version != null) {
        if (versionnum >= 1.4f) {
          // read initial screen position
          String line;         

          first_time_to_run = false;
          SearchPane.bpmrangespinner.getModel().setValue(inputbuffer.readLine());

          RapidEvolution.getMixshareClient().setOutOfSync(StringUtil.isTrue(inputbuffer.readLine()));
          netclient.neverconnectedtoserver = StringUtil.isTrue(inputbuffer.readLine());
          RapidEvolution.getMixshareClient().setIsSyncing(StringUtil.isTrue(inputbuffer.readLine()));
          if (versionnum >= 2.81f) {
            int value = Integer.parseInt(inputbuffer.readLine());
            //OptionsUI.instance.id3writer.setSelectedIndex(value);
          }
          if (versionnum >= 2.83f) {
              int value = Integer.parseInt(inputbuffer.readLine());
              OptionsUI.instance.custom1sortas.setSelectedIndex(value);
              value = Integer.parseInt(inputbuffer.readLine());
              OptionsUI.instance.custom2sortas.setSelectedIndex(value);
              value = Integer.parseInt(inputbuffer.readLine());
              OptionsUI.instance.custom3sortas.setSelectedIndex(value);
              value = Integer.parseInt(inputbuffer.readLine());
              OptionsUI.instance.custom4sortas.setSelectedIndex(value);              
          }
          OptionsUI.instance.keyformatcombo.setSelectedIndex(Integer.parseInt(inputbuffer.readLine()));
          OptionsUI.OSMediaPlayer = inputbuffer.readLine();
          if (versionnum >= 2.82f) {
              RapidEvolutionUI.instance.bpmslider_tickmark_index = Integer.parseInt(inputbuffer.readLine());
          }
          OptionsUI.instance.bpmdetectionquality.setValue(Integer.parseInt(inputbuffer.readLine()));
          if (versionnum >= 2.80f) {
            Bpm.minbpm = Double.parseDouble(inputbuffer.readLine());
            Bpm.maxbpm = Double.parseDouble(inputbuffer.readLine());
          }
          OptionsUI.instance.timepitchshiftquality.setValue(Integer.parseInt(inputbuffer.readLine()));

          OptionsUI.instance.keydetectionquality.setValue(Integer.parseInt(inputbuffer.readLine()));

          Integer octaves = new Integer(Integer.parseInt(inputbuffer.readLine()));
          ((SpinnerNumberModel)MIDIPiano.instance.octavesspinner).setValue(octaves);
          MIDIPiano.instance.setkeyboardshift(octaves.intValue());
          MIDIPiano.instance.num_keys = 12 * octaves.intValue();
          MIDIPiano.instance.keyspressed = new boolean[MIDIPiano.instance.num_keys];
          MIDIPiano.instance.keyspressed2 = new int[MIDIPiano.instance.num_keys];
          MIDIPiano.instance.masterkeyspressed = new int[MIDIPiano.instance.num_keys];
          for (int i = 0; i < MIDIPiano.instance.num_keys; ++i) {
            MIDIPiano.instance.keyspressed[i] = false;
            MIDIPiano.instance.keyspressed2[i] = 0;
            MIDIPiano.instance.masterkeyspressed[i] = 0;
          }

          ((SpinnerNumberModel)MIDIPiano.instance.shiftspinner).setValue(new Integer(Integer.parseInt(inputbuffer.readLine())));
          MIDIPiano.instance.initialcombovalue = -1;
          MIDIPiano.instance.initialmidivalue = inputbuffer.readLine();

          line = inputbuffer.readLine();
          OptionsUI.keychordtype1 = Integer.parseInt(line);
          line = inputbuffer.readLine();
          OptionsUI.keychordtype2 = Integer.parseInt(line);

          OptionsUI.instance.customfieldtext1.setText(inputbuffer.readLine());
          OptionsUI.instance.customfieldtext2.setText(inputbuffer.readLine());
          OptionsUI.instance.customfieldtext3.setText(inputbuffer.readLine());
          OptionsUI.instance.customfieldtext4.setText(inputbuffer.readLine());

          int[] columnindices = new int[Integer.parseInt(inputbuffer.readLine())];
          for (int c = 0; c < columnindices.length; ++c) {
              columnindices[c] = Integer.parseInt(inputbuffer.readLine());
          }
          if (columnindices.length == SearchPane.instance.defaultColumnIndices.length) SearchPane.instance.defaultColumnIndices = columnindices;
          else SearchPane.instance.defaultColumnIndices = SearchPane.instance.determineOrdering(SearchPane.instance.defaultColumnIndices, columnindices, false);

          columnindices = new int[Integer.parseInt(inputbuffer.readLine())];
          for (int c = 0; c < columnindices.length; ++c) {
              columnindices[c] = Integer.parseInt(inputbuffer.readLine());
          }
          if (columnindices.length == MixoutPane.instance.defaultColumnIndices.length) MixoutPane.instance.defaultColumnIndices = columnindices;
          else MixoutPane.instance.defaultColumnIndices = MixoutPane.instance.determineOrdering(MixoutPane.instance.defaultColumnIndices, columnindices, false);

          int totalunique = 39;
          if (versionnum >= 2.73f)
            totalunique = Integer.parseInt(inputbuffer.readLine());

          for (int c = 0; c < totalunique; ++c) {
              SearchPane.instance.searchcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
              MixoutPane.instance.mixoutcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
              ExcludeUI.instance.excludecolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
              RootsUI.instance.rootscolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
              SuggestedMixesUI.instance.suggestedcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
              SyncUI.instance.synccolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
              AddMatchQueryUI.instance.matchcolumnconfig.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
              EditMatchQueryUI.instance.matchcolumnconfig2.setDefaultWidth(c, Integer.parseInt(inputbuffer.readLine()));
          }

        if (versionnum >= 2.0f) {
            line = inputbuffer.readLine();
            SearchPane.instance.searchcolumnconfig.num_columns = Integer.parseInt(line);
            SearchPane.instance.searchcolumnconfig.columnindex = new int[SearchPane.instance.searchcolumnconfig.
                num_columns];
            SearchPane.instance.searchcolumnconfig.columntitles = new String[SearchPane.instance.searchcolumnconfig.
                num_columns];
            SearchPane.instance.searchcolumnconfig.setPreferredWidth(new int[SearchPane.instance.searchcolumnconfig.
                num_columns]);
            for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
              line = inputbuffer.readLine();
              SearchPane.instance.searchcolumnconfig.columnindex[i] = Integer.parseInt(line);
              line = inputbuffer.readLine();
              SearchPane.instance.searchcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
              SearchPane.instance.searchcolumnconfig.columntitles[i] = SearchPane.instance.searchcolumnconfig.
                  getKeyword(
                  SearchPane.instance.searchcolumnconfig.columnindex[i]);

            }
            for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
              int cindex = SearchPane.instance.searchcolumnconfig.columnindex[i];
              OptionsUI.instance.SetSelectedSearchColumn(cindex);
            }
            line = inputbuffer.readLine();
            MixoutPane.instance.mixoutcolumnconfig.num_columns = Integer.parseInt(line);
            MixoutPane.instance.mixoutcolumnconfig.columnindex = new int[MixoutPane.instance.mixoutcolumnconfig.
                num_columns];
            MixoutPane.instance.mixoutcolumnconfig.columntitles = new String[MixoutPane.instance.mixoutcolumnconfig.
                num_columns];
            MixoutPane.instance.mixoutcolumnconfig.setPreferredWidth(new int[MixoutPane.instance.mixoutcolumnconfig.
                num_columns]);
            for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
              line = inputbuffer.readLine();
              MixoutPane.instance.mixoutcolumnconfig.columnindex[i] = Integer.parseInt(line);
              line = inputbuffer.readLine();
              MixoutPane.instance.mixoutcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
              MixoutPane.instance.mixoutcolumnconfig.columntitles[i] = MixoutPane.instance.mixoutcolumnconfig.
                  getKeyword(
                  MixoutPane.instance.mixoutcolumnconfig.columnindex[i]);
            }
            for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
              int cindex = MixoutPane.instance.mixoutcolumnconfig.columnindex[i];
              OptionsUI.instance.SetSelectedMixoutColumn(cindex);
            }
            if (versionnum >= 2.26f) {
              line = inputbuffer.readLine();
              ExcludeUI.instance.excludecolumnconfig.num_columns = Integer.parseInt(line);
              ExcludeUI.instance.excludecolumnconfig.columnindex = new int[ExcludeUI.instance.excludecolumnconfig.num_columns];
              ExcludeUI.instance.excludecolumnconfig.columntitles = new String[ExcludeUI.instance.excludecolumnconfig.num_columns];
              ExcludeUI.instance.excludecolumnconfig.setPreferredWidth(new int[ExcludeUI.instance.excludecolumnconfig.num_columns]);
              for (int i = 0; i < ExcludeUI.instance.excludecolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                ExcludeUI.instance.excludecolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                ExcludeUI.instance.excludecolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                ExcludeUI.instance.excludecolumnconfig.columntitles[i] = ExcludeUI.instance.excludecolumnconfig.
                    getKeyword(
                    ExcludeUI.instance.excludecolumnconfig.columnindex[i]);
              }
              line = inputbuffer.readLine();
              RootsUI.instance.rootscolumnconfig.num_columns = Integer.parseInt(line);
              RootsUI.instance.rootscolumnconfig.columnindex = new int[RootsUI.instance.rootscolumnconfig.num_columns];
              RootsUI.instance.rootscolumnconfig.columntitles = new String[RootsUI.instance.rootscolumnconfig.num_columns];
              RootsUI.instance.rootscolumnconfig.setPreferredWidth(new int[RootsUI.instance.rootscolumnconfig.num_columns]);
              for (int i = 0; i < RootsUI.instance.rootscolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                RootsUI.instance.rootscolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                RootsUI.instance.rootscolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                RootsUI.instance.rootscolumnconfig.columntitles[i] = RootsUI.instance.rootscolumnconfig.
                    getKeyword(
                    RootsUI.instance.rootscolumnconfig.columnindex[i]);
              }
              line = inputbuffer.readLine();
              SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns = Integer.parseInt(line);
              SuggestedMixesUI.instance.suggestedcolumnconfig.columnindex = new int[SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns];
              SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles = new String[SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns];
              SuggestedMixesUI.instance.suggestedcolumnconfig.setPreferredWidth(new int[SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns]);
              for (int i = 0; i < SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                SuggestedMixesUI.instance.suggestedcolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                SuggestedMixesUI.instance.suggestedcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i] = SuggestedMixesUI.instance.suggestedcolumnconfig.
                    getKeyword(
                    SuggestedMixesUI.instance.suggestedcolumnconfig.columnindex[i]);
              }
              line = inputbuffer.readLine();
              SyncUI.instance.synccolumnconfig.num_columns = Integer.parseInt(line);
              SyncUI.instance.synccolumnconfig.columnindex = new int[SyncUI.instance.synccolumnconfig.num_columns];
              SyncUI.instance.synccolumnconfig.columntitles = new String[SyncUI.instance.synccolumnconfig.num_columns];
              SyncUI.instance.synccolumnconfig.setPreferredWidth(new int[SyncUI.instance.synccolumnconfig.num_columns]);
              for (int i = 0; i < SyncUI.instance.synccolumnconfig.num_columns; ++i) {
                line = inputbuffer.readLine();
                SyncUI.instance.synccolumnconfig.columnindex[i] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                SyncUI.instance.synccolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                SyncUI.instance.synccolumnconfig.columntitles[i] = SyncUI.instance.synccolumnconfig.
                    getKeyword(
                    SyncUI.instance.synccolumnconfig.columnindex[i]);
              }

              if (versionnum >= 2.39) {
                line = inputbuffer.readLine();
                AddMatchQueryUI.instance.matchcolumnconfig.num_columns = Integer.parseInt(line);
                AddMatchQueryUI.instance.matchcolumnconfig.columnindex = new int[AddMatchQueryUI.instance.matchcolumnconfig.num_columns];
                AddMatchQueryUI.instance.matchcolumnconfig.columntitles = new String[AddMatchQueryUI.instance.matchcolumnconfig.num_columns];
                AddMatchQueryUI.instance.matchcolumnconfig.setPreferredWidth(new int[AddMatchQueryUI.instance.matchcolumnconfig.num_columns]);
                for (int i = 0; i < AddMatchQueryUI.instance.matchcolumnconfig.num_columns; ++i) {
                  line = inputbuffer.readLine();
                  AddMatchQueryUI.instance.matchcolumnconfig.columnindex[i] = Integer.parseInt(line);
                  line = inputbuffer.readLine();
                  AddMatchQueryUI.instance.matchcolumnconfig.setPreferredWidth(i, Integer.parseInt(line));
                  AddMatchQueryUI.instance.matchcolumnconfig.columntitles[i] = AddMatchQueryUI.instance.matchcolumnconfig.getKeyword(AddMatchQueryUI.instance.matchcolumnconfig.columnindex[i]);
                }
                line = inputbuffer.readLine();
                EditMatchQueryUI.instance.matchcolumnconfig2.num_columns = Integer.parseInt(line);
                EditMatchQueryUI.instance.matchcolumnconfig2.columnindex = new int[EditMatchQueryUI.instance.matchcolumnconfig2.num_columns];
                EditMatchQueryUI.instance.matchcolumnconfig2.columntitles = new String[EditMatchQueryUI.instance.matchcolumnconfig2.num_columns];
                EditMatchQueryUI.instance.matchcolumnconfig2.setPreferredWidth(new int[EditMatchQueryUI.instance.matchcolumnconfig2.num_columns]);
                for (int i = 0; i < EditMatchQueryUI.instance.matchcolumnconfig2.num_columns; ++i) {
                  line = inputbuffer.readLine();
                  EditMatchQueryUI.instance.matchcolumnconfig2.columnindex[i] = Integer.parseInt(line);
                  line = inputbuffer.readLine();
                  EditMatchQueryUI.instance.matchcolumnconfig2.setPreferredWidth(i, Integer.parseInt(line));
                  EditMatchQueryUI.instance.matchcolumnconfig2.columntitles[i] = EditMatchQueryUI.instance.matchcolumnconfig2.getKeyword(EditMatchQueryUI.instance.matchcolumnconfig2.columnindex[i]);
                }
              }
            }
          }

          line = inputbuffer.readLine();
          if (line.charAt(0) == '1')
            OptionsUI.instance.keysearchonly = true;
          else
            OptionsUI.instance.keysearchonly = false;

          rapidevolution_ui.bpmscale = Float.parseFloat(inputbuffer.readLine());
          rapidevolution_ui.bpmscale = 8;

          OptionsUI.instance.username.setText(inputbuffer.readLine());
          OptionsUI.instance.password.setText(inputbuffer.readLine());
          OptionsUI.instance.username.setEnabled(false);
          OptionsUI.instance.emailaddress.setText(inputbuffer.readLine());
          OptionsUI.instance.userwebsite.setText(inputbuffer.readLine());
          line = inputbuffer.readLine();
          SongDB.instance.stylesdirtybit = Integer.parseInt(line);

          // read the styles
          line = inputbuffer.readLine();
          SongDB.instance.num_styles = Integer.parseInt(line);
          StyleLinkedList lastaddedstyle = null;
          int sindex = 0;
          for (int i = 0; i < SongDB.instance.num_styles; ++i) {
            String stylename = inputbuffer.readLine(true);
            if (SongDB.instance.masterstylelist == null) lastaddedstyle = SongDB.instance.masterstylelist = new StyleLinkedList(stylename, null, StyleLinkedList.getNextStyleId());
            else lastaddedstyle = lastaddedstyle.next = new StyleLinkedList(stylename, null, StyleLinkedList.getNextStyleId());
            lastaddedstyle.set_sindex(sindex++);

            int num_keywords = Integer.parseInt(inputbuffer.readLine());
            for (int j = 0; j < num_keywords; ++j) {
              String keyword = inputbuffer.readLine(true);
              lastaddedstyle.insertKeyword(keyword);
            }
            if (versionnum >= 2.05f) {
              int num_excludekeywords = Integer.parseInt(inputbuffer.readLine());
              for (int j = 0; j < num_excludekeywords; ++j) {
                String keyword = inputbuffer.readLine(true);
                lastaddedstyle.insertExcludeKeyword(keyword);
              }
            }
            if (versionnum >= 2.70f) {
              int num_songs = Integer.parseInt(inputbuffer.readLine());
              for (int j = 0; j < num_songs; ++j) {
                long song = Long.parseLong(inputbuffer.readLine());
                lastaddedstyle.insertSong(song);
              }
              int num_excludesongs = Integer.parseInt(inputbuffer.readLine());
              for (int j = 0; j < num_excludesongs; ++j) {
                long song = Long.parseLong(inputbuffer.readLine());
                lastaddedstyle.insertExcludeSong(song);
              }
            }
            
            if (versionnum >= 2.72f) {
              int num_include = Integer.parseInt(inputbuffer.readLine());
              for (int z = 0; z < num_include; ++z) {
                lastaddedstyle.styleincludedisplayvector.add(inputbuffer.readLine(true));
                int type = Integer.parseInt(inputbuffer.readLine());
                if (type == 0) {
                  lastaddedstyle.styleincludevector.add(new Long(Long.parseLong(inputbuffer.readLine())));
                } else {
                  lastaddedstyle.styleincludevector.add(inputbuffer.readLine(true));
                }
              }

              int num_exclude = Integer.parseInt(inputbuffer.readLine());
              for (int z = 0; z < num_exclude; ++z) {
                lastaddedstyle.styleexcludedisplayvector.add(inputbuffer.readLine(true));
                int type = Integer.parseInt(inputbuffer.readLine());
                if (type == 0) {
                  lastaddedstyle.styleexcludevector.add(new Long(Long.parseLong(inputbuffer.readLine())));
                } else {
                  lastaddedstyle.styleexcludevector.add(inputbuffer.readLine(true));
                }
              }

            }
          }
          if (versionnum < 2.72f) SongDB.instance.masterstylelist = SongDB.instance.sortStyleList(SongDB.instance.masterstylelist);
         if (SongDB.instance.stylesortexception) { SongDB.instance.stylesortexception = false; throw new Exception(); }
         if (versionnum >= 2.70f) {
           SongLinkedList.minuniqueid = Integer.parseInt(inputbuffer.readLine());
           line = inputbuffer.readLine();
           if (line.equals("1")) SongLinkedList.checkuniqueid = true;
           else SongLinkedList.checkuniqueid = false;
         }

          SongLinkedList AddPtr = null;
          line = inputbuffer.readLine();
          while ((line != null) && line.equals("-")) {
            ++SongDB.instance.total_songs;
//          ++initialsongsloaded;
            int uniqueid = 0;
            if (versionnum >= 2.70f) uniqueid = Integer.parseInt(inputbuffer.readLine());
            else {
              uniqueid = SongLinkedList.getNextUniqueID();
            }
            String album = inputbuffer.readLine(true);
            String artist = inputbuffer.readLine(true);
            String track = inputbuffer.readLine(true);
            String songname = inputbuffer.readLine(true);
            String remixer = new String("");
            String user1 = new String("");
            String user2 = new String("");
            String user3 = new String("");
            String user4 = new String("");
            if (versionnum >= 2.06f) {
              remixer = inputbuffer.readLine(true);
              user1 = inputbuffer.readLine(true);
              user2 = inputbuffer.readLine(true);
              user3 = inputbuffer.readLine(true);
              user4 = inputbuffer.readLine(true);
            }
            int numcommentlines = 1;
            if (versionnum >= 2.00f)
              numcommentlines = Integer.parseInt(inputbuffer.readLine());
            String comments = new String("");
            for (int cl = 1; cl <= numcommentlines; ++cl) {
              comments += inputbuffer.readLine(true);
              if (cl != numcommentlines)
                comments += "\n";
            }
            String timesig = inputbuffer.readLine();
            line = inputbuffer.readLine();
            boolean vinylbit = false;
            if (line.charAt(0) == '1')
              vinylbit = true;
            line = inputbuffer.readLine();
            boolean nonvinylbit = false;
            if (line.charAt(0) == '1')
              nonvinylbit = true;
            line = inputbuffer.readLine();
            boolean disabled = false;
            if (line.charAt(0) == '1')
              disabled = true;
            boolean servercached = false;
            if (versionnum >= 2.13f) {
              line = inputbuffer.readLine();
              if (line.charAt(0) == '1')
                servercached = true;
            }
            String filename = inputbuffer.readLine(true);
            String startkey = inputbuffer.readLine();
            String endkey = inputbuffer.readLine();
            int key_accuracy = 0;
            if (versionnum >= 2.84f) {
                key_accuracy = Integer.parseInt(inputbuffer.readLine());
            }
            String tracktime = inputbuffer.readLine();
            float startbpm = Float.parseFloat(inputbuffer.readLine());
            float endbpm = Float.parseFloat(inputbuffer.readLine());
            int bpm_accuracy = 0;
            if (versionnum >= 2.84f) {
                bpm_accuracy = Integer.parseInt(inputbuffer.readLine());
            }
            int times_played = 0;
            if (versionnum >= 2.77f) {
              times_played = Integer.parseInt(inputbuffer.readLine());
            }

            java.util.Date dateadded = new java.util.Date();
            if (versionnum >= 2.61f) {
              dateadded = masterdateformat.parse(inputbuffer.readLine());
            }
            String itunes_id = null;
            if (versionnum >= 2.67f) {
              itunes_id = inputbuffer.readLine();
              if (itunes_id.equals("")) itunes_id = null;
            }
                        
            int num_mixouts = Integer.parseInt(inputbuffer.readLine());
            int num_excludes = Integer.parseInt(inputbuffer.readLine());

            try {
              SongDB.instance.addsinglesongsem.acquire();
              if (AddPtr == null)
                AddPtr = SongDB.instance.SongLL = new SongLinkedList(uniqueid, artist, album, track,
                    songname, remixer, comments, vinylbit, nonvinylbit,
                    startbpm, endbpm,
                    num_mixouts, num_excludes, Key.getKey(startkey), Key.getKey(endkey), filename,
                    tracktime, timesig, disabled, servercached, user1, user2,
                    user3, user4, null);
              else
                AddPtr = AddPtr.next = new SongLinkedList(uniqueid, artist, album, track,
                    songname, remixer, comments, vinylbit, nonvinylbit,
                    startbpm, endbpm,
                    num_mixouts, num_excludes, Key.getKey(startkey), Key.getKey(endkey), filename,
                    tracktime, timesig, disabled, servercached, user1, user2,
                    user3, user4, null);

            } catch (Exception e) { }
            SongDB.instance.addsinglesongsem.release();
            AddPtr.setDateAdded(SongLinkedList.getStringDateFormat(dateadded));
            AddPtr.itunes_id = itunes_id;

            AddPtr.setTimesPlayed(times_played);
            AddPtr.setKeyAccuracy(key_accuracy);
            AddPtr.setBpmAccuracy(bpm_accuracy);
            AddPtr.setStyles(new boolean[SongDB.instance.num_styles]);
            if (versionnum >= 2.10f) {
              String stylestring = inputbuffer.readLine();
              for (int z = 0; z < SongDB.instance.num_styles; ++z) {
                if (stylestring.charAt(z) == '1') AddPtr.setStyle(z, true);
                else AddPtr.setStyle(z, false);
              }
            } else {
              for (int z = 0; z < SongDB.instance.num_styles; ++z) AddPtr.setStyle(z, false);
            }
            total_mixes += num_mixouts;
            for (int i = 0; i < num_mixouts; ++i) {
              String mixoutsong = inputbuffer.readLine();
              String mixoutsongname = null;
              float bpmdiff = Float.parseFloat(inputbuffer.readLine());
              int rank = Integer.parseInt(inputbuffer.readLine());
              boolean mixoutcache = false;
              if (versionnum >= 2.14f) {
                String tmp = inputbuffer.readLine();
                if (tmp.charAt(0) == '1') mixoutcache = true;
              }
              numcommentlines = 1;
              if (versionnum >= 2.00f)
                numcommentlines = Integer.parseInt(inputbuffer.readLine());
              String mixcomments = new String("");
              for (int cl = 1; cl <= numcommentlines; ++cl) {
                mixcomments += inputbuffer.readLine(true);
                if (cl != numcommentlines)
                  mixcomments += "\n";
              }
              boolean addon = false;
              line = inputbuffer.readLine();
              if (line.charAt(0) == '1')
                addon = true;
              if (versionnum < 2.44f) mixoutsong = mixoutsong.toLowerCase();
              if (versionnum >= 2.70f) {
                AddPtr.mixout_songs[i] = Long.parseLong(mixoutsong);
              } else AddPtr.setTempMixoutSongname(i, mixoutsong);
              AddPtr._mixout_ranks[i] = rank;
              AddPtr._mixout_comments[i] = mixcomments;
              AddPtr._mixout_bpmdiff[i] =  bpmdiff;
              AddPtr.mixout_servercache[i] = mixoutcache;
              AddPtr._mixout_addons[i] = addon;
            }
            //AddPtr.sort_mixouts();
            AddPtr.calculateSongDisplayIds();
            SongDB.instance.songmap.put(new Long(AddPtr.uniquesongid), AddPtr);
            SongDB.instance.uniquesongstring_songmap.put(AddPtr.getUniqueStringId(), AddPtr);

            for (int i = 0; i < num_excludes; ++i) {
              String excludesong = inputbuffer.readLine();
              String excludesongname = null;
              if (versionnum < 2.44f) excludesong = excludesong.toLowerCase();
              if (versionnum >= 2.70f) {
                AddPtr.exclude_songs[i] = Long.parseLong(excludesong);
              } else AddPtr.setTempExcludeSongname(i, excludesong);
            }
            //AddPtr.sort_excludes();

            if (versionnum >= 2.35f) {
              line = inputbuffer.readLine();
              int scolorver = Integer.parseInt(line);
              if (scolorver == 1) {
                line = inputbuffer.readLine();
                int windowsize = Integer.parseInt(line);
                line = inputbuffer.readLine();
                float samplerate = Float.parseFloat(line);
                line = inputbuffer.readLine();
                double tracktime2 = Double.parseDouble(line);
                SongColor color = new SongColor(windowsize, samplerate, tracktime2, 1);
                for (int e = 0; e < color.num_bands; ++e) {
                line = inputbuffer.readLine();
                color.avgcentroid[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.variancecentroid[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.avgrolloff[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.variancerolloff[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.avgflux[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.varianceflux[e] = Float.parseFloat(line);
                line = inputbuffer.readLine();
                color.avgzerocrossings[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.variancezerocrossing[e] = Integer.parseInt(line);
                line = inputbuffer.readLine();
                color.lowenergy[e] = Float.parseFloat(line);
                }
                line = inputbuffer.readLine();
                color.period0 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude0 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.ratioperiod1 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude1 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.ratioperiod2 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude2 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.ratioperiod3 = Double.parseDouble(line);
                line = inputbuffer.readLine();
                color.amplitude3 = Double.parseDouble(line);
                AddPtr.color = color;
              }
            }
            line = inputbuffer.readLine();
          }
        }
      }
      if (versionnum >= 2.66f) {
        int numartists = Integer.parseInt(inputbuffer.readLine());
        for (int i = 0; i < numartists; ++i) {
          String name = inputbuffer.readLine(true);
          String searchname = inputbuffer.readLine(true);
          int songcount = Integer.parseInt(inputbuffer.readLine());
          Vector songs = new Vector();
          for (int j = 0; j < songcount; ++j) {
            String val = inputbuffer.readLine();
            if (versionnum >= 2.70f) {
              songs.add(new Long(Long.parseLong(val)));
            } else {
              SongLinkedList song = SongDB.instance.OldGetSongPtr(val);
              if (song != null) songs.add(new Long(song.uniquesongid));
            }
          }
          boolean cached = false;
          if (inputbuffer.readLine().equals("1")) cached = true;
          int numsearchstyles = Integer.parseInt(inputbuffer.readLine());
          Vector searchstyles = new Vector();
          Vector searchcount = new Vector();
          for (int j = 0; j < numsearchstyles; ++j) {
            searchstyles.add(inputbuffer.readLine(true));
            searchcount.add(new Integer(Integer.parseInt(inputbuffer.readLine())));
          }
          boolean stylescached = false;
          if (inputbuffer.readLine().equals("1")) stylescached = true;
          boolean profilecached = false;
          if (inputbuffer.readLine().equals("1")) profilecached = true;
          int numprofiles = Integer.parseInt(inputbuffer.readLine());
          Vector styles = new Vector();
          Vector weights = new Vector();
          for (int j = 0; j < numprofiles; ++j) {
            styles.add(inputbuffer.readLine(true));
            weights.add(new Double(Double.parseDouble(inputbuffer.readLine())));
          }
        }
      }
      inputbuffer.close();
      inputstream.close();
      success = true;
      }
    } catch (FileNotFoundException fnfe) { }
    catch (IOException jio) { }
    catch (Exception e) {
        log.error("load(): error", e);
    }

    if (!success) {
        log.error("Load failed! Finding most recent backup...");
        String backupfilename = new String("");
        File dir = new File(".");
        File files[] = new File[dir.listFiles().length];
        files = dir.listFiles();
        files = FileUtil.sortfiles(files);
        for (int i = 0; i < files.length; ++i) {
          if (!files[i].isDirectory()) {
            if (files[i].getName().toLowerCase().endsWith(".dat") ||
                files[i].getName().toLowerCase().endsWith(".xml")) {
              if (files[i].getName().toLowerCase().startsWith("backup")) {
                boolean found = false;
                for (int z = 0; z < triedtoload.size(); ++z) {
                  String s = (String)triedtoload.get(z);
                  if (s.equals(files[i].getName())) found = true;
                }
                if (!found) backupfilename = files[i].getName();
              }
            }
          }
        }
        if (!backupfilename.equals("")) {
          String messagetext = SkinManager.instance.getDialogMessageText("error_loading_database");
          messagetext = StringUtil.ReplaceString("%filename%", messagetext, loadfilename);
          messagetext = StringUtil.ReplaceString("%backupfilename%", messagetext, backupfilename);
          int n = IOptionPane.showConfirmDialog(
              SkinManager.instance.getFrame("main_frame"),
              messagetext,
              SkinManager.instance.getDialogMessageTitle("error_loading_database"),
              IOptionPane.YES_NO_OPTION);
          RapidEvolution.getMixshareClient().setOutOfSync(true);
          if (n == 0) {
              init();              
            return load(backupfilename);
          }            
          else return 0;
        } else return 0;        
    }
    
    OptionsUI.instance.searchcolumn_user1.setText(OptionsUI.instance.customfieldtext1.getText());
    OptionsUI.instance.searchcolumn_user2.setText(OptionsUI.instance.customfieldtext2.getText());
    OptionsUI.instance.searchcolumn_user3.setText(OptionsUI.instance.customfieldtext3.getText());
    OptionsUI.instance.searchcolumn_user4.setText(OptionsUI.instance.customfieldtext4.getText());
    OptionsUI.instance.mixoutcolumn_user1.setText(OptionsUI.instance.customfieldtext1.getText());
    OptionsUI.instance.mixoutcolumn_user2.setText(OptionsUI.instance.customfieldtext2.getText());
    OptionsUI.instance.mixoutcolumn_user3.setText(OptionsUI.instance.customfieldtext3.getText());
    OptionsUI.instance.mixoutcolumn_user4.setText(OptionsUI.instance.customfieldtext4.getText());
    OptionsUI.instance.oldcustomfield1 = OptionsUI.instance.customfieldtext1.getText();
    OptionsUI.instance.oldcustomfield2 = OptionsUI.instance.customfieldtext2.getText();
    OptionsUI.instance.oldcustomfield3 = OptionsUI.instance.customfieldtext3.getText();
    OptionsUI.instance.oldcustomfield4 = OptionsUI.instance.customfieldtext4.getText();
    SearchPane.asearchlistmouse.setuser1.setText(OptionsUI.instance.customfieldtext1.getText());
    SearchPane.asearchlistmouse.setuser2.setText(OptionsUI.instance.customfieldtext2.getText());
    SearchPane.asearchlistmouse.setuser3.setText(OptionsUI.instance.customfieldtext3.getText());
    SearchPane.asearchlistmouse.setuser4.setText(OptionsUI.instance.customfieldtext4.getText());
    AddSongsUI.instance.addcustomfieldlabel1.setText(OptionsUI.instance.customfieldtext1.getText() + ":");
    AddSongsUI.instance.addcustomfieldlabel2.setText(OptionsUI.instance.customfieldtext2.getText() + ":");
    AddSongsUI.instance.addcustomfieldlabel3.setText(OptionsUI.instance.customfieldtext3.getText() + ":");
    AddSongsUI.instance.addcustomfieldlabel4.setText(OptionsUI.instance.customfieldtext4.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel1.setText(OptionsUI.instance.customfieldtext1.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel2.setText(OptionsUI.instance.customfieldtext2.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel3.setText(OptionsUI.instance.customfieldtext3.getText() + ":");
    EditSongUI.instance.editcustomfieldlabel4.setText(OptionsUI.instance.customfieldtext4.getText() + ":");

//    OptionsUI.instance.searchexcludeyeskeylock.setSelected(false);
    if (OptionsUI.instance.disablekeylockfunctionality.isSelected()) {
      rapidevolution_ui.keylockcurrentsong.setSelected(false);
      rapidevolution_ui.keylockcurrentsong.setEnabled(false);
    }

    if (versionnum < 2.55f) {
      SongDB.instance.SortSongLL();
    }

    if (versionnum < 2.56f) {               
        StyleLinkedList siter = SongDB.instance.masterstylelist;
        while (siter != null) {
            String[] keywords = siter.getKeywords();
          for (int i = 0; i < keywords.length; ++i) {
            String klc = keywords[i].toLowerCase();
            if (!klc.equals(keywords[i])) {
              if (SongDB.instance.OldGetSongPtr(klc) != null) {
                  siter.keywords.remove(keywords[i]);
                  siter.keywords.put(klc, null);
              }
            }
          }
          String[] excludekeywords = siter.getExcludeKeywords();
          for (int i = 0; i < excludekeywords.length; ++i) {
            String klc = excludekeywords[i].toLowerCase();          
            if (!klc.equals(excludekeywords[i])) {
              if (SongDB.instance.OldGetSongPtr(klc) != null) {
                  siter.excludekeywords.remove(excludekeywords[i]);
                  siter.excludekeywords.put(klc, null);
              }
            }
          }
          siter = siter.next;
        }        
    }

    if (versionnum < 2.70f) {
        
        SongLinkedList siter = SongDB.instance.SongLL;
        while (siter != null) {
          for (int i = 0; i < siter.getNumMixoutSongs(); ++i) {
            SongLinkedList mixsong = SongDB.instance.OldGetSongPtr(siter.getTempMixoutSongname(i));
            siter.mixout_songs[i] = mixsong.uniquesongid;
          }
          for (int i = 0; i < siter.getNumExcludeSongs(); ++i) {
            SongLinkedList excludesong = SongDB.instance.OldGetSongPtr(siter.getTempExcludeSongname(i));
            siter.exclude_songs[i] = excludesong.uniquesongid;
          }
          siter = siter.next;
        }
        StyleLinkedList iter = SongDB.instance.masterstylelist;
        while (iter != null) {
          HashMap keywords = new HashMap();
          HashMap songs = new HashMap();
          HashMap excludekeywords = new HashMap();
          HashMap excludesongs = new HashMap();
          String[] ikeywords = iter.getKeywords();
          for (int i = 0; i < ikeywords.length; ++i) {
            SongLinkedList test = SongDB.instance.OldGetSongPtr(ikeywords[i]);
            if (test != null) songs.put(new Long(test.uniquesongid), null);
            else keywords.put(ikeywords[i], null);
          }
          String[] ekeywords = iter.getExcludeKeywords();
          for (int i = 0; i < ekeywords.length; ++i) {
            SongLinkedList test = SongDB.instance.OldGetSongPtr(ekeywords[i]);
            if (test != null) excludesongs.put(new Long(test.uniquesongid), null);
            else excludekeywords.put(ekeywords[i], null);
          }
          iter.setKeywords(keywords);
          iter.setSongs(songs);
          iter.setExcludeKeywords(excludekeywords);
          iter.setExcludeSongs(excludesongs);
          iter = iter.next;
        }
        
    }

    if (versionnum < 2.72f) {
        StyleLinkedList siter = SongDB.instance.masterstylelist;
        while (siter != null) {
            String[] keywords = siter.getKeywords();
          for (int j = 0; j < keywords.length; ++j) {
            String keyword = keywords[j];
            String sortkeyword = keyword.toLowerCase();
            keyword = new String("<" + keyword + ">");
            boolean inserted = false;
            int z = 0;
            while ((z < siter.styleincludedisplayvector.size()) && !inserted) {
              String cmpstr = ((String)siter.styleincludedisplayvector.get(z)).toLowerCase();
              if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
              if (sortkeyword.compareTo(cmpstr) < 0) {
                inserted = true;
                siter.styleincludevector.insertElementAt(keywords[j], z);
                siter.styleincludedisplayvector.insertElementAt(keyword, z);
              }
              ++z;
            }
            if (!inserted) {
              siter.styleincludevector.add(keywords[j]);
              siter.styleincludedisplayvector.add(keyword);
            }
          }

          long[] songs = siter.getSongs();
          for (int j = 0; j < songs.length; ++j) {
            SongLinkedList sll = SongDB.instance.NewGetSongPtr(songs[j]);
            String sortkeyword = sll.getSongIdShort();
            String keyword = sll.getSongIdShort();
            boolean inserted = false;
            int z = 0;
            while ((z < siter.styleincludedisplayvector.size()) && !inserted) {
              String cmpstr = ((String)siter.styleincludedisplayvector.get(z));
              if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
              if (sortkeyword.compareToIgnoreCase(cmpstr) < 0) {
                inserted = true;
                siter.styleincludevector.insertElementAt(new Long(songs[j]), z);
                siter.styleincludedisplayvector.insertElementAt(keyword, z);
              }
              ++z;
            }
            if (!inserted) {
              siter.styleincludevector.add(new Long(songs[j]));
              siter.styleincludedisplayvector.add(keyword);
            }
          }

          String[] excludekeywords = siter.getExcludeKeywords();
          for (int i = 0; i < excludekeywords.length; ++i) {
            String keyword = excludekeywords[i];
            String sortkeyword = keyword.toLowerCase();
            keyword = new String("<" + keyword + ">");
            boolean inserted = false;
            int j = 0;
            while ((j < siter.styleexcludedisplayvector.size()) && !inserted) {
              String cmpstr = ((String)siter.styleexcludedisplayvector.get(j)).toLowerCase();
              if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
              if (sortkeyword.compareTo(cmpstr) < 0) {
                inserted = true;
                siter.styleexcludevector.insertElementAt(excludekeywords[i], j);
                siter.styleexcludedisplayvector.insertElementAt(keyword, j);
              }
              ++j;
            }
            if (!inserted) {
              siter.styleexcludevector.add(excludekeywords[i]);
              siter.styleexcludedisplayvector.add(keyword);
            }
          }

          long[] excludesongs = siter.getExcludeSongs();
          for (int i = 0; i < excludesongs.length; ++i) {
            SongLinkedList sll = SongDB.instance.NewGetSongPtr(excludesongs[i]);
            String keyword = sll.getSongIdShort();
            boolean inserted = false;
            int j = 0;
            while ((j < siter.styleexcludedisplayvector.size()) && !inserted) {
              String cmpstr = ((String)siter.styleexcludedisplayvector.get(j));
              if (cmpstr.startsWith("<") && cmpstr.endsWith(">")) cmpstr = cmpstr.substring(1, cmpstr.length() - 1);
              if (keyword.compareToIgnoreCase(cmpstr) < 0) {
                inserted = true;
                siter.styleexcludevector.insertElementAt(new Long(excludesongs[i]), j);
                siter.styleexcludedisplayvector.insertElementAt(keyword, j);
              }
              ++j;
            }
            if (!inserted) {
              siter.styleexcludevector.add(new Long(excludesongs[i]));
              siter.styleexcludedisplayvector.add(keyword);
            }
          }

          siter = siter.next;
        }
    }

    if (versionnum < 3.0f) {
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
            Artist.InsertToArtistList(iter);
            iter = iter.next;
        }
    }


    SearchPane.searchdisplaylist = new SongList();
    SongLinkedList siter = SongDB.instance.SongLL;
    SongList prev = null;
    while (siter != null) {
      prev = SearchPane.searchdisplaylist.insert(siter, prev);
      if (versionnum < 2.24) {
        for (int i = 0; i < siter.getNumMixoutSongs(); ++i) {
          SongLinkedList miter = SongDB.instance.NewGetSongPtr(siter.mixout_songs[i]);
          if ((miter == null) || (miter == siter)) {
              log.debug("NULL mixout found!");
            siter.removeMixOut(i--);
          }
        }
        for (int i = 0; i < siter.getNumExcludeSongs(); ++i) {
          SongLinkedList miter = SongDB.instance.NewGetSongPtr(siter.exclude_songs[i]);
          if ((miter == null) || (miter == siter)) {
              log.debug("NULL exclude found!");
            siter.removeExclude(i--);
          }
        }
      }
      siter = siter.next;
    }
    if ((version != null) && (version.matches("2.15") || version.matches("2.14") || version.matches("2.13") || version.matches("2.12") || version.matches("2.11") || version.matches("2.10") || version.matches("2.09") || version.matches("2.08") || version.matches("2.07"))) SongDB.instance.stylesdirtybit = 1;
    if ((SongDB.instance.stylesdirtybit != 0) || (versionnum < 2.57f)) {
//      stylesdirtybit = 0;
      new UpdateStyles().start();
    }

    if (SearchPane.searchdisplaylist.song == null) SearchPane.searchdisplaylist = null;

    //KeyDetector.CreateMatrix(44100);

//        RapidEvolutionUI.instance.mainWindow.setVisible(true);
//    RapidEvolutionUI.instance.mainWindow.doLayout();
    skin_manager.ProcessAllConditions();
    
    // for some complicated reason i don't want to figure out now, must fire events for non-default valued checkboxes here
    // so skins appear right    
    RapidEvolutionUI.CenterComponent(SkinManager.instance.getFrame("main_frame"));    
    SkinManager.instance.ProcessCondition("options_full_styles_layout_checkbox");    
    SkinManager.instance.ProcessCondition("options_display_styles_on_left");        
    SkinManager.instance.ProcessCondition("options_mixout_hide_details_checkbox");
    
    skin_manager.instance.Load("skin.dat");
    skin_manager.setdefaults = false;
    
    RapidEvolutionUI.instance.postSkinInit();
    rapidevolution_ui.PostLoadInit();
    //new AutoUpdateThread().start();

    // temporary
    PitchTimeShiftUI.instance.normalize.setSelected(true);

    if (OptionsUI.instance.disablekeylockfunctionality.isSelected()) RapidEvolutionUI.instance.keylockcurrentsong.setSelected(false);
    
    loaded = true;
        
    return 1;
  }

  public static void Exit() {
    instance.terminatesignal = true;
    System.exit(1);
  }

  public float currentbpm;
  private float actualbpm;

  public float getCurrentBpm() { return currentbpm; }
  public void setCurrentBpm(float bpm) { currentbpm = bpm; }

  public float getActualBpm() { return actualbpm; }
  public void setActualBpm(float bpm) {
      boolean changed = false;
      if (bpm != actualbpm) changed = true;
    actualbpm = bpm;    
    if (changed) {
        if (OptionsUI.instance.option_bpm_blink_rate.isSelected()) {
            float milliseconds_per_beat = (1.0f / bpm) * 60000.0f;
            SkinManager.instance.setBlinkRate((int)milliseconds_per_beat);
        }
        SongLinkedList.resetColorCache();
    }
  }
}
