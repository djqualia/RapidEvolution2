package rapid_evolution.ui;

import javax.swing.*;

import java.awt.Component;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import rapid_evolution.StringUtil;
import rapid_evolution.piano.MIDIPiano;

import javax.swing.DefaultListCellRenderer;

import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;

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

public class REComboBox extends JComboBox {
  public REComboBox() {
    init();
  }
  private boolean ignorekb = false;
  public REComboBox(boolean ignore_keyboard_input) {
      ignorekb = ignore_keyboard_input;     
  }
  
  private void init() {
    this.setRenderer(new combo_cell_renderer(this));
  }

  public REComboBox(Object[] array) {
    super(array);
    init();
  }

  private JToolTip tooltip;  
  public JToolTip createToolTip() {
      if (SkinManager.instance.use_custom_tooltips) {
          if (tooltip == null) {
              tooltip = new CustomToolTip();
              tooltip.setComponent(this);            
          }
          return tooltip;
      } else {
          return super.createToolTip();
      }
  }
  
  public REComboBox(Object[] array, boolean ignore_keyboard_input) {
      super(array);
      ignorekb = ignore_keyboard_input;
      init();
    }
  
  public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      if (rapid_evolution.RapidEvolution.aaEnabled)
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      super.paintComponent(g2);
  }
  
  
  public void processKeyEvent(KeyEvent e) {
      if (!ignorekb) super.processKeyEvent(e);
      else {
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
