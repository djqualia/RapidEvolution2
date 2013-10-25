package rapid_evolution.ui.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BpmSliderFieldKeyListener extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
            SearchPane.instance.UpdateBpmSliderField();
        }        
    }
}
