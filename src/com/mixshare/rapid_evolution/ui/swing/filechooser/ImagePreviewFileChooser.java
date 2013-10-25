package com.mixshare.rapid_evolution.ui.swing.filechooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import rapid_evolution.ui.SkinManager;

import org.apache.log4j.Logger;

public class ImagePreviewFileChooser extends JPanel implements PropertyChangeListener {
    
    private static Logger log = Logger.getLogger(ImagePreviewFileChooser.class);
    
    private JFileChooser jfc;
    private Image img;
    
    public ImagePreviewFileChooser(JFileChooser jfc) {
        this.jfc = jfc;
        Dimension sz = new Dimension(200, 200);
        setPreferredSize(sz);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            if (log.isDebugEnabled())
                log.debug("propertyChange(): updating");
            File file = jfc.getSelectedFile();
            updateImage(file);
        } catch (Exception e) {
            log.error("propertyChange(): error Exception", e);
        }
    }
    
    public void updateImage(File file) throws IOException {
        if (file == null) {
            return;
        }
        img = ImageIO.read(file);
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        // fill the background
        g.setColor(SkinManager.instance.getColor("panel_background"));
        g.fillRect(0, 0, getWidth(), getHeight());
        if (img != null) {
            // calculate the scaling factor
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            int side = Math.max(w, h);
            double scale = 200.0 / (double)side;
            w = (int)(scale * (double)w);
            h = (int)(scale * (double)h);
            // draw the image
            g.drawImage(img, 0, 0, w, h, null);
            // draw the image dimensions
            String dim = w + " x " + h;
            g.setColor(Color.black);
            g.drawString(dim, 31, 196);
            g.setColor(Color.white);
            g.drawString(dim, 30, 195);        
        } else {
            // print a message
            g.setColor(SkinManager.instance.getColor("default_foreground"));
            g.drawString("not an image", 30, 100);
        }
    }

}
