package rapid_evolution.ui.main;

import rapid_evolution.RapidEvolution;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import rapid_evolution.ui.RapidEvolutionUI;

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

public class BpmRangeKeyListener extends KeyAdapter {
  public void keyPressed(KeyEvent e) {
    if ((e.getKeyCode() == e.VK_BACK_SPACE) || (e.getKeyCode() == e.VK_LEFT) || (e.getKeyCode() == e.VK_RIGHT) || (e.getKeyCode() == e.VK_UP) || (e.getKeyCode() == e.VK_DOWN)) return;
//    if (RapidEvolutionUI.instance.bpmrange.getText().length() > 1) {
//      try { RapidEvolutionUI.instance.bpmrange.setText(RapidEvolutionUI.instance.bpmrange.getText(0, 1)); }
//      catch (Exception ble) { }
//      return;
//    }
  }
}
