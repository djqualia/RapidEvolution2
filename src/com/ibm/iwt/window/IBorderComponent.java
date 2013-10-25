
package com.ibm.iwt.window;

import java.awt.Window;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JPanel;

import com.ibm.iwt.event.WindowChangeEvent;
import com.ibm.iwt.event.WindowChangeListener;
import com.ibm.iwt.util.IWTUtilities;

/**
 * The superclass of any Panel that wishes to resize the application window.
 * <br>The obvious subclasses of this class are the IContentPane and the IWindowTitleBar
 * but any panel may resize the window.  An example of this is the trend
 * to place a large panel in the lower right corner of application windows that
 * assists in resizing of the window.  By simply subclassing this and firing
 * appropriate events, any panel can cause the window to resize.
 * @author MAbernethy
 */
public abstract class IBorderComponent extends JPanel implements MouseListener, MouseMotionListener
{
	/** the direction the mouse is moving */
	protected int direction = WindowChangeEvent.RESIZE_NONE;
	/** whether the mouse is on the border */
	protected boolean isMouseOnBorder;
	/** the window change event listeners */
	protected Vector listeners = new Vector();
	/** the top level parent of this panel - the application window */
	protected Window parent;
	/** the stored x position */
	protected int X = 0;
	/** the stored y position */
	protected int Y = 0;

	/**
	 * Creates an IBorderComponent.
	 */
	public IBorderComponent()
	{
		super();
	}
	
	/**
	 * Adds a listener for WindowChangeEvents.
	 * @param l the listener
	 */
	public synchronized void addWindowChangeListener(WindowChangeListener l)
	{
		if (!listeners.contains(l))		
			listeners.add(l);
	}
	
	/**
	 * Fires a WindowChangeEvent to all listeners.  Subclasses call this event
	 * to relay their events.
	 * @param type the type of WindowChangeEvent that occurred
	 * @param e the WindowChangeEvent that is getting dispatched
	 */
	protected void fireWindowChangeEvent(int type, WindowChangeEvent e)
	{
		int size = listeners.size();
		for (int i=0; i<size; i++)
		{
			switch (type)
			{
				case WindowChangeEvent.WINDOW_MOVED:
				{
					((WindowChangeListener)listeners.get(i)).windowMoved(e);
					continue;
				}
				case WindowChangeEvent.WINDOW_CLOSED:
				{
					((WindowChangeListener)listeners.get(i)).windowClosed(e);
					continue;
				}
				case WindowChangeEvent.WINDOW_MAXIMIZED:
				{
					((WindowChangeListener)listeners.get(i)).windowMaximized(e);
					continue;
				}
				case WindowChangeEvent.WINDOW_MINIMIZED:
				{
					((WindowChangeListener)listeners.get(i)).windowMinimized(e);
					continue;
				}				
				case WindowChangeEvent.WINDOW_RESIZED:
				{
					((WindowChangeListener)listeners.get(i)).windowResized(e);
					continue;
				}	
			}
		}
	}
	
	public boolean isTransparent(int x, int y)
	{
		return false;
	}
	
	/**
	 * Returns whether the window is maximized or not.
	 * @return whether the window is maximized
	 */
	protected boolean isWindowMaximized()
	{
		if (parent == null)
			parent = (Window)getTopLevelAncestor();

		return IWTUtilities.isWindowMaximized(parent);	
	}
	
	/**
	 * Removes a listener for WindowChangeEvents.
	 * @param l the listener
	 */
	public synchronized void removeWindowChangeListener(WindowChangeListener l)
	{
		listeners.remove(l);	
	}
	
}
