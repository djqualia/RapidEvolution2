package rapid_evolution.ui.styles;

import javax.swing.DefaultListModel;
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

public class StyleExcludeKeyListener extends KeyAdapter {

  static String quickstrokestartswith = new String("");
  static long lastkeystroke = 0;

  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == e.VK_BACK_SPACE) {
      if (quickstrokestartswith.length() >= 1) quickstrokestartswith = quickstrokestartswith.substring(0, quickstrokestartswith.length() - 1);
      return;
    } else if (e.getKeyCode() == e.VK_SPACE) {
      if (quickstrokestartswith.length() >= 1) quickstrokestartswith += " ";
      return;
    } else if (e.getKeyCode() == e.VK_DELETE) {
      EditStyleUI.instance.RemoveSelectedExcludeKeywords();
    } else if (e.getKeyCode() == e.VK_ALT) {
      if (!StyleExcludeMouse.instance.m_pmnPopup.isVisible()) {
        StyleExcludeMouse.instance.m_pmnPopup.show(e.getComponent(), StyleExcludeMouse.instance.stylesongspopupx, StyleExcludeMouse.instance.stylesongspopupy);
      }
      else StyleExcludeMouse.instance.m_pmnPopup.setVisible(false);
      return;
    }


    char c = Character.toLowerCase(e.getKeyChar());
    if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == ',') || (c == '-') || (c == '_') || (c == '+') || (c == '\'') || (c == ':') || (c == ';') || (c == '\\') || (c == '/') || (c == '[') || (c == ']') || (c == '{') || (c == '}') || (c == '`') || (c == '!') || (c == '@') || (c == '#') || (c == '$') || (c == '%') || (c == '^') || (c == '&') || (c == '*') || (c == '(') || (c == ')') || (c == '|') || (c == '~')) {
      if ((lastkeystroke == 0) || ((System.currentTimeMillis() - lastkeystroke) < 1500)) quickstrokestartswith += c;
      else quickstrokestartswith =  new String("" + c);
      lastkeystroke = System.currentTimeMillis();
      DefaultListModel dlm = (DefaultListModel) EditStyleUI.instance.styleexcludelist.getModel();
      int start = EditStyleUI.instance.styleexcludelist.getSelectedIndex();
      int index = start;
      if (index >= dlm.getSize()) index = 0;
      if (index < 0) index = 0;
      boolean found = false;
      while (!found) {
        String value = ((String)dlm.getElementAt(index)).toLowerCase();
        if (value.startsWith("<")) value = value.substring(1, value.length());
        if (value.startsWith(quickstrokestartswith)) found = true;
        else index++;
        if (index >= dlm.getSize()) index = 0;
        if (index == start) found = true;
      }
      if (index !=  start) {
          EditStyleUI.instance.styleexcludelist.setSelectedIndex(index);
          EditStyleUI.instance.styleexcludelist.ensureIndexIsVisible(index);
//        int total = (EditStyleUI.instance.editstyleeexcludekeywordsscroll.getVerticalScrollBar().getMaximum() - EditStyleUI.instance.editstyleeexcludekeywordsscroll.getVerticalScrollBar().getMinimum());
//        double percent = (double)index / (double)dlm.getSize();
//        EditStyleUI.instance.editstyleeexcludekeywordsscroll.getVerticalScrollBar().setValue((int)(percent * total));
      }
    }
  }
}
