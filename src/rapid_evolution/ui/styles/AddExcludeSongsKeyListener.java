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
public class AddExcludeSongsKeyListener extends KeyAdapter {

  static String quickstrokestartswith = new String("");
  static long lastkeystroke = 0;

  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == e.VK_BACK_SPACE) {
      if (quickstrokestartswith.length() >= 1) quickstrokestartswith = quickstrokestartswith.substring(0, quickstrokestartswith.length() - 1);
      return;
    } else if (e.getKeyCode() == e.VK_SPACE) {
      if (quickstrokestartswith.length() >= 1) quickstrokestartswith += " ";
      return;
    } else if (e.getKeyCode() == e.VK_ENTER) {
        ExcludeStyleSongsUI.instance.ProcessAddExcludeStyle();
    } else if (e.getKeyCode() == e.VK_ESCAPE) {
        ExcludeStyleSongsUI.instance.CancelAddExcludeSongs();
    }

    char c = Character.toLowerCase(e.getKeyChar());
    if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == ',') || (c == '-') || (c == '_') || (c == '+') || (c == '\'') || (c == ':') || (c == ';') || (c == '\\') || (c == '/') || (c == '[') || (c == ']') || (c == '{') || (c == '}') || (c == '`') || (c == '!') || (c == '@') || (c == '#') || (c == '$') || (c == '%') || (c == '^') || (c == '&') || (c == '*') || (c == '(') || (c == ')') || (c == '|') || (c == '~')) {
      if ((lastkeystroke == 0) || ((System.currentTimeMillis() - lastkeystroke) < 1500)) quickstrokestartswith += c;
      else quickstrokestartswith =  new String("" + c);
      lastkeystroke = System.currentTimeMillis();
      DefaultListModel dlm = (DefaultListModel) ExcludeStyleSongsUI.instance.addsongexcludelist.getModel();
      int start = ExcludeStyleSongsUI.instance.addsongexcludelist.getSelectedIndex();
      int index = start;
      if (index >= dlm.getSize()) index = 0;
      if (index < 0) index = 0;
      boolean found = false;
      while (!found) {
        String value = ((String)dlm.getElementAt(index)).toLowerCase();
        if (value.startsWith(quickstrokestartswith)) found = true;
        else index++;
        if (index >= dlm.getSize()) index = 0;
        if (index == start) found = true;
      }
      if (index != start) {
        ExcludeStyleSongsUI.instance.addsongexcludelist.setSelectedIndex(index);
        ExcludeStyleSongsUI.instance.addsongexcludelist.ensureIndexIsVisible(index);
//        int total = (EditStyleUI.instance.editstylekeywordsscroll.getVerticalScrollBar().getMaximum() - EditStyleUI.instance.editstylekeywordsscroll.getVerticalScrollBar().getMinimum());
//        double percent = (double)index / (double)dlm.getSize();
//        EditStyleUI.instance.editstylekeywordsscroll.getVerticalScrollBar().setValue((int)(percent * total));
      }
    }
  }
}
