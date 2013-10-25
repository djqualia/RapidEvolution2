package com.mixshare.rapid_evolution.ui.swing.tooltip;

import javax.swing.JToolTip;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Dimension;
import javax.swing.JComponent;
import java.awt.Component;
import rapid_evolution.ui.SkinManager;
import java.awt.Rectangle;
import javax.swing.plaf.ToolTipUI;

import javax.swing.plaf.basic.BasicToolTipUI;

public class CustomToolTip extends JToolTip {

    public CustomToolTip() {
        super();
        init();        
    }
    
    private void init() {
        this.setOpaque(false);
    }
    
    public void paintComponent(Graphics g) {
        
        String text = this.getComponent().getToolTipText();
        if ((text == null) || text.equals("")) return;
        
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(SkinManager.instance.getColor("tooltip_border_color"));
        g2.fillRect(0,0, getWidth(), getHeight());
        
        // create a round rectangle
        Shape round = new RoundRectangle2D.Float(4, 4, this.getWidth() - 1 - 8, this.getHeight() - 1 - 8, 15, 15);
        // draw the white background
        if (rapid_evolution.RapidEvolution.aaEnabled)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(SkinManager.instance.getColor("tooltip_background"));
        g2.fill(round);

        // draw the text
        if ((text != null) && !text.equals("")) {
            FontMetrics fm = g2.getFontMetrics();
            int h = fm.getAscent();            
            g2.setColor(SkinManager.instance.getColor("tooltip_foreground"));
            g2.drawString(text, 12, (this.getHeight() + h) / 2 - 1);
        }
    }
    
    public Dimension getPreferredSize() {
        String text = this.getComponent().getToolTipText();
        if ((text == null) || text.equals("")) return new Dimension(1,1); // had an exception with (0,0)
        Dimension dim = super.getPreferredSize();
        return new Dimension((int)dim.getWidth() + 20, (int)dim.getHeight() + 15);
    }

}
