package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.AudioLib;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.audio.BPMTapper;
import rapid_evolution.audio.adddetectfilethread;
import rapid_evolution.filefilters.InputFileFilter;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.ui.main.AddIdentifyThread;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.StylesUI;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.audio.dsps.rga.RGAData;
import com.mixshare.rapid_evolution.audio.tags.TagManager;
import com.mixshare.rapid_evolution.library.RootMusicExcludeList;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.slider.MediaSlider;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class AddSongsUI extends REDialog implements ActionListener, FocusListener {

    private static Logger log = Logger.getLogger(AddSongsUI.class);
    
    public AddSongsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
        addsongkeyprogress_ui = new AddSongKeyProgressUI("add_songs_detect_key_progress_dialog");
        addsongsalbumcover_ui = new AlbumCoverUI("add_songs_album_cover_dialog");
    }

    public static AddSongsUI instance = null;
    public JButton addsongsokbutton = new REButton();
    public JButton addsongscancelbutton = new REButton();
    public AutoTextField addsongsartistfield = new AutoTextField();
    public AutoTextField addsongsalbumfield = new AutoTextField();
    public JTextField addsongstrackfield = new RETextField();
    public JTextField addsongstimefield = new RETextField();
    public JTextField addsongstimesignaturefield = new RETextField();
    private RGAData addsongsrga = null;
    private int addsongsbeatintensity = 0;
    public JTextField addsongstitlefield = new RETextField();
    public JTextField addsongsstartbpmfield = new RETextField();
    public JTextField addsongsendbpmfield = new RETextField();
    public JTextField addsongsstartkeyfield = new RETextField();
    public JTextField addsongsendkeyfield = new RETextField();
    public JTextArea addsongscommentsfield = new RETextArea();
    public JTextField addsongsfilenamefield = new RETextField();
    public JCheckBox addsongsvinylonly = new RECheckBox();
    public JCheckBox addsongsnonvinylonly = new RECheckBox();
    public JCheckBox addsongsdisabled = new RECheckBox();
    public JButton addsongsbrowsebutton = new REButton();
    public JButton addsongsmorebutton = new REButton();
    public JButton addallbutton = new REButton();
    public JButton addsongsskipbutton = new REButton();
    public JButton addsongsdetectkeybutton = new REButton();
    public JButton addsongsplaybutton = new REButton();
    public JButton addsongswritetagbutton = new REButton();
    public JButton addsongsreadtagbutton = new REButton();
    public JButton addsongsrenamebutton = new REButton();
    public JTextField addsongsremixerfield = new RETextField();
    public RETree addsongstylestree = new RETree();
    public JButton addsongsclearstylesbt = new REButton();
    public JButton addsongsaddstylebt = new REButton();
    public JButton addsongseditstylebt = new REButton();
    public JButton addsongsdeletestylebt = new REButton();
    JButton clearaddsongfields = new REButton();
    public JButton addsongsstartbpmtapbutton = new  REButton();
    public JButton addsongsstartbpmresetbutton = new  REButton();
    public JButton addsongsendbpmtapbutton = new  REButton();
    public JButton addsongsendbpmresetbutton = new  REButton();
    public RELabel addcustomfieldlabel1;
    public RELabel addcustomfieldlabel2;
    public RELabel addcustomfieldlabel3;
    public RELabel addcustomfieldlabel4;
    public AutoTextField addcustomfieldtext1 = new AutoTextField();
    public AutoTextField addcustomfieldtext2 = new  AutoTextField();
    public AutoTextField addcustomfieldtext3 = new  AutoTextField();
    public AutoTextField addcustomfieldtext4 = new  AutoTextField();
    public JSlider addsongs_bpmaccuracy = new MediaSlider(JSlider.HORIZONTAL, 0, 100, 0);
    public JSlider addsongs_keyaccuracy = new MediaSlider(JSlider.HORIZONTAL, 0, 100, 0);
    public JButton albumcover_btn = new REButton();    
    public JCheckBox rating1checkbox = new RECheckBox();
    public JCheckBox rating2checkbox = new RECheckBox();
    public JCheckBox rating3checkbox = new RECheckBox();
    public JCheckBox rating4checkbox = new RECheckBox();
    public JCheckBox rating5checkbox = new RECheckBox();    
    
    public AddSongKeyProgressUI addsongkeyprogress_ui;
    public AlbumCoverUI addsongsalbumcover_ui;

    private void setupDialog() {
        addallbutton.setEnabled(false);
        albumcover_btn.setEnabled(false);
        albumcover_btn.addActionListener(this);
        addsongsskipbutton.setEnabled(false);
        addsongsalbumfield.addKeyListener(new albumcoverkeylistener());
        //addsongsartistfield.addKeyListener(new albumcoverkeylistener());
        addsongstimefield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            addsongstimefield.setText(StringUtil.validate_time(addsongstimefield.getText()));
        }});
        addsongstimesignaturefield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            addsongstimesignaturefield.setText(StringUtil.validate_timesig(addsongstimesignaturefield.getText()));
        }});
        addsongsstartbpmfield.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                addsongsstartbpmfield.setText(StringUtil.validate_bpm(addsongsstartbpmfield.getText()));
          }});
        addsongsendbpmfield.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                addsongsendbpmfield.setText(StringUtil.validate_bpm(addsongsendbpmfield.getText()));
              }});
        addsongsstartkeyfield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            addsongsstartkeyfield.setText(StringUtil.validate_keyformat(addsongsstartkeyfield.getText()));
        }});
        addsongsendkeyfield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            addsongsendkeyfield.setText(StringUtil.validate_keyformat(addsongsendkeyfield.getText()));
        }});
        addcustomfieldlabel1 = new RELabel(OptionsUI.instance.customfieldtext1.getText() + ":");
        addcustomfieldlabel2 = new RELabel(OptionsUI.instance.customfieldtext2.getText() + ":");
        addcustomfieldlabel3 = new RELabel(OptionsUI.instance.customfieldtext3.getText() + ":");
        addcustomfieldlabel4 = new RELabel(OptionsUI.instance.customfieldtext4.getText() + ":");
        addsongswritetagbutton.setEnabled(false);
        addsongsreadtagbutton.setEnabled(false);
        addsongsrenamebutton.setEnabled(false);
        addsongsplaybutton.setEnabled(false);

        addsongsfilenamefield.getDocument().addDocumentListener(new addsongsfilenamelistener());
        addsongsclearstylesbt.setEnabled(false);
        
        addsongsartistfield.addFocusListener(this);
        addsongsalbumfield.addFocusListener(this);
        addsongstrackfield.addFocusListener(this);
        addsongstitlefield.addFocusListener(this);
        addsongsremixerfield.addFocusListener(this);
        addsongstimefield.addFocusListener(this);
        addsongstimesignaturefield.addFocusListener(this);
        addsongsstartbpmfield.addFocusListener(this);
        addsongsendbpmfield.addFocusListener(this);
        addsongsstartkeyfield.addFocusListener(this);
        addsongscommentsfield.addFocusListener(this);
        addsongsfilenamefield.addFocusListener(this);
        addcustomfieldtext1.addFocusListener(this);
        addcustomfieldtext2.addFocusListener(this);
        addcustomfieldtext3.addFocusListener(this);
        addcustomfieldtext4.addFocusListener(this);
     
        addsongstylestree.setRootVisible(false);
        StylesUI.addTree(addsongstylestree, style_tree_map);
        addsongseditstylebt.setEnabled(false);
        addsongsdeletestylebt.setEnabled(false);
        addsongstylestree.getSelectionModel().addTreeSelectionListener(new StyleClearSelectionHandler(addsongstylestree, addsongsclearstylesbt, addsongseditstylebt, addsongsaddstylebt, addsongsdeletestylebt));

        rating1checkbox.addActionListener(this);
        rating2checkbox.addActionListener(this);
        rating3checkbox.addActionListener(this);
        rating4checkbox.addActionListener(this);
        rating5checkbox.addActionListener(this);
    }

    public Map style_tree_map = java.util.Collections.synchronizedMap(new HashMap());
    
    public void PostInit() {
      if (!RapidEvolution.instance.loaded) {
        SkinManager.instance.setEnabled("add_songs_query_server_button", false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent we) {
                StopDetecting();
            }
        });
      }
    }
    
    int lastfocus = 3;
    public void focusGained(FocusEvent fe) {                
        if (fe.getSource() == addsongsartistfield) lastfocus = 0;
        else if (fe.getSource() == addsongsalbumfield) lastfocus = 1;            
        else if (fe.getSource() == addsongstrackfield) lastfocus = 2;            
        else if (fe.getSource() == addsongstitlefield) lastfocus = 3;            
        else if (fe.getSource() == addsongsremixerfield) lastfocus = 4;            

        else if (fe.getSource() == addsongstimefield) lastfocus = 5;            
        else if (fe.getSource() == addsongstimesignaturefield) lastfocus = 6;            
        else if (fe.getSource() == addsongsstartbpmfield) lastfocus = 7;            
        else if (fe.getSource() == addsongsendbpmfield) lastfocus = 8;            

        else if (fe.getSource() == addsongsstartkeyfield) lastfocus = 9;            
        else if (fe.getSource() == addsongsendkeyfield) lastfocus = 10;            
        else if (fe.getSource() == addsongscommentsfield) lastfocus = 11;            
        else if (fe.getSource() == addsongsfilenamefield) lastfocus = 12;            

        else if (fe.getSource() == addcustomfieldtext1) lastfocus = 13;            
        else if (fe.getSource() == addcustomfieldtext2) lastfocus = 14;            
        else if (fe.getSource() == addcustomfieldtext3) lastfocus = 15;            
        else if (fe.getSource() == addcustomfieldtext4) lastfocus = 16;            
        
    }
    public void focusLost(FocusEvent fe) {
    }    
    
    public void PostDisplay() {
        if (lastfocus == 0) addsongsartistfield.requestFocus();
        if (lastfocus == 1) addsongsalbumfield.requestFocus();
        if (lastfocus == 2) addsongstrackfield.requestFocus();
        if (lastfocus == 3) addsongstitlefield.requestFocus();
        if (lastfocus == 4) addsongsremixerfield.requestFocus();
        if (lastfocus == 5) addsongstimefield.requestFocus();
        if (lastfocus == 6) addsongstimesignaturefield.requestFocus();
        if (lastfocus == 7) addsongsstartbpmfield.requestFocus();
        if (lastfocus == 8) addsongsendbpmfield.requestFocus();
        if (lastfocus == 9) addsongsstartkeyfield.requestFocus();
        if (lastfocus == 10) addsongsendkeyfield.requestFocus();
        if (lastfocus == 11) addsongscommentsfield.requestFocus();
        if (lastfocus == 12) addsongsfilenamefield.requestFocus();
        if (lastfocus == 13) addcustomfieldtext1.requestFocus();
        if (lastfocus == 14) addcustomfieldtext2.requestFocus();
        if (lastfocus == 15) addcustomfieldtext3.requestFocus();
        if (lastfocus == 16) addcustomfieldtext4.requestFocus();        
    }    

    class addsongsfilenamelistener implements javax.swing.event.DocumentListener {
     public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
     public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
     public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
     private void updateVal() {
         String newfile = addsongsfilenamefield.getText();
         if (!newfile.equals("")) {
           File file = new File(newfile);
           if (file.exists() && file.isFile()) {
             addsongsreadtagbutton.setEnabled(true);
             addsongsrenamebutton.setEnabled(true);
             addsongswritetagbutton.setEnabled(true);
             addsongsplaybutton.setEnabled(true);
             return;
           }
         }
         addsongsreadtagbutton.setEnabled(false);
         addsongswritetagbutton.setEnabled(false);
         addsongsplaybutton.setEnabled(false);
         addsongsrenamebutton.setEnabled(false);
     }
   }

    public class AddSongGetKey extends Thread {
      public AddSongGetKey() {  }
      public void run() {
        AudioEngine.instance.adddetectingfromfile = true;
          addsongkeyprogress_ui.progressbar2.setValue(0);
          addsongkeyprogress_ui.Display();
          new adddetectfilethread(addsongsfilenamefield.getText()).start();
      }
    }

    
    // TODO: aggregate other calls to reset field state
    private void getNextFieldState(boolean advance_file_state) {
//        javax.swing.SwingUtilities.invokeAndWait(new Thread extends Runnab);
        addsongstrackfield.setText(StringUtil.track_increment(addsongstrackfield.getText()));
        addsongstimefield.setText("");
        addsongstimesignaturefield.setText("4/4");
        addsongsrga = null;
        addsongsbeatintensity = 0;
        addsongstitlefield.setText("");
        addsongsstartbpmfield.setText("");
        addsongsendbpmfield.setText("");
        addsongsstartkeyfield.setText("");
        addsongsendkeyfield.setText("");
        addsongscommentsfield.setText("");
        addsongsremixerfield.setText("");
        addsongs_bpmaccuracy.setValue(0);
        addsongs_keyaccuracy.setValue(0);
        rating1checkbox.setSelected(false);
        rating2checkbox.setSelected(false);
        rating3checkbox.setSelected(false);
        rating4checkbox.setSelected(false);
        rating5checkbox.setSelected(false);
        if (!OptionsUI.instance.donotclearuserfield1whenadding.isSelected()) addcustomfieldtext1.setText("");
        if (!OptionsUI.instance.donotclearuserfield2whenadding.isSelected()) addcustomfieldtext2.setText("");
        if (!OptionsUI.instance.donotclearuserfield3whenadding.isSelected()) addcustomfieldtext3.setText("");
        if (!OptionsUI.instance.donotclearuserfield4whenadding.isSelected()) addcustomfieldtext4.setText("");
        if (!OptionsUI.instance.addsongalbumstyle.isSelected()) { 
            addsongsartistfield.setText(""); addsongsalbumfield.setText(""); addsongstrackfield.setText(""); addsongstylestree.clearSelection(); 
            addcustomfieldtext1.setText("");
            addcustomfieldtext2.setText("");
            addcustomfieldtext3.setText("");
            addcustomfieldtext4.setText("");
        }
        addsongsfilenamefield.setText("");
        addsongsreadtagbutton.setEnabled(false);
        addsongsrenamebutton.setEnabled(false);
        addsongswritetagbutton.setEnabled(false);
        addsongsplaybutton.setEnabled(false);

        if (advance_file_state) {

        if (RapidEvolutionUI.instance.filenames.size() >= 1) {
          addsongsfilenamefield.setText((String)RapidEvolutionUI.instance.filenames.get(0));
          RapidEvolutionUI.instance.filenames.remove(0);
          File file = new File(addsongsfilenamefield.getText());
          if (file.exists() && file.isFile()) {
            addsongsreadtagbutton.setEnabled(true);
            addsongswritetagbutton.setEnabled(true);
            addsongsplaybutton.setEnabled(true);
            addsongsrenamebutton.setEnabled(true);
          } else {
              addsongsreadtagbutton.setEnabled(false);
              addsongswritetagbutton.setEnabled(false);
              addsongsplaybutton.setEnabled(false);
              addsongsrenamebutton.setEnabled(false);
          }
        }
        }
        if (RapidEvolutionUI.instance.filenames.size() >= 1) {
          addsongsskipbutton.setEnabled(true);
          addallbutton.setEnabled(true);
        } else {
          addsongsskipbutton.setEnabled(false);
          addallbutton.setEnabled(false);
        }
        if (!addsongsfilenamefield.getText().equals("")) {
          addsongsreadtagbutton.setEnabled(true);
          addsongswritetagbutton.setEnabled(true);
          addsongsplaybutton.setEnabled(true);
          addsongsrenamebutton.setEnabled(true);
        }
        else {
          addsongsreadtagbutton.setEnabled(false);
          addsongswritetagbutton.setEnabled(false);
          addsongsplaybutton.setEnabled(false);
          addsongsrenamebutton.setEnabled(false);
        }        
    }
    
 // if normalmode = true, don't display.. i know, what was i thinking??
    public void InitiateAddSongs(boolean normalmode) {
      if (!this.isVisible()) {
          getNextFieldState(normalmode);
        if (!normalmode) SkinManager.instance.setVisible("add_songs_dialog", true);
      } else requestFocus();
    }
    
    public void PopulateAddSongDialog(SongLinkedList datasong, boolean overwrite, boolean createStyles) {
      if (datasong == null) return;
      if ((datasong.getArtist() != null) && (!datasong.getArtist().equals(""))) {
    	  if (overwrite || addsongsartistfield.getText().equals(""))
    		  addsongsartistfield.setText(datasong.getArtist());
      }
      if ((datasong.getAlbum() != null) && (!datasong.getAlbum().equals(""))) {
    	  if (overwrite || addsongsalbumfield.getText().equals(""))
    		  addsongsalbumfield.setText(datasong.getAlbum());
      }
      if ((datasong.getTrack() != null) && (!datasong.getTrack().equals(""))) {
    	  if (overwrite || addsongstrackfield.getText().equals(""))
    		  addsongstrackfield.setText(datasong.getTrack());
      }
      if (!datasong.getSongname().equals("")) {
    	  if (overwrite || addsongstitlefield.getText().equals(""))
    		  addsongstitlefield.setText(datasong.getSongname());
      }
      if ((datasong.getRemixer() != null) && (!datasong.getRemixer().equals(""))) {
    	  if (overwrite || addsongsremixerfield.getText().equals(""))
    		  addsongsremixerfield.setText(datasong.getRemixer());
      }
      if (!datasong.getTime().equals("")) {
    	  if (overwrite || addsongstimefield.getText().equals(""))
    		  addsongstimefield.setText(datasong.getTime());
      }
      if (!datasong.getComments().equals("")) {
    	  if (overwrite || addsongscommentsfield.getText().equals(""))
    		  addsongscommentsfield.setText(datasong.getComments());
      }
      if (datasong.getBpmAccuracy() != 0) {
    	  if (overwrite || (addsongs_bpmaccuracy.getValue() == 0))
    		  addsongs_bpmaccuracy.setValue(datasong.getBpmAccuracy());
      }
      if (datasong.getStartbpm() != 0) {
    	  if (overwrite || addsongsstartbpmfield.getText().equals(""))
    		  addsongsstartbpmfield.setText(String.valueOf(datasong.getStartbpm()));
      }
      if (datasong.getEndbpm() != 0) {
    	  if (overwrite || addsongsendbpmfield.getText().equals(""))
    		  addsongsendbpmfield.setText(String.valueOf(datasong.getEndbpm()));
      }
      if (datasong.getKeyAccuracy() != 0) {
    	  if (overwrite || (addsongs_keyaccuracy.getValue() == 0))
    		  addsongs_keyaccuracy.setValue(datasong.getKeyAccuracy());
      }
      if (datasong.getStartKey().isValid()) {
    	  if (overwrite || addsongsstartkeyfield.getText().equals(""))
    		  addsongsstartkeyfield.setText(datasong.getStartKey().toString());
      }
      if (datasong.getEndKey().isValid()) {
    	  if (overwrite || addsongsendkeyfield.getText().equals(""))
    		  addsongsendkeyfield.setText(datasong.getEndKey().toString());
      }
      if (!datasong.getTimesig().equals("")) {
    	  if (overwrite || addsongstimesignaturefield.getText().equals(""))
    		  addsongstimesignaturefield.setText(datasong.getTimesig());
      }
      if (datasong.getRGA().isValid()) {
    	  if (overwrite || (addsongsrga == null) || !addsongsrga.isValid())
    		  addsongsrga = datasong.getRGA();
      }
      if (datasong.getBeatIntensity() != 0) {
    	  if (overwrite || (addsongsbeatintensity == 0))
    		  addsongsbeatintensity = datasong.getBeatIntensity();
      }
      if (datasong.getRating() == 0) {
      } else if (datasong.getRating() == 1) {
    	  if (overwrite || !rating1checkbox.isSelected()) {
	          rating1checkbox.setSelected(true);
	          rating2checkbox.setSelected(false);
	          rating3checkbox.setSelected(false);
	          rating4checkbox.setSelected(false);
	          rating5checkbox.setSelected(false);
    	  }
      } else if (datasong.getRating() == 2) {
    	  if (overwrite || !rating1checkbox.isSelected()) {
	          rating1checkbox.setSelected(true);
	          rating2checkbox.setSelected(true);
	          rating3checkbox.setSelected(false);
	          rating4checkbox.setSelected(false);
	          rating5checkbox.setSelected(false);
    	  }
      } else if (datasong.getRating() == 3) {
    	  if (overwrite || !rating1checkbox.isSelected()) {
	          rating1checkbox.setSelected(true);
	          rating2checkbox.setSelected(true);
	          rating3checkbox.setSelected(true);
	          rating4checkbox.setSelected(false);
	          rating5checkbox.setSelected(false);
    	  }
      } else if (datasong.getRating() == 4) {
    	  if (overwrite || !rating1checkbox.isSelected()) {
	          rating1checkbox.setSelected(true);
	          rating2checkbox.setSelected(true);
	          rating3checkbox.setSelected(true);
	          rating4checkbox.setSelected(true);
	          rating5checkbox.setSelected(false);
    	  }
      } else if (datasong.getRating() == 5) {
    	  if (overwrite || !rating1checkbox.isSelected()) {
	          rating1checkbox.setSelected(true);
	          rating2checkbox.setSelected(true);
	          rating3checkbox.setSelected(true);
	          rating4checkbox.setSelected(true);
	          rating5checkbox.setSelected(true);
    	  }
      }

      if (!datasong.getUser1().equals("")) {
    	  if (overwrite || addcustomfieldtext1.getText().equals(""))
    		  addcustomfieldtext1.setText(datasong.getUser1());
      }
      if (!datasong.getUser2().equals("")) {
    	  if (overwrite || addcustomfieldtext2.getText().equals(""))
    		  addcustomfieldtext2.setText(datasong.getUser2());
      }
      if (!datasong.getUser3().equals("")) {
    	  if (overwrite || addcustomfieldtext3.getText().equals(""))
    		  addcustomfieldtext3.setText(datasong.getUser3());
      }
      if (!datasong.getUser4().equals("")) {
    	  if (overwrite || addcustomfieldtext4.getText().equals(""))
    		  addcustomfieldtext4.setText(datasong.getUser4());
      }

      DefaultTreeModel dlm = (DefaultTreeModel)addsongstylestree.getModel();
      
      if (datasong.stylelist != null ) {
          for (int s = 0; s < datasong.stylelist.length; ++s) {
              String stylename = datasong.stylelist[s];

              boolean found = false;
                            
              StyleLinkedList siter = SongDB.instance.masterstylelist;
              while (siter != null) {
                  if (StringUtil.areStyleNamesEqual(siter.getName(), stylename)) {
                      found = true;
                      Vector nodes = (Vector)style_tree_map.get(siter);
                      if (nodes != null) {
                          for (int i = 0; i < nodes.size(); ++i) {
                              MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(i);
                              addsongstylestree.addSelectionPath(new TreePath(node.getPath()));
                          }
                      }
                  }
                  siter = siter.next;
              }
              
              if (!found && createStyles) {
                  siter = SongDB.instance.addStyle(stylename, false, new SelectStyleRunnable());
              }                    
                           
          }  
      }      
      
      if ((addsongsalbumfield.getText().length() == 0))
          albumcover_btn.setEnabled(false);
      else albumcover_btn.setEnabled(true);
      
    }
    
    public class SelectStyleRunnable extends AddStyleRunnable {
        public void run() {
        	if (log.isDebugEnabled())
        		log.debug("SelectStyleRunnable.run(): starting=" + style.getName());        	
            Vector nodes = (Vector)style_tree_map.get(style);
            if (nodes != null) {
                for (int i = 0; i < nodes.size(); ++i) {
                    MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(i);
                    addsongstylestree.addSelectionPath(new TreePath(node.getPath()));
                }
            }            
        }
    }

    public class AddAllThread extends Thread {
      public AddAllThread()  { }
      public void run()  {
        Hide();
        int songsadded = 1; // initialize at this point because just getting here means the first song was added
        int totalsongs = 1;
        addsongsskipbutton.setEnabled(false);
        addallbutton.setEnabled(false);
        SongDB.instance.AddSingleSong();
        Vector thesefilenames = RapidEvolutionUI.instance.filenames;
        RapidEvolutionUI.instance.filenames = new Vector();
        SongLinkedList lastsong = new SongLinkedList();
        lastsong.setTrack(addsongstrackfield.getText());
        lastsong.setArtist(addsongsartistfield.getText());
        lastsong.setAlbum(addsongsalbumfield.getText());        
        if (OptionsUI.instance.donotclearuserfield1whenadding.isSelected()) lastsong.setUser1(addcustomfieldtext1.getText());
        if (OptionsUI.instance.donotclearuserfield2whenadding.isSelected()) lastsong.setUser2( addcustomfieldtext2.getText());
        if (OptionsUI.instance.donotclearuserfield3whenadding.isSelected()) lastsong.setUser3( addcustomfieldtext3.getText());
        if (OptionsUI.instance.donotclearuserfield4whenadding.isSelected()) lastsong.setUser4( addcustomfieldtext4.getText());
        String oldDirectory = FileUtil.getDirectoryFromFilename(addsongsfilenamefield.getText());
        String newDirectory = FileUtil.getDirectoryFromFilename((String) thesefilenames.get(0));
        if (!OptionsUI.instance.addsongalbumstyle.isSelected() || detectDirectoryChange(oldDirectory, newDirectory)) {
       lastsong.setArtist("");
       lastsong.setAlbum("");
       lastsong.setTrack("");
       lastsong.setUser1("");
       lastsong.setUser2("");
       lastsong.setUser3("");
       lastsong.setUser4("");
        }
        lastsong.setVinylOnly(addsongsvinylonly.isSelected());
        lastsong.setNonVinylOnly(addsongsnonvinylonly.isSelected());
        lastsong.setDisabled(addsongsdisabled.isSelected());

        addsongstrackfield.setText("");
        addsongstimefield.setText("");
        addsongstimesignaturefield.setText("4/4");
        addsongsrga = null;
        addsongsbeatintensity = 0;
        addsongstitlefield.setText("");
        addsongsremixerfield.setText("");
        addsongsstartbpmfield.setText("");
        addsongsendbpmfield.setText("");
        addsongsstartkeyfield.setText("");
        addsongsendkeyfield.setText("");
        addsongscommentsfield.setText("");
        addsongsfilenamefield.setText("");
        addsongs_bpmaccuracy.setValue(0);
        addsongs_keyaccuracy.setValue(0);
        rating1checkbox.setSelected(false);
        rating2checkbox.setSelected(false);
        rating3checkbox.setSelected(false);
        rating4checkbox.setSelected(false);
        rating5checkbox.setSelected(false);
        if (!OptionsUI.instance.donotclearuserfield1whenadding.isSelected()) addcustomfieldtext1.setText("");
        if (!OptionsUI.instance.donotclearuserfield2whenadding.isSelected()) addcustomfieldtext2.setText("");
        if (!OptionsUI.instance.donotclearuserfield3whenadding.isSelected()) addcustomfieldtext3.setText("");
        if (!OptionsUI.instance.donotclearuserfield4whenadding.isSelected()) addcustomfieldtext4.setText("");
        if (!OptionsUI.instance.addsongalbumstyle.isSelected() || detectDirectoryChange(oldDirectory, newDirectory)) {
          addsongsalbumfield.setText("");
          addsongsartistfield.setText("");
          addsongstrackfield.setText("");
          addcustomfieldtext1.setText("");
          addcustomfieldtext2.setText("");
          addcustomfieldtext3.setText("");
          addcustomfieldtext4.setText("");
        }

        TreePath[] selections = addsongstylestree.getSelectionPaths();
        Vector initialstyles = new Vector();
        if (OptionsUI.instance.addsongalbumstyle.isSelected() && !detectDirectoryChange(oldDirectory, newDirectory)) {
            for (int i = 0; i < selections.length; ++i) {
                MyMutableStyleNode node = (MyMutableStyleNode)selections[i].getLastPathComponent();
                initialstyles.add(node.getStyle());
            }
        }

        while ((thesefilenames.size() >= 1) && !RapidEvolution.instance.terminatesignal) {
            totalsongs++;
          SongLinkedList newsong = new SongLinkedList();
          newsong.setTrack(StringUtil.track_increment(lastsong.getTrack()));
          newsong.setAlbum(lastsong.getAlbum());
          newsong.setArtist(lastsong.getArtist());
          newsong.setFilename((String) thesefilenames.get(0));
          if (OptionsUI.instance.donotclearuserfield1whenadding.isSelected()) newsong.setUser1(lastsong.getUser1());
          if (OptionsUI.instance.donotclearuserfield2whenadding.isSelected()) newsong.setUser2(lastsong.getUser2());
          if (OptionsUI.instance.donotclearuserfield3whenadding.isSelected()) newsong.setUser3(lastsong.getUser3());
          if (OptionsUI.instance.donotclearuserfield4whenadding.isSelected()) newsong.setUser4(lastsong.getUser4());
          if (!OptionsUI.instance.addsongalbumstyle.isSelected() || detectDirectoryChange(oldDirectory, newDirectory)) {
            newsong.setArtist(""); newsong.setAlbum(""); newsong.setTrack(""); initialstyles.clear();
            newsong.setUser1("");
            newsong.setUser2("");
            newsong.setUser3("");
            newsong.setUser4("");
          }
          newsong.setVinylOnly(lastsong.getVinylOnly());
          newsong.setNonVinylOnly( lastsong.getNonVinylOnly());
          newsong.setDisabled(lastsong.isDisabled());

          thesefilenames.remove(0);
          if (!OptionsUI.instance.disableautotagreading.isSelected()) {
            SongLinkedList datasong = TagManager.readTags(newsong.getRealFileName());
            SongLinkedList.PopulateSong(newsong, datasong);              
            if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
	            try {
	                MixshareClient.instance.querySongDetails(newsong, false);
	            } catch (Exception e) { log.error("run(): error", e); }
            }
            try {
                if (newsong.getTime().equals("")) {
                  int seconds = (int)AudioLib.get_track_time(newsong.getRealFileName());
                  newsong.setTime(StringUtil.seconds_to_time(seconds));
                }
              } catch (Exception e) { }
            SongDB.instance.AddSingleSong(newsong, initialstyles);
            songsadded++;
          } else {
            try {
              int seconds = (int)AudioLib.get_track_time(newsong.getRealFileName());
              newsong.setTime(StringUtil.seconds_to_time(seconds));
            } catch (Exception e) { }
            if (newsong.getSongname().equals("")) {
              if (OptionsUI.instance.usefilenameastag.isSelected()) newsong.setSongname(StringUtil.remove_underscores(StringUtil.ScrubFileType(newsong.getFileName())));
            }
            if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
                try {
                    MixshareClient.instance.querySongDetails(newsong, false);
                } catch (Exception e) { log.error("run(): error", e); }
                SongDB.instance.AddSingleSong(newsong, initialstyles);
            }
            songsadded++;
          }
          lastsong = newsong;
        }
        IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"), "there were " + songsadded + " songs successfully added out of " + totalsongs + " files", "add all songs finished", IOptionPane.PLAIN_MESSAGE);
      }      
        
      
    }

    private void setupActionListeners() {
      addsongsbrowsebutton.addActionListener(this);
      addsongswritetagbutton.addActionListener(this);
      addsongscancelbutton.addActionListener(this);
      addsongsokbutton.addActionListener(this);
      addallbutton.addActionListener(this);
      addsongsmorebutton.addActionListener(this);
      addsongsplaybutton.addActionListener(this);
      addsongsskipbutton.addActionListener(this);
      addsongsdetectkeybutton.addActionListener(this);
      addsongsvinylonly.addActionListener(this);
      addsongsnonvinylonly.addActionListener(this);
      addsongsclearstylesbt.addActionListener(this);
      clearaddsongfields.addActionListener(this);
      addsongsstartbpmtapbutton.addActionListener(this);
      addsongsstartbpmresetbutton.addActionListener(this);
      addsongsendbpmtapbutton.addActionListener(this);
      addsongsendbpmresetbutton.addActionListener(this);
      addsongsreadtagbutton.addActionListener(this);
      addsongsrenamebutton.addActionListener(this);
      addsongsdeletestylebt.addActionListener(this);
      addsongseditstylebt.addActionListener(this);
    }
    
    public boolean detectDirectoryChange(String oldDirectory, String newDirectory) {
    	if ((oldDirectory == null) && (newDirectory == null))
    		return false;
    	if ((oldDirectory != null) && (newDirectory != null))
    		return !oldDirectory.equalsIgnoreCase(newDirectory);
    	return true;
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addsongsvinylonly) {
        addsongsnonvinylonly.setSelected(false);
      } else if (ae.getSource() == addsongscancelbutton) {
        if (AudioEngine.instance.adddetectingfromfile) return;
        StopDetecting();
        if (!addsongsfilenamefield.getText().equals("")) { RapidEvolutionUI.instance.filenames.insertElementAt(addsongsfilenamefield.getText(), 0); }
        Hide();
      } else if (ae.getSource() == addsongseditstylebt) {
          if (addsongstylestree.getSelectionPath() != null) {
              MyMutableStyleNode selected_node = (MyMutableStyleNode)addsongstylestree.getSelectionPath().getLastPathComponent();
              EditStyleUI.instance.display_parameter = selected_node.getStyle();
              EditStyleUI.instance.Display();
          }                          
      } else if (ae.getSource() == addsongsmorebutton) {
            if (AudioEngine.instance.adddetectingfromfile) return;
            if (addsongsartistfield.getText().equals("") &&
                addsongsalbumfield.getText().equals("") &&
                addsongstrackfield.getText().equals("") &&
                addsongstitlefield.getText().equals("") &&
                addsongsremixerfield.getText().equals("")) {
                IOptionPane.showMessageDialog(getDialog(),
                 SkinManager.instance.getDialogMessageText("invalid_song_information"),
                SkinManager.instance.getDialogMessageTitle("invalid_song_information"),
                IOptionPane.ERROR_MESSAGE);
              return;
            }

            String uniquesongid = SongLinkedList.calculate_unique_id(addsongsartistfield.getText(),
                                                      addsongsalbumfield.getText(),
                                                      addsongstrackfield.getText(),
                                                      addsongstitlefield.getText(),
                                                      addsongsremixerfield.getText());

            AddIdentifyThread.addseqnum++;
            SongLinkedList existingsong = SongDB.instance.OldGetSongPtr(uniquesongid);
		    if (existingsong != null) {
		        if (OptionsUI.instance.autoupdatepaths.isSelected()
		                && !addsongsfilenamefield.getText().equals("")) {
		            // auto update path       
		            if (!addsongsfilenamefield.getText().equals(""))
		                existingsong.setFilename(addsongsfilenamefield.getText());
		        } else {
		            IOptionPane.showMessageDialog(getDialog(),
		               SkinManager.instance.getDialogMessageText("invalid_song_information"),
		              SkinManager.instance.getDialogMessageTitle("invalid_song_information"),
		              IOptionPane.ERROR_MESSAGE);
		              return;
		           }
		    } else {
	            SongDB.instance.AddSingleSong();		        
		    }

            addsongstrackfield.setText(StringUtil.track_increment(addsongstrackfield.getText()));
            addsongstimefield.setText("");
            addsongstimesignaturefield.setText("4/4");
            addsongsrga = null;
            addsongsbeatintensity = 0;
            addsongstitlefield.setText("");
            addsongsstartbpmfield.setText("");
            addsongsendbpmfield.setText("");
            addsongsstartkeyfield.setText("");
            addsongsendkeyfield.setText("");
            addsongscommentsfield.setText("");
            String oldDirectory = FileUtil.getDirectoryFromFilename(addsongsfilenamefield.getText());
            addsongsfilenamefield.setText("");
            addsongsremixerfield.setText("");
            
            rating1checkbox.setSelected(false);
            rating2checkbox.setSelected(false);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);
            
            String newDirectory = FileUtil.getDirectoryFromFilename((String)RapidEvolutionUI.instance.filenames.get(0));
            
            if (!OptionsUI.instance.addsongalbumstyle.isSelected() || detectDirectoryChange(oldDirectory, newDirectory)) {
              addsongsalbumfield.setText("");
              addsongsartistfield.setText("");
              addsongstrackfield.setText("");
              addsongstylestree.clearSelection();
              addcustomfieldtext1.setText("");
              addcustomfieldtext2.setText("");
              addcustomfieldtext3.setText("");
              addcustomfieldtext4.setText("");
            }
            addsongs_bpmaccuracy.setValue(0);
            addsongs_keyaccuracy.setValue(0);
            if (!OptionsUI.instance.donotclearuserfield1whenadding.isSelected()) addcustomfieldtext1.setText("");
            if (!OptionsUI.instance.donotclearuserfield2whenadding.isSelected()) addcustomfieldtext2.setText("");
            if (!OptionsUI.instance.donotclearuserfield3whenadding.isSelected()) addcustomfieldtext3.setText("");
            if (!OptionsUI.instance.donotclearuserfield4whenadding.isSelected()) addcustomfieldtext4.setText("");

            addsongsreadtagbutton.setEnabled(false);
            addsongswritetagbutton.setEnabled(false);
            addsongsplaybutton.setEnabled(false);
            addsongsrenamebutton.setEnabled(false);

            if (RapidEvolutionUI.instance.filenames.size() >= 1) {
              addsongsfilenamefield.setText((String)RapidEvolutionUI.instance.filenames.get(0));
              addsongsreadtagbutton.setEnabled(true);
              addsongswritetagbutton.setEnabled(true);
              addsongsplaybutton.setEnabled(true);
              addsongsrenamebutton.setEnabled(true);
              RapidEvolutionUI.instance.filenames.remove(0);
            }
            new AddIdentifyThread(AddIdentifyThread.addseqnum).start();
