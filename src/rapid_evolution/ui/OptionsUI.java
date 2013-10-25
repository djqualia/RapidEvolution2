package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import rapid_evolution.OldSongValues;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import rapid_evolution.FileUtil;
import rapid_evolution.Filter;
import rapid_evolution.ImageIconFactory;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.comparables.myImageIcon;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.net.RestoreFromServerThread;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.ui.main.MixListMouse;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchListMouse;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.styles.ListStyleSongMouse;
import rapid_evolution.ui.styles.StyleExcludeMouse;
import rapid_evolution.ui.styles.StyleIncludeMouse;

import rapid_evolution.ui.MySliderLabel;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.library.RootMusicDirectoryScanner;
import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.music.KeyCode;
import com.mixshare.rapid_evolution.qt.QTUtil;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.colorchooser.ColorPicker;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.lookfeel.LookAndFeelManager;
import com.mixshare.rapid_evolution.ui.swing.slider.CPUMediaSlider;
import com.mixshare.rapid_evolution.ui.swing.slider.MediaSlider;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import org.jvnet.substance.SubstanceLookAndFeel;

import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.textfield.REPasswordField;

public class OptionsUI extends REDialog implements ActionListener {
    private static Logger log = Logger.getLogger(OptionsUI.class);

    public OptionsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
        keyformat_ui = new KeyFormatUI("key_format_dialog");
    }

   
    public static OptionsUI instance = null;
