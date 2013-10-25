package rapid_evolution.piano;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.REComboBox;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.slider.MediaSlider;
import com.mixshare.rapid_evolution.ui.swing.slider.PitchBendMediaSlider;

public class MIDIPiano extends REDialog implements ActionListener, ChangeListener {

    private static Logger log = Logger.getLogger(MIDIPiano.class);
    
    public MIDIPiano(String id) {
      super(id);
      instance = this;
      setupDialog();
      setupListeners();
    }

    public static MIDIPiano instance;

    // user interface
    public static int pianowidth = 0;
    public static int pianoheight = 0;
    public PianoPanel pianopnl = new PianoPanel();
    public JButton pianookbutton = new  REButton("ok");
    public PianoComboBox chordlist = new PianoComboBox(chords, true);
    static public String[] training_notes = { "C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B" };
    public PianoComboBox training_note = new PianoComboBox(training_notes, true);
    public PianoComboBox training_modes = new PianoComboBox(new String[] { "listen", "test" }, true);
    public JCheckBox training = new RECheckBox();
    public JButton training_start_stop = new REButton();
    
    public JSlider midivolume = new MediaSlider(JSlider.VERTICAL, 0, 127, 100);
    public JSlider pitchbend = new PitchBendMediaSlider();
    String[] midiprogramStrings = {
      "acoustic grand piano",
      "bright acoustic piano",
      "electric grand piano",
      "honky-tonk piano",
      "electric piano 1",
      "electric piano 2",
      "harpsichord",
      "clavi",
      "celesta",
      "glockenspiel",
      "music box",
      "vibraphone",
      "marimba",
      "xylophone",
      "tubular bells",
      "dulcimer",
      "drawbar organ",
      "percussive organ",
      "rock organ",
      "church organ",
      "reed organ",
      "accordion",
      "harmonica",
      "tango accordion",
      "acoustic guitar (nylon)",
      "acoustic guitar (steel)",
      "electric guitar (jazz)",
      "electric guitar (clean)",
      "electric guitar (muted)",
      "overdriven guitar",
      "distortion guitar",
      "guitar harmonics",
      "acoustic bass",
      "electric bass (finger)",
      "electric bass (pick)",
      "fretless bass",
      "slap bass 1 ",
      "slap bass 2",
      "synth bass 1",
      "synth bass 2",
      "violin ",
      "viola",
      "cello",
      "contrabass",
      "tremolo strings",
      "pizzicato strings",
      "orchestral harp",
      "timpani",
      "string ensemble 1",
      "string ensemble 2",
      "synth strings 1",
      "synth strings 2",
      "choir aahs",
      "voice oohs",
      "synth voice",
      "orchestra hit",
      "trumpet",
      "trombone",
      "tuba",
      "muted trumpet",
      "french horn",
      "brass section",
      "synth brass 1",
      "synth brass 2",
      "soprano sax",
      "alto sax",
      "tenor sax",
      "baritone sax",
      "oboe",
      "english horn",
      "bassoon",
      "clarinet",
      "piccolo",
      "flute",
      "recorder",
      "pan flute",
      "blown bottle",
      "shakuhachi",
      "whistle",
      "ocarina",
      "lead 1 (square)",
      "lead 2 (sawtooth)",
      "lead 3 (calliope)",
      "lead 4 (chiff)",
      "lead 5 (charang)",
      "lead 6 (voice)",
      "lead 7 (fifths)",
      "lead 8 (bass + lead)",
      "pad 1 (new age)",
      "pad 2 (warm)",
      "pad 3 (polysynth)",
      "pad 4 (choir)",
      "pad 5 (bowed)",
      "pad 6 (metallic)",
      "pad 7 (halo)",
      "pad 8 (sweep)",
      "fx 1 (rain)",
      "fx 2 (soundtrack)",
      "fx 3 (crystal)",
      "fx 4 (atmosphere)",
      "fx 5 (brightness)",
      "fx 6 (goblins)",
      "fx 7 (echoes)",
      "fx 8 (sci-fi)",
      "sitar",
      "banjo",
      "shamisen",
      "koto",
      "kalimba",
      "bag pipe",
      "fiddle",
      "shanai",
      "tinkle bell",
      "agogo",
      "steel drums",
      "woodblock",
      "taiko drum",
      "melodic tom",
      "synth drum",
      "reverse cymbal",
      "guitar fret noise",
      "breath noise",
      "seashore",
      "bird tweet",
      "telephone ring",
      "helicopter",
      "applause",
      "gunshot"
  };
    public RELabel keyboardnotelabel = new RELabel("");
    public PianoComboBox midiprogramList = new PianoComboBox(midiprogramStrings, true);
    static public String[] chords = { "single note", "major", "minor", "augmented", "diminished", "diminished 7th", "half diminished", "5th", "7th", "major 7th", "minor 7th", "major/minor 7th", "suspended 2nd", "suspended 4th", "7th suspended 2nd", "7th suspended 4th", "added 2nd", "added 4th", "added 9th", "6th", "minor 6th", "6/9", "9th", "major 9th", "minor 9th", "11th", "major 11th", "minor 11th", "13th", "major 13th", "minor 13th", "7th sharp 5th", "7th flat 5th", "7th sharp 9th", "7th flat 9th" };
    public Vector chordbook = new Vector();
    public NoKBSpinner octspinner;
    public NoKBSpinner spdspinner;
    public JLabel spdspinnerlabel = new RELabel("speed:");
    public NoKBSpinner shiftsspinner;

