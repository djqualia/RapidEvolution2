package com.mixshare.rapid_evolution.ui.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import javax.swing.border.AbstractBorder;

import java.awt.AlphaComposite;

public class ShadowBorder extends AbstractBorder {
    
    int xoff, yoff;
    Insets insets;
    
    public ShadowBorder(int x, int y) {
        this.xoff = x;
        this.yoff = y;
        insets = new Insets(0, 0, xoff, yoff);
    }
    
    public Insets getBorderInsets(Component c) {
        return insets;
    }
    
    public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
        
        BufferedImage bufimg = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufimg.createGraphics();
        g2.setColor(g.getColor());
        
        
        g2.setColor(Color.BLACK);
        g2.translate(x, y);
        // draw right side
        g2.fillRect(width - xoff, yoff, xoff, height - yoff);
        // draw bottom side
        g2.fillRect(xoff, height - yoff, width - xoff, yoff);
        g2.translate(-x, -y);
        
        Graphics2D gx = (Graphics2D)g;
        gx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        gx.drawImage(bufimg, 0, 0, null);
        gx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
    }
    
}
