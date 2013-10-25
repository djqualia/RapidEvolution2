package rapid_evolution.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.GenerateMixSet;
import rapid_evolution.MixoutObject;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongStack;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.comparables.MyStringFloat;
import rapid_evolution.filefilters.MixFileFilter;
import rapid_evolution.filefilters.SaveFileFilter;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.styles.StylesUI;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.button.RERadioButton;

import org.jvnet.substance.SubstanceLookAndFeel;

import com.mixshare.rapid_evolution.mix.SongTrail;
import com.mixshare.rapid_evolution.mix.SongTrailManager;

public class MixGeneratorUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(MixGeneratorUI.class);
    
    public MixGeneratorUI(String id) {
      super(id);
      instance = this;
      setupDialog();
      setupActionListeners();
    }

    static public MixGeneratorUI instance = null;
    public float averagerank;

    public JButton mixsetokbutton = new REButton();
    public JButton cancelmixsetbutton = new REButton();
    public JButton generatemixsetbutton = new REButton();
    public JButton loadmixsetbutton = new REButton();
    public JButton savemixsetbutton = new REButton();
//    public JButton importsongtrail = new REButton("import song trail");
    public JButton savetexttrail = new REButton();
    public JButton appendtosongtrail = new REButton();
    public JButton exporttosongtrail = new REButton();
    public JCheckBox closestmatchcheckbox = new RECheckBox();
    public JCheckBox avoidsongtrailcheckbox = new RECheckBox();
    public JRadioButton usenonvinylrb = new RERadioButton();
    public JRadioButton usevinylrb = new RERadioButton();
    public JRadioButton useallrb = new RERadioButton();
    public JTextField avgmixscorefield = new RETextField();
    public JTextField mixrankresults = new RETextField();
    public JCheckBox controlbpmrangecb = new RECheckBox();
    public JButton clearmixstylesbutton = new REButton();
    public RETree mixsetstylestree = new RETree();
    public JButton clearstartswith = new REButton();
    public SongLinkedList startswith = null;
    public boolean usestartswith = false;
    public JTextField startswithfield = new RETextField();
    public JTextField excludekeywordsfield = new RETextField();
    public JTextField includekeywordsfield = new RETextField();
    public JTextField numsongsmixsetfield = new RETextField();
    public JCheckBox weakincludecb = new RECheckBox();
    public JCheckBox minrankcb = new RECheckBox();
    public JTextField minrankfield = new RETextField();
    public JTextField maxresultsfield = new RETextField();
    public JButton fastprevmixsetbutton = new REButton();
    public JButton prevmixsetbutton = new REButton();
    public JButton nextmixsetbutton = new REButton();
    public JButton fastnextmixsetbutton = new REButton();
    public DragMixSetList mixgenlist = new DragMixSetList();

    //process variables
    public boolean generated = false;
    public SongLinkedList [] [] mixes;
    public int num_mixes;
    public int max_mixes = 1000;
    public boolean mixcancelled = false;
    public boolean isgenerating = false;
    public boolean waitingtocancel = false;
    public int current_mix = 0;
    public int num_current_songs = 0;
    public int numsongs = 15;

    private void setupDialog() {
        savemixsetbutton.setEnabled(false);
        appendtosongtrail.addActionListener(this);
        appendtosongtrail.setEnabled(false);
        exporttosongtrail.setEnabled(false);
        savetexttrail.setEnabled(false);
        cancelmixsetbutton.setEnabled(false);
        clearmixstylesbutton.setEnabled(false);
        clearstartswith.setEnabled(false);        
        
        avgmixscorefield.putClientProperty(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS , Boolean.TRUE);
        mixrankresults.putClientProperty(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS , Boolean.TRUE);        
        
        numsongsmixsetfield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            numsongsmixsetfield.setText(validate_numsongs(numsongsmixsetfield.getText()));
          }
        });

        minrankfield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            minrankfield.setText(StringUtil.validate_rank(minrankfield.getText()));
          }
        });
        mixgenlist.addMouseListener(new MixGenMouseHandler());
        mixgenlist.addKeyListener(new MixGenKeyListener());
        mixgenlist.setModel(new DefaultListModel());
        prevmixsetbutton.setEnabled(false);
        nextmixsetbutton.setEnabled(false);
        fastprevmixsetbutton.setEnabled(false);
        fastnextmixsetbutton.setEnabled(false);
        maxresultsfield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            maxresultsfield.setText(validate_maxmixes(maxresultsfield.getText()));
            try { MixGeneratorUI.instance.max_mixes = Integer.parseInt(maxresultsfield.getText()); } catch (Exception e1) { }
          }
        });
        
        mixsetstylestree.setRootVisible(false);
        StylesUI.addTree(mixsetstylestree, style_tree_map);
        mixsetstylestree.getSelectionModel().addTreeSelectionListener(new StyleClearSelectionHandler(mixsetstylestree, clearmixstylesbutton));
        
    }
    
    public Map style_tree_map =java.util.Collections.synchronizedMap(new HashMap());

    public class DragMixSetList extends REList implements DragSourceListener, DragGestureListener {
        public DragMixSetList() {
          super();
          dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
        }
        DragSource dragSource = DragSource.getDefaultDragSource();
        public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}
        public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}
        public void dragExit(DragSourceEvent DragSourceEvent){}
        public void dragOver(DragSourceDragEvent DragSourceDragEvent){}
        public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}
        public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
          int index = mixgenlist.getSelectedIndex();
          if (index < 0) return;
          if (MixGeneratorUI.instance.num_mixes > 0) {
            int count = 0;
            for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) {
              boolean make = false;
              if (count == index) make = true;
              else if (i < MixGeneratorUI.instance.numsongs - 1) {
                int sindex = -1;
                for (int z = 0; z < MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getNumMixoutSongs(); ++z) if (MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].mixout_songs[z] == MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i + 1].uniquesongid) sindex = z;
                if (sindex >= 0) {
                  count++;
                  if (count == index) make = true;
                }
              }
              if (make) {
                FileSelection transferable = new FileSelection(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getFile(), MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i]);
                try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
                catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
                return;
              }
              count++;
            }
          }
        }
    }

    private void setupActionListeners() {
        mixsetokbutton.addActionListener(this);
        cancelmixsetbutton.addActionListener(this);
        loadmixsetbutton.addActionListener(this);
        generatemixsetbutton.addActionListener(this);
        savemixsetbutton.addActionListener(this);
        savetexttrail.addActionListener(this);
        exporttosongtrail.addActionListener(this);
        useallrb.addActionListener(this);
        usevinylrb.addActionListener(this);
        usenonvinylrb.addActionListener(this);
        clearmixstylesbutton.addActionListener(this);
        clearstartswith.addActionListener(this);
        nextmixsetbutton.addActionListener(this);
        prevmixsetbutton.addActionListener(this);
        fastnextmixsetbutton.addActionListener(this);
        fastprevmixsetbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == mixsetokbutton) {
        setVisible(false);
      } else if (ae.getSource() == cancelmixsetbutton) {
        MixGeneratorUI.instance.waitingtocancel = true;
      } else if (ae.getSource() == usevinylrb) {
          usevinylrb.setSelected(true);
          useallrb.setSelected(false);
          usenonvinylrb.setSelected(false);
      } else if (ae.getSource() == usenonvinylrb) {
          usevinylrb.setSelected(false);
          useallrb.setSelected(false);
          usenonvinylrb.setSelected(true);
      } else if (ae.getSource() == useallrb) {
          usevinylrb.setSelected(false);
          useallrb.setSelected(true);
          usenonvinylrb.setSelected(false);
      } else if (ae.getSource() == clearstartswith) {
          startswith = null;
          usestartswith = false;
          startswithfield.setText(new String(""));
          clearstartswith.setEnabled(false);
      } else if (ae.getSource() == clearmixstylesbutton) {
        mixsetstylestree.clearSelection();
        clearmixstylesbutton.setEnabled(false);
    } else if (ae.getSource() == nextmixsetbutton) {
        DisplayMix(MixGeneratorUI.instance.current_mix + 1);
    } else if (ae.getSource() == prevmixsetbutton) {
        DisplayMix(MixGeneratorUI.instance.current_mix - 1);
    } else if (ae.getSource() == fastnextmixsetbutton) {
        int next = (int) (0.1f * (float) MixGeneratorUI.instance.num_mixes);
        if (next < 1) next = 1;
        DisplayMix(MixGeneratorUI.instance.current_mix + next);
    } else if (ae.getSource() == fastprevmixsetbutton) {
        int next = (int) (0.1f * (float) MixGeneratorUI.instance.num_mixes);
        if (next < 1) next = 1;
        DisplayMix(MixGeneratorUI.instance.current_mix - next);
      } else if (ae.getSource() == exporttosongtrail) {
          if (MixGeneratorUI.instance.num_mixes > 0) {
              RapidEvolutionUI.instance.prevstack = null;
              RapidEvolutionUI.instance.nextstack = null;
              RapidEvolutionUI.instance.backbutton.setEnabled(false);
              RapidEvolutionUI.instance.nextbutton.setEnabled(true);
              for (int i = MixGeneratorUI.instance.numsongs - 1; i >= 1; --i) {
                  RapidEvolutionUI.instance.nextstack = new SongStack(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].uniquesongid,
                                            RapidEvolutionUI.instance.nextstack);
              }
              SearchPane.instance.bpmslider.setValue(0);
              RapidEvolutionUI.instance.change_current_song(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][0], 0, true, true);
          }
      } else if (ae.getSource() == loadmixsetbutton) {
          JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
          if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
          fc.addChoosableFileFilter(new MixFileFilter());
          fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fc.setMultiSelectionEnabled(false);
          int returnVal = fc.showOpenDialog(getDialog());
          File tmp = fc.getSelectedFile();
          if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
          if (returnVal == JFileChooser.APPROVE_OPTION) {
              String filestr = (String) tmp.getAbsolutePath();
              SongTrail songTrail = SongTrailManager.loadSongTrail(filestr);
              if (songTrail != null) {
                  MixGeneratorUI.instance.mixes = new SongLinkedList[1][];
                  MixGeneratorUI.instance.num_mixes = 0;
                  MixGeneratorUI.instance.numsongs = songTrail.getNumSongs();
                  Vector inputsongs = new Vector();
                  MixGeneratorUI.instance.mixes[0] = new SongLinkedList[MixGeneratorUI.instance.numsongs];
                  for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) {
                      String input = songTrail.getSong(i).uniquestringid;
                      inputsongs.add(input);
                      MixGeneratorUI.instance.mixes[0][i] = SongDB.instance.OldGetSongPtr(input);
                      mixrankresults.setText("");
                      DefaultListModel dlm = (DefaultListModel) mixgenlist.getModel();
                      dlm.removeAllElements();
                      ScrubSongTrail2(inputsongs, 0);
                  }                  
              }              
          }
      } else if (ae.getSource() == savemixsetbutton) {
           if (MixGeneratorUI.instance.num_mixes <= 0) return;
           JFileChooser fc = new JFileChooser();
           if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
           fc.addChoosableFileFilter(new MixFileFilter());
           fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
           fc.setMultiSelectionEnabled(false);
           int returnVal = fc.showSaveDialog(getDialog());
           File tmp = fc.getSelectedFile();
           if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
           if (returnVal == JFileChooser.APPROVE_OPTION) {
             String filestr = (String)tmp.getAbsolutePath();
             SongTrailManager.saveSongTrail(new SongTrail(MixGeneratorUI.instance.numsongs, MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix]), filestr);
           }
       } else if (ae.getSource() == savetexttrail) {
         PrintMix(MixGeneratorUI.instance.current_mix);
       }  else if (ae.getSource() == appendtosongtrail) {
         if (MixGeneratorUI.instance.num_mixes > 0) {
             SongStack tmpstack = null;
             for (int i = MixGeneratorUI.instance.numsongs - 1; i >= 1; --i) {
                 tmpstack = new SongStack(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].uniquesongid,
                                          tmpstack);
             }
             if (RapidEvolutionUI.instance.currentsong == null) {
                 SearchPane.instance.bpmslider.setValue(0);
                 RapidEvolutionUI.instance.change_current_song(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][0], 0, false, true);
             } else tmpstack = new SongStack(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][0].uniquesongid,
                                             tmpstack);
             if (RapidEvolutionUI.instance.nextstack == null) {
                 RapidEvolutionUI.instance.nextstack = tmpstack;
                 RapidEvolutionUI.instance.nextbutton.setEnabled(true);
             } else {
                 SongStack iter = RapidEvolutionUI.instance.nextstack;
                 while (iter.next != null) iter = iter.next;
                 iter.next = tmpstack;
             }
             SongTrailUI.instance.StartRedrawSongTrailThread();
         }
       } else if (ae.getSource() == generatemixsetbutton) {
          if (!MixGeneratorUI.instance.isgenerating) {

            MixGeneratorUI.instance.generated = false;

            DisableMixGenUI();
            cancelmixsetbutton.setEnabled(true);

              avgmixscorefield.setText("");
              MixGeneratorUI.instance.mixes = null;
              MixGeneratorUI.instance.num_mixes = 0;
              DefaultListModel dlm = (DefaultListModel) mixgenlist.getModel();
              dlm.removeAllElements();
              mixrankresults.setText("");
              MixGeneratorUI.instance.isgenerating = true;
              MixGeneratorUI.instance.mixcancelled = false;
              new GenerateMixSet().start();
              MixGeneratorUI.instance.mixcancelled = true;
          }
      }
    }

    public void EnableMixGenUI() {
      cancelmixsetbutton.setEnabled(false);
      generatemixsetbutton.setEnabled(true);
      closestmatchcheckbox.setEnabled(true);
      avoidsongtrailcheckbox.setEnabled(true);
      controlbpmrangecb.setEnabled(true);
      minrankcb.setEnabled(true);
      weakincludecb.setEnabled(true);

      mixsetstylestree.setFocusable(true);
      clearmixstylesbutton.setFocusable(true);
      usenonvinylrb.setFocusable(true);
      usevinylrb.setFocusable(true);
      useallrb.setFocusable(true);
      maxresultsfield.setFocusable(true);
      appendtosongtrail.setFocusable(true);
      includekeywordsfield.setFocusable(true);
      excludekeywordsfield.setFocusable(true);
      numsongsmixsetfield.setFocusable(true);
      minrankfield.setFocusable(true);
      clearstartswith.setFocusable(true);
      loadmixsetbutton.setFocusable(true);
      savemixsetbutton.setFocusable(true);

      Iterator old_cursors_iter = old_cursors.entrySet().iterator();
      while (old_cursors_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_cursors_iter.next();
        JComponent component = (JComponent)entry.getKey();
        Cursor cursor = (Cursor)entry.getValue();
        component.setCursor(cursor);
      }
      old_cursors = new HashMap();
    }

    private HashMap old_cursors = new HashMap();
    private void DisableMixGenUI() {
      mixsetstylestree.setFocusable(false);
      clearmixstylesbutton.setFocusable(false);
      usenonvinylrb.setFocusable(false);
      usevinylrb.setFocusable(false);
      useallrb.setFocusable(false);
      maxresultsfield.setFocusable(false);
      appendtosongtrail.setFocusable(false);
      exporttosongtrail.setFocusable(false);
      includekeywordsfield.setFocusable(false);
      excludekeywordsfield.setFocusable(false);
      numsongsmixsetfield.setFocusable(false);
      minrankfield.setFocusable(false);
      clearstartswith.setFocusable(false);
      loadmixsetbutton.setFocusable(false);
      savemixsetbutton.setFocusable(false);

      old_cursors.put(mixsetstylestree, mixsetstylestree.getCursor());
      old_cursors.put(clearmixstylesbutton, clearmixstylesbutton.getCursor());
      old_cursors.put(usenonvinylrb, usenonvinylrb.getCursor());
      old_cursors.put(usevinylrb, usevinylrb.getCursor());
      old_cursors.put(useallrb, useallrb.getCursor());
      old_cursors.put(maxresultsfield, maxresultsfield.getCursor());
      old_cursors.put(appendtosongtrail, appendtosongtrail.getCursor());
      old_cursors.put(exporttosongtrail, exporttosongtrail.getCursor());
      old_cursors.put(includekeywordsfield, includekeywordsfield.getCursor());
      old_cursors.put(excludekeywordsfield, excludekeywordsfield.getCursor());
      old_cursors.put(numsongsmixsetfield, numsongsmixsetfield.getCursor());
      old_cursors.put(minrankfield, minrankfield.getCursor());
      old_cursors.put(clearstartswith, clearstartswith.getCursor());
      old_cursors.put(loadmixsetbutton, loadmixsetbutton.getCursor());
      old_cursors.put(savemixsetbutton, savemixsetbutton.getCursor());

      mixsetstylestree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      clearmixstylesbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      usenonvinylrb.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      usevinylrb.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      useallrb.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      maxresultsfield.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      appendtosongtrail.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      exporttosongtrail.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      includekeywordsfield.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      excludekeywordsfield.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      numsongsmixsetfield.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      minrankfield.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      clearstartswith.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      loadmixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      savemixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      closestmatchcheckbox.setEnabled(false);
      avoidsongtrailcheckbox.setEnabled(false);
      controlbpmrangecb.setEnabled(false);
      minrankcb.setEnabled(false);
      weakincludecb.setEnabled(false);
      appendtosongtrail.setEnabled(false);
      generatemixsetbutton.setEnabled(false);
      prevmixsetbutton.setEnabled(false);
      nextmixsetbutton.setEnabled(false);
      fastprevmixsetbutton.setEnabled(false);
      fastnextmixsetbutton.setEnabled(false);
      savetexttrail.setEnabled(false);
      savemixsetbutton.setEnabled(false);
      appendtosongtrail.setEnabled(false);
      exporttosongtrail.setEnabled(false);

      old_cursors.put(closestmatchcheckbox, closestmatchcheckbox.getCursor());
      old_cursors.put(avoidsongtrailcheckbox, avoidsongtrailcheckbox.getCursor());
      old_cursors.put(controlbpmrangecb, controlbpmrangecb.getCursor());
      old_cursors.put(minrankcb, minrankcb.getCursor());
      old_cursors.put(weakincludecb, weakincludecb.getCursor());
      old_cursors.put(appendtosongtrail, appendtosongtrail.getCursor());
      old_cursors.put(generatemixsetbutton, generatemixsetbutton.getCursor());
      old_cursors.put(prevmixsetbutton, prevmixsetbutton.getCursor());
      old_cursors.put(nextmixsetbutton, nextmixsetbutton.getCursor());
      old_cursors.put(fastprevmixsetbutton, fastprevmixsetbutton.getCursor());
      old_cursors.put(fastnextmixsetbutton, fastnextmixsetbutton.getCursor());
      old_cursors.put(savetexttrail, savetexttrail.getCursor());
      old_cursors.put(savemixsetbutton, savemixsetbutton.getCursor());
      old_cursors.put(appendtosongtrail, appendtosongtrail.getCursor());
      old_cursors.put(exporttosongtrail, exporttosongtrail.getCursor());

      closestmatchcheckbox.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      avoidsongtrailcheckbox.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      controlbpmrangecb.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      minrankcb.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      weakincludecb.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      appendtosongtrail.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      generatemixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      prevmixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      nextmixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      fastprevmixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      fastnextmixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      savetexttrail.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      savemixsetbutton.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      appendtosongtrail.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      exporttosongtrail.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    Semaphore DisplayMixSem = new Semaphore(1);
    public void DisplayMix(int inputmix) {
      try {
      DisplayMixSem.acquire();
      if (inputmix >= MixGeneratorUI.instance.num_mixes) inputmix = MixGeneratorUI.instance.num_mixes - 1;
      if (inputmix < 0) inputmix = 0;
      DefaultListModel dlm = (DefaultListModel) mixgenlist.getModel();
      dlm.removeAllElements();
      if (MixGeneratorUI.instance.num_mixes != 1) mixrankresults.setText(String.valueOf(MixGeneratorUI.instance.num_mixes) + new String(" mixes"));
      else mixrankresults.setText(String.valueOf(MixGeneratorUI.instance.num_mixes) + new String(" mix"));
      if (MixGeneratorUI.instance.num_mixes > 0) {
        MixGeneratorUI.instance.current_mix = inputmix;
        if (inputmix == 0) {
          prevmixsetbutton.setEnabled(false);
          fastprevmixsetbutton.setEnabled(false);
        }
        else {
          prevmixsetbutton.setEnabled(true);
          fastprevmixsetbutton.setEnabled(true);
        }
        if (inputmix == (MixGeneratorUI.instance.num_mixes - 1)) {
          fastnextmixsetbutton.setEnabled(false);
          nextmixsetbutton.setEnabled(false);
        }
        else {
          nextmixsetbutton.setEnabled(true);
          fastnextmixsetbutton.setEnabled(true);
        }
        averagerank = 0;
        MixGeneratorUI.instance.num_current_songs = MixGeneratorUI.instance.numsongs;
        for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) {
          int count = i + 1;
          String tempstr;
          if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 10))
            tempstr = new String("00");
          else if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 100))
            tempstr = new String("0");
          else if ( (MixGeneratorUI.instance.numsongs >= 10) && (count < 10))
            tempstr = new String("0");
          else
            tempstr = new String("");
          tempstr += String.valueOf(count) + new String(":   ");
          tempstr += MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getSongId();
          dlm.insertElementAt(tempstr, dlm.getSize());
          if (i < MixGeneratorUI.instance.numsongs - 1) {
            for (int z = 0; z < MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getNumMixoutSongs(); ++z) {
              if (MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].mixout_songs[z] == MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i + 1].uniquesongid) {
                String tempstr2;
                if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 10))
                  tempstr2 = new String("00");
                else if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 100))
                  tempstr2 = new String("0");
                else if ( (MixGeneratorUI.instance.numsongs >= 10) && (count < 10))
                  tempstr2 = new String("0");
                else
                  tempstr2 = new String("");
                tempstr2 += String.valueOf(count) + new String(">      [");
                tempstr2 += String.valueOf(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getMixoutRank(z)) + "]   (";
                tempstr2 += new MyStringFloat(String.valueOf(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getMixoutBpmdiff(z))).toString();
                tempstr2 += ")   ";
                tempstr2 += MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getMixoutComments(z);
                dlm.insertElementAt(tempstr2, dlm.getSize());
                averagerank += MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getMixoutRank(z);
              }
            }
          }
        }
        averagerank /= (MixGeneratorUI.instance.num_current_songs - 1);
        String scoretext = String.valueOf(averagerank);
        int length = scoretext.length();
        if (length > 6) length = 6;
        avgmixscorefield.setText(scoretext.substring(0,length));
        savemixsetbutton.setEnabled(true);
      }
      else {
        savemixsetbutton.setEnabled(false);
        avgmixscorefield.setText("");
        savetexttrail.setEnabled(false);
        savemixsetbutton.setEnabled(false);
        appendtosongtrail.setEnabled(false);
        exporttosongtrail.setEnabled(false);
      }
      } catch (Exception e) { }
      DisplayMixSem.release();
   }

   public void ScrubSongTrail2(Vector songstrings2, int startval) {
     SongTrailUI.instance.replacesongmode = 1;
     SongTrailUI.instance.replacevector2 = songstrings2;
     for (int i = startval; i < songstrings2.size(); ++i) if (SongDB.instance.OldGetSongPtr((String)songstrings2.get(i)) == null) {
       SongTrailUI.instance.replaceindex2 = i;
       if (SongDB.instance.OldGetSongPtr(((String)songstrings2.get(i)).toLowerCase()) != null)  {
         MixGeneratorUI.instance.mixes[0][i] = SongDB.instance.OldGetSongPtr(((String)songstrings2.get(i)).toLowerCase()); //songstrings2.set(i, ((String)songstrings2.get(i)).toLowerCase());
       } else {
         String missingsong = StringUtil.ReplaceString("%songid%", SkinManager.instance.getDialogMessageText("load_song_trail_missing_song"), (String)songstrings2.get(i));
         int n = IOptionPane.showConfirmDialog(
             getDialog(),
             missingsong,
             SkinManager.instance.getDialogMessageTitle("load_song_trail_missing_song"),
             IOptionPane.YES_NO_OPTION);
         if (n != 0) return;
         SelectMissingSongUI.instance.selectmissingsongsfield.setText("");
         DefaultListModel dlm = (DefaultListModel) SelectMissingSongUI.instance.selectmissingsonglist.getModel();
         dlm.removeAllElements();
         SelectMissingSongUI.instance.selectmixxingsongsvector.removeAllElements();
         SongLinkedList iter = SongDB.instance.SongLL;
         while (iter != null) {
           dlm.addElement(iter.getSongId());
           SelectMissingSongUI.instance.selectmixxingsongsvector.add(iter);
           iter = iter.next;
         }
         SelectMissingSongUI.instance.setVisible(true);
         SelectMissingSongUI.instance.selectmissingsongsfield.requestFocus();
         return;
       }
     }
     for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) if (MixGeneratorUI.instance.mixes[0][i] == null) return;
     MixGeneratorUI.instance.num_mixes = 1;
     DisplayMix(0);
     savetexttrail.setEnabled(true);
     appendtosongtrail.setEnabled(true);
     exporttosongtrail.setEnabled(true);
     mixrankresults.setText("1 mix");
   }


  public boolean isMemberOfMixStyle(SongLinkedList song) {
    if (mixsetstylestree.isSelectionEmpty()) return true;
    TreePath[] paths = mixsetstylestree.getSelectionPaths();
    for (int i = 0; i < paths.length; ++i) {
        MyMutableStyleNode node = (MyMutableStyleNode)paths[i].getLastPathComponent();
        StyleLinkedList styleiter = node.getStyle();
        if (styleiter.containsLogical(song)) return true;
    }
    return false;
  }

  String validate_numsongs(String input) {
    if ((input == null) || (input.equals(""))) return new String("2");
    try {
      int rank = Integer.parseInt(input);
      if (rank < 2) return new String("2");
      return input;
    } catch (Exception e) { }
    return new String("2");
  }

  String validate_maxmixes(String input) {
    if ((input == null) || (input.equals(""))) return new String("1");
    try {
      int rank = Integer.parseInt(input);
      if (rank < 1) return new String("1");
      return input;
    } catch (Exception e) { }
    return new String("1");
  }

  class MixGenKeyListener extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == e.VK_DELETE) {
        DefaultListModel dlm = (DefaultListModel) mixgenlist.getModel();
        if (mixgenlist.getSelectedIndex() >= 0) {
          int count = 0;
          for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) {
            if (i < MixGeneratorUI.instance.numsongs - 1) {
              int sindex = -1;
              for (int z = 0; z < MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getNumMixoutSongs(); ++z) if (MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].mixout_songs[z] == MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i + 1].uniquesongid) sindex = z;
              if (sindex >= 0) {
                count++;
                if (count == mixgenlist.getSelectedIndex()) {
                  int n = IOptionPane.showConfirmDialog(
                      SkinManager.instance.getFrame("main_frame"),
                      SkinManager.instance.getDialogMessageText("delete_mixout_confirm"),
                      SkinManager.instance.getDialogMessageTitle("delete_mixout_confirm"),
                      IOptionPane.YES_NO_OPTION);
                  if (n == 0) {
                    MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].removeMixOut(sindex);
                    MixoutPane.instance.RedrawMixoutTable();
                    DisplayMix(MixGeneratorUI.instance.current_mix);
                  }
                }
              }
            }
            count++;
          }
        }
      }
    }
  }

  class MixGenMouseHandler extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
        Point pt = e.getPoint();
        DefaultListModel dlm = (DefaultListModel) mixgenlist.getModel();
        int index = mixgenlist.locationToIndex(pt);
        if (index < 0) return;
        if (MixGeneratorUI.instance.num_mixes > 0) {
          int count = 0;
          for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) {
            boolean make = false;
            if (count == index) make = true;
            else if (i < MixGeneratorUI.instance.numsongs - 1) {
              int sindex = -1;
              for (int z = 0; z < MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getNumMixoutSongs(); ++z) if (MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].mixout_songs[z] == MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i + 1].uniquesongid) sindex = z;
              if (sindex >= 0) {
                count++;
                if (count == index) make = true;
              }
            }
            if (make) {
              int j = i;
              RapidEvolutionUI.instance.prevstack = null;
              for (i = 0; i < j; ++i) RapidEvolutionUI.instance.prevstack = new SongStack(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].uniquesongid, RapidEvolutionUI.instance.prevstack);
              RapidEvolutionUI.instance.nextstack = null;
              for (i = j + 1; i < MixGeneratorUI.instance.numsongs; ++i) RapidEvolutionUI.instance.nextstack = new SongStack(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].uniquesongid, RapidEvolutionUI.instance.nextstack);
              SearchPane.instance.bpmslider.setValue(0);
              RapidEvolutionUI.instance.change_current_song(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][j], 0, true, true);
              return;
            }
            count++;
          }
        }
      } else if (SwingUtilities.isRightMouseButton(e)) {
        Point pt = e.getPoint();
        DefaultListModel dlm = (DefaultListModel) mixgenlist.getModel();
        int index = mixgenlist.locationToIndex(pt);
        if (index < 0) return;
        int count = 0;
        if (MixGeneratorUI.instance.num_mixes > 0) {
          for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) {
            if (index == count) {
              EditSongUI.instance.EditSong(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i]);
            } else if ((index == count + 1) && (i < MixGeneratorUI.instance.numsongs - 1)) {
              int sindex = -1;
              for (int z = 0; z < MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getNumMixoutSongs(); ++z) if (MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].mixout_songs[z] == MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i + 1].uniquesongid) sindex = z;
              if (sindex >= 0) {
                EditMixoutMixGenUI.instance.editedmixout2 = new MixoutObject(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i], sindex);
                if (!EditMixoutMixGenUI.instance.isVisible()) {
                  EditMixoutMixGenUI.instance.editmixout2fromsong = EditMixoutMixGenUI.instance.editedmixout2.song;
                  EditMixoutMixGenUI.instance.editmixout2tosong = SongDB.instance.NewGetSongPtr(EditMixoutMixGenUI.instance.editedmixout2.song.
                      mixout_songs[EditMixoutMixGenUI.instance.editedmixout2.index]);
                  EditMixoutMixGenUI.instance.editmixout2fromfield.setText(EditMixoutMixGenUI.instance.editmixout2fromsong.getSongIdShort());
                  EditMixoutMixGenUI.instance.editmixout2tofield.setText(EditMixoutMixGenUI.instance.editmixout2tosong.getSongIdShort());
                  EditMixoutMixGenUI.instance.editmixout2commentsfield.setText(EditMixoutMixGenUI.instance.editedmixout2.song.getMixoutComments(EditMixoutMixGenUI.instance.editedmixout2.index));
                  EditMixoutMixGenUI.instance.editmixout2bpmdifffield.setText(String.valueOf(EditMixoutMixGenUI.instance.editedmixout2.
                      song.getMixoutBpmdiff(EditMixoutMixGenUI.instance.editedmixout2.index)));
                  EditMixoutMixGenUI.instance.editmixout2scorefield.setText(String.valueOf(EditMixoutMixGenUI.instance.editedmixout2.
                      song.getMixoutRank(EditMixoutMixGenUI.instance.editedmixout2.index)));
                  EditMixoutMixGenUI.instance.editmixout2addoncb.setSelected(EditMixoutMixGenUI.instance.editedmixout2.song.getMixoutAddon(EditMixoutMixGenUI.instance.editedmixout2.index));
                  EditMixoutMixGenUI.instance.Display();
                }
                else
                  EditMixoutMixGenUI.instance.requestFocus();
              }
            }
            if (i < MixGeneratorUI.instance.numsongs - 1) {
              int sindex = -1;
              for (int z = 0; z < MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getNumMixoutSongs(); ++z)
                if (MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].mixout_songs[z] == MixGeneratorUI.instance.mixes[
                    MixGeneratorUI.instance.current_mix][i + 1].uniquesongid)
                  sindex = z;
              if (sindex >= 0)
                count++;
            }
            count++;
          }
        }
      }
    }
  }

  public void PrintMix(int inputmix) {
    JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
    if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
    fc.addChoosableFileFilter(new SaveFileFilter());
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setMultiSelectionEnabled(false);
    int returnVal = fc.showSaveDialog(getDialog());
    File tmp = fc.getSelectedFile();
    if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String filestr = (String) tmp.getAbsolutePath();
      if (!filestr.toLowerCase().endsWith(".txt")) filestr += ".txt";
      try {
        FileWriter outputstream = new FileWriter(filestr);
        BufferedWriter outputbuffer = new BufferedWriter(outputstream);

        if (inputmix >= MixGeneratorUI.instance.num_mixes) inputmix = MixGeneratorUI.instance.num_mixes - 1;
        if (inputmix < 0) inputmix = 0;
        if (MixGeneratorUI.instance.num_mixes > 0) {
          MixGeneratorUI.instance.current_mix = inputmix;
          for (int i = 0; i < MixGeneratorUI.instance.numsongs; ++i) {
            int count = i + 1;
            String tempstr;
            if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 10))
              tempstr = new String("00");
            else if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 100))
              tempstr = new String("0");
            else if ( (MixGeneratorUI.instance.numsongs >= 10) && (count < 10))
              tempstr = new String("0");
            else
              tempstr = new String("");
            tempstr += String.valueOf(count) + new String(":   ");
            tempstr += MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getSongId();
            outputbuffer.write(tempstr); outputbuffer.newLine();
            if (i < MixGeneratorUI.instance.numsongs - 1) {
              for (int z = 0; z < MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getNumMixoutSongs(); ++z) {
                if (MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].mixout_songs[z] == MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i + 1].uniquesongid) {
                  String tempstr2;
                  if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 10))
                    tempstr2 = new String("00");
                  else if ( (MixGeneratorUI.instance.numsongs >= 100) && (count < 100))
                    tempstr2 = new String("0");
                  else if ( (MixGeneratorUI.instance.numsongs >= 10) && (count < 10))
                    tempstr2 = new String("0");
                  else
                    tempstr2 = new String("");
                  tempstr2 += String.valueOf(count) + new String(">      [");
                  tempstr2 += String.valueOf(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getMixoutRank(z)) + "]   (";
                  tempstr2 += new MyStringFloat(String.valueOf(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getMixoutBpmdiff(z))).toString();
                  tempstr2 += ")   ";
                  tempstr2 += MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.current_mix][i].getMixoutComments(z);
                  outputbuffer.write(tempstr2); outputbuffer.newLine();
                }
              }
            }
          }
        }
        outputbuffer.close();
        outputstream.close();
      }
      catch (Exception e) {}
    }
  }

}
