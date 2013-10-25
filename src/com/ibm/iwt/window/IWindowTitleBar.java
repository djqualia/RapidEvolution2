package com.ibm.iwt.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

import com.ibm.iwt.event.WindowChangeEvent;
import com.ibm.iwt.layout.GroupFlowLayout;
import com.ibm.iwt.layout.GroupFlowLayoutConstraints;
import com.ibm.iwt.util.IWTUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import rapid_evolution.audio.SubBandSeperator;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;

/**
 * The IWindowTitleBar acts as the title bar for all IFrames.  It provides
 * multiple functions for displaying and manipulating the title bar to simulate
 * a real title bar that you would find on a native frame.
 * <p>The default implementation of IWindowTitleBar will paint itself like
 * the title bar on a Windows 2000 machine and will handle its own mouse events
 * that it receives from the frame's rootpane.  It will also handle its own
 * cursor changes.  This default implementation assumes only a rectangular title bar.
 * <p>Basic functions have been added to manipulate the colors, fonts, layouts,
 * and buttons on the title bar.  Users who wish to simply change the properties
 * on the title bar without changing its shape can simply call these functions.
 * <p>More advanced windows will need to subclass IWindowTitleBar to paint
 * more complex looks and to customize where the borders are located and how they
 * should behave.  Subclasses should override the <code>isMouseOnBorder</code>
 * function to define where the borders are on the title bar and <code>isInsideTitleBar</code>
 * to define where the title bar is located.
 * @author MAbernethy
 */
public class IWindowTitleBar extends IBorderComponent implements ActionListener
{
    private static Logger log = Logger.getLogger(IWindowTitleBar.class);
    
	/** the instance of the title label used by the default implementation */
	protected JLabel lblTitle;
	/** the instance of the logo used by the default implementation */
	protected JLabel lblLogo = new RELabel();;

	/** contains all the buttons on the title bar */
	protected Vector vctWindowButtons = new Vector();

	/** the border around the title bar */
	protected Insets borderSize;

	/**
	 * Creates an IWindowTitleBar that is by default:
	 * <br>22 pixels high
	 * <br>A background color of medium blue (default Windows 2000)
	 * <br>Has a Windows border
	 * <br>Displays the 3 title bar buttons on the right side
	 */
	public IWindowTitleBar()
	{
		super();
		setPreferredSize(new Dimension(0, 22));
		setBackground(new Color(106, 128, 168));
		setBorder(new DefaultBorder());
		setBorderSize(new Insets(2, 2, 0, 2));
		GroupFlowLayout absLayout = new GroupFlowLayout();
		setLayout(absLayout);
		addAllWindowButtons();
                lblLogo.setText("");
                lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	}

