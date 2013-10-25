package com.mixshare.rapid_evolution.ui.swing.viewport;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JViewport;

public class ScrollPaneWatermark extends JViewport {
    
    private BufferedImage fgimage, bgimage;
    private TexturePaint texture;
    private boolean tile = false;
    
    public ScrollPaneWatermark() {
        setOpaque(false);
    }
    
    public void setBackgroundTexture(URL url) throws IOException {
        bgimage = ImageIO.read(url);
        Rectangle rect = new Rectangle(0, 0, bgimage.getWidth(null), bgimage.getHeight(null));
        texture = new TexturePaint(bgimage, rect);
    }
    
    public void setForegroundBadge(URL url) throws IOException {
        fgimage = ImageIO.read(url);
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);        
        if (texture != null) {
            Graphics2D g2 = (Graphics2D)g;
            if (rapid_evolution.RapidEvolution.aaEnabled)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (tile) {
                g2.setPaint(texture);            
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g2.drawImage(bgimage, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }
    
    public void setTile(boolean tile) {
        this.tile = tile;
    }
    
    public void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (fgimage != null) {
            g.drawImage(fgimage, getWidth() - fgimage.getWidth(null), 0, null);
        }
    }
    
    public void setView(JComponent view) {
        view.setOpaque(false);
        super.setView(view);
    }

}
