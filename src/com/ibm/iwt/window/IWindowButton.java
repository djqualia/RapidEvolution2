
package com.ibm.iwt.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import javax.swing.JButton;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

/**
 * The IWindowButton provides the user the exact same 3 functions that
 * are provided in the standard Windows OS window.  These functions
 * are the minimize button, the restore button, and the close button.  
 * <p>The IWindowButton will also paint the same icons that appear in Windows 2000
 * by default and allow the user to set the foreground and background colors 
 * for some variety.
 * <p>For more complex window buttons, this class should be subclassed and the 
 * appropriate paint method overridden.
 * <p>To add more functionality to a window that is not provided by one of the 3
 * default IWindowButton settings, this class should be subclassed.
 * @author MAbernethy
 */
public class IWindowButton extends JButton
{
	/** Constant for the close button */
	public static final int CLOSE = 3;
	/** Constant for the minimize button */
	public static final int MINIMIZE = 0;
	/** Constant for the restore to max size button */
	public static final int RESTORE_MAX = 1;
	/** Constant for the restore to min size button */
	public static final int RESTORE_MIN = 2;

	private int buttonType = MINIMIZE;

	/**
	 * Creates an IWindowButton with the minimize settings.
	 */
	public IWindowButton()
	{	
		this(MINIMIZE);
	}
	
	/**
	 * Creates an IWindowButton with the specified button type.  By default
	 * the possible button types are MINIMIZE, RESTORE_MAX, RESTORE_MIN,
	 * and CLOSE but by subclassing this class, more values are possible.
	 * @param buttonType the button type
	 */
	public IWindowButton(int buttonType)
	{
		super();
		this.buttonType = buttonType;
		initialize();	
	}

    public Color getBackground() {
        Color color = rapid_evolution.ui.SkinManager.instance.getColor("window_button_background");
        if (color != null) return color;
        return super.getBackground();
    }
    
    public Color getForeground() {
        Color color = rapid_evolution.ui.SkinManager.instance.getColor("window_button_foreground");
        if (color != null) return color;
        return super.getForeground();        
    }
    
	/**
	 * Returns the button type.
	 * @return the button type
	 */
	public int getButtonType()
	{
		return buttonType;
	}
	
	private void initialize()
	{
		this.setPreferredSize(new Dimension(16, 14));
	}

	/**
	 * Paints the IWindowButton.  Subclasses of this class
	 * that wish to change the way the button is painted should
	 * override this method.
	 * @param g the Graphics instance
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		switch (buttonType)
		{
			case MINIMIZE:
			{
				paintMinButton(g);
				return;
			}
			case RESTORE_MAX:
			{
				paintRestoreMinButton(g);
				return;
			}
			case RESTORE_MIN:
			{
				paintRestoreMaxButton(g);
				return;
			}
			case CLOSE:
			{
				paintCloseButton(g);
				return;
			}
		}
	}
	
	/**
	 * Paints the close button.  Subclasses should override
	 * this method if they wish to change the appearance of the close
	 * button.
	 * @param g the Graphics instance
	 */
	protected void paintCloseButton(Graphics g)
	{
		int w = getWidth();
		int h = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);
		g.setColor(getForeground());
		g.drawLine((int)(w/4), (int)(h/3), w-(int)(w/4)-2, h-(int)(h/3));
		g.drawLine((int)(w/4)+1, (int)(h/3), w-(int)(w/4) -1, h-(int)(h/3));
		g.drawLine((int)(w/4)+2, (int)(h/3), w-(int)(w/4), h-(int)(h/3));
		g.drawLine(w-(int)(w/4)-2, (int)(h/3), (int)(w/4), h-(int)(h/3));
		g.drawLine(w-(int)(w/4)-1, (int)(h/3), (int)(w/4)+1, h-(int)(h/3));
		g.drawLine(w-(int)(w/4), (int)(h/3), (int)(w/4)+2, h-(int)(h/3));

	}
	
	/**
	 * Paints the minimize button.  Subclasses should override
	 * this method if they wish to change the appearance of the minimize
	 * button.
	 * @param g the Graphics instance
	 */
	protected void paintMinButton(Graphics g)
	{
		int w = getWidth();
		int h = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);
		g.setColor(getForeground());
		g.fillRect((int)(w*.25), h-6, (int)(w*.4)+1, (h/5)+1);
	}
	
	/**
	 * Paints the restore max button.  Subclasses should override
	 * this method if they wish to change the appearance of the restore max
	 * button.
	 * @param g the Graphics instance
	 */
	protected void paintRestoreMaxButton(Graphics g)
	{
		int w = getWidth();
		int h = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);
		g.setColor(getForeground());
		g.fillRect((int)(.4*w), (int)(.2*h), (int)(.4*w), 2);
		g.drawLine((int)(.4*w), (int)(.3*h), (int)(.4*w), (int)(.3*h));
		g.drawLine((int)(.75*w)-1, (int)(.3*h), (int)(.75*w)-1, (int)(.5*h));
		g.drawLine((int)(.75*w)-2, (int)(.5*h), (int)(.75*w)-2, (int)(.5*h));
		g.drawRect((int)(.25*w), (int)(.4*w), (int)(.25*w)+2, (int)(h/3));
		g.fillRect((int)(.25*w), (int)(.4*w), (int)(.25*w)+2, (int)(.1*h));
	}

	/**
	 * Paints the restore min button.  Subclasses should override
	 * this method if they wish to change the appearance of the restore min
	 * button.
	 * @param g the Graphics instance
	 */
	protected void paintRestoreMinButton(Graphics g)
	{		
		int w = getWidth();
		int h = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);
		g.setColor(getForeground());
		g.drawRect((int)(w*.25), (int)(h*.25), (int)w/2, (int)h/2);
		g.fillRect((int)(w*.25), (int)(h*.25), (int)w/2, (h/5)+1);
	}

	/**
	 * Sets the button type and repaints the button.
	 * @param i the button type
	 */
	public void setButtonType(int buttonType)
	{
		this.buttonType = buttonType;
		repaint();
	}

}
