package rapid_evolution.ui;


import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.border.*;

public class RoundedBorder extends AbstractBorder
{
    private Color wallColor = null;
    private String wallColorId = null;
    private int sinkLevel = 10;
    private int insetLevel = 3;

    public RoundedBorder() { }
    public RoundedBorder(int sinkLevel) { this.sinkLevel = sinkLevel; }
    public RoundedBorder(Color wall) { this.wallColor = wall; }
    public RoundedBorder(int sinkLevel, Color wall)    {
        this.sinkLevel = sinkLevel;
        this.wallColor = wall;
    }
    
    public void setInsetLevel(int level) { insetLevel = level; }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int w, int h)
    {
        /*
        g.setColor(getWallColor());
        c.getBackground();                
        //  Paint a tall wall around the component
        for (int i = 0; i < sinkLevel; i++) {
            g.drawRoundRect(x+i, y+i, x+w-i*2, y+h-i*2, sinkLevel*2-i, sinkLevel*2-i);
            g.drawRoundRect(x+i+1, y+i, x+w-i*2-2, y+h-i*2, sinkLevel*2-i, sinkLevel*2-i);
            g.drawRoundRect(x+i, y+i+1, x+w-i*2, y+h-i*2-2, sinkLevel*2-i, sinkLevel*2-i);   
        }        
        if (fill) g.fillRect(x+sinkLevel, y+sinkLevel, w-sinkLevel*2, h-sinkLevel*2);
        */
        Graphics2D g2 = (Graphics2D)g;
        if (rapid_evolution.RapidEvolution.aaEnabled)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float scale = getScale();
        g.translate(x, y);
        drawLiquidBorder(getWallColor(), w, h, scale, g2);        
        g.translate(-x, -y);
    }
    
    protected float getScale() {
        // TODO: tweak
        return (50.0f / 30.0f) * 10;// * this.getFont().getSize2D();
    }        
    
    protected void drawLiquidBorder(Color base, int width, int height, float scale, Graphics2D g2) {
        // calculate inset
        int inset = (int)(scale * 0.04f);
        int w = width - inset * 2 - 1;
        int h = height - (int)(scale * 0.1f) - 1;
        
        g2.translate(inset, 0);
        drawDropShadow(w, h, scale, g2);                
        drawButtonBody(w, h, scale, base, g2);
        drawHighlight(w, h, scale, base, g2);
        drawBorder(w, h, scale, g2);
        
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
      Color grad_top = base.brighter();
      
      Color grad_bot = base.darker();
      
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
        inner = alphaColor(inner, 75);
        g2.setColor(inner);
        this.fillRoundRect(g2,
                scale * 0.4f,
                scale * 0.4f,
                w - scale * 0.8f, h - scale * 0.5f,
                scale * 0.6f, scale * 0.4f);
    }    

    protected void drawHighlight(int w, int h, float scale, Color base, Graphics2D g2) {
        
        // create the highlight
        int highlightTouch = 175;
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
    
    public Insets getBorderInsets(Component c) {
        return new Insets(insetLevel, insetLevel, insetLevel, insetLevel);
    }
    public Insets getBorderInsets(Component c, Insets i) {
        i.left = i.right = i.bottom = i.top = insetLevel;
        return i;
    }
    public boolean isBorderOpaque() { return true; }
    public int getSinkLevel() { return sinkLevel; }
    public Color getWallColor() {
        if (wallColor != null)
            return wallColor;
        if (wallColorId != null)
            return SkinManager.instance.getColor(wallColorId);
        return Color.gray;
    }
    public void setWallColorId(String id) {
        wallColorId = id;
    }
    
    boolean fill = true;
    public void setBorderFill(boolean fill) {
        this.fill = fill;
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
    
    // generate the alpha version of the specified color
    protected static Color alphaColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    
}