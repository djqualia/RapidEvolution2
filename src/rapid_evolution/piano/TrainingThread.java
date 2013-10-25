package rapid_evolution.piano;

import org.apache.log4j.Logger;

import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

import java.awt.Color;

public class TrainingThread extends Thread {

    static private Logger log = Logger.getLogger(TrainingThread.class);
        
    private boolean stop = false;
    private boolean paused = false;
    private boolean listening = true;
    private int note_triggered = -1;
    
    int num_total = 0;
    int num_correct = 0;
    int num_incorrect = 0;
    int num_failed = 0;
    
    int last_note = -1;
    int last_midi_note_value = -1;
    int last_octave = -1;

    public TrainingThread(boolean listening) {
        this.listening = listening;
    }
           
    public void run() {
        try {
            if (log.isDebugEnabled()) log.debug("run(): started, listening=" + listening);
            int num_octaves = ((Integer)MIDIPiano.instance.octavesspinner.getValue()).intValue();
            while (!stop) {
                int note_duration = (25 - ((Integer)MIDIPiano.instance.spdspinner.getValue()).intValue()) * 75;
                if (paused) {
                    Thread.sleep(note_duration);
                    paused = false;
                }
                
                // determine a random (atonal) note:
                int note = last_note;
                while (note == last_note) note = (int)Math.round((Math.random() * 12.0) - 0.5);
                if (note < 0) note = 0;
                if (note > 11) note = 11;
                int octave = (int)Math.round(Math.random() * (num_octaves - 1));
                boolean is_training_note = (note == MIDIPiano.instance.training_note.getSelectedIndex());
                int midi_note_value = MIDIPiano.instance.pianostartkey + MIDIPiano.instance.octaves * 12 + octave * 12 + note;
                if (log.isDebugEnabled()) log.debug("run(): note=" + note + ", octave=" + octave + ", is_training_note=" + is_training_note + ", midi_note_value=" + midi_note_value);                

                // trigger note:
                MIDIPiano.instance.keynotetrigger(midi_note_value, note + octave * 12, listening && is_training_note);                
                if (listening && is_training_note)
                    MIDIPiano.instance.keyboardnotelabel.setText((String)MIDIPiano.instance.training_note.getSelectedItem());
                else if (listening)
                    MIDIPiano.instance.keyboardnotelabel.setText("");
                    
                last_note = note;
                last_octave = octave;
                last_midi_note_value = midi_note_value;
                ++num_total;
                if (!listening && is_training_note) {
                    note_triggered = note;
                }
                Thread.sleep(note_duration);
                MIDIPiano.instance.allNotesOff();
                if (stop) break;
                if (note_triggered != -1) {
                    if (log.isDebugEnabled()) log.debug("run(): failed to identify");
                    ++num_failed;
                    if (foreground == null)
                        foreground = MIDIPiano.instance.keyboardnotelabel.getForeground();
                    MIDIPiano.instance.keyboardnotelabel.setForeground(new Color(255,0,0));
                    PianoPanel.instance.setColor(Color.RED);
                    MIDIPiano.instance.paint_keyboard(last_midi_note_value, last_note + last_octave * 12);
                    MIDIPiano.instance.keyboardnotelabel.setText(SkinManager.instance.getMessageText("ear_training_failed") + " (" + num_failed + ")");
                    paused = true;
                } else {
                    if (paused) {
                        Thread.sleep(note_duration);
                        paused = false;                        
                    }                    
                    MIDIPiano.instance.keyboardnotelabel.setText("");
                }
                note_triggered = -1;
                
            }
            MIDIPiano.instance.pianopnl.repaint();
            if (foreground != null)
                MIDIPiano.instance.keyboardnotelabel.setForeground(foreground);
            MIDIPiano.instance.keyboardnotelabel.setText("");
            PianoPanel.instance.restoreColor();
            
            if (!listening) {
	            StringBuffer score_text = new StringBuffer();
	            score_text.append("# notes: ");
	            score_text.append(String.valueOf(num_total));
	            score_text.append(", # correct: ");
	            score_text.append(String.valueOf(num_correct));
	            score_text.append(", # incorrect: ");
	            score_text.append(String.valueOf(num_incorrect));
	            score_text.append(", # failed: ");
	            score_text.append(String.valueOf(num_failed));
	            IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
	                    score_text.toString(),
	                  "ear training scorecard",
	                  IOptionPane.INFORMATION_MESSAGE);            
	            
            }

            if (log.isDebugEnabled()) log.debug("run(): stopped");
        } catch (Exception e) {
            log.error("run(): error Exception", e);
        }
    }

    public Color foreground = null;
    public void keyHit() {
        if (listening || paused) return;
        if (log.isDebugEnabled()) log.debug("keyHit(): detected, note_triggered=" + note_triggered);
        try {            
            if (note_triggered == -1) {
                ++num_incorrect;
                if (foreground == null)
                    foreground = MIDIPiano.instance.keyboardnotelabel.getForeground();
                MIDIPiano.instance.keyboardnotelabel.setForeground(new Color(255,0,0));
                MIDIPiano.instance.keyboardnotelabel.setText(SkinManager.instance.getMessageText("ear_training_incorrect") +  " (" + num_incorrect + ")");
                PianoPanel.instance.setColor(Color.RED);
                MIDIPiano.instance.paint_keyboard(last_midi_note_value, last_note + last_octave * 12);
                if (log.isDebugEnabled()) log.debug("keyHit(): incorrect");
            } else {
                ++num_correct;
                if (foreground != null)
                    MIDIPiano.instance.keyboardnotelabel.setForeground(foreground);                    
                MIDIPiano.instance.keyboardnotelabel.setText(SkinManager.instance.getMessageText("ear_training_correct") + " (" + num_correct + ")");
                PianoPanel.instance.restoreColor();                    
                MIDIPiano.instance.paint_keyboard(last_midi_note_value, last_note + last_octave * 12);
                if (log.isDebugEnabled()) log.debug("keyHit(): correct!");
                note_triggered = -1;
            }
            paused = true;
        } catch (Exception e) {
            log.error("keyHit(): error Exception", e);
        }
    }
        
    public void stopTraining() {
        stop = true;
    }
    
}
