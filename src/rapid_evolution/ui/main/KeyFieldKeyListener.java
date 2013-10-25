package rapid_evolution.ui.main;

import rapid_evolution.RapidEvolution;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import rapid_evolution.ui.main.SearchPane;

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

public class KeyFieldKeyListener extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == e.VK_ENTER) {
        SearchPane.instance.UpdateKeyField();
      }
    }
}