    private void setupDialog() {
//    pianodlg.addFocusListener(new FocusAdapter() {
//      public void focusLost(FocusEvent e) {
//        if (debugmode) System.out.println("keyboard focus lost");
//        allNotesOff();
//      }
//    });
        pianookbutton.addActionListener(this);
        spdspinnerlabel.setFocusable(false);
        training_start_stop.addActionListener(this);
        training.addActionListener(this);
        midivolume.addKeyListener(new MidiPianoKeyListener());
        pitchbend.addKeyListener(new MidiPianoKeyListener());
        pianopnl.addKeyListener(new MidiPianoKeyListener());
        pianopnl.addMouseMotionListener(new PianoMouseMotionListener());
        pianopnl.addMouseListener(new PianoMouseListener());
                        
        /*
        KeyListener[] existing_listeners = midiprogramList.getKeyListeners();
        for (int i = 0; i < existing_listeners.length; ++i) midiprogramList.removeKeyListener(existing_listeners[i]);
        existing_listeners = midiprogramList.getEditor().getEditorComponent().getKeyListeners();
        for (int i = 0; i < existing_listeners.length; ++i) midiprogramList.getEditor().getEditorComponent().removeKeyListener(existing_listeners[i]);         
        midiprogramList.enableInputMethods(false);              
        midiprogramList.getActionMap().clear();
        */       
        
        midiprogramList.addKeyListener(new MidiPianoKeyListener());;
        pianookbutton.addKeyListener(new MidiPianoKeyListener());
        chordlist.addKeyListener(new MidiPianoKeyListener());

        octspinner = new NoKBSpinner(octavesspinner); //addLabeledSpinner(pianodlg.getContentPane(), "octaves:", octavesspinner);
        spdspinner = new NoKBSpinner(speedspinner);
        octspinner.addFocusListener(new FocusListener() { public void focusGained(FocusEvent e) { } public void focusLost(FocusEvent e) {  allNotesOff();
          pianopnl.repaint();
     } });
        octspinner.addKeyListener(new MidiPianoKeyListener());
//    octavesspinner.;
        octavesspinner.addChangeListener(new OctSpinnerChangeListener());


        shiftsspinner = new NoKBSpinner(shiftspinner); //addLabeledSpinner(pianodlg.getContentPane(), "shift:", shiftspinner);
        shiftsspinner.addKeyListener(new MidiPianoKeyListener());
        shiftspinner.addChangeListener(new ShiftSpinnerChangeListener());

        pitchbend.setMajorTickSpacing(1024);
        pitchbend.setMinorTickSpacing(256);
        pitchbend.setPaintTicks(true);
        pitchbend.setPaintLabels(true);

        midivolume.setMajorTickSpacing(127);
        midivolume.setMinorTickSpacing(50);
        midivolume.setPaintTicks(true);
        midivolume.setPaintLabels(true);

        setupChordBook();

        chordlist.setSelectedIndex(0);
        chordlist.setKeySelectionManager(new InvalidKeySelection());
        chordlist.setFocusTraversalKeysEnabled(false);

//    for (int z = 0; z < midiprogramStrings.length; ++z) midiprogramStrings[z] = midiprogramStrings[z].toLowerCase();
        midiprogramList.setSelectedIndex(0);
        midiprogramList.setKeySelectionManager(new InvalidKeySelection());
        midiprogramList.setFocusTraversalKeysEnabled(false);
        midivolume.setFocusTraversalKeysEnabled(false);
        pitchbend.setFocusTraversalKeysEnabled(false);
       pianookbutton.setFocusTraversalKeysEnabled(false);

       
       JTextComponent editor = (JTextComponent) midiprogramList.getEditor().getEditorComponent();
       editor.setEditable(false);
       editor.setDocument(new IgnoreKeyboardDocument());
       midiprogramList.setFocusable(false);

       editor = (JTextComponent) chordlist.getEditor().getEditorComponent();
       editor.setEditable(false);
       editor.setDocument(new IgnoreKeyboardDocument());
       chordlist.setFocusable(false);

       octspinner.getEditor().setEnabled(false);
       shiftsspinner.getEditor().setEnabled(false);
       
       octspinner.setFocusable(false);
       octspinner.setFocusTraversalKeysEnabled(false);
       shiftsspinner.setFocusable(false);
       shiftsspinner.setFocusTraversalKeysEnabled(false);
    }

    public void postInit() {
      if (!RapidEvolution.instance.loaded) addKeyListener(new MidiPianoKeyListener());
    }

