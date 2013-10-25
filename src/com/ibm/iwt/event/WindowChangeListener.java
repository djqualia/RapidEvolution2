
package com.ibm.iwt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving window change events. The class that is 
 * interested in processing a window change event implements this interface, and 
 * the object created with that class is registered with a component, using 
 * the component's addWindowChangeListener method. When the window change event occurs, 
 * that object's appropriate window change method is invoked. 
 * @author MAbernethy
 */
public interface WindowChangeListener extends EventListener
{
	/** Invoked when the window is to close */
	public void windowClosed(WindowChangeEvent e);
	/** Invoked when the window is to be maximied */
	public void windowMaximized(WindowChangeEvent e);
	/** Invoked when the window is to be minimized */
	public void windowMinimized(WindowChangeEvent e);
	/** Invoked when the window is to be moved */
	public void windowMoved(WindowChangeEvent e);
	/** Invoked when the window is to be resized */
	public void windowResized(WindowChangeEvent e);
}
