package rapid_evolution.ui.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditMixoutKeyListener extends KeyAdapter {
  public void keyPressed(KeyEvent e) {
    if ((e.getKeyCode() == e.VK_ESCAPE)) {
        EditMixoutUI.instance.closeEditMixoutDialog();
    }
  }
}
