package rapid_evolution.ui;

import javax.swing.Icon;
import rapid_evolution.RapidEvolution;
import java.awt.Component;
import java.awt.Color;
import java.awt.Graphics;

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

public class SortArrowIcon
  implements Icon
{
  protected int direction;
  protected int width = 8;
  protected int height = 8;

  public SortArrowIcon(int direction)
  {
    this.direction = direction;
  }

  public int getIconWidth()
  {
    return width;
  }

  public int getIconHeight()
  {
    return height;
  }

  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    Color bg = c.getBackground();
    Color light = bg.brighter();
    Color shade = bg.darker();

    int w = width;
    int h = height;
    int m = w / 2;
    if (direction == 2)
    {
      g.setColor(shade);
      g.drawLine(x, y, x + w, y);
      g.drawLine(x, y, x + m, y + h);
      g.setColor(light);
      g.drawLine(x + w, y, x + m, y + h);
    }
    if (direction == 1)
    {
      g.setColor(shade);
      g.drawLine(x + m, y, x, y + h);
      g.setColor(light);
      g.drawLine(x, y + h, x + w, y + h);
      g.drawLine(x + m, y, x + w, y + h);
    }
  }
}
