package rapid_evolution.piano;

import javax.swing.JPanel;
import rapid_evolution.RapidEvolution;
import java.awt.Color;
import java.awt.Graphics;
import rapid_evolution.ui.SkinManager;

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

public class PianoPanel extends JPanel {
  int keywidth;
  
  static public PianoPanel instance = null;
  
  public PianoPanel() {
      instance = this;
  }
  
  Color use_color = null;
  public void setColor(Color color) {
      use_color = color;
  }
  public void restoreColor() {
      use_color = null;
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Color pressedkeycolor = use_color;
    if (pressedkeycolor == null) pressedkeycolor = SkinManager.instance.getColor("midi_keyboard_pressed");
    int bottombuffer = 0;
    int yoffset = 0;
    int heightsegment = (getHeight() - bottombuffer - 5) / 3;
    int width = getWidth() - 1;
    keywidth = (int)((float)width / (float)MIDIPiano.instance.num_keys);
    int keywidthhalf = keywidth / 2;
    int keywidthhalf2 = keywidth / 2;

    if ((keywidthhalf * 2) < keywidth) keywidthhalf2++;
    int xoffset = (width - (keywidth * MIDIPiano.instance.num_keys)) / 2;
    int keyheight = heightsegment * 2;
    g.setColor(Color.white);
    int initialoffset = 0;
    if (MIDIPiano.instance.pianostartkey == 0) initialoffset = (int)(0.5 * keywidth);
    else if ((MIDIPiano.instance.pianostartkey == 2) || (MIDIPiano.instance.pianostartkey == 7)) initialoffset = (int)(5.0 / 7.0 * keywidth);
    else if ((MIDIPiano.instance.pianostartkey == 5) || (MIDIPiano.instance.pianostartkey == 10)) initialoffset = (int)(2.0 / 7.0 * keywidth);
    int pianoendkey = MIDIPiano.instance.pianostartkey + MIDIPiano.instance.num_keys;
    while (pianoendkey >= 12) pianoendkey -= 12;
    g.fillRect(xoffset - initialoffset, yoffset + keyheight, keywidth * MIDIPiano.instance.num_keys + initialoffset, heightsegment + 1);
    for (int i = 0; i < MIDIPiano.instance.num_keys; ++i) {
      int j = i + MIDIPiano.instance.pianostartkey;
      while (j >= 12) j -= 12;
      int jprev = j - 1;
      if (jprev < 0) jprev = 11;
      if (MIDIPiano.instance.topblack[j]) g.setColor(Color.black);
      else g.setColor(Color.white);
      if (MIDIPiano.instance.masterkeyspressed[i] > 0) {
        g.setColor(pressedkeycolor);
        if (!MIDIPiano.instance.topblack[j]) g.fillRect(xoffset + i * keywidth, yoffset+keyheight, keywidth,yoffset + (getHeight() - bottombuffer - 5) - (yoffset + keyheight));
      }
      if (i == 0) g.fillRect(xoffset - initialoffset, yoffset, keywidth + initialoffset, keyheight);
      else g.fillRect(xoffset + i * keywidth, yoffset, keywidth, keyheight);
      if (MIDIPiano.instance.masterkeyspressed[i] > 0) {
        g.setColor(Color.black);
        if ((j == 11) || (j == 4) || (j == 9) || (j == 6) || (j == 1)) {
          g.drawLine(xoffset + i * keywidth, yoffset, xoffset + i * keywidth + keywidth - 1, yoffset);
          g.drawLine(xoffset + i * keywidth, yoffset, xoffset + i * keywidth, yoffset + keyheight - 1);
          g.drawLine(xoffset + i * keywidth + keywidth - 1, yoffset + keyheight - 1, xoffset + i * keywidth + keywidth - 1, yoffset);
          g.drawLine(xoffset + i * keywidth + keywidth - 1, yoffset + keyheight - 1, xoffset + i * keywidth, yoffset + keyheight - 1);
        }
      }
      if (MIDIPiano.instance.topblack[j]) {
        g.setColor(Color.black);
        if (j == 11) {
          g.drawLine(xoffset + (i * keywidth) + (keywidth / 2),
                     yoffset + keyheight,
                     xoffset + i * keywidth + keywidth / 2,
                     yoffset + (getHeight() - bottombuffer - 5));
          if (MIDIPiano.instance.masterkeyspressed[i - 1] > 0) {
            g.setColor(pressedkeycolor);
            int wid = keywidthhalf;
            g.fillRect(xoffset + (i * keywidth), yoffset + keyheight, wid, yoffset + (getHeight() - bottombuffer - 5) - (yoffset + keyheight));
          }
          if (MIDIPiano.instance.masterkeyspressed[i + 1] > 0) {
            g.setColor(pressedkeycolor);
            g.fillRect(xoffset + (i * keywidth) + (keywidth / 2) + 1, yoffset + keyheight, keywidthhalf2 - 1, yoffset + (getHeight() - bottombuffer - 5) - (yoffset + keyheight));
          }
        }
        if ((j == 4) || (j == 9)) {
          g.drawLine( (int) (xoffset + (i * keywidth) + (keywidth / 2) +
                             (3.0 / 14.0 * keywidth)), yoffset + keyheight,
                     (int) (xoffset + (i * keywidth) + (keywidth / 2) +
                            (3.0 / 14.0 * keywidth)),
                     yoffset + (getHeight() - bottombuffer - 5));
         if (MIDIPiano.instance.masterkeyspressed[i - 1] > 0) {
           g.setColor(pressedkeycolor);
           g.fillRect(xoffset + (i * keywidth), yoffset + keyheight, keywidthhalf + (int)(3.0 / 14.0 * keywidth), yoffset + (getHeight() - bottombuffer - 5) - (yoffset + keyheight));
         }
         if (MIDIPiano.instance.masterkeyspressed[i + 1] > 0) {
           g.setColor(pressedkeycolor);
           g.fillRect(xoffset + (i * keywidth) + keywidth / 2 + (int)(3.0 / 14.0 * keywidth) + 1, yoffset + keyheight, keywidthhalf2 - (int)(3.0 / 14.0 * keywidth) - 1, yoffset + (getHeight() - bottombuffer - 5) - (yoffset + keyheight));
         }
        }
        if ((j == 6) || (j == 1)) {
          g.drawLine( truncate((xoffset + (i * keywidth) + (keywidth / 2) -
                             (3.0 / 14.0 * keywidth))) + 1, yoffset + keyheight,
                     truncate( (xoffset + (i * keywidth) + (keywidth / 2) -
                            (3.0 / 14.0 * keywidth))) + 1,
                     yoffset + (getHeight() - bottombuffer - 5));
         if (MIDIPiano.instance.masterkeyspressed[i - 1] > 0) {
           g.setColor(pressedkeycolor);
           g.fillRect(xoffset + (i * keywidth), yoffset + keyheight, keywidthhalf - (int)(3.0 / 14.0 * keywidth), yoffset + (getHeight() - bottombuffer - 5) - (yoffset + keyheight));
         }
         if (MIDIPiano.instance.masterkeyspressed[i + 1] > 0) {
           g.setColor(pressedkeycolor);
           g.fillRect(xoffset + (i * keywidth) + keywidth / 2 - (int)(3.0 / 14.0 * keywidth) + 1, yoffset + keyheight, keywidthhalf2 + (int)(3.0 / 14.0 * keywidth) - 1, yoffset + (getHeight() - bottombuffer - 5) - (yoffset + keyheight));
         }
        }
      }
      if ((i > 0) && !MIDIPiano.instance.topblack[j] && !MIDIPiano.instance.topblack[jprev]) {
        g.setColor(Color.black);
        g.drawLine(xoffset + (i * keywidth), yoffset, xoffset + (i * keywidth), yoffset + (getHeight() - bottombuffer - 5));
      }
    }
    g.setColor(Color.black);
    g.drawLine(xoffset - initialoffset, yoffset, xoffset + keywidth * MIDIPiano.instance.num_keys, yoffset);
    g.drawLine(xoffset - initialoffset, yoffset, xoffset - initialoffset, yoffset + (getHeight() - bottombuffer - 5));
    g.drawLine(xoffset + keywidth * MIDIPiano.instance.num_keys, yoffset + (getHeight() - bottombuffer - 5), xoffset + keywidth * MIDIPiano.instance.num_keys, yoffset);
    g.drawLine(xoffset + keywidth * MIDIPiano.instance.num_keys, yoffset + (getHeight() - bottombuffer - 5), xoffset - initialoffset, yoffset + (getHeight() - bottombuffer - 5));

//      requestFocus();
  }

  public int truncate(double val) {
    int returnval = (int)val - 1;
    while (((double)returnval) < val) {
      returnval++;
    }
    return returnval - 1;
  }
}
