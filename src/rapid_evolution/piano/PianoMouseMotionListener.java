package rapid_evolution.piano;

import rapid_evolution.RapidEvolution;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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

public class PianoMouseMotionListener extends MouseMotionAdapter {
  public void mouseDragged(MouseEvent e) {
      if (MIDIPiano.instance.isTraining()) {
          return;
      }      
    int bottombuffer = 0;
    int yoffset = 0;
    int heightsegment = (MIDIPiano.instance.pianopnl.getHeight() - bottombuffer - 5) / 3;
    int width = MIDIPiano.instance.pianopnl.getWidth() - 1;
    int keywidth = (int)((float)width / (float)MIDIPiano.instance.num_keys);
    int xoffset = (width - (keywidth * MIDIPiano.instance.num_keys)) / 2;
    int mousex = (int)(e.getX()) - xoffset;
    int mousey = (int)(e.getY()); // - pianopnl.getLocationOnScreen().getY());
    int keyheight = heightsegment * 2;
    for (int i = 0; i < MIDIPiano.instance.num_keys; ++i) {
      int j = i + MIDIPiano.instance.pianostartkey;
      int shifts = 0;
      while (j >= 12) {
        shifts++;
        j -= 12;
      }
      int jprev = j - 1;
      if (jprev < 0) jprev = 11;
        if (mousey > keyheight) {
          if ((mousex >= (i * keywidth)) && (mousex < (i * keywidth + keywidth))) {
            int bi = i;
            if ((j == 1) || (j == 6)) {
              if ((mousex >= (i * keywidth)) && (mousex < (int)(i * keywidth) + (keywidth / 2) - (3.0 / 14.0 * keywidth))) {
                j--;
                bi--;
                if (j < 0) j = 11;
              } else {
                j++;
                bi++;
                if (j >= 12) j = 0;
              }
            }
            else if ((j == 4) || (j == 9)) {
              if ((mousex >= (i * keywidth)) && (mousex < (int)((i * keywidth) + (keywidth / 2) + (3.0 / 14.0 * keywidth)))) {
                j--;
                bi--;
                if (j < 0) j = 11;
              } else {
                j++;
                bi++;
                if (j >= 12) j = 0;
              }
            }
            else if (j == 11) {
              if ((mousex >= (i * keywidth)) && (mousex < (i * keywidth) + (keywidth / 2))) {
                j--;
                bi--;
                if (j < 0) j = 11;
              } else {
                j++;
                bi++;
                if (j >= 12) j = 0;
              }
            }
            MIDIPiano.instance.midinotetrigger(j, bi);
          }
        } else {
          if ((mousex >= (i * keywidth)) && (mousex < (i * keywidth + keywidth)))
            MIDIPiano.instance.midinotetrigger(j, i);
        }
    }

  }
}