    public void PostLoadInit() {
      try {
//        if ((pianowidth == 0) || (pianoheight == 0)) setSize(800,350);
//        else setSize(pianowidth,pianoheight);

        int javasoundindex = 0;
        for (int i = 0; i < OptionsUI.mididevicescombo.getItemCount(); ++i) {
          if (deviceinfo[i].toString().equals(initialmidivalue))
              initialcombovalue = i;
          if (deviceinfo[i].toString().equals("Java Sound Synthesizer"))
              javasoundindex = i;
        }
        if ( (initialcombovalue < OptionsUI.mididevicescombo.getItemCount()) && (initialcombovalue >= 0))
            OptionsUI.mididevicescombo.setSelectedIndex(initialcombovalue);
        else OptionsUI.mididevicescombo.setSelectedIndex(javasoundindex);

        if (deviceinfo[OptionsUI.mididevicescombo.getSelectedIndex()].toString().equals("Java Sound Synthesizer")) {
          synth = MidiSystem.getSynthesizer();
          synth.open();
          channels = synth.getChannels();
          channel = channels[0];
          synthenabled = true;
        } else {
          synthenabled = false;
          mididevice = MidiSystem.getMidiDevice(deviceinfo[OptionsUI.mididevicescombo.
                                                getSelectedIndex()]);
          if (!mididevice.isOpen()) mididevice.open();
          receiver = mididevice.getReceiver();
        }
        if (synthenabled) programChange(midiprogramList.getSelectedIndex());
        keyspressed = new boolean[num_keys];
        keyspressed2 = new int[num_keys];
        masterkeyspressed = new int[num_keys];
        for (int i = 0; i < num_keys; ++i) {
          keyspressed[i] = false;
          keyspressed2[i] = 0;
          masterkeyspressed[i] = 0;
        }               
      } catch (javax.sound.midi.MidiUnavailableException mue) {
          log.debug("PostLoadInit(): midi unavailable exception: " + mue.getMessage());
      } catch (Exception e) { log.error("PostLoadInit(): error", e);  }
      
//      keyboardnotelabel.setLeftShadow(1, 1, keyboardnotelabel.getForeground().brighter());
//      keyboardnotelabel.setRightShadow(2, 3, keyboardnotelabel.getForeground().darker());
      
    }

