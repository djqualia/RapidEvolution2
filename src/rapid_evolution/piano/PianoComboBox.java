package rapid_evolution.piano;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import rapid_evolution.ui.SkinManager;

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

public class PianoComboBox extends JComboBox {
  public PianoComboBox() {
    init();
  }
  private boolean ignorekb = false;
  public PianoComboBox(boolean ignore_keyboard_input) {
      ignorekb = ignore_keyboard_input;
      init();
  }
  

  private void init() {
    this.setRenderer(new combo_cell_renderer(this));
    /*
    KeyListener[] listeners = getKeyListeners();
    for (int k = 0; k < listeners.length; ++k)
        removeKeyListener(listeners[k]);
        */
  }

  public PianoComboBox(Object[] array) {
    super(array);
    init();
  }

  public PianoComboBox(Object[] array, boolean ignore_keyboard_input) {
      super(array);
      ignorekb = ignore_keyboard_input;
      init();
    }
  
  public void processKeyEvent(KeyEvent e) {
      if (!ignorekb) {
          if (e.getID() == KeyEvent.KEY_PRESSED)
              MidiPianoKeyListener.key_pressed(e);
          else if (e.getID() == KeyEvent.KEY_RELEASED)
              MidiPianoKeyListener.key_released(e);
          //super.processKeyEvent(e);
      }
      else {
          if (e.getKeyCode() == e.VK_DOWN) {
              int v = MIDIPiano.instance.midivolume.getValue();
              v -= 5;
              if (v < 0) v = 0;
              MIDIPiano.instance.midivolume.setValue(v);
          } else if (e.getKeyCode() == e.VK_UP) {
              int v = MIDIPiano.instance.midivolume.getValue();
              v += 5;
              if (v > 127) v = 127;
              MIDIPiano.instance.midivolume.setValue(v);
          } else {
/*              if (e.getID() == KeyEvent.KEY_PRESSED) {
                  MidiPianoKeyListener.key_pressed(e);
              } else if (e.getID() == KeyEvent.KEY_PRESSED) {
                  MidiPianoKeyListener.key_released(e);
              }*/
              if (e.getID() == KeyEvent.KEY_PRESSED)
                  MidiPianoKeyListener.key_pressed(e);
              else if (e.getID() == KeyEvent.KEY_RELEASED)
                  MidiPianoKeyListener.key_released(e);
              //super.processKeyEvent(e);
          }
      }
  }
//  public static combo_cell_renderer renderer = new combo_cell_renderer();

  class combo_cell_renderer extends DefaultListCellRenderer implements ListCellRenderer {
    public combo_cell_renderer(JComboBox _combobox) {
      combobox = _combobox;
      this.setOpaque(true);
    }
    JComboBox combobox;
    public Component getListCellRendererComponent(
                                           JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                       boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (SkinManager.instance != null) {
        if (isSelected) {
          Color sforeground = SkinManager.instance.getAspectColor(combobox, ".selectionForeground", null);
          if (sforeground != null) {
            combobox.getEditor().getEditorComponent().setForeground(sforeground);
            combobox.setForeground(sforeground);
            list.setForeground(sforeground);
            c.setForeground(sforeground);
          }
          Color sbackground = SkinManager.instance.getAspectColor(combobox, ".selectionBackground", null);
          if (sbackground != null) {
            combobox.getEditor().getEditorComponent().setBackground(sbackground);
            combobox.setBackground(sbackground);
            list.setBackground(sbackground);
            c.setBackground(sbackground);
          }
        } else {
          Color sforeground = SkinManager.instance.getAspectColor(combobox, ".foreground", null);
          if (sforeground != null) {
            c.setForeground(sforeground);
          }
          Color sbackground = SkinManager.instance.getAspectColor(combobox, ".background", null);
          if (sbackground != null) {
            c.setBackground(sbackground);
          }
        }
        }
        return c;
    }
  }
}
