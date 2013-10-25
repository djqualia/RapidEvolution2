package rapid_evolution.piano;

import java.util.Vector;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class MidiPianoKeyListener extends KeyAdapter {
    public static void key_pressed(KeyEvent e) {
        e = MIDIPiano.instance.translatekeycode(e);
        if (MIDIPiano.instance.isCurrentlyPressed(e.getKeyCode())) return;
        if ((e.getKeyCode() == 255) &&  MIDIPiano.instance.isCurrentlyPressed(254)) return;
        if ((e.getKeyCode() == 254) &&  MIDIPiano.instance.isCurrentlyPressed(255)) return;
        try {
        int kp = e.getKeyCode();
        MIDIPiano.instance.keyspresseddown.add(new Integer(e.getKeyCode()));
        if (e.getKeyCode() == e.VK_TAB) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_1) || (e.getKeyChar() == e.VK_EXCLAMATION_MARK)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 1 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  1 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'q') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 2 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  2 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_2) || (e.getKeyChar() == e.VK_AT)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 3 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  3 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'w') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 4 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  4 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'e') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 5 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  5 + MIDIPiano.instance.keyboardshift);
        }

        else if ((e.getKeyChar() == e.VK_4) || (e.getKeyChar() == e.VK_DOLLAR)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 6 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  6 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'r') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 7 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  7 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_5)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 8 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  8 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 't') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 9 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  9 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_6) || (e.getKeyChar() == e.VK_CIRCUMFLEX)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 10 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  10 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'y') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 11 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  11 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'u') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 12 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  12 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_8) || (e.getKeyChar() == e.VK_AMPERSAND)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 13 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  13 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'i') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 14 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  14 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_9) || (e.getKeyChar() == e.VK_LEFT_PARENTHESIS)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 15 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  15 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'o') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 16 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  16 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'p') {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 17 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  17 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_MINUS) || (e.getKeyChar() == e.VK_UNDERSCORE)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 18 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  18 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == '[') || (e.getKeyChar() == e.VK_BRACELEFT)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 19 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  19 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_EQUALS) || (e.getKeyChar() == e.VK_PLUS)){
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 20 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  20 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == ']') || (e.getKeyChar() == e.VK_BRACERIGHT)) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 21 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  21 + MIDIPiano.instance.keyboardshift);
        }
        else if (e.getKeyCode() == e.VK_BACK_SPACE) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 22 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  22 + MIDIPiano.instance.keyboardshift);
          MIDIPiano.instance.triggerTrainingKeyhit();
        }
        else if ((e.getKeyChar() == e.VK_BACK_SLASH) || (e.getKeyChar() == '|')) {
          MIDIPiano.instance.keynotetrigger(MIDIPiano.instance.pianostartkey + 23 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  23 + MIDIPiano.instance.keyboardshift);
        }

        else if (e.getKeyCode() == e.VK_CAPS_LOCK) {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  MIDIPiano.instance.keyboardshift);
          e.setKeyCode(e.VK_ESCAPE);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'a') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 1 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  1 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 's') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 2 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  2 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'd') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 3 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  3 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'f') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 4 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  4 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'g') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 5 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  5 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'h') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 6 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  6 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'j') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 7 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  7 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'k') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 8 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  8 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'l') {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 9 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  9 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_SEMICOLON) || (e.getKeyChar() == e.VK_COLON)) {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 10 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  10 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == '\'') || (e.getKeyChar() == e.VK_QUOTEDBL)) {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 11 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  11 + MIDIPiano.instance.keyboardshift);
        }
        else if (e.getKeyCode() == e.VK_ENTER) {
          MIDIPiano.instance.chordnotetrigger(MIDIPiano.instance.pianostartkey + 12 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  12 + MIDIPiano.instance.keyboardshift);
        }

        else if ((e.getKeyCode() == 255)) {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'z') {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 1 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  1 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'x') {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 2 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  2 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'c') {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 3 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  3 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'v') {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 4 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  4 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'b') {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 5 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  5 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'n') {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 6 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  6 + MIDIPiano.instance.keyboardshift);
        }
        else if (Character.toLowerCase(e.getKeyChar()) == 'm') {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 7 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  7 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_COMMA) || (e.getKeyChar() == e.VK_LESS)) {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 8 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  8 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_PERIOD) || (e.getKeyChar() == e.VK_GREATER)) {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 9 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  9 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyChar() == e.VK_SLASH) || (e.getKeyChar() == '?')) {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 10 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  10 + MIDIPiano.instance.keyboardshift);
        }
        else if ((e.getKeyCode() == 254)) {
          MIDIPiano.instance.chordnotetrigger2(MIDIPiano.instance.pianostartkey + 11 + MIDIPiano.instance.octaves * 12 + MIDIPiano.instance.keyboardshift,  11 + MIDIPiano.instance.keyboardshift);
        }
        else if (e.getKeyCode() == e.VK_PAGE_UP) {
          MIDIPiano.instance.allNotesOff(); MIDIPiano.instance.keyboardshift += 12; if (MIDIPiano.instance.keyboardshift >= MIDIPiano.instance.num_keys) MIDIPiano.instance.keyboardshift -= 12; }
        else if (e.getKeyCode() == e.VK_PAGE_DOWN) {
          MIDIPiano.instance.allNotesOff();  MIDIPiano.instance.keyboardshift -= 12; if (MIDIPiano.instance.keyboardshift < 0) MIDIPiano.instance.keyboardshift = 0;}
        else if (e.getKeyCode() == e.VK_HOME) {
          int v = MIDIPiano.instance.pitchbend.getValue();
          v+=4096/32;
          if (v > MIDIPiano.instance.pitchbend.getMaximum()) v = MIDIPiano.instance.pitchbend.getMaximum();
          MIDIPiano.instance.pitchbend.setValue(v);
        }
        else if (e.getKeyCode() == e.VK_END) {
          int v = MIDIPiano.instance.pitchbend.getValue();
          v-=4096/32;
          if (v < MIDIPiano.instance.pitchbend.getMinimum()) v = MIDIPiano.instance.pitchbend.getMinimum();
          MIDIPiano.instance.pitchbend.setValue(v);
        }
        else if (e.getKeyCode() == e.VK_LEFT) {
          MIDIPiano.instance.allNotesOff();
          int v = MIDIPiano.instance.midiprogramList.getSelectedIndex();
          v--;
          if (v < 0) v = 0;
          MIDIPiano.instance.midiprogramList.setSelectedIndex(v);
          MIDIPiano.instance.programChange(v);
        }
        else if (e.getKeyCode() == e.VK_RIGHT) {
          MIDIPiano.instance.allNotesOff();
          int v = MIDIPiano.instance.midiprogramList.getSelectedIndex();
          v++;
          if (v > 127) v = 127;
          MIDIPiano.instance.midiprogramList.setSelectedIndex(v);
          MIDIPiano.instance.programChange(v);
        }
        else if (e.getKeyCode() == e.VK_DOWN) {
          int v = MIDIPiano.instance.midivolume.getValue();
          v -= 5;
          if (v < 0) v = 0;
          MIDIPiano.instance.midivolume.setValue(v);
        }
        else if (e.getKeyCode() == e.VK_UP) {
          int v = MIDIPiano.instance.midivolume.getValue();
          v += 5;
          if (v > 127) v = 127;
          MIDIPiano.instance.midivolume.setValue(v);
        }

        if ((e.getKeyCode() == e.VK_HOME) || (e.getKeyCode() == e.VK_END) ||
            (e.getKeyCode() == e.VK_PAGE_DOWN) || (e.getKeyCode() == e.VK_PAGE_UP) ||
            (e.getKeyCode() == e.VK_UP) || (e.getKeyCode() == e.VK_DOWN) ||
            (e.getKeyCode() == e.VK_LEFT) || (e.getKeyCode() == e.VK_RIGHT)) {
            e.setKeyCode('~');
            return;
          }

        } catch (Exception e1) { }        
    }
  public void keyPressed(KeyEvent e) {
      key_pressed(e);
  }
  
  public static void key_released(KeyEvent e) {
      e = MIDIPiano.instance.translatekeycode(e);
      if ((e.getKeyCode() == 255) &&  MIDIPiano.instance.isCurrentlyPressed(254)) e.setKeyCode(254);
      if ((e.getKeyCode() == 254) &&  MIDIPiano.instance.isCurrentlyPressed(255)) e.setKeyCode(255);
      try {
      boolean done = false;
      int indshift = -1;
      if (e.getKeyCode() == e.VK_TAB) indshift = 0;
      if ((e.getKeyChar() == e.VK_1) || (e.getKeyChar() == e.VK_EXCLAMATION_MARK)) indshift = 1;
      if (Character.toLowerCase(e.getKeyChar()) == 'q') indshift = 2;
      if ((e.getKeyChar() == e.VK_2) || (e.getKeyChar() == e.VK_AT)) indshift = 3;
      if (Character.toLowerCase(e.getKeyChar()) == 'w') indshift = 4;
      if (Character.toLowerCase(e.getKeyChar()) == 'e') indshift = 5;
      if ((e.getKeyChar() == e.VK_4) || (e.getKeyChar() == e.VK_DOLLAR)) indshift = 6;
      if (Character.toLowerCase(e.getKeyChar()) == 'r') indshift = 7;
      if ((e.getKeyChar() == e.VK_5)) indshift = 8;
      if (Character.toLowerCase(e.getKeyChar()) == 't') indshift = 9;
      if ((e.getKeyChar() == '6') || (e.getKeyChar() == e.VK_CIRCUMFLEX)) indshift = 10;
      if (Character.toLowerCase(e.getKeyChar()) == 'y') indshift = 11;
      if (Character.toLowerCase(e.getKeyChar()) == 'u') indshift = 12;
      if ((e.getKeyChar() == '8') || (e.getKeyChar() == e.VK_AMPERSAND)) indshift = 13;
      if (Character.toLowerCase(e.getKeyChar()) == 'i') indshift = 14;
      if ((e.getKeyChar() == '9') || (e.getKeyChar() == e.VK_LEFT_PARENTHESIS)) indshift = 15;
      if (Character.toLowerCase(e.getKeyChar()) == 'o') indshift = 16;
      if (Character.toLowerCase(e.getKeyChar()) == 'p') indshift = 17;
      if ((e.getKeyChar() == e.VK_MINUS) || (e.getKeyChar() == e.VK_UNDERSCORE)) indshift = 18;
      if ((e.getKeyChar() == '[') || (e.getKeyChar() == e.VK_BRACELEFT)) indshift = 19;
      if ((e.getKeyChar() == e.VK_EQUALS) || (e.getKeyChar() == e.VK_PLUS)) indshift = 20;
      if ((e.getKeyChar() == ']') || (e.getKeyChar() == e.VK_BRACERIGHT)) indshift = 21;
      if (e.getKeyCode() == e.VK_BACK_SPACE) indshift = 22;
      if ((e.getKeyChar() == e.VK_BACK_SLASH) || (e.getKeyChar() == '|')) indshift = 23;
      if (indshift >= 0) {
        Vector chord = (Vector)MIDIPiano.instance.chordbook.get(MIDIPiano.instance.chordlist.getSelectedIndex());
        for (int i = 0; i < chord.size(); ++i) {
          int ind = indshift + MIDIPiano.instance.keyboardshift + ((Integer)chord.get(i)).intValue();
          if ((ind < MIDIPiano.instance.num_keys) && (ind >= 0)) {
            MIDIPiano.instance.keyspressed2[ind]--;
            MIDIPiano.instance.masterkeyspressed[ind]--;
            if (MIDIPiano.instance.keyspressed2[ind] < 0) MIDIPiano.instance.keyspressed2[ind] = 0;
            if (MIDIPiano.instance.masterkeyspressed[ind] < 0) MIDIPiano.instance.masterkeyspressed[ind] = 0;
            if (MIDIPiano.instance.masterkeyspressed[ind] == 0) MIDIPiano.instance.noteOff(ind + MIDIPiano.instance.octaves * 12);
          }
        }
        done = true;
      }
      if (!done) {
        if (e.getKeyCode() == e.VK_CAPS_LOCK) indshift = 0;
        if (Character.toLowerCase(e.getKeyChar()) == 'a') indshift = 1;
        if (Character.toLowerCase(e.getKeyChar()) == 's') indshift = 2;
        if (Character.toLowerCase(e.getKeyChar()) == 'd') indshift = 3;
        if (Character.toLowerCase(e.getKeyChar()) == 'f') indshift = 4;
        if (Character.toLowerCase(e.getKeyChar()) == 'g') indshift = 5;
        if (Character.toLowerCase(e.getKeyChar()) == 'h') indshift = 6;
        if (Character.toLowerCase(e.getKeyChar()) == 'j') indshift = 7;
        if (Character.toLowerCase(e.getKeyChar()) == 'k') indshift = 8;
        if (Character.toLowerCase(e.getKeyChar()) == 'l') indshift = 9;
        if ((e.getKeyChar() == e.VK_SEMICOLON) || (e.getKeyChar() == e.VK_COLON)) indshift = 10;
        if ((e.getKeyChar() == '\'') || (e.getKeyChar() == e.VK_QUOTEDBL)) indshift = 11;
        if (e.getKeyCode() == e.VK_ENTER) indshift = 12;
        if (indshift >= 0) {
          Vector chord = MIDIPiano.instance.chordrow1;
          for (int i = 0; i < chord.size(); ++i) {
            int ind = indshift + MIDIPiano.instance.keyboardshift + ((Integer)chord.get(i)).intValue();
            if ((ind < MIDIPiano.instance.num_keys) && (ind >= 0)) {
              MIDIPiano.instance.keyspressed2[ind]--;
              MIDIPiano.instance.masterkeyspressed[ind]--;
              if (MIDIPiano.instance.keyspressed2[ind] < 0) MIDIPiano.instance.keyspressed2[ind] = 0;
              if (MIDIPiano.instance.masterkeyspressed[ind] < 0) MIDIPiano.instance.masterkeyspressed[ind] = 0;
              if (MIDIPiano.instance.masterkeyspressed[ind] == 0) MIDIPiano.instance.noteOff(ind + MIDIPiano.instance.octaves * 12);
            }
          }
          done = true;
        }
      }
      if (!done) {
        if ((e.getKeyCode() == 255)) indshift = 0;
        if (Character.toLowerCase(e.getKeyChar()) == 'z') indshift = 1;
        if (Character.toLowerCase(e.getKeyChar()) == 'x') indshift = 2;
        if (Character.toLowerCase(e.getKeyChar()) == 'c') indshift = 3;
        if (Character.toLowerCase(e.getKeyChar()) == 'v') indshift = 4;
        if (Character.toLowerCase(e.getKeyChar()) == 'b') indshift = 5;
        if (Character.toLowerCase(e.getKeyChar()) == 'n') indshift = 6;
        if (Character.toLowerCase(e.getKeyChar()) == 'm') indshift = 7;
        if ((e.getKeyChar() == e.VK_COMMA) || (e.getKeyChar() == e.VK_LESS)) indshift = 8;
        if ((e.getKeyChar() == e.VK_PERIOD) || (e.getKeyChar() == e.VK_GREATER)) indshift = 9;
        if ((e.getKeyChar() == e.VK_SLASH) || (e.getKeyChar() == '?')) indshift = 10;
        if ( (e.getKeyCode() == 254)) indshift = 11;
        if (indshift >= 0) {
          Vector chord = MIDIPiano.instance.chordrow2;
          for (int i = 0; i < chord.size(); ++i) {
            int ind = indshift + MIDIPiano.instance.keyboardshift + ((Integer)chord.get(i)).intValue();
            if ((ind < MIDIPiano.instance.num_keys) && (ind >= 0)) {
              MIDIPiano.instance.keyspressed2[ind]--;
              MIDIPiano.instance.masterkeyspressed[ind]--;
              if (MIDIPiano.instance.keyspressed2[ind] < 0) MIDIPiano.instance.keyspressed2[ind] = 0;
              if (MIDIPiano.instance.masterkeyspressed[ind] < 0) MIDIPiano.instance.masterkeyspressed[ind] = 0;
              if (MIDIPiano.instance.masterkeyspressed[ind] == 0) MIDIPiano.instance.noteOff(ind + MIDIPiano.instance.octaves * 12);
            }
          }
          done = true;
        }
      }
      MIDIPiano.instance.pianopnl.repaint();
      MIDIPiano.instance.CheckIfNoNotes();
      MIDIPiano.instance.RemoveKeyPressed(e.getKeyCode());
      } catch (Exception e1) { }
    }
  
  public void keyReleased(KeyEvent e) {
      key_released(e);
  }
}
