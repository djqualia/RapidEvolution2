package rapid_evolution.ui.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MixoutTableKeyListener extends KeyAdapter {
  public void keyPressed(KeyEvent e) {
    if ((e.getKeyCode() == e.VK_DELETE) && (MixoutPane.instance.mixouttable.getSelectedRowCount() == 1)) {
      MixoutPane.instance.RemoveSelectedMixout();
    } else if (e.getKeyCode() == e.VK_ALT) {
      if (!MixListMouse.instance.m_pmnPopup2.isVisible()) {
        MixListMouse.instance.m_pmnPopup2.show(e.getComponent(), MixListMouse.instance.mixoutpopupx, MixListMouse.instance.mixoutpopupy);
//         m_pmnPopup.setVisible(true);
      }
      else MixListMouse.instance.m_pmnPopup2.setVisible(false);
    }
  }
}