    private void setupChordBook() {
        Vector singlenote = new Vector();
        singlenote.add(new Integer(0));
        Vector majortriad = new Vector();
        majortriad.add(new Integer(0));
        majortriad.add(new Integer(4));
        majortriad.add(new Integer(7));
        Vector minortriad = new Vector();
        minortriad.add(new Integer(0));
        minortriad.add(new Integer(3));
        minortriad.add(new Integer(7));
        Vector augmentedtriad = new Vector();
        augmentedtriad.add(new Integer(0));
        augmentedtriad.add(new Integer(4));
        augmentedtriad.add(new Integer(8));
        Vector diminishedtriad = new Vector();
        diminishedtriad.add(new Integer(0));
        diminishedtriad.add(new Integer(3));
        diminishedtriad.add(new Integer(6));

        chordbook.add(singlenote);
        chordbook.add(majortriad);
        chordrow1 = majortriad;
        chordbook.add(minortriad);
        chordrow2 = minortriad;
        chordbook.add(augmentedtriad);
        chordbook.add(diminishedtriad);
        Vector diminished7th = new Vector();
        diminished7th.add(new Integer(0));
        diminished7th.add(new Integer(3));
        diminished7th.add(new Integer(6));
        diminished7th.add(new Integer(9));
        chordbook.add(diminished7th);
        Vector halfdiminished7th = new Vector();
        halfdiminished7th.add(new Integer(0));
        halfdiminished7th.add(new Integer(3));
        halfdiminished7th.add(new Integer(6));
        halfdiminished7th.add(new Integer(10));
        chordbook.add(halfdiminished7th);
        Vector fifth = new Vector();
        fifth.add(new Integer(0));
        fifth.add(new Integer(7));
        chordbook.add(fifth);
        Vector seventh = new Vector();
        seventh.add(new Integer(0));
        seventh.add(new Integer(4));
        seventh.add(new Integer(7));
        seventh.add(new Integer(10));
        chordbook.add(seventh);
        Vector majorseventh = new Vector();
        majorseventh.add(new Integer(0));
        majorseventh.add(new Integer(4));
        majorseventh.add(new Integer(7));
        majorseventh.add(new Integer(11));
        chordbook.add(majorseventh);
        Vector minorseventh = new Vector();
        minorseventh.add(new Integer(0));
        minorseventh.add(new Integer(3));
        minorseventh.add(new Integer(7));
        minorseventh.add(new Integer(10));
        chordbook.add(minorseventh);
        Vector majorminorseventh = new Vector();
        majorminorseventh.add(new Integer(0));
        majorminorseventh.add(new Integer(3));
        majorminorseventh.add(new Integer(7));
        majorminorseventh.add(new Integer(11));
        chordbook.add(majorminorseventh);
        Vector suspended2nd = new Vector();
        suspended2nd.add(new Integer(0));
        suspended2nd.add(new Integer(2));
        suspended2nd.add(new Integer(7));
        chordbook.add(suspended2nd);
        Vector suspended4th = new Vector();
        suspended4th.add(new Integer(0));
        suspended4th.add(new Integer(5));
        suspended4th.add(new Integer(7));
        chordbook.add(suspended4th);
        Vector seventhsuspended2nd = new Vector();
        seventhsuspended2nd.add(new Integer(0));
        seventhsuspended2nd.add(new Integer(2));
        seventhsuspended2nd.add(new Integer(7));
        seventhsuspended2nd.add(new Integer(10));
        chordbook.add(seventhsuspended2nd);
        Vector seventhsuspended4th = new Vector();
        seventhsuspended4th.add(new Integer(0));
        seventhsuspended4th.add(new Integer(5));
        seventhsuspended4th.add(new Integer(7));
        seventhsuspended4th.add(new Integer(10));
        chordbook.add(seventhsuspended4th);
        Vector added2nd = new Vector();
        added2nd.add(new Integer(0));
        added2nd.add(new Integer(2));
        added2nd.add(new Integer(4));
        added2nd.add(new Integer(7));
        chordbook.add(added2nd);
        Vector added4th = new Vector();
        added4th.add(new Integer(0));
        added4th.add(new Integer(4));
        added4th.add(new Integer(5));
        added4th.add(new Integer(7));
        chordbook.add(added4th);
        Vector added9th = new Vector();
        added9th.add(new Integer(0));
        added9th.add(new Integer(4));
        added9th.add(new Integer(7));
        added9th.add(new Integer(14));
        chordbook.add(added9th);
        Vector sixth = new Vector();
        sixth.add(new Integer(0));
        sixth.add(new Integer(4));
        sixth.add(new Integer(7));
        sixth.add(new Integer(9));
        chordbook.add(sixth);
        Vector minorsixth = new Vector();
        minorsixth.add(new Integer(0));
        minorsixth.add(new Integer(3));
        minorsixth.add(new Integer(7));
        minorsixth.add(new Integer(9));
        chordbook.add(minorsixth);
        Vector sixnineths = new Vector();
        sixnineths.add(new Integer(0));
        sixnineths.add(new Integer(4));
        sixnineths.add(new Integer(7));
        sixnineths.add(new Integer(9));
        sixnineths.add(new Integer(14));
        chordbook.add(sixnineths);
        Vector nineth = new Vector();
        nineth.add(new Integer(0));
        nineth.add(new Integer(4));
        nineth.add(new Integer(7));
        nineth.add(new Integer(10));
        nineth.add(new Integer(14));
        chordbook.add(nineth);
        Vector majornineth = new Vector();
        majornineth.add(new Integer(0));
        majornineth.add(new Integer(4));
        majornineth.add(new Integer(7));
        majornineth.add(new Integer(11));
        majornineth.add(new Integer(14));
        chordbook.add(majornineth);
        Vector minornineth = new Vector();
        minornineth.add(new Integer(0));
        minornineth.add(new Integer(3));
        minornineth.add(new Integer(7));
        minornineth.add(new Integer(10));
        minornineth.add(new Integer(14));
        chordbook.add(minornineth);

        Vector eleventh = new Vector();
        eleventh.add(new Integer(0));
        eleventh.add(new Integer(4));
        eleventh.add(new Integer(7));
        eleventh.add(new Integer(10));
        eleventh.add(new Integer(14));
        eleventh.add(new Integer(17));
        chordbook.add(eleventh);
        Vector majoreleventh = new Vector();
        majoreleventh.add(new Integer(0));
        majoreleventh.add(new Integer(4));
        majoreleventh.add(new Integer(7));
        majoreleventh.add(new Integer(11));
        majoreleventh.add(new Integer(14));
        majoreleventh.add(new Integer(17));
        chordbook.add(majoreleventh);
        Vector minoreleventh = new Vector();
        minoreleventh.add(new Integer(0));
        minoreleventh.add(new Integer(3));
        minoreleventh.add(new Integer(7));
        minoreleventh.add(new Integer(10));
        minoreleventh.add(new Integer(14));
        minoreleventh.add(new Integer(17));
        chordbook.add(minoreleventh);

        Vector thirteenth = new Vector();
        thirteenth.add(new Integer(0));
        thirteenth.add(new Integer(4));
        thirteenth.add(new Integer(7));
        thirteenth.add(new Integer(10));
        thirteenth.add(new Integer(14));
        thirteenth.add(new Integer(17));
        thirteenth.add(new Integer(21));

        chordbook.add(thirteenth);
        Vector majorthirteenth = new Vector();
        majorthirteenth.add(new Integer(0));
        majorthirteenth.add(new Integer(4));
        majorthirteenth.add(new Integer(7));
        majorthirteenth.add(new Integer(11));
        majorthirteenth.add(new Integer(14));
        majorthirteenth.add(new Integer(17));
        majorthirteenth.add(new Integer(21));

        chordbook.add(majorthirteenth);
        Vector minorthirteenth = new Vector();
        minorthirteenth.add(new Integer(0));
        minorthirteenth.add(new Integer(3));
        minorthirteenth.add(new Integer(7));
        minorthirteenth.add(new Integer(10));
        minorthirteenth.add(new Integer(14));
        minorthirteenth.add(new Integer(17));
        minorthirteenth.add(new Integer(21));

        chordbook.add(minorthirteenth);

        Vector seventhsharp5th = new Vector();
        seventhsharp5th.add(new Integer(0));
        seventhsharp5th.add(new Integer(4));
        seventhsharp5th.add(new Integer(8));
        seventhsharp5th.add(new Integer(10));
        chordbook.add(seventhsharp5th);
        Vector seventhflat5th = new Vector();
        seventhflat5th.add(new Integer(0));
        seventhflat5th.add(new Integer(4));
        seventhflat5th.add(new Integer(6));
        seventhflat5th.add(new Integer(10));
        chordbook.add(seventhflat5th);

        Vector seventhsharp9th = new Vector();
        seventhsharp9th.add(new Integer(0));
        seventhsharp9th.add(new Integer(4));
        seventhsharp9th.add(new Integer(7));
        seventhsharp9th.add(new Integer(10));
        seventhsharp9th.add(new Integer(15));
        chordbook.add(seventhsharp9th);
        Vector seventhflat9th = new Vector();
        seventhflat9th.add(new Integer(0));
        seventhflat9th.add(new Integer(4));
        seventhflat9th.add(new Integer(7));
        seventhflat9th.add(new Integer(10));
        seventhflat9th.add(new Integer(13));
        chordbook.add(seventhflat9th);
    }

    //
    public boolean[] topblack = { false, true, false, false, true, false, true, false, false, true, false, true };
    public int num_keys = 48;
    public boolean[] keyspressed = new boolean[60];
    public int[] keyspressed2 = new int[60];
    public int[] masterkeyspressed = new int[60];
    public int pianostartkey = 3; // a

    public int octaves = 3;

   public boolean synthenabled = false;

