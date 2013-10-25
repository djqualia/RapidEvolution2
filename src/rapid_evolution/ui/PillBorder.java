package rapid_evolution.ui;


import java.awt.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;

import rapid_evolution.Artist;


import java.awt.geom.RoundRectangle2D;

public class PillBorder extends AbstractBorder
{
    private static Logger log = Logger.getLogger(PillBorder.class);
    
    private static boolean debug = true;
    
    private Color wallColor = Color.gray;
    private int insetLevel = 3;

    public PillBorder() { }
    public PillBorder(Color wall) { this.wallColor = wall; }
    
    public void setInsetLevel(int level) { insetLevel = level; }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int w, int h)
    {
        log.trace("paintBorder(): x: " + x + ", y: " + y + ", w: " + w + ", h: " + h);
        g.setColor(getWallColor());
        
        int radius = (int)Math.ceil(0.5 * h);
        for (int i = 0; i < radius; ++i) {
            log.trace("paintBorder(): i == " + i);
            double period = (Math.sin(Math.PI / 6 + Math.PI * (i + 1) / radius / 3.0) - 0.5) * 2.0;
            log.trace("paintBorder(): \tperiod: " + period);
            int length = (int)(period * radius);
            log.trace("paintBorder(): \tlength: " + length);
            g.drawLine(x+radius, y + i, x + radius - length, y + i);
            g.drawLine(x+radius, y + h - 1 - i, x + radius - length, y + h - 1 - i);
            g.drawLine(x + w - radius, y + i, x + w - radius + length, y + i);
            g.drawLine(x + w - radius, y + h - 1 - i, x + w - radius + length, y + h - 1 - i);

        }
        

    }

    public Insets getBorderInsets(Component c) {
        log.trace("getBorderInsets(): c.size()=" + c.getSize());
        return new Insets(insetLevel, (int)(c.getSize().getHeight() / 2), insetLevel, (int)(c.getSize().getHeight() / 2));
    }
    public Insets getBorderInsets(Component c, Insets i) {
        log.trace("getBorderInsets(): c.size()=" + c.getSize());
        i.top = i.bottom = insetLevel;
        i.left = i.right = (int)(c.getSize().getHeight() / 2);
        return i;
    }
    public boolean isBorderOpaque() { return true; }
    public Color getWallColor() { return wallColor; }
    
    boolean fill = true;
    public void setBorderFill(boolean fill) {
        this.fill = fill;
    }
    
}