//    public JCheckBox detectallchannels = new JCheckBox("detect from all audio channels");;
    public JCheckBox autobpmkeysearch = new RECheckBox();
    public JCheckBox show_advanced_key_information = new RECheckBox();
    public JCheckBox disable_multiple_delect = new RECheckBox();
    public JButton optionsokbutton = new REButton();
    public REComboBox colorselectionscombo = new REComboBox();;
    public JColorChooser colorchooser = new JColorChooser();;
    public JTextField customfieldtext1 = new  RETextField();
    public JTextField customfieldtext2 = new  RETextField();
    public JTextField customfieldtext3 = new  RETextField();
    public JTextField customfieldtext4 = new  RETextField();
    public JCheckBox searchcolumn_songid = new RECheckBox();
    public JCheckBox searchcolumn_shortid = new RECheckBox();
    public JCheckBox searchcolumn_shortidwinfo = new RECheckBox();
    public JCheckBox searchcolumn_artist = new RECheckBox();
    public JCheckBox searchcolumn_album = new RECheckBox();
    public JCheckBox searchcolumn_track = new RECheckBox();
    public JCheckBox searchcolumn_title = new RECheckBox();
    public JCheckBox searchcolumn_bpmdiff = new RECheckBox();
    public JCheckBox disableserver = new RECheckBox();
    public JCheckBox searchcolumn_stylesimilarity = new RECheckBox();
    public JCheckBox searchcolumn_artistsimilarity = new RECheckBox();
    public JCheckBox searchcolumn_colorsimilarity = new RECheckBox();
    public JCheckBox searchcolumn_timesplayed = new RECheckBox();
    public JCheckBox searchcolumn_replaygain = new RECheckBox();
    public JCheckBox searchcolumn_relbpmdiff = new RECheckBox();
    public JCheckBox searchcolumn_keylock = new RECheckBox();
    public JCheckBox option_bpm_blink_rate = new RECheckBox();
    public JCheckBox searchcolumn_keymode = new RECheckBox();
    public JCheckBox searchcolumn_keycode = new RECheckBox();
    public JCheckBox searchcolumn_actualkey = new RECheckBox();
    public JCheckBox searchcolumn_actualkeycode = new RECheckBox();
    public JCheckBox searchcolumn_pitchshift = new RECheckBox();
    public JCheckBox searchcolumn_length = new RECheckBox();
    public JCheckBox searchcolumn_remixer = new RECheckBox();
    public JCheckBox searchcolumn_user1 = new RECheckBox(customfieldtext1.getText());
    public JCheckBox searchcolumn_user2 = new RECheckBox(customfieldtext2.getText());
    public JCheckBox searchcolumn_user3 = new RECheckBox(customfieldtext3.getText());
    public JCheckBox searchcolumn_user4 = new RECheckBox(customfieldtext4.getText());
    public JCheckBox searchcolumn_dateadded = new RECheckBox();
    public JCheckBox searchcolumn_lastmodified = new RECheckBox();
    public JCheckBox searchcolumn_timesig = new RECheckBox();
    public JCheckBox searchcolumn_bpm = new RECheckBox();
    public JCheckBox searchcolumn_startbpm = new RECheckBox();
    public JCheckBox searchcolumn_endbpm = new RECheckBox();
    public JCheckBox searchcolumn_key = new RECheckBox();
    public JCheckBox searchcolumn_startkey = new RECheckBox();
    public JCheckBox searchcolumn_endkey = new RECheckBox();
    public JCheckBox searchcolumn_comments = new RECheckBox();
    public JCheckBox searchcolumn_styles = new RECheckBox();
    public JCheckBox searchcolumn_filename = new RECheckBox();
    public JCheckBox searchcolumn_hasalbumcover = new RECheckBox();
    public JCheckBox searchcolumn_vinyl = new RECheckBox();
    public JCheckBox searchcolumn_nonvinyl = new RECheckBox();
    public JCheckBox searchcolumn_disabled = new RECheckBox();
    public JCheckBox searchcolumn_nummixouts = new RECheckBox();
    public JCheckBox searchcolumn_numaddons = new RECheckBox();
    public JCheckBox searchcolumn_songidwinfo = new RECheckBox();
    public JCheckBox searchcolumn_keyaccuracy = new RECheckBox();
    public JCheckBox searchcolumn_bpmaccuracy = new RECheckBox();
    public JCheckBox searchcolumn_beatintensity = new RECheckBox();
    public JCheckBox searchcolumn_rating = new RECheckBox();
    public JCheckBox searchcolumn_albumcover = new RECheckBox();
    public JCheckBox mixoutcolumn_songid = new RECheckBox();
    public JCheckBox mixoutcolumn_artist = new RECheckBox();
    public JCheckBox mixoutcolumn_album = new RECheckBox();
    public JCheckBox mixoutcolumn_track = new RECheckBox();
    public JCheckBox mixoutcolumn_title = new RECheckBox();
    public JCheckBox mixoutcolumn_remixer = new RECheckBox();
    public JCheckBox mixoutcolumn_stylesimilarity = new RECheckBox();
    public JCheckBox mixoutcolumn_artistsimilarity = new RECheckBox();
    public JCheckBox mixoutcolumn_colorsimilarity = new RECheckBox();
    public JCheckBox mixoutcolumn_timesplayed = new RECheckBox();
    public JCheckBox mixoutcolumn_replaygain = new RECheckBox();
    public JCheckBox mixoutcolumn_user1 = new JCheckBox(customfieldtext1.getText());
    public JCheckBox mixoutcolumn_user2 = new JCheckBox(customfieldtext2.getText());
    public JCheckBox mixoutcolumn_user3 = new JCheckBox(customfieldtext3.getText());
    public JCheckBox mixoutcolumn_user4 = new JCheckBox(customfieldtext4.getText());
    public JCheckBox mixoutcolumn_dateadded = new RECheckBox();
    public JCheckBox mixoutcolumn_lastmodified = new RECheckBox();
    public JCheckBox mixoutcolumn_bpmdiff = new RECheckBox();
    public JCheckBox mixoutcolumn_relbpmdiff = new RECheckBox();
    public JCheckBox mixoutcolumn_keylock = new RECheckBox();
    public JCheckBox mixoutcolumn_keymode = new RECheckBox();
    public JCheckBox mixoutcolumn_keycode = new RECheckBox();
    public JCheckBox mixoutcolumn_actualkey = new RECheckBox();
    public JCheckBox mixoutcolumn_actualkeycode = new RECheckBox();
    public JCheckBox mixoutcolumn_pitchshift = new RECheckBox();
    public JCheckBox mixoutcolumn_length = new RECheckBox();
    public JCheckBox mixoutcolumn_timesig = new RECheckBox();
    public JCheckBox mixoutcolumn_bpm = new RECheckBox();
    public JCheckBox mixoutcolumn_startbpm = new RECheckBox();
    public JCheckBox mixoutcolumn_endbpm = new RECheckBox();
    public JCheckBox mixoutcolumn_key = new RECheckBox();
    public JCheckBox mixoutcolumn_startkey = new RECheckBox();
    public JCheckBox mixoutcolumn_endkey = new RECheckBox();
    public JCheckBox mixoutcolumn_comments = new RECheckBox();
    public JCheckBox mixoutcolumn_styles = new RECheckBox();
    public JCheckBox mixoutcolumn_hasalbumcover = new RECheckBox();
    public JCheckBox mixoutcolumn_filename = new RECheckBox();
    public JCheckBox mixoutcolumn_vinyl = new RECheckBox();
    public JCheckBox mixoutcolumn_nonvinyl = new RECheckBox();
    public JCheckBox mixoutcolumn_disabled = new RECheckBox();
    public JCheckBox mixoutcolumn_nummixouts = new RECheckBox();
    public JCheckBox mixoutcolumn_numaddons = new RECheckBox();
    public JCheckBox mixoutcolumn_songidwinfo = new RECheckBox();
    public JCheckBox mixoutcolumn_shortid = new RECheckBox();
    public JCheckBox mixoutcolumn_shortidwinfo = new RECheckBox();
    public JCheckBox mixoutcolumn_rank = new RECheckBox();
    public JCheckBox mixoutcolumn_isaddon = new RECheckBox();
    public JCheckBox mixoutcolumn_mixcomments = new RECheckBox();
    public JCheckBox mixoutcolumn_keyaccuracy = new RECheckBox();
    public JCheckBox mixoutcolumn_bpmaccuracy = new RECheckBox();
    public JCheckBox mixoutcolumn_beatintensity = new RECheckBox();
    public JCheckBox mixoutcolumn_rating = new RECheckBox();
    public JCheckBox mixoutcolumn_albumcover = new RECheckBox();
    public JCheckBox autoScanForNewMusic = new RECheckBox();
    public JTextField skin_title_field = new RETextField();
    public JTextField skin_author_field = new RETextField();
    public JTextField skin_locale_field = new RETextField();
    public JTextArea skin_description_field = new RETextArea();
    public JButton skin_reset_button = new REButton();
    public JSortTable available_skins_table = new RETable();
    public JButton load_skin_button = new REButton();
    public JButton refresh_available_skins_button = new REButton();
    public JButton applysearchcolumns = new REButton();
    public JButton applymixoutcolumns = new REButton();
    public JCheckBox searchtableautoscroll = new RECheckBox();
    public JCheckBox searchinplaceediting = new RECheckBox();
    public JCheckBox mixouttableautoscroll = new RECheckBox();
    public JCheckBox mixoutinplaceediting = new RECheckBox();
    public JCheckBox mixouttablehide = new RECheckBox();
    public JCheckBox donotshowdisabledosngs = new RECheckBox();
    public JCheckBox automaticallyquerywhenadding = new RECheckBox();
    public JCheckBox automaticsearchonuserinput = new RECheckBox();
    public JCheckBox donotsharemixcomments = new RECheckBox();
    public JCheckBox donotsharemixouts = new RECheckBox();
    public JCheckBox overwritewhenquerying = new RECheckBox();
    public JCheckBox donotquerycomments = new RECheckBox();
    public JCheckBox strictsearch = new RECheckBox();
    public JCheckBox donotquerystyles = new RECheckBox();
    public JCheckBox disableautotagreading = new RECheckBox();
    public JCheckBox tagautoupdate = new RECheckBox();
    public JCheckBox prefer_id3_v23 = new RECheckBox();
    public JSlider keydetectionquality = new MediaSlider(JSlider.HORIZONTAL, 10, 100, 10, false);
    public JSlider bpmdetectionquality = new MediaSlider(JSlider.HORIZONTAL, 0, 10, 0, false);
    public JSlider timepitchshiftquality = new MediaSlider(JSlider.HORIZONTAL, 0, 28, 0, false);
    public JSlider cpuutilization = new CPUMediaSlider();
    public static REComboBox lookandfeelcombo;
    public JCheckBox fullstyleslayout = new RECheckBox();
    public JCheckBox alwaysontop = new RECheckBox();
    public JCheckBox displaystylesonleft = new RECheckBox();
    public JCheckBox disableautosave = new RECheckBox();
    public JCheckBox showprevioustrack = new RECheckBox();
    public REComboBox keyformatcombo = new REComboBox();    
    JLabel keyformatlabel = new RELabel();
    public JCheckBox excludesongsondonottrylist = new RECheckBox();
    public JCheckBox preventrepeats = new RECheckBox();
    public JCheckBox disablekeylockfunctionality = new RECheckBox();
    public JCheckBox useosplayer = new RECheckBox();
    public JButton browsemediaplayer = new REButton();
    public JButton browsemusicdirectory = new REButton();
    public JButton scanNowButton = new REButton();
    public JButton resetmediaplayer = new REButton();
    public static String OSMediaPlayer = null;
    public JCheckBox smoothbpmslider = new RECheckBox();
    public JCheckBox autohighlightstyles = new RECheckBox();
    public JCheckBox useselectedstylesimilarity = new RECheckBox();
    public JCheckBox disabletooltips = new RECheckBox();
    public JButton defaultsearchcolumns = new REButton();
    public JButton defaultmixoutcolumns = new REButton();
    public JCheckBox disableonline = new RECheckBox();
    public JCheckBox findallpreventrepeats = new RECheckBox();
    public JCheckBox searchwithinstyles = new RECheckBox();
    public JCheckBox evenbpmmultiples = new RECheckBox();
    public boolean keysearchonly; // can be removed
    public JCheckBox ignoremajorminor = new RECheckBox();
    public JCheckBox clearsearchautomatically = new RECheckBox();
    public JCheckBox excludesongsonsamerecord = new RECheckBox();
    public JCheckBox searchexcludenokeylock = new RECheckBox();
    public JCheckBox lockpitchshift = new RECheckBox();
    public static REComboBox mididevicescombo;
    public REComboBox row1chordtype = new REComboBox(MIDIPiano.chords);
    public REComboBox row2chordtype = new REComboBox(MIDIPiano.chords);
    public JCheckBox songdisplayartist = new RECheckBox();
    public JCheckBox songdisplayalbum = new RECheckBox();
    public JCheckBox songdisplaytrack = new RECheckBox();
    public JCheckBox songdisplaysongname = new RECheckBox();
    public JCheckBox songdisplayremixer = new RECheckBox();
    public JCheckBox songdisplaystartbpm = new RECheckBox();
    public JCheckBox songdisplayendbpm = new RECheckBox();
    public JCheckBox songdisplaystartkey = new RECheckBox();
    public JCheckBox songdisplayendkey = new RECheckBox();
    public JCheckBox songdisplaytracktime = new RECheckBox();
    public JCheckBox songdisplaytimesig = new RECheckBox();
    public JCheckBox songdisplayfield1 = new RECheckBox();
    public JCheckBox songdisplayfield2 = new RECheckBox();
    public JCheckBox songdisplayfield3 = new RECheckBox();
    public JCheckBox songdisplayfield4 = new RECheckBox();
    public JCheckBox donotclearuserfield1whenadding = new RECheckBox();
    public JCheckBox donotclearuserfield2whenadding = new RECheckBox();
    public JCheckBox donotclearuserfield3whenadding = new RECheckBox();
    public JCheckBox donotclearuserfield4whenadding = new RECheckBox();
    public REComboBox custom1sortas = new REComboBox();
    public REComboBox custom2sortas = new REComboBox();
    public REComboBox custom3sortas = new REComboBox();
    public REComboBox custom4sortas = new REComboBox();
    public JCheckBox addsongalbumstyle = new RECheckBox();
    public JCheckBox usefilenameastag = new RECheckBox();
    public JCheckBox writekeytotitle = new RECheckBox();
    public JCheckBox writekeytocomments = new RECheckBox();
    public JCheckBox writekeycodes = new  RECheckBox();
    public JCheckBox writeremixertotitle = new RECheckBox();
    public JCheckBox tagswriteempty = new RECheckBox();
    public JCheckBox tagswritebpmdecimals = new RECheckBox();
    public JCheckBox tagwriteartist = new  RECheckBox();
    public JCheckBox tagwritealbum = new  RECheckBox();
    public JCheckBox tagwritetrack = new  RECheckBox();
    public JCheckBox tagwritekeytogroupingtag = new  RECheckBox();
    public JCheckBox tagwritesongname = new  RECheckBox();
    public JCheckBox tagwriteremixer = new  RECheckBox();
    public JCheckBox tagwriterga = new  RECheckBox();
    public JCheckBox tagwritebpm = new  RECheckBox();
    public JCheckBox tagwritekey = new  RECheckBox();
    public JCheckBox tagwritecomments = new  RECheckBox();
    public JCheckBox tagwritegenre = new RECheckBox();
    public JCheckBox tagwritetime = new RECheckBox();
    public JCheckBox tagwritetimesig = new RECheckBox();
    public JCheckBox tagwriterating = new RECheckBox();
    public JCheckBox tagwritingstyles = new RECheckBox();
    public JCheckBox tagwritingalbumcover = new RECheckBox();    
    public JCheckBox tagwritingcustomfields = new RECheckBox();    
    public JCheckBox createstylesfromgenretags = new RECheckBox();
    public JButton restoreFromServer = new REButton();
    public JTextField rootMusicDirectory = new RETextField();
    public JTextField username = new RETextField();
    public JPasswordField password = new REPasswordField();
    public JTextField emailaddress = new RETextField();
    public JCheckBox servercreatestyles = new RECheckBox();
    public JTextField userwebsite = new RETextField();
    public REComboBox id3writer = new REComboBox();
    public REComboBox id3reader = new REComboBox();
    public REComboBox filter1options = new REComboBox();
    public REComboBox filter2options = new REComboBox();
    public REComboBox filter3options = new REComboBox();
    public String filter1preset = null;
    public String filter2preset = null;
    public String filter3preset = null;
    public Filter filter1 = null;
    public Filter filter2 = null;
    public Filter filter3 = null;
    public REList filter1list = new REList();
    public REList filter2list = new REList();
    public REList filter3list = new REList();
    public JButton filter1clear = new REButton();
    public JButton filter2clear = new REButton();
    public JButton filter3clear = new REButton();
    public JButton filter1selectall = new REButton();
    public JButton filter2selectall = new REButton();
    public JButton filter3selectall = new REButton();
    public JButton system_info_button = new REButton();
    public JCheckBox enablefilter = new RECheckBox();
    public JCheckBox filterautosearch = new RECheckBox();
    public JCheckBox filter2disable = new RECheckBox();
    public JCheckBox filter3disable = new RECheckBox();
    public JCheckBox filterusestyles = new RECheckBox();
    public JCheckBox filteraffectedbysearch = new RECheckBox();
    public JCheckBox filtersingleselectmode = new RECheckBox();
    public JCheckBox strictstylessearch = new RECheckBox();
    public JCheckBox addmixoutchangesong = new RECheckBox();
    public JTextField albumcoverthumbwidth = new RETextField();
    public JCheckBox usefiltering = new RECheckBox(); // provides a way to search outside of filters without disabling, which can be resource intesnive
    public JCheckBox portablemusicmode = new RECheckBox();
    public JCheckBox autoupdatepaths = new RECheckBox();
    public JCheckBox addmixoutondoubleclick = new RECheckBox();
    public JCheckBox disableautocomplete = new RECheckBox();
    public JCheckBox enabledebug = new RECheckBox();
    public REComboBox style_require_shortcut_combobox = new REComboBox();
    public REComboBox style_exclude_shortcut_combobox = new REComboBox();
    public JCheckBox detect_start_and_end_keys = new RECheckBox();
    public JCheckBox detect_advanced_keys = new RECheckBox();
    public JCheckBox detect_disable_multithreaded = new RECheckBox();
    public JCheckBox automatically_parse_title_field = new RECheckBox();
    public JCheckBox blurDisabledButtons = new RECheckBox();
    public JCheckBox hideAdvancedFeatures = new RECheckBox();
    public JButton colorChooserButton = new JButton();
    
    public ColumnConfig availableskinconfig = new ColumnConfig();
    
    public REComboBox custom_field_1_tag_combo = new REComboBox();
    public REComboBox custom_field_2_tag_combo = new REComboBox();
    public REComboBox custom_field_3_tag_combo = new REComboBox();
    public REComboBox custom_field_4_tag_combo = new REComboBox();
    
    public JLabel version;
    public JLabel programmedby;
    public JLabel email;
    public JLabel web;

    KeyFormatUI keyformat_ui;
    public static int keychordtype1 = -1;
    public static int keychordtype2 = -1;

    public boolean songdisplayformatchanged = false;

    private void setupDialog() {
      // options dialog
      preventrepeats.setSelected(false);
      option_bpm_blink_rate.setSelected(true);
      filter1list.setModel(new DefaultListModel());
      filter2list.setModel(new DefaultListModel());
      filter3list.setModel(new DefaultListModel());      
      bpmdetectionquality.setMajorTickSpacing(2);
      bpmdetectionquality.setMinorTickSpacing(1);
      cpuutilization.setMajorTickSpacing(3);
      cpuutilization.setMinorTickSpacing(1);
      keydetectionquality.setMajorTickSpacing(45);
      keydetectionquality.setMinorTickSpacing(5);
      timepitchshiftquality.setMajorTickSpacing(4);
      timepitchshiftquality.setMinorTickSpacing(2);
      
      fullstyleslayout.setSelected(true);
      displaystylesonleft.setSelected(true);
      mixouttablehide.setSelected(true);
      prefer_id3_v23.setSelected(true);
      
      createstylesfromgenretags.setSelected(true);
      
      skin_title_field.putClientProperty(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS , Boolean.TRUE);
      skin_author_field.putClientProperty(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS , Boolean.TRUE);        
      skin_locale_field.putClientProperty(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS , Boolean.TRUE);
      skin_description_field.putClientProperty(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS , Boolean.TRUE);        
      
      version = new RELabel("rapid evolution " + RapidEvolution.versionString);
      programmedby = new RELabel("created by jesse bickmore");
      email = new RELabel("contact: qualia@mixshare.com");
      web = new RELabel("www.mixshare.com");
      username.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
              new NameChangeThread().start();
            }
          });
       username.setEnabled(false);
       colorchooser.getSelectionModel().addChangeListener(new ColorChangeListner());
       load_skin_button.setEnabled(false);
       available_skins_table.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(available_skins_table));

       available_skins_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
           public void valueChanged(ListSelectionEvent e) {
           if (available_skins_table.getSelectedRowCount() > 0) load_skin_button.setEnabled(true);
           else load_skin_button.setEnabled(false);
         }
       });
      searchwithinstyles.setEnabled(true);
      evenbpmmultiples.setEnabled(true);
      ignoremajorminor.setEnabled(true);
      clearsearchautomatically.setEnabled(true);
      disableonline.setEnabled(false);
      restoreFromServer.setEnabled(false);
      blurDisabledButtons.setSelected(false);
      hideAdvancedFeatures.setSelected(true);
    }

    // set default values, before loading from mixguide.dat
    public void Init() {
      customfieldtext1.setText(SkinManager.instance.getMessageText("default_custom_field_1_1abel"));
      customfieldtext2.setText(SkinManager.instance.getMessageText("default_custom_field_2_1abel"));
      customfieldtext3.setText(SkinManager.instance.getMessageText("default_custom_field_3_1abel"));
      customfieldtext4.setText(SkinManager.instance.getMessageText("default_custom_field_4_1abel"));
      albumcoverthumbwidth.setText(String.valueOf(myImageIcon.icon_size));
      //options_table_row_height.setText(String.valueOf());
      
//        disableonline.setSelected(false);
        if (StylesPane.instance.disableautohighlight) {
          autohighlightstyles.setSelected(false);
          autohighlightstyles.setEnabled(false);
        } else autohighlightstyles.setSelected(true);
    }

    public void PostInit() {
        try {
            // invokeAndWait was throwing exceptions changing skins
            // invokeLater was causing exceptions later because code down the line dependson this being run..
            // SwingUtilities.invokeLater(new PostInitThread());            

            if (!RapidEvolution.instance.loaded) {
                RedrawAvailableSkins();
                Hashtable bpmqualitylabelTable = new Hashtable();
                JLabel label1 = new MySliderLabel(SkinManager.instance.getMessageText("detect_low_quality"), bpmdetectionquality);
                JLabel label2 = new MySliderLabel(SkinManager.instance.getMessageText("detect_high_quality"), bpmdetectionquality);
                label1.setForeground(bpmdetectionquality.getForeground());
                label2.setForeground(bpmdetectionquality.getForeground());
                bpmqualitylabelTable.put(new Integer(1), label1);
                bpmqualitylabelTable.put(new Integer(9), label2);        
                bpmdetectionquality.setLabelTable(bpmqualitylabelTable);
                Hashtable keyqualitylabelTable = new Hashtable();
                label1 = new MySliderLabel(SkinManager.instance.getMessageText("detect_low_quality"), keydetectionquality);
                label2 = new MySliderLabel(SkinManager.instance.getMessageText("detect_high_quality"), keydetectionquality);
                label1.setForeground(keydetectionquality.getForeground());
                label2.setForeground(keydetectionquality.getForeground());        
                keyqualitylabelTable.put(new Integer(19), label1);
                keyqualitylabelTable.put(new Integer(91), label2);
                keydetectionquality.setLabelTable(keyqualitylabelTable);
                Hashtable timepitchlabelTable = new Hashtable();
                label1 = new MySliderLabel(SkinManager.instance.getMessageText("time_pitch_shift_low_quality"), timepitchshiftquality);
                label2 = new MySliderLabel(SkinManager.instance.getMessageText("time_pitch_shift_high_quality"), timepitchshiftquality);
                label1.setForeground(timepitchshiftquality.getForeground());
                label2.setForeground(timepitchshiftquality.getForeground());        
                timepitchlabelTable.put(new Integer(3), label1);
                timepitchlabelTable.put(new Integer(25), label2);
                timepitchshiftquality.setLabelTable(timepitchlabelTable);
                
                Hashtable cpuutilizationTable = new Hashtable();
                label1 = new MySliderLabel("10%", cpuutilization);
                label2 = new MySliderLabel("100%", cpuutilization);
                JLabel label3 = new MySliderLabel("50%", cpuutilization);
                label1.setForeground(cpuutilization.getForeground());
                label2.setForeground(cpuutilization.getForeground());
                label3.setForeground(cpuutilization.getForeground());
                cpuutilizationTable.put(new Integer(1), label1);
                cpuutilizationTable.put(new Integer(5), label3);
                cpuutilizationTable.put(new Integer(10), label2);
                cpuutilization.setLabelTable(cpuutilizationTable);
                
              }
              
              UpdateCustomFieldLabels();

              String[] colorchoiceStrings = { SkinManager.instance.getMessageText("color_search_disabled"),
                  SkinManager.instance.getMessageText("color_search_mixouts"),
                  SkinManager.instance.getMessageText("color_search_in_key_with_mixouts"),
                  SkinManager.instance.getMessageText("color_search_in_key"),
                  SkinManager.instance.getMessageText("color_search_with_mixouts"),
                  SkinManager.instance.getMessageText("color_search_default"),
                  SkinManager.instance.getMessageText("color_mixout_disabled"),
                  SkinManager.instance.getMessageText("color_mixout_ranked_with_mixouts"),
                  SkinManager.instance.getMessageText("color_mixout_ranked"),
                  SkinManager.instance.getMessageText("color_mixout_with_mixouts"),
                  SkinManager.instance.getMessageText("color_mixout_addon_ranked"),
                  SkinManager.instance.getMessageText("color_mixout_addon"),
                  SkinManager.instance.getMessageText("color_mixout_default"),
                  SkinManager.instance.getMessageText("color_style_selected"),
                  SkinManager.instance.getMessageText("color_style_selected_song"),
                  SkinManager.instance.getMessageText("color_style_selected_and_current_song"),
                  SkinManager.instance.getMessageText("color_style_excluded"),
                  SkinManager.instance.getMessageText("color_style_required")
              };
              colorselectionscombo.removeAllItems();
              for (int i = 0; i < colorchoiceStrings.length; ++i) colorselectionscombo.addItem(colorchoiceStrings[i]);
              colorselectionscombo.setSelectedIndex(0);
              
              String[] shortcut_items = new String[] { "ALT", "CTRL", "SHIFT" };
              style_require_shortcut_combobox.removeAllItems();
              style_exclude_shortcut_combobox.removeAllItems();
              for (int i = 0; i < shortcut_items.length; ++i) {
                  style_require_shortcut_combobox.addItem(shortcut_items[i]);
                  style_exclude_shortcut_combobox.addItem(shortcut_items[i]);
              }
              style_require_shortcut_combobox.setSelectedItem("CTRL");
              style_exclude_shortcut_combobox.setSelectedItem("ALT");
                    
              String[] customTagChoiceSkins = { SkinManager.instance.getMessageText("custom_field_id3_tag_none"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_catalogid"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_content_group_description"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_content_type"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_encoded_by"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_file_type"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_publisher"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_languages"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_byte_size"),
                      SkinManager.instance.getMessageText("custom_field_id3_tag_year"),
                  };
              custom_field_1_tag_combo.removeAllItems();
              custom_field_2_tag_combo.removeAllItems();
              custom_field_3_tag_combo.removeAllItems();
              custom_field_4_tag_combo.removeAllItems();
              for (int i = 0; i < customTagChoiceSkins.length; ++i) {
                  custom_field_1_tag_combo.addItem(customTagChoiceSkins[i]);
                  custom_field_2_tag_combo.addItem(customTagChoiceSkins[i]);              
                  custom_field_3_tag_combo.addItem(customTagChoiceSkins[i]);
                  custom_field_4_tag_combo.addItem(customTagChoiceSkins[i]);
              }
              custom_field_1_tag_combo.setSelectedIndex(0);      
              custom_field_2_tag_combo.setSelectedIndex(0);      
              custom_field_3_tag_combo.setSelectedIndex(0);      
              custom_field_4_tag_combo.setSelectedIndex(0);      
              
              keyformatcombo.removeAllItems();
              keyformatcombo.insertItemAt(SkinManager.instance.getMessageText("key_format_sharp"), 0);
              keyformatcombo.insertItemAt(SkinManager.instance.getMessageText("key_format_flat"), 1);
              keyformatcombo.insertItemAt(SkinManager.instance.getMessageText("key_format_code"), 2);
              keyformatcombo.insertItemAt(SkinManager.instance.getMessageText("key_format_custom"), 3);
              keyformatcombo.setSelectedIndex(0);

              id3writer.removeAllItems();
              id3writer.insertItemAt(SkinManager.instance.getMessageText("tag_library_automatic"), 0);
              id3writer.insertItemAt(SkinManager.instance.getMessageText("tag_library_jaudio"), 1);
              id3writer.insertItemAt(SkinManager.instance.getMessageText("tag_library_jid3"), 2);
              id3writer.setSelectedIndex(0);

              id3reader.removeAllItems();
              id3reader.insertItemAt(SkinManager.instance.getMessageText("tag_library_automatic"), 0);
              id3reader.insertItemAt(SkinManager.instance.getMessageText("tag_library_jaudio"), 1);
              id3reader.insertItemAt(SkinManager.instance.getMessageText("tag_library_jid3"), 2);
              id3reader.insertItemAt(SkinManager.instance.getMessageText("tag_library_quicktime"), 3);
              id3reader.setSelectedIndex(0);
              
              custom1sortas.removeAllItems();
              custom1sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_automatic"), 0);
              custom1sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_numeric"), 1);
              custom1sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_text"), 2);
              custom1sortas.setSelectedIndex(0);
              
              custom2sortas.removeAllItems();
              custom2sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_automatic"), 0);
              custom2sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_numeric"), 1);
              custom2sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_text"), 2);
              custom2sortas.setSelectedIndex(0);

              custom3sortas.removeAllItems();
              custom3sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_automatic"), 0);
              custom3sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_numeric"), 1);
              custom3sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_text"), 2);
              custom3sortas.setSelectedIndex(0);

              custom4sortas.removeAllItems();
              custom4sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_automatic"), 0);
              custom4sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_numeric"), 1);
              custom4sortas.insertItemAt(SkinManager.instance.getMessageText("option_custom_field_sort_text"), 2);
              custom4sortas.setSelectedIndex(0);
              
               MixListMouse.instance.PostInit();
               StyleExcludeMouse.instance.PostInit();
               SearchListMouse.instance.PostInit();
               ListStyleSongMouse.instance.PostInit();
               StyleIncludeMouse.instance.PostInit();

               if (disable_multiple_delect.isSelected()) 
                   SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
               else SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);            
               
               MixoutPane.instance.mixouttable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
               
               
        } catch (Exception e) {
            log.error("PostInit(): error Exception", e);
        }
    }

    public class PostInitThread extends Thread {
        public void run() {
            try {
                
            } catch (Exception e) {
                log.error("run(): error Exception", e);
            }
        }        
    }    
    
    class NameChangeThread extends Thread {
      public void run() {
        String serverusername = MixshareClient.instance.servercommand.changeName(username.getText());
        username.setText(serverusername);
      }
    };


    class ColorChangeListner implements ChangeListener {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (RapidEvolution.instance.loaded) {
        	  if (log.isDebugEnabled())
        		  log.debug("stateChanged(): selected_index=" + colorselectionscombo.getSelectedIndex());
            if (colorselectionscombo.getSelectedIndex() == 0) SkinManager.instance.setColor("song_background_disabled", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 1) SkinManager.instance.setColor("song_background_mixouts", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 2) SkinManager.instance.setColor("song_background_in_key_with_mixouts", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 3) SkinManager.instance.setColor("song_background_in_key", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 4) SkinManager.instance.setColor("song_background_with_mixouts", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 5) SkinManager.instance.setColor("song_background_default", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 6) SkinManager.instance.setColor("mixout_background_disabled", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 7) SkinManager.instance.setColor("mixout_background_ranked_with_mixouts", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 8) SkinManager.instance.setColor("mixout_background_ranked", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 9) SkinManager.instance.setColor("mixout_background_with_mixouts", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 10) SkinManager.instance.setColor("mixout_background_addon_ranked", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 11) SkinManager.instance.setColor("mixout_background_addon", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 12) SkinManager.instance.setColor("mixout_background_default", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 13) SkinManager.instance.setColor("style_background_selected", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 14) SkinManager.instance.setColor("style_background_of_selected_song", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 15) SkinManager.instance.setColor("style_background_of_current_song", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 16) SkinManager.instance.setColor("style_background_excluded", colorchooser.getColor());
            if (colorselectionscombo.getSelectedIndex() == 17) SkinManager.instance.setColor("style_background_required", colorchooser.getColor());
            SongLinkedList.resetColorCache();
            StylesPane.instance.styletree.repaint();
            MixoutPane.instance.mixouttable.repaint();
            SearchPane.instance.searchtable.repaint();
          }
      }
    }

    public void ClearSelectedSearchColumns() {
        searchcolumn_songid.setSelected(false);
        searchcolumn_artist.setSelected(false);
        searchcolumn_album.setSelected(false);
        searchcolumn_track.setSelected(false);
        searchcolumn_title.setSelected(false);
        searchcolumn_bpmdiff.setSelected(false);
        searchcolumn_stylesimilarity.setSelected(false);
        searchcolumn_artistsimilarity.setSelected(false);
        searchcolumn_colorsimilarity.setSelected(false);
        searchcolumn_timesplayed.setSelected(false);
        searchcolumn_replaygain.setSelected(false);
        searchcolumn_relbpmdiff.setSelected(false);
        searchcolumn_keylock.setSelected(false);
        searchcolumn_keyaccuracy.setSelected(false);
        searchcolumn_bpmaccuracy.setSelected(false);
        searchcolumn_beatintensity.setSelected(false);
        searchcolumn_rating.setSelected(false);
        searchcolumn_albumcover.setSelected(false);
        searchcolumn_keymode.setSelected(false);
        searchcolumn_keycode.setSelected(false);
        searchcolumn_actualkey.setSelected(false);
        searchcolumn_actualkeycode.setSelected(false);
        searchcolumn_pitchshift.setSelected(false);
        searchcolumn_length.setSelected(false);
        searchcolumn_remixer.setSelected(false);
        searchcolumn_user1.setSelected(false);
        searchcolumn_user2.setSelected(false);
        searchcolumn_user3.setSelected(false);
        searchcolumn_user4.setSelected(false);
        searchcolumn_dateadded.setSelected(false);
        searchcolumn_lastmodified.setSelected(false);
        searchcolumn_timesig.setSelected(false);
        searchcolumn_bpm.setSelected(false);
        searchcolumn_startbpm.setSelected(false);
        searchcolumn_endbpm.setSelected(false);
        searchcolumn_key.setSelected(false);
        searchcolumn_startkey.setSelected(false);
        searchcolumn_endkey.setSelected(false);
        searchcolumn_comments.setSelected(false);
        searchcolumn_styles.setSelected(false);
        searchcolumn_filename.setSelected(false);
        searchcolumn_hasalbumcover.setSelected(false);
        searchcolumn_vinyl.setSelected(false);
        searchcolumn_nonvinyl.setSelected(false);
        searchcolumn_disabled.setSelected(false);
        searchcolumn_nummixouts.setSelected(false);
        searchcolumn_numaddons.setSelected(false);
        searchcolumn_songidwinfo.setSelected(false);
        searchcolumn_shortid.setSelected(false);
        searchcolumn_shortidwinfo.setSelected(false);
    }

    public void SetSelectedSearchColumns() {
        for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
          int cindex = SearchPane.instance.searchcolumnconfig.columnindex[i];
          if (cindex == 0) searchcolumn_songid.setSelected(true);
          if (cindex == 1) searchcolumn_artist.setSelected(true);
          if (cindex == 2) searchcolumn_album.setSelected(true);
          if (cindex == 3) searchcolumn_track.setSelected(true);
          if (cindex == 4) searchcolumn_title.setSelected(true);
          if (cindex == 5) searchcolumn_length.setSelected(true);
          if (cindex == 6) searchcolumn_timesig.setSelected(true);
          if (cindex == 7) searchcolumn_startbpm.setSelected(true);
          if (cindex == 8) searchcolumn_endbpm.setSelected(true);
          if (cindex == 9) searchcolumn_bpm.setSelected(true);
          if (cindex == 10) searchcolumn_startkey.setSelected(true);
          if (cindex == 11) searchcolumn_endkey.setSelected(true);
          if (cindex == 12) searchcolumn_key.setSelected(true);
          if (cindex == 13) searchcolumn_comments.setSelected(true);
          if (cindex == 43) searchcolumn_styles.setSelected(true);
          if (cindex == 14) searchcolumn_filename.setSelected(true);
          if (cindex == 44) searchcolumn_hasalbumcover.setSelected(true);
          if (cindex == 15) searchcolumn_vinyl.setSelected(true);
          if (cindex == 16) searchcolumn_nonvinyl.setSelected(true);
          if (cindex == 17) searchcolumn_disabled.setSelected(true);
          if (cindex == 18) searchcolumn_bpmdiff.setSelected(true);
          if (cindex == 25) searchcolumn_relbpmdiff.setSelected(true);
          if (cindex == 19) searchcolumn_keylock.setSelected(true);
          if (cindex == 20) searchcolumn_nummixouts.setSelected(true);
          if (cindex == 21) searchcolumn_songidwinfo.setSelected(true);
          if (cindex == 23) searchcolumn_numaddons.setSelected(true);
          if (cindex == 26) searchcolumn_keymode.setSelected(true);
          if (cindex == 27) searchcolumn_remixer.setSelected(true);
          if (cindex == 28) searchcolumn_user1.setSelected(true);
          if (cindex == 29) searchcolumn_user2.setSelected(true);
          if (cindex == 30) searchcolumn_user3.setSelected(true);
          if (cindex == 31) searchcolumn_user4.setSelected(true);
          if (cindex == 32) searchcolumn_shortid.setSelected(true);
          if (cindex == 33) searchcolumn_shortidwinfo.setSelected(true);
          if (cindex == 34) searchcolumn_stylesimilarity.setSelected(true);
          if (cindex == 35) searchcolumn_keycode.setSelected(true);
          if (cindex == 36) searchcolumn_colorsimilarity.setSelected(true);
          if (cindex == 37) searchcolumn_dateadded.setSelected(true);
          if (cindex == 38) searchcolumn_artistsimilarity.setSelected(true);
          if (cindex == 40) searchcolumn_timesplayed.setSelected(true);
          if (cindex == 41) searchcolumn_keyaccuracy.setSelected(true);
          if (cindex == 42) searchcolumn_bpmaccuracy.setSelected(true);
          if (cindex == 45) searchcolumn_lastmodified.setSelected(true);
          if (cindex == 46) searchcolumn_rating.setSelected(true);
          if (cindex == 47) searchcolumn_actualkey.setSelected(true);
          if (cindex == 48) searchcolumn_pitchshift.setSelected(true);
          if (cindex == 49) searchcolumn_actualkeycode.setSelected(true);
          if (cindex == 50) searchcolumn_beatintensity.setSelected(true);
          if (cindex == 51) searchcolumn_replaygain.setSelected(true);
          if (cindex == 52) searchcolumn_albumcover.setSelected(true);
        }
    }

    public void ClearSelectedMixoutColumns() {
        mixoutcolumn_songid.setSelected(false);
        mixoutcolumn_artist.setSelected(false);
        mixoutcolumn_album.setSelected(false);
        mixoutcolumn_track.setSelected(false);
        mixoutcolumn_title.setSelected(false);
        mixoutcolumn_remixer.setSelected(false);
        mixoutcolumn_stylesimilarity.setSelected(false);
        mixoutcolumn_artistsimilarity.setSelected(false);
        mixoutcolumn_colorsimilarity.setSelected(false);
        mixoutcolumn_timesplayed.setSelected(false);
        mixoutcolumn_replaygain.setSelected(false);
        mixoutcolumn_user1.setSelected(false);
        mixoutcolumn_user2.setSelected(false);
        mixoutcolumn_user3.setSelected(false);
        mixoutcolumn_user4.setSelected(false);
        mixoutcolumn_dateadded.setSelected(false);
        mixoutcolumn_lastmodified.setSelected(false);
        mixoutcolumn_bpmdiff.setSelected(false);
        mixoutcolumn_relbpmdiff.setSelected(false);
        mixoutcolumn_keylock.setSelected(false);
        mixoutcolumn_keyaccuracy.setSelected(false);
        mixoutcolumn_bpmaccuracy.setSelected(false);
        mixoutcolumn_beatintensity.setSelected(false);
        mixoutcolumn_rating.setSelected(false);
        mixoutcolumn_albumcover.setSelected(false);
        mixoutcolumn_keymode.setSelected(false);
        mixoutcolumn_keycode.setSelected(false);
        mixoutcolumn_actualkey.setSelected(false);
        mixoutcolumn_actualkeycode.setSelected(false);
        mixoutcolumn_pitchshift.setSelected(false);
        mixoutcolumn_length.setSelected(false);
        mixoutcolumn_timesig.setSelected(false);
        mixoutcolumn_bpm.setSelected(false);
        mixoutcolumn_startbpm.setSelected(false);
        mixoutcolumn_endbpm.setSelected(false);
        mixoutcolumn_key.setSelected(false);
        mixoutcolumn_startkey.setSelected(false);
        mixoutcolumn_endkey.setSelected(false);
        mixoutcolumn_comments.setSelected(false);
        mixoutcolumn_styles.setSelected(false);
        mixoutcolumn_filename.setSelected(false);
        mixoutcolumn_hasalbumcover.setSelected(false);
        mixoutcolumn_vinyl.setSelected(false);
        mixoutcolumn_nonvinyl.setSelected(false);
        mixoutcolumn_disabled.setSelected(false);
        mixoutcolumn_nummixouts.setSelected(false);
        mixoutcolumn_numaddons.setSelected(false);
        mixoutcolumn_songidwinfo.setSelected(false);
        mixoutcolumn_shortid.setSelected(false);
        mixoutcolumn_shortidwinfo.setSelected(false);
        mixoutcolumn_rank.setSelected(false);
        mixoutcolumn_isaddon.setSelected(false);
        mixoutcolumn_mixcomments.setSelected(false);
    }

    public void SetSelectedMixoutColumns() {
        for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
          int cindex = MixoutPane.instance.mixoutcolumnconfig.columnindex[i];
          if (cindex == 0) mixoutcolumn_songid.setSelected(true);
          if (cindex == 1) mixoutcolumn_artist.setSelected(true);
          if (cindex == 2) mixoutcolumn_album.setSelected(true);
          if (cindex == 3) mixoutcolumn_track.setSelected(true);
          if (cindex == 4) mixoutcolumn_title.setSelected(true);
          if (cindex == 5) mixoutcolumn_length.setSelected(true);
          if (cindex == 6) mixoutcolumn_timesig.setSelected(true);
          if (cindex == 7) mixoutcolumn_startbpm.setSelected(true);
          if (cindex == 8) mixoutcolumn_endbpm.setSelected(true);
          if (cindex == 9) mixoutcolumn_bpm.setSelected(true);
          if (cindex == 10) mixoutcolumn_startkey.setSelected(true);
          if (cindex == 11) mixoutcolumn_endkey.setSelected(true);
          if (cindex == 12) mixoutcolumn_key.setSelected(true);
          if (cindex == 13) mixoutcolumn_comments.setSelected(true);
          if (cindex == 43) mixoutcolumn_styles.setSelected(true);
          if (cindex == 14) mixoutcolumn_filename.setSelected(true);
          if (cindex == 44) mixoutcolumn_hasalbumcover.setSelected(true);
          if (cindex == 15) mixoutcolumn_vinyl.setSelected(true);
          if (cindex == 16) mixoutcolumn_nonvinyl.setSelected(true);
          if (cindex == 17) mixoutcolumn_disabled.setSelected(true);
          if (cindex == 18) mixoutcolumn_bpmdiff.setSelected(true);
          if (cindex == 19) mixoutcolumn_keylock.setSelected(true);
          if (cindex == 20) mixoutcolumn_nummixouts.setSelected(true);
          if (cindex == 21) mixoutcolumn_songidwinfo.setSelected(true);
          if (cindex == 22) mixoutcolumn_rank.setSelected(true);
          if (cindex == 23) mixoutcolumn_numaddons.setSelected(true);
          if (cindex == 24) mixoutcolumn_isaddon.setSelected(true);
          if (cindex == 25) mixoutcolumn_relbpmdiff.setSelected(true);
          if (cindex == 26) mixoutcolumn_keymode.setSelected(true);
          if (cindex == 27) mixoutcolumn_remixer.setSelected(true);
          if (cindex == 28) mixoutcolumn_user1.setSelected(true);
          if (cindex == 29) mixoutcolumn_user2.setSelected(true);
          if (cindex == 30) mixoutcolumn_user3.setSelected(true);
          if (cindex == 31) mixoutcolumn_user4.setSelected(true);
          if (cindex == 32) mixoutcolumn_shortid.setSelected(true);
          if (cindex == 33) mixoutcolumn_shortidwinfo.setSelected(true);
          if (cindex == 34) mixoutcolumn_stylesimilarity.setSelected(true);
          if (cindex == 35) mixoutcolumn_keycode.setSelected(true);
          if (cindex == 36) mixoutcolumn_colorsimilarity.setSelected(true);
          if (cindex == 37) mixoutcolumn_dateadded.setSelected(true);
          if (cindex == 38) mixoutcolumn_artistsimilarity.setSelected(true);
          if (cindex == 39) mixoutcolumn_mixcomments.setSelected(true);
          if (cindex == 40) mixoutcolumn_timesplayed.setSelected(true);
          if (cindex == 41) mixoutcolumn_keyaccuracy.setSelected(true);
          if (cindex == 42) mixoutcolumn_bpmaccuracy.setSelected(true);
          if (cindex == 45) mixoutcolumn_lastmodified.setSelected(true);
          if (cindex == 46) mixoutcolumn_rating.setSelected(true);
          if (cindex == 47) mixoutcolumn_actualkey.setSelected(true);
          if (cindex == 48) mixoutcolumn_pitchshift.setSelected(true);
          if (cindex == 49) mixoutcolumn_actualkeycode.setSelected(true);
          if (cindex == 50) mixoutcolumn_beatintensity.setSelected(true);
          if (cindex == 51) mixoutcolumn_replaygain.setSelected(true);
          if (cindex == 52) mixoutcolumn_albumcover.setSelected(true);
        }
    }

    public void SetSelectedSearchColumn(int cindex) {
        if (cindex == 0)
          searchcolumn_songid.setSelected(true);
        if (cindex == 1)
          searchcolumn_artist.setSelected(true);
        if (cindex == 2)
          searchcolumn_album.setSelected(true);
        if (cindex == 3)
          searchcolumn_track.setSelected(true);
        if (cindex == 4)
          searchcolumn_title.setSelected(true);
        if (cindex == 5)
          searchcolumn_length.setSelected(true);
        if (cindex == 6)
          searchcolumn_timesig.setSelected(true);
        if (cindex == 7)
          searchcolumn_startbpm.setSelected(true);
        if (cindex == 8)
          searchcolumn_endbpm.setSelected(true);
        if (cindex == 9)
          searchcolumn_bpm.setSelected(true);
        if (cindex == 10)
          searchcolumn_startkey.setSelected(true);
        if (cindex == 11)
          searchcolumn_endkey.setSelected(true);
        if (cindex == 12)
          searchcolumn_key.setSelected(true);
        if (cindex == 13)
          searchcolumn_comments.setSelected(true);
        if (cindex == 43)
            searchcolumn_styles.setSelected(true);
        if (cindex == 14)
          searchcolumn_filename.setSelected(true);
        if (cindex == 44)
            searchcolumn_hasalbumcover.setSelected(true);
        if (cindex == 15)
          searchcolumn_vinyl.setSelected(true);
        if (cindex == 16)
          searchcolumn_nonvinyl.setSelected(true);
        if (cindex == 17)
          searchcolumn_disabled.setSelected(true);
        if (cindex == 18)
          searchcolumn_bpmdiff.setSelected(true);
        if (cindex == 25)
          searchcolumn_relbpmdiff.setSelected(true);
        if (cindex == 19)
          searchcolumn_keylock.setSelected(true);
        if (cindex == 20)
          searchcolumn_nummixouts.setSelected(true);
        if (cindex == 21)
          searchcolumn_songidwinfo.setSelected(true);
        if (cindex == 23)
          searchcolumn_numaddons.setSelected(true);
        if (cindex == 26) searchcolumn_keymode.setSelected(true);
        if (cindex == 27) searchcolumn_remixer.setSelected(true);
        if (cindex == 28) searchcolumn_user1.setSelected(true);
        if (cindex == 29) searchcolumn_user2.setSelected(true);
        if (cindex == 30) searchcolumn_user3.setSelected(true);
        if (cindex == 31) searchcolumn_user4.setSelected(true);
        if (cindex == 32) searchcolumn_shortid.setSelected(true);
        if (cindex == 33) searchcolumn_shortidwinfo.setSelected(true);
        if (cindex == 34) searchcolumn_stylesimilarity.setSelected(true);
        if (cindex == 35) searchcolumn_keycode.setSelected(true);
        if (cindex == 36) searchcolumn_colorsimilarity.setSelected(true);
        if (cindex == 37) searchcolumn_dateadded.setSelected(true);
        if (cindex == 38) searchcolumn_artistsimilarity.setSelected(true);
        if (cindex == 40) searchcolumn_timesplayed.setSelected(true);
        if (cindex == 41) searchcolumn_keyaccuracy.setSelected(true);
        if (cindex == 42) searchcolumn_bpmaccuracy.setSelected(true);
        if (cindex == 45) searchcolumn_lastmodified.setSelected(true);
        if (cindex == 46) searchcolumn_rating.setSelected(true);
        if (cindex == 47) searchcolumn_actualkey.setSelected(true);
        if (cindex == 48) searchcolumn_pitchshift.setSelected(true);
        if (cindex == 49) searchcolumn_actualkeycode.setSelected(true);
        if (cindex == 50) searchcolumn_beatintensity.setSelected(true);
        if (cindex == 51) searchcolumn_replaygain.setSelected(true);
        if (cindex == 52) searchcolumn_albumcover.setSelected(true);
    }    

    public int GetSearchColumnCount() {
      int columncount = 0;
      if (searchcolumn_songid.isSelected()) columncount++;
      if (searchcolumn_artist.isSelected()) columncount++;
      if (searchcolumn_album.isSelected()) columncount++;
      if (searchcolumn_track.isSelected()) columncount++;
      if (searchcolumn_title.isSelected()) columncount++;
      if (searchcolumn_bpmdiff.isSelected()) columncount++;
      if (searchcolumn_stylesimilarity.isSelected()) columncount++;
      if (searchcolumn_artistsimilarity.isSelected()) columncount++;
      if (searchcolumn_colorsimilarity.isSelected()) columncount++;
      if (searchcolumn_timesplayed.isSelected()) columncount++;
      if (searchcolumn_replaygain.isSelected()) columncount++;
      if (searchcolumn_relbpmdiff.isSelected()) columncount++;
      if (searchcolumn_keylock.isSelected()) columncount++;
      if (searchcolumn_keyaccuracy.isSelected()) columncount++;
      if (searchcolumn_bpmaccuracy.isSelected()) columncount++;
      if (searchcolumn_beatintensity.isSelected()) columncount++;
      if (searchcolumn_rating.isSelected()) columncount++;
      if (searchcolumn_albumcover.isSelected()) columncount++;
      if (searchcolumn_keymode.isSelected()) columncount++;
      if (searchcolumn_keycode.isSelected()) columncount++;
      if (searchcolumn_actualkey.isSelected()) columncount++;
      if (searchcolumn_actualkeycode.isSelected()) columncount++;
      if (searchcolumn_pitchshift.isSelected()) columncount++;
      if (searchcolumn_length.isSelected()) columncount++;
      if (searchcolumn_remixer.isSelected()) columncount++;
      if (searchcolumn_user1.isSelected()) columncount++;
      if (searchcolumn_user2.isSelected()) columncount++;
      if (searchcolumn_user3.isSelected()) columncount++;
      if (searchcolumn_user4.isSelected()) columncount++;
      if (searchcolumn_dateadded.isSelected()) columncount++;
      if (searchcolumn_timesig.isSelected()) columncount++;
      if (searchcolumn_bpm.isSelected()) columncount++;
      if (searchcolumn_startbpm.isSelected()) columncount++;
      if (searchcolumn_endbpm.isSelected()) columncount++;
      if (searchcolumn_key.isSelected()) columncount++;
      if (searchcolumn_startkey.isSelected()) columncount++;
      if (searchcolumn_endkey.isSelected()) columncount++;
      if (searchcolumn_comments.isSelected()) columncount++;
      if (searchcolumn_styles.isSelected()) columncount++;
      if (searchcolumn_filename.isSelected()) columncount++;
      if (searchcolumn_hasalbumcover.isSelected()) columncount++;
      if (searchcolumn_vinyl.isSelected()) columncount++;
      if (searchcolumn_nonvinyl.isSelected()) columncount++;
      if (searchcolumn_disabled.isSelected()) columncount++;
      if (searchcolumn_nummixouts.isSelected()) columncount++;
      if (searchcolumn_numaddons.isSelected()) columncount++;
      if (searchcolumn_songidwinfo.isSelected()) columncount++;
      if (searchcolumn_shortidwinfo.isSelected()) columncount++;
      if (searchcolumn_shortid.isSelected()) columncount++;
      if (searchcolumn_lastmodified.isSelected()) columncount++;
      return columncount;
    }

    public void SetSelectedMixoutColumn(int cindex) {
        if (cindex == 0)
          mixoutcolumn_songid.setSelected(true);
        if (cindex == 1)
          mixoutcolumn_artist.setSelected(true);
        if (cindex == 2)
          mixoutcolumn_album.setSelected(true);
        if (cindex == 3)
          mixoutcolumn_track.setSelected(true);
        if (cindex == 4)
          mixoutcolumn_title.setSelected(true);
        if (cindex == 5)
          mixoutcolumn_length.setSelected(true);
        if (cindex == 6)
          mixoutcolumn_timesig.setSelected(true);
        if (cindex == 7)
          mixoutcolumn_startbpm.setSelected(true);
        if (cindex == 8)
          mixoutcolumn_endbpm.setSelected(true);
        if (cindex == 9)
          mixoutcolumn_bpm.setSelected(true);
        if (cindex == 10)
          mixoutcolumn_startkey.setSelected(true);
        if (cindex == 11)
          mixoutcolumn_endkey.setSelected(true);
        if (cindex == 12)
          mixoutcolumn_key.setSelected(true);
        if (cindex == 13)
          mixoutcolumn_comments.setSelected(true);
        if (cindex == 43)
            mixoutcolumn_styles.setSelected(true);
        if (cindex == 14)
          mixoutcolumn_filename.setSelected(true);
        if (cindex == 44) 
            mixoutcolumn_hasalbumcover.setSelected(true);
        if (cindex == 15)
          mixoutcolumn_vinyl.setSelected(true);
        if (cindex == 16)
          mixoutcolumn_nonvinyl.setSelected(true);
        if (cindex == 17)
          mixoutcolumn_disabled.setSelected(true);
        if (cindex == 18)
          mixoutcolumn_bpmdiff.setSelected(true);
        if (cindex == 19)
          mixoutcolumn_keylock.setSelected(true);
        if (cindex == 20)
          mixoutcolumn_nummixouts.setSelected(true);
        if (cindex == 21)
          mixoutcolumn_songidwinfo.setSelected(true);
        if (cindex == 22)
          mixoutcolumn_rank.setSelected(true);
        if (cindex == 23)
          mixoutcolumn_numaddons.setSelected(true);
        if (cindex == 24)
          mixoutcolumn_isaddon.setSelected(true);
        if (cindex == 25)
          mixoutcolumn_relbpmdiff.setSelected(true);
        if (cindex == 26) mixoutcolumn_keymode.setSelected(true);
        if (cindex == 27) mixoutcolumn_remixer.setSelected(true);
        if (cindex == 28) mixoutcolumn_user1.setSelected(true);
        if (cindex == 29) mixoutcolumn_user2.setSelected(true);
        if (cindex == 30) mixoutcolumn_user3.setSelected(true);
        if (cindex == 31) mixoutcolumn_user4.setSelected(true);
        if (cindex == 32) mixoutcolumn_shortid.setSelected(true);
        if (cindex == 33) mixoutcolumn_shortidwinfo.setSelected(true);
        if (cindex == 34) mixoutcolumn_stylesimilarity.setSelected(true);
        if (cindex == 35) mixoutcolumn_keycode.setSelected(true);
        if (cindex == 36) mixoutcolumn_colorsimilarity.setSelected(true);
        if (cindex == 37) mixoutcolumn_dateadded.setSelected(true);
        if (cindex == 38) mixoutcolumn_artistsimilarity.setSelected(true);
        if (cindex == 39) mixoutcolumn_mixcomments.setSelected(true);
        if (cindex == 40) mixoutcolumn_timesplayed.setSelected(true);
        if (cindex == 41) mixoutcolumn_keyaccuracy.setSelected(true);
        if (cindex == 42) mixoutcolumn_bpmaccuracy.setSelected(true);
        if (cindex == 45) mixoutcolumn_lastmodified.setSelected(true);
        if (cindex == 46) mixoutcolumn_rating.setSelected(true);
        if (cindex == 47) mixoutcolumn_actualkey.setSelected(true);
        if (cindex == 48) mixoutcolumn_pitchshift.setSelected(true);
        if (cindex == 49) mixoutcolumn_actualkeycode.setSelected(true);
        if (cindex == 50) mixoutcolumn_beatintensity.setSelected(true);
        if (cindex == 51) mixoutcolumn_replaygain.setSelected(true);
        if (cindex == 52) mixoutcolumn_albumcover.setSelected(true);
    }

    public int GetMixoutColumnCount() {
      int columncount = 0;
      if (mixoutcolumn_songid.isSelected()) columncount++;
      if (mixoutcolumn_artist.isSelected()) columncount++;
      if (mixoutcolumn_album.isSelected()) columncount++;
      if (mixoutcolumn_track.isSelected()) columncount++;
      if (mixoutcolumn_title.isSelected()) columncount++;
      if (mixoutcolumn_remixer.isSelected()) columncount++;
      if (mixoutcolumn_stylesimilarity.isSelected()) columncount++;
      if (mixoutcolumn_artistsimilarity.isSelected()) columncount++;
      if (mixoutcolumn_colorsimilarity.isSelected()) columncount++;
      if (mixoutcolumn_timesplayed.isSelected()) columncount++;
      if (mixoutcolumn_replaygain.isSelected()) columncount++;
      if (mixoutcolumn_user1.isSelected()) columncount++;
      if (mixoutcolumn_user2.isSelected()) columncount++;
      if (mixoutcolumn_user3.isSelected()) columncount++;
      if (mixoutcolumn_user4.isSelected()) columncount++;
      if (mixoutcolumn_dateadded.isSelected()) columncount++;
      if (mixoutcolumn_lastmodified.isSelected()) columncount++;
      if (mixoutcolumn_bpmdiff.isSelected()) columncount++;
      if (mixoutcolumn_relbpmdiff.isSelected()) columncount++;
      if (mixoutcolumn_keylock.isSelected()) columncount++;
      if (mixoutcolumn_keyaccuracy.isSelected()) columncount++;
      if (mixoutcolumn_bpmaccuracy.isSelected()) columncount++;
      if (mixoutcolumn_beatintensity.isSelected()) columncount++;
      if (mixoutcolumn_rating.isSelected()) columncount++;
      if (mixoutcolumn_albumcover.isSelected()) columncount++;
      if (mixoutcolumn_keymode.isSelected()) columncount++;
      if (mixoutcolumn_keycode.isSelected()) columncount++;
      if (mixoutcolumn_actualkey.isSelected()) columncount++;
      if (mixoutcolumn_actualkeycode.isSelected()) columncount++;
      if (mixoutcolumn_pitchshift.isSelected()) columncount++;
      if (mixoutcolumn_length.isSelected()) columncount++;
      if (mixoutcolumn_timesig.isSelected()) columncount++;
      if (mixoutcolumn_bpm.isSelected()) columncount++;
      if (mixoutcolumn_startbpm.isSelected()) columncount++;
      if (mixoutcolumn_endbpm.isSelected()) columncount++;
      if (mixoutcolumn_key.isSelected()) columncount++;
      if (mixoutcolumn_startkey.isSelected()) columncount++;
      if (mixoutcolumn_endkey.isSelected()) columncount++;
      if (mixoutcolumn_comments.isSelected()) columncount++;
      if (mixoutcolumn_styles.isSelected()) columncount++;
      if (mixoutcolumn_filename.isSelected()) columncount++;
      if (mixoutcolumn_hasalbumcover.isSelected()) columncount++;
      if (mixoutcolumn_vinyl.isSelected()) columncount++;
      if (mixoutcolumn_nonvinyl.isSelected()) columncount++;
      if (mixoutcolumn_disabled.isSelected()) columncount++;
      if (mixoutcolumn_nummixouts.isSelected()) columncount++;
      if (mixoutcolumn_numaddons.isSelected()) columncount++;
      if (mixoutcolumn_songidwinfo.isSelected()) columncount++;
      if (mixoutcolumn_shortid.isSelected()) columncount++;
      if (mixoutcolumn_shortidwinfo.isSelected()) columncount++;
      if (mixoutcolumn_rank.isSelected()) columncount++;
      if (mixoutcolumn_isaddon.isSelected()) columncount++;
      if (mixoutcolumn_mixcomments.isSelected()) columncount++;
      return columncount;
    }

    public String oldcustomfield1;
    public String oldcustomfield2;
    public String oldcustomfield3;
    public String oldcustomfield4;
    class customkeylistener extends KeyAdapter {
      public void keyReleased(KeyEvent e) {
        UpdateCustomFieldLabels();
      }
    }

    public void UpdateCustomFieldLabels() {
        boolean disable1 = false;
        boolean disable2 = false;
        boolean disable3 = false;
        boolean disable4 = false;
        int start = 0;
        boolean notdone = true;
        while (notdone) {
          if ((ColumnConfig.getKeyword(start) == null) || (ColumnConfig.getKeyword(start).equals("")) || (ColumnConfig.getKeyword(start).equals("invalid column code"))) notdone = false;
          else {
            if (!disable1 && (ColumnConfig.getKeyword(start).equals(customfieldtext1.getText()))) { if ((start < 28) || (start > 31)) disable1 = true; }
            if (!disable2 && (ColumnConfig.getKeyword(start).equals(customfieldtext2.getText()))) { if ((start < 28) || (start > 31)) disable2 = true; }
            if (!disable3 && (ColumnConfig.getKeyword(start).equals(customfieldtext3.getText()))) { if ((start < 28) || (start > 31)) disable3 = true; }
            if (!disable4 && (ColumnConfig.getKeyword(start).equals(customfieldtext4.getText()))) { if ((start < 28) || (start > 31)) disable4 = true; }
            start++;
          }
        }
        if (disable1 || disable2 || disable3 || disable4) {
            IOptionPane.showMessageDialog(getDialog(),
            SkinManager.instance.getDialogMessageText("invalid_custom_field"),
            SkinManager.instance.getDialogMessageTitle("invalid_custom_field"),
            IOptionPane.ERROR_MESSAGE);
        }
        if (disable1) customfieldtext1.setText(oldcustomfield1);
        if (disable2) customfieldtext2.setText(oldcustomfield2);
        if (disable3) customfieldtext3.setText(oldcustomfield3);
        if (disable4) customfieldtext4.setText(oldcustomfield4);
        AddSongsUI.instance.addcustomfieldlabel1.setText(customfieldtext1.getText()+ ":");
        EditSongUI.instance.editcustomfieldlabel1.setText(customfieldtext1.getText()+ ":");
        AddSongsUI.instance.addcustomfieldlabel2.setText(customfieldtext2.getText()+ ":");
        EditSongUI.instance.editcustomfieldlabel2.setText(customfieldtext2.getText()+ ":");
        AddSongsUI.instance.addcustomfieldlabel3.setText(customfieldtext3.getText()+ ":");
        EditSongUI.instance.editcustomfieldlabel3.setText(customfieldtext3.getText()+ ":");
        AddSongsUI.instance.addcustomfieldlabel4.setText(customfieldtext4.getText()+ ":");
        EditSongUI.instance.editcustomfieldlabel4.setText(customfieldtext4.getText()+ ":");
        searchcolumn_user1.setText(customfieldtext1.getText());
        searchcolumn_user2.setText(customfieldtext2.getText());
        searchcolumn_user3.setText(customfieldtext3.getText());
        searchcolumn_user4.setText(customfieldtext4.getText());
        mixoutcolumn_user1.setText(customfieldtext1.getText());
        mixoutcolumn_user2.setText(customfieldtext2.getText());
        mixoutcolumn_user3.setText(customfieldtext3.getText());
        mixoutcolumn_user4.setText(customfieldtext4.getText());
        SearchListMouse.instance.setuser1.setText(customfieldtext1.getText());
        SearchListMouse.instance.setuser2.setText(customfieldtext2.getText());
        SearchListMouse.instance.setuser3.setText(customfieldtext3.getText());
        SearchListMouse.instance.setuser4.setText(customfieldtext4.getText());

        for (int i = 0; i < SearchPane.instance.searchtable.getColumnModel().getColumnCount(); ++i) {
          if (SearchPane.instance.searchtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield1)) {
              SearchPane.instance.searchtable.getColumn(oldcustomfield1).setHeaderValue(customfieldtext1.getText());
              SearchPane.instance.searchtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext1.getText());
          }
          if (SearchPane.instance.searchtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield2)) {
              SearchPane.instance.searchtable.getColumn(oldcustomfield2).setHeaderValue(customfieldtext2.getText());
              SearchPane.instance.searchtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext2.getText());
          }
          if (SearchPane.instance.searchtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield3)) {
              SearchPane.instance.searchtable.getColumn(oldcustomfield3).setHeaderValue(customfieldtext3.getText());
              SearchPane.instance.searchtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext3.getText());
          }
          if (SearchPane.instance.searchtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield4)) {
              SearchPane.instance.searchtable.getColumn(oldcustomfield4).setHeaderValue(customfieldtext4.getText());
              SearchPane.instance.searchtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext4.getText());          
          }
        }        
        
        SkinManager.instance.setMessageText("default_custom_field_1_1abel", customfieldtext1.getText());
        SkinManager.instance.setMessageText("default_custom_field_2_1abel", customfieldtext2.getText());
        SkinManager.instance.setMessageText("default_custom_field_3_1abel", customfieldtext3.getText());
        SkinManager.instance.setMessageText("default_custom_field_4_1abel", customfieldtext4.getText());       
        
        for (int i = 0; i < MixoutPane.instance.mixouttable.getColumnModel().getColumnCount(); ++i) {
          if (MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield1)) {
              MixoutPane.instance.mixouttable.getColumn(oldcustomfield1).setHeaderValue(customfieldtext1.getText());
              MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext1.getText());
          }
          if (MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield2)) {
              MixoutPane.instance.mixouttable.getColumn(oldcustomfield2).setHeaderValue(customfieldtext2.getText());
              MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext2.getText());
          }
          if (MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield3)) {
              MixoutPane.instance.mixouttable.getColumn(oldcustomfield3).setHeaderValue(customfieldtext3.getText());
              MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext3.getText());
          }
          if (MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield4)) {
              MixoutPane.instance.mixouttable.getColumn(oldcustomfield4).setHeaderValue(customfieldtext4.getText());
              MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext4.getText());
          }
        }
      for (int i = 0; i < RootsUI.instance.rootstable.getColumnModel().getColumnCount(); ++i) {
        if (RootsUI.instance.rootstable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield1)) {
            RootsUI.instance.rootstable.getColumn(oldcustomfield1).setHeaderValue(customfieldtext1.getText());            
            RootsUI.instance.rootstable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext1.getText());
        }
        if (RootsUI.instance.rootstable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield2)) {
            RootsUI.instance.rootstable.getColumn(oldcustomfield2).setHeaderValue(customfieldtext2.getText());            
            RootsUI.instance.rootstable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext2.getText());
        }
        if (RootsUI.instance.rootstable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield3)) {
            RootsUI.instance.rootstable.getColumn(oldcustomfield3).setHeaderValue(customfieldtext3.getText());            
            RootsUI.instance.rootstable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext3.getText());
        }
        if (RootsUI.instance.rootstable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield4)) {
            RootsUI.instance.rootstable.getColumn(oldcustomfield4).setHeaderValue(customfieldtext4.getText());            
            RootsUI.instance.rootstable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext4.getText());
        }
      }
      for (int i = 0; i < SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumnCount(); ++i) {
        if (SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield1)) {
            SuggestedMixesUI.instance.suggestedtable.getColumn(oldcustomfield1).setHeaderValue(customfieldtext1.getText());            
            SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext1.getText());
        }
        if (SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield2)) {
            SuggestedMixesUI.instance.suggestedtable.getColumn(oldcustomfield2).setHeaderValue(customfieldtext2.getText());            
            SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext2.getText());
        }
        if (SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield3)) {
            SuggestedMixesUI.instance.suggestedtable.getColumn(oldcustomfield3).setHeaderValue(customfieldtext3.getText());            
            SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext3.getText());
        }
        if (SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield4)) {
            SuggestedMixesUI.instance.suggestedtable.getColumn(oldcustomfield4).setHeaderValue(customfieldtext4.getText());            
            SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext4.getText());
        }
      }
      for (int i = 0; i < ExcludeUI.instance.excludetable.getColumnModel().getColumnCount(); ++i) {
        if (ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield1)) {
            ExcludeUI.instance.excludetable.getColumn(oldcustomfield1).setHeaderValue(customfieldtext1.getText());            
            ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext1.getText());
        }
        if (ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield2)) {
            ExcludeUI.instance.excludetable.getColumn(oldcustomfield2).setHeaderValue(customfieldtext2.getText());            
            ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext2.getText());
        }
        if (ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield3)) {
            ExcludeUI.instance.excludetable.getColumn(oldcustomfield3).setHeaderValue(customfieldtext3.getText());            
            ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext3.getText());
        }
        if (ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield4)) {
            ExcludeUI.instance.excludetable.getColumn(oldcustomfield4).setHeaderValue(customfieldtext4.getText());            
            ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext4.getText());
        }
      }
      for (int i = 0; i < SyncUI.instance.synctable.getColumnModel().getColumnCount(); ++i) {
        if (SyncUI.instance.synctable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield1)) {
            SyncUI.instance.synctable.getColumn(oldcustomfield1).setHeaderValue(customfieldtext1.getText());            
            SyncUI.instance.synctable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext1.getText());
        }
        if (SyncUI.instance.synctable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield2)) {
            SyncUI.instance.synctable.getColumn(oldcustomfield2).setHeaderValue(customfieldtext2.getText());            
            SyncUI.instance.synctable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext2.getText());
        }
        if (SyncUI.instance.synctable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield3)) {
            SyncUI.instance.synctable.getColumn(oldcustomfield3).setHeaderValue(customfieldtext3.getText());            
            SyncUI.instance.synctable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext3.getText());
        }
        if (SyncUI.instance.synctable.getColumnModel().getColumn(i).getHeaderValue().equals(oldcustomfield4)) {
            SyncUI.instance.synctable.getColumn(oldcustomfield4).setHeaderValue(customfieldtext4.getText());            
            SyncUI.instance.synctable.getColumnModel().getColumn(i).setHeaderValue(customfieldtext4.getText());
        }
      }
      
      if (SearchPane.instance.searchcolumnconfig.columntitles != null) {
          for (int i = 0; i < SearchPane.instance.searchcolumnconfig.columntitles.length; ++i) {
              if (SearchPane.instance.searchcolumnconfig.columntitles[i].equals(oldcustomfield1)) SearchPane.instance.searchcolumnconfig.columntitles[i] =  customfieldtext1.getText();
              if (SearchPane.instance.searchcolumnconfig.columntitles[i].equals(oldcustomfield2)) SearchPane.instance.searchcolumnconfig.columntitles[i] =  customfieldtext2.getText();
              if (SearchPane.instance.searchcolumnconfig.columntitles[i].equals(oldcustomfield3)) SearchPane.instance.searchcolumnconfig.columntitles[i] =  customfieldtext3.getText();
              if (SearchPane.instance.searchcolumnconfig.columntitles[i].equals(oldcustomfield4)) SearchPane.instance.searchcolumnconfig.columntitles[i] =  customfieldtext4.getText();            
          }
      }
      if (MixoutPane.instance.mixoutcolumnconfig.columntitles != null) {
          for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.columntitles.length; ++i) {
              if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i].equals(oldcustomfield1)) {
                  MixoutPane.instance.mixoutcolumnconfig.columntitles[i] =  customfieldtext1.getText();
              }
              if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i].equals(oldcustomfield2)) {
                  MixoutPane.instance.mixoutcolumnconfig.columntitles[i] =  customfieldtext2.getText();
              }
              if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i].equals(oldcustomfield3)) {
                  MixoutPane.instance.mixoutcolumnconfig.columntitles[i] =  customfieldtext3.getText();
              }
              if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i].equals(oldcustomfield4)) {
                  MixoutPane.instance.mixoutcolumnconfig.columntitles[i] =  customfieldtext4.getText();            
              }
          }
      }
      
      if (oldcustomfield1 != null) {
      	UpdateCombobox(CopyFieldsUI.instance.copyfieldfrom, oldcustomfield1, customfieldtext1.getText());
      	UpdateCombobox(CopyFieldsUI.instance.copyfieldfrom, oldcustomfield2, customfieldtext2.getText());
      	UpdateCombobox(CopyFieldsUI.instance.copyfieldfrom, oldcustomfield3, customfieldtext3.getText());
      	UpdateCombobox(CopyFieldsUI.instance.copyfieldfrom, oldcustomfield4, customfieldtext4.getText());

      	UpdateCombobox(CopyFieldsUI.instance.copyfieldto, oldcustomfield1, customfieldtext1.getText());
      	UpdateCombobox(CopyFieldsUI.instance.copyfieldto, oldcustomfield2, customfieldtext2.getText());
      	UpdateCombobox(CopyFieldsUI.instance.copyfieldto, oldcustomfield3, customfieldtext3.getText());
      	UpdateCombobox(CopyFieldsUI.instance.copyfieldto, oldcustomfield4, customfieldtext4.getText());
      	
      	UpdateCombobox(filter1options, oldcustomfield1, customfieldtext1.getText());
      	UpdateCombobox(filter1options, oldcustomfield2, customfieldtext2.getText());
      	UpdateCombobox(filter1options, oldcustomfield3, customfieldtext3.getText());
      	UpdateCombobox(filter1options, oldcustomfield4, customfieldtext4.getText());

      	UpdateCombobox(filter2options, oldcustomfield1, customfieldtext1.getText());
      	UpdateCombobox(filter2options, oldcustomfield2, customfieldtext2.getText());
      	UpdateCombobox(filter2options, oldcustomfield3, customfieldtext3.getText());
      	UpdateCombobox(filter2options, oldcustomfield4, customfieldtext4.getText());

      	UpdateCombobox(filter3options, oldcustomfield1, customfieldtext1.getText());
      	UpdateCombobox(filter3options, oldcustomfield2, customfieldtext2.getText());
      	UpdateCombobox(filter3options, oldcustomfield3, customfieldtext3.getText());
      	UpdateCombobox(filter3options, oldcustomfield4, customfieldtext4.getText());
      }      
      	
      oldcustomfield1 = customfieldtext1.getText();
        oldcustomfield2 = customfieldtext2.getText();
        oldcustomfield3 = customfieldtext3.getText();
        oldcustomfield4 = customfieldtext4.getText();
                       
        
    }
    
    private void UpdateCombobox(JComboBox combobox, String oldstr, String newstr) {
        if (oldstr.equals(newstr)) return;
        for (int i = 0; i < combobox.getItemCount(); ++i) {
            if (combobox.getItemAt(i).toString().equals(oldstr)) {
                boolean selected = (combobox.getSelectedIndex() == i);
                combobox.removeItemAt(i);
                // determine insert index
                int insert_point = 0;
                boolean found = false;
                while ((insert_point < combobox.getItemCount()) && !found) {
                    if (newstr.compareToIgnoreCase((String)combobox.getItemAt(insert_point)) < 0) {
                        found = true;
                    } else {
                        ++insert_point;
                    }
                }
                combobox.insertItemAt(newstr, insert_point);
                if (selected) combobox.setSelectedIndex(insert_point);
                return;
            }            
        }
    }
    
    public static String[] filter_options = null;
    public void PostLoadInit() {
        try {
            javax.swing.SwingUtilities.invokeAndWait(new PostLoadInitThread());
        } catch (Exception e) {
            log.error("PostLoadInit(): error Exception", e);
        }
    }

    public class PostLoadInitThread extends Thread {

        public void run() {
            try {
                CopyFieldsUI.instance.copyfieldfrom.removeAllItems();
                CopyFieldsUI.instance.copyfieldto.removeAllItems();
                String custom1 = customfieldtext1.getText();
                String custom2 = customfieldtext2.getText();
                String custom3 = customfieldtext3.getText();
                String custom4 = customfieldtext4.getText();
                String[] copyfield_options = new String[] {
                        SkinManager.instance.getMessageText("column_title_artist"),
                        SkinManager.instance.getMessageText("column_title_album"),
                        SkinManager.instance.getMessageText("column_title_bpm_accuracy"),
                        SkinManager.instance.getMessageText("column_title_key_accuracy"),
                        SkinManager.instance.getMessageText("column_title_track"),
                        SkinManager.instance.getMessageText("column_title_title"),
                        SkinManager.instance.getMessageText("column_title_remix"),
                        SkinManager.instance.getMessageText("column_title_time"),
                        SkinManager.instance.getMessageText("column_title_time_signature"),
                        SkinManager.instance.getMessageText("column_title_bpm_start"),
                        SkinManager.instance.getMessageText("column_title_bpm_end"),
                        SkinManager.instance.getMessageText("column_title_song_comments"),
                        SkinManager.instance.getMessageText("column_title_filename"),
                        SkinManager.instance.getMessageText("column_title_key_start"),
                        SkinManager.instance.getMessageText("column_title_key_end"),
                        custom1,
                        custom2,
                        custom3,
                        custom4
                    };
                java.util.Arrays.sort(copyfield_options, String.CASE_INSENSITIVE_ORDER);
                for (int i = 0; i < copyfield_options.length; ++i) {
                    CopyFieldsUI.instance.copyfieldfrom.insertItemAt(copyfield_options[i], i);
                    CopyFieldsUI.instance.copyfieldto.insertItemAt(copyfield_options[i], i);
                }        
                MIDIPiano.instance.setupTraining();
                
                setKeyNotationUI();
                
                filter1options.removeAllItems();
                filter2options.removeAllItems();
                filter3options.removeAllItems();
                filter_options = new String[] { SkinManager.instance.getMessageText("column_title_artist"),
                        SkinManager.instance.getMessageText("column_title_album"),
                        SkinManager.instance.getMessageText("column_title_album_cover"),
                        SkinManager.instance.getMessageText("column_title_time"),
                        SkinManager.instance.getMessageText("column_title_bpm"),
                        SkinManager.instance.getMessageText("column_title_song_comments"),
                        SkinManager.instance.getMessageText("column_title_key"),
                        SkinManager.instance.getMessageText("column_title_key_code"),
                        SkinManager.instance.getMessageText("column_title_key_relation"),
                        SkinManager.instance.getMessageText("column_title_bpm_shift"),
                        SkinManager.instance.getMessageText("column_title_bpm_diff"),
                        SkinManager.instance.getMessageText("column_title_num_plays"),
                        SkinManager.instance.getMessageText("column_title_num_mixouts"),
                        SkinManager.instance.getMessageText("column_title_time_signature"),
                        SkinManager.instance.getMessageText("column_title_date_added"),
                        SkinManager.instance.getMessageText("column_title_styles"),
                        SkinManager.instance.getMessageText("column_title_rating"),
                        customfieldtext1.getText(),
                        customfieldtext2.getText(),
                        customfieldtext3.getText(),
                        customfieldtext4.getText()
                    };
                java.util.Arrays.sort(filter_options, String.CASE_INSENSITIVE_ORDER);
                for (int i = 0; i < filter_options.length; ++i) {
                    filter1options.insertItemAt(filter_options[i], i);
                    filter2options.insertItemAt(filter_options[i], i);
                    filter3options.insertItemAt(filter_options[i], i);
                }        
                if (filter1preset != null) setFilter(filter1preset, filter1options);
                else setFilter(SkinManager.instance.getMessageText("column_title_artist"), filter1options);
                if (filter2preset != null) setFilter(filter2preset, filter2options);
                else setFilter(SkinManager.instance.getMessageText("column_title_album"), filter2options);
                if (filter3preset != null) setFilter(filter3preset, filter3options);
                else setFilter(SkinManager.instance.getMessageText("column_title_key"), filter3options);
                filter1 = new Filter(filter1list, filter1options.getSelectedItem().toString(), null, filter1clear, filter1selectall, null);
                filter2 = new Filter(filter2list, filter2options.getSelectedItem().toString(), filter1, filter2clear, filter2selectall, filter2disable);
                filter3 = new Filter(filter3list, filter3options.getSelectedItem().toString(), filter2, filter3clear, filter3selectall, filter3disable);
                filter1.setChild(filter2);
                filter2.setChild(filter3);
                if (RapidEvolution.first_time_to_run) {
                    filter3disable.setSelected(true);
                    SkinManager.instance.ProcessCondition("options_song_filter_3_disable");
                }
                SkinManager.instance.setText("filter_1_values_label", filter1options.getSelectedItem().toString());
                SkinManager.instance.setText("filter_2_values_label", filter2options.getSelectedItem().toString());
                SkinManager.instance.setText("filter_3_values_label", filter3options.getSelectedItem().toString());
                if (enablefilter.isSelected()) {
                    usefiltering.setVisible(!filteraffectedbysearch.isSelected());
                } else {
                    usefiltering.setVisible(false);
                }            
                if (filteraffectedbysearch.isSelected()) {
                    usefiltering.setVisible(false);
                } else {
                    usefiltering.setVisible(enablefilter.isSelected());
                }        
                if (!filtersingleselectmode.isSelected()) {
                    filter1list.setSelectionModel(new ToggleSelectionModel());
                    filter2list.setSelectionModel(new ToggleSelectionModel());
                    filter3list.setSelectionModel(new ToggleSelectionModel());          
                } else {
                    filter1list.setSelectionModel(new DefaultListSelectionModel());
                    filter2list.setSelectionModel(new DefaultListSelectionModel());
                    filter3list.setSelectionModel(new DefaultListSelectionModel());                    
                }          
                
                SkinManager.instance.getFrame("main_frame").setAlwaysOnTop(alwaysontop.isSelected());
                
            } catch (Exception e) {
                log.error("run(): error Exception", e);
            }
        }
        
    }
    
    private void setFilter(String filter, REComboBox filtercombo) {
        for (int i = 0; i < filter_options.length; ++i) {
            if (filter_options[i].equalsIgnoreCase(filter)) {
                filtercombo.setSelectedIndex(i);
            }
        }        
    }

    private void setupActionListeners() {
        optionsokbutton.addActionListener(this);
        show_advanced_key_information.addActionListener(this);
        keyformatcombo.addActionListener(this);
        option_bpm_blink_rate.addActionListener(this);
        colorselectionscombo.addActionListener(this);
        refresh_available_skins_button.addActionListener(this);
        load_skin_button.addActionListener(this);
        applysearchcolumns.addActionListener(this);
        skin_reset_button.addActionListener(this);
        enabledebug.addActionListener(this);
        system_info_button.addActionListener(this);
        applymixoutcolumns.addActionListener(this);
        mixouttableautoscroll.addActionListener(this);
        disableautocomplete.addActionListener(this);
        searchtableautoscroll.addActionListener(this);
        donotsharemixcomments.addActionListener(this);
        lookandfeelcombo.addActionListener(this);
        showprevioustrack.addActionListener(this);
        customfieldtext1.addKeyListener(new customkeylistener());
        customfieldtext2.addKeyListener(new customkeylistener());
        customfieldtext3.addKeyListener(new customkeylistener());
        customfieldtext4.addKeyListener(new customkeylistener());
        disablekeylockfunctionality.addActionListener(this);
        useosplayer.addActionListener(this);
        disable_multiple_delect.addActionListener(this);
        browsemediaplayer.addActionListener(this);
        browsemusicdirectory.addActionListener(this);
        scanNowButton.addActionListener(this);
        resetmediaplayer.addActionListener(this);
        disabletooltips.addActionListener(this);
        defaultsearchcolumns.addActionListener(this);
        defaultmixoutcolumns.addActionListener(this);
        disableonline.addActionListener(this);
        mididevicescombo.addActionListener(this);
        row1chordtype.addActionListener(this);
        row2chordtype.addActionListener(this);
        songdisplayartist.addActionListener(this);
        songdisplayalbum.addActionListener(this);
        songdisplaytrack.addActionListener(this);
        songdisplaysongname.addActionListener(this);
        songdisplayremixer.addActionListener(this);
        songdisplaystartbpm.addActionListener(this);
        songdisplayendbpm.addActionListener(this);
        songdisplaystartkey.addActionListener(this);
        songdisplayendkey.addActionListener(this);
        songdisplaytracktime.addActionListener(this);
        songdisplaytimesig.addActionListener(this);
        songdisplayfield1.addActionListener(this);
        songdisplayfield2.addActionListener(this);
        songdisplayfield3.addActionListener(this);
        songdisplayfield4.addActionListener(this);
        restoreFromServer.addActionListener(this);
        filter1options.addActionListener(this);
        filter2options.addActionListener(this);
        filter3options.addActionListener(this);
        filter1clear.addActionListener(this);
        filter2clear.addActionListener(this);
        filter3clear.addActionListener(this);
        filter1selectall.addActionListener(this);
        filter2selectall.addActionListener(this);
        filter3selectall.addActionListener(this);
        filter2disable.addActionListener(this);
        filter3disable.addActionListener(this);
        enablefilter.addActionListener(this);
        filterusestyles.addActionListener(this);
        filteraffectedbysearch.addActionListener(this);
        filtersingleselectmode.addActionListener(this);
        hideAdvancedFeatures.addActionListener(this);
        colorChooserButton.addActionListener(this);
        alwaysontop.addActionListener(this);
    }

    public class UpdateSongFormat extends Thread {
        public UpdateSongFormat() { }
        public void run()  {
          SongLinkedList siter = SongDB.instance.SongLL;
          OldSongValues oldValues = new OldSongValues(siter);
          while (siter != null) {
            RapidEvolutionUI.instance.UpdateSongRoutine(siter, oldValues, false);
            siter = siter.next;
          }
          SongTrailUI.instance.RedrawSongTrailRoutine();
          MixGeneratorUI.instance.DisplayMix(MixGeneratorUI.instance.current_mix);
        }
      }
    
    
    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == optionsokbutton) {
        setVisible(false);
        MixshareClient.instance.dontconnectinoptions = false;
        SongDB.instance.dirtybit = true;
        if (songdisplayformatchanged) new UpdateSongFormat().start();
        try {
            int thumbnailwidth = Integer.parseInt(albumcoverthumbwidth.getText());
            if ((thumbnailwidth > 0) && (thumbnailwidth != myImageIcon.icon_size)) {
                myImageIcon.icon_size = thumbnailwidth;
                ImageIconFactory.reset();
                filter1.changedIconSize();
                filter2.changedIconSize();
                filter3.changedIconSize();
                if (SearchPane.instance.searchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_ALBUM_COVER)) {          
                    SearchPane.instance.searchtable.setRowHeight(thumbnailwidth + 10);
                } else {
                    SearchPane.instance.searchtable.setRowHeight(SkinManager.instance.getTableRowHeight());
                }
                if (MixoutPane.instance.mixoutcolumnconfig.containsColumnType(ColumnConfig.COLUMN_ALBUM_COVER)) {          
                    MixoutPane.instance.mixouttable.setRowHeight(thumbnailwidth + 10);
                } else {
                    MixoutPane.instance.mixouttable.setRowHeight(SkinManager.instance.getTableRowHeight());
                }                
            }
        } catch (Exception e) { 
            log.error("actionPerformed(): error", e);
        }
      } else if (ae.getSource() == refresh_available_skins_button) {
        RedrawAvailableSkins();
      } else if (ae.getSource() == load_skin_button) {
        int selection = available_skins_table.getSelectedRow();
        if (selection >= 0) {
          File file = (File)available_skins_table.getValueAt(selection, num_available_skin_table_columns);
          SkinManager.instance.ChangeSkins(file);          
        }
      } else if ((ae.getSource() == filter2disable) || (ae.getSource() == filter3disable)) {          
          SkinManager.instance.setText("filter_1_values_label", filter1options.getSelectedItem().toString());
          SkinManager.instance.setText("filter_2_values_label", filter2options.getSelectedItem().toString());
          SkinManager.instance.setText("filter_3_values_label", filter3options.getSelectedItem().toString());
      } else if (ae.getSource() == filteraffectedbysearch) {
          if (filteraffectedbysearch.isSelected()) {
              usefiltering.setVisible(false);
          } else {
              usefiltering.setVisible(enablefilter.isSelected());
              filter1.updateList();
          }
      } else if (ae.getSource() == enablefilter) {
          if (enablefilter.isSelected()) {
              SkinManager.instance.setText("filter_1_values_label", filter1options.getSelectedItem().toString());
              SkinManager.instance.setText("filter_2_values_label", filter2options.getSelectedItem().toString());
              SkinManager.instance.setText("filter_3_values_label", filter3options.getSelectedItem().toString());
              filter1.changeFilterDataType(filter1options.getSelectedItem().toString());
              if (!filter2disable.isSelected()) filter2.changeFilterDataType(filter2options.getSelectedItem().toString());
              if (!filter2disable.isSelected() && !filter3disable.isSelected()) filter3.changeFilterDataType(filter3options.getSelectedItem().toString());
              filter1.updateList();
              usefiltering.setVisible(!filteraffectedbysearch.isSelected());
          } else {
              usefiltering.setVisible(false);
          }
      } else if (ae.getSource() == filter1selectall) {
          filter1.removeSelectionListeners();
          DefaultListModel dlm = (DefaultListModel) filter1list.getModel();
          int[] indices = new int[dlm.size()];
          for (int i = 0; i < indices.length; ++i)
              indices[i] = i;
          filter1list.setSelectedIndices(indices);
          filter1.addSelectionListener();
          filter1.valueChanged(null, true);        
      } else if (ae.getSource() == filter2selectall) {
          filter2.removeSelectionListeners();
          DefaultListModel dlm = (DefaultListModel) filter2list.getModel();
          int[] indices = new int[dlm.size()];
          for (int i = 0; i < indices.length; ++i)
              indices[i] = i;
          filter2list.setSelectedIndices(indices);
          filter2.addSelectionListener();
          filter2.valueChanged(null, true);        
          
      } else if (ae.getSource() == filter3selectall) {
          filter3.removeSelectionListeners();
              DefaultListModel dlm = (DefaultListModel) filter3list.getModel();
              int[] indices = new int[dlm.size()];
              for (int i = 0; i < indices.length; ++i)
                  indices[i] = i;
              filter3list.setSelectedIndices(indices);              
              filter3.addSelectionListener();
              filter3.valueChanged(null, true);        
      } else if (ae.getSource() == alwaysontop) {
          SkinManager.instance.getFrame("main_frame").setAlwaysOnTop(alwaysontop.isSelected());
      } else if (ae.getSource() == colorChooserButton) {
          new ColorPicker(colorchooser).show();
      } else if (ae.getSource() == hideAdvancedFeatures) {
          checkAdvancedFeatures();
      } else if (ae.getSource() == filtersingleselectmode) {
          if (!filtersingleselectmode.isSelected()) {
              filter1list.setSelectionModel(new ToggleSelectionModel());
              filter2list.setSelectionModel(new ToggleSelectionModel());
              filter3list.setSelectionModel(new ToggleSelectionModel());          
          } else {
              filter1list.setSelectionModel(new DefaultListSelectionModel());
              filter2list.setSelectionModel(new DefaultListSelectionModel());
              filter3list.setSelectionModel(new DefaultListSelectionModel());                    
          }          
      } else if (ae.getSource() == filterusestyles) {
          filter1.updateList();
      } else if (ae.getSource() == filter1clear) {
          filter1list.clearSelection();
      } else if (ae.getSource() == filter2clear) {
          filter2list.clearSelection();
      } else if (ae.getSource() == filter3clear) {
          filter3list.clearSelection();
      } else if (ae.getSource() == filter1options) {
          if (filter1 != null) {
              SkinManager.instance.setText("filter_1_values_label", filter1options.getSelectedItem().toString());
              filter1.changeFilterDataType(filter1options.getSelectedItem().toString());
          }
      } else if (ae.getSource() == filter2options) {
          if (filter2 != null) {
              SkinManager.instance.setText("filter_2_values_label", filter2options.getSelectedItem().toString());
              filter2.changeFilterDataType(filter2options.getSelectedItem().toString());
          }
      } else if (ae.getSource() == filter3options) {
          if (filter3 != null) {
              SkinManager.instance.setText("filter_3_values_label", filter3options.getSelectedItem().toString());
              filter3.changeFilterDataType(filter3options.getSelectedItem().toString());
          }
      } else if (ae.getSource() == skin_reset_button) {
        SkinManager.instance.Reset();
      } else if (ae.getSource() == colorselectionscombo) {
        if (colorselectionscombo.getSelectedIndex() == 0) colorchooser.
                setColor(SkinManager.instance.getColor("song_background_disabled"));
        if (colorselectionscombo.getSelectedIndex() == 1) colorchooser.
        		setColor(SkinManager.instance.getColor("song_background_mixouts"));
        if (colorselectionscombo.getSelectedIndex() == 2) colorchooser.
                setColor(SkinManager.instance.getColor("song_background_in_key_with_mixouts"));
        if (colorselectionscombo.getSelectedIndex() == 3) colorchooser.
                setColor(SkinManager.instance.getColor("song_background_in_key"));
        if (colorselectionscombo.getSelectedIndex() == 4) colorchooser.
                setColor(SkinManager.instance.getColor("song_background_with_mixouts"));
        if (colorselectionscombo.getSelectedIndex() == 5) colorchooser.
                setColor(SkinManager.instance.getColor("song_background_default"));
        if (colorselectionscombo.getSelectedIndex() == 6) colorchooser.
                setColor(SkinManager.instance.getColor("mixout_background_disabled"));
        if (colorselectionscombo.getSelectedIndex() == 7) colorchooser.
                setColor(SkinManager.instance.getColor("mixout_background_ranked_with_mixouts"));
        if (colorselectionscombo.getSelectedIndex() == 8) colorchooser.
                setColor(SkinManager.instance.getColor("mixout_background_ranked"));
        if (colorselectionscombo.getSelectedIndex() == 9) colorchooser.
                setColor(SkinManager.instance.getColor("mixout_background_with_mixouts"));
        if (colorselectionscombo.getSelectedIndex() == 10) colorchooser.
                setColor(SkinManager.instance.getColor("mixout_background_addon_ranked"));
        if (colorselectionscombo.getSelectedIndex() == 11) colorchooser.
                setColor(SkinManager.instance.getColor("mixout_background_addon"));
        if (colorselectionscombo.getSelectedIndex() == 12) colorchooser.
                setColor(SkinManager.instance.getColor("mixout_background_default"));
        if (colorselectionscombo.getSelectedIndex() == 13) colorchooser.
                setColor(SkinManager.instance.getColor("style_background_selected"));
        if (colorselectionscombo.getSelectedIndex() == 14) colorchooser.
                setColor(SkinManager.instance.getColor("style_background_of_selected_song"));
        if (colorselectionscombo.getSelectedIndex() == 15) colorchooser.
                setColor(SkinManager.instance.getColor("style_background_of_current_song"));
        if (colorselectionscombo.getSelectedIndex() == 16) colorchooser.
        		setColor(SkinManager.instance.getColor("style_background_excluded"));
        if (colorselectionscombo.getSelectedIndex() == 17) colorchooser.
        		setColor(SkinManager.instance.getColor("style_background_required"));
      } else if (ae.getSource() == applysearchcolumns) {
          SearchPane.instance.RecreateSearchColumns();
          if (disable_multiple_delect.isSelected()) 
              SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          else SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);            
          SyncUI.instance.RecreateSyncColumns();
          ExcludeUI.instance.RecreateExcludeColumns();
          AddMatchQueryUI.instance.RecreateMatchColumns();
          EditMatchQueryUI.instance.RecreateMatchColumns2();
      } else if (ae.getSource() == useosplayer) {
        if (useosplayer.isSelected()) {
          SkinManager.instance.setEnabled("media_player_button", false);
          SkinManager.instance.setVisible("media_player_button", false);
          AudioPlayer.stop();
          MediaPlayerUI.instance.songplayingtracker.setValue(0);
        } else {
          SkinManager.instance.setEnabled("media_player_button", true);
          SkinManager.instance.setVisible("media_player_button", true);
        }
      } else if (ae.getSource() == disable_multiple_delect) {
          if (disable_multiple_delect.isSelected()) 
          SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          else SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      } else if (ae.getSource() == row1chordtype) {
            MIDIPiano.instance.chordrow1 = (Vector)MIDIPiano.instance.chordbook.get(row1chordtype.getSelectedIndex());
      } else if (ae.getSource() == row2chordtype) {
            MIDIPiano.instance.chordrow2 = (Vector)MIDIPiano.instance.chordbook.get(row2chordtype.getSelectedIndex());
      } else if (ae.getSource() == applymixoutcolumns) {
          MixoutPane.instance.RecreateMixoutColumns();
          RootsUI.instance.RecreateRootsColumns();
          SuggestedMixesUI.instance.RecreateSuggestedColumns();
      } else if (ae.getSource() == disabletooltips) {
        if (disabletooltips.isSelected()) ToolTipManager.sharedInstance().setEnabled(false);
        else ToolTipManager.sharedInstance().setEnabled(true);
      } else if (ae.getSource() == disableautocomplete) {
          setupAutocomplete(!disableautocomplete.isSelected());
      } else if (ae.getSource() == defaultmixoutcolumns) {
        ClearSelectedMixoutColumns();
      } else if (ae.getSource() == defaultsearchcolumns) {
        ClearSelectedSearchColumns();
      } else if (ae.getSource() == searchtableautoscroll){
          SearchPane.instance.searchtable.setAutoscrolls(searchtableautoscroll.isSelected());
      } else if (ae.getSource() == mixouttableautoscroll) {
          MixoutPane.instance.mixouttable.setAutoscrolls(mixouttableautoscroll.isSelected());
      } else if (ae.getSource() == keyformatcombo) {
           if (keyformatcombo.getSelectedIndex() == 1) {
             KeyFormatUI.dflatoption.setSelected(true);
             KeyFormatUI.eflatoption.setSelected(true);
             KeyFormatUI.gflatoption.setSelected(true);
             KeyFormatUI.aflatoption.setSelected(true);
             KeyFormatUI.bflatoption.setSelected(true);
           } else if ((keyformatcombo.getSelectedIndex() == 0)) {
             KeyFormatUI.csharpoption.setSelected(true);
             KeyFormatUI.dsharpoption.setSelected(true);
             KeyFormatUI.fsharpoption.setSelected(true);
             KeyFormatUI.gsharpoption.setSelected(true);
             KeyFormatUI.asharpoption.setSelected(true);
           }
           setKeyNotationUI();
      } else if (ae.getSource() == donotsharemixcomments) {
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
              for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
                  iter.mixout_servercache[i] = false;
              }
              iter = iter.next;
          }
      } else if (ae.getSource() == enabledebug) {
          if (enabledebug.isSelected()) {
              Logger.getRoot().setLevel(Level.DEBUG);              
          } else {
              Logger.getRoot().setLevel(RapidEvolution.original_log_level);              
          }          
      } else if (ae.getSource() == resetmediaplayer) {
          OSMediaPlayer = null;
      } else if (ae.getSource() == scanNowButton) {
    	  new RootMusicDirectoryScanner().start();          	  
      } else if (ae.getSource() == browsemusicdirectory) {
          JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fc.setMultiSelectionEnabled(false);
          int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
          File tmp = fc.getSelectedFile();
          if (returnVal == JFileChooser.APPROVE_OPTION) {
        	  rootMusicDirectory.setText(tmp.getAbsolutePath());
          }    	  
      } else if (ae.getSource() == browsemediaplayer) {
            JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
            if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
            File tmp = fc.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
             OSMediaPlayer = (String)tmp.getAbsolutePath();
            }
      } else if (ae.getSource() == mididevicescombo) {
          MIDIPiano.instance.ChangeMidiDevice();
      } else if ((ae.getSource() == songdisplayartist) || (ae.getSource() == songdisplayalbum) || (ae.getSource() == songdisplaytrack) || (ae.getSource() == songdisplaysongname) || (ae.getSource() == songdisplayremixer) || (ae.getSource() == songdisplaystartbpm) || (ae.getSource() == songdisplayendbpm) || (ae.getSource() == songdisplaystartkey) || (ae.getSource() == songdisplayendkey) || (ae.getSource() == songdisplaytracktime) || (ae.getSource() == songdisplaytimesig) || (ae.getSource() == songdisplayfield1) || (ae.getSource() == songdisplayfield2) || (ae.getSource() == songdisplayfield3) || (ae.getSource() == songdisplayfield4)) {
        songdisplayformatchanged = true;
      } else if (ae.getSource() == showprevioustrack) {
          SkinManager.instance.getFrame("main_frame").repaint();
//          SwingUtilities.updateComponentTreeUI(SkinManager.instance.getFrame("main_frame"));
      } else if (ae.getSource() == option_bpm_blink_rate) {
        if (option_bpm_blink_rate.isSelected()) {
          RapidEvolution.instance.setActualBpm(RapidEvolution.instance.getActualBpm());
        } else {
          SkinManager.instance.resetBlinkRate();
        }
    } else if (ae.getSource() == disablekeylockfunctionality) {
      if (disablekeylockfunctionality.isSelected()) RapidEvolutionUI.instance.keylockcurrentsong.setSelected(false);
      SearchPane.instance.RecalculateCurrentBpmKey();
      SkinManager.instance.getFrame("main_frame").repaint();
    } else if (ae.getSource() == restoreFromServer) {
      new RestoreFromServerThread().start();
      //RapidEvolutionUI.instance.RestoreFromServer();
    } else if (ae.getSource() == show_advanced_key_information) {
        setKeyNotationUI();
    } else if (ae.getSource() == system_info_button) {
        StringBuffer text = new StringBuffer();
        text.append("Rapid Evolution: ");
        text.append(RapidEvolution.versionString);        
        text.append("\n");
        text.append("Detected OS: ");
        text.append(System.getProperty("os.name"));
        text.append("\n");
        text.append("JVM Version: ");
        text.append(System.getProperty("java.version"));
        text.append("\n"); 
        try {
            String QTVersion = QTUtil.getVersionString();
            if (QTVersion != null) {
                text.append(QTVersion);
                text.append("\n");
            }
        } catch (java.lang.Error e) { 
        } catch (Exception e) { }            
        text.append("Max Memory: ");
        text.append(String.valueOf(Runtime.getRuntime().maxMemory() / 1048576));
        text.append("mb\n");                                
        text.append("Used Memory: ");
        text.append(String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
        text.append("mb\n");
        IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                text.toString(),
                "system info",
              IOptionPane.INFORMATION_MESSAGE);        
    } else if (ae.getSource() == lookandfeelcombo) {
          try {
            LookAndFeelManager.saveCurrentLookAndFeel();

            String looknfeel = RapidEvolutionUI.inf[lookandfeelcombo.getSelectedIndex()].getName();
            LookAndFeelManager.setLookAndFeel(looknfeel);
            
          } catch (Exception e) {
             log.error("actionPerformed(): error", e);
          }
      }

    }
    
    public boolean PreDisplay() {
      songdisplayformatchanged = false;
      ClearSelectedSearchColumns();
      SetSelectedSearchColumns();
      ClearSelectedMixoutColumns();
      SetSelectedMixoutColumns();
      return true;
    }

    public void PostDisplay() {
      MixshareClient.instance.dontconnectinoptions = true;
    }

    MyContentHandler mySAXSkinHandler = new MyContentHandler();
    Vector current_sax_skin_row = null;

    public SortTableModel makeAvailableSkinModel() {
      try {
        Vector data = new Vector();
        Vector searchfiles = new Vector();
        File basedir = new File("skins");
        FileUtil.RecurseFileTree2(basedir, searchfiles);
        for (int i = 0; i < searchfiles.size(); ++i) {
          try {
            File file = (File) searchfiles.get(i);
            if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
              current_sax_skin_row = new Vector();

              // Create a parser factory and use it to create a parser
              SAXParserFactory parserFactory = SAXParserFactory.newInstance();
              SAXParser saxParser = parserFactory.newSAXParser();
              saxParser.parse(file, mySAXSkinHandler);
              if (current_sax_skin_row.size() > 0) {

                  current_sax_skin_row.add(file.getAbsoluteFile().toString().
                                           substring(basedir.getAbsolutePath().
                                                     length() + 1));
                  current_sax_skin_row.add(file);
                  data.add(current_sax_skin_row);
              }
            }
          } catch (Exception e) { }
        }
        Vector columnames = new Vector();
        columnames.add(new rapid_evolution.comparables.MyString(SkinManager.instance.getMessageText("skin_title_text")));
        columnames.add(new rapid_evolution.comparables.MyString(SkinManager.instance.getMessageText("skin_author_text")));
        columnames.add(new rapid_evolution.comparables.MyString(SkinManager.instance.getMessageText("skin_locale_text")));
        columnames.add(new rapid_evolution.comparables.MyString(SkinManager.instance.getMessageText("skin_description_text")));
        columnames.add(new rapid_evolution.comparables.MyString(SkinManager.instance.getMessageText("skin_filename_text")));
        columnames.add("id code");
        DefaultSortTableModel result = new DefaultSortTableModel(data, columnames) {
          public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
          }
        };
        return result;
      } catch (Exception e) {
        log.error("makeAvailableSkinModel(): error", e);
      }
      return null;
    }

    int num_available_skin_table_columns = 5;

    public void RedrawAvailableSkins() {

      available_skins_table.setModel(makeAvailableSkinModel());
      available_skins_table.getColumnModel().getColumn(num_available_skin_table_columns).setResizable(false);
      available_skins_table.getColumnModel().getColumn(num_available_skin_table_columns).setMinWidth(0);
      available_skins_table.getColumnModel().getColumn(num_available_skin_table_columns).setMaxWidth(0);
      available_skins_table.getColumnModel().getColumn(num_available_skin_table_columns).setWidth(0);

      available_skins_table.getColumnModel().getColumn(0).setPreferredWidth(140);
      available_skins_table.getColumnModel().getColumn(0).setWidth(140);
      available_skins_table.getColumnModel().getColumn(2).setPreferredWidth(50);
      available_skins_table.getColumnModel().getColumn(2).setWidth(50);
      available_skins_table.getColumnModel().getColumn(3).setPreferredWidth(230);
      available_skins_table.getColumnModel().getColumn(3).setWidth(330);
      available_skins_table.getColumnModel().getColumn(4).setPreferredWidth(230);
      available_skins_table.getColumnModel().getColumn(4).setWidth(330);

      available_skins_table.sort(availableskinconfig);
      
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              com.mixshare.rapid_evolution.ui.swing.table.ColumnResizer.adjustColumnPreferredWidths(available_skins_table);
              available_skins_table.revalidate();
          }
      });
    }

    public class MyContentHandler extends DefaultHandler {
      public void startElement(String uri, String localName, String qName, Attributes atts) {
        if (qName.equalsIgnoreCase("rapid-evolution-ui")) {
            String redirect = atts.getValue("redirect");
            if ((redirect == null) || redirect.equals("")) {
                current_sax_skin_row.add(new rapid_evolution.comparables.MyString(atts.getValue("title")));
                current_sax_skin_row.add(new rapid_evolution.comparables.MyString(atts.getValue("author")));
                current_sax_skin_row.add(new rapid_evolution.comparables.MyString(atts.getValue("locale")));
                current_sax_skin_row.add(new rapid_evolution.comparables.MyString(atts.getValue("description")));                
            }
        }
      }
      public void endElement(String uri, String localName, String qName) {
      }
      public void characters(char[ ] chars, int start, int length) {
      }
    }
    
    public void setupAutocomplete(boolean enabled) {
        if (enabled) {
            AddSongsUI.instance.addsongsartistfield.setFieldIndex(RapidEvolution.getMusicDatabase().getArtistIndex());
            AddSongsUI.instance.addsongsalbumfield.setFieldIndex(RapidEvolution.getMusicDatabase().getAlbumIndex());
            if (donotclearuserfield1whenadding.isSelected()) AddSongsUI.instance.addcustomfieldtext1.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom1Index());
            else AddSongsUI.instance.addcustomfieldtext1.setFieldIndex(null);
            if (donotclearuserfield2whenadding.isSelected()) AddSongsUI.instance.addcustomfieldtext2.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom2Index());
            else AddSongsUI.instance.addcustomfieldtext2.setFieldIndex(null);
            if (donotclearuserfield3whenadding.isSelected()) AddSongsUI.instance.addcustomfieldtext3.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom3Index());
            else AddSongsUI.instance.addcustomfieldtext3.setFieldIndex(null);
            if (donotclearuserfield4whenadding.isSelected()) AddSongsUI.instance.addcustomfieldtext4.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom4Index());
            else AddSongsUI.instance.addcustomfieldtext4.setFieldIndex(null);

            EditSongUI.instance.editsongsartistfield.setFieldIndex(RapidEvolution.getMusicDatabase().getArtistIndex());
            EditSongUI.instance.editsongsalbumfield.setFieldIndex(RapidEvolution.getMusicDatabase().getAlbumIndex());
            if (donotclearuserfield1whenadding.isSelected()) EditSongUI.instance.editcustomfieldtext1.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom1Index());
            else EditSongUI.instance.editcustomfieldtext1.setFieldIndex(null);
            if (donotclearuserfield2whenadding.isSelected()) EditSongUI.instance.editcustomfieldtext2.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom2Index());
            else EditSongUI.instance.editcustomfieldtext2.setFieldIndex(null);
            if (donotclearuserfield3whenadding.isSelected()) EditSongUI.instance.editcustomfieldtext3.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom3Index());
            else EditSongUI.instance.editcustomfieldtext3.setFieldIndex(null);
            if (donotclearuserfield4whenadding.isSelected()) EditSongUI.instance.editcustomfieldtext4.setFieldIndex(RapidEvolution.getMusicDatabase().getCustom4Index());
            else EditSongUI.instance.editcustomfieldtext4.setFieldIndex(null);

        } else {
            AddSongsUI.instance.addsongsartistfield.setFieldIndex(null);
            AddSongsUI.instance.addsongsalbumfield.setFieldIndex(null);
            AddSongsUI.instance.addcustomfieldtext1.setFieldIndex(null);
            AddSongsUI.instance.addcustomfieldtext2.setFieldIndex(null);
            AddSongsUI.instance.addcustomfieldtext3.setFieldIndex(null);
            AddSongsUI.instance.addcustomfieldtext4.setFieldIndex(null);

            EditSongUI.instance.editsongsartistfield.setFieldIndex(null);
            EditSongUI.instance.editsongsalbumfield.setFieldIndex(null);
            EditSongUI.instance.editcustomfieldtext1.setFieldIndex(null);
            EditSongUI.instance.editcustomfieldtext2.setFieldIndex(null);
            EditSongUI.instance.editcustomfieldtext3.setFieldIndex(null);
            EditSongUI.instance.editcustomfieldtext4.setFieldIndex(null);

        }
    }
    
    public void setKeyNotationUI() {
        if (show_advanced_key_information.isSelected())
            SearchPane.instance.keyfield.setColumns(10);
        else
            SearchPane.instance.keyfield.setColumns(3);
        Key.invalidateCache();
        KeyCode.invalidateCache();
        SongLinkedList siter = SongDB.instance.SongLL;
        while (siter != null) {
            siter.getKey().invalidate();
            if (OptionsUI.instance.songdisplaystartkey.isSelected() || OptionsUI.instance.songdisplayendkey.isSelected()) {
                siter.calculateSongDisplayIds();
            }
            siter = siter.next;
        }
        SearchPane.instance.keyfield.setText(Key.getKey(SearchPane.instance.keyfield.getText()).toString());
        SwingUtilities.updateComponentTreeUI(SearchPane.instance.keyfield.getParent());  
    }
    
    public void checkAdvancedFeatures() {
        if (hideAdvancedFeatures.isSelected()) {
            SkinManager.instance.setVisible("view_excludes_button", false);
            RapidEvolutionUI.instance.addexcludebutton.setVisible(false);
            RapidEvolutionUI.instance.savelistbutton.setVisible(false);
            SearchPane.instance.showallradio.setSelected(true);
            SearchPane.instance.shownonvinylradio.setSelected(false);
            SearchPane.instance.showvinylradio.setSelected(false);
            SkinManager.instance.setVisible("search_filter_results_button", false);
            //StylesPane.instance.dynamixstylescheckbox.setSelected(false);
            //StylesPane.instance.dynamixstylescheckbox.setVisible(false);
            //SkinManager.instance.setVisible("current_song_mixes_button", false);
           //SkinManager.instance.setVisible("current_song_roots_button", false);
            //SkinManager.instance.setVisible("current_song_sync_button", false);
        } else {
            SkinManager.instance.setVisible("view_excludes_button", true);
            RapidEvolutionUI.instance.addexcludebutton.setVisible(true);            
            RapidEvolutionUI.instance.savelistbutton.setVisible(true);
            SkinManager.instance.setVisible("search_filter_results_button", true);
            //StylesPane.instance.dynamixstylescheckbox.setVisible(true);
            //SkinManager.instance.setVisible("current_song_mixes_button", true);
            //SkinManager.instance.setVisible("current_song_roots_button", true);
            //SkinManager.instance.setVisible("current_song_sync_button", true);            
        }
        
    }
    
    
}