   public Vector keyspresseddown = new Vector();
   public boolean isCurrentlyPressed(int note) {
     try {
       for (int i = 0; i < keyspresseddown.size(); ++i) {
         int val = ((Integer)keyspresseddown.get(i)).intValue();
         if (val == note) return true;
       }
     } catch (Exception e) { }
     return false;
   }

   public void RemoveKeyPressed(int note) {
     try {
       for (int i = 0; i < keyspresseddown.size(); ++i) {
         int val = ((Integer)keyspresseddown.get(i)).intValue();
         if (val == note) {
           keyspresseddown.removeElementAt(i);
           return;
         }
       }
     } catch (Exception e) { }
   }

   class InvalidKeySelection implements JComboBox.KeySelectionManager {
     public int selectionForKey(char aKey, ComboBoxModel model) { return -1; }
   }

   public void DisplayKeyboard(int note) {
       DisplayKeyboard(note, chords[chordlist.getSelectedIndex()]);
   }
   public void DisplayKeyboard(int note, String chordtype) {
	   if (log.isDebugEnabled())
		   log.debug("DisplayKeyboard(): note=" + note + ", chordtype=" + chordtype);
     String newlabel = new String("");
     while (note >= 12) note -= 12;
     if (note == 0) newlabel += "A";
     else if (note == 1) newlabel += "A#/Bb";
     else if (note == 2) newlabel += "B";
     else if (note == 3) newlabel += "C";
     else if (note == 4) newlabel += "C#/Db";
     else if (note == 5) newlabel += "D";
     else if (note == 6) newlabel += "D#/Eb";
     else if (note == 7) newlabel += "E";
     else if (note == 8) newlabel += "F";
     else if (note == 9) newlabel += "F#/Gb";
     else if (note == 10) newlabel += "G";
     else if (note == 11) newlabel += "G#/Ab";
     else return;
     if (!chordtype.equals("single note")) {
       newlabel += " " + chordtype;
     }
     cachedkeydisplayval = newlabel;
     if (pitchbend.getValue() != 8192) {
       int val = pitchbend.getValue();
       if (val > 8192) {
         int diff = (val - 8192) * 100 / 4096;
         if (diff != 0) newlabel += " +" + String.valueOf(diff) + SkinManager.instance.getMessageText("pitch_bend_cents_prefix");
       } else {
         int diff = (8192 - val) * 100 / 4096;
         if (diff != 0) newlabel += " -" + String.valueOf(diff) + SkinManager.instance.getMessageText("pitch_bend_cents_prefix");
       }
     }
     if (!isTraining()) keyboardnotelabel.setText(newlabel);
   }

   public String cachedkeydisplayval = null;
   public Vector chordrow1 = null;
   public Vector chordrow2 = null;

   public void chordnotetrigger(int note, int index) { // 0 = a
          try {
            if (synthenabled && !synth.isOpen()) {
              synth.open();
              channels = synth.getChannels();
              channel = channels[0];
              channel.programChange(midiprogramList.getSelectedIndex());
              channel.setMono(true);
            }

           DisplayKeyboard(note, chords[OptionsUI.instance.row1chordtype.getSelectedIndex()]);
           Vector chord = chordrow1;
           for (int i = 0; i < chord.size(); ++i) {
             int ind = index + ((Integer)chord.get(i)).intValue();
             if ((ind < num_keys) && (ind >= 0)) {
              if (masterkeyspressed[ind] == 0) noteOn(ind + octaves * 12);
              keyspressed2[ind]++;
              masterkeyspressed[ind]++;
             }
           }

         pianopnl.repaint();
       } catch (Exception e) { }
      }

      public void chordnotetrigger2(int note, int index) { // 0 = a

             try {
               if (synthenabled && !synth.isOpen()) {
                 synth.open();
                 channels = synth.getChannels();
                 channel = channels[0];
                 channel.programChange(midiprogramList.getSelectedIndex());
                 channel.setMono(true);
               }

           DisplayKeyboard(note, chords[OptionsUI.instance.row2chordtype.getSelectedIndex()]);

              Vector chord = chordrow2;
              for (int i = 0; i < chord.size(); ++i) {
                int ind = index + ((Integer)chord.get(i)).intValue();
                if ((ind < num_keys) && (ind >= 0)) {
                 if (masterkeyspressed[ind] == 0) noteOn(ind + octaves * 12);
                 keyspressed2[ind]++;
                 masterkeyspressed[ind]++;
                }
              }

            pianopnl.repaint();
          } catch (Exception e) { }
         }

      public void keynotetrigger(int note, int index) {
          keynotetrigger(note, index, true);
      }
      
      public void paint_keyboard(int note, int index) { // 0 = a
          try {
           Vector chord = (Vector)chordbook.get(chordlist.getSelectedIndex());
           for (int i = 0; i < chord.size(); ++i) {
             int ind = index + ((Integer)chord.get(i)).intValue();
             if ((ind < num_keys) && (ind >= 0)) {
              keyspressed2[ind]++;
              masterkeyspressed[ind]++;
             }
           }
         pianopnl.repaint();
       } catch (Exception e) { }
      }      
      
   public void keynotetrigger(int note, int index, boolean display) { // 0 = a
      try {
        if (synthenabled && !synth.isOpen()) {
          synth.open();
          channels = synth.getChannels();
          channel = channels[0];
          channel.programChange(midiprogramList.getSelectedIndex());
          channel.setMono(true);
        }

        if (display) DisplayKeyboard(note, chords[chordlist.getSelectedIndex()]);
       Vector chord = (Vector)chordbook.get(chordlist.getSelectedIndex());
       for (int i = 0; i < chord.size(); ++i) {
         int ind = index + ((Integer)chord.get(i)).intValue();
         if ((ind < num_keys) && (ind >= 0)) {
          if (masterkeyspressed[ind] == 0) noteOn(ind + octaves * 12);
          if (display) {
              keyspressed2[ind]++;
              masterkeyspressed[ind]++;
          }
         }
       }

     pianopnl.repaint();
   } catch (Exception e) { }
  }

