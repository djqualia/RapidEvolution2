package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.Set;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.audio.BPMTapper;
import rapid_evolution.audio.editdetectfilethread;
import com.mixshare.rapid_evolution.audio.tags.TagManager;
import rapid_evolution.filefilters.InputFileFilter;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.StylesUI;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

import com.mixshare.rapid_evolution.ui.swing.slider.MediaSlider;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

import java.util.Map;

public class EditSongUI extends REDialog implements ActionListener, FocusListener, WindowListener {

    private static Logger log = Logger.getLogger(EditSongUI.class);
        
    public EditSongUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
        editsongkeyprogress_ui = new EditSongKeyProgressUI("edit_song_detect_key_progress_dialog");
        editsongalbumcover_ui = new AlbumCoverUI("edit_song_album_cover_dialog");        
    }

    public static EditSongUI instance = null;

    public RETree editsongstylestree = new RETree();
    public JButton editsongsclearstylesbt = new REButton();
    public JButton editsongsaddstylebt = new REButton();
    public JButton editsongseditstylebt = new REButton();
    public JButton editsongsdeletestylebt = new REButton();
    public JButton editsongsokbutton = new REButton();
    public JButton editsongscancelbutton = new REButton();
    public AutoTextField editsongsartistfield = new AutoTextField();
    public JTextField editsongsremixerfield = new RETextField();
    public AutoTextField editsongsalbumfield = new AutoTextField();
    public JTextField editsongstrackfield = new RETextField();
    public JTextField editsongstimefield = new RETextField();
    public JTextField editsongstimesignaturefield = new RETextField();
    private int editsongsbeatintensity = 0;
    public JTextField editsongstitlefield = new RETextField();
    public JTextField editsongsstartbpmfield = new RETextField(); //(bpmFormat);
    public JTextField editsongsendbpmfield = new RETextField();
    public JTextField editsongsstartkeyfield = new RETextField();
    public JTextField editsongsendkeyfield = new RETextField();
    public JTextArea editsongscommentsfield = new RETextArea();
    public JTextField editsongsfilenamefield = new RETextField();
    public JCheckBox editsongsvinylonly = new RECheckBox();
    public JCheckBox editsongsnonvinylonly = new RECheckBox();
    public JCheckBox editsongsdisabled = new RECheckBox();
    public JButton editsongsdetectkeybutton = new REButton();
    public JButton editsongswritetagbutton = new REButton();
    public JButton editsongsrenamebutton = new REButton();
