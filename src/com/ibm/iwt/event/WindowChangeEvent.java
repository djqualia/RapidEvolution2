
package com.ibm.iwt.event;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A WindowChangeEvent adds to the information that can be associated with windows
 * and adds to the functionality of <code>WindowEvent</code>.
 * <p>The class adds to <code>WindowEvent</code> by providing events that describe
 * all the ways a window can change its position and/or its location.  
 * <p><b>Note:</b>  These events merely tell the listener what the window <i>should</i>
 * do.  It is the listener's responsibility to take the appropriate action on the window
 * itself.
 * @author MAbernethy
 */
public class WindowChangeEvent
{
	/** The constant for no resize direction */
	public static final int RESIZE_NONE = -1;
	/** The constant for resizing east */
	public static final int RESIZE_EAST = 0;	
	/** The constant for resizing north */
	public static final int RESIZE_NORTH = 1;
	/** The constant for resizing northeast */
	public static final int RESIZE_NORTH_EAST = 2;
	/** The constant for resizing northwest */
	public static final int RESIZE_NORTH_WEST = 3;
	/** The constant for resizing south */
	public static final int RESIZE_SOUTH = 4;
	/** The constant for resizing southeast */
	public static final int RESIZE_SOUTH_EAST = 5;
	/** The constant for resizing southwest */
	public static final int RESIZE_SOUTH_WEST = 6;
	/** The constant for resizing west */
	public static final int RESIZE_WEST = 7;
	
	/** The constant for the window closing */
	public static final int WINDOW_CLOSED = 1;
	/** The constant for the window maximizing */
	public static final int WINDOW_MAXIMIZED = 2;
	/** The constant for the window minimizing */
	public static final int WINDOW_MINIMIZED = 3;
	/** The constant for the window moving */
	public static final int WINDOW_MOVED = 4;
	/** The constant for the window resizing */
	public static final int WINDOW_RESIZED = 5;
	
	private int changeX = 0;
	private int changeY = 0;
	private int direction;
	private boolean isDragging;
	
	private int posX = 0;
	private int posY = 0;
	private JComponent source;
	
	/**
	 * Constructs a WindowChangeEvent.
	 */
	public WindowChangeEvent() { }
	
	/**
	 * Constructs a WindowChangeEvent.
	 * @param x the x position
	 * @param y the y position
	 */
	public WindowChangeEvent(int x, int y)
	{
		this(null, x, y, 0, 0, RESIZE_NONE, false);
	}
	
	/**
	 * Constructs a WindowChangeEvent.
	 * @param source the source of the event
	 * @param posX the x position
	 * @param posY the y position
	 * @param changeX the change in the x position
	 * @param changeY the change in the y position
	 * @param direction the direction of the change
	 * @param isDragging whether the change is currently happening
	 */
	public WindowChangeEvent(JComponent source, int posX, int posY, int changeX, int changeY, int direction, boolean isDragging)
	{
		this.source = source;
		this.posX = posX;
		this.posY = posY;
		this.changeX = changeX;
		this.changeY = changeY;
		this.direction = direction;
		this.isDragging = isDragging;
	}
	
	/**
	 * Constructs a WindowChangeEvent.
	 * @param source the source of the event
	 * @param x the x position
	 * @param y the y position
	 */
	public WindowChangeEvent(JPanel source, int x, int y)
	{
		this(source, x, y, 0, 0, RESIZE_NONE, false);
	}

	/**
	 * Returns the change in the x position from the last event until this current event.
	 * @return the change in x
	 */
	public int getChangeX()
	{
		return changeX;
	}

	/**
	 * Returns the change in the y position from the last event until this current event.
	 * @return the change in y
	 */
	public int getChangeY()
	{
		return changeY;
	}
	
	/**
	 * Returns the direction of the change in positions.
	 * @return the direction
	 */
	public int getDirection()
	{
		return direction;
	}

	/**
	 * Returns the x position where this event originated.
	 * @return the x position
	 */
	public int getPosX()
	{
		return posX;
	}

	/**
	 * Returns the y position where this event originated.
	 * @return the y position
	 */
	public int getPosY()
	{
		return posY;
	}
	
	/**
	 * Returns the source of the event.  In this architecture, the source
	 * will always be a subclass of IBorderPanel.
	 * @return the object that originated the event
	 */
	public JComponent getSource() 
	{ 
		return source;		
	}

	/**
	 * Returns whether the change in positions is currently changing.
	 * @return whether the position is changing
	 */
	public boolean isDragging()
	{
		return isDragging;
	}
}