    public Synthesizer synth = null;
    public MidiChannel[] channels;
    public MidiChannel channel;
    public MidiDevice.Info[] deviceinfo = null;

    public void midinotetrigger(int note, int index) { // 0 = a

      try {
        if (synthenabled && !synth.isOpen()) {
          synth.open();
          channels = synth.getChannels();
          channel = channels[0];
          channel.programChange(midiprogramList.getSelectedIndex());
          channel.setMono(true);
        }
          DisplayKeyboard(note, chords[chordlist.getSelectedIndex()]);
          Vector chord = (Vector)chordbook.get(chordlist.getSelectedIndex());
          for (int i = 0; i < chord.size(); ++i) {
            int ind = index + ((Integer)chord.get(i)).intValue();
            if ((ind < num_keys) && (ind >= 0))
            if (masterkeyspressed[ind] == 0) noteOn(ind + octaves * 12);
          }
//      }
          boolean[] newkeyspressed = new boolean[num_keys];
          chord = (Vector)chordbook.get(chordlist.getSelectedIndex());
          for (int j = 0; j < chord.size(); ++j) {
            int ind = index + ((Integer)chord.get(j)).intValue();
            if (((ind < num_keys) && (ind >= 0))) {
              newkeyspressed[ind] = true;
              if (!keyspressed[ind]) masterkeyspressed[ind]++;
            }
          }
// for mouse drags:
          boolean match = true;
          for (int i = 0; i < num_keys; ++i) {
            if (newkeyspressed[i] != keyspressed[i]) {
              match = false;
              if (!newkeyspressed[i]) {
                masterkeyspressed[i]--;
                if (masterkeyspressed[i] == 0) noteOff(i + octaves * 12);
              }
            }
          }
          keyspressed = newkeyspressed;
          pianopnl.repaint();
      } catch (Exception e) { }
    }

    public void CheckIfNoNotes() {
      for (int i = 0; i < masterkeyspressed.length; ++i) if (masterkeyspressed[i] > 0) return;
      if (!isTraining()) keyboardnotelabel.setText("");
      cachedkeydisplayval = null;
    }

    public SpinnerModel octavesspinner = new SpinnerNumberModel(num_keys / 12, //initial value
                                   1, //min
                                   11, //max
                                   1);

    public SpinnerModel speedspinner = new SpinnerNumberModel(10,
            1, //min
            20, //max
            1);

    public SpinnerModel shiftspinner = new SpinnerNumberModel(3, 0, 10, 1);

    public int currentshiftkey = 0;

    public int keyboardshift = 12;

