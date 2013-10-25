package com.mixshare.rapid_evolution.ui.swing.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.Insets;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.plaf.ButtonUI;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JToolTip;

import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;
import org.jvnet.substance.SubstanceLookAndFeel;

public class REButton extends JButton implements MouseListener {

    static public int BUTTON_TYPE_NORMAL = 0;
    static public int BUTTON_TYPE_LIQUID = 1;
    
    static private int CURRENT_BUTTON_TYPE = BUTTON_TYPE_NORMAL; // default
    static public void setCurrentButtonType(int type) { CURRENT_BUTTON_TYPE = type; }    
    static public int getCurrentButtonType() { return CURRENT_BUTTON_TYPE; }
    
    private JToolTip tooltip;
    
    public REButton() {
        super();
        init();
    }
    
    public REButton(String text) {
        super(text);
        init();
    }
    
    public JToolTip createToolTip() {
        if (SkinManager.instance.use_custom_tooltips) {
            if (tooltip == null) {
                tooltip = new CustomToolTip();
                tooltip.setComponent(this);            
            }
            return tooltip;
        } else {
            return super.createToolTip();
        }
    }
    
    private void init() {
        this.addMouseListener(this);
//        this.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);
    }
        
    
    static private int liquidInsetWidth = 10;
    static private int liquidInsetHeight = 3;
    public Insets getInsets() {
        Insets superInsets = super.getInsets();        
        if (CURRENT_BUTTON_TYPE == BUTTON_TYPE_LIQUID) {
            return new Insets(superInsets.top + liquidInsetHeight, superInsets.left + liquidInsetWidth, superInsets.bottom + liquidInsetHeight, superInsets.right + liquidInsetWidth);
        }
        return superInsets;
    }
    
    public Dimension getPreferredSize() {
        // the SkinManager sets preferred sizes so this will return whatever was set...
        return super.getPreferredSize();
        /*
        if (CURRENT_BUTTON_TYPE == BUTTON_TYPE_LIQUID) {
            String text = getText();
            FontMetrics fm = this.getFontMetrics(getFont());
            float scale = getScale();
            int w = fm.stringWidth(text);
            w += (int)(scale * 1.4f);
            int h = fm.getHeight();
            h += (int)(scale * 0.3f);
            return new Dimension(w, h);
        } else {
            // BUTTON_TYPE_NORMAL
            return super.getPreferredSize();
        }
        */
    }

    static private Border emptyBorder = BorderFactory.createEmptyBorder();    
    public Border getBorder() {
        if (CURRENT_BUTTON_TYPE == BUTTON_TYPE_LIQUID)
            return emptyBorder;
        return super.getBorder();
    }
    
