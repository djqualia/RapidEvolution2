
package com.ibm.iwt.window;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import com.ibm.iwt.event.WindowChangeEvent;
import com.ibm.iwt.util.IWTUtilities;

/**
 * The IContentPane adds to the functionality of a normal content pane
 * by handling mouse events.  The mouse events are relayed
 * from the parent window's rootpane and it is up to the IContentPane
 * to handle the mouse events itself.
 * <p>Like the content pane, all components should be added to the IContentPane and
 * it has a default BorderLayout.
 * <p>For default application windows, the IContentPane handles all mouse events 
 * based on a rectangular application window.  
 * <p>For more complex application windows
 * that are not rectangular but still maintain
 * a mostly rectangular shape and will not need
 * to change Cursor types, they should subclass IContentPane and override the 
 * <code>isMouseOnBorder</code> function.
 * <p>For extremely complex application window shapes that require different cursors
 * than would be provided by the default implementation, the <code>isMouseOnBorder</code>
 * as well as the <code>handleDirections</code> functions should be overridden in the 
 * subclass.
 * @author MAbernethy
 */
public class IContentPane extends IBorderComponent
{
	
	/** the border around the content pane */
	protected Insets borderSize = new Insets(3,3,3,3);
	private int direction;
	
	/**
	 * Returns the border size around the content pane in pixels.
	 * @return the border size
	 */
	public Insets getBorderSize()
	{
		return borderSize;
	}
	
	/**
	 * Returns the insets that are used by the IContentPane to draw the border.  This
	 * prevents the IContentPane from painting over the border with its children.
	 * @return the insets where the border is drawn
	 */
	public Insets getInsets()
	{
		return borderSize;
	}
	
	/**
	 * Based on the coordinates contained in the MouseEvent, computes whether
	 * the mouse is over the border and draw the appropriate cursor.  It also
	 * determines the direction which is used for window resizing events.
	 * @param e the MouseEvent from the window's rootpane
	 * @param changeDirection if the direction should change
	 */
	protected void handleDirections(MouseEvent e, boolean changeDirection)
	{		
		// left border
		if (e.getX() < borderSize.left && e.getY() < IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NORTH_WEST;
			setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
		}
		else if (e.getX() < borderSize.left && e.getY() > getHeight()-IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_SOUTH_WEST;
			setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
		}
		else if (e.getX() < borderSize.left)
		{
			if (changeDirection)	
				direction = WindowChangeEvent.RESIZE_WEST;
			setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		}
		
		// bottom border
		if (e.getY() > getHeight()-borderSize.bottom && e.getX() < IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_SOUTH_WEST;
			setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
		}
		else if (e.getY() > getHeight()-borderSize.bottom && e.getX() > getWidth()-IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_SOUTH_EAST;
			setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		}
		else if (e.getY() > getHeight()-borderSize.bottom)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_SOUTH;
			setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
		// right border
		if (e.getX() > getWidth()-borderSize.right && e.getY() > getHeight()-IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_SOUTH_EAST;
			setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		}
		else if (e.getX() > getWidth()-borderSize.right && e.getY() < IWTUtilities.DIAGONAL_RESIZE_SIZE)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_NORTH_EAST;
			setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
		}
		else if (e.getX() > getWidth()-borderSize.right)
		{
			if (changeDirection)
				direction = WindowChangeEvent.RESIZE_EAST;
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		}
		
		if (e.getX() > borderSize.left && e.getX() < getWidth()-borderSize.right &&
			e.getY() > borderSize.top && e.getY() < getHeight()-borderSize.bottom)
			{
				if (changeDirection)
					direction = WindowChangeEvent.RESIZE_NONE;
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
	}
	
	/**
	 * Returns whether the coordinates are on a border of the icontent pane.  By
	 * default, this returns true if the coordinates are within the border size
	 * defined by <code>borderSize</code> and is a rectangular shape.
	 * <br>More advanced subclasses will override this method to return true
	 * only when the coordinates lie on a border.
	 * @param x the x coordinates
	 * @param y the y coordinates
	 */
	protected void isMouseOnBorder(int x, int y)
	{
		if (y > getHeight() - borderSize.bottom)
			isMouseOnBorder = true;
		else if (x < borderSize.left)
			isMouseOnBorder = true;
		else if (x > getWidth() - borderSize.right)
			isMouseOnBorder = true;
		else
			isMouseOnBorder = false;
	}

	/**
	 * Does nothing.
	 * @param the MouseEvent from the window's rootpane
	 */
	public void mouseClicked(MouseEvent e){	}

	/**
	 * After computing the appropriate coordinates, it tells any listeners
	 * that the window should be resized.
	 * @param e the MouseEvent from the window's rootpane
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (isWindowMaximized() || !isMouseOnBorder)
			return;
		WindowChangeEvent event = new WindowChangeEvent(this, e.getX(), e.getY(), e.getX()-X, e.getY()-Y, direction, true);
		fireWindowChangeEvent(WindowChangeEvent.WINDOW_RESIZED, event);	
	}
	
	/**
	 * Computes the coordinates where the mouse enters.  Only changes the cursor.
	 * @param e the MouseEvent from the window's rootpane
	 */
	public void mouseEntered(MouseEvent e)
	{
		if (isWindowMaximized())
			return;
		handleDirections(e, false);
	}
	
	/** 
	 * Computes the coordinates where the mouse exits.  Changes the cursor back
	 * to the default cursor.
	 * @param e the MouseEvent from the window's rootpane
	 */
	public void mouseExited(MouseEvent e)
	{
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Computes the coordinates where the mouse moves.  Only changes the cursor.
	 * @param e the MouseEvent from the window's rootpane
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (isWindowMaximized())
			return;
		handleDirections(e, false);
	}
	
	/**
	 * Computes the coordinates where the mouse is first pressed.  Uses these
	 * coordinates as a basis for all mouse movements.
	 * @param e the MouseEvent from the window's rootpane
	 */
	public void mousePressed(MouseEvent e)
	{
		if (isWindowMaximized())
			return;
		X = e.getX();
		Y = e.getY();
		isMouseOnBorder(X, Y);
		handleDirections(e, true);
	}
	
	/**
	 * Computes the coordinates where the mouse is released and tells any listeners
	 * that the window should stop resizing.
	 * @param e the MouseEvent from the window's rootpane
	 */
	public void mouseReleased(MouseEvent e)
	{	
		if (isWindowMaximized() || !isMouseOnBorder)
			return;
		WindowChangeEvent event = new WindowChangeEvent(this, e.getX(), e.getY(), e.getX()-X, e.getY()-Y, direction, false);
		fireWindowChangeEvent(WindowChangeEvent.WINDOW_RESIZED, event);	
	}
	
	/**
	 * Sets the border size around the content pane in pixels.
	 * @param borderSize the border size
	 */
	public void setBorderSize(Insets borderSize)
	{
		this.borderSize = borderSize;
	}

}
