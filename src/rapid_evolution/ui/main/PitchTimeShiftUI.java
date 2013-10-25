package rapid_evolution.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.audio.Normalizer;
import rapid_evolution.audio.PitchShift;
import rapid_evolution.filefilters.WaveFileFilter;
import rapid_evolution.threads.NormalizationWorker;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class PitchTimeShiftUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(PitchTimeShiftUI.class);
    
    public PitchTimeShiftUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
        timepitchshiftprogress_ui = new TimePitchShiftProgressUI("time_pitch_shift_progress_dialog");
    }

    public static PitchTimeShiftUI instance = null;
    TimePitchShiftProgressUI timepitchshiftprogress_ui;

    public JButton shiftokbutton = new REButton();
    public JButton shiftcancelbutton = new REButton();

    public JLabel keylabel = new RELabel();
    public JLabel bpmlabel = new RELabel();
    public JLabel shiftlabel = new RELabel();
    public JLabel scalelabel = new RELabel();
    public JLabel resultlabel = new RELabel();
    public JLabel result2label = new RELabel();
    public JLabel endlabel = new RELabel();
    public JCheckBox normalize = new RECheckBox();

    public JTextField startkey = new RETextField();
    public JTextField startbpm = new RETextField();
    public JTextField endkey = new RETextField();
    public JTextField endbpm = new RETextField();

    public JTextField shiftkey = new RETextField();
    public JTextField shiftbpm = new RETextField();

    SongLinkedList processsong = null;

    private void setupDialog() {
      // set field dialog
      shiftkey.getDocument().addDocumentListener(new shiftkeychangelistener());
      endkey.getDocument().addDocumentListener(new shiftendkeychangelistener());
      shiftbpm.getDocument().addDocumentListener(new shiftbpmchangelistener());
      endbpm.getDocument().addDocumentListener(new shiftendbpmchangelistener());
      normalize.setEnabled(false);
    }


    class shiftkeychangelistener implements javax.swing.event.DocumentListener {
     public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
     public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
     public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
     private void updateVal() {
       if (!isVisible() || shiftingkey) return;
         float semitoneshift = 0.0f;
         try {
             semitoneshift = Float.parseFloat(shiftkey.getText());
         } catch (Exception e) { }
         String result = "";
         if (processsong.getStartKey().isValid()) {
           Key key = processsong.getStartKey().getShiftedKeyBySemitones(semitoneshift);
           result += key.getShortKeyNotation();
         }
         if (processsong.getEndKey().isValid()) {
           Key key = processsong.getEndKey().getShiftedKeyBySemitones(semitoneshift);
           if (!result.equals("")) result += "->";
           result += key.getShortKeyNotation();
         }
         shiftingendkey = true;
         endkey.setText(result);
         shiftingendkey = false;
     }
   }

   boolean shiftingkey = false;
   boolean shiftingendkey = false;
   class shiftendkeychangelistener implements javax.swing.event.DocumentListener {
    public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    private void updateVal() {
      if (!isVisible() || shiftingendkey) return;
      try {
        Key startkey = null;
        if (processsong.getStartKey().isValid()) startkey = processsong.getStartKey();
        else if (!processsong.getEndKey().isValid()) startkey = processsong.getEndKey();
        else return;
        Key endingkey = Key.getKey(endkey.getText());
        if (startkey.getScaleType() != endingkey.getScaleType()) return;
        double semitones = -(startkey.getRootValue() - endingkey.getRootValue());
        double semitones2 = -(endingkey.getRootValue() - startkey.getRootValue());
        if (Math.abs(semitones2) < Math.abs(semitones)) semitones = semitones2;
        shiftingkey = true;
        if (semitones == 0.0f) shiftkey.setText("");
        else if (semitones > 0.0f) shiftkey.setText("+" + semitones);
        else shiftkey.setText(String.valueOf(semitones));
        shiftingkey = false;
      } catch (Exception e) { }
    }
   }

   float parseBpmShift(String text) throws Exception {
     boolean percentage = false;
     if (text.endsWith("%")) {
       text = text.substring(0, text.length() - 1);
       percentage = true;
     }
     float val = Float.parseFloat(text);
//     if (percentage) val /= 100.0f;
     val /= 100.0f;
     return val;
   }

   boolean shiftingbpm = false;
   boolean shiftingendbpm = false;
   class shiftbpmchangelistener implements javax.swing.event.DocumentListener {
    public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
    private void updateVal() {
      if (!isVisible() || shiftingbpm) return;
        float timestretch = 0.0f;
        try {
            timestretch = parseBpmShift(shiftbpm.getText());
        } catch (Exception e) { }
        String result = "";
        if (processsong.getStartbpm() != 0.0f) {
          result += String.valueOf(processsong.getStartbpm() / timestretch);
        }
        if (processsong.getEndbpm() != 0.0f) {
          if (!result.equals("")) result += "->";
          result += String.valueOf(processsong.getEndbpm() / timestretch);
        }
        shiftingendbpm = true;
        endbpm.setText(result);
        shiftingendbpm = false;
    }
  }

  float parseEndbpm(String value) throws Exception {
    int i = 0;
    boolean done = false;
    while ((i < value.length()) && !done) {
      char val = value.charAt(i);
      if (val == '-') done = true;
      else ++i;
    }
    return Float.parseFloat(value.substring(0, i));
  }

  class shiftendbpmchangelistener implements javax.swing.event.DocumentListener {
   public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
   public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
   public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVal(); }
   private void updateVal() {
     if (!isVisible() || shiftingendbpm) return;
     try {
       float bpm = parseEndbpm(endbpm.getText());
       float comparebpm = processsong.getStartbpm();
       if (comparebpm == 0.0f) comparebpm = processsong.getEndbpm();
       float percentage = comparebpm / bpm * 100.0f;
       shiftingbpm = true;
       shiftbpm.setText(String.valueOf(percentage) + "%");
       shiftingbpm = false;
     } catch (Exception e) { }
   }
  }


    private void setupToolTips() {

    }

    private void setupActionListeners() {
        shiftokbutton.addActionListener(this);
        shiftcancelbutton.addActionListener(this);
    }

    String GetProposedFilename(String filename) {
      String returnval = StringUtil.RemoveExtension(filename);
      if (!endbpm.getText().equals("") && !endbpm.getText().equals(startbpm.getText())) {
        returnval += "-" + String.valueOf(endbpm.getText()) + "bpm";
      }
      if (!endkey.getText().equals("") && !endkey.getText().equals(startkey.getText())) {
        returnval += "-" + StringUtil.FilterKeyForFilename(endkey.getText());
      }
      return returnval;
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == shiftokbutton) {
        try {
          JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
          fc.setSelectedFile(new File(GetProposedFilename(processsong.getFileName())));
          if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
          fc.addChoosableFileFilter(new WaveFileFilter());
          fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fc.setMultiSelectionEnabled(false);
          int returnVal = fc.showSaveDialog(SkinManager.instance.getFrame("main_frame"));
          File tmp = fc.getSelectedFile();
          if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filestr = (String)tmp.getAbsolutePath();
            PitchShift shifter = new PitchShift();
            float shiftkeyval = 0.0f;
            try {
              shiftkeyval = Float.parseFloat(shiftkey.getText());
            } catch (Exception e) { }
            float shiftbpmval = 1.0f;
            try {
              shiftbpmval = parseBpmShift(shiftbpm.getText());
            } catch (Exception e) { }
            timepitchshiftprogress_ui.shifter = shifter;

            float bpmpitchratio = shiftbpmval;
            if (bpmpitchratio != 1.0f) {
              shiftkeyval += (float)(Math.log(shiftbpmval) / Math.log(2.0) * 12.0);
              // if you want minimal shift to get to the same key:
              float newshift = shiftkeyval;
              while (newshift >= 12.0f) newshift -= 12.0f;
              while (newshift < 0.0f) newshift += 12.0f;
              float highpositive = newshift;
              newshift -= 12.0f;
              if (Math.abs(newshift) < highpositive) shiftkeyval = newshift;
              else shiftkeyval = highpositive;
            }

            new PitchShiftWorker(shifter, filestr, shiftkeyval, shiftbpmval, normalize.isSelected()).start();
            timepitchshiftprogress_ui.Display();
            setVisible(false);
          }
        } catch (Exception e) { log.error("actionPerformed(): error", e); }
      } else if (ae.getSource() == shiftcancelbutton) {
          setVisible(false);
      }
    }

    class PitchShiftWorker extends Thread {
      public PitchShiftWorker(PitchShift in_shifter, String filename, float keyshift, float bpmshift, boolean do_normalization) {
        shifter = in_shifter;
        filestr = filename;
        keyshiftval = keyshift;
        bpmshiftval = bpmshift;
        normalize = do_normalization;
      }
      PitchShift shifter;
      String filestr;
      float keyshiftval;
      float bpmshiftval;
      boolean normalize;
      public void run() {
        if (!normalize) shifter.pitchShiftFile(processsong.getRealFileName(), filestr, keyshiftval, bpmshiftval, normalize);
        else {
          File tempFile = new File("temp.wav");
          Normalizer norm = shifter.pitchShiftFile(processsong.getRealFileName(), "temp.wav", keyshiftval, bpmshiftval, normalize);
          if (norm != null) {
            NormalizationProgressUI.instance.normalizer = norm;
            new NormalizationWorker(norm, "temp.wav", filestr, tempFile).start();
            NormalizationProgressUI.instance.Display();
          }
        }
      }
    }

   public boolean PreDisplay() {
     processsong = (SongLinkedList)display_parameter;
     if (processsong.getStartKey().isValid()) {
       startkey.setText(processsong.getStartKey().toString());
       endkey.setText(processsong.getStartKey().toString());
     } else {
       startkey.setText(processsong.getEndKey().toString());
       endkey.setText(processsong.getEndKey().toString());
     }
     if (processsong.getStartbpm() != 0.0f) {
       startbpm.setText(String.valueOf(processsong.getStartbpm()));
       endbpm.setText(String.valueOf(processsong.getStartbpm()));
     } else if (processsong.getEndbpm() != 0.0f) {
       startbpm.setText(String.valueOf(processsong.getEndbpm()));
       endbpm.setText(String.valueOf(processsong.getEndbpm()));
     } else {
       startbpm.setText("");
       endbpm.setText("");
     }
     if (endbpm.getText().equals("")) endbpm.setFocusable(false);
     else endbpm.setFocusable(true);
     if (endkey.getText().equals("")) endkey.setFocusable(false);
     else endkey.setFocusable(true);
     shiftbpm.setText("");
     shiftkey.setText("");
     return true;
   }

}