    public void paintComponent(Graphics g) {
        if ((CURRENT_BUTTON_TYPE == BUTTON_TYPE_LIQUID) && (this.getIcon() == null)) {
            if (isEnabled()) {
                Graphics2D g2 = (Graphics2D)g;
                if (rapid_evolution.RapidEvolution.aaEnabled)
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this.isOpaque()) {
                    g2.setColor(this.getBackground());
                    g2.fillRect(0,0, this.getWidth(), this.getHeight());
                }
                float scale = getScale();
                drawLiquidButton(this.getForeground(), this.getWidth(), this.getHeight(), getText(), scale, g2);
            } else {
                if (OptionsUI.instance.blurDisabledButtons.isSelected()) {
                    BufferedImage buf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
                    Graphics2D g2 = (Graphics2D)buf.getGraphics();
                    if (rapid_evolution.RapidEvolution.aaEnabled)
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (this.isOpaque()) {
                        g2.setColor(this.getBackground());
                        g2.fillRect(0,0, getWidth(), getHeight());
                    }
                    float scale = getScale();
                    drawLiquidButton(this.getForeground(), this.getWidth(), this.getHeight(), getText(), scale, g2);
                    float center = 0.5f;
                    float sides = (1.0f - center) / 8.0f;
                    float[] my_kernel = {
                            sides, sides, sides,
                            sides, center, sides,
                            sides, sides, sides 
                    };
                    ConvolveOp op = new ConvolveOp(new Kernel(3, 3, my_kernel), ConvolveOp.EDGE_NO_OP, null);
                    Image img = op.filter(buf, null);
                    g.drawImage(img, 0, 0, null);
                } else {
                    BufferedImage buf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
                    Graphics2D g2 = (Graphics2D)g;
                    if (rapid_evolution.RapidEvolution.aaEnabled)
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (this.isOpaque()) {
                        g2.setColor(this.getBackground());
                        g2.fillRect(0,0, getWidth(), getHeight());
                    }
                    float scale = getScale();
                    drawLiquidButton(this.getForeground(), this.getWidth(), this.getHeight(), getText(), scale, g2);                 
                }
            }
        } else {
            // BUTTON_TYPE_NORMAL
            super.paintComponent(g);
        }
    }

    /* mouse listener implementation */
    protected boolean pressed = false;
    protected boolean hovering = false;
    public void mouseExited(MouseEvent evt) {
        hovering = false;
    }
    public void mouseEntered(MouseEvent evt) {
        hovering = true;
    }
    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) {
        pressed = false;
    }
    public void mousePressed(MouseEvent evt) {
        pressed = true;
    }
    
    protected float getScale() {
        return (50.0f / 30.0f) * this.getFont().getSize2D();
    }        
    
    protected void drawLiquidButton(Color base, int width, int height, String text, float scale, Graphics2D g2) {
        // calculate inset
        int inset = (int)(scale * 0.04f);
        int w = width - inset * 2 - 1;
        int h = height - (int)(scale * 0.1f) - 1;
        
        g2.translate(inset, 0);
        drawDropShadow(w, h, scale, g2);
        
        if (pressed) {
            g2.translate(0, 0.04f * scale);
        }
        
        drawButtonBody(w, h, scale, base, g2);
        drawText(w, h, scale, text, g2, base);
        drawHighlight(w, h, scale, base, g2);
        drawBorder(w, h, scale, g2);
        
        if (pressed) {
            g2.translate(0, 0.04f * scale);
        }
        g2.translate(-inset, 0);
    }
    
    protected void drawDropShadow(int w, int h, float scale, Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 50));
        fillRoundRect(g2,
                -0.04f * scale,
                0.02f * scale,
                w + 0.08f * scale, h + 0.08f * scale,
                scale * 1.04f, scale * 1.04f);
        g2.setColor(new Color(0, 0, 0, 100));
        fillRoundRect(g2, 0f, 0.06f * scale, (float)w, (float)h, scale, scale);
    }
    
    protected void drawButtonBody(int w, int h, float scale, Color base, Graphics2D g2) {
      Color grad_top = base;
      if (!this.isEnabled()) grad_top = base.darker();
      else if (hovering) grad_top = base.brighter();
      
      Color grad_bot = base.darker().darker();
      if (!this.isEnabled()) grad_bot = grad_bot.darker();
      else if (hovering) grad_bot = base.darker();
      
        GradientPaint bg = new GradientPaint(
                new Point(0, 0), grad_top,
                new Point(0, h), grad_bot);
        g2.setPaint(bg);
        this.fillRoundRect(g2,
                0 * scale,
                0 * scale,
                w, h, 1 * scale, 1 * scale);
        
        // draw the inner color
        Color inner = base;
        if (!this.isEnabled()) inner = base.darker();
        else if (hovering) inner = base.brighter();        
        inner = alphaColor(inner, 75);
        g2.setColor(inner);
        this.fillRoundRect(g2,
                scale * 0.4f,
                scale * 0.4f,
                w - scale * 0.8f, h - scale * 0.5f,
                scale * 0.6f, scale * 0.4f);
    }
    
    // generate the alpha version of the specified color
    protected static Color alphaColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    protected void drawText(int w, int h, float scale, String text, Graphics2D g2, Color base) {
        
        // calculate the width and height
        int fw = g2.getFontMetrics().stringWidth(text);
        int fh = g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent();
        int textx = (w - fw) / 2;
        int texty = h / 2 + fh / 2;
        
        // draw the text
        g2.setColor(new Color(0, 0, 0, 70));
        g2.drawString(text, (int)((float)textx + scale * 0.04f), (int)((float)texty + scale * 0.04f));
        Color textColor = Color.BLACK;        
        if (hovering) {
            textColor = rapid_evolution.ui.SkinManager.instance.getColor("button_hover_foreground");
            if (textColor.equals(Color.BLACK)) textColor = Color.BLACK.brighter();
        }
        g2.setColor(textColor);
        g2.drawString(text, textx, texty); 
        
    }
    
    protected void drawHighlight(int w, int h, float scale, Color base, Graphics2D g2) {
        
        // create the highlight
        int highlightTouch = 175;
        if (!this.isEnabled()) highlightTouch = 50;
        else if (hovering) highlightTouch = 200;
        GradientPaint highlight = new GradientPaint(
                new Point2D.Float(scale * 0.2f, scale * 0.2f),
                new Color(255, 255, 255, highlightTouch),
                new Point2D.Float(scale * 0.2f, scale * 0.55f),
                new Color(255, 255, 255, 0));
        g2.setPaint(highlight);
        this.fillRoundRect(g2, scale * 0.2f, scale * 0.1f,
                w - scale * 0.4f, scale * 0.4f, scale * 0.8f, scale * 0.4f);
        this.drawRoundRect(g2, scale * 0.2f, scale * 0.1f,
                w - scale * 0.4f, scale * 0.4f, scale * 0.8f, scale * 0.4f);
    }
    
    protected void drawBorder(int w, int h, float scale, Graphics2D g2) {
        // draw the border
        g2.setColor(new Color(0, 0, 0, 150));
        this.drawRoundRect(g2,
                scale * 0,
                scale * 0,
                w, h, scale, scale);
    }
    
    // float version of fill round rect
    protected static void fillRoundRect(Graphics2D g2,
            float x, float y, float w, float h, float ax, float ay) {
        g2.fillRoundRect((int)x, (int)y,
                (int)w, (int)h,
                (int)ax, (int)ay);        
    }
    
    // float version of draw round rect
    protected static void drawRoundRect(Graphics2D g2,
            float x, float y, float w, float h, float ax, float ay) {
        g2.drawRoundRect((int)x, (int)y,
                (int)w, (int)h,
                (int)ax, (int)ay);
    }
}