//            RapidEvolution.instance.StartIdentifyThread(AddIdentifyThread.addseqnum);
            if (RapidEvolutionUI.instance.filenames.size() >= 1) {
              addsongsskipbutton.setEnabled(true);
              addallbutton.setEnabled(true);
            } else {
              addsongsskipbutton.setEnabled(false);
              addallbutton.setEnabled(false);
            }
            if (!addsongstitlefield.getText().equals("")) addsongscommentsfield.requestFocus();
            else addsongstitlefield.requestFocus();
      } else if (ae.getSource() == addsongsbrowsebutton) {
            JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
            if (!RapidEvolutionUI.instance.previousfilepath.equals(""))
                fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
            if (!addsongsfilenamefield.getText().equals("")) {
                File addsongsfile = FileUtil.getFileObject(addsongsfilenamefield.getText());
                if (addsongsfile != null) {
                    if (!addsongsfile.getAbsolutePath().equals("")) {
                        fc.setCurrentDirectory(new File(addsongsfile.getAbsolutePath()));
                    }
                }
            }
            fc.addChoosableFileFilter(new InputFileFilter());
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.setMultiSelectionEnabled(true);
            int returnVal = fc.showOpenDialog(getDialog());
            File tmp = fc.getSelectedFile();
            if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                RapidEvolutionUI.instance.AcceptBrowseAddSongs(files, false);
            }
            requestFocus();
      } else if (ae.getSource() == addallbutton) {
            if (AudioEngine.instance.adddetectingfromfile) return;
            try { while (AddIdentifyThread.addidentifythreadcount != 0) Thread.sleep(250); } catch (Exception e) { }
            AddAllThread at = new AddAllThread();
            at.start();

      } else if (ae.getSource() == rating1checkbox) {
          if (rating2checkbox.isSelected() || rating3checkbox.isSelected() || rating4checkbox.isSelected() || rating5checkbox.isSelected())
              rating1checkbox.setSelected(true);
          rating2checkbox.setSelected(false);
          rating3checkbox.setSelected(false);
          rating4checkbox.setSelected(false);
          rating5checkbox.setSelected(false);
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
          
      } else if (ae.getSource() == addsongsskipbutton) {
            if (AudioEngine.instance.adddetectingfromfile) return;
            AddIdentifyThread.addseqnum++;
            addsongstrackfield.setText(StringUtil.track_increment(addsongstrackfield.getText()));
            addsongstimefield.setText("");
            addsongstimesignaturefield.setText("4/4");
            addsongsrga = null;
            addsongsbeatintensity = 0;
            addsongstitlefield.setText("");
            addsongsstartbpmfield.setText("");
            addsongsendbpmfield.setText("");
            addsongsstartkeyfield.setText("");
            addsongsendkeyfield.setText("");
            addsongscommentsfield.setText("");
            RootMusicExcludeList.addExcludedFile(addsongsfilenamefield.getText());
            String oldDirectory = FileUtil.getDirectoryFromFilename(addsongsfilenamefield.getText());
            String newDirectory = FileUtil.getDirectoryFromFilename((String)RapidEvolutionUI.instance.filenames.get(0));
            addsongsfilenamefield.setText("");
            addsongs_bpmaccuracy.setValue(0);
            addsongs_keyaccuracy.setValue(0);
            if (!OptionsUI.instance.donotclearuserfield1whenadding.isSelected()) addcustomfieldtext1.setText("");
            if (!OptionsUI.instance.donotclearuserfield2whenadding.isSelected()) addcustomfieldtext2.setText("");
            if (!OptionsUI.instance.donotclearuserfield3whenadding.isSelected()) addcustomfieldtext3.setText("");
            if (!OptionsUI.instance.donotclearuserfield4whenadding.isSelected()) addcustomfieldtext4.setText("");
            if (!OptionsUI.instance.addsongalbumstyle.isSelected() || detectDirectoryChange(oldDirectory, newDirectory)) {
              addsongsalbumfield.setText("");
              addsongsartistfield.setText("");
              addsongstrackfield.setText("");
              addsongstylestree.clearSelection();
              addcustomfieldtext1.setText("");
              addcustomfieldtext2.setText("");
              addcustomfieldtext3.setText("");
              addcustomfieldtext4.setText("");
            }

            addsongsreadtagbutton.setEnabled(false);
            addsongswritetagbutton.setEnabled(false);
            addsongsplaybutton.setEnabled(false);
            addsongsrenamebutton.setEnabled(false);
            if (RapidEvolutionUI.instance.filenames.size() >= 1) {
              addsongsfilenamefield.setText((String)RapidEvolutionUI.instance.filenames.get(0));
              if (!addsongsfilenamefield.getText().equals("")) {
                addsongsreadtagbutton.setEnabled(true);
                addsongswritetagbutton.setEnabled(true);
                addsongsplaybutton.setEnabled(true);
                addsongsrenamebutton.setEnabled(true);
              }
              RapidEvolutionUI.instance.filenames.remove(0);
              new AddIdentifyThread(AddIdentifyThread.addseqnum).start();
              //RapidEvolution.instance.StartIdentifyThread(AddIdentifyThread.addseqnum);
            }
            if (RapidEvolutionUI.instance.filenames.size() >= 1) {
              addsongsskipbutton.setEnabled(true);
              addallbutton.setEnabled(true);
            } else {
              addsongsskipbutton.setEnabled(false);
              addallbutton.setEnabled(false);
            }
            if (!addsongstitlefield.getText().equals("")) addsongscommentsfield.requestFocus();
            else addsongstitlefield.requestFocus();
      } else if (ae.getSource() == albumcover_btn) {
          String dir = FileUtil.getDirectoryFromFilename(addsongsfilenamefield.getText());
          if (dir != null) {
              addsongsalbumcover_ui.previousfilepath = dir;
              addsongsalbumcover_ui.pathisknown = true; 
          } else {
              addsongsalbumcover_ui.pathisknown = false;
          }
          addsongsalbumcover_ui.showAlbum(addsongsalbumfield.getText(), addsongsartistfield.getText());
      } else if (ae.getSource() == addsongsdetectkeybutton) {
            if (AudioEngine.instance.adddetecting) {
              StopDetecting();
              return;
            }
            int n = 0;
            if (!addsongsfilenamefield.getText().equals("")) {
              Object[] options = {
                  SkinManager.instance.getDialogOption("select_audio_source", 1), // live input
                  SkinManager.instance.getDialogOption("select_audio_source", 2), // file
                  SkinManager.instance.getDialogOption("select_audio_source", 3)}; // cancel
              n = IOptionPane.showOptionDialog(getDialog(),
                  SkinManager.instance.getDialogMessageText("select_audio_source"),
                  SkinManager.instance.getDialogMessageTitle("select_audio_source"),
                  IOptionPane.YES_NO_CANCEL_OPTION,
                  options);
              repaint();
              requestFocus();
            }
            if (n == 0) {
              if ((MIDIPiano.instance.synth != null) && (MIDIPiano.instance.synth.isOpen())) MIDIPiano.instance.synth.close();
              AudioEngine.instance.adddetecting = true;
              addsongsdetectkeybutton.setText(SkinManager.instance.getAltTextFor(addsongsdetectkeybutton));
              AudioEngine.instance.addCaptureAudio();
            }
            else if (n == 1) new AddSongGetKey().start();
      } else if (ae.getSource() == addsongsstartbpmtapbutton) {
           BPMTapper.instance.maintapped = false;
           BPMTapper.instance.addsongstartbpm1tapped = true;
           BPMTapper.instance.addsongendbpm1tapped = false;
           BPMTapper.instance.editsongstartbpm1tapped = false;
           BPMTapper.instance.editsongendbpm1tapped = false;
           BPMTapper.instance.TapBPM();
      } else if (ae.getSource() == addsongsstartbpmresetbutton) {
           BPMTapper.instance.addsongstartbpm1tapped = true;
           BPMTapper.instance.addsongendbpm1tapped = false;
           BPMTapper.instance.editsongstartbpm1tapped = false;
           BPMTapper.instance.editsongendbpm1tapped = false;
           BPMTapper.instance.m_numTicks = -1;
           BPMTapper.instance.ResetBpm();
      } else if (ae.getSource() == addsongsendbpmtapbutton) {
           BPMTapper.instance.maintapped = false;
           BPMTapper.instance.addsongstartbpm1tapped = false;
           BPMTapper.instance.addsongendbpm1tapped = true;
           BPMTapper.instance.editsongstartbpm1tapped = false;
           BPMTapper.instance.editsongendbpm1tapped = false;
           BPMTapper.instance.TapBPM();
      } else if (ae.getSource() == addsongsendbpmresetbutton) {
           BPMTapper.instance.addsongstartbpm1tapped = false;
           BPMTapper.instance.addsongendbpm1tapped = true;
           BPMTapper.instance.editsongstartbpm1tapped = false;
           BPMTapper.instance.editsongendbpm1tapped = false;
           BPMTapper.instance.m_numTicks = -1;
           BPMTapper.instance.ResetBpm();
      } else if (ae.getSource() == addsongsclearstylesbt) {
           addsongstylestree.clearSelection();
      } else if (ae.getSource() == addsongswritetagbutton) {
          SongLinkedList song = new SongLinkedList();
          song.setAlbum(AddSongsUI.instance.addsongsalbumfield.getText());
          song.setComments(AddSongsUI.instance.addsongscommentsfield.getText());
          song.setArtist(AddSongsUI.instance.addsongsartistfield.getText());
          song.setTrack(AddSongsUI.instance.addsongstrackfield.getText());
          song.setTime(AddSongsUI.instance.addsongstimefield.getText());
          song.setTimesig(AddSongsUI.instance.addsongstimesignaturefield.getText());
          if (addsongsrga != null && (addsongsrga.isValid()))
              song.setRGA(addsongsrga);
          if (addsongsbeatintensity != 0)
              song.setBeatIntensity(addsongsbeatintensity);
          song.setRemixer(AddSongsUI.instance.addsongsremixerfield.getText());
          song.setSongname(AddSongsUI.instance.addsongstitlefield.getText());
          song.setKeyAccuracy(addsongs_keyaccuracy.getValue());
          song.setStartkey(AddSongsUI.instance.addsongsstartkeyfield.getText());
          song.setEndkey(AddSongsUI.instance.addsongsendkeyfield.getText());
          song.setBpmAccuracy(addsongs_bpmaccuracy.getValue());
          song.setUser1(addcustomfieldtext1.getText());
          song.setUser2(addcustomfieldtext2.getText());
          song.setUser3(addcustomfieldtext3.getText());
          song.setUser4(addcustomfieldtext4.getText());          
          try {
            song.setStartbpm(Float.parseFloat(AddSongsUI.instance.addsongsstartbpmfield.getText()));
          } catch (Exception e) { }
          try {
            song.setEndbpm(Float.parseFloat(AddSongsUI.instance.addsongsendbpmfield.getText()));
          } catch (Exception e) {  }
          if (AddSongsUI.instance.rating5checkbox.isSelected()) song.setRating((char)5);
          else if (AddSongsUI.instance.rating4checkbox.isSelected()) song.setRating((char)4);
          else if (AddSongsUI.instance.rating3checkbox.isSelected()) song.setRating((char)3);
          else if (AddSongsUI.instance.rating2checkbox.isSelected()) song.setRating((char)2);
          else if (AddSongsUI.instance.rating1checkbox.isSelected()) song.setRating((char)1);                    
          String[] styles = null;
          TreePath[] paths = addsongstylestree.getSelectionPaths();
          if (paths != null) {
              HashMap styles_map = new HashMap();
	            for (int j = 0; j < paths.length; ++j) {
	                MyMutableStyleNode node = (MyMutableStyleNode)paths[j].getLastPathComponent();
	                StyleLinkedList styleiter = node.getStyle();
	                styles_map.put(styleiter.getName(), null);
	            }
	            styles = new String[styles_map.size()];
	            Set keys = styles_map.keySet();
	            int count = 0;
	            if (keys != null) {
	                Iterator iter = keys.iterator();
	                while (iter.hasNext()) {
	                    styles[count++] = (String)iter.next();
	                }
	            }
          }          
           TagManager.writeTags(song, addsongsfilenamefield.getText(), styles, addsongstylestree.getRootSelection());
      } else if (ae.getSource() == addsongsrenamebutton) {
          RenameFilesUI.instance.songs = new SongLinkedList[1];
          SongLinkedList blank = createSongFromValues();
          RenameFilesUI.instance.songs[0] = blank;
          RenameFilesUI.instance.source_id = 1;
          RenameFilesUI.instance.Display();
      } else if (ae.getSource() == addsongsreadtagbutton) {
            SongLinkedList datasong = TagManager.readTags(addsongsfilenamefield.getText());
            PopulateAddSongDialog(datasong, true, OptionsUI.instance.createstylesfromgenretags.isSelected());
      } else if (ae.getSource() == addsongsplaybutton) {
            SongLinkedList newsong = new SongLinkedList();
            newsong.setArtist(addsongsartistfield.getText());
            newsong.setAlbum(addsongsalbumfield.getText());
            newsong.setTrack(addsongstrackfield.getText());
            newsong.setSongname(addsongstitlefield.getText());
            newsong.setRemixer(addsongsremixerfield.getText());
            newsong.setTime(addsongstimefield.getText());
            newsong.setStartkey(addsongsstartkeyfield.getText());
            newsong.setEndkey(addsongsendkeyfield.getText());
            newsong.setFilename(addsongsfilenamefield.getText());
            try {
              newsong.setStartbpm(Float.parseFloat(addsongsstartbpmfield.getText()));
            } catch (Exception e) { }
            try {
              newsong.setEndbpm(Float.parseFloat(addsongsendbpmfield.getText()));
            } catch (Exception e) { }
            newsong.setSongId(SongLinkedList.song_to_string(newsong));
            AudioPlayer.songsplaying = new Vector();
            AudioPlayer.songsplaying.add(newsong);
            AudioPlayer.PlaySongs();
      } else if (ae.getSource() == addsongsdeletestylebt) {
          StylesPane.deleteSelectedStyles(addsongstylestree);
      } else if (ae.getSource() == clearaddsongfields) {
        addsongsartistfield.setText("");
        addsongsalbumfield.setText("");
        addsongstrackfield.setText("");
        addsongstitlefield.setText("");
        addsongsremixerfield.setText("");
        addsongstimefield.setText("");
        addsongsstartbpmfield.setText("");
        addsongsendbpmfield.setText("");
        addsongscommentsfield.setText("");
        addsongsstartkeyfield.setText("");
        addsongsendkeyfield.setText("");
        albumcover_btn.setEnabled(false);
      } else if (ae.getSource() == addsongsokbutton) {
        if (AudioEngine.instance.adddetectingfromfile)
                return;
            StopDetecting();
            if (addsongsartistfield.getText().equals("")
                    && addsongsalbumfield.getText().equals("")
                    && addsongstrackfield.getText().equals("")
                    && addsongstitlefield.getText().equals("")
                    && addsongstimefield.getText().equals("")
                    && (addsongstimesignaturefield.getText().equals("") || addsongstimesignaturefield
                            .getText().equals("4/4"))
                    && addsongsstartbpmfield.getText().equals("")
                    && addsongsendbpmfield.getText().equals("")
                    && addsongsstartkeyfield.getText().equals("")
                    && addsongsendkeyfield.getText().equals("")
                    && addsongscommentsfield.getText().equals("")
                    && addcustomfieldtext1.getText().equals("")
                    && addcustomfieldtext2.getText().equals("")
                    && addcustomfieldtext3.getText().equals("")
                    && addcustomfieldtext4.getText().equals("")
                    && addsongsfilenamefield.getText().equals("")
                    && addsongsremixerfield.getText().equals("")) {
                setVisible(false);
                return;
            }
            if (addsongsartistfield.getText().equals("")
                    && addsongsalbumfield.getText().equals("")
                    && addsongstrackfield.getText().equals("")
                    && addsongstitlefield.getText().equals("")
                    && addsongsremixerfield.getText().equals("")) {
                IOptionPane
                        .showMessageDialog(
                                getDialog(),
                                SkinManager.instance
                                        .getDialogMessageText("invalid_song_information"),
                                SkinManager.instance
                                        .getDialogMessageTitle("invalid_song_information"),
                                        IOptionPane.ERROR_MESSAGE);
                return;
            }

            AddIdentifyThread.addseqnum++;
            String uniquesongid = SongLinkedList.calculate_unique_id(
                    addsongsartistfield.getText(),
                    addsongsalbumfield.getText(), addsongstrackfield.getText(),
                    addsongstitlefield.getText(), addsongsremixerfield
                            .getText());
            SongLinkedList existingsong = SongDB.instance
                    .OldGetSongPtr(uniquesongid);
            if (existingsong != null) {
                if (OptionsUI.instance.autoupdatepaths.isSelected()
                        && !addsongsfilenamefield.getText().equals("")) {
                    // auto update path       
                     existingsong.setFilename(addsongsfilenamefield.getText());
                } else {
                    IOptionPane
                            .showMessageDialog(
                                    getDialog(),
                                    SkinManager.instance
                                            .getDialogMessageText("invalid_song_information"),
                                    SkinManager.instance
                                            .getDialogMessageTitle("invalid_song_information"),
                                            IOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {                
                SongDB.instance.AddSingleSong();
            }            
            if (RapidEvolutionUI.instance.filenames.size() == 0)
                setVisible(false);
            else {
                addsongstrackfield.setText(StringUtil
                        .track_increment(addsongstrackfield.getText()));
                addsongstimefield.setText("");
                addsongstimesignaturefield.setText("4/4");
                addsongsrga = null;
                addsongsbeatintensity = 0;
                addsongstitlefield.setText("");
                addsongsstartbpmfield.setText("");
                addsongsendbpmfield.setText("");
                addsongsstartkeyfield.setText("");
                addsongsendkeyfield.setText("");
                addsongscommentsfield.setText("");
                addsongsremixerfield.setText("");
                String oldDirectory = FileUtil.getDirectoryFromFilename(addsongsfilenamefield.getText());
                String newDirectory = FileUtil.getDirectoryFromFilename((String) RapidEvolutionUI.instance.filenames.get(0));
                if (!OptionsUI.instance.addsongalbumstyle.isSelected() || detectDirectoryChange(oldDirectory, newDirectory)) {
                    addsongsalbumfield.setText("");
                    addsongsartistfield.setText("");
                    addsongstrackfield.setText("");
                    addsongstylestree.clearSelection();
                    addcustomfieldtext1.setText("");
                    addcustomfieldtext2.setText("");
                    addcustomfieldtext3.setText("");
                    addcustomfieldtext4.setText("");
                }
                addsongs_bpmaccuracy.setValue(0);
                addsongs_keyaccuracy.setValue(0);
                if (!OptionsUI.instance.donotclearuserfield1whenadding
                        .isSelected())
                    addcustomfieldtext1.setText("");
                if (!OptionsUI.instance.donotclearuserfield2whenadding
                        .isSelected())
                    addcustomfieldtext2.setText("");
                if (!OptionsUI.instance.donotclearuserfield3whenadding
                        .isSelected())
                    addcustomfieldtext3.setText("");
                if (!OptionsUI.instance.donotclearuserfield4whenadding
                        .isSelected())
                    addcustomfieldtext4.setText("");
                addsongsfilenamefield.setText("");
                addsongsfilenamefield
                        .setText((String) RapidEvolutionUI.instance.filenames
                                .get(0));
                addsongsreadtagbutton.setEnabled(true);
                addsongswritetagbutton.setEnabled(true);
                addsongsplaybutton.setEnabled(true);
                addsongsrenamebutton.setEnabled(true);
                
                rating1checkbox.setSelected(false);
                rating2checkbox.setSelected(false);
                rating3checkbox.setSelected(false);
                rating4checkbox.setSelected(false);
                rating5checkbox.setSelected(false);
                
                RapidEvolutionUI.instance.filenames.remove(0);

                new AddIdentifyThread(AddIdentifyThread.addseqnum).start();
                //RapidEvolution.instance.StartIdentifyThread(AddIdentifyThread.addseqnum);

                if (RapidEvolutionUI.instance.filenames.size() >= 1) {
                    addsongsskipbutton.setEnabled(true);
                    addallbutton.setEnabled(true);
                } else {
                    addsongsskipbutton.setEnabled(false);
                    addallbutton.setEnabled(false);
                }
                if (!addsongstitlefield.getText().equals(""))
                    addsongscommentsfield.requestFocus();
                else
                    addsongstitlefield.requestFocus();

            }
      } else if (ae.getSource() == addsongsnonvinylonly) {
        addsongsvinylonly.setSelected(false);
      }
    }

    private void StopDetecting() {
      try {
        AudioEngine.instance.adddetecting = false;
        AudioEngine.instance.addTargetDataLine.stop();
        AudioEngine.instance.addTargetDataLine.close();
        addsongsdetectkeybutton.setText(SkinManager.instance.getTextFor(addsongsdetectkeybutton));
      } catch (Exception e) { }
    }

    public boolean PreDisplay(Object source) {
      InitiateAddSongs(true);
      if ((addsongsalbumfield.getText().length() == 0))
          albumcover_btn.setEnabled(false);
      else albumcover_btn.setEnabled(true);
      addsongstylestree.minimizeTree();
      return true;
    }
    
    class albumcoverkeylistener extends KeyAdapter {
        public void keyReleased(KeyEvent e) {
            if ((addsongsalbumfield.getText().length() == 0)) //||
                    //(addsongsartistfield.getText().length() == 0))
                albumcover_btn.setEnabled(false);
            else albumcover_btn.setEnabled(true);
        }
      }
    
    public SongLinkedList createSongFromValues() {
        float startbpm = 0.0f;
        try {
          startbpm = Float.parseFloat(AddSongsUI.instance.addsongsstartbpmfield.getText());
        } catch (Exception e) { }
        float endbpm = 0.0f;
        try {
            endbpm = Float.parseFloat(AddSongsUI.instance.addsongsendbpmfield.getText());
        } catch (Exception e) { }

        SongLinkedList newsong = new SongLinkedList();
        newsong.setArtist(AddSongsUI.instance.addsongsartistfield.getText());
        newsong.setAlbum(AddSongsUI.instance.addsongsalbumfield.getText());
        newsong.setTrack(AddSongsUI.instance.addsongstrackfield.getText());
        newsong.setSongname(AddSongsUI.instance.addsongstitlefield.getText());
        newsong.setRemixer(AddSongsUI.instance.addsongsremixerfield.getText());
        newsong.setComments(AddSongsUI.instance.addsongscommentsfield.getText());
        newsong.setVinylOnly(AddSongsUI.instance.addsongsvinylonly.isSelected());
        newsong.setNonVinylOnly(AddSongsUI.instance.addsongsnonvinylonly.isSelected());
        newsong.setStartbpm(startbpm);
        newsong.setEndbpm(endbpm);
        newsong.setStartkey(AddSongsUI.instance.addsongsstartkeyfield.getText());
        newsong.setEndkey(AddSongsUI.instance.addsongsendkeyfield.getText());
        newsong.setFilename(AddSongsUI.instance.addsongsfilenamefield.getText());
        newsong.setTime(AddSongsUI.instance.addsongstimefield.getText());
        newsong.setTimesig(AddSongsUI.instance.addsongstimesignaturefield.getText());
        if (addsongsrga != null && (addsongsrga.isValid()))
            newsong.setRGA(addsongsrga);
        if (addsongsbeatintensity != 0)
            newsong.setBeatIntensity(addsongsbeatintensity);
        newsong.setDisabled( AddSongsUI.instance.addsongsdisabled.isSelected());
        newsong.setUser1( AddSongsUI.instance.addcustomfieldtext1.getText());
        newsong.setUser2( AddSongsUI.instance.addcustomfieldtext2.getText());
        newsong.setUser3( AddSongsUI.instance.addcustomfieldtext3.getText());
        newsong.setUser4( AddSongsUI.instance.addcustomfieldtext4.getText());
        newsong.setKeyAccuracy(AddSongsUI.instance.addsongs_keyaccuracy.getValue());
        newsong.setBpmAccuracy(AddSongsUI.instance.addsongs_bpmaccuracy.getValue());

        if (rating5checkbox.isSelected()) newsong.setRating((char)5);
        else if (rating4checkbox.isSelected()) newsong.setRating((char)4);
        else if (rating3checkbox.isSelected()) newsong.setRating((char)3);
        else if (rating2checkbox.isSelected()) newsong.setRating((char)2);
        else if (rating1checkbox.isSelected()) newsong.setRating((char)1);
        else newsong.setRating((char)0);
        
        return newsong;
    }
}