    class OctSpinnerChangeListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
        int octaves = ((SpinnerNumberModel)octavesspinner).getNumber().intValue();
        num_keys = ((SpinnerNumberModel)octavesspinner).getNumber().intValue() * 12;
        setkeyboardshift(octaves);
        allNotesOff();
        keyspressed = new boolean[num_keys];
        keyspressed2 = new int[num_keys];
        masterkeyspressed = new int[num_keys];
        for (int i = 0; i < num_keys; ++i) {
          keyspressed[i] = false;
          keyspressed2[i] = 0;
          masterkeyspressed[i] = 0;
        }
        pianopnl.repaint();
      }
    }

    class ShiftSpinnerChangeListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
        octaves = ((SpinnerNumberModel)shiftspinner).getNumber().intValue();
        allNotesOff();
        keyspressed = new boolean[num_keys];
        keyspressed2 = new int[num_keys];
        masterkeyspressed = new int[num_keys];
        for (int i = 0; i < num_keys; ++i) {
          keyspressed[i] = false;
          keyspressed2[i] = 0;
          masterkeyspressed[i] = 0;
        }
        pianopnl.repaint();
      }
    }

    public int initialcombovalue = 0;
    public String initialmidivalue = null;

    public MidiDevice mididevice = null;
    public Receiver receiver = null;

    public void InitMIDI() {
      try {
    	  // TEMP FIX, midi crashes on mac osx 10.6
    	  String lcOSName = System.getProperty("os.name").toLowerCase();
    	  boolean MAC_OS_X = lcOSName.startsWith("mac os x");
    	  if (MAC_OS_X && (System.getProperty("os.version").startsWith("10.6")))
    		  deviceinfo = new Info[0];
    	  else
    		  deviceinfo = MidiSystem.getMidiDeviceInfo();
        Vector devices = new Vector();

        for (int i = 0; i < deviceinfo.length; ++i) {
    //        values[i] = new String(deviceinfo[i].toString());
          boolean alreadyfound = false;
//        for (int j = 0; j < devices.size(); ++j) {
//          String old = (String)devices.get(j);
//          if (old.toLowerCase().equals(deviceinfo[i].toString().toLowerCase())) alreadyfound = true;
//        }
          if (!alreadyfound) devices.add(deviceinfo[i].toString());
        }

        String[] values = new String[devices.size()];
        for (int i = 0; i < devices.size(); ++i) {
          values[i] = (String)devices.get(i);
        }

        OptionsUI.mididevicescombo = new REComboBox(values);
      }
      catch (Exception e) { log.error("InitMIDI(): error", e); }
    }

    public void setkeyboardshift(int octaves) {
      if ((octaves == 2) || (octaves == 1)) keyboardshift = 0;
      if ((octaves == 3) || (octaves == 4)) keyboardshift = 12;
      if ((octaves == 5) || (octaves == 6)) keyboardshift = 24;
      if ((octaves == 7) || (octaves == 8)) keyboardshift = 36;
      if ((octaves == 9) || (octaves == 10)) keyboardshift = 48;
      if (octaves > 10) keyboardshift = 56;
    }

    int bend = 0;

    public void noteOn(int note) {
      try {
        if (synthenabled) channel.noteOn(note, midivolume.getValue());
        else if (receiver != null) {
          ShortMessage myMsg = new ShortMessage();
          myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, midivolume.getValue());
          receiver.send(myMsg, -1);
        }
      } catch (Exception e) { log.error("noteOn(): error", e); }
    }

    public void noteOff(int note) {
      try {
        if (synthenabled) channel.noteOff(note);
        else if (receiver != null) {
          ShortMessage myMsg = new ShortMessage();
          myMsg.setMessage(ShortMessage.NOTE_OFF, 0, note, 0);
          receiver.send(myMsg, -1);
        }
      } catch (Exception e) { log.error("noteOff(): error", e); }
    }

    public void allNotesOff() {
      if (!RapidEvolution.instance.loaded) return;
      try {
        if (synthenabled) channel.allNotesOff();
        else if (receiver != null) {
          for (int note = 0; note < 128; note++) {
            ShortMessage myMsg = new ShortMessage();
            myMsg.setMessage(ShortMessage.NOTE_OFF, 0, note, 0);
            receiver.send(myMsg, -1);
          }
        }
        for (int i = 0; i < masterkeyspressed.length; ++i) {
          masterkeyspressed[i] = 0;
          keyspressed[i] = false;
          keyspressed2[i] = 0;
        }
      } catch (Exception e) { log.error("allNotesOff(): error", e); }
    }

    public void programChange(int prog) {
      try {
        if (synthenabled) channel.programChange(prog);
        else {
          ShortMessage myMsg = new ShortMessage();
          myMsg.setMessage(ShortMessage.PROGRAM_CHANGE, 0, prog, 0);

          receiver.send(myMsg, -1);
        }
      } catch (Exception e) { log.error("programChange(): error", e); }
    }

    public KeyEvent translatekeycode(KeyEvent e) {

  /*      if ((e.getKeyCode() == 16) && (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT)) e.setKeyCode(255);
        else if ((e.getKeyCode() == 16) && (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT)) e.setKeyCode(254);
        else if (e.getKeyCode() == e.VK_LESS) e.setKeyCode(e.VK_COMMA);
        else if (e.getKeyCode() == e.VK_GREATER) e.setKeyCode(e.VK_PERIOD);
        else if (e.getKeyCode() == '?') e.setKeyCode('/');
        else if (e.getKeyCode() == ':') e.setKeyCode(';');
        else if (e.getKeyCode() == '"') e.setKeyCode('\'');
        else if (e.getKeyCode() == '{') e.setKeyCode('[');
        else if (e.getKeyCode() == '}') e.setKeyCode(']');
        else if (e.getKeyCode() == '|') e.setKeyCode('\\');
        else if (e.getKeyCode() == '!') e.setKeyCode('1');
        else if (e.getKeyCode() == '@') e.setKeyCode('2');
        else if (e.getKeyCode() == '$') e.setKeyCode('4');
        else if (e.getKeyCode() == '%') e.setKeyCode('5');
        else if (e.getKeyCode() == '^') e.setKeyCode('6');
        else if (e.getKeyCode() == '*') e.setKeyCode('8');
        else if (e.getKeyCode() == '(') e.setKeyCode('9');
        else if (e.getKeyCode() == '_') e.setKeyCode('-');
        else if (e.getKeyCode() == '+') e.setKeyCode('=');
  */
      if ((e.getKeyCode() == 16) && (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT)) e.setKeyCode(255);
      else if ((e.getKeyCode() == 16) && (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT)) e.setKeyCode(254);
      else if (e.getKeyCode() == e.VK_LESS) e.setKeyCode(e.VK_COMMA);
      else if (e.getKeyCode() == e.VK_GREATER) e.setKeyCode(e.VK_PERIOD);
      else if (e.getKeyCode() == '?') e.setKeyCode(e.VK_SLASH);
      else if (e.getKeyCode() == e.VK_COLON) e.setKeyCode(e.VK_SEMICOLON);
      else if (e.getKeyCode() == e.VK_QUOTEDBL) e.setKeyCode('\'');
      else if (e.getKeyCode() == e.VK_BRACELEFT) e.setKeyCode('[');
      else if (e.getKeyCode() == e.VK_BRACERIGHT) e.setKeyCode(']');
      else if (e.getKeyCode() == '|') e.setKeyCode(e.VK_BACK_SLASH);
      else if (e.getKeyCode() == e.VK_EXCLAMATION_MARK) e.setKeyCode(e.VK_1);
      else if (e.getKeyCode() == e.VK_AT) e.setKeyCode(e.VK_2);
      else if (e.getKeyCode() == e.VK_DOLLAR) e.setKeyCode(e.VK_4);
//    else if (e.getKeyCode() == e.VK_PERCENTAGE'\%') e.setKeyCode('5');
      else if (e.getKeyCode() == e.VK_CIRCUMFLEX) e.setKeyCode(e.VK_6);
      else if (e.getKeyCode() == e.VK_AMPERSAND) e.setKeyCode(e.VK_8);
      else if (e.getKeyCode() == e.VK_LEFT_PARENTHESIS) e.setKeyCode(e.VK_9);
      else if (e.getKeyCode() == e.VK_UNDERSCORE) e.setKeyCode(e.VK_MINUS);
      else if (e.getKeyCode() == e.VK_PLUS) e.setKeyCode(e.VK_EQUALS);
      return e;
    }

    public void ChangeMidiDevice() {
        try {
          if ((mididevice != null) && (mididevice.isOpen())) mididevice.close();
          if (deviceinfo[OptionsUI.instance.mididevicescombo.getSelectedIndex()].toString().equals("Java Sound Synthesizer")) {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            channel = channels[0];
            synthenabled = true;
          } else {
            try {
              synthenabled = false;
              mididevice = MidiSystem.getMidiDevice(deviceinfo[OptionsUI.instance.mididevicescombo.
                                                  getSelectedIndex()]);
              if (!mididevice.isOpen())
                mididevice.open();
              receiver = mididevice.getReceiver();
            } catch (Exception e) {
              for (int i = 0; i < OptionsUI.instance.mididevicescombo.getItemCount(); ++i) {
                if (OptionsUI.instance.mididevicescombo.getItemAt(i).toString().equals("Java Sound Synthesizer")) {
                  OptionsUI.instance.mididevicescombo.setSelectedIndex(i);
                  synth = MidiSystem.getSynthesizer();
                  synth.open();
                  channels = synth.getChannels();
                  channel = channels[0];
                  synthenabled = true;
                }
              }
            }
          }
          programChange(midiprogramList.getSelectedIndex());
          pitchbend.setValue(8192);
        } catch (javax.sound.midi.MidiUnavailableException mue) {
            log.debug("ChangeMidiDevice(): midi unavailable exception: " + mue.getMessage());
        } catch (Exception e) {
            log.error("ChangeMidiDevice(): error", e);
        }
    }

    private void setupListeners() {
        midivolume.addChangeListener(this);
       pitchbend.addChangeListener(this);
       midiprogramList.addActionListener(this);
       chordlist.addActionListener(this);
    }

    public void stateChanged(ChangeEvent event) {
      if (event.getSource() == pitchbend) {
              if (synthenabled) {
                channel.setPitchBend(pitchbend.getValue());
              } else {
                try {
                  ShortMessage myMsg = new ShortMessage();
                  myMsg.setMessage(ShortMessage.PITCH_BEND, 0, (short)(pitchbend.getValue() & 127), (short)((pitchbend.getValue() >> 7) & 127));
//          myMsg.setMessage(ShortMessage.PITCH_BEND, 0, 127, 0);
                  receiver.send(myMsg, -1);
                } catch (Exception e) { log.error("stateChanged(): error", e); }
              }
              if (cachedkeydisplayval != null) {
              String newlabel = cachedkeydisplayval;
              if (pitchbend.getValue() != 8192) {
                int val = pitchbend.getValue();
                if (val > 8192) {
                  int diff = (val - 8192) * 100 / 4096;
                  if (diff != 0) newlabel += " +" + String.valueOf(diff) + SkinManager.instance.getMessageText("pitch_bend_cents_prefix");
                } else {
                  int diff = (8192 - val) * 100 / 4096;
                  if (diff != 0) newlabel += " -" + String.valueOf(diff) + SkinManager.instance.getMessageText("pitch_bend_cents_prefix");
                }
              }
              if (!isTraining()) keyboardnotelabel.setText(newlabel);
              }
      }
    }

    public void enableTraining() {
        training_note.setVisible(true);
        training_modes.setVisible(true);
        training_start_stop.setVisible(true);
        spdspinner.setVisible(true);
        spdspinnerlabel.setVisible(true);
    }
    
    public void disableTraining() {
        training_note.setVisible(false);
        training_modes.setVisible(false);
        training_start_stop.setVisible(false);        
        spdspinner.setVisible(false);
        spdspinnerlabel.setVisible(false);
    }

    public void setupTraining() {
        if (training.isSelected()) {
            enableTraining();
        } else {
            stopTraining();
            disableTraining();            
        }        
    }
    
    private TrainingThread training_thread = null;
    
    public void triggerTrainingKeyhit() {
        if (training_thread != null) {
            training_thread.keyHit();
        }
    }
    
    public boolean isTraining() {
        return training_thread != null;
    }
    
    public void stopTraining() {
        // stopping
        training_start_stop.setText(SkinManager.instance.getTextFor(training_start_stop));
        if (training_thread != null) {
            training_thread.stopTraining();
            training_thread = null;
        }                      
    }
    
    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == midiprogramList) {
        programChange(midiprogramList.getSelectedIndex());
      } else if (ae.getSource() == training) {
          setupTraining();
      } else if (ae.getSource() == training_start_stop) {
          if (training_thread == null) {
              // starting
              training_start_stop.setText(SkinManager.instance.getAltTextFor(training_start_stop));              
              training_thread = new TrainingThread((training_modes.getSelectedIndex() == 0));
              training_thread.start();
          } else {
              stopTraining();
          }
      } else if (ae.getSource() == pianookbutton) {
          stopTraining();
        if ((synth != null) && (synth.isOpen())) synth.close();
        allNotesOff();
        keyspresseddown = new Vector();
        setVisible(false);
      }
    }

    public boolean PreDisplay() {
        stopTraining();
      keyboardnotelabel.setText("");
      cachedkeydisplayval = null;
      return true;
    }
}
