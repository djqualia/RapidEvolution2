package rapid_evolution.ui.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.SongTrailUI;

import com.ibm.iwt.IOptionPane;

public class SearchKeyListener extends KeyAdapter {

  static String quickstrokestartswith = new String("");
  static long lastkeystroke = 0;

  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == e.VK_BACK_SPACE) {
      if (quickstrokestartswith.length() >= 1) quickstrokestartswith = quickstrokestartswith.substring(0, quickstrokestartswith.length() - 1);
      return;
    } else if (e.getKeyCode() == e.VK_SPACE) {
      if (quickstrokestartswith.length() >= 1) quickstrokestartswith += " ";
      return;
    } else if (e.getKeyCode() == e.VK_ALT) {
      if (!SearchListMouse.instance.m_pmnPopup.isVisible()) {
        SearchListMouse.instance.m_pmnPopup.show(e.getComponent(), SearchListMouse.instance.searchpopupx, SearchListMouse.instance.searchpopupy);
//         m_pmnPopup.setVisible(true);
      }
      else SearchListMouse.instance.m_pmnPopup.setVisible(false);
      return;
    } else if (e.getKeyCode() == e.VK_DELETE) {
        RapidEvolutionUI.instance.deletesongs_ui.deleteSongs(RapidEvolutionUI.getSelectedSearchSongs());
    }

    char c = Character.toLowerCase(e.getKeyChar());
    if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == ',') || (c == '-') || (c == '_') || (c == '+') || (c == '\'') || (c == ':') || (c == ';') || (c == '\\') || (c == '/') || (c == '[') || (c == ']') || (c == '{') || (c == '}') || (c == '`') || (c == '!') || (c == '@') || (c == '#') || (c == '$') || (c == '%') || (c == '^') || (c == '&') || (c == '*') || (c == '(') || (c == ')') || (c == '|') || (c == '~')) {
      if ((lastkeystroke == 0) || ((System.currentTimeMillis() - lastkeystroke) < 1500)) quickstrokestartswith += c;
      else quickstrokestartswith =  new String("" + c);
      lastkeystroke = System.currentTimeMillis();
      int start = SearchPane.instance.searchtable.getSelectedRow();
      int index = start;
      if (index >= SearchPane.instance.searchtable.getRowCount()) index = 0;
      if (index < 0) index = 0;
      boolean found = false;
      while (!found) {
        SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(index, SearchPane.instance.searchcolumnconfig.num_columns);
        if (song.getSongIdShort().toLowerCase().startsWith(quickstrokestartswith)) found = true;
        else index++;
        if (index >= SearchPane.instance.searchtable.getRowCount()) index = 0;
        if (index == start) found = true;
      }
      if (index !=  start) {
        SearchPane.instance.searchtable.setRowSelectionInterval(index,index);
        JScrollPane scrollpanel = SkinManager.instance.getScrollPanel(SearchPane.instance.searchtable);
        JScrollBar vertical = scrollpanel.getVerticalScrollBar();
        int total = (vertical.getMaximum() - vertical.getMinimum());
        double percent = (double)index / (double)SearchPane.instance.searchtable.getRowCount();
        SkinManager.instance.getScrollPanel(SearchPane.instance.searchtable).getVerticalScrollBar().setValue((int)(percent * total));
      }
    }
  }
}
