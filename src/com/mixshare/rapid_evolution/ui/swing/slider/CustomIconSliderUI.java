package com.mixshare.rapid_evolution.ui.swing.slider;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.Color;

import org.apache.log4j.Logger;

import rapid_evolution.ui.SkinManager;

import rapid_evolution.AudioColor;

public class CustomIconSliderUI extends MetalSliderUI {
    
    private static Logger log = Logger.getLogger(CustomIconSliderUI.class);
    
    Image im;
    
    public CustomIconSliderUI() {
        super();
    }
    
    public void paintThumb(Graphics g) {
        if (log.isTraceEnabled())
            log.trace("paintThumb(): running");
        if (im == null) {
            im = SkinManager.instance.getIcon("bpm_slider_thumb_icon").getImage();
        }
        //URL url = new URL(getClass().getResource("Save.gif"), "Save.gif");
        //im = ImageIO.read(url);
        Rectangle thumb = thumbRect;
//        g.drawImage(im, thumb.x, thumb.y, null);
        g.setColor(Color.white);
        g.fillRect(0, 0, 10, 10);
    }   
    
}