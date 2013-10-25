package com.mixshare.rapid_evolution.ui.swing.label;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JLabel;

public class RichJLabel extends JLabel {
    
    private int tracking;
    public RichJLabel(int tracking) {
        super();
        this.tracking = tracking;
    }
    public RichJLabel(String text, int tracking) {
        super(text);
        this.tracking = tracking;
    }
    public RichJLabel(Icon icon) {
        super(icon);
    }
    
    private int left_x, left_y, right_x, right_y;
    private Color left_color, right_color;
    public void setLeftShadow(int x, int y, Color color) {
        left_x = x;
        left_y = y;
        left_color = color;
    }
    
    public void setRightShadow(int x, int y, Color color) {
        right_x = x;
        right_y = y;
        right_color = color;
    }
    
    public Dimension getPreferredSize() {
        // the original code was messing up the main retree expand icon clickability
        return super.getPreferredSize();
        /*
        String text = getText();
        FontMetrics fm = this.getFontMetrics(getFont());        
        int w = fm.stringWidth(text);
        w += (text.length() - 1) * tracking;
        w += left_x + right_x;        
        int h = fm.getHeight();
        h += left_y + right_y;        
        return new Dimension(w, h);
        */
    }
    
    public void paintComponentSuper(Graphics g) {
        super.paintComponent(g);
    }
    
    public void paintComponent(Graphics g) {
        if (rapid_evolution.RapidEvolution.aaEnabled)
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String text = getText();        
        char[] chars = text == null ? new char[0] : getText().toCharArray();
        FontMetrics fm = this.getFontMetrics(getFont());
        int h = fm.getAscent();
        int x = 0;
        for (int i = 0; i < chars.length; ++i) {
            char ch = chars[i];
            int w = fm.charWidth(ch) + tracking;
            if ((left_x != 0) && (left_y != 0)) {
                g.setColor(left_color);
                g.drawString("" + ch, x - left_x, h - left_y);
            }
            if ((right_x != 0) && (right_y != 0)) {
                g.setColor(right_color);
                g.drawString("" + ch, x + right_x, h + right_y);
            }
            g.setColor(getForeground());
            g.drawString("" + ch, x, h);
            x += w;
        }
        if (rapid_evolution.RapidEvolution.aaEnabled)
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);        
    }

}