	/**
	 * Returns the minimum size that this title bar can be.  By default
	 * the title bar cannot change its height.
	 * @return the minimum size
	 */
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	/**
	 * Returns the maximum size that this title bar can be.  By default
	 * the title bar cannot change its height.
	 */
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}

	/**
	 * Returns the border size of the border around the title bar.
	 * @return the border in pixels
	 */
	public Insets getBorderSize()
	{
		return borderSize;
	}

	/**
	 * Sets the border size of the border around the title bar.
	 * @param borderSize the border in pixels
	 */
	public void setBorderSize(Insets borderSize)
	{
		this.borderSize = borderSize;
	}

	/**
	 * Returns whether the coordinates are inside the title bar.  By default
	 * this returns true since the default title bar takes up the entire panel.
	 * <br>More advanced subclasses that don't use the entire
	 * panel as the title bar will override this method to return true only
	 * when the coordinates are within the desired title bar.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return whether the coordinates are inside the title bar
	 */
	protected boolean isInsideTitleBar(int x, int y)
	{
		return true;
	}

	/**
	 * Returns whether the coordinates are on a border of the title bar.  By
	 * default, this returns true if the coordinates are within the border size
	 * defined by <code>borderSize</code> and is a rectangular shape.
	 * <br>More advanced subclasses will override this method to return true
	 * only when the coordinates lie on a border.
	 * @param x the x coordinates
	 * @param y the y coordinates
	 */
	protected void isMouseOnBorder(int x, int y)
	{
		if (y < borderSize.top)
			isMouseOnBorder = true;
		else if (x < borderSize.left)
			isMouseOnBorder = true;
		else if (x > getWidth() - borderSize.right)
			isMouseOnBorder = true;
		else
			isMouseOnBorder = false;
	}

	/**
	 * After computing the appropriate coordinates, it tells any listeners
	 * that the window should be resized or moved.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	public void mouseDragged(MouseEvent e)
	{
	    try {
	        log.trace("mouseDragged(): event=" + e);
	        log.trace("mouseDragged(): \tisWindowMaximized(): " + isWindowMaximized());	        
	        log.trace("mouseDragged(): \tisInsideTitleBar(): " + isInsideTitleBar(e.getX(), e.getY()));
	        log.trace("mouseDragged(): \tisMouseOnBorder(): " + isMouseOnBorder);
	        log.trace("mouseDragged(): \tposX(): " + e.getX() + ", posY(): " + e.getY());
	        log.trace("mouseDragged(): \tchangeX(): " + (e.getX()-X) + ", changeY(): " + (e.getY()-Y));
	        log.trace("mouseDragged(): \tdirection: " + direction);
		    
		if (isWindowMaximized())
			return;

		if (!isInsideTitleBar(e.getX(), e.getY()))
			return;

		if (((e.getX() - X) == 0) && ((e.getY() - Y) == 0)) return;
		
		WindowChangeEvent event = new WindowChangeEvent(this, e.getX(), e.getY(), e.getX()-X, e.getY()-Y, direction, true);
		if (isMouseOnBorder)
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_RESIZED, event);
		else
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_MOVED, event);
	    } catch (Exception e2) {
	        log.error("mouseDragged(): ", e2);
	    }
		
	}

	/**
	 * Computes the coordinates where the mouse is first pressed.  Uses these
	 * coordinates as a basis for all mouse movements.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	public void mousePressed(MouseEvent e)
	{
		if (isWindowMaximized())
			return;

		if (!isInsideTitleBar(e.getX(), e.getY()))
			return;

		X = e.getX();
		Y = e.getY();
		isMouseOnBorder(X, Y);
		handleDirections(e, true);
	}

	/**
	 * Computes the coordinates where the mouse is released and tells any listeners
	 * that the window should stop resizing or moving.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	public void mouseReleased(MouseEvent e)
	{
		if (isWindowMaximized())
			return;

		if (!isInsideTitleBar(e.getX(), e.getY()))
			return;

		WindowChangeEvent event = new WindowChangeEvent(this, e.getX(), e.getY(), e.getX()-X, e.getY()-Y, direction, false);
		if (isMouseOnBorder)
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_RESIZED, event);
		else
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_MOVED, event);
	}

	/**
	 * Computes the coordinates where the mouse enters.  Only changes the cursor.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	public void mouseEntered(MouseEvent e)
	{
		if (isWindowMaximized())
			return;

		if (!isInsideTitleBar(e.getX(), e.getY()))
			return;

		handleDirections(e, false);
	}

	/**
	 * Computes the coordinates where the mouse exits.  Changes the cursor back
	 * to the default cursor.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	public void mouseExited(MouseEvent e)
	{
		if (!isInsideTitleBar(e.getX(), e.getY()))
			return;
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Computes the coordinates where the mouse moves.  Only changes the cursor.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (isWindowMaximized())
			return;

		if (!isInsideTitleBar(e.getX(), e.getY()))
			return;

		handleDirections(e, false);
	}

	/**
	 * Computes the coordinates where the mouse is clicked.  Maximizes
	 * the window when it is double clicked.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	public void mouseClicked(MouseEvent e)
	{
		if (!isInsideTitleBar(e.getX(), e.getY()))
			return;

		if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e))
		{
			changeRestoreButton();
			WindowChangeEvent event = new WindowChangeEvent();
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_MAXIMIZED, event);
		}
	}

	/**
	 * Based on the coordinates contained in the MouseEvent, computes whether
	 * the mouse is over the border and draws the appropriate cursor.  It also
	 * determines the direction which is used for window change events.
	 * @param e the MouseEvent from the frame's rootpane
	 */
	private void handleDirections(MouseEvent e, boolean changeDirection)
	{
		if (e.getY() < borderSize.top && e.getX() < IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NORTH_WEST;
		}
		else if (e.getY() < borderSize.top && e.getX() > getWidth() -IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NORTH_EAST;
		}
		else if (e.getY() < borderSize.top)
		{
			setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NORTH;
		}

		if (e.getX() < borderSize.left)
		{
			setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NORTH_WEST;
		}
		if (e.getX() > getWidth() - borderSize.right)
		{
			setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NORTH_EAST;
		}

		if (e.getX() > borderSize.right && e.getX() < getWidth()-borderSize.right &&
		e.getY() > borderSize.top && e.getY() < getHeight()-borderSize.bottom)
		{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NONE;
		}

	}

	/**
	 * Captures events from the title bar buttons and fires the appropriate
	 * window change event to the listeners
	 * @param e the ActionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		IWindowButton b = (IWindowButton)e.getSource();
		if (b.getButtonType() == IWindowButton.MINIMIZE)
		{
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_MINIMIZED, new WindowChangeEvent());
		}
		else if (b.getButtonType() == IWindowButton.RESTORE_MAX || b.getButtonType() == IWindowButton.RESTORE_MIN)
		{
			changeRestoreButton();
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_MAXIMIZED, new WindowChangeEvent());
		}
		else if (b.getButtonType() == IWindowButton.CLOSE)
		{
			fireWindowChangeEvent(WindowChangeEvent.WINDOW_CLOSED, new WindowChangeEvent());
		}
	}

	/**
	 * Sets the restore button state as either restore maximized or restore minimized.
	 * @param isMaximized if the restore state is mazimized
	 */
	public void setRestoreButtonState(boolean isMaximized)
	{
		for (int i=0; i<vctWindowButtons.size(); i++)
		{
			IWindowButton iButton = (IWindowButton)vctWindowButtons.get(i);
			if (iButton.getButtonType() == IWindowButton.RESTORE_MAX && !isMaximized)
				iButton.setButtonType(IWindowButton.RESTORE_MIN)	;
			else if (iButton.getButtonType() == IWindowButton.RESTORE_MIN && isMaximized)
				iButton.setButtonType(IWindowButton.RESTORE_MAX);
		}
	}

	/**
	 * Changes the restore button from either maximized to minimized, or minimized
	 * to maximized.
	 * <p>This changes only the button and has no effect on the frame itself.
	 */
	public void changeRestoreButton()
	{
		for (int i=0; i<vctWindowButtons.size(); i++)
		{
			IWindowButton iButton = (IWindowButton)vctWindowButtons.get(i);
			if (iButton.getButtonType() == IWindowButton.RESTORE_MAX)
				iButton.setButtonType(IWindowButton.RESTORE_MIN)	;
			else if (iButton.getButtonType() == IWindowButton.RESTORE_MIN)
				iButton.setButtonType(IWindowButton.RESTORE_MAX);
		}
	}

	/**
	 * Removes all the default settings from the title bar including
	 * the title, all the title bar buttons, and the border.
	 */
	public void removeWindowDecorations()
	{
		removeTitle();
		removeAllWindowButtons();
		setBorder(null);
	}

        private void createTitle() {
          lblTitle = new RELabel();
          lblTitle.setOpaque(false);
          add(lblLogo, new GroupFlowLayoutConstraints(SwingConstants.LEFT, new Insets(3, 6, 3, 3)));
          add(lblTitle, new GroupFlowLayoutConstraints(SwingConstants.LEFT, new Insets(3, 6, 3, 3)));
        }

	/**
	 * Sets the title on this title bar and by default adds it to the left side of
	 * the title bar.
	 * @param title the window's title
	 */
	public void setTitle(String title)
	{
		if (lblTitle == null)
		{
                  createTitle();
                  Color color = UIManager.getColor("frame_foreground");
                  if (color == null) color = Color.white;
                  lblTitle.setForeground(color);
		}
		lblTitle.setText(title);
	}

	/**
	 * Removes the title from the title bar.
	 */
	public void removeTitle()
	{
		if (lblTitle == null)
			lblTitle = new RELabel();
		remove(lblTitle);
	}

	/**
	 * Adds a title to the title bar.
	 * @param title the frame's title
	 * @param alignment where the title should align (RIGHT, CENTER, LEFT)
	 * @param f the font of the title
	 * @param foreground the foreground color of the title
	 */
	public void addTitle(String title, int alignment, Font f, Color foreground)
	{
		if (lblTitle != null)
			remove(lblTitle);
		lblTitle.setFont(f);
		lblTitle.setForeground(foreground);
	}

        public void setTitleFont(Font f)
        {
          if (lblTitle == null)
          {
            createTitle();
          }
          lblTitle.setFont(f);
	}

        public void setTitleBackground(Color c)
        {
          if (lblTitle == null)
          {
            createTitle();
          }
          lblTitle.setBackground(c);
        }

        public void setTitleForeground(Color c)
        {
          if (lblTitle == null)
          {
            createTitle();
          }
          lblTitle.setForeground(c);
        }
	/**
	 * Sets the logo on the title bar and adds it to the left side.
	 * @param i the icon used as the logo
	 */
	public void setLogo(Icon i)
	{
		lblLogo.setPreferredSize(new Dimension(i.getIconWidth(), i.getIconHeight()));
		lblLogo.setIcon(i);
	}

	/**
	 * Adds a logo to the title bar on the left side.
	 * @param i the icon used as the logo
	 */
	public void addLogo(Icon i)
	{
		javax.swing.JLabel extraLogo = new RELabel();
		extraLogo.setText("");
		extraLogo.setPreferredSize(new Dimension(i.getIconWidth(), i.getIconHeight()));
		extraLogo.setIcon(i);
		extraLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(extraLogo, new GroupFlowLayoutConstraints(SwingConstants.LEFT, new Insets(3, 6, 3, 3)));
//                add(lblTitle, new GroupFlowLayoutConstraints(SwingConstants.LEFT, new Insets(3, 6, 3, 3)));
	}

	/**
	 * Sets the foreground color on the window buttons.
	 * @param foreground the window buttons' foreground color
	 */
	public void setWindowButtonForeground(Color foreground)
	{
		for (int i=0; i<vctWindowButtons.size(); i++)
		{
			((IWindowButton)vctWindowButtons.get(i)).setForeground(foreground);
		}
	}

	/**
	 * Sets the background color on the window buttons.
	 * @param background the window buttons' background color
	 */
	public void setWindowButtonBackground(Color background)
	{
		for (int i=0; i<vctWindowButtons.size(); i++)
		{
			((IWindowButton)vctWindowButtons.get(i)).setBackground(background);
		}
	}

	/**
	 * Sets the background and foreground colors on the window buttons.
	 * @param background the window buttons' background color
	 * @param foreground the window buttons' foreground color
	 */
	public void setWindowButtonColors(Color background, Color foreground)
	{
		for (int i=0; i<vctWindowButtons.size(); i++)
		{
			((IWindowButton)vctWindowButtons.get(i)).setBackground(background);
			((IWindowButton)vctWindowButtons.get(i)).setForeground(foreground);
		}
	}

	/**
	 * Returns the background color of the first window button on the title bar.
	 * Since every button can be a different color, this function loses
	 * some of its effectiveness when multiple background colors are used
	 * on window buttons.
	 * @return the background color of the first window button
	 */
	public Color getWindowButtonBackground()
	{
		if (vctWindowButtons.size() > 0)
			return ((IWindowButton)vctWindowButtons.get(0)).getBackground();
		return Color.BLACK;
	}

	/**
	 * Returns the foreground color of the first window button on the title bar.
	 * Since every button can be a different color, this function loses
	 * some of its effectiveness when multiple foreground colors are used
	 * on window buttons.
	 * @return the foreground color of the first window button
	 */
	public Color getWindowButtonForeground()
	{
		if (vctWindowButtons.size() > 0)
			return ((IWindowButton)vctWindowButtons.get(0)).getForeground();
		return Color.WHITE;
	}

	/**
	 * Returns the preferred size of the first window button on the title bar.
	 * Since every button can be a different size, this function loses
	 * some of its effectiveness when multiple sizes are used
	 * on window buttons.
	 * @return the size of the first window button
	 */
	public Dimension getWindowButtonSize()
	{
		if (vctWindowButtons.size() > 0)
			return ((IWindowButton)vctWindowButtons.get(0)).getPreferredSize();
		return new Dimension(0,0);
	}

	/**
	 * Adds a button on the title bar on the right side.
	 * @param iButton the window button
	 */
	public void addWindowButton(IWindowButton iButton)
	{
		iButton.addActionListener(this);
		vctWindowButtons.add(iButton);
		add(iButton, new GroupFlowLayoutConstraints(SwingConstants.RIGHT, new Insets(3, 1, 3, 1)));
	}

	/**
	 * Adds a button on the title bar on the right side.
	 * @param buttonType the type of button to add
	 */
	public void addWindowButton(int buttonType)
	{
		IWindowButton iButton = new IWindowButton(buttonType);
		vctWindowButtons.add(iButton);
		addWindowButton(buttonType, iButton.getForeground(), iButton.getBackground());
	}

	/**
	 * Adds a button on the title bar on the right side.
	 * @param buttonType the type of button to add
	 * @param foreground the foreground color of the button
	 * @param background the background color of the button
	 */
	public void addWindowButton(int buttonType, Color foreground, Color background)
	{
		IWindowButton iButton = new IWindowButton(buttonType);
		iButton.setForeground(foreground);
		iButton.setBackground(background);
		iButton.addActionListener(this);
		vctWindowButtons.add(iButton);
		add(iButton, new GroupFlowLayoutConstraints(SwingConstants.RIGHT, new Insets(3, 3, 3, 3)));
	}

	/**
	 * Adds a button on the title bar with the desired orientation on the title bar.
	 * @param buttonType the type of button to add
	 * @param orientation the orientation the title where the button should be added
	 */
	public void addWindowButton(int buttonType, int orientation)
	{
		addWindowButton(new IWindowButton(buttonType), orientation);
	}

	/**
	 * Adds a button on the title bar with the desired orientation on the title bar.
	 * @param button the button to add
	 * @param orientation the orientation the title where the button should be added
	 */
	public void addWindowButton(IWindowButton button, int orientation)
	{
		vctWindowButtons.add(button);
		button.addActionListener(this);
		add(button, new GroupFlowLayoutConstraints(orientation, new Insets(3,3,3,3)));
	}

	/**
	 * Adds all the window buttons that are default displayed in a Windows OS.
	 */
	public void addAllWindowButtons()
	{
		removeAllWindowButtons();
		addWindowButton(IWindowButton.MINIMIZE);
		addWindowButton(IWindowButton.RESTORE_MIN);
		addWindowButton(IWindowButton.CLOSE);
	}

        public void setMaximize(boolean value) {
          if (value) addAllWindowButtons();
          else {
            removeAllWindowButtons();
            addWindowButton(IWindowButton.MINIMIZE);
            addWindowButton(IWindowButton.CLOSE);
          }
        }

	/**
	 * Adds all the window buttons that are default displayed in a Windows OS
	 * with the specified colors.
	 * @param foreground the foreground color of the buttons
	 * @param background the background colors of the buttons
	 */
	public void addAllWindowButtons(Color foreground, Color background)
	{
		removeAllWindowButtons();
		addWindowButton(IWindowButton.MINIMIZE, foreground, background);
		addWindowButton(IWindowButton.RESTORE_MIN, foreground, background);
		addWindowButton(IWindowButton.CLOSE, foreground, background);
	}

	/**
	 * Removes all the window buttons from the title bar.
	 */
	public void removeAllWindowButtons()
	{
		for (int i=0; i<vctWindowButtons.size(); i++)
		{
			IWindowButton b = (IWindowButton)vctWindowButtons.get(i);
			remove(b);
			b.removeActionListener(this);
		}
		vctWindowButtons.removeAllElements();
	}

	/**
	 * Removes the specified window button from the title bar.
	 * @param iButton the button to be removed
	 */
	public void removeWindowButton(IWindowButton iButton)
	{
		remove(iButton);
		iButton.removeActionListener(this);
		vctWindowButtons.remove(iButton);
	}
	
	/**
	 * Sets the size of the buttons on the title bar.
	 * @param size the size of the buttons
	 */
	public void setWindowButtonSize(Dimension size)
	{
		for (int i=0; i<vctWindowButtons.size(); i++)
		{
			 ((IWindowButton)vctWindowButtons.get(i)).setPreferredSize(size);
		}
	}

	/**
	 * The default border that appears around the title bar in Windows 2000.
	 * @author MAbernethy
	 */
	protected class DefaultBorder extends AbstractBorder
	{
		public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
		{
			g.setColor(c.getBackground().brighter().brighter());
	        g.drawLine(0, 0, 0, h-1);
        	g.drawLine(1, 0, w-1, 0);

	        g.setColor(c.getBackground().brighter());
	        g.drawLine(1, 1, 1, h-2);
        	g.drawLine(2, 1, w-2, 1);

	        g.setColor(c.getBackground().darker().darker());
	        g.drawLine(w-1, 1, w-1, h-2);

	        g.setColor(c.getBackground().darker());
	        g.drawLine(w-2, 2, w-2, h-3);
		}
	}

}