//    public JButton editsongsviewcolorbutton = new REButton("color");
    public JButton editsongsbrowsebutton = new REButton();
    public JButton editsongsreadtagbutton = new REButton();
    public JButton editsongsplaybutton = new REButton();
    public JButton editsongsstartbpmtapbutton = new REButton();
    public JButton editsongsstartbpmresetbutton = new REButton();
    public JButton editsongsendbpmtapbutton = new REButton();
    public JButton editsongsendbpmresetbutton = new REButton();
    public AutoTextField editcustomfieldtext1 = new  AutoTextField();
    public AutoTextField editcustomfieldtext2 = new  AutoTextField();
    public AutoTextField editcustomfieldtext3 = new  AutoTextField();
    public AutoTextField editcustomfieldtext4 = new  AutoTextField();
    public JLabel editcustomfieldlabel1;
    public JLabel editcustomfieldlabel2;
    public JLabel editcustomfieldlabel3;
    public JLabel editcustomfieldlabel4;
    public EditSongKeyProgressUI editsongkeyprogress_ui;
    public AlbumCoverUI editsongalbumcover_ui;
    public JSlider editsong_bpmaccuracy = new MediaSlider(JSlider.HORIZONTAL, 0, 100, 0);
    public JSlider editsong_keyaccuracy = new MediaSlider(JSlider.HORIZONTAL, 0, 100, 0);
    public JButton albumcover_btn = new REButton();
    
    public JButton backbutton = new REButton();
    public JButton nextbutton = new REButton();
    public JButton cleareditsongfields = new REButton();
    public JCheckBox rating1checkbox = new RECheckBox();
    public JCheckBox rating2checkbox = new RECheckBox();
    public JCheckBox rating3checkbox = new RECheckBox();
    public JCheckBox rating4checkbox = new RECheckBox();
    public JCheckBox rating5checkbox = new RECheckBox();
    
    
    private SongLinkedList editedsong;
    public SongLinkedList getEditedSong() {
        return editedsong;
    }

    public Map style_tree_map = java.util.Collections.synchronizedMap(new HashMap());
    
    private void setupDialog() {
            // edit songs dialog
            albumcover_btn.addActionListener(this);
            albumcover_btn.setEnabled(false);
            editsongsalbumfield.addKeyListener(new albumcoverkeylistener());
            //editsongsartistfield.addKeyListener(new albumcoverkeylistener());
            editsongstimefield.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                  editsongstimefield.setText(StringUtil.validate_time(editsongstimefield.getText()));
                }});
            editsongstimesignaturefield.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                editsongstimesignaturefield.setText(StringUtil.validate_timesig(editsongstimesignaturefield.getText()));
              }});
            editsongsstartbpmfield.addFocusListener(new FocusAdapter() {
                  public void focusLost(FocusEvent e) {
                    editsongsstartbpmfield.setText(StringUtil.validate_bpm(editsongsstartbpmfield.getText()));
                  }});
            editsongsendbpmfield.addFocusListener(new FocusAdapter() {
                  public void focusLost(FocusEvent e) {
                    editsongsendbpmfield.setText(StringUtil.validate_bpm(editsongsendbpmfield.getText()));
                  }});
            editsongsstartkeyfield.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                editsongsstartkeyfield.setText(StringUtil.validate_keyformat(editsongsstartkeyfield.getText()));
              }});
            editsongsendkeyfield.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                editsongsendkeyfield.setText(StringUtil.validate_keyformat(editsongsendkeyfield.getText()));
              }});
            editcustomfieldlabel1 = new RELabel(OptionsUI.instance.customfieldtext1.getText() + ":");
            editcustomfieldlabel2 = new RELabel(OptionsUI.instance.customfieldtext2.getText() + ":");
            editcustomfieldlabel3 = new RELabel(OptionsUI.instance.customfieldtext3.getText() + ":");
            editcustomfieldlabel4 = new RELabel(OptionsUI.instance.customfieldtext4.getText() + ":");

            editsongsreadtagbutton.setEnabled(false);
            editsongsplaybutton.setEnabled(false);
            editsongswritetagbutton.setEnabled(false);
            editsongsrenamebutton.setEnabled(false);
            editsongsplaybutton.setEnabled(false);
           editsongsfilenamefield.getDocument().addDocumentListener(new editsongsfilenamelistener());
           editsongsclearstylesbt.setEnabled(false);

           editsongsartistfield.addFocusListener(this);
           editsongsalbumfield.addFocusListener(this);
           editsongstrackfield.addFocusListener(this);
           editsongstitlefield.addFocusListener(this);
           editsongsremixerfield.addFocusListener(this);
           editsongstimefield.addFocusListener(this);
           editsongstimesignaturefield.addFocusListener(this);
           editsongsstartbpmfield.addFocusListener(this);
           editsongsendbpmfield.addFocusListener(this);
           editsongsstartkeyfield.addFocusListener(this);
           editsongscommentsfield.addFocusListener(this);
           editsongsfilenamefield.addFocusListener(this);
           editcustomfieldtext1.addFocusListener(this);
           editcustomfieldtext2.addFocusListener(this);
           editcustomfieldtext3.addFocusListener(this);
           editcustomfieldtext4.addFocusListener(this);
           rating1checkbox.addActionListener(this);
           rating2checkbox.addActionListener(this);
           rating3checkbox.addActionListener(this);
           rating4checkbox.addActionListener(this);
           rating5checkbox.addActionListener(this);

           cleareditsongfields.setVisible(false);
           
           backbutton.setEnabled(false);
           nextbutton.setEnabled(false);
           backbutton.addActionListener(this);
           nextbutton.addActionListener(this);
           
           editsongstylestree.setRootVisible(false);
           StylesUI.addTree(editsongstylestree, style_tree_map);
           editsongseditstylebt.setEnabled(false);
           editsongsdeletestylebt.setEnabled(false);           
           editsongstylestree.getSelectionModel().addTreeSelectionListener(new StyleClearSelectionHandler(editsongstylestree, editsongsclearstylesbt, editsongseditstylebt, editsongsaddstylebt, editsongsdeletestylebt));
       
           
    }

    public void PostInit() {
      if (!RapidEvolution.instance.loaded) {
        SkinManager.instance.setEnabled("edit_song_query_server_button", false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent we) {
                StopDetecting();
            }
        });
      }
    }

    int lastfocus = 3;
    public void focusGained(FocusEvent fe) {                
        if (fe.getSource() == editsongsartistfield) lastfocus = 0;
        else if (fe.getSource() == editsongsalbumfield) lastfocus = 1;            
        else if (fe.getSource() == editsongstrackfield) lastfocus = 2;            
        else if (fe.getSource() == editsongstitlefield) lastfocus = 3;            
        else if (fe.getSource() == editsongsremixerfield) lastfocus = 4;            

        else if (fe.getSource() == editsongstimefield) lastfocus = 5;            
        else if (fe.getSource() == editsongstimesignaturefield) lastfocus = 6;            
        else if (fe.getSource() == editsongsstartbpmfield) lastfocus = 7;            
        else if (fe.getSource() == editsongsendbpmfield) lastfocus = 8;            

        else if (fe.getSource() == editsongsstartkeyfield) lastfocus = 9;            
        else if (fe.getSource() == editsongsendkeyfield) lastfocus = 10;            
        else if (fe.getSource() == editsongscommentsfield) lastfocus = 11;            
        else if (fe.getSource() == editsongsfilenamefield) lastfocus = 12;            

        else if (fe.getSource() == editcustomfieldtext1) lastfocus = 13;            
        else if (fe.getSource() == editcustomfieldtext2) lastfocus = 14;            
        else if (fe.getSource() == editcustomfieldtext3) lastfocus = 15;            
        else if (fe.getSource() == editcustomfieldtext4) lastfocus = 16;            
        
    }
    public void focusLost(FocusEvent fe) {
    }    
    
    public void PostDisplay() {
        if (lastfocus == 0) editsongsartistfield.requestFocus();
        if (lastfocus == 1) editsongsalbumfield.requestFocus();
        if (lastfocus == 2) editsongstrackfield.requestFocus();
        if (lastfocus == 3) editsongstitlefield.requestFocus();
        if (lastfocus == 4) editsongsremixerfield.requestFocus();
        if (lastfocus == 5) editsongstimefield.requestFocus();
        if (lastfocus == 6) editsongstimesignaturefield.requestFocus();
        if (lastfocus == 7) editsongsstartbpmfield.requestFocus();
        if (lastfocus == 8) editsongsendbpmfield.requestFocus();
        if (lastfocus == 9) editsongsstartkeyfield.requestFocus();
        if (lastfocus == 10) editsongsendkeyfield.requestFocus();
        if (lastfocus == 11) editsongscommentsfield.requestFocus();
        if (lastfocus == 12) editsongsfilenamefield.requestFocus();
        if (lastfocus == 13) editcustomfieldtext1.requestFocus();
        if (lastfocus == 14) editcustomfieldtext2.requestFocus();
        if (lastfocus == 15) editcustomfieldtext3.requestFocus();
        if (lastfocus == 16) editcustomfieldtext4.requestFocus();        
    }
    
   class editsongsfilenamelistener implements javax.swing.event.DocumentListener {
    public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    private void updateVal() {
        String newfile = editsongsfilenamefield.getText();
        if (!newfile.equals("")) {
          File file = new File(newfile);
          if (file.exists() && file.isFile()) {
            editsongsreadtagbutton.setEnabled(true);
            editsongswritetagbutton.setEnabled(true);
            editsongsrenamebutton.setEnabled(true);
            editsongsplaybutton.setEnabled(true);
            return;
          }
        }
        editsongsreadtagbutton.setEnabled(false);
        editsongswritetagbutton.setEnabled(false);
        editsongsplaybutton.setEnabled(false);
        editsongsrenamebutton.setEnabled(false);
    }
   }

    public class EditSongGetKey extends Thread {
      public EditSongGetKey() {  }
      public void run() {
        AudioEngine.instance.editdetectingfromfile = true;
          editsongkeyprogress_ui.Display();
          editsongkeyprogress_ui.progressbar.setValue(0);
          new editdetectfilethread(editsongsfilenamefield.getText()).start();
      }
    }
    
    public void PopulateEditSongDialog(SongLinkedList datasong, boolean overwrite, boolean createStyles) {
      if (datasong == null) return;
      if (!datasong.getArtist().equals("")) {
    	  if (overwrite || editsongsartistfield.getText().equals(""))
    		  editsongsartistfield.setText(datasong.getArtist());
      }
      if (!datasong.getAlbum().equals("")) {
    	  if (overwrite || editsongsalbumfield.getText().equals(""))
    		  editsongsalbumfield.setText(datasong.getAlbum());
      }
      if (!datasong.getTrack().equals("")) {
    	  if (overwrite || editsongstrackfield.getText().equals(""))
    		  editsongstrackfield.setText(datasong.getTrack());
      }
      if (!datasong.getSongname().equals("")) {
    	  if (overwrite || editsongstitlefield.getText().equals(""))
    		  editsongstitlefield.setText(datasong.getSongname());
      }
      if (!datasong.getTime().equals("")) {
    	  if (overwrite || editsongstimefield.getText().equals(""))
    		  editsongstimefield.setText(datasong.getTime());
      }
      if (!datasong.getComments().equals("")) {
    	  if (overwrite || editsongscommentsfield.getText().equals(""))
    		  editsongscommentsfield.setText(datasong.getComments());
      }
      if (!datasong.getRemixer().equals("")) {
    	  if (overwrite || editsongsremixerfield.getText().equals(""))
    		  editsongsremixerfield.setText(datasong.getRemixer());
      }
      if (datasong.getBpmAccuracy() != 0) {
    	  if (overwrite || (editsong_bpmaccuracy.getValue() == 0))
    		  editsong_bpmaccuracy.setValue(datasong.getBpmAccuracy());
      }
      if (datasong.getStartbpm() != 0) {
    	  if (overwrite || editsongsstartbpmfield.getText().equals(""))
    		  editsongsstartbpmfield.setText(String.valueOf(datasong.getStartbpm()));
      }
      if (datasong.getEndbpm() != 0) {
    	  if (overwrite || editsongsendbpmfield.getText().equals(""))
    		  editsongsendbpmfield.setText(String.valueOf(datasong.getEndbpm()));
      }
      if (datasong.getKeyAccuracy() != 0) {
    	  if (overwrite || (editsong_keyaccuracy.getValue() == 0))
    		  editsong_keyaccuracy.setValue(datasong.getKeyAccuracy());
      }
      if (datasong.getStartKey().isValid()) {
    	  if (overwrite || editsongsstartkeyfield.getText().equals(""))
    		  editsongsstartkeyfield.setText(datasong.getStartKey().toStringExact());
      }
      if (datasong.getEndKey().isValid()) {
    	  if (overwrite || editsongsendkeyfield.getText().equals(""))
    		  editsongsendkeyfield.setText(datasong.getEndKey().toStringExact());
      }
      if (!datasong.getTimesig().equals("")) {
    	  if (overwrite || editsongstimesignaturefield.getText().equals(""))
    		  editsongstimesignaturefield.setText(datasong.getTimesig());
      }     
      if (datasong.getBeatIntensity() != 0) {
    	  if (overwrite || (editsongsbeatintensity == 0))
    		  editsongsbeatintensity = datasong.getBeatIntensity();
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
    	  if (overwrite || editcustomfieldtext1.getText().equals(""))
    		  editcustomfieldtext1.setText(datasong.getUser1());
      }
      if (!datasong.getUser2().equals("")) {
    	  if (overwrite || editcustomfieldtext2.getText().equals(""))
    		  editcustomfieldtext2.setText(datasong.getUser2());
      }
      if (!datasong.getUser3().equals("")) {
    	  if (overwrite || editcustomfieldtext3.getText().equals(""))
    		  editcustomfieldtext3.setText(datasong.getUser3());
      }
      if (!datasong.getUser4().equals("")) {
    	  if (overwrite || editcustomfieldtext4.getText().equals(""))
    		  editcustomfieldtext4.setText(datasong.getUser4());
      }
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
				                editsongstylestree.addSelectionPath(new TreePath(node.getPath()));
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
      
      if ((editsongsalbumfield.getText().length() == 0))
          albumcover_btn.setEnabled(false);
      else albumcover_btn.setEnabled(true);
      
    }
    
    public class SelectStyleRunnable extends AddStyleRunnable {
        public void run() {
        	if (log.isDebugEnabled())
        		log.debug("SelectStyleRunnable.run(): starting");        	
            Vector nodes = (Vector)style_tree_map.get(style);
            if (nodes != null) {
                for (int i = 0; i < nodes.size(); ++i) {
                    MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(i);
                    editsongstylestree.addSelectionPath(new TreePath(node.getPath()));
                }
            }
        }
    }

    public void EditSongsOkProc(SongLinkedList editedsong) {
        OldSongValues old_values = new OldSongValues(editedsong);
        //String uniqueid = SongLinkedList.calculate_unique_id(editsongsartistfield.getText(), editsongsalbumfield.getText(), editsongstrackfield.getText(), editsongstitlefield.getText(), editsongsremixerfield.getText());
        editedsong.setArtist(editsongsartistfield.getText());
        editedsong.setAlbum(editsongsalbumfield.getText());
        editedsong.setTrack(editsongstrackfield.getText());
        editedsong.setTime(editsongstimefield.getText());
        editedsong.setRemixer(editsongsremixerfield.getText());
        editedsong.setUser1(editcustomfieldtext1.getText());
        editedsong.setUser2(editcustomfieldtext2.getText());
        editedsong.setUser3(editcustomfieldtext3.getText());
        editedsong.setUser4( editcustomfieldtext4.getText());
        editedsong.setKeyAccuracy(editsong_keyaccuracy.getValue());
        editedsong.setBpmAccuracy(editsong_bpmaccuracy.getValue());
        editedsong.setTimesig(editsongstimesignaturefield.getText());
        if (editedsong.getTimesig().equals("")) editedsong.setTimesig("4/4");
        editedsong.setSongname(editsongstitlefield.getText());
        try {
          float bpm = Float.parseFloat(editsongsstartbpmfield.getText());
          if (bpm != 0) editedsong.setStartbpm(bpm);
        } catch (Exception e) { if (editsongsstartbpmfield.getText().equals("")) editedsong.setStartbpm(0); }
        try {
          float bpm = Float.parseFloat(editsongsendbpmfield.getText());
          if (bpm != 0) editedsong.setEndbpm(bpm);
        } catch (Exception e) { if (editsongsendbpmfield.getText().equals("")) editedsong.setEndbpm(0); }
        editedsong.setStartkey(editsongsstartkeyfield.getText());
        editedsong.setEndkey(editsongsendkeyfield.getText());
        editedsong.setComments(editsongscommentsfield.getText());
        editedsong.setFilename( editsongsfilenamefield.getText());
        editedsong.setVinylOnly(editsongsvinylonly.isSelected());
        editedsong.setNonVinylOnly( editsongsnonvinylonly.isSelected());
        editedsong.setDisabled(editsongsdisabled.isSelected());
        editedsong.setBeatIntensity(editsongsbeatintensity);
        if (rating5checkbox.isSelected()) editedsong.setRating((char)5);
        else if (rating4checkbox.isSelected()) editedsong.setRating((char)4);
        else if (rating3checkbox.isSelected()) editedsong.setRating((char)3);
        else if (rating2checkbox.isSelected()) editedsong.setRating((char)2);
        else if (rating1checkbox.isSelected()) editedsong.setRating((char)1);
        else editedsong.setRating((char)0);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode)editsongstylestree.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); ++i) {
            MyMutableStyleNode node = (MyMutableStyleNode)root.getChildAt(i);
            RecurseRemoveUnselected(node);
        }
        
        TreePath[] paths = editsongstylestree.getSelectionPaths();
        for (int j = 0; j < paths.length; ++j) {
            MyMutableStyleNode node = (MyMutableStyleNode)paths[j].getLastPathComponent();
            StyleLinkedList styleiter = node.getStyle();
            if (!styleiter.containsDirect(editedsong)) {
                log.trace("run(): adding style: " + styleiter + ", to song: " + editedsong);                                
                if (styleiter.containsExcludeSong(editedsong.uniquesongid))
                  styleiter.removeExcludeSong(editedsong);
                String[] ekeywords = styleiter.getExcludeKeywords();
              for (int i = 0; i < ekeywords.length; ++i) {
                if (styleiter.matchesExcludeKeywords(editedsong, ekeywords[i])) {
                  String display = SkinManager.instance.getDialogMessageText("delete_style_exclude_keyword");
                  display = StringUtil.ReplaceString("%style%", display, styleiter.getName());
                  display = StringUtil.ReplaceString("%keyword%", display, ekeywords[i]);
                  display = StringUtil.ReplaceString("%songid%", display, editedsong.getShortId());
                  int n = IOptionPane.showConfirmDialog(
                      SkinManager.instance.getFrame("main_frame"),
                      display,
                      SkinManager.instance.getDialogMessageTitle("delete_style_exclude_keyword"),
                      IOptionPane.YES_NO_OPTION);
                  if (n == 0) {
                    styleiter.removeExcludeKeyword(ekeywords[i]);
                    --i;
                  }
                }
              }
              styleiter.insertSong(editedsong);
            }            
        }
        SongDB.instance.UpdateSong(editedsong,old_values);
        //RapidEvolutionUI.instance.UpdateRoutine();
        if (MixGeneratorUI.instance.isVisible()) {
            MixGeneratorUI.instance.DisplayMix(MixGeneratorUI.instance.current_mix);
        }          
    }    
    public class EditSongsOkThread extends Thread {
      SongLinkedList editedsong = null;
      public EditSongsOkThread(SongLinkedList editsong) { editedsong = editsong; }
      public void run()  {
          EditSongsOkProc(editedsong);
      }
    }

    private void RecurseRemoveUnselected(MyMutableStyleNode node) {
        if (!editsongstylestree.getSelectionModel().isPathSelected(new TreePath(node.getPath())) && node.getStyle().containsDirect(editedsong)) {
            log.trace("RecurseRemoveUnselected(): removing style: " + node.getStyle() + ", from song: " + editedsong);
            node.getStyle().removeSong(editedsong);
            node.getStyle().insertExcludeSong(editedsong);            
        }
        for (int i = 0; i < node.getChildCount(); ++i) {
            MyMutableStyleNode child = (MyMutableStyleNode)node.getChildAt(i);
            RecurseRemoveUnselected(child);
        }
    }
    
    public void EditSong(SongLinkedList editedsong) {
      if (editedsong == null) return;
      if (!isVisible()) {
          this.editedsong = editedsong;
          InitEditSong();
        Display();
      } else requestFocus();

    }
    
    private void InitEditSong() {
        editsongsartistfield.setText(editedsong.getArtist());
        editsongsalbumfield.setText(editedsong.getAlbum());
        editsongstrackfield.setText(editedsong.getTrack());
        editsongstimefield.setText(editedsong.getTime());
        editsongsremixerfield.setText(editedsong.getRemixer());
        editsongstimesignaturefield.setText(editedsong.getTimesig());
        editsongstitlefield.setText(editedsong.getSongname());
        if (editedsong.getStartbpm() != 0) editsongsstartbpmfield.setText(String.valueOf(editedsong.getStartbpm()));
        else editsongsstartbpmfield.setText("");
        if (editedsong.getEndbpm() != 0) editsongsendbpmfield.setText(String.valueOf(editedsong.getEndbpm()));
        else editsongsendbpmfield.setText("");
        editsongsstartkeyfield.setText(editedsong.getStartKey().toStringExact());
        editsongsendkeyfield.setText(editedsong.getEndKey().toStringExact());
        editsongscommentsfield.setText(editedsong.getComments());
        editsongsfilenamefield.setText(editedsong.getFileName());
        editsongsvinylonly.setSelected(editedsong.getVinylOnly());
        editsongsnonvinylonly.setSelected(editedsong.getNonVinylOnly());
        editsongsdisabled.setSelected(editedsong.isDisabled());
        editcustomfieldtext1.setText(editedsong.getUser1());
        editcustomfieldtext2.setText(editedsong.getUser2());
        editcustomfieldtext3.setText(editedsong.getUser3());
        editcustomfieldtext4.setText(editedsong.getUser4());
        editsongsbeatintensity = editedsong.getBeatIntensity();
        editsong_bpmaccuracy.setValue(editedsong.getBpmAccuracy());
        editsong_keyaccuracy.setValue(editedsong.getKeyAccuracy());
        if (editedsong.getRating() == 0) {
            rating1checkbox.setSelected(false);
            rating2checkbox.setSelected(false);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);
        } else if (editedsong.getRating() == 1) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(false);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);            
        } else if (editedsong.getRating() == 2) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);            
        } else if (editedsong.getRating() == 3) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);            
        } else if (editedsong.getRating() == 4) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
            rating4checkbox.setSelected(true);
            rating5checkbox.setSelected(false);            
        } else if (editedsong.getRating() == 5) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
            rating4checkbox.setSelected(true);
            rating5checkbox.setSelected(true);            
        }

        if (!editsongsfilenamefield.getText().equals("")) {
          File file = new File(editsongsfilenamefield.getText());
          if (file.exists() && file.isFile()) {
            editsongsreadtagbutton.setEnabled(true);
            editsongswritetagbutton.setEnabled(true);
            editsongsrenamebutton.setEnabled(true);
            editsongsplaybutton.setEnabled(true);
          } else {
            editsongsreadtagbutton.setEnabled(false);
            editsongsplaybutton.setEnabled(false);
            editsongswritetagbutton.setEnabled(false);
            editsongsrenamebutton.setEnabled(false);
          }
        }
        else {
          editsongsreadtagbutton.setEnabled(false);
          editsongsplaybutton.setEnabled(false);
          editsongsrenamebutton.setEnabled(false);
          editsongswritetagbutton.setEnabled(false);
        }
        
        populateSongStyles(editedsong);
        editsongstylestree.minimizeTree();        
    }
    
    public void populateSongStyles(SongLinkedList song) {
        StyleLinkedList styleiter = SongDB.instance.masterstylelist;
        editsongstylestree.clearSelection();
        while (styleiter != null) {
          if (styleiter.containsDirect(song)) {
              log.trace("populateSongStyles(): song is a member of: " + styleiter);
              Vector nodes = (Vector)style_tree_map.get(styleiter);
              if (nodes != null) {
                  for (int i = 0; i < nodes.size(); ++i) {
                      MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(i);
                      log.trace("populateSongStyles(): adding selection path to node: " + node);
                      editsongstylestree.addSelectionPath(new TreePath(node.getPath()));
                  }
              }
          }
          styleiter = styleiter.next;
        }        
    }

    private void setupActionListeners() {
      editsongsclearstylesbt.addActionListener(this);
      editsongsokbutton.addActionListener(this);
      editsongswritetagbutton.addActionListener(this);
      editsongsrenamebutton.addActionListener(this);
//      editsongsviewcolorbutton.addActionListener(this);
      editsongscancelbutton.addActionListener(this);
      editsongsdetectkeybutton.addActionListener(this);
      editsongsvinylonly.addActionListener(this);
      editsongsnonvinylonly.addActionListener(this);
      editsongsbrowsebutton.addActionListener(this);
      editsongsstartbpmtapbutton.addActionListener(this);
      editsongsstartbpmresetbutton.addActionListener(this);
      editsongsendbpmtapbutton.addActionListener(this);
      editsongsendbpmresetbutton.addActionListener(this);
      editsongsreadtagbutton.addActionListener(this);
      editsongsplaybutton.addActionListener(this);
      editsongsdeletestylebt.addActionListener(this);
      editsongseditstylebt.addActionListener(this);
    }

    private boolean isValidData() {
        if (editsongsartistfield.getText().equals("") && editsongsalbumfield.getText().equals("") && editsongstrackfield.getText().equals("") && editsongstitlefield.getText().equals("") && editsongsremixerfield.getText().equals("")) {
            return false;
        }
        String uniqueid = SongLinkedList.calculate_unique_id(editsongsartistfield.getText(), editsongsalbumfield.getText(), editsongstrackfield.getText(), editsongstitlefield.getText(), editsongsremixerfield.getText());
        SongLinkedList testptr = SongDB.instance.OldGetSongPtr(uniqueid);
        if ((testptr == null) || (testptr == editedsong)) {
            return true;
        } else  return false;
    }
    
    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == editsongsokbutton) {
        if (AudioEngine.instance.editdetectingfromfile) return;
        StopDetecting();
        if (isValidData()) {
          new EditSongsOkThread(editedsong).start();
        } else {
            IOptionPane.showMessageDialog(getDialog(),
             SkinManager.instance.getDialogMessageText("invalid_song_information"),
            SkinManager.instance.getDialogMessageTitle("invalid_song_information"),
            IOptionPane.ERROR_MESSAGE);
          return;
        }
        setVisible(false);
      } else if (ae.getSource() == editsongseditstylebt) {
          if (editsongstylestree.getSelectionPath() != null) {
              MyMutableStyleNode selected_node = (MyMutableStyleNode)editsongstylestree.getSelectionPath().getLastPathComponent();
              EditStyleUI.instance.display_parameter = selected_node.getStyle();
              EditStyleUI.instance.Display();
          }                                    
      } else if (ae.getSource() == editsongsdetectkeybutton) {
            if (AudioEngine.instance.editdetecting) {
                StopDetecting();
              return;
            }
            int n = 0;
            if (!editsongsfilenamefield.getText().equals("")) {
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
              AudioEngine.instance.editdetecting = true;
              editsongsdetectkeybutton.setText(SkinManager.instance.getAltTextFor(editsongsdetectkeybutton));
              AudioEngine.instance.editCaptureAudio();
            }
            else if (n == 1) new EditSongGetKey().start();
      } else if (ae.getSource() == editsongsdeletestylebt) {
          StylesPane.deleteSelectedStyles(editsongstylestree);

      } else if (ae.getSource() == backbutton) {
          SongLinkedList nextsong = null;
          int index = -1;
          if (edit_table.equals(SearchPane.instance.searchtable)) {
              int searchindex = SearchPane.instance.getSearchViewIndex(editedsong);
              if (searchindex != -1) {
                  index = searchindex - 1;
                  if (index >= 0) {
                      nextsong = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(index, SearchPane.instance.searchcolumnconfig.num_columns);
                  }
              }
          }
          if (nextsong != null) {
              if (AudioEngine.instance.editdetectingfromfile) return;
              StopDetecting();
              if (isValidData()) {
                  ChangeEditSongTo(nextsong, index);
              } else {
                  IOptionPane.showMessageDialog(getDialog(),
                   SkinManager.instance.getDialogMessageText("invalid_song_information"),
                  SkinManager.instance.getDialogMessageTitle("invalid_song_information"),
                  IOptionPane.ERROR_MESSAGE);
                return;
              }
          } else {
              backbutton.setEnabled(false);
          }           
      } else if (ae.getSource() == nextbutton) {
          SongLinkedList nextsong = null;
          int index = -1;
          if (edit_table.equals(SearchPane.instance.searchtable)) {
	          int searchindex = SearchPane.instance.getSearchViewIndex(editedsong);
	          if (searchindex != -1) {
	              index = searchindex + 1;
	              if (index < edit_table.getRowCount()) {
	                  nextsong = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(index, SearchPane.instance.searchcolumnconfig.num_columns);
	              }
	          }
          }
          if (nextsong != null) {
              if (AudioEngine.instance.editdetectingfromfile) return;
              StopDetecting();
              if (isValidData()) {
                  ChangeEditSongTo(nextsong, index);
              } else {
                  IOptionPane.showMessageDialog(getDialog(),
                   SkinManager.instance.getDialogMessageText("invalid_song_information"),
                  SkinManager.instance.getDialogMessageTitle("invalid_song_information"),
                  IOptionPane.ERROR_MESSAGE);
                return;
              }
          } else {
              backbutton.setEnabled(false);
          }              
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
      
      } else if (ae.getSource() == editsongsbrowsebutton) {
            JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
            boolean setdir = false;
            if (!editedsong.getFileName().equals("")) {
              File efile = editedsong.getFile();
              String name = efile.getName();
              String path = efile.getPath();
              int pos = path.indexOf(name);
              efile = new File(path.substring(0, pos));
              if (efile.isDirectory()) {
                fc.setCurrentDirectory(efile);
                setdir = true;
              }
            }
            if (!setdir && !RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
            fc.addChoosableFileFilter(new InputFileFilter());
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showOpenDialog(getDialog());
            File tmp = fc.getSelectedFile();
            if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                  editsongsfilenamefield.setText((String)tmp.getAbsolutePath());
                  editsongsreadtagbutton.setEnabled(true);
                  editsongswritetagbutton.setEnabled(true);
                  editsongsrenamebutton.setEnabled(true);
                  editsongsplaybutton.setEnabled(true);
            }
            requestFocus();
      } else if (ae.getSource() == editsongscancelbutton) {
        if (AudioEngine.instance.editdetectingfromfile) return;
        StopDetecting();
        setVisible(false);
      } else if (ae.getSource() == editsongsplaybutton) {
            AudioPlayer.songsplaying = new Vector();
            SongLinkedList editedcopy = new SongLinkedList(editedsong);
            editedcopy.setFilename(editsongsfilenamefield.getText());            
            editedcopy.calculateSongDisplayIds(editedsong);
            AudioPlayer.songsplaying.add(editedcopy);
            AudioPlayer.PlaySongs();
      /*      if (editsongsplaying){
              editsongsplaybutton.setText("play");
              editsongsplaying = false;
              EditSongStopPlayback = true;
            } else {
              if ((synth != null) && (synth.isOpen())) synth.close();
              editsongsplaying = true;
              EditSongStopPlayback = false;
              editsongsplaybutton.setText("stop");
//        EditSongplayAudio(editsongsfilenamefield.getText());
              playAudioFile(editsongsfilenamefield.getText());
            }*/
      }/* else if (ae.getSource() == editsongsviewcolorbutton) {
            JOptionPane.showMessageDialog(editsongsdlg,
              editedsong.color.toString(),
              "song color information",
             JOptionPane.INFORMATION_MESSAGE);

      }*/ else if (ae.getSource() == editsongswritetagbutton) {
            SongLinkedList newsong = new SongLinkedList(editedsong);
            newsong.setKeyAccuracy(editsong_keyaccuracy.getValue());
            newsong.setStartkey(editsongsstartkeyfield.getText());
            newsong.setEndkey(editsongsendkeyfield.getText());
            newsong.setBpmAccuracy(editsong_bpmaccuracy.getValue());
            try {
              newsong.setStartbpm(Float.parseFloat(editsongsstartbpmfield.getText()));
            } catch (Exception e) { }
            try {
              newsong.setEndbpm(Float.parseFloat(editsongsendbpmfield.getText()));
            } catch (Exception e) { }
            newsong.setArtist(editsongsartistfield.getText());
            newsong.setAlbum(editsongsalbumfield.getText());
            newsong.setSongname(editsongstitlefield.getText());
            newsong.setTrack(editsongstrackfield.getText());
            newsong.setRemixer(editsongsremixerfield.getText());
            newsong.setTime(editsongstimefield.getText());
            newsong.setTimesig(editsongstimesignaturefield.getText());
            newsong.setComments(editsongscommentsfield.getText());
            if (rating5checkbox.isSelected()) newsong.setRating((char)5);
            else if (rating4checkbox.isSelected()) newsong.setRating((char)4);
            else if (rating3checkbox.isSelected()) newsong.setRating((char)3);
            else if (rating2checkbox.isSelected()) newsong.setRating((char)2);
            else if (rating1checkbox.isSelected()) newsong.setRating((char)1);
            newsong.setUser1(editcustomfieldtext1.getText());
            newsong.setUser2(editcustomfieldtext2.getText());
            newsong.setUser3(editcustomfieldtext3.getText());
            newsong.setUser4(editcustomfieldtext4.getText());
            String[] styles = null;
            TreePath[] paths = editsongstylestree.getSelectionPaths();
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
            TagManager.writeTags(newsong, editsongsfilenamefield.getText(), styles, editsongstylestree.getRootSelection());
      } else if (ae.getSource() == editsongsrenamebutton) {
          RenameFilesUI.instance.songs = new SongLinkedList[1];
          RenameFilesUI.instance.songs[0] = createSongFromValues();
          RenameFilesUI.instance.source_id = 2;
          RenameFilesUI.instance.Display();
      } else if (ae.getSource() == editsongsreadtagbutton) {
           SongLinkedList datasong = TagManager.readTags(editsongsfilenamefield.getText());
           PopulateEditSongDialog(datasong, true, OptionsUI.instance.createstylesfromgenretags.isSelected());
      } else if (ae.getSource() == editsongsclearstylesbt) {
            editsongstylestree.clearSelection();
      } else if (ae.getSource() == editsongsstartbpmtapbutton) {
            BPMTapper.instance.maintapped = false;
            BPMTapper.instance.addsongstartbpm1tapped = false;
            BPMTapper.instance.addsongendbpm1tapped = false;
            BPMTapper.instance.editsongstartbpm1tapped = true;
            BPMTapper.instance.editsongendbpm1tapped = false;
            BPMTapper.instance.TapBPM();
      } else if (ae.getSource() == editsongsstartbpmresetbutton) {
            BPMTapper.instance.addsongstartbpm1tapped = false;
            BPMTapper.instance.addsongendbpm1tapped = false;
            BPMTapper.instance.editsongstartbpm1tapped = true;
            BPMTapper.instance.editsongendbpm1tapped = false;
            BPMTapper.instance.m_numTicks = -1;
            BPMTapper.instance.ResetBpm();
      } else if (ae.getSource() == editsongsendbpmtapbutton) {
            BPMTapper.instance.maintapped = false;
            BPMTapper.instance.addsongstartbpm1tapped = false;
            BPMTapper.instance.addsongendbpm1tapped = false;
            BPMTapper.instance.editsongstartbpm1tapped = false;
            BPMTapper.instance.editsongendbpm1tapped = true;
            BPMTapper.instance.TapBPM();
      } else if (ae.getSource() == editsongsendbpmresetbutton) {
            BPMTapper.instance.addsongstartbpm1tapped = false;
            BPMTapper.instance.addsongendbpm1tapped = false;
            BPMTapper.instance.editsongstartbpm1tapped = false;
            BPMTapper.instance.editsongendbpm1tapped = true;
            BPMTapper.instance.m_numTicks = -1;
            BPMTapper.instance.ResetBpm();
      } else if (ae.getSource() == editsongsvinylonly) {
        editsongsnonvinylonly.setSelected(false);
      } else if (ae.getSource() == editsongsnonvinylonly) {
        editsongsvinylonly.setSelected(false);
      } else if (ae.getSource() == albumcover_btn) {
          String dir = FileUtil.getDirectoryFromFilename(editsongsfilenamefield.getText());
          if (dir != null) {
              editsongalbumcover_ui.previousfilepath = dir;
              editsongalbumcover_ui.pathisknown = true; 
          } else {
              editsongalbumcover_ui.pathisknown = false;
          }
          editsongalbumcover_ui.showAlbum(editsongsalbumfield.getText(),editsongsartistfield.getText());
      }
    }

    private void StopDetecting() {
      try {
        AudioEngine.instance.editdetecting = false;
        AudioEngine.instance.editTargetDataLine.stop();
        AudioEngine.instance.editTargetDataLine.close();
        editsongsdetectkeybutton.setText(SkinManager.instance.getTextFor(editsongsdetectkeybutton));
      } catch (Exception e) { }
    }
    
    public boolean PreDisplay() {
        if ((editsongsalbumfield.getText().length() == 0))
            albumcover_btn.setEnabled(false);
        else albumcover_btn.setEnabled(true);
        
        if (edit_table != null) {
            if (edit_table_initial_row + 1 < edit_table.getRowCount()) nextbutton.setEnabled(true);
            else nextbutton.setEnabled(false);
            if (edit_table_initial_row - 1 >= 0) backbutton.setEnabled(true);
            else backbutton.setEnabled(false);            
        } else {
            nextbutton.setEnabled(false);
            backbutton.setEnabled(false);            
        }
        
        
        return true;
    }
    
    private void ChangeEditSongTo(SongLinkedList song, int new_row) {
        if (song == null) return;
        if (new_row + 1 < edit_table.getRowCount()) nextbutton.setEnabled(true);
        else nextbutton.setEnabled(false);
        if (new_row - 1 >= 0) backbutton.setEnabled(true);
        else backbutton.setEnabled(false);  
        EditSongsOkProc(editedsong);
        editedsong = song;
        InitEditSong();                
    }

    public void windowActivated(java.awt.event.WindowEvent e) {
    }
    
    public void windowClosing(java.awt.event.WindowEvent e) {
    }    
    public void windowClosed(java.awt.event.WindowEvent e) {
        edit_table = null;
    }    
    
    RETable edit_table = null;
    int edit_table_initial_row = -1;
    public void setTableRow(RETable table, int row) {
        edit_table = table;
        edit_table_initial_row = row;
    }
    
    class albumcoverkeylistener extends KeyAdapter {
        public void keyReleased(KeyEvent e) {
            if ((editsongsalbumfield.getText().length() == 0))// ||
                    //(editsongsartistfield.getText().length() == 0))
                albumcover_btn.setEnabled(false);
            else albumcover_btn.setEnabled(true);
        }
      }   
    
    public SongLinkedList createSongFromValues() {
        float startbpm = 0.0f;
        try {
          startbpm = Float.parseFloat(EditSongUI.instance.editsongsstartbpmfield.getText());
        } catch (Exception e) { }
        float endbpm = 0.0f;
        try {
            endbpm = Float.parseFloat(EditSongUI.instance.editsongsendbpmfield.getText());
        } catch (Exception e) { }

        SongLinkedList newsong = new SongLinkedList();
        newsong.setArtist(EditSongUI.instance.editsongsartistfield.getText());
        newsong.setAlbum(EditSongUI.instance.editsongsalbumfield.getText());
        newsong.setTrack(EditSongUI.instance.editsongstrackfield.getText());
        newsong.setSongname(EditSongUI.instance.editsongstitlefield.getText());
        newsong.setRemixer(EditSongUI.instance.editsongsremixerfield.getText());
        newsong.setComments(EditSongUI.instance.editsongscommentsfield.getText());
        newsong.setVinylOnly(EditSongUI.instance.editsongsvinylonly.isSelected());
        newsong.setNonVinylOnly(EditSongUI.instance.editsongsnonvinylonly.isSelected());
        newsong.setStartbpm(startbpm);
        newsong.setEndbpm(endbpm);
        newsong.setStartkey(EditSongUI.instance.editsongsstartkeyfield.getText());
        newsong.setEndkey(EditSongUI.instance.editsongsendkeyfield.getText());
        newsong.setFilename(EditSongUI.instance.editsongsfilenamefield.getText());
        newsong.setTime(EditSongUI.instance.editsongstimefield.getText());
        newsong.setTimesig(EditSongUI.instance.editsongstimesignaturefield.getText());
        newsong.setDisabled( EditSongUI.instance.editsongsdisabled.isSelected());
        newsong.setUser1( EditSongUI.instance.editcustomfieldtext1.getText());
        newsong.setUser2( EditSongUI.instance.editcustomfieldtext2.getText());
        newsong.setUser3( EditSongUI.instance.editcustomfieldtext3.getText());
        newsong.setUser4( EditSongUI.instance.editcustomfieldtext4.getText());
        newsong.setKeyAccuracy(EditSongUI.instance.editsong_keyaccuracy.getValue());
        newsong.setBpmAccuracy(EditSongUI.instance.editsong_bpmaccuracy.getValue());
        return newsong;
    }    
    
    
    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

}
